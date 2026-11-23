package com.upt.dormirbien.domain.usecase.preferencias

import com.upt.dormirbien.domain.model.PreferenciasUsuario
import com.upt.dormirbien.domain.repository.PreferenciasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso: observar las preferencias del usuario.
 *
 * Un caso de uso representa UNA acción del negocio. Este es muy simple (solo
 * delega al repositorio), pero sirve de plantilla para el equipo: los
 * ViewModels hablan con casos de uso y no directamente con los repositorios.
 *
 * `operator fun invoke` permite llamarlo como si fuera una función:
 * `observarPreferencias()`.
 */
class ObservarPreferenciasUseCase @Inject constructor(
    private val repositorio: PreferenciasRepository,
) {
    operator fun invoke(): Flow<PreferenciasUsuario> = repositorio.preferencias
}
