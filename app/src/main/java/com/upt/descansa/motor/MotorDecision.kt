package com.upt.descansa.motor

import com.upt.descansa.domain.model.Epoca
import com.upt.descansa.domain.model.MarcaEpoca
import com.upt.descansa.domain.model.Sensibilidad

/**
 * Qué tanto movimiento cuenta como "inquieto" y cuántos minutos inquietos hacen falta.
 *
 * @property factorInquieta una época es INQUIETA si su actividad supera la línea base × este factor.
 * @property requeridas cuántas épocas inquietas…
 * @property revisadas …entre las últimas N épocas dentro de la ventana.
 */
data class ParametrosSensibilidad(
    val factorInquieta: Double,
    val requeridas: Int,
    val revisadas: Int,
)

/** Tabla de sensibilidad (D-12). Más sensible = suena con menos movimiento. */
fun Sensibilidad.parametros(): ParametrosSensibilidad = when (this) {
    Sensibilidad.BAJA -> ParametrosSensibilidad(factorInquieta = 3.0, requeridas = 3, revisadas = 3)
    Sensibilidad.MEDIA -> ParametrosSensibilidad(factorInquieta = 2.0, requeridas = 2, revisadas = 3)
    Sensibilidad.ALTA -> ParametrosSensibilidad(factorInquieta = 1.5, requeridas = 1, revisadas = 2)
}

/** Lo que el motor decide tras cada época. */
enum class Decision {
    ESPERAR,
    DESPERTAR,
}

/** La época clasificada y la decisión tomada con ella. */
data class Resultado(val epoca: Epoca, val decision: Decision)

/**
 * WakeDecisionEngine: decide, minuto a minuto, si es buen momento para despertar.
 *
 * Reglas (todas aproximadas; no es un estudio clínico):
 * 1. Línea base = mediana de la actividad de la noche (sin contar cuando el
 *    teléfono fue tomado), con un piso para teléfonos muy quietos.
 * 2. Cada época se marca TRANQUILA, INQUIETA (actividad > base × factor) o
 *    TOMADO (movimiento enorme: alguien levantó el teléfono).
 * 3. Solo dentro de la ventana [ventanaInicioEn, horaLimiteEn): si hay
 *    suficientes épocas inquietas seguidas (según la sensibilidad), o si el
 *    teléfono fue tomado (la persona ya está despierta) → DESPERTAR.
 * 4. Decide DESPERTAR una sola vez. Fuera de la ventana nunca despierta: para
 *    eso está la alarma de respaldo a la hora límite.
 *
 * Es Kotlin puro: no conoce el reloj ni los sensores, solo los números que recibe.
 * Por eso funciona igual con minutos reales que con el simulador acelerado.
 */
class MotorDecision(
    sensibilidad: Sensibilidad,
    private val ventanaInicioEn: Long,
    private val horaLimiteEn: Long,
) {
    private val parametros = sensibilidad.parametros()
    private val historial = mutableListOf<Epoca>()
    private var yaDecidio = false

    /** Épocas procesadas hasta ahora, en orden. */
    val epocas: List<Epoca> get() = historial

    fun procesar(cruda: EpocaCruda): Resultado {
        val base = lineaBase()
        val marca = when {
            cruda.actividad >= UMBRAL_TOMADO -> MarcaEpoca.TOMADO
            cruda.actividad > base * parametros.factorInquieta -> MarcaEpoca.INQUIETA
            else -> MarcaEpoca.TRANQUILA
        }
        val epoca = Epoca(cruda.minutoTs, cruda.actividad, marca)
        historial += epoca
        return Resultado(epoca, decidir(epoca))
    }

    private fun decidir(epoca: Epoca): Decision {
        if (yaDecidio) return Decision.ESPERAR
        val enVentana = epoca.minutoTs >= ventanaInicioEn && epoca.minutoTs < horaLimiteEn
        if (!enVentana) return Decision.ESPERAR

        val recientes = historial.filter { it.minutoTs >= ventanaInicioEn }.takeLast(parametros.revisadas)
        val inquietas = recientes.count { it.marca == MarcaEpoca.INQUIETA }
        val despertar = epoca.marca == MarcaEpoca.TOMADO || inquietas >= parametros.requeridas
        if (despertar) yaDecidio = true
        return if (despertar) Decision.DESPERTAR else Decision.ESPERAR
    }

    /**
     * Mediana de la actividad sin las épocas TOMADO. La mediana (y no el promedio)
     * para que unas pocas vueltas grandes no suban la base de toda la noche.
     */
    private fun lineaBase(): Double {
        val valores = historial.filter { it.marca != MarcaEpoca.TOMADO }.map { it.actividad }.sorted()
        if (valores.size < MIN_EPOCAS_BASE) return PISO_ACTIVIDAD
        val mitad = valores.size / 2
        val mediana = if (valores.size % 2 == 1) valores[mitad] else (valores[mitad - 1] + valores[mitad]) / 2
        return mediana.coerceAtLeast(PISO_ACTIVIDAD)
    }

    companion object {
        /** Se guarda en cada noche (`version_motor`) para comparar calibraciones. */
        const val VERSION = 1

        /** Actividad mínima de la base: un teléfono inmóvil marca ~0 y todo parecería "inquieto". */
        const val PISO_ACTIVIDAD = 0.002

        /** Por encima de esto (en g) el teléfono fue tomado o sacudido. */
        const val UMBRAL_TOMADO = 0.1

        /** Antes de este número de épocas no hay base confiable y se usa el piso. */
        const val MIN_EPOCAS_BASE = 10
    }
}
