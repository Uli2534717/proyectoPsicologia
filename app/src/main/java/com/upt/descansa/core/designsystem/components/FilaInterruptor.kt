package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Fila con interruptor dentro de una tarjeta de contorno. Toda la fila es
 * tocable (área grande) y TalkBack la anuncia como interruptor.
 */
@Composable
fun FilaInterruptor(
    titulo: String,
    activado: Boolean,
    onCambiar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    detalle: String? = null,
) {
    val esquema = MaterialTheme.colorScheme
    TarjetaCartel(
        modifier
            .fillMaxWidth()
            .toggleable(value = activado, role = Role.Switch, onValueChange = onCambiar),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(titulo, style = TipoCartel.titulo, color = esquema.onBackground)
                if (detalle != null) Text(detalle, style = TipoCartel.cuerpo, color = esquema.onBackground)
            }
            Spacer(Modifier.width(Espacio.l))
            // Apagado: solo contorno. Encendido: pista en el color interactivo.
            Switch(
                checked = activado,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = esquema.background,
                    checkedTrackColor = esquema.primary,
                    checkedBorderColor = esquema.primary,
                    uncheckedThumbColor = esquema.outline,
                    uncheckedTrackColor = esquema.background,
                    uncheckedBorderColor = esquema.outline,
                ),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FilaInterruptorPreview() {
    VistaPrevia { FilaInterruptor("No molestar", true, {}, detalle = "Las alarmas seguirán sonando.") }
}
