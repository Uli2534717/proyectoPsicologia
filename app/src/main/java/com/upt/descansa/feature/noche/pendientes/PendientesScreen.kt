package com.upt.descansa.feature.noche.pendientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.AvisoTarjeta
import com.upt.descansa.core.designsystem.FormaPildora
import com.upt.descansa.core.designsystem.PlantillaPaso
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.theme.Borde
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.TextoSobreLavanda
import com.upt.descansa.data.local.entity.MAX_CARACTERES_PENDIENTE

/** Un pendiente en pantalla. */
data class PendienteUi(val id: Long, val texto: String, val hecho: Boolean)

/**
 * Pantalla 3 · Pendientes (paso 1 de 5).
 *
 * FASE 0: la lista vive solo en memoria para poder navegar el flujo.
 * En la Fase 4 se conecta a la tabla `pendientes` (se guarda al instante,
 * sobrevive al cierre de la app y aparece en el resumen de la mañana).
 */
@Composable
fun PendientesScreen(onAtras: () -> Unit, onSiguiente: () -> Unit) {
    val pendientes = remember { mutableStateListOf<PendienteUi>() }
    PendientesContenido(
        pendientes = pendientes,
        onAgregar = { texto -> pendientes.add(PendienteUi(id = System.nanoTime(), texto = texto, hecho = false)) },
        onMarcar = { p -> pendientes[pendientes.indexOf(p)] = p.copy(hecho = !p.hecho) },
        onAtras = onAtras,
        onSiguiente = onSiguiente,
    )
}

@Composable
fun PendientesContenido(
    pendientes: List<PendienteUi>,
    onAgregar: (String) -> Unit,
    onMarcar: (PendienteUi) -> Unit,
    onAtras: () -> Unit,
    onSiguiente: () -> Unit,
) {
    var texto by rememberSaveable { mutableStateOf("") }
    val agregar = {
        if (texto.isNotBlank()) {
            onAgregar(texto.trim())
            texto = ""
        }
    }
    PlantillaPaso(
        paso = 1,
        titulo = stringResource(R.string.pendientes_titulo),
        subtitulo = stringResource(R.string.pendientes_subtitulo),
        textoBoton = stringResource(R.string.pendientes_boton),
        onAtras = onAtras,
        onBoton = onSiguiente,
        onOmitir = onSiguiente,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = texto,
                // Límite de 280 caracteres (sección 8).
                onValueChange = { if (it.length <= MAX_CARACTERES_PENDIENTE) texto = it },
                placeholder = { Text(stringResource(R.string.pendientes_campo), color = TextoAtenuado, maxLines = 1) },
                singleLine = true,
                shape = FormaPildora,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { agregar() }),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SuperficieSecundaria,
                    focusedContainerColor = SuperficieSecundaria,
                    unfocusedBorderColor = Borde,
                    focusedBorderColor = Lavanda,
                ),
                modifier = Modifier.weight(1f),
            )
            FilledIconButton(
                onClick = agregar,
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Lavanda, contentColor = TextoSobreLavanda),
                modifier = Modifier.size(56.dp),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.pendientes_agregar))
            }
        }
        pendientes.forEach { pendiente ->
            TarjetaDescansa(modifier = Modifier.fillMaxWidth(), onClick = { onMarcar(pendiente) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // onCheckedChange = null: el clic lo maneja toda la tarjeta (área táctil grande).
                    Checkbox(
                        checked = pendiente.hecho,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = Lavanda, uncheckedColor = TextoAtenuado),
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(pendiente.texto, style = MaterialTheme.typography.bodyLarge, color = Texto)
                }
            }
        }
        if (pendientes.isNotEmpty()) {
            AvisoTarjeta(icono = Icons.Outlined.Check, texto = stringResource(R.string.pendientes_tranquilizador))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun PendientesPreview() {
    DescansaTheme {
        PendientesContenido(
            pendientes = listOf(
                PendienteUi(1, "Entregar el avance del proyecto", false),
                PendienteUi(2, "Comprar material para el laboratorio", false),
                PendienteUi(3, "Responder el correo del asesor", false),
            ),
            onAgregar = {},
            onMarcar = {},
            onAtras = {},
            onSiguiente = {},
        )
    }
}
