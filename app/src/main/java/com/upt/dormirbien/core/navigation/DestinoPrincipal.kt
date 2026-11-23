package com.upt.dormirbien.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.upt.dormirbien.R

/**
 * Las 5 pestañas de la barra de navegación inferior.
 *
 * Material recomienda entre 3 y 5 pestañas; el resto de los módulos
 * (vaciado mental, respiración, aprende, etc.) se abren desde Inicio.
 *
 * @property ruta pantalla a la que lleva la pestaña.
 * @property etiqueta texto visible (también lo lee TalkBack).
 * @property icono ícono de la pestaña.
 */
enum class DestinoPrincipal(
    val ruta: RutaApp,
    @StringRes val etiqueta: Int,
    val icono: ImageVector,
) {
    INICIO(RutaInicio, R.string.nav_inicio, Icons.Filled.Home),
    SONIDOS(RutaSonidos, R.string.nav_sonidos, Icons.Filled.PlayArrow),
    ALARMA(RutaAlarma, R.string.nav_alarma, Icons.Filled.Notifications),
    EMOCIONES(RutaEmociones, R.string.nav_emociones, Icons.Filled.Face),
    AJUSTES(RutaAjustes, R.string.nav_ajustes, Icons.Filled.Settings),
}
