package com.upt.descansa.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel

/**
 * Línea en zigzag ANGULAR (sin curvas) para separar secciones.
 * Se desplaza suavemente en horizontal; con animaciones reducidas se queda quieta.
 *
 * @param color turquesa (decorativo) o el color interactivo.
 */
@Composable
fun SeparadorOnda(
    modifier: Modifier = Modifier,
    color: Color = LocalColoresCartel.current.turquesa,
) {
    val reducidas = animacionesReducidas()
    val transicion = rememberInfiniteTransition(label = "onda")
    val fase by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(Movimiento.CICLO_ONDA, easing = LinearEasing), RepeatMode.Restart),
        label = "faseOnda",
    )
    Canvas(
        modifier
            .fillMaxWidth()
            .height(12.dp)
            .clipToBounds()
            // Decorativo: TalkBack lo ignora.
            .clearAndSetSemantics { },
    ) {
        val periodo = 16.dp.toPx()
        val desplazamiento = if (reducidas) 0f else fase * periodo
        val arriba = Bordes.normal.toPx()
        val abajo = size.height - Bordes.normal.toPx()
        val trazo = Path()
        // Empieza un periodo antes del borde para que el movimiento no deje huecos.
        var x = -periodo + desplazamiento
        trazo.moveTo(x, abajo)
        while (x < size.width + periodo) {
            trazo.lineTo(x + periodo / 2, arriba)
            trazo.lineTo(x + periodo, abajo)
            x += periodo
        }
        drawPath(trazo, color, style = Stroke(width = Bordes.normal.toPx() * 1.4f, join = StrokeJoin.Miter))
    }
}

@PreviewLightDark
@Composable
private fun SeparadorOndaPreview() {
    VistaPrevia {
        Column {
            SeparadorOnda()
            Spacer(Modifier.height(20.dp))
            SeparadorOnda(color = MaterialTheme.colorScheme.primary)
        }
    }
}
