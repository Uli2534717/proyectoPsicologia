package com.upt.descansa.domain.repository

import com.upt.descansa.domain.model.TipoSimulacion

/**
 * Arranca y detiene el servicio nocturno (el que mide el movimiento toda la noche).
 * La implementación real está en `sistema/noche`; en las pruebas se usa una falsa.
 */
interface ControlServicioNocturno {

    /** @param simulacion null = acelerómetro real; si no, una noche simulada y acelerada. */
    fun iniciar(simulacion: TipoSimulacion? = null)

    fun detener()
}
