package com.upt.descansa.sistema.alarma

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.upt.descansa.R
import com.upt.descansa.domain.alarma.AlarmaProgramada
import com.upt.descansa.domain.alarma.CalculoAlarma
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Servicio en primer plano que hace sonar la alarma.
 *
 * ¿Por qué un servicio y no solo una pantalla?
 * - El sonido debe seguir aunque la pantalla de timbre no se pueda mostrar
 *   (por ejemplo, si el usuario está usando el teléfono, Android muestra solo
 *   una notificación emergente).
 * - En Android 17, una app solo puede reproducir audio en segundo plano con un
 *   servicio en primer plano activo.
 *
 * Tipo `systemExempted`: la documentación lo permite a apps con el permiso de
 * alarmas exactas para "continuar alarmas en segundo plano" (ver DECISIONES.md).
 *
 * Es `directBootAware` y NO usa Hilt ni DataStore: debe funcionar aunque el
 * teléfono se haya reiniciado y siga bloqueado.
 */
class TimbreService : Service() {

    private var reproductor: ReproductorAlarma? = null
    private var bloqueoActivo: PowerManager.WakeLock? = null
    private val manejador = Handler(Looper.getMainLooper())
    private var horaLimiteMinutos = 7 * 60
    private var posponerMinutos = 5

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACCION_SONAR -> sonar(intent)
            ACCION_POSPONER -> posponer()
            ACCION_DETENER -> detener()
            // Si Android reinicia el servicio sin intent, ante la duda: suena.
            null -> sonar(Intent())
        }
        // Si el sistema mata el servicio mientras suena, lo recrea con el mismo intent.
        return START_REDELIVER_INTENT
    }

    private fun sonar(intent: Intent) {
        horaLimiteMinutos = intent.getIntExtra(EXTRA_HORA_LIMITE, horaLimiteMinutos)
        posponerMinutos = intent.getIntExtra(EXTRA_POSPONER, posponerMinutos)
        posponerMinutosActual = posponerMinutos
        val yaSonaba = reproductor != null
        // Se marca ANTES de publicar la notificación: la pantalla de timbre que
        // ésta abre se cierra sola si ve `sonando = false`.
        _sonando.value = true

        // Obligatorio en los primeros segundos tras startForegroundService().
        val tipo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
        } else {
            0
        }
        ServiceCompat.startForeground(this, ID_NOTIFICACION, crearNotificacion(), tipo)

        if (yaSonaba) return
        // Mantiene la CPU despierta mientras suena; se libera sola como máximo
        // al terminar el tiempo máximo de timbre.
        bloqueoActivo = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Descansa:Timbre")
            .apply { acquire(TIEMPO_MAXIMO_MS) }
        reproductor = ReproductorAlarma(this).also { it.iniciar() }
        // Para no gastar batería sin fin si nadie está cerca (ver DECISIONES.md).
        manejador.postDelayed({ detener() }, TIEMPO_MAXIMO_MS)
    }

    private fun posponer() {
        val ahora = System.currentTimeMillis()
        ProgramadorAlarma(applicationContext).programar(
            AlarmaProgramada(
                disparoEnMs = CalculoAlarma.horaPosposicion(ahora, posponerMinutos),
                horaLimiteMinutos = horaLimiteMinutos,
                posponerMinutos = posponerMinutos,
                esPosposicion = true,
            ),
        )
        detener()
    }

    private fun detener() {
        manejador.removeCallbacksAndMessages(null)
        reproductor?.detener()
        reproductor = null
        bloqueoActivo?.takeIf { it.isHeld }?.release()
        bloqueoActivo = null
        _sonando.value = false
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        // Si el sistema destruye el servicio, nunca dejamos sonido ni wake lock colgados.
        reproductor?.detener()
        bloqueoActivo?.takeIf { it.isHeld }?.release()
        _sonando.value = false
        super.onDestroy()
    }

    private fun crearNotificacion(): Notification {
        crearCanal(this)
        val abrirTimbre = PendingIntent.getActivity(
            this,
            0,
            TimbreActivity.intent(this, posponerMinutos),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val accion = { codigo: Int, intent: Intent ->
            PendingIntent.getService(this, codigo, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return NotificationCompat.Builder(this, CANAL_ALARMA)
            .setSmallIcon(R.drawable.ic_notificacion_alarma)
            .setContentTitle(getString(R.string.timbre_titulo))
            .setContentText(getString(R.string.timbre_notificacion_texto))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(abrirTimbre)
            // Abre la pantalla de timbre sobre la pantalla de bloqueo. Si Android
            // no lo permite (permiso de pantalla completa desactivado), se muestra
            // como notificación emergente y el sonido sigue igual.
            .setFullScreenIntent(abrirTimbre, true)
            .addAction(0, getString(R.string.timbre_posponer, posponerMinutos), accion(1, intentPosponer(this)))
            .addAction(0, getString(R.string.timbre_detener), accion(2, intentDetener(this)))
            .build()
    }

    companion object {
        const val CANAL_ALARMA = "alarma"
        private const val ID_NOTIFICACION = 7
        private const val ACCION_SONAR = "sonar"
        private const val ACCION_POSPONER = "posponer"
        private const val ACCION_DETENER = "detener"
        private const val EXTRA_HORA_LIMITE = "hora_limite"
        private const val EXTRA_POSPONER = "posponer_minutos"

        /** Tiempo máximo sonando: 30 minutos. */
        private const val TIEMPO_MAXIMO_MS = 30 * 60_000L

        private val _sonando = MutableStateFlow(false)

        /** `true` mientras la alarma suena. La pantalla de timbre se cierra cuando pasa a `false`. */
        val sonando: StateFlow<Boolean> = _sonando

        /** Minutos de "Posponer" de la alarma que suena (para abrir la pantalla de timbre desde la app). */
        var posponerMinutosActual: Int = 5
            private set

        fun intentSonar(context: Context, alarma: AlarmaProgramada?): Intent =
            Intent(context, TimbreService::class.java).setAction(ACCION_SONAR).apply {
                if (alarma != null) {
                    putExtra(EXTRA_HORA_LIMITE, alarma.horaLimiteMinutos)
                    putExtra(EXTRA_POSPONER, alarma.posponerMinutos)
                }
            }

        fun intentPosponer(context: Context): Intent =
            Intent(context, TimbreService::class.java).setAction(ACCION_POSPONER)

        fun intentDetener(context: Context): Intent =
            Intent(context, TimbreService::class.java).setAction(ACCION_DETENER)

        /**
         * Canal de notificaciones de la alarma: importancia alta y SIN sonido
         * propio (el sonido lo pone [ReproductorAlarma] con volumen creciente).
         */
        fun crearCanal(context: Context) {
            val canal = NotificationChannel(
                CANAL_ALARMA,
                context.getString(R.string.canal_alarma),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = context.getString(R.string.canal_alarma_descripcion)
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }
}
