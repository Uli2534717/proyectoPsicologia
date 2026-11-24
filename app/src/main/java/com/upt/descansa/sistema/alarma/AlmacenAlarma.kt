package com.upt.descansa.sistema.alarma

import android.content.Context
import android.content.SharedPreferences
import com.upt.descansa.domain.alarma.AlarmaProgramada
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Guarda la alarma programada en almacenamiento NATIVO y "protegido por dispositivo".
 *
 * ¿Por qué no DataStore o Room?
 * - Al reiniciar un teléfono con PIN o huella, Android arranca en "Direct Boot":
 *   hasta que el usuario lo desbloquea, solo se puede leer el almacenamiento
 *   protegido por dispositivo. DataStore y Room viven en el almacenamiento
 *   normal (cifrado con la credencial) y NO se pueden leer todavía.
 * - Para que la alarma suene aunque nadie desbloquee el teléfono tras el
 *   reinicio, el receptor de arranque debe poder leer este estado.
 *
 * Solo guardamos datos mínimos y no sensibles (horas), así que no afecta la privacidad.
 * Se escribe con `commit()` (síncrono) para que el estado quede en disco antes
 * de que el sistema pueda matar el proceso.
 */
class AlmacenAlarma(context: Context) {

    private val prefs: SharedPreferences = context
        .createDeviceProtectedStorageContext()
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    fun leer(): AlarmaProgramada? {
        if (!prefs.getBoolean(ACTIVA, false)) return null
        return AlarmaProgramada(
            disparoEnMs = prefs.getLong(DISPARO, 0L),
            horaLimiteMinutos = prefs.getInt(HORA_LIMITE, 7 * 60),
            posponerMinutos = prefs.getInt(POSPONER, 5),
            esPosposicion = prefs.getBoolean(ES_POSPOSICION, false),
        )
    }

    fun guardar(alarma: AlarmaProgramada) {
        prefs.edit()
            .putBoolean(ACTIVA, true)
            .putLong(DISPARO, alarma.disparoEnMs)
            .putInt(HORA_LIMITE, alarma.horaLimiteMinutos)
            .putInt(POSPONER, alarma.posponerMinutos)
            .putBoolean(ES_POSPOSICION, alarma.esPosposicion)
            .commit()
    }

    fun borrar() {
        prefs.edit().clear().commit()
    }

    /** Emite el estado actual y cada cambio (lo usa la pantalla de Modo noche). */
    fun observar(): Flow<AlarmaProgramada?> = callbackFlow {
        val escucha = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> trySend(leer()) }
        trySend(leer())
        prefs.registerOnSharedPreferenceChangeListener(escucha)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(escucha) }
    }

    private companion object {
        const val ARCHIVO = "alarma_respaldo"
        const val ACTIVA = "activa"
        const val DISPARO = "disparo_en_ms"
        const val HORA_LIMITE = "hora_limite_minutos"
        const val POSPONER = "posponer_minutos"
        const val ES_POSPOSICION = "es_posposicion"
    }
}
