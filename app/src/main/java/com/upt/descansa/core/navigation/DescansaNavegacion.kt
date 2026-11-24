package com.upt.descansa.core.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upt.descansa.R
import com.upt.descansa.core.theme.BordeTenue
import com.upt.descansa.core.theme.Fondo
import com.upt.descansa.core.theme.FondoModoNoche
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.feature.ajustes.AjustesScreen
import com.upt.descansa.feature.aprender.AprenderScreen
import com.upt.descansa.feature.bienvenida.BienvenidaScreen
import com.upt.descansa.feature.inicio.InicioScreen
import com.upt.descansa.feature.manana.MananaScreen
import com.upt.descansa.feature.modonoche.ModoNocheScreen
import com.upt.descansa.feature.noche.alarma.AlarmaScreen
import com.upt.descansa.feature.noche.pendientes.PendientesScreen
import com.upt.descansa.feature.noche.relajacion.RelajacionScreen
import com.upt.descansa.feature.noche.respiracion.RespiracionScreen
import com.upt.descansa.feature.noche.sonidos.SonidosScreen

/** Las 4 pestañas de la barra inferior (sección 4: Inicio, Noche, Despertar, Aprender). */
private enum class Pestana(val ruta: RutaApp, @StringRes val etiqueta: Int, val icono: ImageVector) {
    INICIO(RutaInicio, R.string.nav_inicio, Icons.Outlined.Home),
    NOCHE(RutaPendientes, R.string.nav_noche, Icons.Outlined.Bedtime),
    DESPERTAR(RutaManana, R.string.nav_despertar, Icons.Outlined.Alarm),
    APRENDER(RutaAprender, R.string.nav_aprender, Icons.Outlined.Book),
}

/** La barra inferior solo aparece en Inicio y Aprender (regla del prototipo). */
private fun NavDestination?.muestraBarra(): Boolean =
    this != null && (hasRoute(RutaInicio::class) || hasRoute(RutaAprender::class))

/**
 * Raíz visual de la app: NavHost con las 11 pantallas + barra inferior.
 *
 * Las pantallas no conocen al NavController: reciben lambdas como `onSiguiente`.
 * Así cada integrante puede probar y previsualizar su pantalla sin la navegación,
 * y TODO el flujo de la app se lee en este único archivo.
 *
 * @param bienvenidaCompletada decide si la app abre en Bienvenida o en Inicio.
 * @param abrirManana true cuando se acaba de apagar la alarma: se abre el resumen.
 * @param onMananaAbierta avisa que ya se navegó, para no repetirlo.
 * @param nocheEnCurso true si al abrir la app ya hay una noche activa: se abre el modo noche.
 */
@Composable
fun DescansaNavegacion(
    bienvenidaCompletada: Boolean,
    abrirManana: Boolean = false,
    onMananaAbierta: () -> Unit = {},
    nocheEnCurso: Boolean = false,
    navController: NavHostController = rememberNavController(),
) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoActual = entradaActual?.destination

    Scaffold(
        // En modo noche, el fondo detrás de las barras del sistema también va casi negro.
        containerColor = if (destinoActual?.hasRoute(RutaModoNoche::class) == true) FondoModoNoche else Fondo,
        bottomBar = {
            if (destinoActual.muestraBarra()) {
                BarraInferior(destinoActual, onSeleccionar = { navController.irAPestana(it.ruta) })
            }
        },
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = if (bienvenidaCompletada) RutaInicio else RutaBienvenida,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .consumeWindowInsets(paddingInterno),
        ) {
            composable<RutaBienvenida> {
                BienvenidaScreen(
                    onComenzar = {
                        // Bienvenida no vuelve a aparecer con "atrás".
                        navController.navigate(RutaInicio) { popUpTo(RutaBienvenida) { inclusive = true } }
                    },
                )
            }
            composable<RutaInicio> {
                InicioScreen(
                    onPrepararNoche = { navController.navigate(RutaPendientes) },
                    onAbrir = { ruta -> navController.navigate(ruta) },
                )
            }

            // --- Flujo "Preparar mi noche": cada paso lleva al siguiente (o lo omite). ---
            composable<RutaPendientes> {
                PendientesScreen(onAtras = navController::popBackStack, onSiguiente = { navController.navigate(RutaRespiracion) })
            }
            composable<RutaRespiracion> {
                RespiracionScreen(onAtras = navController::popBackStack, onSiguiente = { navController.navigate(RutaRelajacion) })
            }
            composable<RutaRelajacion> {
                RelajacionScreen(onAtras = navController::popBackStack, onSiguiente = { navController.navigate(RutaSonidos) })
            }
            composable<RutaSonidos> {
                SonidosScreen(onAtras = navController::popBackStack, onSiguiente = { navController.navigate(RutaAlarma) })
            }
            composable<RutaAlarma> {
                AlarmaScreen(
                    onAtras = navController::popBackStack,
                    onActivarModoNoche = {
                        // Al activar, la pila queda Inicio → Modo noche (no se regresa a los pasos).
                        navController.navigate(RutaModoNoche) { popUpTo(RutaInicio) }
                    },
                )
            }
            composable<RutaModoNoche> {
                ModoNocheScreen(
                    onDetenido = { navController.navigate(RutaManana) { popUpTo(RutaInicio) } },
                )
            }
            composable<RutaManana> {
                MananaScreen(onVolverAlInicio = { navController.irAPestana(RutaInicio) })
            }
            composable<RutaAprender> { AprenderScreen() }
            composable<RutaAjustes> {
                AjustesScreen(
                    onAtras = navController::popBackStack,
                    // Simulador (modo desarrollador): igual que activar una noche real.
                    onSimulacionIniciada = { navController.navigate(RutaModoNoche) { popUpTo(RutaInicio) } },
                )
            }
        }

        // Estos efectos van DESPUÉS del NavHost: así la gráfica de navegación ya existe
        // cuando navegan (si fueran antes, en un arranque en frío fallarían).

        // Se abrió la app con una noche en curso: directo al modo noche.
        LaunchedEffect(nocheEnCurso) {
            if (nocheEnCurso && !abrirManana) navController.navigate(RutaModoNoche) { popUpTo(RutaInicio) }
        }
        // Al apagar la alarma: Inicio → Mañana (también quita el modo noche de la pila).
        LaunchedEffect(abrirManana) {
            if (abrirManana) {
                navController.navigate(RutaManana) { popUpTo(RutaInicio) }
                onMananaAbierta()
            }
        }
    }
}

@Composable
private fun BarraInferior(destinoActual: NavDestination?, onSeleccionar: (Pestana) -> Unit) {
    // Column: la línea divisoria va ENCIMA de la barra (sin ella se enciman).
    Column {
        HorizontalDivider(color = BordeTenue)
        NavigationBar(containerColor = SuperficieSecundaria) {
            Pestana.entries.forEach { pestana ->
                NavigationBarItem(
                    selected = destinoActual?.hasRoute(pestana.ruta::class) == true,
                    onClick = { onSeleccionar(pestana) },
                    // La etiqueta ya dice qué es; TalkBack la lee.
                    icon = { Icon(pestana.icono, contentDescription = null) },
                    label = { Text(stringResource(pestana.etiqueta), style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Lavanda,
                        selectedTextColor = Lavanda,
                        unselectedIconColor = TextoAtenuado,
                        unselectedTextColor = TextoAtenuado,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}

/** Navegación "de pestaña": no apila pantallas repetidas y vuelve a Inicio como base. */
private fun NavHostController.irAPestana(ruta: RutaApp) {
    navigate(ruta) {
        popUpTo(RutaInicio) { saveState = ruta != RutaInicio }
        launchSingleTop = true
        restoreState = true
    }
}
