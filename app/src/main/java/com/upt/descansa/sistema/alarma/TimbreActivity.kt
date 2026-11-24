package com.upt.descansa.sistema.alarma

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.descansa.MainActivity
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.components.BotonPildora
import com.upt.descansa.core.designsystem.components.BotonSecundario
import com.upt.descansa.core.designsystem.components.PantallaCartel
import com.upt.descansa.core.designsystem.motion.animacionesReducidas
import com.upt.descansa.core.designsystem.theme.LocalColoresCartel
import com.upt.descansa.core.designsystem.theme.TemaNocturno
import com.upt.descansa.core.designsystem.theme.TipoCartel
import com.upt.descansa.core.util.formatearHora
import kotlinx.coroutines.delay
import java.time.LocalTime

/**
 * Pantalla de timbre (sección 6.3): aparece encima de la pantalla de bloqueo
 * (aunque tenga PIN o huella) y enciende la pantalla.
 *
 * Es una Activity aparte (no una ruta de la navegación) porque necesita
 * `showWhenLocked` y `turnScreenOn`, y porque debe funcionar en Direct Boot,
 * cuando la app principal todavía no puede leer sus datos.
 * No usa Hilt ni DataStore por esa misma razón.
 */
class TimbreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // También están en el manifiesto; aquí se refuerzan por si el sistema recrea la Activity.
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val barras = SystemBarStyle.dark(Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barras, navigationBarStyle = barras)

        val posponerMinutos = intent.getIntExtra(EXTRA_POSPONER, 5)

        setContent {
            TemaNocturno {
                val sonando by TimbreService.sonando.collectAsStateWithLifecycle()
                // Si la alarma se detuvo desde la notificación, esta pantalla se cierra sola.
                LaunchedEffect(sonando) { if (!sonando) finishAndRemoveTask() }
                // "Atrás" no hace nada: la alarma se apaga solo con Detener o Posponer.
                BackHandler { }
                TimbreContenido(
                    posponerMinutos = posponerMinutos,
                    onDetener = ::detener,
                    onPosponer = ::posponer,
                )
            }
        }
    }

    private fun detener() {
        startService(TimbreService.intentDetener(this))
        // Al detener se abre el resumen de la mañana (sección 6.3). Si el teléfono
        // sigue bloqueado, Android lo muestra después de desbloquear.
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_ABRIR_MANANA, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        )
        finishAndRemoveTask()
    }

    private fun posponer() {
        startService(TimbreService.intentPosponer(this))
        finishAndRemoveTask()
    }

    companion object {
        private const val EXTRA_POSPONER = "posponer_minutos"

        fun intent(context: Context, posponerMinutos: Int): Intent =
            Intent(context, TimbreActivity::class.java)
                .putExtra(EXTRA_POSPONER, posponerMinutos)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
    }
}

/**
 * Contenido de la pantalla de timbre (turquesa: DESPIERTA), siempre en tema nocturno.
 * La palabra DESPIERTA crece de 0.9 a 1.0 en 30 s, al mismo ritmo que el volumen.
 */
@Composable
private fun TimbreContenido(posponerMinutos: Int, onDetener: () -> Unit, onPosponer: () -> Unit) {
    var minutoActual by remember { mutableIntStateOf(LocalTime.now().let { it.hour * 60 + it.minute }) }
    LaunchedEffect(Unit) {
        while (true) {
            val ahora = LocalTime.now()
            minutoActual = ahora.hour * 60 + ahora.minute
            delay(1_000)
        }
    }
    val reducidas = animacionesReducidas()
    val escala = remember { Animatable(if (reducidas) 1f else 0.9f) }
    // 30 s = la misma rampa que el volumen de ReproductorAlarma.
    LaunchedEffect(Unit) { escala.animateTo(1f, tween(30_000, easing = LinearEasing)) }

    PantallaCartel(
        palabra = stringResource(R.string.palabra_alarma),
        colorPalabra = LocalColoresCartel.current.turquesa,
        titulo = stringResource(R.string.timbre_titulo),
        descripcion = stringResource(R.string.timbre_subtitulo),
        imagen = R.drawable.img_alarma_despertador,
        descripcionImagen = stringResource(R.string.img_alarma_despertador),
        nocturna = true,
        // Crece desde la esquina izquierda, como si el cartel se acercara.
        modificadorPalabra = Modifier.graphicsLayer {
            scaleX = escala.value
            scaleY = escala.value
            transformOrigin = TransformOrigin(0f, 0.5f)
        },
        accionPrincipal = {
            BotonSecundario(
                stringResource(R.string.timbre_posponer, posponerMinutos),
                onClick = onPosponer,
                modifier = Modifier.fillMaxWidth(),
            )
            BotonPildora(stringResource(R.string.timbre_detener), onClick = onDetener)
        },
    ) {
        Text(formatearHora(minutoActual), style = TipoCartel.hora, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun TimbrePreview() {
    TemaNocturno { TimbreContenido(posponerMinutos = 5, onDetener = {}, onPosponer = {}) }
}
