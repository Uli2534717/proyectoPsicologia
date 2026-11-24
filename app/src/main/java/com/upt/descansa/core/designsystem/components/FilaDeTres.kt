package com.upt.descansa.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.theme.Espacio

/** Un elemento de la fila: imagen, etiqueta de categoría y leyenda. */
data class ElementoFila(
    @DrawableRes val imagen: Int,
    val descripcionImagen: String,
    val etiqueta: String,
    val leyenda: String,
    val onClick: (() -> Unit)? = null,
)

/**
 * Tres columnas iguales separadas 20 dp, sin divisores. Cada columna lleva
 * imagen + EtiquetaCategoria + LeyendaSeccion.
 *
 * En pantallas angostas (menos de 360 dp de ancho, o con letra muy grande)
 * cambia a desplazamiento horizontal para que nada se apriete.
 */
@Composable
fun FilaDeTres(elementos: List<ElementoFila>, modifier: Modifier = Modifier) {
    // Con letra grande del sistema, tres columnas no caben sin partir palabras.
    val letraGrande = LocalDensity.current.fontScale > 1.15f
    BoxWithConstraints(modifier.fillMaxWidth()) {
        if (maxWidth < 360.dp || letraGrande) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Espacio.xl),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                elementos.forEach { ColumnaFila(it, Modifier.width(if (letraGrande) 180.dp else 140.dp)) }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(Espacio.xl)) {
                elementos.forEach { ColumnaFila(it, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ColumnaFila(elemento: ElementoFila, modifier: Modifier) {
    val clic = elemento.onClick
    Column(
        modifier.then(
            if (clic != null) Modifier.clickable(role = Role.Button, onClick = clic) else Modifier,
        ),
    ) {
        ImagenCartel(elemento.imagen, elemento.descripcionImagen, Modifier.fillMaxWidth())
        LeyendaSeccion(elemento.leyenda)
        EtiquetaCategoria(elemento.etiqueta, adorno = if (clic != null) AdornoEtiqueta.CHEVRON else AdornoEtiqueta.LINEA)
    }
}

@PreviewLightDark
@Composable
private fun FilaDeTresPreview() {
    VistaPrevia {
        FilaDeTres(
            listOf(
                ElementoFila(R.drawable.img_sonido_lluvia, "Lluvia", "Lluvia", "Suave"),
                ElementoFila(R.drawable.img_sonido_olas, "Olas", "Olas", "Del mar"),
                ElementoFila(R.drawable.img_sonido_bosque, "Bosque", "Bosque", "De noche"),
            ),
        )
    }
}
