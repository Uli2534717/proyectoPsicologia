package com.upt.descansa.feature.modonoche

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.FormaPildora
import com.upt.descansa.core.theme.Borde
import com.upt.descansa.core.theme.BordeTenue
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.FondoModoNoche
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa
import com.upt.descansa.core.util.formatearHora
import com.upt.descansa.core.util.inicioVentana
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime
import javax.inject.Inject

/** Lee los ajustes con los que se activó la noche, para mostrarlos. */
@HiltViewModel
class ModoNocheViewModel @Inject constructor(observarAjustes: ObservarAjustesUseCase) : ViewModel() {
    val ajustes: StateFlow<AjustesUsuario> =
        observarAjustes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AjustesUsuario())
}

/**
 * Pantalla 8 · Modo noche activo.
 *
 * Fondo casi negro y poco texto para no iluminar el cuarto.
 * FASE 0: muestra lo configurado y "Detener" (con confirmación) lleva al
 * resumen de la mañana. En las Fases 1–3 esta pantalla reflejará el estado
 * real del servicio nocturno, la alarma de respaldo y No molestar.
 */
@Composable
fun ModoNocheScreen(
    onDetenido: () -> Unit,
    viewModel: ModoNocheViewModel = hiltViewModel(),
) {
    val ajustes by viewModel.ajustes.collectAsStateWithLifecycle()
    ModoNocheContenido(ajustes = ajustes, onDetener = onDetenido)
}

@Composable
fun ModoNocheContenido(ajustes: AjustesUsuario, onDetener: () -> Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoModoNoche)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Icon(Icons.Outlined.Bedtime, contentDescription = null, tint = Lavanda, modifier = Modifier.size(40.dp))
        Text(formatearHora(minutoActual), style = MaterialTheme.typography.displayLarge, color = Texto.copy(alpha = 0.85f))
        Text(stringResource(R.string.modo_noche_activo), style = MaterialTheme.typography.titleMedium, color = TextoAtenuado)
        Spacer(Modifier.height(24.dp))
        Surface(
            color = SuperficieSecundaria.copy(alpha = 0.6f),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, BordeTenue),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                EtiquetaSeccion(stringResource(R.string.alarma_titulo))
                Text(stringResource(R.string.modo_noche_rango, desde, horaLimite), style = MaterialTheme.typography.titleLarge, color = Texto)
                Text(stringResource(R.string.modo_noche_a_mas_tardar, horaLimite), style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
            }
        }
        Spacer(Modifier.height(24.dp))
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (ajustes.noMolestarActivado) LineaActivada(stringResource(R.string.modo_noche_no_molestar))
            LineaActivada(stringResource(R.string.modo_noche_respaldo, horaLimite))
            val fuente = stringResource(
                if (ajustes.fuente == FuenteDatos.SMARTWATCH) R.string.alarma_fuente_reloj else R.string.alarma_fuente_telefono,
            )
            LineaActivada(stringResource(R.string.modo_noche_fuente, fuente.lowercase()))
        }
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.modo_noche_colchon),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoAtenuado,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = { confirmando = true },
            shape = FormaPildora,
            border = BorderStroke(1.5.dp, Borde),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(bottom = 0.dp),
        ) {
            Text(stringResource(R.string.modo_noche_detener), style = MaterialTheme.typography.labelLarge, color = Texto)
        }
        Spacer(Modifier.height(16.dp))
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text(stringResource(R.string.modo_noche_confirmar_titulo)) },
            text = { Text(stringResource(R.string.modo_noche_confirmar_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmando = false
                    onDetener()
                }) { Text(stringResource(R.string.modo_noche_detener)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmando = false }) { Text(stringResource(R.string.accion_cancelar)) }
            },
        )
    }
}

@Composable
private fun LineaActivada(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Check, contentDescription = null, tint = Turquesa)
        Spacer(Modifier.width(16.dp))
        Text(texto, style = MaterialTheme.typography.bodyLarge, color = Texto)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070D17, heightDp = 900)
@Composable
private fun ModoNochePreview() {
    DescansaTheme { ModoNocheContenido(ajustes = AjustesUsuario(), onDetener = {}) }
}
