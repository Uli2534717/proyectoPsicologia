package com.upt.descansa.domain.alarma

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/** Casos límite de la sección 13: medianoche, hora que ya pasó, horario de verano. */
class CalculoAlarmaTest {

    private val mexico = ZoneId.of("America/Mexico_City")

    private fun ms(zona: ZoneId, anio: Int, mes: Int, dia: Int, h: Int, m: Int) =
        ZonedDateTime.of(anio, mes, dia, h, m, 0, 0, zona).toInstant().toEpochMilli()

    @Test
    fun `si la hora limite aun no llega suena hoy`() {
        val ahora = ms(mexico, 2026, 11, 24, 5, 0)
        assertEquals(ms(mexico, 2026, 11, 24, 7, 0), CalculoAlarma.proximaHoraLimite(ahora, mexico, 7 * 60))
    }

    @Test
    fun `si la hora limite ya paso suena manana`() {
        val ahora = ms(mexico, 2026, 11, 24, 23, 30)
        assertEquals(ms(mexico, 2026, 11, 25, 7, 0), CalculoAlarma.proximaHoraLimite(ahora, mexico, 7 * 60))
    }

    @Test
    fun `si es exactamente la hora limite suena manana`() {
        val ahora = ms(mexico, 2026, 11, 24, 7, 0)
        assertEquals(ms(mexico, 2026, 11, 25, 7, 0), CalculoAlarma.proximaHoraLimite(ahora, mexico, 7 * 60))
    }

    @Test
    fun `cruza fin de mes y de anio`() {
        val ahora = ms(mexico, 2026, 12, 31, 22, 0)
        assertEquals(ms(mexico, 2027, 1, 1, 6, 30), CalculoAlarma.proximaHoraLimite(ahora, mexico, 6 * 60 + 30))
    }

    @Test
    fun `hora limite a medianoche`() {
        val ahora = ms(mexico, 2026, 11, 24, 23, 50)
        assertEquals(ms(mexico, 2026, 11, 25, 0, 0), CalculoAlarma.proximaHoraLimite(ahora, mexico, 0))
    }

    @Test
    fun `horario de verano la noche del cambio sigue sonando a las 7 locales`() {
        // En Nueva York, el 8 de marzo de 2026 a las 2:00 el reloj salta a las 3:00 (la noche dura 1 h menos).
        val nuevaYork = ZoneId.of("America/New_York")
        val ahora = ms(nuevaYork, 2026, 3, 7, 23, 0)
        val esperado = ms(nuevaYork, 2026, 3, 8, 7, 0)
        assertEquals(esperado, CalculoAlarma.proximaHoraLimite(ahora, nuevaYork, 7 * 60))
        // Son 7 h reales, no 8, entre las 23:00 y las 7:00 de esa noche.
        assertEquals(7 * 3_600_000L, esperado - ahora)
    }

    @Test
    fun `posponer suma los minutos`() {
        assertEquals(1_000L + 5 * 60_000L, CalculoAlarma.horaPosposicion(1_000L, 5))
    }
}
