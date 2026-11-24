package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Enlace de texto ("Omitir paso"): texto en el color normal, SUBRAYADO en azul
 * (uso permitido del AzulCartel). Área táctil de al menos 48 dp.
 */
@Composable
fun EnlaceTexto(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val azul = MaterialTheme.colorScheme.secondary
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .heightIn(min = AltoMinimoTactil)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Espacio.xs),
    ) {
        // El subrayado azul se dibuja con un texto de color normal: se lee bien y se ve como enlace.
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.onBackground)) {
                    append(texto.uppercase())
                }
            },
            style = TipoCartel.etiqueta,
            color = azul,
        )
    }
}

@PreviewLightDark
@Composable
private fun EnlaceTextoPreview() {
    VistaPrevia { EnlaceTexto("Omitir paso", onClick = {}) }
}
