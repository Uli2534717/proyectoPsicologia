package com.upt.descansa.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =============================================================================
//  Paleta "Cartel sereno". ÚNICA fuente de colores de la app.
//  Reglas (ver CLAUDE.md → Sistema de diseño):
//  - MoradoProfundo es el ÚNICO color interactivo (en tema claro);
//    LavandaNoche lo es en el tema nocturno.
//  - AzulCartel solo en bordes y subrayados de acciones secundarias, nunca relleno.
//  - Turquesa y amarillo son DECORATIVOS: palabras gigantes y separadores.
//    Nunca en texto funcional menor a 32 sp.
//  - Sin grises de fondo, sin sombras ni degradados.
// =============================================================================

// --- Tema claro ---
/**
 * Morado profundo: color interactivo del tema claro (botones, enlaces,
 * etiquetas, íconos activos). Contraste ≈10:1 sobre blanco.
 * Reemplaza al verde de la guía original (pedido del equipo, D-21).
 */
val MoradoProfundo = Color(0xFF4B2E83)
val AzulCartel = Color(0xFF2B7BB9)
val TurquesaSereno = Color(0xFF56DDDB)
val AmarilloMantequilla = Color(0xFFF9E9A9)
val GrisCarbon = Color(0xFF333333)
val BlancoLienzo = Color(0xFFFFFFFF)

// --- Tema nocturno (derivado con las mismas reglas) ---
/** Fondo nocturno: morado casi negro. */
val MoradoNoche = Color(0xFF160F26)

/** Color interactivo del tema nocturno: lavanda clara, ≈9:1 sobre MoradoNoche. */
val LavandaNoche = Color(0xFFB9A8FF)

/** Texto nocturno: blanco hueso, menos agresivo que el blanco puro de noche. */
val TextoNoche = Color(0xFFF2F2EC)

/**
 * Azul secundario aclarado para fondo nocturno. SUPOSICIÓN: el prompt no define
 * el azul nocturno; AzulCartel sobre MoradoNoche da ≈3.6:1, poco para un borde
 * fino. Este da ≈7:1 (ver DECISIONES.md D-18).
 */
val AzulNoche = Color(0xFF8CC0EA)

/** Colores DECORATIVOS de las palabras gigantes. Se leen con `LocalColoresCartel.current`. */
@Immutable
data class ColoresCartel(
    /** Noche, calma, frío: Pendientes, Respiración, Relajación, Sonidos, Alarma, Modo noche. */
    val turquesa: Color = TurquesaSereno,
    /** Día, ánimo, calidez: Bienvenida, Inicio, Mañana, Aprender, Ajustes. */
    val amarillo: Color = AmarilloMantequilla,
)

/**
 * Separa los colores decorativos de los funcionales de Material, para que
 * nadie use por error el amarillo en un botón o un texto pequeño.
 */
val LocalColoresCartel = staticCompositionLocalOf { ColoresCartel() }
