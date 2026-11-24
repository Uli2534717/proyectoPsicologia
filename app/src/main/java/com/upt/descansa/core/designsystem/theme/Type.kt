package com.upt.descansa.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.upt.descansa.R

/**
 * Work Sans: la ÚNICA familia tipográfica de la app, empaquetada en `res/font/`
 * (funciona sin internet; licencia SIL OFL, ver CREDITS.md).
 *
 * Usamos la versión VARIABLE oficial de Google Fonts: un solo archivo contiene
 * todos los pesos, y aquí elegimos cada peso con `FontVariation.weight(...)`.
 * Así no hacen falta cuatro archivos sueltos (ver DECISIONES.md D-16).
 */
val WorkSans = FontFamily(
    Font(R.font.worksans_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.worksans_variable, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.worksans_variable, FontWeight.Black, variationSettings = FontVariation.Settings(FontVariation.weight(900))),
    Font(
        R.font.worksans_italic_variable,
        FontWeight.Black,
        FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(900)),
    ),
)

/**
 * Recorta el espacio de arriba y de abajo de la línea: con interlineado 0.70
 * las letras quedan casi tocándose, como en un cartel impreso.
 */
private val InterlineadoCompacto = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both,
)

/**
 * Escala "Cartel sereno" adaptada a móvil. Los nombres son los del sistema de diseño.
 * Todo en `sp`: respeta el tamaño de letra que el usuario elige en su teléfono.
 */
object TipoCartel {
    /** Palabra gigante de Inicio y pantallas principales. Se ajusta al ancho (PalabraCartel). */
    val cartelXL = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Black, fontSize = 120.sp,
        lineHeight = 0.70.em, letterSpacing = (-0.01).em, lineHeightStyle = InterlineadoCompacto,
    )

    /** Palabra gigante de las demás secciones. */
    val cartel = cartelXL.copy(fontSize = 96.sp)

    /** Títulos dentro de una pantalla. */
    val subtitulo = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Black, fontSize = 32.sp,
        lineHeight = 1.06.em, letterSpacing = (-0.01).em,
    )

    /** Títulos de tarjetas y elementos de lista. */
    val titulo = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Medium, fontSize = 20.sp,
        lineHeight = 1.15.em, letterSpacing = (-0.01).em,
    )

    /** Texto normal. */
    val cuerpo = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Normal, fontSize = 17.sp,
        lineHeight = 1.45.em, letterSpacing = (-0.01).em,
    )

    /** Etiquetas de categoría y botones (el texto se pasa a MAYÚSCULAS en el componente). */
    val etiqueta = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Medium, fontSize = 14.sp,
        lineHeight = 1.45.em, letterSpacing = 0.05.em,
    )

    /** Pies de imagen y descripciones cortas (en MAYÚSCULAS). */
    val leyenda = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Normal, fontSize = 14.sp,
        lineHeight = 1.45.em, letterSpacing = 0.05.em,
    )

    /** Logotipo: Work Sans 900 cursiva. */
    val logotipo = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic,
        fontSize = 40.sp, lineHeight = 1.0.em, letterSpacing = (-0.01).em,
    )

    /** Subtítulo del logotipo, en mayúsculas muy espaciadas. */
    val logotipoSubtitulo = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.2.em,
    )

    /** Hora grande (modo noche, hora límite, timbre). Es funcional, no decorativa. */
    val hora = TextStyle(
        fontFamily = WorkSans, fontWeight = FontWeight.Black, fontSize = 64.sp,
        lineHeight = 1.0.em, letterSpacing = (-0.01).em,
    )
}

/**
 * La misma escala dentro de Material 3, para que los componentes estándar
 * (diálogos, selector de hora, campos de texto) también usen Work Sans.
 */
val TipografiaMaterial = Typography(
    displayLarge = TipoCartel.hora,
    displayMedium = TipoCartel.hora.copy(fontSize = 48.sp),
    displaySmall = TipoCartel.subtitulo.copy(fontSize = 36.sp),
    headlineLarge = TipoCartel.subtitulo,
    headlineMedium = TipoCartel.subtitulo,
    headlineSmall = TipoCartel.subtitulo.copy(fontSize = 24.sp),
    titleLarge = TipoCartel.titulo.copy(fontSize = 22.sp),
    titleMedium = TipoCartel.titulo,
    titleSmall = TipoCartel.titulo.copy(fontSize = 17.sp),
    bodyLarge = TipoCartel.cuerpo,
    bodyMedium = TipoCartel.cuerpo.copy(fontSize = 15.sp),
    bodySmall = TipoCartel.leyenda.copy(letterSpacing = 0.em),
    labelLarge = TipoCartel.etiqueta.copy(fontSize = 16.sp),
    labelMedium = TipoCartel.etiqueta,
    labelSmall = TipoCartel.etiqueta.copy(fontSize = 12.sp),
)
