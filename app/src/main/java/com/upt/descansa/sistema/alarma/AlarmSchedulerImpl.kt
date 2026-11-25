package com.upt.descansa.sistema.alarma

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.UserManager
import androidx.core.content.edit
import com.upt.descansa.MainActivity
import com.upt.descansa.core.util.minutosDelDiaEn
import com.upt.descansa.core.util.ocurrenciaMasCercana
import com.upt.descansa.domain.repository.ProgramadorAlarma
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
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
 *
 * Se guarda en el almacenamiento "protegido por dispositivo" (Direct Boot): tras
 * reiniciar un teléfono con PIN, es lo único legible antes de desbloquearlo. Así
 * la alarma se reprograma y suena aunque nadie desbloquee el teléfono (D-14).
 * Solo se guardan horas, que no son datos sensibles.
 */
@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ProgramadorAlarma {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val prefs = context.createDeviceProtectedStorageContext().also { protegido ->
        // Instalaciones que ya guardaban la alarma en el almacenamiento normal: se mueve una vez.
        if (context.getSystemService(UserManager::class.java).isUserUnlocked) {
            runCatching { protegido.moveSharedPreferencesFrom(context, ARCHIVO) }
        }
    }.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    /** Hora guardada de la próxima alarma (epoch ms), o null si no hay. */
    val programadaEn: Long?
        get() = prefs.getLong(CLAVE_PROGRAMADA_EN, 0L).takeIf { it > 0L }

    override fun programar(disparoEn: Long) {
        // commit = true: se escribe en disco ya, por si el teléfono se reinicia enseguida.
        prefs.edit(commit = true) {
            putLong(CLAVE_PROGRAMADA_EN, disparoEn)
            // La hora "de reloj" (07:00), para recalcularla si cambia la hora o la zona.
            putInt(CLAVE_MINUTOS_DEL_DIA, minutosDelDiaEn(disparoEn, ZoneId.systemDefault()))
        }
        poner(disparoEn, intentDisparo(CODIGO_DISPARO))
    }

    /**
     * La alarma inteligente usa OTRO PendingIntent (otro código): así no reemplaza
     * a la de respaldo, que sigue programada por si esta fallara.
     */
    override fun dispararAhora() {
        poner(System.currentTimeMillis() + 1_000L, intentDisparo(CODIGO_INTELIGENTE))
    }

    override fun cancelar() {
        alarmManager.cancel(intentDisparo(CODIGO_DISPARO))
        alarmManager.cancel(intentDisparo(CODIGO_INTELIGENTE))
        prefs.edit(commit = true) {
            remove(CLAVE_PROGRAMADA_EN)
            remove(CLAVE_MINUTOS_DEL_DIA)
        }
    }

    private fun poner(disparoEn: Long, operacion: PendingIntent) {
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

    /**
     * Si la alarma se apagó antes de desbloquear el teléfono tras un reinicio, la
     * noche (en Room) y el No molestar no se pudieron cerrar todavía: aquí queda
     * anotado cuándo sonó, y [ArranqueReceiver] los cierra al desbloquear.
     */
    var cierrePendienteDesde: Long?
        get() = prefs.getLong(CLAVE_CIERRE_PENDIENTE, 0L).takeIf { it > 0L }
        set(valor) = prefs.edit(commit = true) {
            if (valor == null) remove(CLAVE_CIERRE_PENDIENTE) else putLong(CLAVE_CIERRE_PENDIENTE, valor)
        }

    /**
     * El usuario o la red cambiaron la hora o la zona horaria: la alarma de las
     * 07:00 debe seguir sonando a las 07:00 del reloj, no en el mismo instante
     * absoluto. Se toma la ocurrencia más cercana a la anterior para no saltarse
     * un día. Si con la hora nueva ya pasó, [reprogramarGuardada] decide.
     */
    fun reajustarPorCambioDeHora(ahora: Long = System.currentTimeMillis()) {
        val anterior = programadaEn ?: return
        val minutos = prefs.getInt(CLAVE_MINUTOS_DEL_DIA, -1).takeIf { it >= 0 }
            ?: return reprogramarGuardada(ahora)
        val nueva = ocurrenciaMasCercana(anterior, ZoneId.systemDefault(), minutos)
        prefs.edit(commit = true) {
            putLong(CLAVE_PROGRAMADA_EN, nueva)
            putInt(CLAVE_MINUTOS_DEL_DIA, minutos)
        }
        reprogramarGuardada(ahora)
    }

    private fun intentDisparo(codigo: Int): PendingIntent = PendingIntent.getBroadcast(
        context,
        codigo,
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
        const val CLAVE_MINUTOS_DEL_DIA = "minutos_del_dia"
        const val CLAVE_CIERRE_PENDIENTE = "cierre_pendiente_desde"
        const val CODIGO_DISPARO = 100
        const val CODIGO_MOSTRAR = 101
        const val CODIGO_INTELIGENTE = 102

        /** Si el teléfono estuvo apagado a la hora de la alarma, suena al encender solo si pasó menos de 1 h. */
        const val TOLERANCIA_ATRASO_MS = 60 * 60 * 1000L
    }
}
