package com.upt.descansa.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import androidx.compose.ui.draw.clip
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel

/** Número total de pasos del flujo "Preparar mi noche". */
const val TOTAL_PASOS_NOCHE = 5

/**
 * Plantilla de los 5 pasos de "Preparar mi noche".
 * Igual que PantallaCartel, más: "PASO N DE 5" con barra segmentada arriba y,
 * abajo, el enlace "Omitir paso" junto al botón principal.
 *
 * @param accionesSecundarias botones extra a la izquierda del principal
 *        (p. ej. "Anterior" o pausa); reciben RowScope para usar `weight`.
 */
@Composable
fun PlantillaPaso(
    paso: Int,
    palabra: String,
    colorPalabra: Color,
    titulo: String,
    textoBoton: String,
    onAtras: () -> Unit,
    onBoton: () -> Unit,
    onOmitir: (() -> Unit)?,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    @DrawableRes imagen: Int? = null,
    descripcionImagen: String? = null,
    nocturna: Boolean = false,
    accionesSecundarias: (@Composable RowScope.() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    PantallaCartel(
        palabra = palabra,
        colorPalabra = colorPalabra,
        titulo = titulo,
        descripcion = descripcion,
        imagen = imagen,
        descripcionImagen = descripcionImagen,
        onAtras = onAtras,
        nocturna = nocturna,
        modifier = modifier,
        encabezadoExtra = { ProgresoPasos(paso) },
        accionPrincipal = {
            if (onOmitir != null) EnlaceTexto(stringResource(R.string.paso_omitir), onOmitir)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Espacio.m),
            ) {
                accionesSecundarias?.invoke(this)
                Box(Modifier.weight(1f)) { BotonPildora(textoBoton, onBoton) }
            }
        },
        contenido = contenido,
    )
}

/** "PASO 2 DE 5" y cinco segmentos: los alcanzados, rellenos; los demás, solo contorno. */
@Composable
private fun ProgresoPasos(paso: Int) {
    val texto = stringResource(R.string.paso_n_de_total, paso, TOTAL_PASOS_NOCHE)
    val color = MaterialTheme.colorScheme.primary
    Column(Modifier.semantics(mergeDescendants = true) { contentDescription = texto }) {
        EtiquetaCategoria(texto, Modifier.clearAndSetSemantics { }, adorno = AdornoEtiqueta.NINGUNO)
        Row(horizontalArrangement = Arrangement.spacedBy(Espacio.xs), modifier = Modifier.fillMaxWidth()) {
            repeat(TOTAL_PASOS_NOCHE) { i ->
                val alcanzado = i < paso
                Box(
                    Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(Formas.pildora)
                        .border(Bordes.normal, color, Formas.pildora)
                        // Un indicador de progreso no es un botón: aquí sí se permite relleno.
                        .then(if (alcanzado) Modifier.background(color) else Modifier),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PlantillaPasoPreview() {
    VistaPrevia {
        PlantillaPaso(
            paso = 2, palabra = "RESPIRA", colorPalabra = LocalColoresCartel.current.noche,
            titulo = "Respira con calma", textoBoton = "Siguiente",
            onAtras = {}, onBoton = {}, onOmitir = {},
        ) { AvisoCartel("Contenido") }
    }
}
