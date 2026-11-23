package com.upt.dormirbien.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.upt.dormirbien.domain.model.ModoTema
import com.upt.dormirbien.domain.model.PreferenciasUsuario
import com.upt.dormirbien.domain.repository.PreferenciasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de [PreferenciasRepository] con Jetpack DataStore.
 *
 * DataStore guarda pares clave-valor en un archivo privado de la app
 * (nadie más puede leerlo). Es el reemplazo moderno de SharedPreferences:
 * trabaja con corrutinas y Flow, así que nunca bloquea la interfaz.
 *
 * `@Singleton`: Hilt crea UNA sola instancia para toda la app.
 */
@Singleton
class PreferenciasRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferenciasRepository {

    /** Nombres de las claves dentro del archivo. No cambiarlos: se perderían datos guardados. */
    private object Claves {
        val MODO_TEMA = stringPreferencesKey("modo_tema")
        val ONBOARDING_COMPLETADO = booleanPreferencesKey("onboarding_completado")
        val META_HORAS_SUENO = floatPreferencesKey("meta_horas_sueno")
    }

    override val preferencias: Flow<PreferenciasUsuario> = dataStore.data
        // Si el archivo no se puede leer (por ejemplo, está dañado), usamos los
        // valores por defecto en vez de cerrar la app.
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            PreferenciasUsuario(
                modoTema = prefs[Claves.MODO_TEMA].aModoTema(),
                onboardingCompletado = prefs[Claves.ONBOARDING_COMPLETADO] ?: false,
                metaHorasSueno = prefs[Claves.META_HORAS_SUENO]
                    ?: PreferenciasUsuario.META_HORAS_POR_DEFECTO,
            )
        }

    override suspend fun cambiarModoTema(modo: ModoTema) {
        dataStore.edit { prefs -> prefs[Claves.MODO_TEMA] = modo.name }
    }

    override suspend fun marcarOnboardingCompletado() {
        dataStore.edit { prefs -> prefs[Claves.ONBOARDING_COMPLETADO] = true }
    }

    override suspend fun cambiarMetaHorasSueno(horas: Float) {
        dataStore.edit { prefs -> prefs[Claves.META_HORAS_SUENO] = horas }
    }

    /** Convierte el texto guardado en un [ModoTema]; si no existe o es inválido, usa OSCURO. */
    private fun String?.aModoTema(): ModoTema =
        ModoTema.entries.firstOrNull { it.name == this } ?: ModoTema.OSCURO
}
