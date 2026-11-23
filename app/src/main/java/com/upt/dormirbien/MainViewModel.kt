package com.upt.dormirbien

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.dormirbien.domain.model.ModoTema
import com.upt.dormirbien.domain.usecase.preferencias.ObservarPreferenciasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel de la Activity principal. Le dice a toda la app qué tema usar.
 * Cuando el usuario cambia el tema en Ajustes, este flujo emite el nuevo
 * valor y la interfaz se redibuja sola con los colores nuevos.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    observarPreferencias: ObservarPreferenciasUseCase,
) : ViewModel() {

    val modoTema: StateFlow<ModoTema> = observarPreferencias()
        .map { it.modoTema }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ModoTema.OSCURO,
        )
}
