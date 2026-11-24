package com.upt.descansa.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Esquema de Material 3 construido con los tokens del prototipo.
 * Así los componentes estándar (Switch, Slider, Checkbox...) toman los colores
 * correctos sin configurarlos uno por uno.
 */
private val EsquemaDescansa = darkColorScheme(
    primary = Lavanda,
    onPrimary = TextoSobreLavanda,
    primaryContainer = SuperficieSeleccionada,
    onPrimaryContainer = Texto,
    secondary = Turquesa,
    onSecondary = Fondo,
    secondaryContainer = SuperficieSeleccionada,
    onSecondaryContainer = Texto,
    tertiary = Advertencia,
    onTertiary = Fondo,
    background = Fondo,
    onBackground = Texto,
    surface = Fondo,
    onSurface = Texto,
    surfaceVariant = Superficie,
    onSurfaceVariant = TextoAtenuado,
    surfaceContainerLowest = FondoModoNoche,
    surfaceContainerLow = SuperficieSecundaria,
    surfaceContainer = Superficie,
    surfaceContainerHigh = Superficie,
    surfaceContainerHighest = SuperficieSeleccionada,
    outline = Borde,
    outlineVariant = BordeTenue,
    error = Advertencia,
    onError = Fondo,
)

/** Esquinas muy redondeadas (tarjetas 16–24 según el prototipo). */
private val FormasDescansa = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
)

/**
 * Tema de Descansa. Solo existe versión oscura: la app se usa de noche y el
 * contraste de todos los tokens se revisó sobre fondo oscuro (ver DECISIONES.md).
 * Tampoco usamos "color dinámico" de Android 12+, para que se vea igual en todos
 * los teléfonos.
 */
@Composable
fun DescansaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaDescansa,
        typography = Tipografia,
        shapes = FormasDescansa,
        content = content,
    )
}
