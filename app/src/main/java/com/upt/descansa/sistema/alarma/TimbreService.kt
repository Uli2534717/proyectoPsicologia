package com.upt.descansa.sistema.alarma

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.UserManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.upt.descansa.R
import com.upt.descansa.core.util.formatearHora
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.repository.ControlServicioNocturno
import com.upt.descansa.domain.usecase.noche.ApagarAlarmaUseCase
import com.upt.descansa.domain.usecase.noche.PosponerAlarmaUseCase
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/**
 * ¿Está sonando la alarma ahora mismo? La pantalla de timbre lo observa para
 * cerrarse sola si la alarma se apagó desde la notificación.
 */
object EstadoTimbre {
    internal val sonandoMutable = MutableStateFlow(false)
    val sonando: StateFlow<Boolean> = sonandoMutable.asStateFlow()
}

/**
 * Servicio en primer plano que hace sonar la alarma.
 *
 * Por qué un servicio y no solo la pantalla: Android puede cerrar una Activity
 * en cualquier momento, y además la pantalla de timbre necesita permisos que el
 * usuario puede negar. El servicio en primer plano es lo más difícil de matar
 * que tiene una app, así que el SONIDO no depende de que se vea la pantalla.
 *
 * - Volumen creciente: empieza al 10 % y sube durante 60 s, para despertar sin sobresalto.
 * - Vibra en paralelo, por si el volumen de alarmas está muy bajo.
 * - Si nadie la apaga en 10 minutos, se calla sola (como el reloj de Android).
 * - Tipos `systemExempted|mediaPlayback` (ver docs/DECISIONES.md, D-11).
 * - Es `directBootAware`: suena aunque el teléfono se haya reiniciado y nadie lo
 *   haya desbloqueado. Por eso sus dependencias son `Lazy`: antes del desbloqueo
 *   no se pueden leer Room, DataStore ni las preferencias normales, y solo se
 *   crean si el teléfono ya está desbloqueado (D-14).
 */
@AndroidEntryPoint
class TimbreService : Service() {

    @Inject lateinit var apagarAlarma: Lazy<ApagarAlarmaUseCase>
    @Inject lateinit var posponerAlarma: Lazy<PosponerAlarmaUseCase>
    @Inject lateinit var servicioNocturno: Lazy<ControlServicioNocturno>

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var reproductor: MediaPlayer? = null
    private var pitido: ToneGenerator? = null
    private var focoAudio: AudioFocusRequest? = null
    private var vibrador: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var subidaVolumen: Job? = null

    /** Cuándo empezó a sonar (se guarda en la noche como `disparada_en`). */
    private var sonandoDesde = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACCION_APAGAR -> terminar { apagar() }
            ACCION_POSPONER -> terminar { posponer() }
            else -> sonar()
        }
        // NOT_STICKY: si Android matara el servicio, no lo revive sin datos.
        // La alarma guardada en AlarmManager sigue siendo el respaldo.
        return START_NOT_STICKY
    }

    private fun sonar() {
        // Primero se marca como "sonando" y luego se publica la notificación:
        // así la pantalla de timbre nunca se abre y se cierra sola por una carrera.
        val yaSonaba = EstadoTimbre.sonando.value
        EstadoTimbre.sonandoMutable.value = true
        iniciarEnPrimerPlano()
        if (yaSonaba) return

        // Ya está sonando (por sueño ligero o por la hora límite): no hace falta seguir midiendo.
        if (desbloqueado()) servicioNocturno.get().detener()
        sonandoDesde = System.currentTimeMillis()
        // Mantiene la CPU despierta mientras suena (se libera sola a los 10 min).
        wakeLock = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Descansa:timbre")
            .apply { acquire(DURACION_MAXIMA_MS) }
        asegurarVolumenAudible()
        pedirFocoAudio()
        reproducirSonido()
        if (reproductor == null) iniciarPitido()
        vibrar()

        subidaVolumen = scope.launch {
            val pasos = 30
            for (paso in 1..pasos) {
                delay(SUBIDA_VOLUMEN_MS / pasos)
                val volumen = VOLUMEN_INICIAL + (1f - VOLUMEN_INICIAL) * paso / pasos
                reproductor?.setVolume(volumen, volumen)
            }
            delay(DURACION_MAXIMA_MS - SUBIDA_VOLUMEN_MS)
            // Nadie respondió: se calla y la noche se cierra como si la hubieran apagado.
            terminar { apagar() }
        }
    }

    private fun desbloqueado(): Boolean = getSystemService(UserManager::class.java).isUserUnlocked

    /**
     * Cierra la noche. Si el teléfono sigue bloqueado tras un reinicio, solo se
     * cancela la alarma y se anota: la noche y el No molestar se cierran al desbloquear.
     */
    private suspend fun apagar() {
        val ahora = System.currentTimeMillis()
        val disparadaEn = sonandoDesde.takeIf { it > 0L } ?: ahora
        if (desbloqueado()) {
            apagarAlarma.get()(disparadaEn = disparadaEn, ahora = ahora)
        } else {
            AlarmSchedulerImpl(applicationContext).apply {
                cancelar()
                cierrePendienteDesde = disparadaEn
            }
        }
    }

    /** Sin desbloquear no se pueden leer los ajustes: se usan los minutos por defecto. */
    private suspend fun posponer() {
        if (desbloqueado()) {
            posponerAlarma.get()()
        } else {
            val minutos = AjustesUsuario().posponerMinutos
            AlarmSchedulerImpl(applicationContext).programar(System.currentTimeMillis() + minutos * 60_000L)
        }
    }

    /** Detiene el sonido, ejecuta [accion] (guardar en la BD o reprogramar) y cierra el servicio. */
    private fun terminar(accion: suspend () -> Unit) {
        detenerSonido()
        scope.launch {
            accion()
            ServiceCompat.stopForeground(this@TimbreService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun detenerSonido() {
        subidaVolumen?.cancel()
        reproductor?.run {
            stop()
            release()
        }
        reproductor = null
        pitido?.release()
        pitido = null
        vibrador?.cancel()
        focoAudio?.let { getSystemService(AudioManager::class.java).abandonAudioFocusRequest(it) }
        focoAudio = null
        wakeLock?.let { if (it.isHeld) it.release() }
        EstadoTimbre.sonandoMutable.value = false
    }

    override fun onDestroy() {
        detenerSonido()
        scope.cancel()
        super.onDestroy()
    }

    private fun iniciarEnPrimerPlano() {
        val notificacion = crearNotificacion()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // systemExempted: permitido por tener permiso de alarmas exactas.
            // mediaPlayback: le dice a Android que este servicio REPRODUCE audio; sin él,
            // Android 16+ marca el audio de una app en segundo plano para silenciarlo.
            val tipos = ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED or
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            startForeground(ID_NOTIFICACION, notificacion, tipos)
        } else {
            startForeground(ID_NOTIFICACION, notificacion)
        }
    }

    /**
     * Si el volumen de alarmas está en 0, la alarma no se oiría: se sube a la mitad.
     * Es lo único que cambiamos del teléfono sin preguntar, porque la alarma la pidió el usuario.
     */
    private fun asegurarVolumenAudible() {
        val audio = getSystemService(AudioManager::class.java)
        try {
            if (audio.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
                val mitad = (audio.getStreamMaxVolume(AudioManager.STREAM_ALARM) / 2).coerceAtLeast(1)
                audio.setStreamVolume(AudioManager.STREAM_ALARM, mitad, 0)
            }
        } catch (_: SecurityException) {
            // Algunos fabricantes lo bloquean; queda la vibración.
        }
    }

    private fun reproducirSonido() {
        val atributos = AudioAttributes.Builder()
            // USAGE_ALARM: suena por el canal de alarmas, que No molestar deja pasar.
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        // Se prueban varios tonos: en algunos emuladores o teléfonos no hay tono de alarma.
        for (uri in tonosCandidatos()) {
            val player = MediaPlayer()
            try {
                player.setAudioAttributes(atributos)
                player.setDataSource(this, uri)
                player.isLooping = true
                player.setVolume(VOLUMEN_INICIAL, VOLUMEN_INICIAL)
                player.prepare()
                player.start()
                reproductor = player
                return
            } catch (_: Exception) {
                player.release()
            }
        }
    }

    /**
     * Respaldo si ningún tono se pudo abrir (por ejemplo, tras reiniciar sin
     * desbloquear, cuando los tonos del usuario aún no se pueden leer): un pitido
     * por el canal de alarma cada 1.5 s. La alarma nunca queda muda.
     */
    private fun iniciarPitido() {
        val tono = runCatching { ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME) }.getOrNull() ?: return
        pitido = tono
        scope.launch {
            while (pitido != null) {
                tono.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 800)
                delay(1_500)
            }
        }
    }

    /** Pide el foco de audio: la música o los sonidos para dormir se pausan mientras suena. */
    private fun pedirFocoAudio() {
        val atributos = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        val solicitud = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(atributos)
            .build()
        getSystemService(AudioManager::class.java).requestAudioFocus(solicitud)
        focoAudio = solicitud
    }

    private fun tonosCandidatos(): List<Uri> = listOfNotNull(
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
    )

    private fun vibrar() {
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
        if (!vib.hasVibrator()) return
        vibrador = vib
        // 0.8 s vibrando, 0.8 s en pausa, repetido (el 0 final = repetir desde el inicio).
        val patron = VibrationEffect.createWaveform(longArrayOf(0, 800, 800), 0)
        val atributos = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        @Suppress("DEPRECATION")
        vib.vibrate(patron, atributos)
    }

    private fun crearNotificacion(): Notification {
        val gestor = getSystemService(NotificationManager::class.java)
        val canal = NotificationChannel(CANAL, getString(R.string.timbre_canal), NotificationManager.IMPORTANCE_HIGH).apply {
            // El sonido lo pone el servicio (con volumen creciente), no la notificación.
            setSound(null, null)
            enableVibration(false)
        }
        gestor.createNotificationChannel(canal)

        val pantalla = PendingIntent.getActivity(
            this,
            0,
            Intent(this, TimbreActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val ahora = LocalTime.now()
        return NotificationCompat.Builder(this, CANAL)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(getString(R.string.timbre_titulo))
            .setContentText(getString(R.string.timbre_notificacion_texto, formatearHora(ahora.hour * 60 + ahora.minute)))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            // Pantalla completa: con el teléfono bloqueado abre TimbreActivity directamente.
            .setFullScreenIntent(pantalla, true)
            .setContentIntent(pantalla)
            // Botones en la notificación: funcionan aunque falte el permiso de pantalla completa.
            .addAction(0, getString(R.string.timbre_posponer), intentServicio(ACCION_POSPONER))
            .addAction(0, getString(R.string.timbre_apagar), intentServicio(ACCION_APAGAR))
            .build()
    }

    private fun intentServicio(accion: String): PendingIntent = PendingIntent.getService(
        this,
        accion.hashCode(),
        Intent(this, TimbreService::class.java).setAction(accion),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    companion object {
        const val ACCION_SONAR = "com.upt.descansa.alarma.SONAR"
        const val ACCION_APAGAR = "com.upt.descansa.alarma.APAGAR"
        const val ACCION_POSPONER = "com.upt.descansa.alarma.POSPONER"

        private const val CANAL = "alarma"
        private const val ID_NOTIFICACION = 1
        private const val VOLUMEN_INICIAL = 0.1f
        private const val SUBIDA_VOLUMEN_MS = 60_000L
        private const val DURACION_MAXIMA_MS = 10 * 60_000L

        fun intentSonar(context: Context): Intent =
            Intent(context, TimbreService::class.java).setAction(ACCION_SONAR)

        /** Para la pantalla de timbre: apagar o posponer (el servicio ya está corriendo). */
        fun enviar(context: Context, accion: String) {
            context.startService(Intent(context, TimbreService::class.java).setAction(accion))
        }
    }
}
