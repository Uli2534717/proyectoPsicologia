package com.upt.descansa.feature.ajustes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.FilaInterruptor
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.components.SelectorOpciones
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.core.util.formatearHora
import com.upt.descansa.domain.alarma.AlarmaProgramada
import com.upt.descansa.domain.alarma.AlarmaRespaldo
import com.upt.descansa.domain.alarma.CalculoAlarma
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.ModoTema
import com.upt.descansa.domain.model.Sensibilidad
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AjustesViewModel @Inject constructor(
    observarAjustes: ObservarAjustesUseCase,
    private val repositorio: AjustesRepository,
    private val alarmaRespaldo: AlarmaRespaldo,
) : ViewModel() {

    val ajustes: StateFlow<AjustesUsuario> =
        observarAjustes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AjustesUsuario())

    fun alCambiar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        viewModelScope.launch { repositorio.actualizar(cambio) }
    }

    /**
     * Modo desarrollador: programa una alarma de respaldo REAL dentro de 2 minutos
     * (criterio de la Fase 1: bloquear, reiniciar y comprobar que suena).
     * Se marca como posposición para que conserve su hora exacta tras un reinicio.
     * @return la hora local "HH:mm" a la que sonará.
     */
    fun probarAlarma(): String {
        val disparo = CalculoAlarma.horaPosposicion(System.currentTimeMillis(), 2)
        alarmaRespaldo.programar(
            AlarmaProgramada(
                disparoEnMs = disparo,
                horaLimiteMinutos = ajustes.value.horaLimiteMinutos,
                posponerMinutos = ajustes.value.posponerMinutos,
                esPosposicion = true,
            ),
        )
        val hora = Instant.ofEpochMilli(disparo).atZone(ZoneId.systemDefault()).toLocalTime()
        return formatearHora(hora.hour * 60 + hora.minute)
    }
}

private val opcionesPosponer = listOf(5, 10, 15)

/**
 * Pantalla 11 · Ajustes (sección amarilla: AJUSTA).
 *
 * Funcionan: tema, sensibilidad, posponer, No molestar y modo desarrollador
 * (con "Probar alarma en 2 minutos"). Permisos con su estado, exportar y borrar
 * datos, privacidad y créditos llegan en la Fase 6 y se muestran como próximos.
 */
@Composable
fun AjustesScreen(
    onAtras: () -> Unit,
    viewModel: AjustesViewModel = hiltViewModel(),
) {
    val ajustes by viewModel.ajustes.collectAsStateWithLifecycle()
    AjustesContenido(
        ajustes = ajustes,
        onAtras = onAtras,
        onCambiar = viewModel::alCambiar,
        onProbarAlarma = viewModel::probarAlarma,
    )
}

@Composable
fun AjustesContenido(
    ajustes: AjustesUsuario,
    onAtras: () -> Unit,
    onCambiar: ((AjustesUsuario) -> AjustesUsuario) -> Unit,
    onProbarAlarma: () -> String = { "" },
) {
    var alarmaDePrueba by rememberSaveable { mutableStateOf<String?>(null) }
    val esquema = MaterialTheme.colorScheme

    PantallaCartel(
        palabra = stringResource(R.string.palabra_ajustes),
        colorPalabra = LocalColoresCartel.current.amarillo,
        titulo = stringResource(R.string.ajustes_titulo),
        imagen = R.drawable.img_ajustes_audifonos,
        descripcionImagen = stringResource(R.string.img_ajustes_audifonos),
        onAtras = onAtras,
    ) {
        EtiquetaCategoria(stringResource(R.string.ajustes_tema))
        SelectorOpciones(
            opciones = ModoTema.entries,
            seleccionada = ajustes.modoTema,
            etiqueta = {
                stringResource(
                    when (it) {
                        ModoTema.AUTOMATICO -> R.string.ajustes_tema_automatico
                        ModoTema.CLARO -> R.string.ajustes_tema_claro
                        ModoTema.OSCURO -> R.string.ajustes_tema_oscuro
                    },
                )
            },
            onSeleccionar = { m -> onCambiar { it.copy(modoTema = m) } },
        )
        Text(stringResource(R.string.ajustes_tema_detalle), style = TipoCartel.cuerpo, color = esquema.onBackground)

        SeparadorOnda()
        EtiquetaCategoria(stringResource(R.string.ajustes_sensibilidad))
        SelectorOpciones(
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
        EtiquetaCategoria(stringResource(R.string.ajustes_posponer))
        SelectorOpciones(
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

        SeparadorOnda()
        EtiquetaCategoria(stringResource(R.string.ajustes_datos))
        listOf(
            R.string.ajustes_permisos,
            R.string.ajustes_exportar,
            R.string.ajustes_borrar,
            R.string.ajustes_privacidad,
            R.string.ajustes_creditos,
        ).forEach { FilaProxima(stringResource(it)) }

        FilaInterruptor(
            titulo = stringResource(R.string.ajustes_desarrollador),
            activado = ajustes.modoDesarrollador,
            onCambiar = { v -> onCambiar { it.copy(modoDesarrollador = v) } },
        )
        if (ajustes.modoDesarrollador) {
            TarjetaCartel(Modifier.fillMaxWidth(), onClick = { alarmaDePrueba = onProbarAlarma() }) {
                EtiquetaCategoria(stringResource(R.string.ajustes_probar_alarma), adorno = AdornoEtiqueta.CHEVRON)
                Text(
                    alarmaDePrueba?.let { stringResource(R.string.ajustes_alarma_programada, it) }
                        ?: stringResource(R.string.ajustes_probar_alarma_detalle),
                    style = TipoCartel.cuerpo,
                    color = esquema.onBackground,
                )
            }
        }
        // Aviso permanente (sección 14).
        Text(stringResource(R.string.aviso_no_medico), style = TipoCartel.cuerpo, color = esquema.onBackground)
    }
}

/** Opción que llegará en una fase posterior: visible, pero honesta sobre su estado. */
@Composable
private fun FilaProxima(titulo: String) {
    TarjetaCartel(Modifier.fillMaxWidth()) {
        Text(titulo, style = TipoCartel.titulo, color = MaterialTheme.colorScheme.onBackground)
        Text(stringResource(R.string.ajustes_proximamente).uppercase(), style = TipoCartel.leyenda, color = MaterialTheme.colorScheme.onBackground)
    }
}

@PreviewLightDark
@Composable
private fun AjustesPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) {
        AjustesContenido(AjustesUsuario(modoDesarrollador = true), onAtras = {}, onCambiar = {})
    }
}
