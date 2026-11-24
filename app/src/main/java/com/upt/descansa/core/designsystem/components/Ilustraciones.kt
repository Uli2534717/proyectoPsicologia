package com.upt.descansa.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import com.upt.descansa.R

/**
 * Dibujo de cada objeto mientras no haya fotografía real (D-22).
 *
 * Son íconos OFICIALES de Material (línea), dibujados grandes y en morado
 * dentro del marco de la imagen. Cuando el equipo agregue la foto `.webp`
 * con el mismo nombre (ver docs/IMAGENES.md), [ImagenCartel] muestra la foto
 * y este dibujo deja de usarse, sin cambiar código.
 */
object Ilustraciones {

    private val porImagen: Map<Int, ImageVector> = mapOf(
        R.drawable.img_bienvenida_lampara_luna to Icons.Outlined.Bedtime,
        R.drawable.img_inicio_almohada to Icons.Outlined.Bed,
        R.drawable.img_vaciado_cuaderno to Icons.Outlined.EditNote,
        R.drawable.img_respiracion_vela to Icons.Outlined.Air,
        R.drawable.img_relajacion_manta to Icons.Outlined.SelfImprovement,
        R.drawable.img_sonido_lluvia to Icons.Outlined.Umbrella,
        R.drawable.img_sonido_ruido_blanco to Icons.Outlined.GraphicEq,
        R.drawable.img_sonido_olas to Icons.Outlined.Waves,
        R.drawable.img_sonido_bosque to Icons.Outlined.Forest,
        R.drawable.img_alarma_despertador to Icons.Outlined.Alarm,
        R.drawable.img_modo_noche_lampara to Icons.Outlined.NightsStay,
        R.drawable.img_manana_taza to Icons.Outlined.LocalCafe,
        R.drawable.img_aprender_lampara to Icons.Outlined.Lightbulb,
        R.drawable.img_aprender_reloj_arena to Icons.Outlined.HourglassEmpty,
        R.drawable.img_aprender_te to Icons.Outlined.EmojiFoodBeverage,
        R.drawable.img_aprender_cuaderno to Icons.Outlined.Psychology,
        R.drawable.img_ajustes_audifonos to Icons.Outlined.Headphones,
    )

    /** El dibujo que corresponde a una imagen, o null si no tiene. */
    fun para(@DrawableRes imagen: Int): ImageVector? = porImagen[imagen]
}
