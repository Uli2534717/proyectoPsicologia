package com.upt.dormirbien.core.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas (pantallas) de la app, con navegación "type-safe".
 *
 * En vez de usar textos como "sonidos" para navegar (fácil equivocarse al
 * escribirlos), cada pantalla es un objeto de Kotlin. Si alguien escribe mal
 * una ruta, el proyecto NO compila: el error aparece antes de probar la app.
 *
 * `@Serializable` lo pide Navigation Compose para poder guardar la ruta
 * (por ejemplo, cuando Android cierra la app en segundo plano y la restaura).
 *
 * Si una pantalla necesita parámetros, se usa `data class` en lugar de
 * `data object`, por ejemplo: `data class RutaDetalleSesion(val idSesion: Long)`.
 */
sealed interface RutaApp

@Serializable data object RutaOnboarding : RutaApp
@Serializable data object RutaInicio : RutaApp
@Serializable data object RutaVaciadoMental : RutaApp
@Serializable data object RutaDiarioSueno : RutaApp
@Serializable data object RutaSonidos : RutaApp
@Serializable data object RutaAlarma : RutaApp
@Serializable data object RutaEmociones : RutaApp
@Serializable data object RutaEstadisticas : RutaApp
@Serializable data object RutaAprende : RutaApp
@Serializable data object RutaRespiracion : RutaApp
@Serializable data object RutaRutina : RutaApp
@Serializable data object RutaAjustes : RutaApp
