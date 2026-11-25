package com.upt.descansa

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.upt.descansa.core.navigation.DescansaNavegacion
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.sistema.alarma.EstadoTimbre
import com.upt.descansa.sistema.alarma.TimbreActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity principal: todas las pantallas son composables que Navigation
 * Compose cambia dentro de ella. La pantalla de timbre es otra Activity
 * (`sistema/alarma/TimbreActivity`) porque debe abrirse sobre el bloqueo.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    /** true cuando la pantalla de timbre pidió abrir el resumen de la mañana. */
    private var abrirManana by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Solo en la primera creación: si se rota la pantalla no se vuelve a abrir.
        if (savedInstanceState == null) leerIntent(intent)
        // App siempre oscura: íconos claros en las barras del sistema.
        val barras = SystemBarStyle.dark(Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barras, navigationBarStyle = barras)

        // Si la alarma está sonando y se abre la app, se muestra la pantalla de timbre.
        // Sin esto, quien negó las notificaciones no tendría dónde apagarla.
        // RESUMED y no STARTED: detrás de la pantalla de bloqueo la app está
        // "iniciada" pero Android bloquea abrir otra pantalla; al volver al frente
        // (ya desbloqueado) se intenta de nuevo.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                EstadoTimbre.sonando.collect { sonando ->
                    if (sonando) startActivity(Intent(this@MainActivity, TimbreActivity::class.java))
                }
            }
        }

        setContent {
            DescansaTheme {
                val arranque by viewModel.arranque.collectAsStateWithLifecycle()
                // Mientras se leen los ajustes se ve solo el fondo de la ventana (oscuro).
                val listo = arranque as? EstadoArranque.Listo ?: return@DescansaTheme
                DescansaNavegacion(
                    bienvenidaCompletada = listo.bienvenidaCompletada,
                    abrirManana = abrirManana,
                    onMananaAbierta = { abrirManana = false },
                    nocheEnCurso = listo.nocheEnCurso,
                )
            }
        }
    }

    /** `singleTop`: si la app ya estaba abierta, el Intent nuevo llega aquí. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        leerIntent(intent)
    }

    private fun leerIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_ABRIR_MANANA, false) == true) abrirManana = true
    }

    companion object {
        const val EXTRA_ABRIR_MANANA = "abrir_manana"
    }
}
