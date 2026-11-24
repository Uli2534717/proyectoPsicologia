package com.upt.descansa.feature.noche.pendientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AvisoCartel
import com.upt.descansa.core.designsystem.components.BotonContorno
import com.upt.descansa.core.designsystem.components.PlantillaPaso
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.data.local.entity.MAX_CARACTERES_PENDIENTE

/** Un pendiente en pantalla. */
data class PendienteUi(val id: Long, val texto: String, val hecho: Boolean)

/**
 * Pantalla 3 · Pendientes, paso 1 de 5 (sección turquesa: SUELTA).
 *
 * Todavía en memoria: la Fase 4 lo conecta a la tabla `pendientes` y agrega
 * la animación de "soltar" (la entrada sube y se desvanece y aparece SUELTA).
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
    val esquema = MaterialTheme.colorScheme
    PlantillaPaso(
        paso = 1,
        palabra = stringResource(R.string.palabra_pendientes),
        colorPalabra = LocalColoresCartel.current.turquesa,
        titulo = stringResource(R.string.pendientes_titulo),
        descripcion = stringResource(R.string.pendientes_subtitulo),
        imagen = R.drawable.img_vaciado_cuaderno,
        descripcionImagen = stringResource(R.string.img_vaciado_cuaderno),
        textoBoton = stringResource(R.string.pendientes_boton),
        onAtras = onAtras,
        onBoton = onSiguiente,
        onOmitir = onSiguiente,
    ) {
        OutlinedTextField(
            value = texto,
            // Límite de 280 caracteres (sección 8).
            onValueChange = { if (it.length <= MAX_CARACTERES_PENDIENTE) texto = it },
            label = { Text(stringResource(R.string.pendientes_campo)) },
            textStyle = TipoCartel.cuerpo,
            shape = Formas.tarjeta,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { agregar() }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = esquema.outline,
                focusedBorderColor = esquema.primary,
                focusedLabelColor = esquema.primary,
                cursorColor = esquema.primary,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        BotonContorno(stringResource(R.string.pendientes_agregar), onClick = agregar, modifier = Modifier.fillMaxWidth())
        pendientes.forEach { pendiente ->
            TarjetaCartel(Modifier.fillMaxWidth(), onClick = { onMarcar(pendiente) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                    // onCheckedChange = null: el clic lo maneja toda la tarjeta (área táctil grande).
                    Checkbox(
                        checked = pendiente.hecho,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = esquema.primary, uncheckedColor = esquema.outline),
                    )
                    Spacer(Modifier.width(Espacio.l))
                    Text(pendiente.texto, style = TipoCartel.cuerpo, color = esquema.onBackground)
                }
            }
        }
        if (pendientes.isNotEmpty()) {
            AvisoCartel(stringResource(R.string.pendientes_tranquilizador), icono = Icons.Outlined.Check)
        }
    }
}

@PreviewLightDark
@Composable
private fun PendientesPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) {
        PendientesContenido(
            pendientes = listOf(
                PendienteUi(1, "Entregar el avance del proyecto", false),
                PendienteUi(2, "Responder el correo del asesor", true),
            ),
            onAgregar = {}, onMarcar = {}, onAtras = {}, onSiguiente = {},
        )
    }
}
