package com.upt.descansa.sistema.alarma

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log

/**
 * Sonido y vibración de la alarma (sección 6.3).
 *
 * - Usa el canal de ALARMA (`USAGE_ALARM`): suena aunque el volumen multimedia
 *   esté en cero o el teléfono en silencio/vibración, y pasa el filtro de
 *   No molestar en modo "solo alarmas". Android 17 además exige este uso de
 *   audio para que una app con alarmas exactas pueda sonar en segundo plano.
 * - Volumen creciente: del 15 % al 100 % en 30 segundos, para despertar sin sobresalto.
 *   Es el volumen del reproductor dentro del volumen de alarma que eligió el usuario.
 * - Si el tono del sistema no se puede abrir (por ejemplo, justo después de
 *   reiniciar sin desbloquear), usa un pitido de respaldo. La alarma nunca queda muda.
 */
class ReproductorAlarma(private val context: Context) {

    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val manejador = Handler(Looper.getMainLooper())
    private var reproductor: MediaPlayer? = null
    private var tonoRespaldo: ToneGenerator? = null
    private var focoAudio: AudioFocusRequest? = null
    private var volumen = VOLUMEN_INICIAL

    private val atributos = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val vibrador: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }

    fun iniciar() {
        pedirFocoAudio()
        val abierto = runCatching { abrirTonoDelSistema() }
            .onFailure { Log.w(TAG, "No se pudo abrir el tono de alarma; uso el pitido de respaldo", it) }
            .isSuccess
        if (!abierto) iniciarPitidoRespaldo()
        iniciarVibracion()
        manejador.post(subirVolumen)
    }

    fun detener() {
        manejador.removeCallbacksAndMessages(null)
        reproductor?.runCatching { stop(); release() }
        reproductor = null
        tonoRespaldo?.release()
        tonoRespaldo = null
        vibrador?.cancel()
        focoAudio?.let { audioManager.abandonAudioFocusRequest(it) }
        focoAudio = null
    }

    private fun abrirTonoDelSistema() {
        reproductor = MediaPlayer().apply {
            setAudioAttributes(atributos)
            setDataSource(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
            isLooping = true
            setVolume(VOLUMEN_INICIAL, VOLUMEN_INICIAL)
            prepare()
            start()
        }
    }

    /** Pitido en el canal de alarma, repetido cada 1.5 s. */
    private fun iniciarPitidoRespaldo() {
        tonoRespaldo = runCatching { ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME) }.getOrNull()
        val pitar = object : Runnable {
            override fun run() {
                tonoRespaldo?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 800)
                manejador.postDelayed(this, 1_500)
            }
        }
        manejador.post(pitar)
    }

    /** Patrón suave: 0.6 s vibrando, 0.9 s de pausa, repetido. */
    private fun iniciarVibracion() {
        val vibrador = vibrador ?: return
        if (!vibrador.hasVibrator()) return
        val efecto = VibrationEffect.createWaveform(longArrayOf(0, 600, 900), 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrador.vibrate(efecto, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM))
        } else {
            @Suppress("DEPRECATION")
            vibrador.vibrate(efecto, atributos)
        }
    }

    /** Sube el volumen un paso cada segundo hasta llegar al 100 %. */
    private val subirVolumen = object : Runnable {
        override fun run() {
            volumen = (volumen + PASO_VOLUMEN).coerceAtMost(1f)
            reproductor?.setVolume(volumen, volumen)
            if (volumen < 1f) manejador.postDelayed(this, 1_000)
        }
    }

    /** Pide el foco de audio para que otros sonidos (música, sonidos relajantes) se pausen. */
    private fun pedirFocoAudio() {
        val solicitud = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(atributos)
            .build()
        audioManager.requestAudioFocus(solicitud)
        focoAudio = solicitud
    }

    private companion object {
        const val TAG = "ReproductorAlarma"
        const val VOLUMEN_INICIAL = 0.15f
        const val SEGUNDOS_RAMPA = 30
        const val PASO_VOLUMEN = (1f - VOLUMEN_INICIAL) / SEGUNDOS_RAMPA
    }
}
