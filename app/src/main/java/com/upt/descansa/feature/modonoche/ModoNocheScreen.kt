package com.upt.descansa.feature.modonoche

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.BotonPildora
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.Bordes
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.Formas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TemaNocturno
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.core.util.formatearHora
import com.upt.descansa.core.util.inicioVentana
import com.upt.descansa.domain.alarma.AlarmaProgramada
import com.upt.descansa.domain.alarma.AlarmaRespaldo
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/** Lee los ajustes de la noche y la alarma de respaldo REAL que está programada. */
@HiltViewModel
class ModoNocheViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
    private val alarmaRespaldo: AlarmaRespaldo,
) : ViewModel() {
    val ajustes: StateFlow<AjustesUsuario> =
        observarAjustes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AjustesUsuario())

    val alarma: StateFlow<AlarmaProgramada?> =
        alarmaRespaldo.programada.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Detener la sesión cancela la alarma de respaldo. */
    fun detener() = alarmaRespaldo.cancelar()
}

/**
 * Pantalla 8 · Modo noche activo (turquesa: DESCANSA).
 * SIEMPRE en tema nocturno y con poco texto, para no iluminar el cuarto.
 */
@Composable
fun ModoNocheScreen(
    onDetenido: () -> Unit,
    viewModel: ModoNocheViewModel = hiltViewModel(),
) {
    val ajustes by viewModel.ajustes.collectAsStateWithLifecycle()
    val alarma by viewModel.alarma.collectAsStateWithLifecycle()
    ModoNocheContenido(
        ajustes = ajustes,
        respaldo = alarma?.let { formatearHora(minutosLocales(it.disparoEnMs)) },
        onDetener = {
            viewModel.detener()
            onDetenido()
        },
    )
}

/** Hora local ("minutos desde 00:00") de un instante en epoch ms. */
private fun minutosLocales(epochMs: Long): Int =
    Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalTime().let { it.hour * 60 + it.minute }

/**
 * @param respaldo hora de la alarma de respaldo programada ("07:00"), o null si
 *        no hay ninguna (se muestra una advertencia, nunca se finge).
 */
@Composable
fun ModoNocheContenido(ajustes: AjustesUsuario, respaldo: String?, onDetener: () -> Unit) {
    var confirmando by rememberSaveable { mutableStateOf(false) }
    val horaLimite = formatearHora(ajustes.horaLimiteMinutos)
    val desde = formatearHora(inicioVentana(ajustes.horaLimiteMinutos, ajustes.ventanaMinutos))

    // Reloj: se actualiza cada segundo solo mientras la pantalla está visible.
    var minutoActual by rememberSaveable { mutableIntStateOf(LocalTime.now().let { it.hour * 60 + it.minute }) }
    LaunchedEffect(Unit) {
        while (true) {
            val ahora = LocalTime.now()
            minutoActual = ahora.hour * 60 + ahora.minute
            delay(1_000)
        }
    }

    TemaNocturno {
        val esquema = MaterialTheme.colorScheme
        PantallaCartel(
            palabra = stringResource(R.string.palabra_modo_noche),
            colorPalabra = LocalColoresCartel.current.turquesa,
            titulo = stringResource(R.string.modo_noche_activo),
            imagen = R.drawable.img_modo_noche_lampara,
            descripcionImagen = stringResource(R.string.img_modo_noche_lampara),
            nocturna = true,
            accionPrincipal = {
                Text(stringResource(R.string.modo_noche_colchon), style = TipoCartel.cuerpo, color = esquema.onBackground)
                BotonPildora(stringResource(R.string.modo_noche_detener), onClick = { confirmando = true })
            },
        ) {
            Text(formatearHora(minutoActual), style = TipoCartel.hora, color = esquema.onBackground)
            TarjetaCartel(Modifier.fillMaxWidth()) {
                EtiquetaCategoria(stringResource(R.string.alarma_titulo), adorno = AdornoEtiqueta.NINGUNO)
                Text(stringResource(R.string.modo_noche_rango, desde, horaLimite), style = TipoCartel.subtitulo, color = esquema.onBackground)
                Text(stringResource(R.string.modo_noche_a_mas_tardar, horaLimite), style = TipoCartel.cuerpo, color = esquema.onBackground)
            }
            SeparadorOnda()
            if (ajustes.noMolestarActivado) Linea(Icons.Outlined.Check, stringResource(R.string.modo_noche_no_molestar))
            if (respaldo != null) {
                Linea(Icons.Outlined.Check, stringResource(R.string.modo_noche_respaldo, respaldo))
            } else {
                Linea(Icons.Outlined.ErrorOutline, stringResource(R.string.modo_noche_sin_respaldo))
            }
            val fuente = stringResource(
                if (ajustes.fuente == FuenteDatos.SMARTWATCH) R.string.alarma_fuente_reloj else R.string.alarma_fuente_telefono,
            )
            Linea(Icons.Outlined.Check, stringResource(R.string.modo_noche_fuente, fuente.lowercase()))
        }

        if (confirmando) {
            AlertDialog(
                onDismissRequest = { confirmando = false },
                title = { Text(stringResource(R.string.modo_noche_confirmar_titulo), style = TipoCartel.titulo) },
                text = { Text(stringResource(R.string.modo_noche_confirmar_texto), style = TipoCartel.cuerpo) },
                confirmButton = {
                    TextButton(onClick = {
                        confirmando = false
                        onDetener()
                    }) { Text(stringResource(R.string.modo_noche_detener).uppercase(), style = TipoCartel.etiqueta) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmando = false }) {
                        Text(stringResource(R.string.accion_cancelar).uppercase(), style = TipoCartel.etiqueta)
                    }
                },
                tonalElevation = 0.dp,
                containerColor = esquema.background,
                modifier = Modifier.border(Bordes.normal, esquema.outline, Formas.tarjeta),
            )
        }
    }
}

/** Línea de estado: ✓ si está activo, ⚠ si falta (el ícono es de estado). */
@Composable
private fun Linea(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(Espacio.l))
        Text(texto, style = TipoCartel.cuerpo, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun ModoNochePreview() {
    ModoNocheContenido(ajustes = AjustesUsuario(), respaldo = "07:00", onDetener = {})
}
