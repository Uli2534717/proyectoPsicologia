package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Título REAL de la pantalla (estilo `subtitulo`: 32 sp, peso 900) con una
 * descripción opcional. Es lo que TalkBack lee como encabezado; la
 * PalabraCartel encima es solo decoración.
 */
@Composable
fun TituloPantalla(texto: String, modifier: Modifier = Modifier, descripcion: String? = null) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Espacio.xs)) {
        Text(
            texto,
            style = TipoCartel.subtitulo,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        if (descripcion != null) {
            Text(descripcion, style = TipoCartel.cuerpo, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@PreviewLightDark
@Composable
private fun TituloPantallaPreview() {
    VistaPrevia { TituloPantalla("Respira con calma", descripcion = "Sigue el círculo.") }
}
