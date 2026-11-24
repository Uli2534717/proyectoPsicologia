package com.upt.descansa

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.descansa.core.navigation.DescansaNavegacion
import com.upt.descansa.core.theme.DescansaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la interfaz: todas las pantallas son composables que
 * Navigation Compose cambia dentro de ella. (La pantalla de timbre de la
 * alarma será otra Activity en la Fase 1, porque debe abrirse sobre el bloqueo.)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // App siempre oscura: íconos claros en las barras del sistema.
        val barras = SystemBarStyle.dark(Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barras, navigationBarStyle = barras)

        setContent {
            DescansaTheme {
                val arranque by viewModel.arranque.collectAsStateWithLifecycle()
                // Mientras se leen los ajustes se ve solo el fondo de la ventana (oscuro).
                val listo = arranque as? EstadoArranque.Listo ?: return@DescansaTheme
                DescansaNavegacion(bienvenidaCompletada = listo.bienvenidaCompletada)
            }
        }
    }
}
