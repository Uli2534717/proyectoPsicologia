package com.upt.descansa.domain.repository

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.MotivoDisparo

/**
 * Contrato para guardar las sesiones de sueño (tabla `noches`).
 * En la Fase 1 solo se abre y se cierra la noche; la Fase 2 agregará las épocas.
 */
interface NocheRepository {

    /**
     * Abre una noche nueva. Si había otra abierta (por ejemplo, la app se cerró
     * a la fuerza), se cierra primero: solo puede haber una noche en curso.
     * @return el id de la noche creada.
     */
    suspend fun iniciar(iniciadaEn: Long, horaLimiteEn: Long, ajustes: AjustesUsuario): Long

    /** Cierra la noche en curso, si existe. Si no hay ninguna, no hace nada. */
    suspend fun terminarActiva(terminadaEn: Long, motivo: MotivoDisparo, disparadaEn: Long?)
}
