package com.upt.descansa.feature.noche.alarma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.domain.alarma.AlarmaProgramada
import com.upt.descansa.domain.alarma.AlarmaRespaldo
import com.upt.descansa.domain.alarma.CalculoAlarma
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

/** Lo que dibuja la pantalla de Alarma. */
data class AlarmaUiState(
    val horaLimiteMinutos: Int = AjustesUsuario().horaLimiteMinutos,
    val ventanaMinutos: Int = AjustesUsuario().ventanaMinutos,
    val fuente: FuenteDatos = AjustesUsuario().fuente,
    val noMolestar: Boolean = AjustesUsuario().noMolestarActivado,
)

/** Resultado de pulsar "Activar modo noche". */
sealed interface ResultadoActivacion {
    data class Programada(val disparoEnMs: Long) : ResultadoActivacion

    /** Android no deja programar alarmas exactas: NO se activa la noche sin respaldo. */
    data object SinPermisoExactas : ResultadoActivacion
}

/**
 * ViewModel de la configuración de la alarma (paso 5 de 5).
 *
 * Cada cambio se guarda de inmediato en los ajustes. "Activar modo noche"
 * programa la alarma de respaldo (Fase 1); la Fase 2 agregará el servicio
 * nocturno y el motor de decisión.
 *
 * @param ahora reloj inyectable para las pruebas.
 */
@HiltViewModel
class AlarmaViewModel(
    observarAjustes: ObservarAjustesUseCase,
    private val repositorio: AjustesRepository,
    private val alarmaRespaldo: AlarmaRespaldo,
    private val ahora: () -> Long,
    private val zona: () -> ZoneId,
) : ViewModel() {

    @Inject
    constructor(
        observarAjustes: ObservarAjustesUseCase,
        repositorio: AjustesRepository,
        alarmaRespaldo: AlarmaRespaldo,
    ) : this(observarAjustes, repositorio, alarmaRespaldo, System::currentTimeMillis, ZoneId::systemDefault)

    val uiState: StateFlow<AlarmaUiState> = observarAjustes()
        .map { AlarmaUiState(it.horaLimiteMinutos, it.ventanaMinutos, it.fuente, it.noMolestarActivado) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmaUiState())

    fun alCambiarHoraLimite(minutosDelDia: Int) = guardar { it.copy(horaLimiteMinutos = minutosDelDia) }

    fun alCambiarVentana(minutos: Int) = guardar { it.copy(ventanaMinutos = minutos) }

    fun alCambiarFuente(fuente: FuenteDatos) = guardar { it.copy(fuente = fuente) }

    fun alCambiarNoMolestar(activado: Boolean) = guardar { it.copy(noMolestarActivado = activado) }

    /**
     * Programa la alarma de respaldo a la próxima hora límite.
     * Si ya había una sesión, la reemplaza (solo puede haber una: sección 13).
     */
    suspend fun activarModoNoche(): ResultadoActivacion {
        if (!alarmaRespaldo.puedeProgramarExactas()) return ResultadoActivacion.SinPermisoExactas
        val ajustes = repositorio.ajustes.first()
        val disparo = CalculoAlarma.proximaHoraLimite(ahora(), zona(), ajustes.horaLimiteMinutos)
        alarmaRespaldo.programar(
            AlarmaProgramada(
                disparoEnMs = disparo,
                horaLimiteMinutos = ajustes.horaLimiteMinutos,
                posponerMinutos = ajustes.posponerMinutos,
            ),
        )
        return ResultadoActivacion.Programada(disparo)
    }

    private fun guardar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        viewModelScope.launch { repositorio.actualizar(cambio) }
    }
}
