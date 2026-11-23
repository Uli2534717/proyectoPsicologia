package com.upt.dormirbien

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.dormirbien.core.navigation.DormirBienNavegacion
import com.upt.dormirbien.core.theme.DormirBienTheme
import com.upt.dormirbien.core.theme.usaTemaOscuro
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la app ("single activity"): todas las pantallas son
 * composables que cambia Navigation Compose dentro de ella.
 *
 * `@AndroidEntryPoint` permite que Hilt le entregue dependencias (aquí, el
 * [MainViewModel]).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dibuja detrás de las barras del sistema (estado y navegación).
        enableEdgeToEdge()

        setContent {
            val modoTema by viewModel.modoTema.collectAsStateWithLifecycle()
            val oscuro = modoTema.usaTemaOscuro()

            // Íconos de las barras del sistema: claros sobre tema oscuro y
            // oscuros sobre tema claro, según el tema de la APP (no del teléfono).
            DisposableEffect(oscuro) {
                val estilo = if (oscuro) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
                onDispose { }
            }

            DormirBienTheme(modoTema = modoTema) {
                DormirBienNavegacion()
            }
        }
    }
}
