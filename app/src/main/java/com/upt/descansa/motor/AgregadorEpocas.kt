package com.upt.descansa.motor

import kotlin.math.sqrt

/** Gravedad estándar (m/s²): para expresar la aceleración en "g". */
private const val G = 9.80665

/** Mínimo de muestras para que una época cuente (a 5 Hz, un minuto trae ~300). */
private const val MIN_MUESTRAS = 10

/** Una época recién medida, todavía sin clasificar. */
data class EpocaCruda(
    val minutoTs: Long,
    val actividad: Double,
)

/**
 * EpochAggregator: junta las lecturas del acelerómetro de cada minuto y
 * calcula su "actividad".
 *
 * Por qué la desviación estándar de |a|/g: la magnitud |a| no depende de cómo
 * esté acostado el teléfono (siempre incluye la gravedad, ~1 g). Si nadie se
 * mueve, |a| casi no cambia y la desviación es ~0; cada vuelta en la cama la sube.
 *
 * No sabe nada de Android: recibe números, así que se prueba con JUnit.
 */
class AgregadorEpocas(private val duracionEpocaMs: Long = 60_000L) {

    private var inicioEpoca: Long? = null
    private val magnitudes = ArrayList<Double>()

    /**
     * Agrega una lectura (x, y, z en m/s²) tomada en [t] (epoch ms).
     * @return la época ANTERIOR cuando esta lectura ya pertenece a una nueva; si no, null.
     */
    fun agregar(t: Long, x: Float, y: Float, z: Float): EpocaCruda? {
        // Se alinea al inicio del minuto: 06:31:47 → 06:31:00.
        val inicio = t - Math.floorMod(t, duracionEpocaMs)
        val actual = inicioEpoca
        val cerrada = if (actual != null && inicio != actual) cerrar(actual) else null
        inicioEpoca = inicio
        magnitudes += sqrt((x * x + y * y + z * z).toDouble()) / G
        return cerrada
    }

    private fun cerrar(inicio: Long): EpocaCruda? {
        // Pocas muestras (el sensor se pausó): mejor no inventar una actividad.
        val epoca = if (magnitudes.size >= MIN_MUESTRAS) EpocaCruda(inicio, desviacionEstandar(magnitudes)) else null
        magnitudes.clear()
        return epoca
    }
}

/** Desviación estándar poblacional. Lista vacía → 0. */
fun desviacionEstandar(valores: List<Double>): Double {
    if (valores.isEmpty()) return 0.0
    val media = valores.average()
    return sqrt(valores.sumOf { (it - media) * (it - media) } / valores.size)
}
