package com.upt.descansa.feature.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonSecundario
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.FilaChips
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.designsystem.TituloPantalla
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Fondo
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.Sensibilidad
import com.upt.descansa.domain.model.TipoSimulacion
import com.upt.descansa.domain.usecase.noche.IniciarSimulacionUseCase
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AjustesViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
    private val repositorio: AjustesRepository,
    private val programador: ProgramadorAlarma,
    private val iniciarSimulacion: IniciarSimulacionUseCase,
) : ViewModel() {

    /** Modo desarrollador: arranca una noche simulada de 1 minuto y luego llama a [onListo]. */
    fun alSimular(tipo: TipoSimulacion, onListo: () -> Unit) {
        viewModelScope.launch {
            iniciarSimulacion(tipo)
            onListo()
        }
    }

    val ajustes: StateFlow<AjustesUsuario> =
        observarAjustes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AjustesUsuario())

    fun alCambiar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        viewModelScope.launch { repositorio.actualizar(cambio) }
    }

    /**
     * Modo desarrollador: hace sonar la alarma en 10 s para probarla sin esperar
     * a la mañana (bloquea el teléfono después de tocarlo). Reemplaza la alarma programada.
     */
    fun alProbarAlarma() {
        programador.programar(System.currentTimeMillis() + 10_000L)
    }
}

private val opcionesPosponer = listOf(5, 10, 15)

/**
 * Pantalla 11 · Ajustes.
 *
 * FASE 0: funcionan sensibilidad, posponer, No molestar y modo desarrollador
 * (se guardan). Permisos con su estado, exportar/borrar datos, privacidad y
 * créditos se agregan en las Fases 1–6 (se muestran marcados como próximos).
 */
@Composable
fun AjustesScreen(
    onAtras: () -> Unit,
    onSimulacionIniciada: () -> Unit = {},
    viewModel: AjustesViewModel = hiltViewModel(),
) {
    val ajustes by viewModel.ajustes.collectAsStateWithLifecycle()
    AjustesContenido(
        ajustes = ajustes,
        onAtras = onAtras,
        onCambiar = viewModel::alCambiar,
        onProbarAlarma = viewModel::alProbarAlarma,
        onSimular = { tipo -> viewModel.alSimular(tipo, onSimulacionIniciada) },
    )
}

@Composable
fun AjustesContenido(
    ajustes: AjustesUsuario,
    onAtras: () -> Unit,
    onCambiar: ((AjustesUsuario) -> AjustesUsuario) -> Unit,
    onProbarAlarma: () -> Unit = {},
    onSimular: (TipoSimulacion) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        IconButton(onClick = onAtras) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBackIos, contentDescription = stringResource(R.string.accion_atras), tint = Texto)
        }
        TituloPantalla(stringResource(R.string.ajustes_titulo))

        EtiquetaSeccion(stringResource(R.string.ajustes_alarma))
        Text(stringResource(R.string.ajustes_sensibilidad), style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        FilaChips(
            opciones = Sensibilidad.entries,
            seleccionada = ajustes.sensibilidad,
            etiqueta = {
                stringResource(
                    when (it) {
                        Sensibilidad.BAJA -> R.string.ajustes_sensibilidad_baja
                        Sensibilidad.MEDIA -> R.string.ajustes_sensibilidad_media
                        Sensibilidad.ALTA -> R.string.ajustes_sensibilidad_alta
                    },
                )
            },
            onSeleccionar = { s -> onCambiar { it.copy(sensibilidad = s) } },
        )
        Text(stringResource(R.string.ajustes_posponer), style = MaterialTheme.typography.labelMedium, color = TextoAtenuado)
        FilaChips(
            opciones = opcionesPosponer,
            seleccionada = ajustes.posponerMinutos,
            etiqueta = { stringResource(R.string.minutos_corto, it) },
            onSeleccionar = { m -> onCambiar { it.copy(posponerMinutos = m) } },
        )
        FilaInterruptor(
            titulo = stringResource(R.string.permiso_no_molestar),
            detalle = stringResource(R.string.alarma_no_molestar_detalle),
            activado = ajustes.noMolestarActivado,
            onCambiar = { v -> onCambiar { it.copy(noMolestarActivado = v) } },
        )

        EtiquetaSeccion(stringResource(R.string.ajustes_permisos))
        FilaProxima(stringResource(R.string.ajustes_permisos))

        EtiquetaSeccion(stringResource(R.string.ajustes_datos))
        FilaProxima(stringResource(R.string.ajustes_exportar))
        FilaProxima(stringResource(R.string.ajustes_borrar))
        FilaProxima(stringResource(R.string.ajustes_privacidad))
        FilaProxima(stringResource(R.string.ajustes_creditos))

        FilaInterruptor(
            titulo = stringResource(R.string.ajustes_desarrollador),
            detalle = null,
            activado = ajustes.modoDesarrollador,
            onCambiar = { v -> onCambiar { it.copy(modoDesarrollador = v) } },
        )
        if (ajustes.modoDesarrollador) {
            BotonSecundario(texto = stringResource(R.string.ajustes_probar_alarma), onClick = onProbarAlarma)
            Text(stringResource(R.string.ajustes_probar_alarma_detalle), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
            // Simulador del motor (Fase 2): el emulador no tiene un acelerómetro que se mueva.
            BotonSecundario(texto = stringResource(R.string.ajustes_simular_inquieta), onClick = { onSimular(TipoSimulacion.INQUIETA) })
            BotonSecundario(texto = stringResource(R.string.ajustes_simular_tranquila), onClick = { onSimular(TipoSimulacion.TRANQUILA) })
            Text(stringResource(R.string.ajustes_simular_detalle), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
        }
        // Aviso permanente (sección 14).
        Text(stringResource(R.string.aviso_no_medico), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
    }
}

@Composable
private fun FilaInterruptor(titulo: String, detalle: String?, activado: Boolean, onCambiar: (Boolean) -> Unit) {
    TarjetaDescansa(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activado, role = Role.Switch, onValueChange = onCambiar),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = Texto)
                if (detalle != null) Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
            }
            Switch(
                checked = activado,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(checkedTrackColor = Turquesa, checkedThumbColor = Fondo),
            )
        }
    }
}

/** Opción que llegará en una fase posterior: visible, pero honesta sobre su estado. */
@Composable
private fun FilaProxima(titulo: String) {
    TarjetaDescansa(Modifier.fillMaxWidth()) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, color = Texto)
        Text(stringResource(R.string.ajustes_proximamente), style = MaterialTheme.typography.bodySmall, color = TextoAtenuado)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 1200)
@Composable
private fun AjustesPreview() {
    DescansaTheme { AjustesContenido(AjustesUsuario(), onAtras = {}, onCambiar = {}) }
}
