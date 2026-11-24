package com.upt.descansa.sistema.noche

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.upt.descansa.MainActivity
import com.upt.descansa.R
import com.upt.descansa.domain.model.Disparo
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.model.TipoSimulacion
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.repository.ControlServicioNocturno
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma
import com.upt.descansa.motor.Decision
import com.upt.descansa.motor.MotorDecision
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Cuántos minutos lleva medidos la noche en curso (lo muestra el modo noche). */
object EstadoNocturno {
    internal val epocasMutable = MutableStateFlow(0)
    val epocasRegistradas: StateFlow<Int> = epocasMutable.asStateFlow()
}

/**
 * Servicio nocturno: mide el movimiento toda la noche y, dentro de la ventana,
 * hace sonar la alarma cuando el motor detecta sueño ligero.
 *
 * - Es un servicio en primer plano de tipo `health` (D-12): Android no lo
 *   detiene con la pantalla apagada y el usuario ve una notificación discreta.
 * - Un wake lock parcial mantiene la CPU despierta para que el sensor no se pause.
 * - Si algo falla aquí, no pasa nada grave: la alarma de respaldo es independiente.
 */
@AndroidEntryPoint
class ServicioNocturno : Service() {

    @Inject lateinit var nocheRepositorio: NocheRepository
    @Inject lateinit var ajustesRepositorio: AjustesRepository
    @Inject lateinit var programador: ProgramadorAlarma

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var trabajo: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!iniciarEnPrimerPlano()) {
            stopSelf()
            return START_NOT_STICKY
        }
        // Si ya estaba midiendo, no se arranca otra vez.
        if (trabajo?.isActive != true) {
            val simulacion = intent?.getStringExtra(EXTRA_SIMULACION)?.let { TipoSimulacion.valueOf(it) }
            trabajo = scope.launch { medirNoche(simulacion) }
        }
        // STICKY: si Android matara el servicio por falta de memoria, lo vuelve a crear.
        return START_STICKY
    }

    private suspend fun medirNoche(simulacion: TipoSimulacion?) {
        val noche = nocheRepositorio.nocheActiva()
        if (noche == null) {
            terminar()
            return
        }
        val sensibilidad = ajustesRepositorio.ajustes.first().sensibilidad
        // En la simulación, 1 "minuto" de ventana dura 1 segundo.
        val msPorMinuto = if (simulacion != null) 1_000L else 60_000L
        val ventanaInicioEn = noche.horaLimiteEn - noche.ventanaMinutos * msPorMinuto
        val motor = MotorDecision(sensibilidad, ventanaInicioEn, noche.horaLimiteEn)

        val duracionMs = (noche.horaLimiteEn - System.currentTimeMillis()).coerceAtLeast(0L) + 30 * 60_000L
        wakeLock = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Descansa:noche")
            .apply { acquire(duracionMs) }
        EstadoNocturno.epocasMutable.value = 0

        val fuente = if (simulacion != null) epocasSimuladas(simulacion, ventanaInicioEn) else epocasDelAcelerometro(this)
        // first { } escucha época por época hasta que la condición sea verdadera.
        fuente.first { cruda ->
            val resultado = motor.procesar(cruda)
            nocheRepositorio.guardarEpoca(noche.id, resultado.epoca)
            EstadoNocturno.epocasMutable.value = motor.epocas.size
            when {
                resultado.decision == Decision.DESPERTAR -> {
                    nocheRepositorio.registrarDisparo(Disparo(MotivoDisparo.SUENO_LIGERO, System.currentTimeMillis()))
                    programador.dispararAhora()
                    true
                }
                // Ya pasó la hora límite: suena (o sonó) la de respaldo; no hay nada más que medir.
                cruda.minutoTs >= noche.horaLimiteEn -> true
                else -> false
            }
        }
        terminar()
    }

    private fun terminar() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    /** @return false si Android no permitió el primer plano (p. ej. reiniciado en segundo plano). */
    private fun iniciarEnPrimerPlano(): Boolean = try {
        val notificacion = crearNotificacion()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(ID_NOTIFICACION, notificacion, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(ID_NOTIFICACION, notificacion)
        }
        true
    } catch (_: Exception) {
        false
    }

    private fun crearNotificacion(): Notification {
        val gestor = getSystemService(NotificationManager::class.java)
        // Importancia baja: sin sonido ni vibración, solo un ícono discreto.
        gestor.createNotificationChannel(
            NotificationChannel(CANAL, getString(R.string.noche_canal), NotificationManager.IMPORTANCE_LOW),
        )
        val abrirApp = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CANAL)
            // Ícono del sistema de "alarma puesta" (los íconos pequeños deben ser de un solo color).
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(getString(R.string.modo_noche_activo))
            .setContentText(getString(R.string.noche_notificacion_texto))
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(abrirApp)
            .build()
    }

    companion object {
        const val EXTRA_SIMULACION = "simulacion"
        private const val CANAL = "noche"
        private const val ID_NOTIFICACION = 2
    }
}

/** Implementación real de [ControlServicioNocturno]: arranca y detiene [ServicioNocturno]. */
@Singleton
class ControlServicioNocturnoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ControlServicioNocturno {

    override fun iniciar(simulacion: TipoSimulacion?) {
        val intent = Intent(context, ServicioNocturno::class.java)
        if (simulacion != null) intent.putExtra(ServicioNocturno.EXTRA_SIMULACION, simulacion.name)
        // Se llama al pulsar "Activar modo noche": la app está en primer plano, así que se permite.
        ContextCompat.startForegroundService(context, intent)
    }

    override fun detener() {
        context.stopService(Intent(context, ServicioNocturno::class.java))
    }
}
