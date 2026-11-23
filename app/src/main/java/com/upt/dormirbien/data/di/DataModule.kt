package com.upt.dormirbien.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.upt.dormirbien.data.local.DormirBienDatabase
import com.upt.dormirbien.data.local.dao.EntradaVaciadoDao
import com.upt.dormirbien.data.prefs.PreferenciasRepositoryImpl
import com.upt.dormirbien.domain.repository.PreferenciasRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt: le dice a Hilt CÓMO crear los objetos de la capa de datos.
 *
 * - `@Provides`: "para obtener X, ejecuta esta función" (objetos de librerías
 *   que no podemos anotar con @Inject, como Room o DataStore).
 * - `@InstallIn(SingletonComponent::class)` + `@Singleton`: vive mientras
 *   viva la app y hay una sola copia.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun proveerBaseDeDatos(@ApplicationContext context: Context): DormirBienDatabase =
        Room.databaseBuilder(context, DormirBienDatabase::class.java, DormirBienDatabase.NOMBRE)
            .build()

    @Provides
    fun proveerEntradaVaciadoDao(db: DormirBienDatabase): EntradaVaciadoDao =
        db.entradaVaciadoDao()

    @Provides
    @Singleton
    fun proveerDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("preferencias_usuario")
        }
}

/**
 * Módulo de Hilt que conecta cada interfaz de `domain` con su implementación
 * de `data`. `@Binds` es más eficiente que `@Provides` para esto.
 *
 * Cada integrante agrega aquí el `@Binds` de su repositorio.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun vincularPreferenciasRepository(
        impl: PreferenciasRepositoryImpl,
    ): PreferenciasRepository
}
