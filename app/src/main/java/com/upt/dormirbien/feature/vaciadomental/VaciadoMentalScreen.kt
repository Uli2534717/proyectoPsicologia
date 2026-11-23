package com.upt.dormirbien.feature.vaciadomental

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "VaciadoMental".
 * Responsable: Sebastián Muñoz Ríos.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (vaciado_*).
 */
@Composable
fun VaciadoMentalScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.vaciado_titulo),
        descripcion = stringResource(R.string.vaciado_descripcion),
        modifier = modifier,
    )
}
