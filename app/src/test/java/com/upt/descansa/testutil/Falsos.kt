package com.upt.descansa.testutil

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.Disparo
import com.upt.descansa.domain.model.Epoca
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.model.NocheActiva
import com.upt.descansa.domain.model.TipoSimulacion
import com.upt.descansa.domain.repository.ControlServicioNocturno
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma

/** Programador FALSO: en vez de hablar con AlarmManager, recuerda lo que se pidió. */
class ProgramadorAlarmaFalso : ProgramadorAlarma {
    var programadaEn: Long? = null
        private set
    var disparosInmediatos = 0
        private set

    override fun programar(disparoEn: Long) {
        programadaEn = disparoEn
    }

    override fun dispararAhora() {
        disparosInmediatos++
    }

    override fun cancelar() {
        programadaEn = null
    }
}

/** Servicio nocturno FALSO: solo anota si está corriendo y en qué modo. */
class ControlServicioNocturnoFalso : ControlServicioNocturno {
    var corriendo = false
        private set
    var simulacion: TipoSimulacion? = null
        private set

    override fun iniciar(simulacion: TipoSimulacion?) {
        corriendo = true
        this.simulacion = simulacion
    }

    override fun detener() {
        corriendo = false
    }
}

/** Una noche guardada por [NocheRepositoryFalso]. */
data class NocheFalsa(
    val iniciadaEn: Long,
    val horaLimiteEn: Long,
    val ventanaMinutos: Int,
    val terminadaEn: Long? = null,
    val motivo: MotivoDisparo = MotivoDisparo.NINGUNO,
    val disparadaEn: Long? = null,
    val epocas: List<Epoca> = emptyList(),
)

/** Repositorio de noches FALSO en memoria (misma regla que el real: una sola noche abierta). */
class NocheRepositoryFalso : NocheRepository {
    val noches = mutableListOf<NocheFalsa>()

    private fun indiceActiva() = noches.indexOfLast { it.terminadaEn == null }

    override suspend fun iniciar(iniciadaEn: Long, horaLimiteEn: Long, ajustes: AjustesUsuario): Long {
        terminarActiva(iniciadaEn, MotivoDisparo.NINGUNO, null)
        noches += NocheFalsa(iniciadaEn, horaLimiteEn, ajustes.ventanaMinutos)
        return noches.size.toLong()
    }

    override suspend fun nocheActiva(): NocheActiva? {
        val i = indiceActiva()
        if (i < 0) return null
        val n = noches[i]
        return NocheActiva(i + 1L, n.iniciadaEn, n.horaLimiteEn, n.ventanaMinutos)
    }

    override suspend fun guardarEpoca(nocheId: Long, epoca: Epoca) {
        val i = (nocheId - 1).toInt()
        noches[i] = noches[i].copy(epocas = noches[i].epocas + epoca)
    }

    override suspend fun registrarDisparo(disparo: Disparo) {
        val i = indiceActiva()
        if (i >= 0) noches[i] = noches[i].copy(motivo = disparo.motivo, disparadaEn = disparo.en)
    }

    override suspend fun disparoRegistrado(): Disparo? {
        val n = noches.getOrNull(indiceActiva()) ?: return null
        val en = n.disparadaEn ?: return null
        return if (n.motivo == MotivoDisparo.NINGUNO) null else Disparo(n.motivo, en)
    }

    override suspend fun terminarActiva(terminadaEn: Long, motivo: MotivoDisparo, disparadaEn: Long?) {
        val i = indiceActiva()
        if (i >= 0) noches[i] = noches[i].copy(terminadaEn = terminadaEn, motivo = motivo, disparadaEn = disparadaEn)
    }
}
