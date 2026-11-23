package com.upt.dormirbien.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upt.dormirbien.data.local.dao.EntradaVaciadoDao
import com.upt.dormirbien.data.local.entity.EntradaVaciadoEntity

/**
 * Base de datos local de la app (Room, sobre SQLite).
 * Todos los datos viven SOLO en el teléfono; nada se envía a servidores.
 *
 * REGLAS PARA EL EQUIPO al agregar o cambiar una tabla:
 * 1. Agrega la entidad a la lista `entities`.
 * 2. Sube `version` en 1.
 * 3. Escribe una migración (o, mientras no haya versión publicada, acuerden
 *    usar la migración destructiva). Room deja el esquema nuevo en
 *    `app/schemas/`; súbelo a Git junto con tu cambio.
 * 4. Solo una persona a la vez cambia la versión: avisa en el grupo para
 *    evitar conflictos.
 */
@Database(
    entities = [
        EntradaVaciadoEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class DormirBienDatabase : RoomDatabase() {

    abstract fun entradaVaciadoDao(): EntradaVaciadoDao

    companion object {
        const val NOMBRE = "dormirbien.db"
    }
}
