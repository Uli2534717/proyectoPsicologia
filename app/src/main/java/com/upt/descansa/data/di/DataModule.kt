package com.upt.descansa.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.upt.descansa.data.local.DescansaDatabase
import com.upt.descansa.data.local.Migraciones
import com.upt.descansa.data.local.dao.EpocaDao
import com.upt.descansa.data.local.dao.EstadisticaSonidoDao
import com.upt.descansa.data.local.dao.NocheDao
import com.upt.descansa.data.local.dao.PendienteDao
import com.upt.descansa.data.prefs.AjustesRepositoryImpl
import com.upt.descansa.data.repository.NocheRepositoryImpl
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.repository.NocheRepository
import com.upt.descansa.domain.repository.ProgramadorAlarma
import com.upt.descansa.sistema.alarma.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt: cómo crear la base de datos, sus DAOs y DataStore.
 * `@Singleton`: una sola copia mientras viva la app.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun proveerBaseDeDatos(@ApplicationContext context: Context): DescansaDatabase =
        Room.databaseBuilder(context, DescansaDatabase::class.java, DescansaDatabase.NOMBRE)
            .addMigrations(*Migraciones.TODAS)
            .build()

    @Provides fun proveerNocheDao(db: DescansaDatabase): NocheDao = db.nocheDao()
    @Provides fun proveerEpocaDao(db: DescansaDatabase): EpocaDao = db.epocaDao()
    @Provides fun proveerPendienteDao(db: DescansaDatabase): PendienteDao = db.pendienteDao()
    @Provides fun proveerEstadisticaSonidoDao(db: DescansaDatabase): EstadisticaSonidoDao = db.estadisticaSonidoDao()

    @Provides
    @Singleton
    fun proveerDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("ajustes") }
}

/** Conecta cada interfaz de `domain` con su implementación de `data`. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun vincularAjustesRepository(impl: AjustesRepositoryImpl): AjustesRepository

    @Binds
    abstract fun vincularNocheRepository(impl: NocheRepositoryImpl): NocheRepository

    @Binds
    abstract fun vincularProgramadorAlarma(impl: AlarmSchedulerImpl): ProgramadorAlarma
}
