package com.upt.descansa.feature.noche.relajacion

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonSecundario
import com.upt.descansa.core.designsystem.ChipOpcion
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.PlantillaPaso
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado

/** Los 6 grupos musculares de la sección 9.2, en orden de pies a rostro. */
enum class GrupoMuscular(@StringRes val nombre: Int, @StringRes val instruccion: Int) {
    PIES(R.string.relajacion_pies, R.string.relajacion_instr_pies),
    PIERNAS(R.string.relajacion_piernas, R.string.relajacion_instr_piernas),
    ABDOMEN(R.string.relajacion_abdomen, R.string.relajacion_instr_abdomen),
    MANOS(R.string.relajacion_manos, R.string.relajacion_instr_manos),
    HOMBROS(R.string.relajacion_hombros, R.string.relajacion_instr_hombros),
    ROSTRO(R.string.relajacion_rostro, R.string.relajacion_instr_rostro),
}

/**
 * Pantalla 5 · Relajación muscular progresiva (paso 3 de 5).
 *
 * "Siguiente" avanza de grupo; en el último grupo pasa al siguiente paso.
 * FASE 0: la cuenta regresiva muestra 00:05 fijo. En la Fase 4 se agregan los
 * tiempos reales (5 s de tensión, 10 s de relajación) desde `relaxation.json`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelajacionScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    var indice by rememberSaveable { mutableIntStateOf(0) }
    val grupos = GrupoMuscular.entries
    val grupo = grupos[indice]
    val esUltimo = indice == grupos.lastIndex

    PlantillaPaso(
        paso = 3,
        titulo = stringResource(R.string.relajacion_titulo),
        subtitulo = stringResource(R.string.relajacion_subtitulo),
        textoBoton = stringResource(R.string.siguiente),
        onAtras = onAtras,
        onBoton = { if (esUltimo) onSiguiente() else indice++ },
        onOmitir = onSiguiente,
        accionesInferiores = {
            BotonSecundario(
                texto = stringResource(R.string.anterior),
                onClick = { if (indice > 0) indice-- else onAtras() },
                modifier = Modifier.weight(1f),
            )
        },
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            grupos.forEachIndexed { i, g ->
                ChipOpcion(texto = stringResource(g.nombre), seleccionado = i == indice, onClick = { indice = i })
            }
        }
        TarjetaDescansa(modifier = Modifier.fillMaxWidth()) {
            val nombre = stringResource(grupo.nombre)
            FiguraCuerpo(
                resaltado = grupo,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 140.dp, height = 180.dp),
                descripcion = stringResource(R.string.relajacion_figura, nombre),
            )
            Spacer(Modifier.height(16.dp))
            EtiquetaSeccion(
                stringResource(R.string.relajacion_grupo_n_de_total, nombre, indice + 1, grupos.size),
                Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(grupo.instruccion),
                style = MaterialTheme.typography.bodyLarge,
                color = Texto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "00:05",
                style = MaterialTheme.typography.displaySmall,
                color = Texto,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
        Text(stringResource(R.string.relajacion_seguridad), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
    }
}

/**
 * Figura de palitos dibujada con Canvas (vector, sin imágenes pesadas).
 * El grupo activo se pinta en lavanda y más grueso; el resto en gris.
 */
@Composable
private fun FiguraCuerpo(resaltado: GrupoMuscular, descripcion: String, modifier: Modifier = Modifier) {
    Canvas(modifier.semantics { contentDescription = descripcion }) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val gris = TextoAtenuado
        fun color(g: GrupoMuscular): Color = if (g == resaltado) Lavanda else gris
        fun grosor(g: GrupoMuscular) = if (g == resaltado) 10.dp.toPx() else 5.dp.toPx()
        fun linea(g: GrupoMuscular, desde: Offset, hasta: Offset) =
            drawLine(color(g), desde, hasta, strokeWidth = grosor(g), cap = StrokeCap.Round)

        val radioCabeza = h * 0.09f
        val cabeza = Offset(cx, radioCabeza + 4f)
        val cuello = Offset(cx, cabeza.y + radioCabeza)
        val hombros = Offset(cx, h * 0.30f)
        val cintura = Offset(cx, h * 0.52f)
        val tobilloIzq = Offset(cx - w * 0.17f, h * 0.92f)
        val tobilloDer = Offset(cx + w * 0.17f, h * 0.92f)

        // Rostro
        drawCircle(color(GrupoMuscular.ROSTRO), radioCabeza, cabeza, style = Stroke(grosor(GrupoMuscular.ROSTRO)))
        // Hombros y tronco superior
        linea(GrupoMuscular.HOMBROS, cuello, hombros)
        linea(GrupoMuscular.HOMBROS, Offset(cx - w * 0.18f, hombros.y), Offset(cx + w * 0.18f, hombros.y))
        // Abdomen
        linea(GrupoMuscular.ABDOMEN, hombros, cintura)
        // Brazos (las manos son los extremos)
        val manoIzq = Offset(cx - w * 0.36f, h * 0.48f)
        val manoDer = Offset(cx + w * 0.36f, h * 0.48f)
        drawLine(gris, Offset(cx - w * 0.18f, hombros.y), manoIzq, 5.dp.toPx(), StrokeCap.Round)
        drawLine(gris, Offset(cx + w * 0.18f, hombros.y), manoDer, 5.dp.toPx(), StrokeCap.Round)
        drawCircle(color(GrupoMuscular.MANOS), grosor(GrupoMuscular.MANOS), manoIzq)
        drawCircle(color(GrupoMuscular.MANOS), grosor(GrupoMuscular.MANOS), manoDer)
        // Piernas
        linea(GrupoMuscular.PIERNAS, cintura, tobilloIzq)
        linea(GrupoMuscular.PIERNAS, cintura, tobilloDer)
        // Pies
        linea(GrupoMuscular.PIES, tobilloIzq, Offset(tobilloIzq.x - w * 0.10f, tobilloIzq.y))
        linea(GrupoMuscular.PIES, tobilloDer, Offset(tobilloDer.x + w * 0.10f, tobilloDer.y))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun RelajacionPreview() {
    DescansaTheme { RelajacionScreen(onAtras = {}, onSiguiente = {}) }
}
