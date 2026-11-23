package com.upt.dormirbien.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upt.dormirbien.R
import com.upt.dormirbien.core.designsystem.PantallaEnConstruccion

/**
 * Pantalla del módulo "Onboarding".
 * Responsable: Sebastián Muñoz Ríos.
 *
 * TEMPORAL: muestra [PantallaEnConstruccion] para que la navegación funcione
 * desde la Fase 1. Reemplázala por la pantalla real (y su ViewModel) en la
 * fase de este módulo. Los textos van en strings.xml (onboarding_*).
 */
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    PantallaEnConstruccion(
        titulo = stringResource(R.string.onboarding_titulo),
        descripcion = stringResource(R.string.onboarding_descripcion),
        modifier = modifier,
    )
}
