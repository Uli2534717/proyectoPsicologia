package com.upt.descansa.data.repository

import com.upt.descansa.data.local.dao.EpocaDao
import com.upt.descansa.data.local.dao.NocheDao
import com.upt.descansa.data.local.entity.EpocaEntity
import com.upt.descansa.data.local.entity.NocheEntity
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.Disparo
import com.upt.descansa.domain.model.Epoca
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.model.NocheActiva
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.motor.MotorDecision
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NocheRepositoryImpl @Inject constructor(
    private val nocheDao: NocheDao,
    private val epocaDao: EpocaDao,
) : NocheRepository {

    override suspend fun iniciar(iniciadaEn: Long, horaLimiteEn: Long, ajustes: AjustesUsuario): Long {
        // Una noche que quedó abierta (app cerrada a la fuerza, batería agotada...)
        // se cierra aquí: así nunca hay dos noches "en curso".
        terminarActiva(iniciadaEn, MotivoDisparo.NINGUNO, disparadaEn = null)
        return nocheDao.insertar(
            NocheEntity(
                iniciadaEn = iniciadaEn,
                horaLimiteEn = horaLimiteEn,
                ventanaMinutos = ajustes.ventanaMinutos,
                fuente = ajustes.fuente,
                noMolestarActivado = ajustes.noMolestarActivado,
                sonidoId = ajustes.sonidoFavoritoId,
                versionMotor = MotorDecision.VERSION,
            ),
        )
    }

    override suspend fun nocheActiva(): NocheActiva? =
        nocheDao.obtenerActiva()?.let { NocheActiva(it.id, it.iniciadaEn, it.horaLimiteEn, it.ventanaMinutos) }

    override suspend fun guardarEpoca(nocheId: Long, epoca: Epoca) {
        epocaDao.insertarTodas(listOf(EpocaEntity(nocheId, epoca.minutoTs, epoca.actividad, marca = epoca.marca)))
    }

    override suspend fun registrarDisparo(disparo: Disparo) {
        val activa = nocheDao.obtenerActiva() ?: return
        nocheDao.actualizar(activa.copy(motivoDisparo = disparo.motivo, disparadaEn = disparo.en))
    }

    override suspend fun disparoRegistrado(): Disparo? {
        val activa = nocheDao.obtenerActiva() ?: return null
        val en = activa.disparadaEn ?: return null
        return if (activa.motivoDisparo == MotivoDisparo.NINGUNO) null else Disparo(activa.motivoDisparo, en)
    }

    override suspend fun terminarActiva(terminadaEn: Long, motivo: MotivoDisparo, disparadaEn: Long?) {
        val activa = nocheDao.obtenerActiva() ?: return
        nocheDao.actualizar(activa.copy(terminadaEn = terminadaEn, motivoDisparo = motivo, disparadaEn = disparadaEn))
    }
}
