package com.upt.descansa.feature.aprender

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.ElementoFila
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.FilaDeTres
import com.upt.descansa.core.designsystem.components.ImagenCartel
import com.upt.descansa.core.designsystem.components.LeyendaSeccion
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.components.SelloDestacado
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel

private data class Articulo(
    @StringRes val titulo: Int,
    @StringRes val detalle: Int,
    @DrawableRes val imagen: Int,
    @StringRes val descripcionImagen: Int,
)

private val articulos = listOf(
    Articulo(R.string.aprender_por_que, R.string.aprender_por_que_detalle, R.drawable.img_aprender_lampara, R.string.img_aprender_lampara),
    Articulo(R.string.aprender_etapas, R.string.aprender_etapas_detalle, R.drawable.img_aprender_reloj_arena, R.string.img_aprender_reloj_arena),
    Articulo(R.string.aprender_habitos, R.string.aprender_habitos_detalle, R.drawable.img_aprender_te, R.string.img_aprender_te),
    Articulo(R.string.aprender_mente, R.string.aprender_mente_detalle, R.drawable.img_aprender_cuaderno, R.string.img_aprender_cuaderno),
)

/**
 * Pantalla 10 · Aprender (sección de día: APRENDE).
 *
 * El consejo del día lleva el SelloDestacado; los artículos van en FilaDeTres
 * y el cuarto aparte. En la Fase 6 el contenido vendrá de `learn.json` (con
 * fuentes citadas) y cada tarjeta abrirá su artículo.
 */
@Composable
fun AprenderScreen() {
    val esquema = MaterialTheme.colorScheme
    PantallaCartel(
        palabra = stringResource(R.string.palabra_aprender),
        colorPalabra = LocalColoresCartel.current.dia,
        titulo = stringResource(R.string.aprender_titulo),
    ) {
        // Consejo de hoy con sello destacado encimado arriba a la izquierda.
        Box(Modifier.fillMaxWidth()) {
            TarjetaCartel(Modifier.fillMaxWidth().padding(top = 60.dp, start = 30.dp)) {
                Column(Modifier.padding(start = 60.dp)) {
                    EtiquetaCategoria(stringResource(R.string.aprender_consejo_hoy), adorno = AdornoEtiqueta.NINGUNO)
                    Text(stringResource(R.string.aprender_consejo_texto), style = TipoCartel.cuerpo, color = esquema.onBackground)
                }
            }
            SelloDestacado()
        }
        SeparadorOnda()
        FilaDeTres(
            articulos.take(3).map {
                ElementoFila(
                    imagen = it.imagen,
                    descripcionImagen = stringResource(it.descripcionImagen),
                    etiqueta = stringResource(it.titulo),
                    leyenda = stringResource(it.detalle),
                    onClick = { /* Fase 6: abrir artículo */ },
                )
            },
        )
        val cuarto = articulos.last()
        Column(Modifier.width(160.dp)) {
            ImagenCartel(cuarto.imagen, stringResource(cuarto.descripcionImagen), Modifier.fillMaxWidth())
            LeyendaSeccion(stringResource(cuarto.detalle))
            EtiquetaCategoria(stringResource(cuarto.titulo), adorno = AdornoEtiqueta.CHEVRON)
        }
        // Aviso permanente (sección 14).
        Text(stringResource(R.string.aviso_no_medico), style = TipoCartel.cuerpo, color = esquema.onBackground)
    }
}

@PreviewLightDark
@Composable
private fun AprenderPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) { AprenderScreen() }
}
