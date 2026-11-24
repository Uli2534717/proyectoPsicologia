package com.upt.descansa.feature.noche.relajacion

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.BotonSecundario
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.OpcionCartel
import com.upt.descansa.core.designsystem.components.PlantillaPaso
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel

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
 * Pantalla 5 · Relajación muscular progresiva, paso 3 de 5 (sección de noche: CALMA).
 *
 * "Siguiente" avanza de grupo; en el último grupo pasa al paso siguiente.
 * La cuenta regresiva muestra 00:05 fija: en la Fase 4 llegan los tiempos
 * reales (5 s de tensión y 10 s de relajación).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelajacionScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    var indice by rememberSaveable { mutableIntStateOf(0) }
    val grupos = GrupoMuscular.entries
    val grupo = grupos[indice]
    val esUltimo = indice == grupos.lastIndex
    val esquema = MaterialTheme.colorScheme

    PlantillaPaso(
        paso = 3,
        palabra = stringResource(R.string.palabra_relajacion),
        colorPalabra = LocalColoresCartel.current.noche,
        titulo = stringResource(R.string.relajacion_titulo),
        descripcion = stringResource(R.string.relajacion_subtitulo),
        imagen = R.drawable.img_relajacion_manta,
        descripcionImagen = stringResource(R.string.img_relajacion_manta),
        textoBoton = stringResource(R.string.siguiente),
        onAtras = onAtras,
        onBoton = { if (esUltimo) onSiguiente() else indice++ },
        onOmitir = onSiguiente,
        accionesSecundarias = {
            BotonSecundario(stringResource(R.string.anterior), onClick = { if (indice > 0) indice-- else onAtras() })
        },
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Espacio.xs),
            verticalArrangement = Arrangement.spacedBy(Espacio.xs),
            modifier = Modifier.selectableGroup(),
        ) {
            grupos.forEachIndexed { i, g ->
                OpcionCartel(stringResource(g.nombre), elegida = i == indice, onClick = { indice = i })
            }
        }
        TarjetaCartel(Modifier.fillMaxWidth()) {
            val nombre = stringResource(grupo.nombre)
            FiguraCuerpo(
                resaltado = grupo,
                descripcion = stringResource(R.string.relajacion_figura, nombre),
                colorResaltado = esquema.primary,
                colorBase = esquema.outline.copy(alpha = 0.45f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 140.dp, height = 180.dp),
            )
            EtiquetaCategoria(
                stringResource(R.string.relajacion_grupo_n_de_total, nombre, indice + 1, grupos.size),
                Modifier.align(Alignment.CenterHorizontally),
                adorno = AdornoEtiqueta.NINGUNO,
            )
            Text(
                stringResource(grupo.instruccion),
                style = TipoCartel.cuerpo,
                color = esquema.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "00:05",
                style = TipoCartel.hora,
                color = esquema.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
        Text(stringResource(R.string.relajacion_seguridad), style = TipoCartel.cuerpo, color = esquema.onBackground)
    }
}

/**
 * Figura de palitos con Canvas: es una GUÍA funcional (qué grupo tensar), no
 * decoración. El grupo activo va en el color interactivo y más grueso.
 */
@Composable
private fun FiguraCuerpo(
    resaltado: GrupoMuscular,
    descripcion: String,
    colorResaltado: Color,
    colorBase: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.semantics { contentDescription = descripcion }) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        fun color(g: GrupoMuscular): Color = if (g == resaltado) colorResaltado else colorBase
        fun grosor(g: GrupoMuscular) = if (g == resaltado) 8.dp.toPx() else 4.dp.toPx()
        fun linea(g: GrupoMuscular, desde: Offset, hasta: Offset) =
            drawLine(color(g), desde, hasta, strokeWidth = grosor(g), cap = StrokeCap.Round)

        val radioCabeza = h * 0.09f
        val cabeza = Offset(cx, radioCabeza + 4f)
        val cuello = Offset(cx, cabeza.y + radioCabeza)
        val hombros = Offset(cx, h * 0.30f)
        val cintura = Offset(cx, h * 0.52f)
        val tobilloIzq = Offset(cx - w * 0.17f, h * 0.92f)
        val tobilloDer = Offset(cx + w * 0.17f, h * 0.92f)
        val manoIzq = Offset(cx - w * 0.36f, h * 0.48f)
        val manoDer = Offset(cx + w * 0.36f, h * 0.48f)

        drawCircle(color(GrupoMuscular.ROSTRO), radioCabeza, cabeza, style = Stroke(grosor(GrupoMuscular.ROSTRO)))
        linea(GrupoMuscular.HOMBROS, cuello, hombros)
        linea(GrupoMuscular.HOMBROS, Offset(cx - w * 0.18f, hombros.y), Offset(cx + w * 0.18f, hombros.y))
        linea(GrupoMuscular.ABDOMEN, hombros, cintura)
        drawLine(colorBase, Offset(cx - w * 0.18f, hombros.y), manoIzq, 4.dp.toPx(), StrokeCap.Round)
        drawLine(colorBase, Offset(cx + w * 0.18f, hombros.y), manoDer, 4.dp.toPx(), StrokeCap.Round)
        drawCircle(color(GrupoMuscular.MANOS), grosor(GrupoMuscular.MANOS), manoIzq)
        drawCircle(color(GrupoMuscular.MANOS), grosor(GrupoMuscular.MANOS), manoDer)
        linea(GrupoMuscular.PIERNAS, cintura, tobilloIzq)
        linea(GrupoMuscular.PIERNAS, cintura, tobilloDer)
        linea(GrupoMuscular.PIES, tobilloIzq, Offset(tobilloIzq.x - w * 0.10f, tobilloIzq.y))
        linea(GrupoMuscular.PIES, tobilloDer, Offset(tobilloDer.x + w * 0.10f, tobilloDer.y))
    }
}

@PreviewLightDark
@Composable
private fun RelajacionPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) { RelajacionScreen(onAtras = {}, onSiguiente = {}) }
}
