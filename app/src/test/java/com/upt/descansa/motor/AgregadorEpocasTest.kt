package com.upt.descansa.motor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AgregadorEpocasTest {

    @Test
    fun `desviacion estandar conocida`() {
        assertEquals(2.0, desviacionEstandar(listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)), 1e-9)
        assertEquals(0.0, desviacionEstandar(emptyList()), 0.0)
    }

    @Test
    fun `telefono quieto da actividad cero y la epoca se cierra al cambiar de minuto`() {
        val agregador = AgregadorEpocas()
        // 30 lecturas en el minuto 0 con el teléfono boca arriba (solo gravedad en z).
        for (i in 0 until 30) assertNull(agregador.agregar(i * 2_000L, 0f, 0f, 9.80665f))
        val epoca = agregador.agregar(60_500L, 0f, 0f, 9.80665f)
        assertNotNull(epoca)
        assertEquals(0L, epoca!!.minutoTs)
        assertEquals(0.0, epoca.actividad, 1e-9)
    }

    @Test
    fun `la orientacion no importa, solo el cambio de magnitud`() {
        val agregador = AgregadorEpocas()
        // Teléfono de lado: la gravedad está en x, pero la magnitud sigue siendo 1 g.
        for (i in 0 until 30) agregador.agregar(i * 1_000L, 9.80665f, 0f, 0f)
        assertEquals(0.0, agregador.agregar(60_000L, 0f, 0f, 9.80665f)!!.actividad, 1e-6)
    }

    @Test
    fun `movimiento sube la actividad`() {
        val agregador = AgregadorEpocas()
        for (i in 0 until 30) {
            val z = if (i % 2 == 0) 9.0f else 10.6f
            agregador.agregar(i * 1_000L, 0f, 0f, z)
        }
        val epoca = agregador.agregar(60_000L, 0f, 0f, 9.8f)!!
        assertEquals(0.0816, epoca.actividad, 0.001)
    }

    @Test
    fun `con muy pocas lecturas la epoca se descarta`() {
        val agregador = AgregadorEpocas()
        agregador.agregar(0L, 0f, 0f, 9.8f)
        assertNull(agregador.agregar(60_000L, 0f, 0f, 9.8f))
    }
}
