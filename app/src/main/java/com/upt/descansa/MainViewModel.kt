package com.upt.descansa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.domain.model.ModoTema
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado de arranque de la app. */
sealed interface EstadoArranque {
    /** Todavía se están leyendo los ajustes (unos milisegundos). */
    data object Cargando : EstadoArranque

    /** Ya se sabe si hay que mostrar la Bienvenida. */
    data class Listo(val bienvenidaCompletada: Boolean) : EstadoArranque
}

/**
 * Decide con qué pantalla abre la app y qué tema usa.
 * La pantalla inicial se lee UNA sola vez (con `first()`): si se observara
 * siempre, al pulsar "Comenzar" el NavHost se reconstruiría. El tema, en
 * cambio, sí se observa: al cambiarlo en Ajustes, la app cambia al momento.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
) : ViewModel() {

    val arranque: StateFlow<EstadoArranque> = flow<EstadoArranque> {
        emit(EstadoArranque.Listo(observarAjustes().first().bienvenidaCompletada))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadoArranque.Cargando)

    val modoTema: StateFlow<ModoTema> = observarAjustes()
        .map { it.modoTema }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ModoTema.AUTOMATICO)
}
