package com.upt.descansa.core.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
//  Tokens de color de Descansa (sección 4 del documento del proyecto).
//  Son la ÚNICA fuente de colores de la app: si el prototipo cambia, se cambia
//  aquí y se actualiza toda la interfaz. No escribas colores sueltos en pantallas.
// =============================================================================

/** Fondo general de la app. */
val Fondo = Color(0xFF0F1B2D)

/** Tarjetas. */
val Superficie = Color(0xFF1A2A44)

/** Cajas dentro de tarjetas y avisos. */
val SuperficieSecundaria = Color(0xFF14233A)

/** Opción elegida (chips, tarjetas seleccionadas). */
val SuperficieSeleccionada = Color(0xFF2A3A66)

/** Borde de chips y campos de texto. */
val Borde = Color(0xFF34496C)

/** Borde tenue (separadores, avisos). */
val BordeTenue = Color(0xFF243756)

/** Acento principal: botones, elementos activos. */
val Lavanda = Color(0xFF9B8CFF)

/** Acento secundario: etiquetas de sección, confirmaciones, interruptores. */
val Turquesa = Color(0xFF5FD1C4)

/** Texto principal. */
val Texto = Color(0xFFE8ECF5)

/** Texto secundario (descripciones, notas). */
val TextoAtenuado = Color(0xFFA9B4C8)

/** Advertencias (batería baja, permisos faltantes). */
val Advertencia = Color(0xFFFFB86B)

/** Fondo de la pantalla "Modo noche activo": casi negro para no iluminar el cuarto. */
val FondoModoNoche = Color(0xFF070D17)

/** Texto sobre botones lavanda (el prototipo usa el color del fondo). */
val TextoSobreLavanda = Fondo
