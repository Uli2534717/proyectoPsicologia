package com.upt.descansa.domain.repository

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.Disparo
import com.upt.descansa.domain.model.Epoca
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.model.NocheActiva

/** Contrato para guardar las sesiones de sueño (tablas `noches` y `epocas`). */
interface NocheRepository {

    /**
     * Abre una noche nueva. Si había otra abierta (por ejemplo, la app se cerró
     * a la fuerza), se cierra primero: solo puede haber una noche en curso.
     * @return el id de la noche creada.
     */
    suspend fun iniciar(iniciadaEn: Long, horaLimiteEn: Long, ajustes: AjustesUsuario): Long

    /** La noche en curso, o null si no hay ninguna. */
    suspend fun nocheActiva(): NocheActiva?

    /** Guarda un minuto ya clasificado de la noche [nocheId]. */
    suspend fun guardarEpoca(nocheId: Long, epoca: Epoca)

    /** Anota en la noche en curso que la alarma sonó, y por qué (sin cerrarla todavía). */
    suspend fun registrarDisparo(disparo: Disparo)

    /** El disparo anotado en la noche en curso, si ya sonó. */
    suspend fun disparoRegistrado(): Disparo?

    /** Cierra la noche en curso, si existe. Si no hay ninguna, no hace nada. */
    suspend fun terminarActiva(terminadaEn: Long, motivo: MotivoDisparo, disparadaEn: Long?)
}
