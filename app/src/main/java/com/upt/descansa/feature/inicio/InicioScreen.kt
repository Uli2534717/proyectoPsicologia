package com.upt.descansa.feature.inicio

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonPrincipal
import com.upt.descansa.core.designsystem.EtiquetaSeccion
import com.upt.descansa.core.designsystem.TarjetaDescansa
import com.upt.descansa.core.designsystem.TituloPantalla
import com.upt.descansa.core.navigation.RutaAjustes
import com.upt.descansa.core.navigation.RutaAlarma
import com.upt.descansa.core.navigation.RutaApp
import com.upt.descansa.core.navigation.RutaPendientes
import com.upt.descansa.core.navigation.RutaRelajacion
import com.upt.descansa.core.navigation.RutaRespiracion
import com.upt.descansa.core.navigation.RutaSonidos
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa
import com.upt.descansa.domain.model.ResumenUltimaNoche

/** Una herramienta de la cuadrícula de Inicio. */
private data class Herramienta(@StringRes val nombre: Int, val icono: ImageVector, val color: Color, val ruta: RutaApp)

private val herramientas = listOf(
    Herramienta(R.string.herramienta_pendientes, Icons.AutoMirrored.Outlined.Notes, Lavanda, RutaPendientes),
    Herramienta(R.string.herramienta_respiracion, Icons.Outlined.RadioButtonChecked, Turquesa, RutaRespiracion),
    Herramienta(R.string.herramienta_relajacion, Icons.Outlined.Accessibility, Lavanda, RutaRelajacion),
    Herramienta(R.string.herramienta_sonidos, Icons.AutoMirrored.Outlined.VolumeUp, Turquesa, RutaSonidos),
    Herramienta(R.string.herramienta_alarma, Icons.Outlined.Alarm, Lavanda, RutaAlarma),
    // El interruptor de No molestar vive en Alarma y en Ajustes (sección 7).
    Herramienta(R.string.herramienta_no_molestar, Icons.Outlined.DoNotDisturbOn, Turquesa, RutaAjustes),
)

/**
 * Pantalla 2 · Inicio.
 *
 * @param ultimaNoche resumen de la noche anterior; `null` muestra el estado
 *        vacío. Fase 0: siempre null (aún no se registran noches). En la Fase 2
 *        un ViewModel lo leerá de la tabla `noches`.
 */
@Composable
fun InicioScreen(
    onPrepararNoche: () -> Unit,
    onAbrir: (RutaApp) -> Unit,
    modifier: Modifier = Modifier,
    ultimaNoche: ResumenUltimaNoche? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            TituloPantalla(
                titulo = stringResource(R.string.inicio_saludo),
                subtitulo = stringResource(R.string.inicio_subtitulo),
                modifier = Modifier.weight(1f),
            )
            // El prototipo no muestra cómo llegar a Ajustes; lo ponemos aquí (ver DECISIONES.md).
            IconButton(onClick = { onAbrir(RutaAjustes) }) {
                Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.accion_ajustes), tint = TextoAtenuado)
            }
        }
        TarjetaUltimaNoche(ultimaNoche)
        BotonPrincipal(
            texto = stringResource(R.string.inicio_preparar_noche),
            onClick = onPrepararNoche,
            icono = Icons.Outlined.Bedtime,
        )
        Text(stringResource(R.string.inicio_herramientas), style = MaterialTheme.typography.titleLarge, color = Texto)
        // Cuadrícula de 2 columnas hecha con filas: dentro de un scroll vertical
        // no se puede usar LazyVerticalGrid, y son solo 6 elementos.
        herramientas.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                fila.forEach { herramienta ->
                    TarjetaHerramienta(herramienta, onClick = { onAbrir(herramienta.ruta) }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TarjetaUltimaNoche(noche: ResumenUltimaNoche?) {
    TarjetaDescansa(modifier = Modifier.fillMaxWidth()) {
        EtiquetaSeccion(stringResource(R.string.inicio_ultima_noche))
        Spacer(Modifier.heightIn(min = 12.dp))
        if (noche == null) {
            // Estado vacío amable: invita, no presiona.
            Text(stringResource(R.string.inicio_sin_noches), style = MaterialTheme.typography.bodyLarge, color = TextoAtenuado)
            return@TarjetaDescansa
        }
        val horas = stringResource(R.string.inicio_horas_minutos, noche.minutosDormidos / 60, noche.minutosDormidos % 60)
        val deSueno = stringResource(R.string.inicio_de_sueno)
        Text(
            buildAnnotatedString {
                withStyle(MaterialTheme.typography.displaySmall.toSpanStyle()) { append(horas) }
                append(" ")
                withStyle(SpanStyle(color = TextoAtenuado)) { append(deSueno) }
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Texto,
        )
        Spacer(Modifier.heightIn(min = 16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DatoHora(stringResource(R.string.inicio_te_dormiste), noche.horaDormir, Modifier.weight(1f))
            DatoHora(stringResource(R.string.inicio_despertaste), noche.horaDespertar, Modifier.weight(1f))
        }
    }
}

@Composable
private fun DatoHora(etiqueta: String, hora: String, modifier: Modifier = Modifier) {
    Surface(color = SuperficieSecundaria, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextoAtenuado)
            Text(hora, style = MaterialTheme.typography.titleLarge, color = Texto)
        }
    }
}

@Composable
private fun TarjetaHerramienta(herramienta: Herramienta, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TarjetaDescansa(modifier = modifier.heightIn(min = 72.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.heightIn(min = 32.dp)) {
            Icon(herramienta.icono, contentDescription = null, tint = herramienta.color)
            Spacer(Modifier.width(12.dp))
            Text(stringResource(herramienta.nombre), style = MaterialTheme.typography.titleMedium, color = Texto)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun InicioConNochePreview() {
    DescansaTheme {
        InicioScreen(
            onPrepararNoche = {},
            onAbrir = {},
            ultimaNoche = ResumenUltimaNoche(minutosDormidos = 432, horaDormir = "23:40", horaDespertar = "06:52"),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 900)
@Composable
private fun InicioVacioPreview() {
    DescansaTheme { InicioScreen(onPrepararNoche = {}, onAbrir = {}) }
}
