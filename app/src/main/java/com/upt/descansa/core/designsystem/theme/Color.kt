package com.upt.descansa.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =============================================================================
//  Paleta "Cartel sereno" en MORADO (pedido del equipo, D-21 y D-23).
//  ÚNICA fuente de colores de la app.
//  - De día: lienzo lila muy suave, texto carbón morado, morado profundo interactivo.
//  - De noche: fondo morado casi negro, texto hueso, lavanda interactiva.
//  - Las palabras gigantes son DECORATIVAS, en dos tonos de morado:
//    violeta (secciones de noche) y orquídea (secciones de día).
//    Nunca se usan en texto funcional menor a 32 sp.
//  - Sin sombras ni degradados.
// =============================================================================

// --- Tema de día (lila claro) ---
/** Fondo de todas las pantallas de día: lila muy suave (antes blanco). */
val LilaLienzo = Color(0xFFF6F2FF)

/** Fondo del marco de los dibujos de objetos (un lila un poco más intenso). */
val LilaMarco = Color(0xFFECE3FF)

/** Texto y bordes de día: carbón con matiz morado (≈13.7:1 sobre LilaLienzo). */
val CarbonMorado = Color(0xFF2B2140)

/**
 * Bordes de día: morado suave en lugar de casi negro (D-24).
 * ≈3.3:1 sobre LilaLienzo: cumple el mínimo de 3:1 para bordes de controles.
 */
val BordeLila = Color(0xFF8E7BBE)

/** ÚNICO color interactivo de día: botones, enlaces, etiquetas (≈9.4:1 sobre LilaLienzo). */
val MoradoProfundo = Color(0xFF4B2E83)

/** Acciones secundarias de día: bordes y subrayados (≈4.6:1). Nunca relleno. */
val VioletaSecundario = Color(0xFF7A5BC4)

// --- Tema de noche ---
/** Fondo nocturno: morado casi negro. */
val MoradoNoche = Color(0xFF160F26)

/** Marco de los dibujos de objetos de noche. */
val MoradoMarcoNoche = Color(0xFF241A3B)

/** Bordes de noche: morado apagado (≈3.2:1 sobre MoradoNoche), en lugar de blanco. */
val BordeNoche = Color(0xFF6E5C9A)

/** Texto nocturno: blanco hueso con un toque lila. */
val TextoNoche = Color(0xFFF1ECFA)

/** Color interactivo nocturno: lavanda clara (≈9:1 sobre MoradoNoche). */
val LavandaNoche = Color(0xFFB9A8FF)

/** Acciones secundarias de noche: rosa lila (≈11:1). */
val RosaLilaNoche = Color(0xFFE7B8F5)

// --- Palabras gigantes (decorativas) ---
/** Secciones de noche (Pendientes, Respiración, Relajación, Sonidos, Alarma, Modo noche). */
val VioletaCartel = Color(0xFF8B6FE8)

/** Secciones de día (Bienvenida, Inicio, Mañana, Aprender, Ajustes). */
val OrquideaCartel = Color(0xFFC7A6F2)

/**
 * Colores DECORATIVOS, separados de los funcionales de Material para que
 * nadie los use por error en un botón o un texto pequeño.
 * Se leen con `LocalColoresCartel.current`.
 */
@Immutable
data class ColoresCartel(
    /** Palabra gigante de las secciones de noche. */
    val noche: Color = VioletaCartel,
    /** Palabra gigante de las secciones de día. */
    val dia: Color = OrquideaCartel,
    /** Fondo del marco de los dibujos de objetos. */
    val fondoIlustracion: Color = LilaMarco,
)

val LocalColoresCartel = staticCompositionLocalOf { ColoresCartel() }
