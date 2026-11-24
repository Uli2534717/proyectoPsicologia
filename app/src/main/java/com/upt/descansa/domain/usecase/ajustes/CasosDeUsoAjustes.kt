package com.upt.descansa.domain.usecase.ajustes

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.repository.AjustesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Caso de uso: observar los ajustes del usuario. */
class ObservarAjustesUseCase @Inject constructor(
    private val repositorio: AjustesRepository,
) {
    operator fun invoke(): Flow<AjustesUsuario> = repositorio.ajustes
}

/**
 * Caso de uso: el usuario terminó la pantalla de bienvenida.
 * Al pulsar "Comenzar" acepta el aviso de salud que se muestra ahí
 * ("no es un producto médico"), así que se guardan ambas cosas juntas.
 */
class CompletarBienvenidaUseCase @Inject constructor(
    private val repositorio: AjustesRepository,
) {
    suspend operator fun invoke() {
        repositorio.actualizar { it.copy(bienvenidaCompletada = true, avisoSaludAceptado = true) }
    }
}
