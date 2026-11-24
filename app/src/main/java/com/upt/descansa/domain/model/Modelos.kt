package com.upt.descansa.domain.model

/**
 * Modelos del dominio de Descansa. Son Kotlin puro (sin Android) para que se
 * puedan usar desde pruebas unitarias y desde el motor de decisión.
 */

/** De dónde salen los datos de la noche (sección 6.1). */
enum class FuenteDatos {
    ACELEROMETRO_TELEFONO,
    SMARTWATCH,
}

/** Sensibilidad del motor de decisión (tabla de la sección 6.5). */
enum class Sensibilidad {
    BAJA,
    MEDIA,
    ALTA,
}

/** Por qué sonó (o no) la alarma de una noche. */
enum class MotivoDisparo {
    /** El motor confirmó señales de sueño ligero dentro de la ventana. */
    SUENO_LIGERO,

    /** No se detectó sueño ligero y sonó a la hora límite (alarma de respaldo). */
    HORA_LIMITE,

    /** El usuario detuvo la sesión antes de que sonara. */
    DETENIDA_MANUALMENTE,

    /** La noche sigue en curso o no terminó normalmente. */
    NINGUNO,
}

/** Clasificación de cada minuto (época) de la noche. */
enum class MarcaEpoca {
    /** Poco movimiento. */
    TRANQUILA,

    /** Movimiento por encima de la línea base. */
    INQUIETA,

    /** Picos grandes: el teléfono fue tomado o movido (el usuario está despierto). */
    TOMADO,
}

/**
 * Ajustes del usuario (se guardan con DataStore).
 *
 * @property horaLimiteMinutos hora límite de la alarma como minutos desde las
 *           00:00 (7:00 = 420). Así no depende de la zona horaria.
 * @property ventanaMinutos minutos antes de la hora límite en que ya puede sonar.
 */
data class AjustesUsuario(
    val horaLimiteMinutos: Int = 7 * 60,
    val ventanaMinutos: Int = 30,
    val fuente: FuenteDatos = FuenteDatos.ACELEROMETRO_TELEFONO,
    val sensibilidad: Sensibilidad = Sensibilidad.MEDIA,
    val apagadoSonidoMinutos: Int = 30,
    val posponerMinutos: Int = 5,
    val sonidoFavoritoId: String? = null,
    val noMolestarActivado: Boolean = true,
    val bienvenidaCompletada: Boolean = false,
    val avisoSaludAceptado: Boolean = false,
    val modoDesarrollador: Boolean = false,
)

/**
 * Un minuto (época) de la noche ya clasificado por el motor.
 * @property minutoTs inicio de la época (epoch ms, UTC).
 * @property actividad desviación estándar de |a|/g en la época (ver DECISIONES.md, D-12).
 */
data class Epoca(
    val minutoTs: Long,
    val actividad: Double,
    val marca: MarcaEpoca,
)

/** Lo que el servicio nocturno necesita saber de la noche en curso. */
data class NocheActiva(
    val id: Long,
    val iniciadaEn: Long,
    val horaLimiteEn: Long,
    val ventanaMinutos: Int,
)

/** Cuándo y por qué sonó la alarma (se guarda antes de que el usuario la apague). */
data class Disparo(
    val motivo: MotivoDisparo,
    val en: Long,
)

/** Tipos de noche del simulador (modo desarrollador). */
enum class TipoSimulacion {
    /** Poco movimiento toda la noche: debe sonar la alarma de respaldo. */
    TRANQUILA,

    /** Movimiento dentro de la ventana: debe sonar antes, por sueño ligero. */
    INQUIETA,
}

/** Datos de la tarjeta "Tu última noche" en Inicio. Horas en formato local "HH:mm". */
data class ResumenUltimaNoche(
    val minutosDormidos: Int,
    val horaDormir: String,
    val horaDespertar: String,
)
