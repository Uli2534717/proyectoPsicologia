package com.upt.dormirbien.domain.repository

import com.upt.dormirbien.domain.model.ModoTema
import com.upt.dormirbien.domain.model.PreferenciasUsuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) para leer y guardar las preferencias del usuario.
 *
 * ¿Por qué una interfaz en `domain` y la implementación en `data`?
 * - Los ViewModels y casos de uso dependen de ESTA interfaz, no de DataStore.
 * - En las pruebas unitarias se puede reemplazar por un "mock" (MockK) sin
 *   necesitar un teléfono.
 * - Si mañana cambiamos DataStore por otra cosa, solo cambia la capa `data`.
 */
interface PreferenciasRepository {

    /** Flujo que emite las preferencias cada vez que cambian. */
    val preferencias: Flow<PreferenciasUsuario>

    suspend fun cambiarModoTema(modo: ModoTema)

    suspend fun marcarOnboardingCompletado()

    suspend fun cambiarMetaHorasSueno(horas: Float)
}
