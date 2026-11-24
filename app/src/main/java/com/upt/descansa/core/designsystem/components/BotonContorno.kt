package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.core.designsystem.theme.Formas

/**
 * Botón normal: texto y borde de 1.5 dp en el color interactivo, esquinas de
 * 20 dp, fondo del lienzo. SIN relleno de color en ningún estado.
 */
@Composable
fun BotonContorno(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null,
) {
    val primario = MaterialTheme.colorScheme.primary
    BotonBase(
        texto = texto,
        onClick = onClick,
        colorBorde = primario,
        colorTexto = primario,
        forma = Formas.boton,
        relleno = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier,
        habilitado = habilitado,
        icono = icono,
    )
}

@PreviewLightDark
@Composable
private fun BotonContornoPreview() {
    VistaPrevia { BotonContorno("Siguiente", onClick = {}) }
}
