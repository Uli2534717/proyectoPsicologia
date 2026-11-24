package com.upt.descansa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upt.descansa.data.local.dao.EpocaDao
import com.upt.descansa.data.local.dao.EstadisticaSonidoDao
import com.upt.descansa.data.local.dao.NocheDao
import com.upt.descansa.data.local.dao.PendienteDao
import com.upt.descansa.data.local.entity.EpocaEntity
import com.upt.descansa.data.local.entity.EstadisticaSonidoEntity
import com.upt.descansa.data.local.entity.NocheEntity
import com.upt.descansa.data.local.entity.PendienteEntity

/**
 * Base de datos local de Descansa (Room sobre SQLite). Todo vive SOLO en el teléfono.
 *
 * Al cambiar una tabla:
 * 1. Sube `version` en 1.
 * 2. Agrega una migración en [Migraciones] (nunca borrar datos del usuario).
 * 3. Sube a Git el nuevo JSON de `app/schemas/`.
 * Avisa al equipo antes: solo una persona a la vez cambia la versión.
 */
@Database(
    entities = [
        NocheEntity::class,
        EpocaEntity::class,
        PendienteEntity::class,
        EstadisticaSonidoEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class DescansaDatabase : RoomDatabase() {

    abstract fun nocheDao(): NocheDao
    abstract fun epocaDao(): EpocaDao
    abstract fun pendienteDao(): PendienteDao
    abstract fun estadisticaSonidoDao(): EstadisticaSonidoDao

    companion object {
        const val NOMBRE = "descansa.db"
    }
}

/** Migraciones entre versiones. Vacío mientras solo exista la versión 1. */
object Migraciones {
    val TODAS = arrayOf<androidx.room.migration.Migration>()
}
