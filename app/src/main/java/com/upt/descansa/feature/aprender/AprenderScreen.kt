package com.upt.descansa.feature.aprender

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.designsystem.TituloPantalla
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa

private data class Articulo(@StringRes val titulo: Int, @StringRes val detalle: Int, val icono: ImageVector, val color: Color)

private val articulos = listOf(
    Articulo(R.string.aprender_por_que, R.string.aprender_por_que_detalle, Icons.Outlined.Bedtime, Lavanda),
    Articulo(R.string.aprender_etapas, R.string.aprender_etapas_detalle, Icons.Outlined.Waves, Turquesa),
    Articulo(R.string.aprender_habitos, R.string.aprender_habitos_detalle, Icons.Outlined.Check, Lavanda),
    Articulo(R.string.aprender_mente, R.string.aprender_mente_detalle, Icons.AutoMirrored.Outlined.Notes, Turquesa),
)

/**
 * Pantalla 10 · Aprender.
 *
 * FASE 0: tarjetas con los títulos del prototipo. En la Fase 6 el contenido
 * vendrá de `assets/content/learn.json` (con fuentes citadas y la marca
 * "pendiente de revisión por el equipo de psicología") y cada tarjeta abrirá su artículo.
 */
@Composable
fun AprenderScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TituloPantalla(stringResource(R.string.aprender_titulo))
        TarjetaDescansa(Modifier.fillMaxWidth(), seleccionada = true) {
            EtiquetaSeccion(stringResource(R.string.aprender_consejo_hoy))
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.aprender_consejo_texto), style = MaterialTheme.typography.bodyLarge, color = Texto)
        }
        articulos.forEach { articulo ->
            TarjetaDescansa(Modifier.fillMaxWidth(), onClick = { /* Fase 6: abrir artículo */ }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .background(SuperficieSecundaria, MaterialTheme.shapes.small),
                    ) {
                        Icon(articulo.icono, contentDescription = null, tint = articulo.color)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(articulo.titulo), style = MaterialTheme.typography.titleMedium, color = Texto)
                        Text(stringResource(articulo.detalle), style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
                    }
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = TextoAtenuado)
                }
            }
        }
        // Aviso permanente (sección 14).
        Text(stringResource(R.string.aviso_no_medico), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 1000)
@Composable
private fun AprenderPreview() {
    DescansaTheme { AprenderScreen() }
}
