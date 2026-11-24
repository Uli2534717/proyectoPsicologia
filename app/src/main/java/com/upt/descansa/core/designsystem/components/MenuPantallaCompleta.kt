package com.upt.descansa.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.upt.descansa.core.designsystem.theme.LocalEsNocturno
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel

/** Una sección del menú: nombre, color de su palabra cartel y qué hacer al tocarla. */
data class EntradaMenu(val titulo: String, val color: Color, val onClick: () -> Unit)

/**
 * Menú a pantalla completa: lienzo liso con los nombres de las secciones en
 * 32 sp (peso 900), cada uno con un punto de su color de sección.
 * REEMPLAZA a la barra de navegación inferior.
 *
 * Se muestra como `Dialog` a pantalla completa: así el botón Atrás del sistema
 * lo cierra sin código extra.
 */
@Composable
fun MenuPantallaCompleta(
    entradas: List<EntradaMenu>,
    onCerrar: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        // El Dialog tiene su PROPIA ventana: hay que decirle también de qué color
        // van los íconos de la barra de estado (oscuros sobre lienzo blanco).
        val nocturno = LocalEsNocturno.current
        val vista = LocalView.current
        SideEffect {
            val ventana = (vista.parent as? DialogWindowProvider)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(ventana, vista).apply {
                isAppearanceLightStatusBars = !nocturno
                isAppearanceLightNavigationBars = !nocturno
            }
        }
        ContenidoMenu(entradas, onCerrar)
    }
}

@Composable
private fun ContenidoMenu(entradas: List<EntradaMenu>, onCerrar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = Espacio.margenPantalla),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MenuMinimo(
                onClick = onCerrar,
                texto = stringResource(R.string.menu_cerrar),
                descripcion = stringResource(R.string.menu_cerrar),
            )
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Espacio.xs, Alignment.CenterVertically),
        ) {
            entradas.forEachIndexed { indice, entrada -> FilaMenu(entrada, indice) }
        }
    }
}

/** Cada sección entra desde abajo, escalonada 50 ms (sin animación si están reducidas). */
@Composable
private fun FilaMenu(entrada: EntradaMenu, indice: Int) {
    val reducidas = animacionesReducidas()
    val progreso = remember { Animatable(if (reducidas) 1f else 0f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(1f, Movimiento.entrada(Movimiento.MEDIA, retraso = indice * Movimiento.ESCALON))
    }
    val desplazamiento = with(LocalDensity.current) { 20.dp.toPx() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AltoMinimoTactil)
            .graphicsLayer {
                alpha = progreso.value
                translationY = (1f - progreso.value) * desplazamiento
            }
            .clickable(onClick = entrada.onClick)
            .semantics { role = Role.Button }
            .padding(vertical = Espacio.xs),
    ) {
        Canvas(Modifier.size(Espacio.m)) { drawCircle(entrada.color) }
        Spacer(Modifier.width(Espacio.l))
        Text(entrada.titulo, style = TipoCartel.subtitulo, color = MaterialTheme.colorScheme.onBackground)
    }
}

@PreviewLightDark
@Composable
private fun MenuPantallaCompletaPreview() {
    VistaPrevia {
        val colores = LocalColoresCartel.current
        ContenidoMenu(
            entradas = listOf(
                EntradaMenu("Inicio", colores.amarillo) {},
                EntradaMenu("Preparar mi noche", colores.turquesa) {},
                EntradaMenu("Aprender", colores.amarillo) {},
            ),
            onCerrar = {},
        )
    }
}
