package com.upt.descansa.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.model.Sensibilidad
import com.upt.descansa.domain.repository.AjustesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ajustes guardados con DataStore Preferences (archivo privado de la app).
 * Los valores por defecto viven en [AjustesUsuario]; aquí solo se traduce
 * entre el objeto y las claves del archivo.
 */
@Singleton
class AjustesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AjustesRepository {

    /** Nombres de las claves. No cambiarlos: se perderían los ajustes guardados. */
    private object Claves {
        val HORA_LIMITE = intPreferencesKey("hora_limite_minutos")
        val VENTANA = intPreferencesKey("ventana_minutos")
        val FUENTE = stringPreferencesKey("fuente_datos")
        val SENSIBILIDAD = stringPreferencesKey("sensibilidad")
        val APAGADO_SONIDO = intPreferencesKey("apagado_sonido_minutos")
        val POSPONER = intPreferencesKey("posponer_minutos")
        val SONIDO_FAVORITO = stringPreferencesKey("sonido_favorito_id")
        val NO_MOLESTAR = booleanPreferencesKey("no_molestar_activado")
        val BIENVENIDA = booleanPreferencesKey("bienvenida_completada")
        val AVISO_SALUD = booleanPreferencesKey("aviso_salud_aceptado")
        val MODO_DESARROLLADOR = booleanPreferencesKey("modo_desarrollador")
    }

    override val ajustes: Flow<AjustesUsuario> = dataStore.data
        // Si el archivo está dañado, seguimos con valores por defecto en vez de cerrar la app.
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { it.aAjustes() }

    override suspend fun actualizar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        // edit() es atómico: lee, aplica el cambio y escribe sin que otro cambio se cruce.
        dataStore.edit { prefs -> prefs.guardar(cambio(prefs.aAjustes())) }
    }

    private fun Preferences.aAjustes(): AjustesUsuario {
        val defecto = AjustesUsuario()
        return AjustesUsuario(
            horaLimiteMinutos = this[Claves.HORA_LIMITE] ?: defecto.horaLimiteMinutos,
            ventanaMinutos = this[Claves.VENTANA] ?: defecto.ventanaMinutos,
            fuente = enumONulo<FuenteDatos>(this[Claves.FUENTE]) ?: defecto.fuente,
            sensibilidad = enumONulo<Sensibilidad>(this[Claves.SENSIBILIDAD]) ?: defecto.sensibilidad,
            apagadoSonidoMinutos = this[Claves.APAGADO_SONIDO] ?: defecto.apagadoSonidoMinutos,
            posponerMinutos = this[Claves.POSPONER] ?: defecto.posponerMinutos,
            sonidoFavoritoId = this[Claves.SONIDO_FAVORITO],
            noMolestarActivado = this[Claves.NO_MOLESTAR] ?: defecto.noMolestarActivado,
            bienvenidaCompletada = this[Claves.BIENVENIDA] ?: defecto.bienvenidaCompletada,
            avisoSaludAceptado = this[Claves.AVISO_SALUD] ?: defecto.avisoSaludAceptado,
            modoDesarrollador = this[Claves.MODO_DESARROLLADOR] ?: defecto.modoDesarrollador,
        )
    }

    private fun MutablePreferences.guardar(a: AjustesUsuario) {
        this[Claves.HORA_LIMITE] = a.horaLimiteMinutos
        this[Claves.VENTANA] = a.ventanaMinutos
        this[Claves.FUENTE] = a.fuente.name
        this[Claves.SENSIBILIDAD] = a.sensibilidad.name
        this[Claves.APAGADO_SONIDO] = a.apagadoSonidoMinutos
        this[Claves.POSPONER] = a.posponerMinutos
        if (a.sonidoFavoritoId != null) this[Claves.SONIDO_FAVORITO] = a.sonidoFavoritoId else remove(Claves.SONIDO_FAVORITO)
        this[Claves.NO_MOLESTAR] = a.noMolestarActivado
        this[Claves.BIENVENIDA] = a.bienvenidaCompletada
        this[Claves.AVISO_SALUD] = a.avisoSaludAceptado
        this[Claves.MODO_DESARROLLADOR] = a.modoDesarrollador
    }

    /** Convierte el texto guardado en un enum; si no existe o es inválido, devuelve null. */
    private inline fun <reified T : Enum<T>> enumONulo(nombre: String?): T? =
        enumValues<T>().firstOrNull { it.name == nombre }
}
