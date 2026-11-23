package com.upt.dormirbien.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.upt.dormirbien.data.local.entity.EntradaVaciadoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): las consultas SQL de la tabla `entradas_vaciado`.
 * Room genera el código real a partir de estas anotaciones al compilar.
 *
 * - Las funciones que devuelven `Flow` se vuelven a emitir solas cada vez
 *   que la tabla cambia (la pantalla se actualiza sin hacer nada extra).
 * - Las funciones `suspend` se ejecutan fuera del hilo principal.
 */
@Dao
interface EntradaVaciadoDao {

    @Query("SELECT * FROM entradas_vaciado WHERE archivada = 0 ORDER BY fecha_creacion DESC")
    fun observarActivas(): Flow<List<EntradaVaciadoEntity>>

    /** Devuelve el id que Room le asignó a la nueva fila. */
    @Insert
    suspend fun insertar(entrada: EntradaVaciadoEntity): Long

    @Update
    suspend fun actualizar(entrada: EntradaVaciadoEntity)
}
