package com.upt.descansa.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.TipoCartel

// =============================================================================
//  Botones "Cartel sereno": SOLO contorno, NUNCA relleno de color.
//  Al presionar, el borde engrosa de 1.5 a 2.5 dp y el botón se reduce a 0.97.
// =============================================================================

/** Alto mínimo de cualquier objetivo táctil (accesibilidad). */
val AltoMinimoTactil = 48.dp

/**
 * Base común de BotonContorno, BotonPildora y BotonSecundario (cada uno en su archivo). Dibuja el contorno, anima la presión y
 * marca el elemento como botón para TalkBack.
 */
@Composable
internal fun BotonBase(
    texto: String,
    onClick: () -> Unit,
    colorBorde: Color,
    colorTexto: Color,
    forma: Shape,
    relleno: PaddingValues,
    modifier: Modifier,
    habilitado: Boolean,
    icono: ImageVector?,
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val grosor by animateDpAsState(
        targetValue = if (presionado) Bordes.presionado else Bordes.normal,
        animationSpec = Movimiento.entrada(Movimiento.CORTA),
        label = "grosorBorde",
    )
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = Movimiento.entrada(Movimiento.CORTA),
        label = "escalaBoton",
    )
    // Deshabilitado: mismo estilo al 40 % de opacidad (nunca gris de relleno).
    val alfa = if (habilitado) 1f else 0.4f
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .scale(escala)
            .heightIn(min = AltoMinimoTactil)
            .clip(forma)
            .border(grosor, colorBorde.copy(alpha = alfa), forma)
            .clickable(
                interactionSource = interaccion,
                // Sin onda de color: la respuesta al toque es el borde y la escala.
                indication = null,
                enabled = habilitado,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(relleno),
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = colorTexto.copy(alpha = alfa), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Espacio.xs))
        }
        Text(
            text = texto.uppercase(),
            style = TipoCartel.etiqueta.copy(fontSize = TipoCartel.cuerpo.fontSize),
            color = colorTexto.copy(alpha = alfa),
            textAlign = TextAlign.Center,
        )
    }
}
