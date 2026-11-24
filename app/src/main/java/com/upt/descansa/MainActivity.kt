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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.descansa.core.navigation.DescansaNavegacion
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.sistema.alarma.TimbreActivity
import com.upt.descansa.sistema.alarma.TimbreService
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la interfaz principal: todas las pantallas son composables
 * que Navigation Compose cambia dentro de ella. La pantalla de timbre es otra
 * Activity ([TimbreActivity]) porque debe abrirse sobre la pantalla de bloqueo.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    /** `true` cuando se llegó aquí al detener la alarma: hay que abrir el resumen de la mañana. */
    private var abrirManana by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // App siempre oscura: íconos claros en las barras del sistema.
        val barras = SystemBarStyle.dark(Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barras, navigationBarStyle = barras)
        abrirManana = intent.getBooleanExtra(EXTRA_ABRIR_MANANA, false)

        setContent {
            DescansaTheme {
                val arranque by viewModel.arranque.collectAsStateWithLifecycle()
                // Mientras se leen los ajustes se ve solo el fondo de la ventana (oscuro).
                val listo = arranque as? EstadoArranque.Listo ?: return@DescansaTheme
                DescansaNavegacion(
                    bienvenidaCompletada = listo.bienvenidaCompletada,
                    abrirManana = abrirManana,
                    onMananaAbierta = { abrirManana = false },
                )
            }
        }
    }

    /** La Activity ya estaba abierta (launchMode singleTop) y llega un intent nuevo. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_ABRIR_MANANA, false)) abrirManana = true
    }

    override fun onResume() {
        super.onResume()
        // Respaldo: si la alarma está sonando pero el usuario no vio la pantalla
        // de timbre (por ejemplo, negó las notificaciones), se la mostramos al abrir la app.
        if (TimbreService.sonando.value) {
            startActivity(TimbreActivity.intent(this, TimbreService.posponerMinutosActual))
        }
    }

    companion object {
        const val EXTRA_ABRIR_MANANA = "abrir_manana"
    }
}
