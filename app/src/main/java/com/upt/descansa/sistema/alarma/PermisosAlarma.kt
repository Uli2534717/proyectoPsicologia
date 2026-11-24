package com.upt.descansa.sistema.alarma

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

// =============================================================================
//  Revisión de los permisos que usa la alarma. Ninguno es necesario para que
//  SUENE (eso lo garantiza el servicio de timbre); sirven para que suene a la
//  hora exacta y para que la pantalla de timbre aparezca sobre el bloqueo.
// =============================================================================

/**
 * ¿Se pueden programar alarmas exactas?
 * Antes de Android 12 no hacía falta permiso. Desde Android 13 la app declara
 * `USE_EXACT_ALARM` (apps de despertador), que se concede al instalar.
 */
fun puedeProgramarExactas(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
        context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

/**
 * ¿Puede aparecer la pantalla de timbre sobre el bloqueo?
 * Necesita notificaciones activadas y, desde Android 14, el permiso de
 * "notificaciones de pantalla completa" (`USE_FULL_SCREEN_INTENT`).
 */
fun puedeMostrarPantallaAlarma(context: Context): Boolean {
    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
        context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
}

/** ¿Todo listo para la alarma? Si no, el modo noche muestra un aviso. */
fun alarmaConTodosLosPermisos(context: Context): Boolean =
    puedeProgramarExactas(context) && puedeMostrarPantallaAlarma(context)

/** Abre la pantalla de Ajustes de Android donde se corrige el primer permiso que falte. */
fun intentAjustesAlarma(context: Context): Intent {
    val paquete = Uri.fromParts("package", context.packageName, null)
    val notificacionesActivas = NotificationManagerCompat.from(context).areNotificationsEnabled()
    val intent = when {
        !notificacionesActivas ->
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !puedeMostrarPantallaAlarma(context) ->
            Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT, paquete)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !puedeProgramarExactas(context) ->
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, paquete)
        else -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, paquete)
    }
    return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
