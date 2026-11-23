package com.upt.dormirbien.domain.usecase.preferencias

import com.upt.dormirbien.domain.model.ModoTema
import com.upt.dormirbien.domain.repository.PreferenciasRepository
import javax.inject.Inject

/** Caso de uso: guardar el modo de tema elegido en Ajustes. */
class CambiarModoTemaUseCase @Inject constructor(
    private val repositorio: PreferenciasRepository,
) {
    suspend operator fun invoke(modo: ModoTema) {
        repositorio.cambiarModoTema(modo)
    }
}
