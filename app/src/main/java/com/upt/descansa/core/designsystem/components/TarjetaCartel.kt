package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Tarjeta "Cartel sereno": SIN fondo de color ni sombra; solo un contorno de
 * 1.5 dp y esquinas de 4 dp. Si está `seleccionada`, el contorno pasa al color
 * interactivo y engrosa a 2.5 dp (la selección no depende solo del color).
 */
@Composable
fun TarjetaCartel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    seleccionada: Boolean = false,
    relleno: PaddingValues = PaddingValues(Espacio.rellenoTarjeta),
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val colorBorde = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val grosor = if (seleccionada) Bordes.presionado else Bordes.normal
    Column(
        modifier
            .clip(Formas.tarjeta)
            .border(grosor, colorBorde, Formas.tarjeta)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .then(if (seleccionada) Modifier.semantics { selected = true } else Modifier)
            .padding(relleno),
        content = contenido,
    )
}

@PreviewLightDark
@Composable
private fun TarjetaCartelPreview() {
    VistaPrevia {
        TarjetaCartel(seleccionada = true) {
            EtiquetaCategoria("Hora límite")
            Text("07:00", style = TipoCartel.hora, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
