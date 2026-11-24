package com.upt.descansa.domain.alarma

import kotlinx.coroutines.flow.Flow

/**
 * Estado de la alarma de respaldo que está programada en el sistema.
 *
 * @property disparoEnMs cuándo sonará (epoch ms, UTC).
 * @property horaLimiteMinutos hora límite elegida (minutos desde 00:00). Se
 *           guarda para reprogramar si cambia la zona horaria.
 * @property posponerMinutos duración de "Posponer" (se guarda aquí porque la
 *           pantalla de timbre no puede leer los ajustes con el teléfono bloqueado).
 * @property esPosposicion `true` si es una alarma pospuesta (hora fija, no se
 *           recalcula con la hora límite).
 */
data class AlarmaProgramada(
    val disparoEnMs: Long,
    val horaLimiteMinutos: Int,
    val posponerMinutos: Int,
    val esPosposicion: Boolean = false,
)

/**
 * Contrato de la alarma de respaldo (principio "la alarma nunca falla").
 * La implementación Android usa AlarmManager.setAlarmClock(); en las pruebas
 * se reemplaza por una falsa.
 */
interface AlarmaRespaldo {

    /** La alarma programada actualmente, o null si no hay. */
    val programada: Flow<AlarmaProgramada?>

    /** Programa (o reprograma) la alarma. Solo existe una a la vez. */
    fun programar(alarma: AlarmaProgramada)

    /** Cancela la alarma y borra su estado. */
    fun cancelar()

    /** `false` si el sistema no deja programar alarmas exactas (ver Ajustes de Android). */
    fun puedeProgramarExactas(): Boolean
}
