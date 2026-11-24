package com.upt.descansa.sistema.nomolestar

// Mismos valores que NotificationManager.INTERRUPTION_FILTER_* (se copian para
// que esta clase sea Kotlin puro y se pueda probar con JUnit).
/** Sin No molestar: todo puede sonar. */
const val FILTRO_TODAS = 1

/** No molestar "prioritario" (lo que el usuario haya configurado). */
const val FILTRO_PRIORIDAD = 2

/** Silencio total. */
const val FILTRO_NINGUNA = 3

/** Solo alarmas: lo que usa Descansa durante la noche. */
const val FILTRO_ALARMAS = 4

/** Lo que el gestor necesita del teléfono. En las pruebas se reemplaza por uno falso. */
interface PlataformaNoMolestar {
    val tienePermiso: Boolean

    /**
     * Android 15+: la app ya no cambia el No molestar global; `setInterruptionFilter`
     * enciende o apaga un "modo" propio de la app (y gana el más restrictivo).
     */
    val reglaPropia: Boolean

    /** El filtro de interrupciones del teléfono (leerlo y cambiarlo). */
    var filtroActual: Int

    /** Lo que había antes de activar (se guarda en disco por si el teléfono se reinicia). null = no activamos nada. */
    var filtroGuardado: Int?
}

/**
 * Reglas para activar y restaurar el No molestar (D-13):
 * 1. Sin permiso, no se toca nada.
 * 2. Android 15+: se enciende el modo propio de la app y al terminar se apaga.
 *    El No molestar del usuario nunca se modifica.
 * 3. Android 9–14: se guarda el filtro previo y se pone "solo alarmas".
 *    Si el usuario YA tenía No molestar, no se toca (podríamos hacerlo menos estricto).
 * 4. Al restaurar en Android 9–14: si el usuario lo cambió a mano durante la noche,
 *    se respeta su cambio.
 */
class GestorNoMolestar(private val plataforma: PlataformaNoMolestar) {

    fun activar(): Boolean {
        if (!plataforma.tienePermiso) return false
        if (plataforma.reglaPropia) {
            plataforma.filtroGuardado = FILTRO_TODAS
            plataforma.filtroActual = FILTRO_ALARMAS
            return true
        }
        // Ya lo activamos antes (por ejemplo, al volver a la app): no se guarda otra vez.
        if (plataforma.filtroGuardado != null) return true
        // El usuario ya estaba en No molestar: lo dejamos como lo tenía.
        if (plataforma.filtroActual != FILTRO_TODAS) return true
        plataforma.filtroGuardado = plataforma.filtroActual
        plataforma.filtroActual = FILTRO_ALARMAS
        return true
    }

    fun restaurar() {
        val guardado = plataforma.filtroGuardado ?: return
        plataforma.filtroGuardado = null
        // Si quitaron el permiso durante la noche, ya no podemos cambiar nada.
        if (!plataforma.tienePermiso) return
        when {
            plataforma.reglaPropia -> plataforma.filtroActual = FILTRO_TODAS
            plataforma.filtroActual == FILTRO_ALARMAS -> plataforma.filtroActual = guardado
            // Si no está en "solo alarmas", el usuario lo cambió a mano: se respeta.
        }
    }
}
