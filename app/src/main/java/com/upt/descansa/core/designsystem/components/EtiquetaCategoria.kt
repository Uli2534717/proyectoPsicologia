package com.upt.descansa.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

/** Cómo se acompaña una etiqueta de categoría. */
enum class AdornoEtiqueta { LINEA, CHEVRON, NINGUNO }

/**
 * Etiqueta de categoría: MAYÚSCULAS, Work Sans 500, 14 sp, en el color
 * interactivo, sin fondo ni borde, con 10 dp de relleno vertical y una línea
 * corta o un chevron. Ejemplos: RUIDO BLANCO, LLUVIA, FASE LIGERA.
 */
@Composable
fun EtiquetaCategoria(
    texto: String,
    modifier: Modifier = Modifier,
    adorno: AdornoEtiqueta = AdornoEtiqueta.LINEA,
) {
    val color = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = Espacio.s)
            .semantics { heading() },
    ) {
        if (adorno == AdornoEtiqueta.LINEA) {
            Box(
                Modifier
                    .width(Espacio.l)
                    .height(Bordes.normal)
                    .background(color),
            )
            Spacer(Modifier.width(Espacio.xs))
        }
        Text(texto.uppercase(), style = TipoCartel.etiqueta, color = color)
        if (adorno == AdornoEtiqueta.CHEVRON) {
            Spacer(Modifier.width(Espacio.xs))
            Text("›", style = TipoCartel.etiqueta, color = color)
        }
    }
}

@PreviewLightDark
@Composable
private fun EtiquetaCategoriaPreview() {
    VistaPrevia {
        Column {
            EtiquetaCategoria("Ruido blanco")
            EtiquetaCategoria("Fase ligera", adorno = AdornoEtiqueta.CHEVRON)
        }
    }
}
