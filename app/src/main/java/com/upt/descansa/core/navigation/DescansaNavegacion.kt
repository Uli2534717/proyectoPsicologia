package com.upt.descansa.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.EntradaMenu
import com.upt.descansa.core.designsystem.components.LocalAbrirMenu
import com.upt.descansa.core.designsystem.components.MenuPantallaCompleta
import com.upt.descansa.core.designsystem.motion.Movimiento
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
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

/**
 * Raíz visual de la app: NavHost con las 11 pantallas + menú a pantalla completa.
 *
 * Ya no hay barra inferior: cada pantalla muestra "MENÚ" arriba a la derecha
 * (lo pide `LocalAbrirMenu`) y el menú lista todas las secciones.
 *
 * Transición "cartel": la pantalla nueva entra desde abajo y la anterior sale
 * hacia arriba (300 ms); como cada sección tiene su color, la palabra gigante
 * cambia de turquesa a amarillo o al revés. Con animaciones reducidas: fundido.
 *
 * Las pantallas no conocen al NavController: reciben lambdas. Todo el flujo
 * de la app se lee en este archivo.
 */
@Composable
fun DescansaNavegacion(
    bienvenidaCompletada: Boolean,
    abrirManana: Boolean = false,
    onMananaAbierta: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    var menuAbierto by rememberSaveable { mutableStateOf(false) }
    val reducidas = animacionesReducidas()

    // Al detener la alarma se abre el resumen de la mañana (sección 6.3).
    LaunchedEffect(abrirManana, bienvenidaCompletada) {
        if (abrirManana && bienvenidaCompletada) {
            navController.navigate(RutaManana) { popUpTo(RutaInicio) }
            onMananaAbierta()
        }
    }

    CompositionLocalProvider(LocalAbrirMenu provides { menuAbierto = true }) {
        NavHost(
            navController = navController,
            startDestination = if (bienvenidaCompletada) RutaInicio else RutaBienvenida,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            enterTransition = { entradaCartel(reducidas) },
            exitTransition = { salidaCartel(reducidas) },
            popEnterTransition = { entradaCartel(reducidas, regreso = true) },
            popExitTransition = { salidaCartel(reducidas, regreso = true) },
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
                ModoNocheScreen(onDetenido = { navController.navigate(RutaManana) { popUpTo(RutaInicio) } })
            }
            composable<RutaManana> {
                MananaScreen(onVolverAlInicio = { navController.irASeccion(RutaInicio) })
            }
            composable<RutaAprender> { AprenderScreen() }
            composable<RutaAjustes> { AjustesScreen(onAtras = navController::popBackStack) }
        }
    }

    if (menuAbierto) {
        val colores = LocalColoresCartel.current
        val ir = { ruta: RutaApp ->
            menuAbierto = false
            navController.irASeccion(ruta)
        }
        MenuPantallaCompleta(
            entradas = listOf(
                EntradaMenu(stringResource(R.string.menu_inicio), colores.amarillo) { ir(RutaInicio) },
                EntradaMenu(stringResource(R.string.menu_noche), colores.turquesa) { ir(RutaPendientes) },
                EntradaMenu(stringResource(R.string.menu_respiracion), colores.turquesa) { ir(RutaRespiracion) },
                EntradaMenu(stringResource(R.string.menu_relajacion), colores.turquesa) { ir(RutaRelajacion) },
                EntradaMenu(stringResource(R.string.menu_sonidos), colores.turquesa) { ir(RutaSonidos) },
                EntradaMenu(stringResource(R.string.menu_alarma), colores.turquesa) { ir(RutaAlarma) },
                EntradaMenu(stringResource(R.string.menu_manana), colores.amarillo) { ir(RutaManana) },
                EntradaMenu(stringResource(R.string.menu_aprender), colores.amarillo) { ir(RutaAprender) },
                EntradaMenu(stringResource(R.string.menu_ajustes), colores.amarillo) { ir(RutaAjustes) },
            ),
            onCerrar = { menuAbierto = false },
        )
    }
}

/** Entra desde abajo (o desde arriba al regresar), como un cartel nuevo. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.entradaCartel(
    reducidas: Boolean,
    regreso: Boolean = false,
): EnterTransition {
    val fundido = fadeIn(Movimiento.entrada(Movimiento.MEDIA))
    if (reducidas) return fundido
    return slideInVertically(Movimiento.entrada(Movimiento.MEDIA)) { alto -> if (regreso) -alto / 6 else alto / 6 } + fundido
}

/** Sale hacia arriba (o hacia abajo al regresar). */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.salidaCartel(
    reducidas: Boolean,
    regreso: Boolean = false,
): ExitTransition {
    val fundido = fadeOut(Movimiento.salida(Movimiento.MEDIA))
    if (reducidas) return fundido
    return slideOutVertically(Movimiento.salida(Movimiento.MEDIA)) { alto -> if (regreso) alto / 6 else -alto / 6 } + fundido
}

/**
 * Ir a una sección desde el menú o al terminar: no apila pantallas repetidas y
 * deja Inicio como base de la pila.
 */
private fun NavHostController.irASeccion(ruta: RutaApp) {
    navigate(ruta) {
        popUpTo(RutaInicio) { inclusive = ruta == RutaInicio }
        launchSingleTop = true
    }
}
