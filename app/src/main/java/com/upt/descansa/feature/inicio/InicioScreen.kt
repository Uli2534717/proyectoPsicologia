package com.upt.descansa.feature.inicio

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.AdornoEtiqueta
import com.upt.descansa.core.designsystem.components.BotonPildora
import com.upt.descansa.core.designsystem.components.ElementoFila
import com.upt.descansa.core.designsystem.components.EtiquetaCategoria
import com.upt.descansa.core.designsystem.components.FilaDeTres
import com.upt.descansa.core.designsystem.components.Logotipo
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.components.SeparadorOnda
import com.upt.descansa.core.designsystem.components.TamanoCartel
import com.upt.descansa.core.designsystem.components.TarjetaCartel
import com.upt.descansa.core.designsystem.theme.DescansaTheme
import com.upt.descansa.core.designsystem.theme.Espacio
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.core.navigation.RutaAjustes
import com.upt.descansa.core.navigation.RutaAlarma
import com.upt.descansa.core.navigation.RutaApp
import com.upt.descansa.core.navigation.RutaPendientes
import com.upt.descansa.core.navigation.RutaRelajacion
import com.upt.descansa.core.navigation.RutaRespiracion
import com.upt.descansa.core.navigation.RutaSonidos
import com.upt.descansa.domain.model.ResumenUltimaNoche

/** Herramienta de la lista inferior de Inicio. */
private data class Herramienta(@StringRes val nombre: Int, val ruta: RutaApp)

/** Las que no aparecen en la fila de tres (con imagen). */
private val otrasHerramientas = listOf(
    Herramienta(R.string.herramienta_relajacion, RutaRelajacion),
    Herramienta(R.string.herramienta_alarma, RutaAlarma),
    // El interruptor de No molestar vive en Alarma y en Ajustes (sección 7).
    Herramienta(R.string.herramienta_no_molestar, RutaAjustes),
)

/**
 * Pantalla 2 · Inicio (sección amarilla: DUERME, tamaño XL).
 *
 * @param ultimaNoche resumen de la noche anterior; `null` muestra el estado vacío.
 *        Aún siempre null: en la Fase 2 un ViewModel lo leerá de la tabla `noches`.
 */
@Composable
fun InicioScreen(
    onPrepararNoche: () -> Unit,
    onAbrir: (RutaApp) -> Unit,
    ultimaNoche: ResumenUltimaNoche? = null,
) {
    PantallaCartel(
        palabra = stringResource(R.string.palabra_inicio),
        colorPalabra = LocalColoresCartel.current.amarillo,
        titulo = stringResource(R.string.inicio_saludo),
        descripcion = stringResource(R.string.inicio_subtitulo),
        tamano = TamanoCartel.XL,
        imagen = R.drawable.img_inicio_almohada,
        descripcionImagen = stringResource(R.string.img_inicio_almohada),
        encabezadoInicio = { Logotipo() },
        accionPrincipal = { BotonPildora(stringResource(R.string.inicio_preparar_noche), onPrepararNoche) },
    ) {
        TarjetaUltimaNoche(ultimaNoche)
        SeparadorOnda()
        EtiquetaCategoria(stringResource(R.string.inicio_accesos))
        FilaDeTres(
            listOf(
                ElementoFila(
                    R.drawable.img_vaciado_cuaderno, stringResource(R.string.img_vaciado_cuaderno),
                    stringResource(R.string.herramienta_pendientes), stringResource(R.string.leyenda_pendientes),
                ) { onAbrir(RutaPendientes) },
                ElementoFila(
                    R.drawable.img_respiracion_vela, stringResource(R.string.img_respiracion_vela),
                    stringResource(R.string.herramienta_respiracion), stringResource(R.string.leyenda_respiracion),
                ) { onAbrir(RutaRespiracion) },
                ElementoFila(
                    R.drawable.img_sonido_lluvia, stringResource(R.string.img_sonido_lluvia),
                    stringResource(R.string.herramienta_sonidos), stringResource(R.string.leyenda_sonidos),
                ) { onAbrir(RutaSonidos) },
            ),
        )
        EtiquetaCategoria(stringResource(R.string.inicio_herramientas))
        Column(verticalArrangement = Arrangement.spacedBy(Espacio.xs)) {
            otrasHerramientas.forEach { herramienta ->
                TarjetaCartel(Modifier.fillMaxWidth(), onClick = { onAbrir(herramienta.ruta) }) {
                    Text(stringResource(herramienta.nombre), style = TipoCartel.titulo, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Composable
private fun TarjetaUltimaNoche(noche: ResumenUltimaNoche?) {
    TarjetaCartel(Modifier.fillMaxWidth()) {
        EtiquetaCategoria(stringResource(R.string.inicio_ultima_noche), adorno = AdornoEtiqueta.NINGUNO)
        if (noche == null) {
            // Estado vacío amable: invita, no presiona.
            Text(stringResource(R.string.inicio_sin_noches), style = TipoCartel.cuerpo, color = MaterialTheme.colorScheme.onBackground)
            return@TarjetaCartel
        }
        Text(
            stringResource(R.string.inicio_horas_minutos, noche.minutosDormidos / 60, noche.minutosDormidos % 60),
            style = TipoCartel.subtitulo,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(Modifier.fillMaxWidth()) {
            DatoHora(stringResource(R.string.inicio_te_dormiste), noche.horaDormir, Modifier.weight(1f))
            DatoHora(stringResource(R.string.inicio_despertaste), noche.horaDespertar, Modifier.weight(1f))
        }
    }
}

@Composable
private fun DatoHora(etiqueta: String, hora: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(etiqueta.uppercase(), style = TipoCartel.leyenda, color = MaterialTheme.colorScheme.onBackground)
        Text(hora, style = TipoCartel.titulo, color = MaterialTheme.colorScheme.onBackground)
    }
}

@PreviewLightDark
@Composable
private fun InicioPreview() {
    DescansaTheme(nocturno = androidx.compose.foundation.isSystemInDarkTheme()) {
        InicioScreen(
            onPrepararNoche = {},
            onAbrir = {},
            ultimaNoche = ResumenUltimaNoche(minutosDormidos = 432, horaDormir = "23:40", horaDespertar = "06:52"),
        )
    }
}
