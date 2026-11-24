package com.upt.descansa.core.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas de la app (sección 4 del documento). Cada pantalla es un objeto de
 * Kotlin: si alguien escribe mal una ruta, el proyecto no compila.
 * `@Serializable` lo pide Navigation Compose para guardar y restaurar la ruta.
 *
 * Flujo principal: Bienvenida → Inicio → Pendientes → Respiración → Relajación
 * → Sonidos → Alarma → Modo noche → Mañana.
 */
sealed interface RutaApp

/** 1. `/bienvenida` */
@Serializable data object RutaBienvenida : RutaApp

/** 2. `/inicio` */
@Serializable data object RutaInicio : RutaApp

/** 3. `/noche/pendientes` (paso 1 de 5) */
@Serializable data object RutaPendientes : RutaApp

/** 4. `/noche/respiracion` (paso 2 de 5) */
@Serializable data object RutaRespiracion : RutaApp

/** 5. `/noche/relajacion` (paso 3 de 5) */
@Serializable data object RutaRelajacion : RutaApp

/** 6. `/noche/sonidos` (paso 4 de 5) */
@Serializable data object RutaSonidos : RutaApp

/** 7. `/noche/alarma` (paso 5 de 5) */
@Serializable data object RutaAlarma : RutaApp

/** 8. `/noche/activo` */
@Serializable data object RutaModoNoche : RutaApp

/** 9. `/manana` */
@Serializable data object RutaManana : RutaApp

/** 10. `/aprender` */
@Serializable data object RutaAprender : RutaApp

/** 11. `/ajustes` */
@Serializable data object RutaAjustes : RutaApp
