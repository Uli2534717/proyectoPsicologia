package com.upt.descansa.sistema.alarma

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Recibe el aviso de AlarmManager cuando llega la hora y arranca el
 * [TimbreService], que hace sonar la alarma y muestra la pantalla de timbre.
 *
 * Es `directBootAware` (ver manifiesto): funciona aunque el teléfono se haya
 * reiniciado y nadie lo haya desbloqueado todavía.
 */
class AlarmaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACCION_SONAR) return
        val almacen = AlmacenAlarma(context)
        // Leemos hora límite y minutos de posposición ANTES de borrar el estado:
        // la alarma ya se usó y, si el usuario pospone, TimbreService programa otra.
        val alarma = almacen.leer()
        almacen.borrar()
        runCatching {
            ContextCompat.startForegroundService(context, TimbreService.intentSonar(context, alarma))
        }.onFailure {
            // No debería pasar (las alarmas exactas están exentas), pero si pasa
            // queda registrado para diagnosticarlo en las pruebas manuales.
            Log.e(TAG, "No se pudo iniciar el servicio de timbre", it)
        }
    }

    companion object {
        const val ACCION_SONAR = "com.upt.descansa.ALARMA_SONAR"
        private const val TAG = "AlarmaReceiver"
    }
}

/**
 * Reprograma la alarma guardada cuando el sistema la pudo haber borrado o movido:
 * - LOCKED_BOOT_COMPLETED: el teléfono encendió (aún bloqueado, en Direct Boot).
 * - BOOT_COMPLETED: el usuario ya lo desbloqueó tras encender.
 * - MY_PACKAGE_REPLACED: se instaló una versión nueva de la app.
 * - TIME_SET / TIMEZONE_CHANGED: el usuario o la red cambiaron la hora o la zona.
 *
 * Reprogramar dos veces (LOCKED y luego BOOT) no es problema: siempre es la misma alarma.
 * No inicia servicios en primer plano (Android 15+ restringe eso al arrancar).
 */
class ReinicioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in ACCIONES) return
        runCatching { ProgramadorAlarma(context.applicationContext).reprogramarGuardada() }
            .onFailure { Log.e(TAG, "No se pudo reprogramar la alarma (${intent.action})", it) }
    }

    private companion object {
        const val TAG = "ReinicioReceiver"
        val ACCIONES = setOf(
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
