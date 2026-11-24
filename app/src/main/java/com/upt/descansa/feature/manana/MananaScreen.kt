package com.upt.descansa.feature.manana

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonPrincipal
import com.upt.descansa.core.designsystem.ChipOpcion
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.designsystem.TituloPantalla
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa

/**
 * Movimiento de EJEMPLO por minuto (valores relativos 0..1) para dibujar la
 * gráfica en la Fase 0. En la Fase 6 se reemplaza por la tabla `epocas`.
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
 * Pantalla 9 · Resumen de la mañana.
 *
 * FASE 0: datos de EJEMPLO, marcados como tales en pantalla (no se presentan
 * como reales). En la Fase 6 se leerán de las tablas `noches`, `epocas`
 * y `pendientes`, y la sensación (1–5) se guardará en `noches.sensacion`.
 */
@Composable
fun MananaScreen(onVolverAlInicio: () -> Unit) {
    var sensacion by rememberSaveable { mutableStateOf<Int?>(null) }
    val hechos = remember { mutableStateOf(setOf<Int>()) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            TituloPantalla(stringResource(R.string.manana_saludo), subtitulo = stringResource(R.string.manana_subtitulo))
            Text(stringResource(R.string.manana_ejemplo), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)

            TarjetaDescansa(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .background(SuperficieSecundaria, CircleShape),
                    ) {
                        Icon(Icons.Outlined.Alarm, contentDescription = null, tint = Turquesa)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        EtiquetaSeccion(stringResource(R.string.manana_alarma_sono, "06:48"))
                        Text(stringResource(R.string.manana_motivo_sueno_ligero), style = MaterialTheme.typography.titleMedium, color = Texto)
                        Text(
                            stringResource(R.string.manana_dormiste, stringResource(R.string.inicio_horas_minutos, 7, 12)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoAtenuado,
                        )
                    }
                }
            }

            TarjetaDescansa(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.manana_movimiento), style = MaterialTheme.typography.titleMedium, color = Texto)
                Spacer(Modifier.height(16.dp))
                GraficaMovimiento(
                    valores = movimientoEjemplo,
                    descripcion = stringResource(R.string.manana_grafica_descripcion),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("23:40", "03:00", "06:52").forEach {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
                    }
                }
            }

            TarjetaDescansa(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.manana_pendientes), style = MaterialTheme.typography.titleMedium, color = Texto)
                pendientesEjemplo.forEachIndexed { i, texto ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.heightIn(min = 48.dp)) {
                        Checkbox(
                            checked = i in hechos.value,
                            onCheckedChange = { marcado -> hechos.value = if (marcado) hechos.value + i else hechos.value - i },
                            colors = CheckboxDefaults.colors(checkedColor = Lavanda, uncheckedColor = TextoAtenuado),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(texto, style = MaterialTheme.typography.bodyLarge, color = Texto)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.manana_como_te_sientes), style = MaterialTheme.typography.titleMedium, color = Texto)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    (1..5).forEach { valor ->
                        ChipOpcion(
                            texto = valor.toString(),
                            seleccionado = sensacion == valor,
                            onClick = { sensacion = valor },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.manana_agotado), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
                    Text(stringResource(R.string.manana_muy_bien), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
                }
            }
        }
        BotonPrincipal(
            texto = stringResource(R.string.manana_volver),
            onClick = onVolverAlInicio,
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}

/**
 * Gráfica de barras del movimiento por minuto, dibujada a mano con Canvas
 * (no hace falta una librería para algo tan simple). La última barra, el
 * momento en que sonó la alarma, va en turquesa como en el prototipo.
 */
@Composable
private fun GraficaMovimiento(valores: List<Float>, descripcion: String, modifier: Modifier = Modifier) {
    Canvas(modifier.semantics { contentDescription = descripcion }) {
        if (valores.isEmpty()) return@Canvas
        val espacio = 6.dp.toPx()
        val ancho = (size.width - espacio * (valores.size - 1)) / valores.size
        val altoMinimo = 4.dp.toPx()
        valores.forEachIndexed { i, v ->
            val alto = (size.height * v.coerceIn(0f, 1f)).coerceAtLeast(altoMinimo)
            drawRoundRect(
                color = if (i == valores.lastIndex) Turquesa else Lavanda,
                topLeft = Offset(i * (ancho + espacio), size.height - alto),
                size = Size(ancho, alto),
                cornerRadius = CornerRadius(ancho / 3),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 1100)
@Composable
private fun MananaPreview() {
    DescansaTheme { MananaScreen(onVolverAlInicio = {}) }
}
