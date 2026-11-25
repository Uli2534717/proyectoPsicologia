package com.upt.descansa.feature.noche.alarma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import com.upt.descansa.domain.usecase.noche.ActivarModoNocheUseCase
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
 * FASE 1: "Activar modo noche" programa la alarma de respaldo y abre la noche.
 * En la Fase 2 también arrancará el servicio nocturno.
 */
@HiltViewModel
class AlarmaViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
    private val repositorio: AjustesRepository,
    private val activarModoNoche: ActivarModoNocheUseCase,
) : ViewModel() {

    /**
     * Programa la alarma y, cuando ya quedó guardada, llama a [onListo] (que navega).
     * Se espera a que termine para no mostrar "Modo noche activo" sin alarma real.
     */
    fun alActivarModoNoche(onListo: () -> Unit) {
        viewModelScope.launch {
            activarModoNoche()
            onListo()
        }
    }

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
