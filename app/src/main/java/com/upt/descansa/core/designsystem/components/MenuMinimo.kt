package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Botón de menú mínimo, arriba a la derecha: la palabra MENÚ y tres líneas
 * (24 dp de ancho, 3 dp de grosor, 4 dp de separación). Sin barra, sin borde y
 * sin sombra. Abre el MenuPantallaCompleta.
 *
 * @param texto permite reutilizarlo como "CERRAR" dentro del menú abierto.
 */
@Composable
fun MenuMinimo(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    texto: String = stringResource(R.string.menu),
    descripcion: String = stringResource(R.string.menu_abrir),
) {
    val color = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = AltoMinimoTactil)
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = descripcion
            }
            .padding(horizontal = Espacio.xs),
    ) {
        Text(
            texto.uppercase(),
            style = TipoCartel.etiqueta.copy(fontSize = 17.sp),
            color = color,
            modifier = Modifier.clearAndSetSemantics { },
        )
        Spacer(Modifier.width(Espacio.m))
        // Tres líneas: alto total = 3 × 3 dp + 2 × 4 dp = 17 dp.
        Canvas(Modifier.size(width = 24.dp, height = 17.dp)) {
            val grosor = 3.dp.toPx()
            val paso = 7.dp.toPx()
            repeat(3) { i ->
                val y = grosor / 2 + i * paso
                drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = grosor)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MenuMinimoPreview() {
    VistaPrevia { MenuMinimo(onClick = {}) }
}
