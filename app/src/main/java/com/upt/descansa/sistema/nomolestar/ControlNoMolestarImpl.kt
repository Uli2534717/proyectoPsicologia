package com.upt.descansa.sistema.nomolestar

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.upt.descansa.domain.repository.ControlNoMolestar
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** La plataforma real: NotificationManager + SharedPreferences. */
private class PlataformaAndroid(context: Context) : PlataformaNoMolestar {
    private val gestor = context.getSystemService(NotificationManager::class.java)
    private val prefs = context.getSharedPreferences("no_molestar", Context.MODE_PRIVATE)

    override val tienePermiso: Boolean
        get() = gestor.isNotificationPolicyAccessGranted

    // Android 15 = VANILLA_ICE_CREAM (API 35). Nuestra targetSdk es 37, así que aplica.
    override val reglaPropia: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

    override var filtroActual: Int
        get() = gestor.currentInterruptionFilter
        set(valor) = gestor.setInterruptionFilter(valor)

    override var filtroGuardado: Int?
        get() = prefs.getInt(CLAVE, 0).takeIf { it > 0 }
        set(valor) = prefs.edit(commit = true) { if (valor == null) remove(CLAVE) else putInt(CLAVE, valor) }

    private companion object {
        const val CLAVE = "filtro_guardado"
    }
}

/**
 * Control real del No molestar. Toda la lógica está en [GestorNoMolestar] (probada
 * con JUnit); aquí solo se conecta con Android y se atrapan errores: si el usuario
 * quita el permiso a media noche, Android lanza SecurityException y no queremos
 * que eso cierre la app ni impida que suene la alarma.
 */
@Singleton
class ControlNoMolestarImpl @Inject constructor(
    @ApplicationContext context: Context,
) : ControlNoMolestar {

    private val plataforma = PlataformaAndroid(context)
    private val gestor = GestorNoMolestar(plataforma)

    override fun tienePermiso(): Boolean = plataforma.tienePermiso

    override fun activar(): Boolean = try {
        gestor.activar()
    } catch (_: SecurityException) {
        false
    }

    override fun restaurar() {
        try {
            gestor.restaurar()
        } catch (_: SecurityException) {
            plataforma.filtroGuardado = null
        }
    }
}

/** Abre la pantalla de Android donde el usuario da el "Acceso a No molestar". */
fun intentPermisoNoMolestar(): Intent =
    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
