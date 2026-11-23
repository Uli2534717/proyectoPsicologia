package com.upt.dormirbien.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.upt.dormirbien.domain.model.ModoTema

/** Esquema de colores oscuro (el predeterminado). */
private val EsquemaOscuro = darkColorScheme(
    primary = Lavanda,
    onPrimary = LavandaSobre,
    primaryContainer = LavandaContenedor,
    onPrimaryContainer = LavandaSobreContenedor,
    secondary = VerdeAgua,
    onSecondary = VerdeAguaSobre,
    secondaryContainer = VerdeAguaContenedor,
    onSecondaryContainer = VerdeAguaSobreContenedor,
    tertiary = Luna,
    onTertiary = LunaSobre,
    tertiaryContainer = LunaContenedor,
    onTertiaryContainer = LunaSobreContenedor,
    background = NocheFondo,
    onBackground = NocheTexto,
    surface = NocheSuperficie,
    onSurface = NocheTexto,
    surfaceVariant = NocheSuperficieVariante,
    onSurfaceVariant = NocheTextoVariante,
    surfaceContainerLowest = NocheFondo,
    surfaceContainerLow = NocheContenedorBajo,
    surfaceContainer = NocheContenedor,
    surfaceContainerHigh = NocheContenedorAlto,
    surfaceContainerHighest = NocheContenedorMasAlto,
    outline = NocheContorno,
    error = ErrorNoche,
    onError = ErrorNocheSobre,
)

/** Esquema de colores claro (opcional, se elige en Ajustes). */
private val EsquemaClaro = lightColorScheme(
    primary = LavandaOscura,
    primaryContainer = LavandaOscuraContenedor,
    onPrimaryContainer = LavandaOscuraSobreContenedor,
    secondary = VerdeAguaOscuro,
    secondaryContainer = VerdeAguaOscuroContenedor,
    onSecondaryContainer = VerdeAguaOscuroSobreContenedor,
    tertiary = LunaOscura,
    tertiaryContainer = LunaOscuraContenedor,
    onTertiaryContainer = LunaOscuraSobreContenedor,
    background = DiaFondo,
    onBackground = DiaTexto,
    surface = DiaFondo,
    onSurface = DiaTexto,
    surfaceVariant = DiaSuperficieVariante,
    onSurfaceVariant = DiaTextoVariante,
    surfaceContainerLowest = DiaFondo,
    surfaceContainerLow = DiaContenedorBajo,
    surfaceContainer = DiaContenedor,
    surfaceContainerHigh = DiaContenedorAlto,
    surfaceContainerHighest = DiaContenedorMasAlto,
    outline = DiaContorno,
    error = ErrorDia,
)

/**
 * Traduce la preferencia del usuario a "¿uso tema oscuro?".
 * Es @Composable porque, en modo SISTEMA, necesita leer la configuración actual.
 */
@Composable
fun ModoTema.usaTemaOscuro(): Boolean = when (this) {
    ModoTema.OSCURO -> true
    ModoTema.CLARO -> false
    ModoTema.SISTEMA -> isSystemInDarkTheme()
}

/**
 * Tema de DormirBien. Envuelve toda la interfaz (ver MainActivity).
 *
 * No usamos "color dinámico" (los colores del fondo de pantalla en Android 12+)
 * a propósito: queremos que la paleta nocturna y su contraste sean siempre
 * los mismos, en todos los teléfonos.
 */
@Composable
fun DormirBienTheme(
    modoTema: ModoTema = ModoTema.OSCURO,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (modoTema.usaTemaOscuro()) EsquemaOscuro else EsquemaClaro,
        typography = Tipografia,
        content = content,
    )
}
