package com.upt.descansa.core.util

import java.util.Locale

/** Minutos que tiene un día. */
const val MINUTOS_POR_DIA = 24 * 60

/**
 * Convierte "minutos desde las 00:00" a texto "HH:mm" (formato de 24 h del prototipo).
 * Acepta valores negativos o mayores a un día y los ajusta: así una ventana que
 * cruza la medianoche (hora límite 00:10, ventana 30 → 23:40) se muestra bien.
 */
fun formatearHora(minutosDelDia: Int): String {
    val normalizado = Math.floorMod(minutosDelDia, MINUTOS_POR_DIA)
    return String.format(Locale.ROOT, "%02d:%02d", normalizado / 60, normalizado % 60)
}

/** Hora desde la que puede sonar la alarma: hora límite menos la ventana. */
fun inicioVentana(horaLimiteMinutos: Int, ventanaMinutos: Int): Int =
    Math.floorMod(horaLimiteMinutos - ventanaMinutos, MINUTOS_POR_DIA)
