package com.upt.descansa.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel

/** Tamaño de la palabra gigante. */
enum class TamanoCartel { XL, NORMAL }

/**
 * Palabra gigante de color que identifica cada sección, como un cartel impreso.
 *
 * - Se ajusta al ancho de la pantalla (`TextAutoSize`): nunca se corta.
 * - Es DECORATIVA: `clearAndSetSemantics {}` hace que TalkBack la ignore.
 *   El título real de la pantalla va aparte, en un texto accesible.
 * - Entrada: sube 40 dp y se revela de abajo hacia arriba en 600 ms, como un
 *   cartel que se pega. Con animaciones reducidas, solo aparece.
 *
 * @param color violeta (noche) u orquídea (día), de `LocalColoresCartel`.
 */
@Composable
fun PalabraCartel(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier,
    tamano: TamanoCartel = TamanoCartel.NORMAL,
    animarEntrada: Boolean = true,
) {
    val reducidas = animacionesReducidas()
    // 0 = oculta, 1 = completamente visible. `texto` como clave: si cambia la
    // palabra (por ejemplo INHALA → EXHALA), se vuelve a "pegar".
    val progreso = remember(texto) { Animatable(if (animarEntrada && !reducidas) 0f else 1f) }
    LaunchedEffect(texto) {
        if (progreso.value < 1f) progreso.animateTo(1f, Movimiento.entrada(Movimiento.LARGA))
    }
    val desplazamiento = with(LocalDensity.current) { 40.dp.toPx() }
    val estilo = if (tamano == TamanoCartel.XL) TipoCartel.cartelXL else TipoCartel.cartel

    BasicText(
        text = texto,
        style = estilo.copy(color = color, textAlign = TextAlign.Start),
        maxLines = 1,
        // Reduce el tamaño de a 2 sp hasta que la palabra quepa en el ancho.
        autoSize = TextAutoSize.StepBased(minFontSize = 32.sp, maxFontSize = estilo.fontSize, stepSize = 2.sp),
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { }
            .graphicsLayer { translationY = (1f - progreso.value) * desplazamiento }
            // Recorte de abajo hacia arriba: solo se dibuja la parte ya "pegada".
            .drawWithContent {
                val visible = size.height * progreso.value
                clipRect(top = size.height - visible) { this@drawWithContent.drawContent() }
            },
    )
}

@PreviewLightDark
@Composable
private fun PalabraCartelPreview() {
    VistaPrevia {
        PalabraCartel("RESPIRA", LocalColoresCartel.current.noche, animarEntrada = false)
    }
}
