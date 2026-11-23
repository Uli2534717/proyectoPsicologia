package com.upt.dormirbien.feature.emociones

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "Emociones".
 * Responsable: Miguel Martínez Mendoza.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (emociones_*).
 */
@Composable
fun EmocionesScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.emociones_titulo),
        descripcion = stringResource(R.string.emociones_descripcion),
        modifier = modifier,
    )
}
