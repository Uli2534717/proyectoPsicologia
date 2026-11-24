package com.upt.descansa.feature.bienvenida

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bedtime
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonPrincipal
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Superficie
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
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

/** Un permiso explicado en la bienvenida. */
private data class PermisoExplicado(val icono: ImageVector, @StringRes val nombre: Int, @StringRes val motivo: Int)

private val permisos = listOf(
    PermisoExplicado(Icons.Outlined.Notifications, R.string.permiso_notificaciones, R.string.permiso_notificaciones_motivo),
    PermisoExplicado(Icons.Outlined.Alarm, R.string.permiso_alarmas, R.string.permiso_alarmas_motivo),
    PermisoExplicado(Icons.Outlined.MonitorHeart, R.string.permiso_sensores, R.string.permiso_sensores_motivo),
    PermisoExplicado(Icons.Outlined.DoNotDisturbOn, R.string.permiso_no_molestar, R.string.permiso_no_molestar_motivo),
)

/**
 * Pantalla 1 · Bienvenida.
 *
 * Solo EXPLICA los permisos. Cada permiso se pide después, en el momento en que
 * se necesita y con su explicación (sección 14: pedir en contexto, no todos de golpe).
 */
@Composable
fun BienvenidaScreen(
    onComenzar: () -> Unit,
    viewModel: BienvenidaViewModel = hiltViewModel(),
) {
    BienvenidaContenido(onComenzar = { viewModel.alComenzar(onComenzar) })
}

@Composable
fun BienvenidaContenido(onComenzar: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(32.dp))
            // Logo: luna dentro de un círculo.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .background(Superficie, CircleShape),
            ) {
                Icon(Icons.Outlined.Bedtime, contentDescription = null, tint = Lavanda, modifier = Modifier.size(44.dp))
            }
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                color = Texto,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.bienvenida_valor),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoAtenuado,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            EtiquetaSeccion(stringResource(R.string.bienvenida_permisos), Modifier.fillMaxWidth())
            permisos.forEach { permiso -> FilaPermiso(permiso) }
            Text(
                text = stringResource(R.string.bienvenida_privacidad),
                style = MaterialTheme.typography.bodySmall,
                color = TextoAtenuado,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = stringResource(R.string.bienvenida_cambiar_permisos),
            style = MaterialTheme.typography.bodySmall,
            color = TextoAtenuado,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        )
        BotonPrincipal(
            texto = stringResource(R.string.bienvenida_comenzar),
            onClick = onComenzar,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun FilaPermiso(permiso: PermisoExplicado) {
    TarjetaDescansa(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(permiso.icono, contentDescription = null, tint = Lavanda)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(stringResource(permiso.nombre), style = MaterialTheme.typography.titleMedium, color = Texto)
                Text(stringResource(permiso.motivo), style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun BienvenidaPreview() {
    DescansaTheme { BienvenidaContenido(onComenzar = {}) }
}
