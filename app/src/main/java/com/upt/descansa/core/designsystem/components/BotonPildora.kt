package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.core.designsystem.theme.Formas

/**
 * Botón PRINCIPAL de la pantalla, con forma de píldora y contorno verde.
 * Regla: solo UNO por pantalla, para la acción más importante
 * ("Preparar mi noche", "Activar modo noche", "Detener").
 */
@Composable
fun BotonPildora(
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
        forma = Formas.pildora,
        relleno = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        modifier = modifier.fillMaxWidth(),
        habilitado = habilitado,
        icono = icono,
    )
}

@PreviewLightDark
@Composable
private fun BotonPildoraPreview() {
    VistaPrevia { BotonPildora("Activar modo noche", onClick = {}) }
}
