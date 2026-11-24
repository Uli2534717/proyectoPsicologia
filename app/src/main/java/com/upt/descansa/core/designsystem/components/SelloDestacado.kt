package com.upt.descansa.core.designsystem.components

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.Bordes

/**
 * Sello circular de 120 dp, sin relleno, con "DESTACADO · DESTACADO ·" escrito
 * en curva. Marca contenido especial (consejo del día, sonido recomendado).
 *
 * Compose no sabe escribir texto sobre una curva, así que usamos el Canvas
 * nativo de Android: `drawTextOnPath` con un camino circular.
 * Gira muy lento (una vuelta cada 20 s), como un sello de goma; con
 * animaciones reducidas se queda quieto.
 */
@Composable
fun SelloDestacado(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    val texto = stringResource(R.string.sello_destacado_texto)
    val descripcion = stringResource(R.string.sello_destacado_descripcion)
    val context = LocalContext.current
    val reducidas = animacionesReducidas()

    // Work Sans 500 para el texto curvo (se carga una sola vez).
    val tipografia = remember(context) {
        val base = ResourcesCompat.getFont(context, R.font.worksans_variable) ?: Typeface.DEFAULT
        Typeface.create(base, 500, false)
    }
    val giro = rememberInfiniteTransition(label = "sello")
    val angulo by giro.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(Movimiento.VUELTA_SELLO, easing = LinearEasing), RepeatMode.Restart),
        label = "anguloSello",
    )

    Canvas(
        modifier
            .size(120.dp)
            .semantics { contentDescription = descripcion }
            .graphicsLayer { rotationZ = if (reducidas) 0f else angulo },
    ) {
        val grosor = Bordes.sello.toPx()
        val radio = size.minDimension / 2 - grosor / 2
        drawCircle(color = color, radius = radio, style = Stroke(width = grosor))

        val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb()
            textSize = 12.sp.toPx()
            typeface = tipografia
            letterSpacing = 0.1f
        }
        // Círculo interior por el que "camina" el texto.
        val radioTexto = radio - 16.dp.toPx()
        val camino = Path().apply { addCircle(center.x, center.y, radioTexto, Path.Direction.CW) }
        drawIntoCanvas { it.nativeCanvas.drawTextOnPath(texto, camino, 0f, 0f, pincel) }
    }
}

@PreviewLightDark
@Composable
private fun SelloDestacadoPreview() {
    VistaPrevia { SelloDestacado() }
}
