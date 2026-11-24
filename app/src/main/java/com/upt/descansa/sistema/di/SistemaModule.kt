package com.upt.descansa.sistema.di

import com.upt.descansa.domain.alarma.AlarmaRespaldo
import com.upt.descansa.sistema.alarma.ProgramadorAlarma
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Conecta los contratos de `domain` con las piezas del sistema Android
 * (alarma; en fases siguientes, servicio nocturno y No molestar).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SistemaModule {

    @Binds
    abstract fun vincularAlarmaRespaldo(impl: ProgramadorAlarma): AlarmaRespaldo
}
