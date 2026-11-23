package com.upt.dormirbien.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.dormirbien.R
import com.upt.dormirbien.core.navigation.RutaApp
import com.upt.dormirbien.core.navigation.RutaAprende
import com.upt.dormirbien.core.navigation.RutaDiarioSueno
import com.upt.dormirbien.core.navigation.RutaEstadisticas
import com.upt.dormirbien.core.navigation.RutaRespiracion
import com.upt.dormirbien.core.navigation.RutaRutina
import com.upt.dormirbien.core.navigation.RutaVaciadoMental
import com.upt.dormirbien.core.theme.DormirBienTheme

/** Un acceso directo de la pantalla de inicio hacia un módulo. */
private data class AccesoModulo(
    @StringRes val titulo: Int,
    val icono: ImageVector,
    val ruta: RutaApp,
)

/** Módulos que no tienen pestaña propia en la barra inferior. */
private val accesos = listOf(
    AccesoModulo(R.string.vaciado_titulo, Icons.Filled.Edit, RutaVaciadoMental),
    AccesoModulo(R.string.diario_sueno_titulo, Icons.Filled.DateRange, RutaDiarioSueno),
    AccesoModulo(R.string.respiracion_titulo, Icons.Filled.Favorite, RutaRespiracion),
    AccesoModulo(R.string.rutina_titulo, Icons.Filled.CheckCircle, RutaRutina),
    AccesoModulo(R.string.estadisticas_titulo, Icons.Filled.Star, RutaEstadisticas),
    AccesoModulo(R.string.aprende_titulo, Icons.Filled.Info, RutaAprende),
)

/**
 * Pantalla de inicio.
 *
 * Por ahora es solo un menú hacia los módulos. En la Fase 2 se le agregan
 * los pendientes del día (vaciado mental) y el resumen de la noche.
 *
 * @param onAbrirModulo se llama con la ruta del módulo que el usuario tocó.
 *        La pantalla NO navega por sí misma: avisa hacia arriba y quien la usa
 *        decide qué hacer. Así se puede probar y previsualizar sin NavController.
 */
@Composable
fun HomeScreen(
    onAbrirModulo: (RutaApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.inicio_saludo),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.inicio_subtitulo),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
        }
        items(accesos) { acceso ->
            TarjetaModulo(acceso = acceso, onClick = { onAbrirModulo(acceso.ruta) })
        }
    }
}

@Composable
private fun TarjetaModulo(acceso: AccesoModulo, onClick: () -> Unit) {
    // Card con onClick: toda la tarjeta es tocable y TalkBack la anuncia como botón.
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = acceso.icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(acceso.titulo),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E1726)
@Composable
private fun HomeScreenPreview() {
    DormirBienTheme {
        HomeScreen(onAbrirModulo = {})
    }
}
