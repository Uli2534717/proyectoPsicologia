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
 * Acción secundaria ("Anterior", "Posponer"): contorno AZUL. El texto va en el
 * color normal de texto porque el azul sobre blanco queda justo en 4.5:1.
 */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null,
) {
    BotonBase(
        texto = texto,
        onClick = onClick,
        colorBorde = MaterialTheme.colorScheme.secondary,
        colorTexto = MaterialTheme.colorScheme.onBackground,
        forma = Formas.boton,
        relleno = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier,
        habilitado = habilitado,
        icono = icono,
    )
}

@PreviewLightDark
@Composable
private fun BotonSecundarioPreview() {
    VistaPrevia { BotonSecundario("Anterior", onClick = {}) }
}
