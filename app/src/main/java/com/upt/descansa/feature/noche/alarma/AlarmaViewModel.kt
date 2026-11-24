package com.upt.descansa.feature.noche.alarma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Lo que dibuja la pantalla de Alarma. */
data class AlarmaUiState(
    val horaLimiteMinutos: Int = AjustesUsuario().horaLimiteMinutos,
    val ventanaMinutos: Int = AjustesUsuario().ventanaMinutos,
    val fuente: FuenteDatos = AjustesUsuario().fuente,
    val noMolestar: Boolean = AjustesUsuario().noMolestarActivado,
)

/**
 * ViewModel de la configuración de la alarma (paso 5 de 5).
 *
 * Cada cambio se guarda de inmediato en los ajustes: la próxima noche la
 * pantalla ya aparece con lo último que eligió el usuario.
 * FASE 0: "Activar modo noche" solo navega. En la Fase 1 programará la alarma
 * de respaldo y en la Fase 2 arrancará el servicio nocturno.
 */
@HiltViewModel
class AlarmaViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
    private val repositorio: AjustesRepository,
) : ViewModel() {

    val uiState: StateFlow<AlarmaUiState> = observarAjustes()
        .map { AlarmaUiState(it.horaLimiteMinutos, it.ventanaMinutos, it.fuente, it.noMolestarActivado) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmaUiState())

    fun alCambiarHoraLimite(minutosDelDia: Int) = guardar { it.copy(horaLimiteMinutos = minutosDelDia) }

    fun alCambiarVentana(minutos: Int) = guardar { it.copy(ventanaMinutos = minutos) }

    fun alCambiarFuente(fuente: FuenteDatos) = guardar { it.copy(fuente = fuente) }

    fun alCambiarNoMolestar(activado: Boolean) = guardar { it.copy(noMolestarActivado = activado) }

    private fun guardar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        viewModelScope.launch { repositorio.actualizar(cambio) }
    }
}
