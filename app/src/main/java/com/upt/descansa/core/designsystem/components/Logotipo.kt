package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Logotipo de texto: "Descansa" en Work Sans 900 cursiva, color interactivo,
 * con el subtítulo SUEÑO · MENTE · CALMA en mayúsculas muy espaciadas.
 * Solo aparece en Inicio (y en Bienvenida).
 */
@Composable
fun Logotipo(modifier: Modifier = Modifier) {
    val nombre = stringResource(R.string.app_name)
    val subtitulo = stringResource(R.string.logotipo_subtitulo)
    // TalkBack lo lee como una sola frase: "Descansa. Sueño, mente, calma".
    Column(modifier.semantics(mergeDescendants = true) { contentDescription = "$nombre. $subtitulo" }) {
        Text(nombre, style = TipoCartel.logotipo, color = MaterialTheme.colorScheme.primary)
        Text(subtitulo.uppercase(), style = TipoCartel.logotipoSubtitulo, color = MaterialTheme.colorScheme.onBackground)
    }
}

@PreviewLightDark
@Composable
private fun LogotipoPreview() {
    VistaPrevia { Logotipo() }
}
