package com.upt.descansa.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/** Pruebas de las utilidades de hora, incluido el caso límite de la medianoche (sección 13). */
class TiempoTest {

    @Test
    fun `formatea con dos digitos`() {
        assertEquals("07:00", formatearHora(7 * 60))
        assertEquals("00:05", formatearHora(5))
        assertEquals("23:59", formatearHora(23 * 60 + 59))
    }

    @Test
    fun `valores fuera del dia se ajustan`() {
        assertEquals("00:00", formatearHora(MINUTOS_POR_DIA))
        assertEquals("23:50", formatearHora(-10))
    }

    @Test
    fun `inicio de ventana normal`() {
        assertEquals(6 * 60 + 30, inicioVentana(horaLimiteMinutos = 7 * 60, ventanaMinutos = 30))
    }

    @Test
    fun `ventana que cruza la medianoche`() {
        // Hora límite 00:10 con ventana de 30 min: puede sonar desde las 23:40.
        assertEquals(23 * 60 + 40, inicioVentana(horaLimiteMinutos = 10, ventanaMinutos = 30))
    }

    @Test
    fun `ventana de cero minutos es una alarma normal`() {
        assertEquals(7 * 60, inicioVentana(horaLimiteMinutos = 7 * 60, ventanaMinutos = 0))
    }

    // --- proximaOcurrencia: a qué fecha y hora se programa la alarma ---

    private val zona = ZoneId.of("America/Mexico_City")

    @Test
    fun `de noche la alarma queda para el dia siguiente`() {
        val ahora = ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona)
        assertEquals(ZonedDateTime.of(2026, 11, 24, 7, 0, 0, 0, zona), proximaOcurrencia(ahora, 7 * 60))
    }

    @Test
    fun `de madrugada la alarma queda para el mismo dia`() {
        val ahora = ZonedDateTime.of(2026, 11, 24, 2, 30, 0, 0, zona)
        assertEquals(ZonedDateTime.of(2026, 11, 24, 7, 0, 0, 0, zona), proximaOcurrencia(ahora, 7 * 60))
    }

    @Test
    fun `justo a la hora limite queda para manana`() {
        val ahora = ZonedDateTime.of(2026, 11, 24, 7, 0, 0, 0, zona)
        assertEquals(ZonedDateTime.of(2026, 11, 25, 7, 0, 0, 0, zona), proximaOcurrencia(ahora, 7 * 60))
    }

    @Test
    fun `hora inexistente por cambio de horario se recorre`() {
        // En Nueva York, el 8 de marzo de 2026 el reloj salta de 02:00 a 03:00.
        val nuevaYork = ZoneId.of("America/New_York")
        val ahora = ZonedDateTime.of(2026, 3, 7, 23, 0, 0, 0, nuevaYork)
        val alarma = proximaOcurrencia(ahora, 2 * 60 + 30)
        assertEquals(ZonedDateTime.of(2026, 3, 8, 3, 30, 0, 0, nuevaYork), alarma)
    }
}
