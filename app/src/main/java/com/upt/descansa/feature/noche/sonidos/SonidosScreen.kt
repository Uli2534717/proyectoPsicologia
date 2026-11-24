package com.upt.descansa.feature.noche.sonidos

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.FilaChips
import com.upt.descansa.core.designsystem.PlantillaPaso
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.theme.Borde
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.TextoSobreLavanda

/** Sonido del catálogo. FASE 0: lista fija; en la Fase 5 vendrá de `catalog.json`. */
private data class SonidoUi(val id: String, @StringRes val nombre: Int)

private val catalogo = listOf(
    SonidoUi("lluvia", R.string.sonido_lluvia),
    SonidoUi("ruido_blanco", R.string.sonido_ruido_blanco),
    SonidoUi("olas", R.string.sonido_olas),
    SonidoUi("bosque", R.string.sonido_bosque),
)

private val duracionesApagado = listOf(15, 30, 60)

/**
 * Pantalla 6 · Sonidos (paso 4 de 5).
 *
 * FASE 0: solo interfaz (los botones de reproducir aún no suenan). En la Fase 5
 * se agrega la reproducción en bucle con pantalla apagada, el apagado gradual,
 * los favoritos guardados y la recomendación de la sección 10.3.
 */
@Composable
fun SonidosScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    var volumen by rememberSaveable { mutableFloatStateOf(0.4f) }
    var apagado by rememberSaveable { mutableIntStateOf(30) }
    val favoritos = remember { mutableStateListOf("olas") }
    val recomendado = catalogo.first()

    PlantillaPaso(
        paso = 4,
        titulo = stringResource(R.string.sonidos_titulo),
        textoBoton = stringResource(R.string.continuar),
        onAtras = onAtras,
        onBoton = onSiguiente,
        onOmitir = onSiguiente,
    ) {
        val nombreRecomendado = stringResource(recomendado.nombre)
        TarjetaDescansa(modifier = Modifier.fillMaxWidth(), seleccionada = true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { /* Fase 5: reproducir */ },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Lavanda, CircleShape),
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.sonidos_reproducir, nombreRecomendado),
                        tint = TextoSobreLavanda,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    EtiquetaSeccion(stringResource(R.string.sonidos_recomendado))
                    Text(nombreRecomendado, style = MaterialTheme.typography.titleLarge, color = Texto)
                    // No afirma causalidad: es una sugerencia (sección 10.3).
                    Text(stringResource(R.string.sonidos_recomendado_motivo), style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
                }
            }
        }
        catalogo.drop(1).forEach { sonido ->
            val nombre = stringResource(sonido.nombre)
            TarjetaDescansa(modifier = Modifier.fillMaxWidth(), relleno = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Fase 5: reproducir */ }) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.sonidos_reproducir, nombre), tint = TextoAtenuado)
                    }
                    Text(nombre, style = MaterialTheme.typography.titleMedium, color = Texto, modifier = Modifier.weight(1f))
                    val esFavorito = sonido.id in favoritos
                    IconToggleButton(
                        checked = esFavorito,
                        onCheckedChange = { if (it) favoritos.add(sonido.id) else favoritos.remove(sonido.id) },
                    ) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.sonidos_favorito, nombre),
                            tint = if (esFavorito) Lavanda else TextoAtenuado,
                        )
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, tint = TextoAtenuado)
            Spacer(Modifier.width(16.dp))
            val etiquetaVolumen = stringResource(R.string.sonidos_volumen)
            Slider(
                value = volumen,
                onValueChange = { volumen = it },
                colors = SliderDefaults.colors(thumbColor = Lavanda, activeTrackColor = Lavanda, inactiveTrackColor = Borde),
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = etiquetaVolumen },
            )
        }
        Box {
            Text(stringResource(R.string.sonidos_apagado_gradual), style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        }
        FilaChips(
            opciones = duracionesApagado,
            seleccionada = apagado,
            etiqueta = { stringResource(R.string.minutos_corto, it) },
            onSeleccionar = { apagado = it },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun SonidosPreview() {
    DescansaTheme { SonidosScreen(onAtras = {}, onSiguiente = {}) }
}
