package com.upt.descansa.core.designsystem.components

import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel

/**
 * Imagen principal de una sección: cuadrada, esquinas de 16 dp y contorno de 1.5 dp.
 *
 * - Si en `res/drawable/` hay una FOTO (.webp, .png, .jpg) con ese nombre, se muestra la foto.
 * - Si solo está el marcador de prueba (.xml), se dibuja el objeto con un ícono
 *   de línea en morado sobre un fondo lila (ver [Ilustraciones]).
 * Así el equipo solo tiene que poner la foto con el mismo nombre (docs/IMAGENES.md).
 *
 * @param descripcion texto para TalkBack, en español (obligatorio).
 */
@Composable
fun ImagenCartel(
    @DrawableRes id: Int,
    descripcion: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // ¿El recurso es el marcador .xml (todavía no hay foto)?
    val esMarcador = remember(id) {
        val valor = TypedValue()
        context.resources.getValue(id, valor, true)
        valor.string?.endsWith(".xml") == true
    }
    val dibujo = if (esMarcador) Ilustraciones.para(id) else null
    val marco = modifier
        .aspectRatio(1f)
        .clip(Formas.tarjeta)
        .border(Bordes.normal, MaterialTheme.colorScheme.outline, Formas.tarjeta)

    if (dibujo != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = marco
                .background(LocalColoresCartel.current.fondoIlustracion)
                .semantics { contentDescription = descripcion },
        ) {
            Icon(
                imageVector = dibujo,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
            )
        }
    } else {
        Image(
            painter = painterResource(id),
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            modifier = marco,
        )
    }
}

@PreviewLightDark
@Composable
private fun ImagenCartelPreview() {
    VistaPrevia { ImagenCartel(R.drawable.img_alarma_despertador, "Un despertador", Modifier.width(140.dp)) }
}
