package com.upt.descansa.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TemaNocturno

/**
 * Cómo abrir el menú de pantalla completa. Lo provee la navegación; si es null
 * (por ejemplo, en un @Preview o en la pantalla de timbre), no se muestra "MENÚ".
 */
val LocalAbrirMenu = staticCompositionLocalOf<(() -> Unit)?> { null }

/**
 * Estructura base de TODA pantalla "Cartel sereno":
 * 1. Encabezado: atrás (si aplica) o logotipo, y MENÚ arriba a la derecha.
 * 2. La palabra cartel de la sección, con la imagen principal encimada.
 * 3. El título real (accesible) y el contenido, con scroll.
 * 4. Un solo botón principal fijo abajo (`accionPrincipal`).
 *
 * @param nocturna fuerza el tema nocturno (Respiración, Modo noche, timbre).
 * @param encabezadoInicio contenido a la izquierda del encabezado (p. ej. Logotipo);
 *        si es null y hay `onAtras`, se muestra la flecha de regreso.
 */
@Composable
fun PantallaCartel(
    palabra: String,
    colorPalabra: Color,
    titulo: String,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    tamano: TamanoCartel = TamanoCartel.NORMAL,
    @DrawableRes imagen: Int? = null,
    descripcionImagen: String? = null,
    onAtras: (() -> Unit)? = null,
    nocturna: Boolean = false,
    modificadorPalabra: Modifier = Modifier,
    encabezadoInicio: (@Composable RowScope.() -> Unit)? = null,
    encabezadoExtra: (@Composable ColumnScope.() -> Unit)? = null,
    accionPrincipal: (@Composable ColumnScope.() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val cuerpo: @Composable () -> Unit = {
        Column(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding(),
        ) {
            EncabezadoCartel(onAtras = onAtras, inicio = encabezadoInicio)
            encabezadoExtra?.let { Column(Modifier.padding(horizontal = Espacio.margenPantalla), content = it) }
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Espacio.margenPantalla),
                verticalArrangement = Arrangement.spacedBy(Espacio.entreElementos),
            ) {
                EscenaCartel(palabra, colorPalabra, tamano, imagen, descripcionImagen, modificadorPalabra)
                TituloPantalla(titulo, descripcion = descripcion)
                contenido()
                Spacer(Modifier.padding(bottom = Espacio.xl))
            }
            if (accionPrincipal != null) {
                Column(
                    Modifier.padding(horizontal = Espacio.margenPantalla, vertical = Espacio.l),
                    verticalArrangement = Arrangement.spacedBy(Espacio.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = accionPrincipal,
                )
            }
        }
    }
    if (nocturna) TemaNocturno(cuerpo) else cuerpo()
}

/** Encabezado mínimo: sin barra de fondo, sin sombra. */
@Composable
fun EncabezadoCartel(
    onAtras: (() -> Unit)?,
    modifier: Modifier = Modifier,
    inicio: (@Composable RowScope.() -> Unit)? = null,
) {
    val abrirMenu = LocalAbrirMenu.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Espacio.xs, vertical = Espacio.xxs),
    ) {
        when {
            inicio != null -> {
                Spacer(Modifier.width(Espacio.m))
                inicio()
            }
            onAtras != null -> IconButton(onClick = onAtras) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.accion_atras),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.weight(1f))
        if (abrirMenu != null) MenuMinimo(onClick = abrirMenu)
    }
}

/**
 * La palabra cartel con la imagen principal encimada abajo a la derecha, como
 * en un cartel impreso. Primero se "pega" la palabra (600 ms) y después
 * aparece la imagen (300 ms).
 */
@Composable
fun EscenaCartel(
    palabra: String,
    color: Color,
    tamano: TamanoCartel = TamanoCartel.NORMAL,
    @DrawableRes imagen: Int? = null,
    descripcionImagen: String? = null,
    modificadorPalabra: Modifier = Modifier,
) {
    Box(Modifier.fillMaxWidth()) {
        PalabraCartel(palabra, color, modifier = modificadorPalabra, tamano = tamano)
        if (imagen != null && descripcionImagen != null) {
            val reducidas = animacionesReducidas()
            val alfa = remember { Animatable(if (reducidas) 1f else 0f) }
            LaunchedEffect(Unit) {
                alfa.animateTo(1f, Movimiento.entrada(Movimiento.MEDIA, retraso = Movimiento.LARGA / 2))
            }
            ImagenCartel(
                imagen,
                descripcionImagen,
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = if (tamano == TamanoCartel.XL) 56.dp else 44.dp)
                    .width(128.dp)
                    .graphicsLayer { alpha = alfa.value },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PantallaCartelPreview() {
    VistaPrevia {
        PantallaCartel(
            palabra = "SUEÑA",
            colorPalabra = LocalColoresCartel.current.noche,
            titulo = "Elige tu sonido",
            imagen = R.drawable.img_sonido_lluvia,
            descripcionImagen = "Lluvia",
            onAtras = {},
            accionPrincipal = { BotonPildora("Continuar", onClick = {}) },
        ) {
            AvisoCartel("Contenido de ejemplo.")
        }
    }
}
