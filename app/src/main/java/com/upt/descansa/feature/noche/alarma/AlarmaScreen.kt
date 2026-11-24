package com.upt.descansa.feature.noche.alarma

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.AvisoCartel
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.FilaInterruptor
import com.upt.descansa.core.designsystem.components.PlantillaPaso
import com.upt.descansa.core.designsystem.components.SelectorOpciones
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.core.util.formatearHora
import com.upt.descansa.core.util.inicioVentana
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.sistema.Permisos
import kotlinx.coroutines.launch

/** Ventanas de despertar permitidas (sección 6.1). */
private val ventanas = listOf(15, 20, 30)

/** Pantalla 7 · Alarma inteligente, paso 5 de 5 (sección turquesa: DESPIERTA). */
@Composable
fun AlarmaScreen(
    onAtras: () -> Unit,
    onActivarModoNoche: () -> Unit,
    viewModel: AlarmaViewModel = hiltViewModel(),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val alcance = rememberCoroutineScope()
    var dialogo by rememberSaveable { mutableStateOf(DialogoPermiso.NINGUNO) }
    var errorExactas by rememberSaveable { mutableStateOf(false) }

    // Paso final: programar la alarma y, si Android no deja la pantalla
    // completa, ofrecer activarla (sin bloquear: la alarma suena igual).
    val activar: () -> Unit = {
        alcance.launch {
            when (viewModel.activarModoNoche()) {
                ResultadoActivacion.SinPermisoExactas -> errorExactas = true
                is ResultadoActivacion.Programada -> {
                    errorExactas = false
                    if (Permisos.puedeUsarPantallaCompleta(context)) onActivarModoNoche() else dialogo = DialogoPermiso.PANTALLA_COMPLETA
                }
            }
        }
    }
    // Se pide el permiso de notificaciones; con o sin él, se activa la noche.
    val pedirNotificaciones = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { activar() }

    AlarmaContenido(
        estado = estado,
        onAtras = onAtras,
        onActivarModoNoche = {
            if (Permisos.faltaNotificaciones(context)) dialogo = DialogoPermiso.NOTIFICACIONES else activar()
        },
        onCambiarHora = viewModel::alCambiarHoraLimite,
        onCambiarVentana = viewModel::alCambiarVentana,
        onCambiarFuente = viewModel::alCambiarFuente,
        onCambiarNoMolestar = viewModel::alCambiarNoMolestar,
        error = if (errorExactas) stringResource(R.string.alarma_error_exactas) else null,
    )

    when (dialogo) {
        DialogoPermiso.NINGUNO -> Unit
        // Explicación ANTES del diálogo del sistema (sección 14).
        DialogoPermiso.NOTIFICACIONES -> DialogoExplicacion(
            titulo = stringResource(R.string.permiso_notif_titulo),
            texto = stringResource(R.string.permiso_notif_texto),
            textoAceptar = stringResource(R.string.permiso_permitir),
            onAceptar = {
                dialogo = DialogoPermiso.NINGUNO
                pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onRechazar = {
                dialogo = DialogoPermiso.NINGUNO
                activar()
            },
        )
        DialogoPermiso.PANTALLA_COMPLETA -> DialogoExplicacion(
            titulo = stringResource(R.string.permiso_fsi_titulo),
            texto = stringResource(R.string.permiso_fsi_texto),
            textoAceptar = stringResource(R.string.permiso_abrir_ajustes),
            onAceptar = {
                dialogo = DialogoPermiso.NINGUNO
                Permisos.abrirAjustePantallaCompleta(context)
                onActivarModoNoche()
            },
            onRechazar = {
                dialogo = DialogoPermiso.NINGUNO
                onActivarModoNoche()
            },
        )
    }
}

private enum class DialogoPermiso { NINGUNO, NOTIFICACIONES, PANTALLA_COMPLETA }

/** Diálogo "Cartel sereno": sin elevación ni sombra, con contorno. */
@Composable
private fun DialogoExplicacion(
    titulo: String,
    texto: String,
    textoAceptar: String,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onRechazar,
        title = { Text(titulo, style = TipoCartel.subtitulo.copy(fontSize = TipoCartel.titulo.fontSize)) },
        text = { Text(texto, style = TipoCartel.cuerpo) },
        confirmButton = { TextButton(onClick = onAceptar) { Text(textoAceptar.uppercase(), style = TipoCartel.etiqueta) } },
        dismissButton = {
            TextButton(onClick = onRechazar) { Text(stringResource(R.string.permiso_ahora_no).uppercase(), style = TipoCartel.etiqueta) }
        },
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.border(Bordes.normal, MaterialTheme.colorScheme.outline, Formas.tarjeta),
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
    error: String? = null,
) {
    var eligiendoHora by rememberSaveable { mutableStateOf(false) }
    val horaLimite = formatearHora(estado.horaLimiteMinutos)
    val desde = formatearHora(inicioVentana(estado.horaLimiteMinutos, estado.ventanaMinutos))
    val esquema = MaterialTheme.colorScheme

    PlantillaPaso(
        paso = 5,
        palabra = stringResource(R.string.palabra_alarma),
        colorPalabra = LocalColoresCartel.current.turquesa,
        titulo = stringResource(R.string.alarma_titulo),
        imagen = R.drawable.img_alarma_despertador,
        descripcionImagen = stringResource(R.string.img_alarma_despertador),
        textoBoton = stringResource(R.string.alarma_activar),
        onAtras = onAtras,
        onBoton = onActivarModoNoche,
        // El último paso no se omite: es el que activa la noche (D-07).
        onOmitir = null,
    ) {
        // Hora límite: al tocarla se abre el selector de hora.
        TarjetaCartel(Modifier.fillMaxWidth(), onClick = { eligiendoHora = true }) {
            EtiquetaCategoria(stringResource(R.string.alarma_hora_limite), adorno = AdornoEtiqueta.CHEVRON)
            Text(horaLimite, style = TipoCartel.hora, color = esquema.onBackground)
            Text(stringResource(R.string.alarma_puede_sonar_desde, desde), style = TipoCartel.cuerpo, color = esquema.onBackground)
        }

        EtiquetaCategoria(stringResource(R.string.alarma_ventana))
        SelectorOpciones(
            opciones = ventanas,
            seleccionada = estado.ventanaMinutos,
            etiqueta = { stringResource(R.string.minutos_corto, it) },
            onSeleccionar = onCambiarVentana,
        )

        EtiquetaCategoria(stringResource(R.string.alarma_fuente))
        OpcionFuente(
            icono = Icons.Outlined.PhoneAndroid,
            titulo = stringResource(R.string.alarma_fuente_telefono),
            detalle = stringResource(R.string.alarma_fuente_telefono_detalle),
            seleccionada = estado.fuente == FuenteDatos.ACELEROMETRO_TELEFONO,
            onClick = { onCambiarFuente(FuenteDatos.ACELEROMETRO_TELEFONO) },
        )
        OpcionFuente(
            icono = Icons.Outlined.Watch,
            titulo = stringResource(R.string.alarma_fuente_reloj),
            detalle = stringResource(R.string.alarma_fuente_reloj_detalle),
            seleccionada = estado.fuente == FuenteDatos.SMARTWATCH,
            onClick = { onCambiarFuente(FuenteDatos.SMARTWATCH) },
        )

        // Principio "la alarma nunca falla": siempre se avisa del respaldo.
        AvisoCartel(stringResource(R.string.alarma_respaldo), icono = Icons.Outlined.ErrorOutline)
        if (error != null) AvisoCartel(error, icono = Icons.Outlined.ErrorOutline)

        FilaInterruptor(
            titulo = stringResource(R.string.permiso_no_molestar),
            detalle = stringResource(R.string.alarma_no_molestar_detalle),
            activado = estado.noMolestar,
            onCambiar = onCambiarNoMolestar,
        )
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
                }) { Text(stringResource(android.R.string.ok).uppercase(), style = TipoCartel.etiqueta) }
            },
            dismissButton = {
                TextButton(onClick = { eligiendoHora = false }) {
                    Text(stringResource(R.string.accion_cancelar).uppercase(), style = TipoCartel.etiqueta)
                }
            },
            title = { Text(stringResource(R.string.alarma_cambiar_hora), style = TipoCartel.titulo) },
            // Selector sin rellenos grises: contornos y el color interactivo.
            text = {
                TimePicker(
                    state = estadoReloj,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = esquema.background,
                        selectorColor = esquema.primary,
                        timeSelectorSelectedContainerColor = esquema.background,
                        timeSelectorUnselectedContainerColor = esquema.background,
                        timeSelectorSelectedContentColor = esquema.primary,
                    ),
                )
            },
            tonalElevation = 0.dp,
            containerColor = esquema.background,
            modifier = Modifier.border(Bordes.normal, esquema.outline, Formas.tarjeta),
        )
    }
}

@Composable
private fun OpcionFuente(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
) {
    val esquema = MaterialTheme.colorScheme
    TarjetaCartel(Modifier.fillMaxWidth(), onClick = onClick, seleccionada = seleccionada) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = esquema.primary)
            Spacer(Modifier.width(Espacio.l))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = TipoCartel.titulo, color = esquema.onBackground)
                Text(detalle, style = TipoCartel.cuerpo, color = esquema.onBackground)
            }
            // La selección se indica con borde grueso Y con ✓ (no solo con color).
            if (seleccionada) Text("✓", style = TipoCartel.titulo, color = esquema.primary)
        }
    }
}

@PreviewLightDark
@Composable
private fun AlarmaPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) {
        AlarmaContenido(AlarmaUiState(), {}, {}, {}, {}, {}, {})
    }
}
