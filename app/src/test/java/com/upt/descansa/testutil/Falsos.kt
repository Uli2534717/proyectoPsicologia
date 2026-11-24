package com.upt.descansa.testutil

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma

/** Programador FALSO: en vez de hablar con AlarmManager, recuerda la hora pedida. */
class ProgramadorAlarmaFalso : ProgramadorAlarma {
    var programadaEn: Long? = null
        private set

    override fun programar(disparoEn: Long) {
        programadaEn = disparoEn
    }

    override fun cancelar() {
        programadaEn = null
    }
}

/** Una noche guardada por [NocheRepositoryFalso]. */
data class NocheFalsa(
    val iniciadaEn: Long,
    val horaLimiteEn: Long,
    val terminadaEn: Long? = null,
    val motivo: MotivoDisparo = MotivoDisparo.NINGUNO,
    val disparadaEn: Long? = null,
)

/** Repositorio de noches FALSO en memoria (misma regla que el real: una sola noche abierta). */
class NocheRepositoryFalso : NocheRepository {
    val noches = mutableListOf<NocheFalsa>()

    override suspend fun iniciar(iniciadaEn: Long, horaLimiteEn: Long, ajustes: AjustesUsuario): Long {
        terminarActiva(iniciadaEn, MotivoDisparo.NINGUNO, null)
        noches += NocheFalsa(iniciadaEn, horaLimiteEn)
        return noches.size.toLong()
    }

    override suspend fun terminarActiva(terminadaEn: Long, motivo: MotivoDisparo, disparadaEn: Long?) {
        val indice = noches.indexOfLast { it.terminadaEn == null }
        if (indice >= 0) noches[indice] = noches[indice].copy(terminadaEn = terminadaEn, motivo = motivo, disparadaEn = disparadaEn)
    }
}
