package com.upt.descansa.data.repository

import com.upt.descansa.data.local.dao.NocheDao
import com.upt.descansa.data.local.entity.NocheEntity
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.repository.NocheRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Versión del motor de decisión que se guarda en cada noche.
 * 0 = sin motor: en la Fase 1 solo existe la alarma de respaldo.
 */
private const val VERSION_MOTOR_SOLO_RESPALDO = 0

@Singleton
class NocheRepositoryImpl @Inject constructor(
    private val dao: NocheDao,
) : NocheRepository {

    override suspend fun iniciar(iniciadaEn: Long, horaLimiteEn: Long, ajustes: AjustesUsuario): Long {
        // Una noche que quedó abierta (app cerrada a la fuerza, batería agotada...)
        // se cierra aquí: así nunca hay dos noches "en curso".
        terminarActiva(iniciadaEn, MotivoDisparo.NINGUNO, disparadaEn = null)
        return dao.insertar(
            NocheEntity(
                iniciadaEn = iniciadaEn,
                horaLimiteEn = horaLimiteEn,
                ventanaMinutos = ajustes.ventanaMinutos,
                fuente = ajustes.fuente,
                noMolestarActivado = ajustes.noMolestarActivado,
                sonidoId = ajustes.sonidoFavoritoId,
                versionMotor = VERSION_MOTOR_SOLO_RESPALDO,
            ),
        )
    }

    override suspend fun terminarActiva(terminadaEn: Long, motivo: MotivoDisparo, disparadaEn: Long?) {
        val activa = dao.obtenerActiva() ?: return
        dao.actualizar(activa.copy(terminadaEn = terminadaEn, motivoDisparo = motivo, disparadaEn = disparadaEn))
    }
}
