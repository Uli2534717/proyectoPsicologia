package com.upt.descansa.motor

import com.upt.descansa.domain.model.TipoSimulacion
import kotlin.random.Random

/**
 * Genera la actividad de una noche falsa para probar el motor sin dormir
 * (el emulador no tiene un acelerómetro que se mueva).
 *
 * - TRANQUILA: ruido pequeño toda la noche, con alguna vuelta aislada.
 * - INQUIETA: igual, pero dentro de la ventana hay varios minutos con mucho
 *   movimiento seguidos (como alguien en sueño ligero).
 *
 * Se usa con semilla fija para que las pruebas den siempre lo mismo.
 */
class SimuladorNoche(
    private val tipo: TipoSimulacion,
    private val random: Random = Random(42),
) {
    /** Actividad (g) de la época número [indice]. [enVentana]: si ya cayó dentro de la ventana. */
    fun actividad(indice: Int, enVentana: Boolean): Double {
        val ruido = 0.002 + random.nextDouble() * 0.001
        // Una vuelta aislada cada 12 minutos: nunca dos seguidas, así que no despierta.
        val vueltaAislada = indice % 12 == 6
        return when {
            tipo == TipoSimulacion.INQUIETA && enVentana -> 0.012 + random.nextDouble() * 0.004
            vueltaAislada -> 0.008
            else -> ruido
        }
    }
}
