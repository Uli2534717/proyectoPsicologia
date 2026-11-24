package com.upt.descansa.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.upt.descansa.domain.model.ModoTema

/**
 * Tema de día "Cartel sereno": lienzo lila suave, texto carbón morado y morado profundo como único color interactivo.
 * Todas las superficies son del mismo lila: sin grises de fondo ni elevación tonal.
 */
private val EsquemaClaro = lightColorScheme(
    primary = MoradoProfundo,
    onPrimary = LilaLienzo,
    primaryContainer = LilaLienzo,
    onPrimaryContainer = MoradoProfundo,
    secondary = VioletaSecundario,
    onSecondary = LilaLienzo,
    tertiary = VioletaCartel,
    onTertiary = CarbonMorado,
    background = LilaLienzo,
    onBackground = CarbonMorado,
    surface = LilaLienzo,
    onSurface = CarbonMorado,
    surfaceVariant = LilaLienzo,
    onSurfaceVariant = CarbonMorado,
    surfaceContainerLowest = LilaLienzo,
    surfaceContainerLow = LilaLienzo,
    surfaceContainer = LilaLienzo,
    surfaceContainerHigh = LilaLienzo,
    surfaceContainerHighest = LilaLienzo,
    surfaceTint = LilaLienzo,
    outline = CarbonMorado,
    outlineVariant = CarbonMorado,
    error = CarbonMorado,
    onError = LilaLienzo,
)

/**
 * Tema nocturno derivado: morado casi negro, texto hueso y LAVANDA como color
 * interactivo (el morado profundo no tendría contraste sobre fondo oscuro).
 */
private val EsquemaNocturno = darkColorScheme(
    primary = LavandaNoche,
    onPrimary = MoradoNoche,
    primaryContainer = MoradoNoche,
    onPrimaryContainer = LavandaNoche,
    secondary = RosaLilaNoche,
    onSecondary = MoradoNoche,
    tertiary = VioletaCartel,
    onTertiary = MoradoNoche,
    background = MoradoNoche,
    onBackground = TextoNoche,
    surface = MoradoNoche,
    onSurface = TextoNoche,
    surfaceVariant = MoradoNoche,
    onSurfaceVariant = TextoNoche,
    surfaceContainerLowest = MoradoNoche,
    surfaceContainerLow = MoradoNoche,
    surfaceContainer = MoradoNoche,
    surfaceContainerHigh = MoradoNoche,
    surfaceContainerHighest = MoradoNoche,
    surfaceTint = MoradoNoche,
    outline = TextoNoche,
    outlineVariant = TextoNoche,
    error = RosaLilaNoche,
    onError = MoradoNoche,
)

/** Material usa estas formas en diálogos y campos; las limitamos a las permitidas. */
private val FormasMaterial = Shapes(
    extraSmall = Formas.tarjeta,
    small = Formas.tarjeta,
    medium = Formas.tarjeta,
    large = Formas.tarjeta,
    extraLarge = Formas.tarjeta,
)

/** `true` si la pantalla actual usa el tema nocturno (para decidir íconos de la barra de estado, etc.). */
val LocalEsNocturno = staticCompositionLocalOf { false }

/** Traduce la preferencia del usuario a "¿uso tema nocturno?". */
@Composable
fun ModoTema.esNocturno(): Boolean = when (this) {
    ModoTema.AUTOMATICO -> isSystemInDarkTheme()
    ModoTema.CLARO -> false
    ModoTema.OSCURO -> true
}

/**
 * Tema de Descansa. Por defecto sigue al sistema; el usuario puede fijarlo en Ajustes.
 * No hay color dinámico: la paleta del cartel es siempre la misma.
 */
@Composable
fun DescansaTheme(
    nocturno: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColoresCartel provides ColoresCartel(
            fondoIlustracion = if (nocturno) MoradoMarcoNoche else LilaMarco,
        ),
        LocalEsNocturno provides nocturno,
    ) {
        MaterialTheme(
            colorScheme = if (nocturno) EsquemaNocturno else EsquemaClaro,
            typography = TipografiaMaterial,
            shapes = FormasMaterial,
        ) {
            IconosBarrasSistema(claros = nocturno)
            content()
        }
    }
}

/**
 * Fuerza el tema nocturno en una parte de la interfaz, sin importar lo que eligió
 * el usuario. Se usa en Respiración, Modo noche y la pantalla de timbre, para no
 * deslumbrar de noche.
 */
@Composable
fun TemaNocturno(content: @Composable () -> Unit) {
    DescansaTheme(nocturno = true, content = content)
}

/**
 * Íconos de la barra de estado y de navegación: claros sobre fondo nocturno,
 * oscuros sobre lienzo blanco. Al salir de un tema nocturno forzado se restauran.
 */
@Composable
private fun IconosBarrasSistema(claros: Boolean) {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    DisposableEffect(claros) {
        val ventana = (vista.context as? Activity)?.window
        val controlador = ventana?.let { WindowCompat.getInsetsController(it, vista) }
        val antesEstado = controlador?.isAppearanceLightStatusBars
        val antesNavegacion = controlador?.isAppearanceLightNavigationBars
        controlador?.isAppearanceLightStatusBars = !claros
        controlador?.isAppearanceLightNavigationBars = !claros
        onDispose {
            if (antesEstado != null) controlador.isAppearanceLightStatusBars = antesEstado
            if (antesNavegacion != null) controlador.isAppearanceLightNavigationBars = antesNavegacion
        }
    }
}
