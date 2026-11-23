package com.upt.dormirbien.feature.alarma

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "Alarma".
 * Responsable: Rodrigo Ortiz Leal.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (alarma_*).
 */
@Composable
fun AlarmaScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.alarma_titulo),
        descripcion = stringResource(R.string.alarma_descripcion),
        modifier = modifier,
    )
}
