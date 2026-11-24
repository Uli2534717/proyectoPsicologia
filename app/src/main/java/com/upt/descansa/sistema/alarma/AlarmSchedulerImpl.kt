package com.upt.descansa.sistema.alarma

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import com.upt.descansa.MainActivity
import com.upt.descansa.domain.repository.ProgramadorAlarma
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Programa la alarma de respaldo con `AlarmManager.setAlarmClock()`.
 *
 * Por qué `setAlarmClock` y no `setExact`: es la única que Android trata como
 * "despertador del usuario". Suena aunque el teléfono esté en reposo (Doze),
 * el sistema nunca la recorre y muestra el ícono de alarma en la barra de estado.
 *
 * La hora programada también se guarda en SharedPreferences porque Android
 * BORRA todas las alarmas al reiniciar el teléfono: [ArranqueReceiver] la lee
 * para volver a programarla. Se usa SharedPreferences (y no DataStore) porque
 * se lee desde un BroadcastReceiver, donde conviene una lectura inmediata.
 */
@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ProgramadorAlarma {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    /** Hora guardada de la próxima alarma (epoch ms), o null si no hay. */
    val programadaEn: Long?
        get() = prefs.getLong(CLAVE_PROGRAMADA_EN, 0L).takeIf { it > 0L }

    override fun programar(disparoEn: Long) {
        // commit = true: se escribe en disco ya, por si el teléfono se reinicia enseguida.
        prefs.edit(commit = true) { putLong(CLAVE_PROGRAMADA_EN, disparoEn) }
        val operacion = intentDisparo()
        try {
            if (puedeProgramarExactas(context)) {
                // El segundo PendingIntent es lo que se abre al tocar el ícono de alarma del sistema.
                alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(disparoEn, intentMostrar()), operacion)
                return
            }
        } catch (_: SecurityException) {
            // El usuario quitó el permiso entre la revisión y la llamada: seguimos abajo.
        }
        // Plan B sin permiso de alarmas exactas: puede retrasarse unos minutos, pero suena.
        // La pantalla de modo noche avisa de esto al usuario.
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, disparoEn, operacion)
    }

    override fun cancelar() {
        alarmManager.cancel(intentDisparo())
        prefs.edit(commit = true) { remove(CLAVE_PROGRAMADA_EN) }
    }

    /**
     * Vuelve a programar la alarma guardada (tras reiniciar el teléfono o actualizar la app).
     * Si su hora ya pasó hace poco (el teléfono estaba apagado a la hora de la
     * alarma), suena en unos segundos; si pasó hace mucho, se descarta.
     */
    fun reprogramarGuardada(ahora: Long = System.currentTimeMillis()) {
        val hora = programadaEn ?: return
        when {
            hora > ahora -> programar(hora)
            ahora - hora <= TOLERANCIA_ATRASO_MS -> programar(ahora + 5_000L)
            else -> cancelar()
        }
    }

    private fun intentDisparo(): PendingIntent = PendingIntent.getBroadcast(
        context,
        CODIGO_DISPARO,
        Intent(context, AlarmaReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun intentMostrar(): PendingIntent = PendingIntent.getActivity(
        context,
        CODIGO_MOSTRAR,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private companion object {
        const val ARCHIVO = "alarma_respaldo"
        const val CLAVE_PROGRAMADA_EN = "programada_en"
        const val CODIGO_DISPARO = 100
        const val CODIGO_MOSTRAR = 101

        /** Si el teléfono estuvo apagado a la hora de la alarma, suena al encender solo si pasó menos de 1 h. */
        const val TOLERANCIA_ATRASO_MS = 60 * 60 * 1000L
    }
}
