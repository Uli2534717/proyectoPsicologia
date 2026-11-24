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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.descansa.MainActivity
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonPrincipal
import com.upt.descansa.core.designsystem.BotonSecundario
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.Fondo
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.Turquesa
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
            DescansaTheme {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))
        Icon(Icons.Outlined.WbTwilight, contentDescription = null, tint = Turquesa, modifier = Modifier.size(56.dp))
        Text(formatearHora(minutoActual), style = MaterialTheme.typography.displayLarge, color = Texto)
        Text(stringResource(R.string.timbre_titulo), style = MaterialTheme.typography.headlineMedium, color = Texto)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.timbre_subtitulo), style = MaterialTheme.typography.bodyLarge, color = TextoAtenuado)
        Spacer(Modifier.weight(1f))
        BotonSecundario(texto = stringResource(R.string.timbre_posponer, posponerMinutos), onClick = onPosponer)
        Spacer(Modifier.height(16.dp))
        BotonPrincipal(texto = stringResource(R.string.timbre_detener), onClick = onDetener)
        Spacer(Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1B2D, heightDp = 800)
@Composable
private fun TimbrePreview() {
    DescansaTheme { TimbreContenido(posponerMinutos = 5, onDetener = {}, onPosponer = {}) }
}
