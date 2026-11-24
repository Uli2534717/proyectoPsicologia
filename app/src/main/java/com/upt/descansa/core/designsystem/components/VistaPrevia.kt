package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio

/**
 * Marco para los `@Preview` de los componentes. Con `@PreviewLightDark`,
 * Android Studio dibuja cada componente dos veces: en tema claro y en nocturno.
 */
@Composable
internal fun VistaPrevia(content: @Composable () -> Unit) {
    DescansaTheme(nocturno = isSystemInDarkTheme()) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(Espacio.xl),
        ) { content() }
    }
}
