package com.upt.descansa.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

// =============================================================================
//  Formas y espaciado "Cartel sereno". SOLO estos valores están permitidos.
// =============================================================================

/** Radios de esquina permitidos: 0, 4, 20 dp y píldora. */
object Formas {
    /** 0 dp: etiquetas de categoría. */
    val recta = RectangleShape

    /** 4 dp: tarjetas, campos de texto e imágenes. */
    val tarjeta = RoundedCornerShape(4.dp)

    /** 20 dp: botones normales. */
    val boton = RoundedCornerShape(20.dp)

    /** Píldora (50 %): solo el botón principal de cada pantalla. */
    val pildora = RoundedCornerShape(percent = 50)
}

/** Grosores de borde. */
object Bordes {
    val normal = 1.5.dp
    val presionado = 2.5.dp
    val sello = 2.dp
}

/**
 * Espaciado con unidad base de 4 dp.
 * Valores permitidos: 4, 8, 10, 12, 16, 20, 24, 30 y 50 dp.
 */
object Espacio {
    val xxs = 4.dp
    val xs = 8.dp
    val s = 10.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 30.dp
    val enorme = 50.dp

    /** Margen lateral de toda pantalla. */
    val margenPantalla = xl

    /** Entre secciones de una pantalla. */
    val entreSecciones = enorme

    /** Entre elementos de una sección. */
    val entreElementos = xl

    /** Relleno interior de las tarjetas. */
    val rellenoTarjeta = xl
}
