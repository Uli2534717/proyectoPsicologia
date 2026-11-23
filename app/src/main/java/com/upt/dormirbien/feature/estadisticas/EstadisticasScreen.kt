package com.upt.dormirbien.feature.estadisticas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "Estadisticas".
 * Responsable: Miguel Martínez Mendoza.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (estadisticas_*).
 */
@Composable
fun EstadisticasScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.estadisticas_titulo),
        descripcion = stringResource(R.string.estadisticas_descripcion),
        modifier = modifier,
    )
}
