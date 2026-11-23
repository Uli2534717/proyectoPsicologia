package com.upt.dormirbien.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tipo de una entrada del vaciado mental (3.1 del documento del proyecto).
 * Room guarda los enum como texto (su `name`).
 */
enum class TipoEntradaVaciado {
    /** Debe aparecer al día siguiente en la pantalla de inicio. */
    PENDIENTE_MANANA,

    /** Solo para desahogarse; no genera recordatorio. */
    SOLO_DESAHOGO,
}

/**
 * Tabla `entradas_vaciado`: lo que el usuario escribe antes de dormir.
 *
 * SUPOSICIÓN: Room exige al menos una tabla para compilar, así que dejamos
 * esta primera entidad desde la Fase 1. Sebastián la completará en la Fase 2
 * (vaciado mental); las demás tablas se agregan en la fase de su módulo.
 *
 * @property fechaCreacion momento en que se escribió, en milisegundos desde 1970 (UTC).
 * @property archivada `true` cuando el usuario la "soltó" (sigue guardada, pero no se muestra).
 */
@Entity(tableName = "entradas_vaciado")
data class EntradaVaciadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val texto: String,
    val tipo: TipoEntradaVaciado,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long,
    val archivada: Boolean = false,
)
