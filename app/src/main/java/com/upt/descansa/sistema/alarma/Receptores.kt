package com.upt.descansa.sistema.alarma

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

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
 * - al reiniciar el teléfono (`BOOT_COMPLETED`),
 * - al instalar una versión nueva de la app (`MY_PACKAGE_REPLACED`),
 * - al cambiar el permiso de alarmas exactas (Android 12+).
 *
 * Solo REPROGRAMA; no reproduce nada. Desde Android 15 no se permite iniciar
 * ciertos servicios en primer plano desde `BOOT_COMPLETED`, y aquí no hace falta.
 */
class ArranqueReceiver : BroadcastReceiver() {

    /** Solo se aceptan estos avisos del sistema; cualquier otro Intent se ignora. */
    private val accionesValidas = setOf(
        Intent.ACTION_BOOT_COMPLETED,
        Intent.ACTION_MY_PACKAGE_REPLACED,
        AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
    )

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in accionesValidas) return
        // Se crea directamente (sin Hilt): solo necesita el Context y así no
        // dependemos de la inyección dentro de un receptor del sistema.
        AlarmSchedulerImpl(context.applicationContext).reprogramarGuardada()
    }
}
