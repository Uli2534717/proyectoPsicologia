package com.upt.dormirbien.feature.sonidos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "Sonidos".
 * Responsable: Ángel Ulises Ramos Vera.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (sonidos_*).
 */
@Composable
fun SonidosScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.sonidos_titulo),
        descripcion = stringResource(R.string.sonidos_descripcion),
        modifier = modifier,
    )
}
