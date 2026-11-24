package com.upt.descansa.domain.usecase.noche

import com.upt.descansa.core.util.proximaOcurrencia
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

// =============================================================================
//  Casos de uso de la noche (Fase 1: alarma de respaldo).
//  Reciben la hora "ahora" como parámetro para que las pruebas no dependan
//  del reloj real de la computadora.
// =============================================================================

/**
 * "Activar modo noche": programa la alarma de respaldo a la hora límite y abre
 * la noche en la base de datos.
 *
 * @return la hora a la que sonará la alarma (epoch ms).
 */
class ActivarModoNocheUseCase @Inject constructor(
    private val ajustesRepositorio: AjustesRepository,
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
) {
    suspend operator fun invoke(ahora: ZonedDateTime = ZonedDateTime.now()): Long {
        val ajustes = ajustesRepositorio.ajustes.first()
        val horaLimiteEn = proximaOcurrencia(ahora, ajustes.horaLimiteMinutos).toInstant().toEpochMilli()
        // Primero la alarma: si después fallara la base de datos, la alarma ya está
        // puesta (principio "la alarma nunca falla").
        programador.programar(horaLimiteEn)
        nocheRepositorio.iniciar(ahora.toInstant().toEpochMilli(), horaLimiteEn, ajustes)
        return horaLimiteEn
    }
}

/** "Detener" en el modo noche: cancela la alarma y cierra la noche sin que haya sonado. */
class DetenerModoNocheUseCase @Inject constructor(
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
) {
    suspend operator fun invoke(ahora: Long = System.currentTimeMillis()) {
        programador.cancelar()
        nocheRepositorio.terminarActiva(ahora, MotivoDisparo.DETENIDA_MANUALMENTE, disparadaEn = null)
    }
}

/**
 * "Apagar" en la pantalla de timbre: la noche termina.
 * En la Fase 1 la alarma solo suena a la hora límite, por eso el motivo es
 * siempre [MotivoDisparo.HORA_LIMITE]. La Fase 2 agregará [MotivoDisparo.SUENO_LIGERO].
 *
 * @param disparadaEn cuándo empezó a sonar (no cuándo se apagó).
 */
class ApagarAlarmaUseCase @Inject constructor(
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
) {
    suspend operator fun invoke(disparadaEn: Long, ahora: Long = System.currentTimeMillis()) {
        programador.cancelar()
        nocheRepositorio.terminarActiva(ahora, MotivoDisparo.HORA_LIMITE, disparadaEn)
    }
}

/** "Posponer": vuelve a programar la alarma dentro de los minutos elegidos en Ajustes. */
class PosponerAlarmaUseCase @Inject constructor(
    private val ajustesRepositorio: AjustesRepository,
    private val programador: ProgramadorAlarma,
) {
    /** @return la nueva hora de la alarma (epoch ms). */
    suspend operator fun invoke(ahora: Long = System.currentTimeMillis()): Long {
        val minutos = ajustesRepositorio.ajustes.first().posponerMinutos
        val nuevaHora = ahora + minutos * 60_000L
        programador.programar(nuevaHora)
        return nuevaHora
    }
}
