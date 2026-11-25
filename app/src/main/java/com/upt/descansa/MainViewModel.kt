package com.upt.descansa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado de arranque de la app. */
sealed interface EstadoArranque {
    /** Todavía se están leyendo los ajustes (unos milisegundos). */
    data object Cargando : EstadoArranque

    /**
     * Ya se sabe con qué pantalla abrir.
     * @property nocheEnCurso hay una noche activa: se abre el modo noche.
     */
    data class Listo(
        val bienvenidaCompletada: Boolean,
        val nocheEnCurso: Boolean = false,
    ) : EstadoArranque
}

/**
 * Decide con qué pantalla abre la app. Se lee UNA sola vez (con `first()`):
 * si se observara siempre, al pulsar "Comenzar" el NavHost se reconstruiría.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
    nocheRepositorio: NocheRepository,
) : ViewModel() {

    val arranque: StateFlow<EstadoArranque> = flow<EstadoArranque> {
        emit(
            EstadoArranque.Listo(
                bienvenidaCompletada = observarAjustes().first().bienvenidaCompletada,
                nocheEnCurso = nocheRepositorio.nocheActiva() != null,
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadoArranque.Cargando)
}
