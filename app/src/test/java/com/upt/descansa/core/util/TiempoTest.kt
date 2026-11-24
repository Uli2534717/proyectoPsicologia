package com.upt.descansa.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

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
}
