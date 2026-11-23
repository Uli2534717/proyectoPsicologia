package com.upt.dormirbien.feature.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.dormirbien.domain.model.ModoTema
import com.upt.dormirbien.domain.usecase.preferencias.CambiarModoTemaUseCase
import com.upt.dormirbien.domain.usecase.preferencias.ObservarPreferenciasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Todo lo que la pantalla de Ajustes necesita para dibujarse. */
data class AjustesUiState(
    val modoTema: ModoTema = ModoTema.OSCURO,
)

/**
 * ViewModel de Ajustes (patrón MVVM).
 *
 * - Sobrevive a la rotación de pantalla (la Activity se recrea, él no).
 * - Expone un único `StateFlow<AjustesUiState>`: la pantalla solo lee ese
 *   estado y llama funciones; nunca toca DataStore directamente.
 *
 * `@HiltViewModel` + `@Inject constructor`: Hilt le pasa los casos de uso solo.
 *
 * Fase 1: únicamente el tema. Sebastián completa Ajustes en su fase.
 */
@HiltViewModel
class AjustesViewModel @Inject constructor(
    observarPreferencias: ObservarPreferenciasUseCase,
    private val cambiarModoTema: CambiarModoTemaUseCase,
) : ViewModel() {

    val uiState: StateFlow<AjustesUiState> = observarPreferencias()
        .map { prefs -> AjustesUiState(modoTema = prefs.modoTema) }
        .stateIn(
            scope = viewModelScope,
            // Deja de escuchar 5 s después de que la pantalla deja de verse
            // (así una rotación no reinicia la lectura).
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AjustesUiState(),
        )

    fun alSeleccionarModoTema(modo: ModoTema) {
        viewModelScope.launch { cambiarModoTema(modo) }
    }
}
