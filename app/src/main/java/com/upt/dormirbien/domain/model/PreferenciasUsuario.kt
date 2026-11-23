package com.upt.dormirbien.domain.model

/**
 * Modo de tema que eligió el usuario.
 *
 * El valor por defecto de la app es [OSCURO], porque se usa sobre todo de noche
 * y una pantalla clara molesta la vista y dificulta conciliar el sueño.
 */
enum class ModoTema {
    OSCURO,
    CLARO,

    /** Sigue el modo claro/oscuro que tenga configurado el teléfono. */
    SISTEMA,
}

/**
 * Preferencias generales del usuario (se guardan con DataStore).
 *
 * Es una clase de la capa `domain`: no sabe nada de Android ni de DataStore,
 * solo describe QUÉ datos hay. La capa `data` se encarga de CÓMO se guardan.
 *
 * @property modoTema tema visual de la app.
 * @property onboardingCompletado `true` cuando el usuario terminó las pantallas
 *           de bienvenida y aceptó el aviso de privacidad (se usará en la Fase 2).
 * @property metaHorasSueno meta diaria de horas de sueño (se usará en rachas y metas).
 */
data class PreferenciasUsuario(
    val modoTema: ModoTema = ModoTema.OSCURO,
    val onboardingCompletado: Boolean = false,
    val metaHorasSueno: Float = META_HORAS_POR_DEFECTO,
) {
    companion object {
        /** Recomendación general para adultos jóvenes: 7 a 9 horas. Usamos 8. */
        const val META_HORAS_POR_DEFECTO = 8f
    }
}
