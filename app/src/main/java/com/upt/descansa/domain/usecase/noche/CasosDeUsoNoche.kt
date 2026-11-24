package com.upt.descansa.domain.usecase.noche

import com.upt.descansa.core.util.proximaOcurrencia
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.model.TipoSimulacion
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.repository.ControlNoMolestar
import com.upt.descansa.domain.repository.ControlServicioNocturno
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

// =============================================================================
//  Casos de uso de la noche (Fases 1 a 3).
//  Reciben la hora "ahora" como parámetro para que las pruebas no dependan
//  del reloj real de la computadora.
// =============================================================================

/**
 * "Activar modo noche": programa la alarma de respaldo a la hora límite, abre
 * la noche en la base de datos y arranca el servicio que mide el movimiento.
 *
 * @return la hora a la que sonará la alarma de respaldo (epoch ms).
 */
class ActivarModoNocheUseCase @Inject constructor(
    private val ajustesRepositorio: AjustesRepository,
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
    private val servicio: ControlServicioNocturno,
    private val noMolestar: ControlNoMolestar,
) {
    suspend operator fun invoke(ahora: ZonedDateTime = ZonedDateTime.now()): Long {
        val ajustes = ajustesRepositorio.ajustes.first()
        val horaLimiteEn = proximaOcurrencia(ahora, ajustes.horaLimiteMinutos).toInstant().toEpochMilli()
        // Primero la alarma: si después fallara la base de datos o el servicio, la
        // alarma ya está puesta (principio "la alarma nunca falla").
        programador.programar(horaLimiteEn)
        nocheRepositorio.iniciar(ahora.toInstant().toEpochMilli(), horaLimiteEn, ajustes)
        servicio.iniciar()
        // Sin permiso devuelve false y la noche sigue igual (el modo noche lo avisa).
        if (ajustes.noMolestarActivado) noMolestar.activar()
        return horaLimiteEn
    }
}

/**
 * Modo desarrollador: una noche de 60 "minutos" que dura 60 segundos, con
 * ventana de 20. Prueba el motor y las dos alarmas en un minuto (D-12).
 */
class IniciarSimulacionUseCase @Inject constructor(
    private val ajustesRepositorio: AjustesRepository,
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
    private val servicio: ControlServicioNocturno,
    private val noMolestar: ControlNoMolestar,
) {
    suspend operator fun invoke(tipo: TipoSimulacion, ahora: Long = System.currentTimeMillis()) {
        val ajustes = ajustesRepositorio.ajustes.first().copy(ventanaMinutos = VENTANA_SIMULADA)
        val horaLimiteEn = ahora + DURACION_SIMULADA_MS
        programador.programar(horaLimiteEn)
        nocheRepositorio.iniciar(ahora, horaLimiteEn, ajustes)
        servicio.iniciar(tipo)
        if (ajustes.noMolestarActivado) noMolestar.activar()
    }

    companion object {
        const val DURACION_SIMULADA_MS = 60_000L
        const val VENTANA_SIMULADA = 20
    }
}

/** "Detener" en el modo noche: cancela la alarma y cierra la noche sin que haya sonado. */
class DetenerModoNocheUseCase @Inject constructor(
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
    private val servicio: ControlServicioNocturno,
    private val noMolestar: ControlNoMolestar,
) {
    suspend operator fun invoke(ahora: Long = System.currentTimeMillis()) {
        servicio.detener()
        noMolestar.restaurar()
        programador.cancelar()
        nocheRepositorio.terminarActiva(ahora, MotivoDisparo.DETENIDA_MANUALMENTE, disparadaEn = null)
    }
}

/**
 * "Apagar" en la pantalla de timbre: la noche termina.
 * Si el motor ya había anotado que sonó por sueño ligero, se conserva ese motivo;
 * si no, sonó la alarma de respaldo ([MotivoDisparo.HORA_LIMITE]).
 *
 * @param disparadaEn cuándo empezó a sonar (no cuándo se apagó).
 */
class ApagarAlarmaUseCase @Inject constructor(
    private val nocheRepositorio: NocheRepository,
    private val programador: ProgramadorAlarma,
    private val servicio: ControlServicioNocturno,
    private val noMolestar: ControlNoMolestar,
) {
    suspend operator fun invoke(disparadaEn: Long, ahora: Long = System.currentTimeMillis()) {
        servicio.detener()
        // Ya despertó: el teléfono vuelve a sonar como siempre.
        noMolestar.restaurar()
        programador.cancelar()
        val disparo = nocheRepositorio.disparoRegistrado()
        nocheRepositorio.terminarActiva(
            terminadaEn = ahora,
            motivo = disparo?.motivo ?: MotivoDisparo.HORA_LIMITE,
            disparadaEn = disparo?.en ?: disparadaEn,
        )
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
