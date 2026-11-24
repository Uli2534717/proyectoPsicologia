package com.upt.descansa.domain.repository

import com.upt.descansa.domain.model.AjustesUsuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para leer y guardar los ajustes del usuario.
 * Vive en `domain` para que los ViewModels no dependan de DataStore
 * (y en las pruebas se pueda reemplazar por un objeto falso).
 */
interface AjustesRepository {

    /** Emite los ajustes actuales y de nuevo cada vez que cambian. */
    val ajustes: Flow<AjustesUsuario>

    /**
     * Aplica un cambio a los ajustes de forma atómica.
     * Ejemplo: `actualizar { it.copy(ventanaMinutos = 20) }`.
     */
    suspend fun actualizar(cambio: (AjustesUsuario) -> AjustesUsuario)
}
