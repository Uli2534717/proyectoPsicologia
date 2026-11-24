package com.upt.descansa.sistema.alarma

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.upt.descansa.MainActivity
import com.upt.descansa.domain.alarma.AlarmaProgramada
import com.upt.descansa.domain.alarma.AlarmaRespaldo
import com.upt.descansa.domain.alarma.CalculoAlarma
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación Android de la alarma de respaldo (sección 6.2).
 *
 * Usa `AlarmManager.setAlarmClock()` porque:
 * - Es exacta y el sistema NUNCA la retrasa, ni en Doze (ahorro de energía).
 * - Android la trata como alarma de despertador: muestra el ícono de reloj en
 *   la barra de estado y la hora en la pantalla de bloqueo.
 * - Al dispararse, la app puede iniciar un servicio en primer plano aunque
 *   esté en segundo plano (exención documentada para alarmas exactas).
 *
 * Tiene constructor normal además de @Inject porque los receptores de
 * arranque lo crean a mano (en Direct Boot evitamos depender de Hilt y DataStore).
 */
@Singleton
class ProgramadorAlarma @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmaRespaldo {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val almacen = AlmacenAlarma(context)

    override val programada: Flow<AlarmaProgramada?> = almacen.observar()

    override fun puedeProgramarExactas(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    override fun programar(alarma: AlarmaProgramada) {
        // Primero guardamos: si el proceso muere justo después, el receptor de
        // arranque aún sabe qué reprogramar.
        almacen.guardar(alarma)
        // Al tocar el reloj de la pantalla de bloqueo se abre la app.
        val mostrar = PendingIntent.getActivity(
            context,
            CODIGO_MOSTRAR,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarma.disparoEnMs, mostrar), intentDisparo())
    }

    override fun cancelar() {
        alarmManager.cancel(intentDisparo())
        almacen.borrar()
    }

    /**
     * Vuelve a programar la alarma guardada. Se llama tras reiniciar el teléfono
     * (las alarmas se borran al apagarlo) o al cambiar la hora o la zona horaria.
     *
     * - Una alarma normal se recalcula desde la hora límite (07:00 sigue siendo
     *   07:00 en la nueva zona horaria).
     * - Una posposición conserva su momento exacto.
     * - Si su hora ya pasó mientras el teléfono estaba apagado, suena de inmediato:
     *   es mejor despertar tarde que no despertar.
     */
    fun reprogramarGuardada(ahoraMs: Long = System.currentTimeMillis()) {
        val guardada = almacen.leer() ?: return
        val disparo = when {
            guardada.esPosposicion -> guardada.disparoEnMs
            guardada.disparoEnMs <= ahoraMs -> guardada.disparoEnMs
            else -> CalculoAlarma.proximaHoraLimite(ahoraMs, ZoneId.systemDefault(), guardada.horaLimiteMinutos)
                // Nunca la movemos más de un día hacia adelante por un cambio de hora.
                .coerceAtMost(guardada.disparoEnMs + UN_DIA_MS)
        }
        programar(guardada.copy(disparoEnMs = maxOf(disparo, ahoraMs + 1_000)))
    }

    /** El "qué hacer" cuando suene: avisar a [AlarmaReceiver]. Siempre el mismo, para poder cancelarlo. */
    private fun intentDisparo(): PendingIntent = PendingIntent.getBroadcast(
        context,
        CODIGO_DISPARO,
        Intent(context, AlarmaReceiver::class.java).setAction(AlarmaReceiver.ACCION_SONAR),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private companion object {
        const val CODIGO_DISPARO = 100
        const val CODIGO_MOSTRAR = 101
        const val UN_DIA_MS = 24 * 3_600_000L
    }
}
