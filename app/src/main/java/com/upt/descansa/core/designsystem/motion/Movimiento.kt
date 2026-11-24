package com.upt.descansa.core.designsystem.motion

import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Movimiento "Cartel sereno": sobrio, sin rebotes ni brillos.
 * TODAS las animaciones de la app toman sus duraciones y curvas de aquí.
 */
object Movimiento {
    /** Respuesta a un toque (botones, etiquetas escalonadas). */
    const val CORTA = 150

    /** Cambios de contenido y transiciones entre pantallas. */
    const val MEDIA = 300

    /** Entradas protagonistas (palabra cartel, "soltar", gráficas). */
    const val LARGA = 600

    /** Retraso entre elementos que aparecen uno tras otro. */
    const val ESCALON = 50

    /** Una vuelta del sello destacado. */
    const val VUELTA_SELLO = 20_000

    /** Desplazamiento del separador en zigzag. */
    const val CICLO_ONDA = 3_000

    /** Curva de entrada: arranca rápido y frena suave. */
    fun <T> entrada(duracion: Int = MEDIA, retraso: Int = 0): TweenSpec<T> =
        tween(durationMillis = duracion, delayMillis = retraso, easing = FastOutSlowInEasing)

    /** Curva de salida. */
    fun <T> salida(duracion: Int = MEDIA): TweenSpec<T> =
        tween(durationMillis = duracion, easing = LinearOutSlowInEasing)
}

/**
 * `true` si el usuario desactivó las animaciones en Android
 * (Opciones de accesibilidad → "Quitar animaciones", o escala de animación en 0).
 *
 * En ese caso: entradas = fundido simple o nada; el sello deja de girar;
 * el separador deja de moverse; el círculo de respiración queda estático.
 */
@Composable
fun animacionesReducidas(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}
