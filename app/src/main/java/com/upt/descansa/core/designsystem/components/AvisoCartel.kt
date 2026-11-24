package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Aviso dentro de una tarjeta de contorno ("Si no detectamos sueño ligero,
 * sonará a la hora límite."). El ícono es de CONTROL/estado, no decorativo,
 * pero el texto ya lo explica: TalkBack lo omite.
 */
@Composable
fun AvisoCartel(
    texto: String,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Outlined.Info,
) {
    TarjetaCartel(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Espacio.l))
            Text(texto, style = TipoCartel.cuerpo, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@PreviewLightDark
@Composable
private fun AvisoCartelPreview() {
    VistaPrevia { AvisoCartel("Si no detectamos sueño ligero, sonará a la hora límite.") }
}
