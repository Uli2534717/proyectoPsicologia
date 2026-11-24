package com.upt.descansa.domain.repository

/**
 * Contrato para programar la alarma de respaldo en el sistema.
 *
 * Vive en `domain` (sin Android) para que los casos de uso se puedan probar con
 * un programador falso. La implementación real usa `AlarmManager.setAlarmClock()`
 * (ver `sistema/alarma/AlarmSchedulerImpl`).
 */
interface ProgramadorAlarma {

    /** Programa (o reemplaza) la alarma para que suene en [disparoEn] (epoch ms, UTC). */
    fun programar(disparoEn: Long)

    /** Cancela la alarma programada, si hay una. */
    fun cancelar()
}
