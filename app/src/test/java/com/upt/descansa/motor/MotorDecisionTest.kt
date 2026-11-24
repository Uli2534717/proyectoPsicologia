package com.upt.descansa.motor

import com.upt.descansa.domain.model.MarcaEpoca
import com.upt.descansa.domain.model.Sensibilidad
import com.upt.descansa.domain.model.TipoSimulacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas del motor de decisión. Cada época es "un minuto": usamos índices
 * (0, 1, 2…) como marcas de tiempo para que se lean fácil.
 * Noche de 60 épocas, ventana de 20 → la ventana va de la 40 a la 59.
 */
class MotorDecisionTest {

    private val ventanaInicio = 40L
    private val horaLimite = 60L
    private val quieto = 0.0025
    private val movido = 0.015

    private fun motor(sensibilidad: Sensibilidad = Sensibilidad.MEDIA) =
        MotorDecision(sensibilidad, ventanaInicio, horaLimite)

    /** Procesa la noche y devuelve el índice donde decidió despertar (o null). */
    private fun MotorDecision.correr(actividad: (Int) -> Double): Int? {
        var despertoEn: Int? = null
        for (i in 0 until horaLimite.toInt()) {
            val r = procesar(EpocaCruda(i.toLong(), actividad(i)))
            if (r.decision == Decision.DESPERTAR) {
                assertEquals("solo debe despertar una vez", null, despertoEn)
                despertoEn = i
            }
        }
        return despertoEn
    }

    @Test
    fun `noche quieta no despierta antes de la hora limite`() {
        assertEquals(null, motor().correr { quieto })
    }

    @Test
    fun `mucho movimiento fuera de la ventana no despierta`() {
        assertEquals(null, motor().correr { i -> if (i in 20..30) movido else quieto })
    }

    @Test
    fun `sensibilidad media despierta con 2 minutos inquietos en la ventana`() {
        val despertoEn = motor().correr { i -> if (i >= 45) movido else quieto }
        assertEquals(46, despertoEn)
    }

    @Test
    fun `sensibilidad baja necesita 3 minutos inquietos seguidos`() {
        val despertoEn = motor(Sensibilidad.BAJA).correr { i -> if (i >= 45) movido else quieto }
        assertEquals(47, despertoEn)
    }

    @Test
    fun `sensibilidad alta despierta con el primer minuto inquieto`() {
        val despertoEn = motor(Sensibilidad.ALTA).correr { i -> if (i >= 45) movido else quieto }
        assertEquals(45, despertoEn)
    }

    @Test
    fun `una sola vuelta en la ventana no despierta con sensibilidad media`() {
        assertEquals(null, motor().correr { i -> if (i == 50) movido else quieto })
    }

    @Test
    fun `tomar el telefono dentro de la ventana despierta`() {
        val despertoEn = motor().correr { i -> if (i == 42) 0.3 else quieto }
        assertEquals(42, despertoEn)
    }

    @Test
    fun `la linea base se adapta a una persona que se mueve mucho`() {
        // Alguien que siempre se mueve "0.006": eso es SU normal, no sueño ligero.
        val motor = motor()
        val despertoEn = motor.correr { 0.006 }
        assertEquals(null, despertoEn)
        assertTrue(motor.epocas.drop(MotorDecision.MIN_EPOCAS_BASE).all { it.marca == MarcaEpoca.TRANQUILA })
    }

    @Test
    fun `telefono inmovil usa el piso y no marca todo como inquieto`() {
        val motor = motor()
        motor.correr { 0.0 }
        assertTrue(motor.epocas.all { it.marca == MarcaEpoca.TRANQUILA })
    }

    // --- Simulador: las dos noches de prueba dan el resultado esperado ---

    private fun simular(tipo: TipoSimulacion): Int? {
        val simulador = SimuladorNoche(tipo)
        return motor().correr { i -> simulador.actividad(i, enVentana = i >= ventanaInicio) }
    }

    @Test
    fun `simulador noche inquieta despierta dentro de la ventana`() {
        val despertoEn = simular(TipoSimulacion.INQUIETA)
        assertTrue("despertó en $despertoEn", despertoEn != null && despertoEn >= ventanaInicio)
    }

    @Test
    fun `simulador noche tranquila deja sonar la alarma de respaldo`() {
        assertEquals(null, simular(TipoSimulacion.TRANQUILA))
    }
}
