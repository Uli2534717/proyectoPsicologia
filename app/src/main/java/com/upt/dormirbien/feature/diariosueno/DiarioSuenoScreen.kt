package com.upt.dormirbien.feature.diariosueno

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "DiarioSueno".
 * Responsable: por asignar.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (diario_sueno_*).
 */
@Composable
fun DiarioSuenoScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.diario_sueno_titulo),
        descripcion = stringResource(R.string.diario_sueno_descripcion),
        modifier = modifier,
    )
}
