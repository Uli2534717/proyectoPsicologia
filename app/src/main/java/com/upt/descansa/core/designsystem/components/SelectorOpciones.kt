package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Opciones excluyentes ("15 min · 20 min · 30 min") del mismo ancho, en una fila.
 * Para muchas opciones que deben bajar de línea, usar [OpcionCartel] dentro de un FlowRow.
 */
@Composable
fun <T> SelectorOpciones(
    opciones: List<T>,
    seleccionada: T,
    etiqueta: @Composable (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Espacio.m),
        // selectableGroup: TalkBack dice "1 de 3", "2 de 3"...
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
    ) {
        opciones.forEach { opcion ->
            OpcionCartel(
                texto = etiqueta(opcion),
                elegida = opcion == seleccionada,
                onClick = { onSeleccionar(opcion) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Una opción seleccionable con contorno y esquinas de 20 dp.
 * La elegida lleva borde morado de 2.5 dp Y una ✓: nunca se indica solo con
 * color ni con relleno (regla del sistema).
 */
@Composable
fun OpcionCartel(
    texto: String,
    elegida: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val esquema = MaterialTheme.colorScheme
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .heightIn(min = AltoMinimoTactil)
            .clip(Formas.boton)
            .border(
                if (elegida) Bordes.presionado else Bordes.normal,
                if (elegida) esquema.primary else esquema.outline,
                Formas.boton,
            )
            .selectable(selected = elegida, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Espacio.m, vertical = Espacio.m),
    ) {
        Text(
            text = (if (elegida) "✓ " else "") + texto.uppercase(),
            style = TipoCartel.etiqueta,
            color = if (elegida) esquema.primary else esquema.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewLightDark
@Composable
private fun SelectorOpcionesPreview() {
    VistaPrevia {
        SelectorOpciones(listOf(15, 20, 30), 30, { "$it min" }, {})
    }
}
