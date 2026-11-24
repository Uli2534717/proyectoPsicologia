package com.upt.descansa.domain.repository

/**
 * Activa el No molestar durante la noche y lo deja como estaba al terminar.
 * La implementación real está en `sistema/nomolestar`; en las pruebas se usa una falsa.
 */
interface ControlNoMolestar {

    /** ¿El usuario ya dio el permiso de "Acceso a No molestar"? */
    fun tienePermiso(): Boolean

    /**
     * Pone el modo "solo alarmas". Se puede llamar varias veces sin problema.
     * @return false si no hay permiso (la noche sigue igual, solo sin No molestar).
     */
    fun activar(): Boolean

    /** Deja el No molestar como estaba antes de [activar]. Si no se activó, no hace nada. */
    fun restaurar()
}
