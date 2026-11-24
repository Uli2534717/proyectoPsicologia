package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Pie de imagen o descripción corta: MAYÚSCULAS, Work Sans 400, 14 sp, color
 * de texto, con 20 dp arriba. Va debajo de cada imagen.
 */
@Composable
fun LeyendaSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        style = TipoCartel.leyenda,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.padding(top = Espacio.xl),
    )
}

@PreviewLightDark
@Composable
private fun LeyendaSeccionPreview() {
    VistaPrevia { LeyendaSeccion("Lluvia suave sobre una ventana") }
}
