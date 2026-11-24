package com.upt.descansa.sistema.alarma

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upt.descansa.MainActivity
import com.upt.descansa.R
import com.upt.descansa.core.designsystem.BotonPrincipal
import com.upt.descansa.core.designsystem.BotonSecundario
import com.upt.descansa.core.theme.DescansaTheme
import com.upt.descansa.core.theme.FondoModoNoche
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.util.formatearHora
import java.time.LocalTime

/**
 * Pantalla de timbre: aparece sobre la pantalla de bloqueo cuando suena la alarma.
 *
 * Es una Activity aparte (no una ruta de Navigation) porque necesita
 * `showWhenLocked`/`turnScreenOn` y no debe mezclarse con la pila de la app.
 * No reproduce nada: solo manda "apagar" o "posponer" al [TimbreService].
 */
class TimbreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Encima del bloqueo, enciende la pantalla y no la deja apagarse mientras suena.
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val barras = SystemBarStyle.dark(Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barras, navigationBarStyle = barras)

        val ahora = LocalTime.now()
        val hora = formatearHora(ahora.hour * 60 + ahora.minute)

        setContent {
            DescansaTheme {
                val sonando by EstadoTimbre.sonando.collectAsStateWithLifecycle()
                // Si se apagó desde la notificación (o ya no suena), esta pantalla sobra.
                LaunchedEffect(sonando) { if (!sonando) finish() }
                TimbreContenido(
                    hora = hora,
                    onPosponer = {
                        TimbreService.enviar(this, TimbreService.ACCION_POSPONER)
                        finish()
                    },
                    onApagar = {
                        TimbreService.enviar(this, TimbreService.ACCION_APAGAR)
                        // Al apagar se abre el resumen de la mañana.
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .putExtra(MainActivity.EXTRA_ABRIR_MANANA, true)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        )
                        finish()
                    },
                )
            }
        }
    }
}

@Composable
fun TimbreContenido(hora: String, onPosponer: () -> Unit, onApagar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoModoNoche)
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.weight(1f))
        Icon(Icons.Outlined.Alarm, contentDescription = null, tint = Lavanda, modifier = Modifier.size(56.dp))
        Text(hora, style = MaterialTheme.typography.displayLarge, color = Texto)
        Text(stringResource(R.string.timbre_titulo), style = MaterialTheme.typography.titleLarge, color = Texto)
        Text(
            stringResource(R.string.timbre_saludo),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoAtenuado,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        BotonPrincipal(texto = stringResource(R.string.timbre_apagar), onClick = onApagar)
        BotonSecundario(texto = stringResource(R.string.timbre_posponer), onClick = onPosponer)
        Spacer(Modifier.height(8.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070D17, heightDp = 800)
@Composable
private fun TimbrePreview() {
    DescansaTheme { TimbreContenido(hora = "07:00", onPosponer = {}, onApagar = {}) }
}
