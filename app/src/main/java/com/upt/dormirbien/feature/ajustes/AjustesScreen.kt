package com.upt.dormirbien.feature.ajustes

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.dormirbien.R
import com.upt.dormirbien.core.theme.DormirBienTheme
import com.upt.dormirbien.domain.model.ModoTema

/**
 * Pantalla de Ajustes "con estado": obtiene su ViewModel y le pasa el estado
 * a [AjustesContenido]. Separarlas permite previsualizar y probar el contenido
 * sin Hilt ni DataStore.
 */
@Composable
fun AjustesScreen(
    modifier: Modifier = Modifier,
    viewModel: AjustesViewModel = hiltViewModel(),
) {
    // collectAsStateWithLifecycle: deja de escuchar cuando la app está en segundo plano.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AjustesContenido(
        uiState = uiState,
        onSeleccionarModoTema = viewModel::alSeleccionarModoTema,
        modifier = modifier,
    )
}

/** Texto visible de cada opción de tema. */
@StringRes
private fun ModoTema.etiqueta(): Int = when (this) {
    ModoTema.OSCURO -> R.string.ajustes_tema_oscuro
    ModoTema.CLARO -> R.string.ajustes_tema_claro
    ModoTema.SISTEMA -> R.string.ajustes_tema_sistema
}

/** Pantalla de Ajustes "sin estado": solo dibuja lo que recibe. */
@Composable
fun AjustesContenido(
    uiState: AjustesUiState,
    onSeleccionarModoTema: (ModoTema) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.ajustes_titulo),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.ajustes_tema),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { heading() },
        )
        // selectableGroup: TalkBack anuncia "1 de 3", "2 de 3"... como un grupo de radio.
        Column(Modifier.selectableGroup()) {
            ModoTema.entries.forEach { modo ->
                val seleccionado = uiState.modoTema == modo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Toda la fila es tocable (área grande = más accesible).
                        .selectable(
                            selected = seleccionado,
                            onClick = { onSeleccionarModoTema(modo) },
                            role = Role.RadioButton,
                        )
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // onClick = null: el clic lo maneja la fila completa.
                    RadioButton(selected = seleccionado, onClick = null)
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(modo.etiqueta()),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E1726)
@Composable
private fun AjustesContenidoPreview() {
    DormirBienTheme {
        AjustesContenido(uiState = AjustesUiState(), onSeleccionarModoTema = {})
    }
}
