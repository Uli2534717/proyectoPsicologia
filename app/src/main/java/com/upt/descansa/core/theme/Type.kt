package com.upt.descansa.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Familia tipográfica de la app.
 *
 * PENDIENTE: el diseño pide Nunito (400, 600, 700, 800) empaquetada en la app
 * para que funcione sin internet. Mientras se agregan los archivos .ttf a
 * `res/font/`, se usa la fuente del sistema. Al agregarlos, solo cambia esta línea:
 * `val FuenteDescansa = FontFamily(Font(R.font.nunito_regular, FontWeight.Normal), ...)`.
 */
val FuenteDescansa: FontFamily = FontFamily.Default

/**
 * Escala tipográfica según el prototipo (los "px" del diseño = sp en Android):
 * títulos de pantalla 28–30, texto 15–16, notas 12–13.
 * Todo en `sp` para respetar el tamaño de letra que el usuario eligió en su teléfono.
 */
val Tipografia = Typography(
    // Hora grande (modo noche, hora límite).
    displayLarge = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.ExtraBold, fontSize = 64.sp, lineHeight = 72.sp),
    // "7 h 12 min" de la tarjeta de última noche.
    displaySmall = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 46.sp),
    // Título de pantalla ("Buenas noches", "Respira con calma").
    headlineMedium = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp),
    // Subtítulos de sección ("Herramientas").
    titleLarge = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    // Título de tarjeta.
    titleMedium = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 23.sp),
    // Texto normal.
    bodyLarge = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 21.sp),
    // Notas.
    bodySmall = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    // Texto del botón principal: 18, peso 800.
    labelLarge = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, lineHeight = 22.sp),
    // Chips y etiquetas.
    labelMedium = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp),
    // Etiqueta de sección en mayúsculas ("TU ÚLTIMA NOCHE").
    labelSmall = TextStyle(fontFamily = FuenteDescansa, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.8.sp),
)
