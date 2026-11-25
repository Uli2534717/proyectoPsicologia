package com.upt.descansa.domain.repository

/**
 * Contrato para programar la alarma en el sistema.
 *
 * Vive en `domain` (sin Android) para que los casos de uso se puedan probar con
 * un programador falso. La implementación real usa `AlarmManager.setAlarmClock()`
 * (ver `sistema/alarma/AlarmSchedulerImpl`).
 */
interface ProgramadorAlarma {

    /** Programa (o reemplaza) la alarma de respaldo para [disparoEn] (epoch ms, UTC). */
    fun programar(disparoEn: Long)

    /**
     * Hace sonar la alarma ya (la decidió el motor por sueño ligero).
     * NO toca la alarma de respaldo: si esta fallara, la de la hora límite sigue en pie.
     */
    fun dispararAhora()

    /** Cancela la alarma de respaldo y la inteligente, si hay. */
    fun cancelar()
}
