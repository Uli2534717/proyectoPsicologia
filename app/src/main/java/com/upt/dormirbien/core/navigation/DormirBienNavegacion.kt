package com.upt.dormirbien.core.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upt.dormirbien.feature.ajustes.AjustesScreen
import com.upt.dormirbien.feature.alarma.AlarmaScreen
import com.upt.dormirbien.feature.aprende.AprendeScreen
import com.upt.dormirbien.feature.diariosueno.DiarioSuenoScreen
import com.upt.dormirbien.feature.emociones.EmocionesScreen
import com.upt.dormirbien.feature.estadisticas.EstadisticasScreen
import com.upt.dormirbien.feature.home.HomeScreen
import com.upt.dormirbien.feature.onboarding.OnboardingScreen
import com.upt.dormirbien.feature.respiracion.RespiracionScreen
import com.upt.dormirbien.feature.rutina.RutinaScreen
import com.upt.dormirbien.feature.sonidos.SonidosScreen
import com.upt.dormirbien.feature.vaciadomental.VaciadoMentalScreen

/**
 * Raíz visual de la app: barra inferior + contenedor de pantallas (NavHost).
 *
 * `Scaffold` acomoda la barra inferior y nos da `paddingInterno` para que el
 * contenido no quede tapado por la barra ni por la barra de estado del sistema.
 */
@Composable
fun DormirBienNavegacion(
    navController: NavHostController = rememberNavController(),
) {
    // Pantalla actual; cambia cada vez que navegamos.
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoActual = entradaActual?.destination

    Scaffold(
        bottomBar = {
            BarraInferior(
                destinoActual = destinoActual,
                onSeleccionar = { destino -> navController.navegarAPestana(destino.ruta) },
            )
        },
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = RutaInicio,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .consumeWindowInsets(paddingInterno),
        ) {
            // Cada `composable<Ruta>` dice qué pantalla dibujar para esa ruta.
            // Cada integrante solo toca la línea de SU módulo.
            composable<RutaOnboarding> { OnboardingScreen() }
            composable<RutaInicio> {
                HomeScreen(onAbrirModulo = { ruta -> navController.navigate(ruta) })
            }
            composable<RutaVaciadoMental> { VaciadoMentalScreen() }
            composable<RutaDiarioSueno> { DiarioSuenoScreen() }
            composable<RutaSonidos> { SonidosScreen() }
            composable<RutaAlarma> { AlarmaScreen() }
            composable<RutaEmociones> { EmocionesScreen() }
            composable<RutaEstadisticas> { EstadisticasScreen() }
            composable<RutaAprende> { AprendeScreen() }
            composable<RutaRespiracion> { RespiracionScreen() }
            composable<RutaRutina> { RutinaScreen() }
            composable<RutaAjustes> { AjustesScreen() }
        }
    }
}

@Composable
private fun BarraInferior(
    destinoActual: NavDestination?,
    onSeleccionar: (DestinoPrincipal) -> Unit,
) {
    NavigationBar {
        DestinoPrincipal.entries.forEach { destino ->
            // ¿La pantalla actual pertenece a esta pestaña?
            val seleccionado = destinoActual?.hierarchy?.any { it.hasRoute(destino.ruta::class) } == true
            NavigationBarItem(
                selected = seleccionado,
                onClick = { onSeleccionar(destino) },
                // La etiqueta ya describe el ícono; TalkBack lee la etiqueta.
                icon = { Icon(destino.icono, contentDescription = null) },
                label = { Text(stringResource(destino.etiqueta)) },
            )
        }
    }
}

/**
 * Navegación "de pestaña": no apila pantallas repetidas y conserva el estado
 * de cada pestaña al ir y venir (patrón recomendado por Android).
 */
private fun NavHostController.navegarAPestana(ruta: RutaApp) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
