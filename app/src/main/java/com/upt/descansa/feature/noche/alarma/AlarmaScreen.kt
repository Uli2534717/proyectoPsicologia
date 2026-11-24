package com.upt.descansa.feature.noche.alarma

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.AvisoTarjeta
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.FilaChips
import com.upt.descansa.core.designsystem.PlantillaPaso
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Fondo
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa
import com.upt.descansa.core.util.formatearHora
import com.upt.descansa.core.util.inicioVentana
import com.upt.descansa.domain.model.FuenteDatos

/** Ventanas de despertar permitidas (sección 6.1). */
private val ventanas = listOf(15, 20, 30)

/** Pantalla 7 · Alarma inteligente (paso 5 de 5). */
@Composable
fun AlarmaScreen(
    onAtras: () -> Unit,
    onActivarModoNoche: () -> Unit,
    viewModel: AlarmaViewModel = hiltViewModel(),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    AlarmaContenido(
        estado = estado,
        onAtras = onAtras,
        onActivarModoNoche = onActivarModoNoche,
        onCambiarHora = viewModel::alCambiarHoraLimite,
        onCambiarVentana = viewModel::alCambiarVentana,
        onCambiarFuente = viewModel::alCambiarFuente,
        onCambiarNoMolestar = viewModel::alCambiarNoMolestar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmaContenido(
    estado: AlarmaUiState,
    onAtras: () -> Unit,
    onActivarModoNoche: () -> Unit,
    onCambiarHora: (Int) -> Unit,
    onCambiarVentana: (Int) -> Unit,
    onCambiarFuente: (FuenteDatos) -> Unit,
    onCambiarNoMolestar: (Boolean) -> Unit,
) {
    var eligiendoHora by rememberSaveable { mutableStateOf(false) }
    val horaLimite = formatearHora(estado.horaLimiteMinutos)
    val desde = formatearHora(inicioVentana(estado.horaLimiteMinutos, estado.ventanaMinutos))

    PlantillaPaso(
        paso = 5,
        titulo = stringResource(R.string.alarma_titulo),
        textoBoton = stringResource(R.string.alarma_activar),
        onAtras = onAtras,
        onBoton = onActivarModoNoche,
        // El último paso no se omite: es el que activa la noche.
        onOmitir = null,
    ) {
        // Tarjeta de hora límite: al tocarla se abre el selector de hora.
        TarjetaDescansa(modifier = Modifier.fillMaxWidth(), onClick = { eligiendoHora = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    EtiquetaSeccion(stringResource(R.string.alarma_hora_limite))
                    Text(stringResource(R.string.alarma_puede_sonar_desde, desde), style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
                }
                Text(horaLimite, style = MaterialTheme.typography.displaySmall, color = Texto)
            }
        }

        Text(stringResource(R.string.alarma_ventana), style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        FilaChips(
            opciones = ventanas,
            seleccionada = estado.ventanaMinutos,
            etiqueta = { stringResource(R.string.minutos_corto, it) },
            onSeleccionar = onCambiarVentana,
        )

        Text(stringResource(R.string.alarma_fuente), style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        OpcionFuente(
            icono = Icons.Outlined.PhoneAndroid,
            colorIcono = Lavanda,
            titulo = stringResource(R.string.alarma_fuente_telefono),
            detalle = stringResource(R.string.alarma_fuente_telefono_detalle),
            seleccionada = estado.fuente == FuenteDatos.ACELEROMETRO_TELEFONO,
            onClick = { onCambiarFuente(FuenteDatos.ACELEROMETRO_TELEFONO) },
        )
        OpcionFuente(
            icono = Icons.Outlined.Watch,
            colorIcono = Turquesa,
            titulo = stringResource(R.string.alarma_fuente_reloj),
            detalle = stringResource(R.string.alarma_fuente_reloj_detalle),
            seleccionada = estado.fuente == FuenteDatos.SMARTWATCH,
            onClick = { onCambiarFuente(FuenteDatos.SMARTWATCH) },
        )

        // Principio "la alarma nunca falla": siempre se avisa del respaldo.
        AvisoTarjeta(icono = Icons.Outlined.ErrorOutline, texto = stringResource(R.string.alarma_respaldo))

        TarjetaDescansa(
            modifier = Modifier
                .fillMaxWidth()
                // Toda la tarjeta cambia el interruptor (área táctil grande).
                .toggleable(value = estado.noMolestar, role = Role.Switch, onValueChange = onCambiarNoMolestar),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.permiso_no_molestar), style = MaterialTheme.typography.titleMedium, color = Texto)
                    Text(stringResource(R.string.alarma_no_molestar_detalle), style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
                }
                Switch(
                    checked = estado.noMolestar,
                    onCheckedChange = null,
                    colors = SwitchDefaults.colors(checkedTrackColor = Turquesa, checkedThumbColor = Fondo),
                )
            }
        }
    }

    if (eligiendoHora) {
        val estadoReloj = rememberTimePickerState(
            initialHour = estado.horaLimiteMinutos / 60,
            initialMinute = estado.horaLimiteMinutos % 60,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { eligiendoHora = false },
            confirmButton = {
                TextButton(onClick = {
                    onCambiarHora(estadoReloj.hour * 60 + estadoReloj.minute)
                    eligiendoHora = false
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { eligiendoHora = false }) { Text(stringResource(R.string.accion_cancelar)) }
            },
            title = { Text(stringResource(R.string.alarma_cambiar_hora)) },
            text = { TimePicker(state = estadoReloj) },
        )
    }
}

@Composable
private fun OpcionFuente(
    icono: ImageVector,
    colorIcono: Color,
    titulo: String,
    detalle: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
) {
    TarjetaDescansa(modifier = Modifier.fillMaxWidth(), seleccionada = seleccionada, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = colorIcono)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = Texto)
                Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 1000)
@Composable
private fun AlarmaPreview() {
    DescansaTheme {
        AlarmaContenido(AlarmaUiState(), {}, {}, {}, {}, {}, {})
    }
}
