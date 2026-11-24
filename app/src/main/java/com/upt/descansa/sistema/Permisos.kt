package com.upt.descansa.sistema

import android.Manifest
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Consultas de permisos en un solo lugar, con las diferencias por versión de Android.
 * En la Fase 6, Ajustes mostrará el estado de cada uno con un botón para corregirlo.
 */
object Permisos {

    /** Android 13+ pide permiso para notificaciones; antes venía concedido. */
    fun faltaNotificaciones(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    /**
     * Android 14+ solo deja la "pantalla completa" a apps de llamadas y alarmas,
     * y el usuario puede desactivarla.
     */
    fun puedeUsarPantallaCompleta(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
            context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()

    /** Abre la pantalla de Android para activar la pantalla completa de esta app. */
    fun abrirAjustePantallaCompleta(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return
        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT, Uri.parse("package:${context.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Algunos fabricantes no tienen esa pantalla: abrimos la info de la app.
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}
