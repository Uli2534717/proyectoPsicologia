package com.upt.descansa.feature.manana

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.BotonPildora
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.components.SelectorOpciones
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel

/**
 * Movimiento de EJEMPLO por minuto (valores relativos 0..1).
 * En la Fase 6 se reemplaza por la tabla `epocas`.
 */
private val movimientoEjemplo = listOf(
    0.12f, 0.10f, 0.06f, 0.06f, 0.06f, 0.05f, 0.05f, 0.06f, 0.28f, 0.06f, 0.05f, 0.05f,
    0.05f, 0.07f, 0.34f, 0.05f, 0.05f, 0.05f, 0.06f, 0.10f, 0.48f, 0.20f, 0.42f, 1f,
)

private val pendientesEjemplo = listOf(
    "Entregar el avance del proyecto",
    "Comprar material para el laboratorio",
    "Responder el correo del asesor",
)

/**
 * Pantalla 9 · Resumen de la mañana (sección de día: ÁNIMO).
 *
 * Usa datos de EJEMPLO y lo dice en pantalla (nunca se presentan como reales).
 * En la Fase 6 se leerán de `noches`, `epocas` y `pendientes`, y la sensación
 * (1–5) se guardará en `noches.sensacion`.
 */
@Composable
fun MananaScreen(onVolverAlInicio: () -> Unit) {
    var sensacion by rememberSaveable { mutableStateOf<Int?>(null) }
    val hechos = remember { mutableStateOf(setOf<Int>()) }
    val esquema = MaterialTheme.colorScheme

    PantallaCartel(
        palabra = stringResource(R.string.palabra_manana),
        colorPalabra = LocalColoresCartel.current.dia,
        titulo = stringResource(R.string.manana_saludo),
        descripcion = stringResource(R.string.manana_subtitulo),
        imagen = R.drawable.img_manana_taza,
        descripcionImagen = stringResource(R.string.img_manana_taza),
        accionPrincipal = { BotonPildora(stringResource(R.string.manana_volver), onVolverAlInicio) },
    ) {
        Text(stringResource(R.string.manana_ejemplo).uppercase(), style = TipoCartel.leyenda, color = esquema.onBackground)

        TarjetaCartel(Modifier.fillMaxWidth()) {
            EtiquetaCategoria(stringResource(R.string.manana_alarma_sono, "06:48"), adorno = AdornoEtiqueta.NINGUNO)
            Text(stringResource(R.string.manana_motivo_sueno_ligero), style = TipoCartel.titulo, color = esquema.onBackground)
            Text(
                stringResource(R.string.manana_dormiste, stringResource(R.string.inicio_horas_minutos, 7, 12)),
                style = TipoCartel.cuerpo,
                color = esquema.onBackground,
            )
        }

        EtiquetaCategoria(stringResource(R.string.manana_movimiento))
        GraficaMovimiento(
            valores = movimientoEjemplo,
            descripcion = stringResource(R.string.manana_grafica_descripcion),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("23:40", "03:00", "06:52").forEach { Text(it, style = TipoCartel.leyenda, color = esquema.onBackground) }
        }

        SeparadorOnda()
        EtiquetaCategoria(stringResource(R.string.manana_pendientes))
        pendientesEjemplo.forEachIndexed { i, texto ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.heightIn(min = 48.dp)) {
                Checkbox(
                    checked = i in hechos.value,
                    onCheckedChange = { marcado -> hechos.value = if (marcado) hechos.value + i else hechos.value - i },
                    colors = CheckboxDefaults.colors(checkedColor = esquema.primary, uncheckedColor = esquema.outline),
                )
                Spacer(Modifier.width(Espacio.xs))
                Text(texto, style = TipoCartel.cuerpo, color = esquema.onBackground)
            }
        }

        EtiquetaCategoria(stringResource(R.string.manana_como_te_sientes))
        SelectorOpciones(
            opciones = (1..5).toList(),
            seleccionada = sensacion,
            etiqueta = { it.toString() },
            onSeleccionar = { sensacion = it },
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.manana_agotado).uppercase(), style = TipoCartel.leyenda, color = esquema.onBackground)
            Text(stringResource(R.string.manana_muy_bien).uppercase(), style = TipoCartel.leyenda, color = esquema.onBackground)
        }
    }
}

/**
 * Movimiento por minuto como LÍNEAS verticales de contorno (sin rellenos de
 * área): morado el resto de la noche y azul el minuto en que sonó la alarma.
 * Se dibuja de izquierda a derecha en 600 ms; con animaciones reducidas, de golpe.
 */
@Composable
private fun GraficaMovimiento(valores: List<Float>, descripcion: String, modifier: Modifier = Modifier) {
    val reducidas = animacionesReducidas()
    val progreso = remember { Animatable(if (reducidas) 1f else 0f) }
    LaunchedEffect(Unit) { progreso.animateTo(1f, Movimiento.entrada(Movimiento.LARGA)) }
    val principal = MaterialTheme.colorScheme.primary
    val secundario = MaterialTheme.colorScheme.secondary
    val eje = MaterialTheme.colorScheme.outline
    Canvas(modifier.semantics { contentDescription = descripcion }) {
        if (valores.isEmpty()) return@Canvas
        val grosor = 3.dp.toPx()
        val paso = size.width / valores.size
        // Eje inferior en el color de texto.
        drawLine(eje, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1.5.dp.toPx())
        clipRect(right = size.width * progreso.value) {
            valores.forEachIndexed { i, v ->
                val x = paso * i + paso / 2
                val alto = (size.height * v.coerceIn(0f, 1f)).coerceAtLeast(grosor)
                drawLine(
                    color = if (i == valores.lastIndex) secundario else principal,
                    start = Offset(x, size.height),
                    end = Offset(x, size.height - alto),
                    strokeWidth = grosor,
                    cap = StrokeCap.Butt,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MananaPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) { MananaScreen(onVolverAlInicio = {}) }
}
