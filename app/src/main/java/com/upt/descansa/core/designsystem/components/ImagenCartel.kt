package com.upt.descansa.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Formas

/**
 * Fotografía de un objeto sobre fondo blanco (almohada, taza, despertador...),
 * cuadrada, con esquinas de 4 dp y contorno de 1.5 dp.
 * Las imágenes y su foto definitiva están listadas en docs/IMAGENES.md.
 *
 * @param descripcion texto para TalkBack, en español (obligatorio).
 */
@Composable
fun ImagenCartel(
    @DrawableRes id: Int,
    descripcion: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id),
        contentDescription = descripcion,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(1f)
            .clip(Formas.tarjeta)
            .border(Bordes.normal, MaterialTheme.colorScheme.onBackground, Formas.tarjeta),
    )
}

@PreviewLightDark
@Composable
private fun ImagenCartelPreview() {
    VistaPrevia { ImagenCartel(R.drawable.img_sonido_lluvia, "Lluvia", Modifier.width(140.dp)) }
}
