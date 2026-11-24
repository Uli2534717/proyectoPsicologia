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

/** Tema visual elegido en Ajustes. */
enum class ModoTema {
    /** Sigue el modo claro/oscuro del teléfono (por defecto). */
    AUTOMATICO,
    CLARO,
    OSCURO,
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
    val modoTema: ModoTema = ModoTema.AUTOMATICO,
)

/** Datos de la tarjeta "Tu última noche" en Inicio. Horas en formato local "HH:mm". */
data class ResumenUltimaNoche(
    val minutosDormidos: Int,
    val horaDormir: String,
    val horaDespertar: String,
)
