package com.upt.descansa.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.upt.descansa.data.local.entity.EpocaEntity
import com.upt.descansa.data.local.entity.EstadisticaSonidoEntity
import com.upt.descansa.data.local.entity.NocheEntity
import com.upt.descansa.data.local.entity.PendienteEntity
import kotlinx.coroutines.flow.Flow

// =============================================================================
//  DAOs: las consultas de cada tabla. Room genera el código al compilar.
//  - Las funciones que devuelven Flow se vuelven a emitir solas cuando la tabla cambia.
//  - Las funciones suspend se ejecutan fuera del hilo principal.
// =============================================================================

@Dao
interface NocheDao {

    @Insert
    suspend fun insertar(noche: NocheEntity): Long

    @Update
    suspend fun actualizar(noche: NocheEntity)

    /** La sesión en curso (sin fecha de fin), si existe. Solo puede haber una. */
    @Query("SELECT * FROM noches WHERE terminada_en IS NULL ORDER BY iniciada_en DESC LIMIT 1")
    suspend fun obtenerActiva(): NocheEntity?

    /** La última noche terminada, para la tarjeta "Tu última noche" y el resumen. */
    @Query("SELECT * FROM noches WHERE terminada_en IS NOT NULL ORDER BY terminada_en DESC LIMIT 1")
    fun observarUltimaTerminada(): Flow<NocheEntity?>
}

@Dao
interface EpocaDao {

    @Insert
    suspend fun insertarTodas(epocas: List<EpocaEntity>)

    @Query("SELECT * FROM epocas WHERE noche_id = :nocheId ORDER BY minuto_ts")
    fun observarDeNoche(nocheId: Long): Flow<List<EpocaEntity>>
}

@Dao
interface PendienteDao {

    @Insert
    suspend fun insertar(pendiente: PendienteEntity): Long

    @Update
    suspend fun actualizar(pendiente: PendienteEntity)

    @Delete
    suspend fun eliminar(pendiente: PendienteEntity)

    /** Pendientes sin completar + completados recientes (los demás se consideran archivados). */
    @Query(
        "SELECT * FROM pendientes WHERE hecho = 0 OR hecho_en >= :hechosDesde ORDER BY hecho, creado_en",
    )
    fun observarVisibles(hechosDesde: Long): Flow<List<PendienteEntity>>

    @Query("SELECT * FROM pendientes WHERE noche_id = :nocheId ORDER BY creado_en")
    fun observarDeNoche(nocheId: Long): Flow<List<PendienteEntity>>
}

@Dao
interface EstadisticaSonidoDao {

    @Query("SELECT * FROM estadisticas_sonido")
    fun observarTodas(): Flow<List<EstadisticaSonidoEntity>>

    /** Inserta o reemplaza las estadísticas de un sonido. */
    @Upsert
    suspend fun guardar(estadistica: EstadisticaSonidoEntity)
}
