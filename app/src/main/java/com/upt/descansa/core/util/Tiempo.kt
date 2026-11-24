package com.upt.descansa.core.util

import java.time.LocalTime
import java.time.ZonedDateTime
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

/**
 * La próxima vez (estrictamente después de [ahora]) que el reloj marque
 * [minutosDelDia] en la zona horaria de [ahora].
 *
 * Con hora límite 07:00: a las 23:00 → mañana 07:00; a las 05:00 → hoy 07:00;
 * justo a las 07:00 → mañana (una alarma "para ya" no tiene sentido).
 * Si esa hora no existe ese día por un cambio de horario, `atZone` la recorre
 * a la siguiente hora válida en vez de fallar.
 */
fun proximaOcurrencia(ahora: ZonedDateTime, minutosDelDia: Int): ZonedDateTime {
    val normalizado = Math.floorMod(minutosDelDia, MINUTOS_POR_DIA)
    val hora = LocalTime.of(normalizado / 60, normalizado % 60)
    val hoy = ahora.toLocalDate().atTime(hora).atZone(ahora.zone)
    return if (hoy.isAfter(ahora)) hoy else ahora.toLocalDate().plusDays(1).atTime(hora).atZone(ahora.zone)
}
