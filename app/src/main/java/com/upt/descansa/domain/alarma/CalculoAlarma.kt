package com.upt.descansa.domain.alarma

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Cálculos de la alarma de respaldo. Kotlin puro (solo java.time): se prueba
 * con JUnit sin teléfono.
 */
object CalculoAlarma {

    /**
     * Próximo momento (epoch ms, UTC) en que debe sonar la alarma de respaldo.
     *
     * Regla de la sección 6.1: "si la hora límite ya pasó hoy, programa para mañana".
     * Si la hora es EXACTAMENTE ahora, también se pasa a mañana: una alarma en
     * el pasado sonaría al instante.
     *
     * Por qué con ZonedDateTime y no sumando 24 h: en zonas con horario de
     * verano un día puede durar 23 o 25 h. `atZone` además resuelve horas que
     * no existen (se recorre a la siguiente válida).
     *
     * @param ahoraMs hora actual en epoch ms.
     * @param zona zona horaria del teléfono.
     * @param horaLimiteMinutos minutos desde las 00:00 (7:00 = 420).
     */
    fun proximaHoraLimite(ahoraMs: Long, zona: ZoneId, horaLimiteMinutos: Int): Long {
        val ahora = Instant.ofEpochMilli(ahoraMs).atZone(zona)
        val hora = LocalTime.of(horaLimiteMinutos / 60, horaLimiteMinutos % 60)
        var candidata: ZonedDateTime = ahora.toLocalDate().atTime(hora).atZone(zona)
        if (!candidata.isAfter(ahora)) {
            candidata = ahora.toLocalDate().plusDays(1).atTime(hora).atZone(zona)
        }
        return candidata.toInstant().toEpochMilli()
    }

    /** Momento en que debe sonar una alarma pospuesta. */
    fun horaPosposicion(ahoraMs: Long, posponerMinutos: Int): Long =
        ahoraMs + posponerMinutos * 60_000L
}
