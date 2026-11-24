package com.upt.descansa.feature.noche.sonidos

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.ElementoFila
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.FilaDeTres
import com.upt.descansa.core.designsystem.components.PlantillaPaso
import com.upt.descansa.core.designsystem.components.SelectorOpciones
import com.upt.descansa.core.designsystem.components.SelloDestacado
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel

/** Sonido del catálogo. Todavía fijo; en la Fase 5 vendrá de `catalog.json`. */
private data class SonidoUi(
    val id: String,
    @StringRes val nombre: Int,
    @StringRes val leyenda: Int,
    @DrawableRes val imagen: Int,
    @StringRes val descripcionImagen: Int,
)

private val catalogo = listOf(
    SonidoUi("lluvia", R.string.sonido_lluvia, R.string.sonidos_recomendado_motivo, R.drawable.img_sonido_lluvia, R.string.img_sonido_lluvia),
    SonidoUi("ruido_blanco", R.string.sonido_ruido_blanco, R.string.sonidos_leyenda_ruido_blanco, R.drawable.img_sonido_ruido_blanco, R.string.img_sonido_ruido_blanco),
    SonidoUi("olas", R.string.sonido_olas, R.string.sonidos_leyenda_olas, R.drawable.img_sonido_olas, R.string.img_sonido_olas),
    SonidoUi("bosque", R.string.sonido_bosque, R.string.sonidos_leyenda_bosque, R.drawable.img_sonido_bosque, R.string.img_sonido_bosque),
)

private val duracionesApagado = listOf(15, 30, 60)

/**
 * Pantalla 6 · Sonidos, paso 4 de 5 (sección de noche: SUEÑA).
 *
 * Todavía sin audio: los botones de reproducir no suenan. En la Fase 5 llegan
 * la reproducción en bucle, el apagado gradual, los favoritos guardados, la
 * recomendación (10.3), el tema nocturno mientras suena y las barras de
 * contorno animadas de la mezcla.
 */
@Composable
fun SonidosScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    var volumen by rememberSaveable { mutableFloatStateOf(0.4f) }
    var apagado by rememberSaveable { mutableIntStateOf(30) }
    val favoritos = remember { mutableStateListOf("olas") }
    val recomendado = catalogo.first()
    val esquema = MaterialTheme.colorScheme

    PlantillaPaso(
        paso = 4,
        palabra = stringResource(R.string.palabra_sonidos),
        colorPalabra = LocalColoresCartel.current.noche,
        titulo = stringResource(R.string.sonidos_titulo),
        imagen = recomendado.imagen,
        descripcionImagen = stringResource(recomendado.descripcionImagen),
        textoBoton = stringResource(R.string.continuar),
        onAtras = onAtras,
        onBoton = onSiguiente,
        onOmitir = onSiguiente,
    ) {
        // Recomendado: el sello DESTACADO se encima arriba a la izquierda de la tarjeta.
        Box(Modifier.fillMaxWidth()) {
            TarjetaCartel(Modifier.fillMaxWidth().padding(top = 60.dp, start = 30.dp)) {
                val nombre = stringResource(recomendado.nombre)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f).padding(start = 60.dp)) {
                        EtiquetaCategoria(stringResource(R.string.sonidos_recomendado), adorno = AdornoEtiqueta.NINGUNO)
                        Text(nombre, style = TipoCartel.titulo, color = esquema.onBackground)
                        // No afirma causalidad: es una sugerencia (sección 10.3).
                        Text(stringResource(R.string.sonidos_recomendado_motivo), style = TipoCartel.cuerpo, color = esquema.onBackground)
                    }
                    IconButton(onClick = { /* Fase 5: reproducir */ }) {
                        Icon(Icons.Outlined.PlayArrow, stringResource(R.string.sonidos_reproducir, nombre), tint = esquema.primary)
                    }
                }
            }
            SelloDestacado()
        }

        SeparadorOnda()
        FilaDeTres(
            catalogo.drop(1).map { sonido ->
                ElementoFila(
                    imagen = sonido.imagen,
                    descripcionImagen = stringResource(sonido.descripcionImagen),
                    etiqueta = stringResource(sonido.nombre),
                    leyenda = stringResource(sonido.leyenda),
                    onClick = { /* Fase 5: reproducir */ },
                )
            },
        )

        // Favoritos: el corazón es un CONTROL (permitido), con nombre para TalkBack.
        catalogo.forEach { sonido ->
            val nombre = stringResource(sonido.nombre)
            val esFavorito = sonido.id in favoritos
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(nombre, style = TipoCartel.titulo, color = esquema.onBackground, modifier = Modifier.weight(1f))
                IconToggleButton(
                    checked = esFavorito,
                    onCheckedChange = { if (it) favoritos.add(sonido.id) else favoritos.remove(sonido.id) },
                ) {
                    Icon(
                        if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.sonidos_favorito, nombre),
                        tint = esquema.primary,
                    )
                }
            }
        }

        EtiquetaCategoria(stringResource(R.string.sonidos_volumen))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, tint = esquema.onBackground)
            Spacer(Modifier.width(Espacio.l))
            val etiquetaVolumen = stringResource(R.string.sonidos_volumen)
            Slider(
                value = volumen,
                onValueChange = { volumen = it },
                colors = SliderDefaults.colors(
                    thumbColor = esquema.primary,
                    activeTrackColor = esquema.primary,
                    inactiveTrackColor = esquema.outline.copy(alpha = 0.3f),
                ),
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = etiquetaVolumen },
            )
        }
        EtiquetaCategoria(stringResource(R.string.sonidos_apagado_gradual))
        SelectorOpciones(
            opciones = duracionesApagado,
            seleccionada = apagado,
            etiqueta = { stringResource(R.string.minutos_corto, it) },
            onSeleccionar = { apagado = it },
        )
    }
}

@PreviewLightDark
@Composable
private fun SonidosPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) { SonidosScreen(onAtras = {}, onSiguiente = {}) }
}
