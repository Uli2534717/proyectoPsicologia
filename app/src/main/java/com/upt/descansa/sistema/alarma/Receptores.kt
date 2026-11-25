package com.upt.descansa.sistema.alarma

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.upt.descansa.domain.usecase.noche.ApagarAlarmaUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Lo despierta `AlarmManager` a la hora de la alarma. Solo arranca el servicio
 * de timbre: un BroadcastReceiver vive pocos segundos y no puede reproducir
 * sonido por minutos.
 *
 * Android permite iniciar un servicio en primer plano desde aquí, aunque la app
 * esté en segundo plano, porque lo disparó una alarma exacta.
 */
class AlarmaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ContextCompat.startForegroundService(context, TimbreService.intentSonar(context))
    }
}

/**
 * Vuelve a programar la alarma cuando Android la borra o la cambia:
 * - al encender el teléfono, aún bloqueado (`LOCKED_BOOT_COMPLETED`, Direct Boot),
 * - al desbloquearlo tras encender (`BOOT_COMPLETED`),
 * - al instalar una versión nueva de la app (`MY_PACKAGE_REPLACED`),
 * - al cambiar el permiso de alarmas exactas (Android 12+),
 * - al cambiar la hora o la zona horaria (`TIME_SET`, `TIMEZONE_CHANGED`).
 *
 * Solo REPROGRAMA; no reproduce nada. Desde Android 15 no se permite iniciar
 * ciertos servicios en primer plano desde `BOOT_COMPLETED`, y aquí no hace falta.
 *
 * Es `directBootAware` y no usa Hilt para reprogramar: antes del desbloqueo no
 * se pueden leer Room ni DataStore. Hilt solo se usa, ya desbloqueado, para
 * cerrar una noche cuya alarma se apagó antes del desbloqueo.
 */
class ArranqueReceiver : BroadcastReceiver() {

    /** Solo se aceptan estos avisos del sistema; cualquier otro Intent se ignora. */
    private val accionesValidas = setOf(
        Intent.ACTION_LOCKED_BOOT_COMPLETED,
        Intent.ACTION_BOOT_COMPLETED,
        Intent.ACTION_MY_PACKAGE_REPLACED,
        AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
        Intent.ACTION_TIME_CHANGED, // su texto es "android.intent.action.TIME_SET"
        Intent.ACTION_TIMEZONE_CHANGED,
    )

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in accionesValidas) return
        val app = context.applicationContext
        val programador = AlarmSchedulerImpl(app)
        when (intent.action) {
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> programador.reajustarPorCambioDeHora()
            else -> programador.reprogramarGuardada()
        }
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) cerrarNochePendiente(app, programador)
    }

    /** Ya desbloqueado: termina en Room la noche cuya alarma se apagó antes del desbloqueo. */
    private fun cerrarNochePendiente(app: Context, programador: AlarmSchedulerImpl) {
        val disparadaEn = programador.cierrePendienteDesde ?: return
        val apagar = EntryPointAccessors.fromApplication(app, PuntoEntradaAlarma::class.java).apagarAlarma()
        val resultado = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apagar(disparadaEn = disparadaEn)
                programador.cierrePendienteDesde = null
            } finally {
                resultado.finish()
            }
        }
    }
}

/** Acceso a Hilt desde clases que Android crea a mano (receptores sin `@AndroidEntryPoint`). */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface PuntoEntradaAlarma {
    fun apagarAlarma(): ApagarAlarmaUseCase
}
