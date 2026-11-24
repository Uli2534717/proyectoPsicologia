package com.upt.descansa.core.designsystem

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.theme.Borde
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado

/** Número total de pasos del flujo "Preparar mi noche". */
const val TOTAL_PASOS_NOCHE = 5

/**
 * Plantilla común de los 5 pasos de "Preparar mi noche" (pantallas 3 a 7).
 *
 * Arriba: flecha atrás, "Paso N de 5" y barra de progreso segmentada.
 * En medio: título, subtítulo y el contenido de cada paso (con scroll).
 * Abajo: enlace "Omitir paso" (el usuario no está obligado a hacer los 5)
 * y el botón principal.
 *
 * Cada paso solo aporta su contenido; así los 5 se ven idénticos.
 */
@Composable
fun PlantillaPaso(
    paso: Int,
    titulo: String,
    textoBoton: String,
    onAtras: () -> Unit,
    onBoton: () -> Unit,
    onOmitir: (() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    accionesInferiores: (@Composable RowScope.() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        EncabezadoPaso(paso = paso, onAtras = onAtras)
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TituloPantalla(titulo = titulo, subtitulo = subtitulo)
            contenido()
        }
        if (onOmitir != null) {
            TextButton(
                onClick = onOmitir,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .heightIn(min = AltoMinimoTactil),
            ) {
                Text(stringResource(R.string.paso_omitir), color = TextoAtenuado, style = MaterialTheme.typography.labelMedium)
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            accionesInferiores?.invoke(this)
            BotonPrincipal(texto = textoBoton, onClick = onBoton, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun EncabezadoPaso(paso: Int, onAtras: () -> Unit) {
    val textoPaso = stringResource(R.string.paso_n_de_total, paso, TOTAL_PASOS_NOCHE)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onAtras) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBackIos,
                    contentDescription = stringResource(R.string.accion_atras),
                    tint = Texto,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(textoPaso, style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        }
        Spacer(Modifier.height(12.dp))
        // Barra de progreso segmentada: un segmento por paso, lavanda si ya se llegó.
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                // TalkBack lee "Paso 2 de 5" en vez de 5 cajas sin sentido.
                .semantics { contentDescription = textoPaso },
        ) {
            repeat(TOTAL_PASOS_NOCHE) { indice ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(if (indice < paso) Lavanda else Borde, RoundedCornerShape(3.dp)),
                )
            }
        }
    }
}
