package com.upt.descansa.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.model.MarcaEpoca
import com.upt.descansa.domain.model.MotivoDisparo

// =============================================================================
//  Tablas de la base de datos (sección 5 del documento del proyecto).
//  Todas las fechas se guardan en UTC como milisegundos desde 1970 (epoch ms)
//  y se convierten a hora local solo al mostrarlas.
//  Room guarda los enum como texto (su nombre).
// =============================================================================

/** Tabla `noches`: una fila por sesión de sueño (doc: `nights`). */
@Entity(tableName = "noches")
data class NocheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "iniciada_en") val iniciadaEn: Long,
    @ColumnInfo(name = "terminada_en") val terminadaEn: Long? = null,
    @ColumnInfo(name = "hora_limite_en") val horaLimiteEn: Long,
    @ColumnInfo(name = "ventana_minutos") val ventanaMinutos: Int,
    val fuente: FuenteDatos,
    @ColumnInfo(name = "disparada_en") val disparadaEn: Long? = null,
    @ColumnInfo(name = "motivo_disparo") val motivoDisparo: MotivoDisparo = MotivoDisparo.NINGUNO,
    @ColumnInfo(name = "no_molestar_activado") val noMolestarActivado: Boolean,
    @ColumnInfo(name = "sonido_id") val sonidoId: String? = null,
    @ColumnInfo(name = "inicio_sueno_estimado_en") val inicioSuenoEstimadoEn: Long? = null,
    /** "¿Cómo te sientes?" del resumen de la mañana: 1 (agotado) a 5 (muy bien). */
    val sensacion: Int? = null,
    /** Versión del motor de decisión que se usó (para comparar calibraciones). */
    @ColumnInfo(name = "version_motor") val versionMotor: Int,
)

/** Tabla `epocas`: resumen por minuto de una noche (doc: `epochs`). */
@Entity(
    tableName = "epocas",
    primaryKeys = ["noche_id", "minuto_ts"],
    foreignKeys = [
        // Si se borra la noche, se borran sus épocas.
        ForeignKey(entity = NocheEntity::class, parentColumns = ["id"], childColumns = ["noche_id"], onDelete = ForeignKey.CASCADE),
    ],
)
data class EpocaEntity(
    @ColumnInfo(name = "noche_id") val nocheId: Long,
    @ColumnInfo(name = "minuto_ts") val minutoTs: Long,
    /** Desviación estándar de |a|/g en el minuto (ver DECISIONES.md). */
    val actividad: Double,
    @ColumnInfo(name = "frecuencia_cardiaca") val frecuenciaCardiaca: Int? = null,
    val marca: MarcaEpoca,
)

/** Tabla `pendientes`: lo que el usuario anota antes de dormir (doc: `tasks`). */
@Entity(
    tableName = "pendientes",
    foreignKeys = [
        // Si se borra la noche, el pendiente se conserva sin noche.
        ForeignKey(entity = NocheEntity::class, parentColumns = ["id"], childColumns = ["noche_id"], onDelete = ForeignKey.SET_NULL),
    ],
    indices = [Index("noche_id")],
)
data class PendienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "noche_id") val nocheId: Long? = null,
    /** Máximo 280 caracteres (se valida en la pantalla). */
    val texto: String,
    val hecho: Boolean = false,
    @ColumnInfo(name = "creado_en") val creadoEn: Long,
    @ColumnInfo(name = "hecho_en") val hechoEn: Long? = null,
)

/** Tabla `estadisticas_sonido`: datos para recomendar sonidos (doc: `sound_stats`). */
@Entity(tableName = "estadisticas_sonido")
data class EstadisticaSonidoEntity(
    @PrimaryKey @ColumnInfo(name = "sonido_id") val sonidoId: String,
    val reproducciones: Int = 0,
    @ColumnInfo(name = "es_favorito") val esFavorito: Boolean = false,
    @ColumnInfo(name = "latencia_promedio_min") val latenciaPromedioMin: Double? = null,
    @ColumnInfo(name = "apagados_completos") val apagadosCompletos: Int = 0,
    @ColumnInfo(name = "ultima_reproduccion_en") val ultimaReproduccionEn: Long? = null,
)

/** Largo máximo de [PendienteEntity.texto]. */
const val MAX_CARACTERES_PENDIENTE = 280
