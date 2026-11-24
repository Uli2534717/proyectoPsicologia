package com.upt.descansa.feature.bienvenida

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.BotonPildora
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.Logotipo
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TamanoCartel
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.domain.usecase.ajustes.CompletarBienvenidaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Guarda que el usuario ya vio la bienvenida para no mostrarla de nuevo. */
@HiltViewModel
class BienvenidaViewModel @Inject constructor(
    private val completarBienvenida: CompletarBienvenidaUseCase,
) : ViewModel() {
    fun alComenzar(alTerminar: () -> Unit) {
        viewModelScope.launch {
            completarBienvenida()
            alTerminar()
        }
    }
}

/** Un permiso explicado en la bienvenida. El ícono es de estado, no decorativo. */
private data class PermisoExplicado(val icono: ImageVector, @StringRes val nombre: Int, @StringRes val motivo: Int)

private val permisos = listOf(
    PermisoExplicado(Icons.Outlined.Notifications, R.string.permiso_notificaciones, R.string.permiso_notificaciones_motivo),
    PermisoExplicado(Icons.Outlined.Alarm, R.string.permiso_alarmas, R.string.permiso_alarmas_motivo),
    PermisoExplicado(Icons.Outlined.MonitorHeart, R.string.permiso_sensores, R.string.permiso_sensores_motivo),
    PermisoExplicado(Icons.Outlined.DoNotDisturbOn, R.string.permiso_no_molestar, R.string.permiso_no_molestar_motivo),
)

/**
 * Pantalla 1 · Bienvenida (sección amarilla: DESCANSA).
 * Solo EXPLICA los permisos; cada uno se pide después, en su momento (sección 14).
 */
@Composable
fun BienvenidaScreen(
    onComenzar: () -> Unit,
    viewModel: BienvenidaViewModel = hiltViewModel(),
) {
    BienvenidaContenido(onComenzar = { viewModel.alComenzar(onComenzar) })
}

@Composable
fun BienvenidaContenido(onComenzar: () -> Unit) {
    PantallaCartel(
        palabra = stringResource(R.string.palabra_bienvenida),
        colorPalabra = LocalColoresCartel.current.amarillo,
        titulo = stringResource(R.string.app_name),
        descripcion = stringResource(R.string.bienvenida_valor),
        tamano = TamanoCartel.XL,
        imagen = R.drawable.img_bienvenida_lampara_luna,
        descripcionImagen = stringResource(R.string.img_bienvenida_lampara_luna),
        encabezadoInicio = { Logotipo() },
        accionPrincipal = {
            Text(
                stringResource(R.string.bienvenida_cambiar_permisos),
                style = TipoCartel.leyenda,
                color = MaterialTheme.colorScheme.onBackground,
            )
            BotonPildora(stringResource(R.string.bienvenida_comenzar), onComenzar)
        },
    ) {
        SeparadorOnda()
        EtiquetaCategoria(stringResource(R.string.bienvenida_permisos))
        permisos.forEach { FilaPermiso(it) }
        Text(stringResource(R.string.bienvenida_privacidad), style = TipoCartel.cuerpo, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun FilaPermiso(permiso: PermisoExplicado) {
    TarjetaCartel(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(permiso.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Espacio.l))
            Column {
                Text(stringResource(permiso.nombre), style = TipoCartel.titulo, color = MaterialTheme.colorScheme.onBackground)
                Text(stringResource(permiso.motivo), style = TipoCartel.cuerpo, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BienvenidaPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) { BienvenidaContenido(onComenzar = {}) }
}
