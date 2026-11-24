package com.upt.descansa.feature.noche.respiracion

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.BotonContorno
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.PalabraCartel
import com.upt.descansa.core.designsystem.components.PlantillaPaso
import com.upt.descansa.core.designsystem.components.SelectorOpciones
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TemaNocturno
import kotlinx.coroutines.delay

/** Duraciones que se pueden elegir (sección 9.1). */
private val duraciones = listOf(2, 5, 10)

/** Una fase de la respiración: palabra que se muestra, cuánto dura y a qué tamaño va el círculo. */
private data class Fase(@StringRes val palabra: Int, val segundos: Int, val escala: Float)

/**
 * Respiración cuadrada: inhala 4, sostén 4, exhala 4, sostén 4.
 * SUPOSICIÓN: técnica fija por ahora; en la Fase 4 se elige entre las tres de
 * `breathing.json` (cuadrada, 4-7-8 y exhalación larga).
 */
private val respiracionCuadrada = listOf(
    Fase(R.string.palabra_inhala, 4, 1f),
    Fase(R.string.palabra_sosten, 4, 1f),
    Fase(R.string.palabra_exhala, 4, ESCALA_MINIMA),
    Fase(R.string.palabra_sosten, 4, ESCALA_MINIMA),
)

private const val ESCALA_MINIMA = 0.55f

/**
 * Pantalla 4 · Respiración guiada, paso 2 de 5 (sección de noche: RESPIRA).
 * SIEMPRE en tema nocturno para no deslumbrar.
 *
 * El RITMO lo marca un temporizador (`delay` por fase), no la duración de la
 * animación: así nunca se desfasa (sección 9.1). El círculo solo SIGUE al
 * temporizador. Con animaciones reducidas, el círculo queda quieto y solo
 * cambia la palabra.
 */
@Composable
fun RespiracionScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    var minutos by rememberSaveable { mutableIntStateOf(5) }
    var pausado by rememberSaveable { mutableStateOf(false) }
    var indiceFase by rememberSaveable { mutableIntStateOf(0) }
    val fase = respiracionCuadrada[indiceFase]
    val reducidas = animacionesReducidas()
    val escala = remember { Animatable(ESCALA_MINIMA) }

    // Temporizador de fases: avanza cada `fase.segundos` mientras no esté en pausa.
    LaunchedEffect(indiceFase, pausado) {
        if (pausado) return@LaunchedEffect
        delay(fase.segundos * 1_000L)
        indiceFase = (indiceFase + 1) % respiracionCuadrada.size
    }
    // El círculo acompaña a la fase actual, durante lo que dura la fase.
    LaunchedEffect(indiceFase, pausado, reducidas) {
        if (pausado || reducidas) return@LaunchedEffect
        escala.animateTo(fase.escala, tween(fase.segundos * 1_000, easing = LinearEasing))
    }

    TemaNocturno {
        PlantillaPaso(
            paso = 2,
            palabra = stringResource(R.string.palabra_respiracion),
            colorPalabra = LocalColoresCartel.current.noche,
            titulo = stringResource(R.string.respiracion_titulo),
            descripcion = stringResource(R.string.respiracion_subtitulo),
            textoBoton = stringResource(R.string.siguiente),
            onAtras = onAtras,
            onBoton = onSiguiente,
            onOmitir = onSiguiente,
            nocturna = true,
            accionesSecundarias = {
                BotonContorno(
                    texto = stringResource(if (pausado) R.string.respiracion_reanudar else R.string.respiracion_pausar),
                    onClick = { pausado = !pausado },
                    icono = if (pausado) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                )
            },
        ) {
            CirculoRespiracion(escala = if (reducidas) 0.8f else escala.value)
            // La palabra de la fase, en estilo cartel, cambia con un fundido.
            // liveRegion: TalkBack anuncia "Inhala", "Sostén", "Exhala" al cambiar.
            val palabraFase = stringResource(fase.palabra)
            Crossfade(
                targetState = palabraFase,
                animationSpec = Movimiento.entrada(Movimiento.MEDIA),
                label = "faseRespiracion",
                modifier = Modifier.semantics {
                    contentDescription = palabraFase
                    liveRegion = LiveRegionMode.Polite
                },
            ) { palabra ->
                PalabraCartel(palabra, LocalColoresCartel.current.noche, animarEntrada = false)
            }
            EtiquetaCategoria(stringResource(R.string.duracion))
            SelectorOpciones(
                opciones = duraciones,
                seleccionada = minutos,
                etiqueta = { stringResource(R.string.minutos_corto, it) },
                onSeleccionar = { minutos = it },
            )
        }
    }
}

/** Círculo de SOLO contorno (2 dp, violeta) con un anillo guía fijo. */
@Composable
private fun CirculoRespiracion(escala: Float) {
    val violeta = LocalColoresCartel.current.noche
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
    ) {
        // Decorativo: la información la da la palabra de la fase.
        Canvas(Modifier.size(240.dp)) {
            val grosor = Bordes.sello.toPx()
            val radioMaximo = size.minDimension / 2 - grosor
            drawCircle(violeta.copy(alpha = 0.35f), radius = radioMaximo, style = Stroke(width = grosor / 2))
            drawCircle(violeta, radius = radioMaximo * escala, style = Stroke(width = grosor))
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RespiracionPreview() {
    TemaNocturno { RespiracionScreen(onAtras = {}, onSiguiente = {}) }
}
