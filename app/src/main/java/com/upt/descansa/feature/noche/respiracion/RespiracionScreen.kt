package com.upt.descansa.feature.noche.respiracion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.FilaChips
import com.upt.descansa.core.designsystem.PlantillaPaso
import com.upt.descansa.core.theme.Borde
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Superficie
import com.upt.descansa.core.theme.SuperficieSeleccionada
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado

/** Duraciones que se pueden elegir (sección 9.1). */
private val duraciones = listOf(2, 5, 10)

/**
 * Pantalla 4 · Respiración guiada (paso 2 de 5).
 *
 * FASE 0: círculo estático con el diseño del prototipo y controles de duración
 * y pausa. En la Fase 4 se agrega el temporizador preciso, la animación (que
 * respeta "reducir animaciones"), las 3 técnicas de `breathing.json` y la vibración.
 */
@Composable
fun RespiracionScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    var minutos by rememberSaveable { mutableIntStateOf(5) }
    var pausado by rememberSaveable { mutableStateOf(false) }

    PlantillaPaso(
        paso = 2,
        titulo = stringResource(R.string.respiracion_titulo),
        subtitulo = stringResource(R.string.respiracion_subtitulo),
        textoBoton = stringResource(R.string.siguiente),
        onAtras = onAtras,
        onBoton = onSiguiente,
        onOmitir = onSiguiente,
        accionesInferiores = {
            OutlinedIconButton(
                onClick = { pausado = !pausado },
                shape = CircleShape,
                border = BorderStroke(1.dp, Borde),
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = if (pausado) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                    contentDescription = stringResource(if (pausado) R.string.respiracion_reanudar else R.string.respiracion_pausar),
                    tint = Texto,
                )
            }
        },
    ) {
        // Círculo exterior (guía) + círculo interior (el que crecerá y se encogerá).
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
            Surface(shape = CircleShape, color = Superficie.copy(alpha = 0f), border = BorderStroke(2.dp, Borde), modifier = Modifier.size(260.dp)) {}
            Surface(
                shape = CircleShape,
                color = SuperficieSeleccionada,
                border = BorderStroke(2.dp, Lavanda),
                modifier = Modifier.size(216.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.respiracion_inhala_exhala), style = MaterialTheme.typography.titleLarge, color = Texto)
                }
            }
        }
        Text(stringResource(R.string.duracion), style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        FilaChips(
            opciones = duraciones,
            seleccionada = minutos,
            etiqueta = { stringResource(R.string.minutos_corto, it) },
            onSeleccionar = { minutos = it },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun RespiracionPreview() {
    DescansaTheme { RespiracionScreen(onAtras = {}, onSiguiente = {}) }
}
