package com.upt.descansa.sistema.nomolestar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Teléfono FALSO: guarda el filtro en una variable. */
private class PlataformaFalsa(
    override var tienePermiso: Boolean = true,
    override val reglaPropia: Boolean = false,
    override var filtroActual: Int = FILTRO_TODAS,
    override var filtroGuardado: Int? = null,
) : PlataformaNoMolestar

class GestorNoMolestarTest {

    @Test
    fun `sin permiso no toca nada`() {
        val p = PlataformaFalsa(tienePermiso = false)
        assertFalse(GestorNoMolestar(p).activar())
        assertEquals(FILTRO_TODAS, p.filtroActual)
        assertNull(p.filtroGuardado)
    }

    // --- Android 9 a 14: cambia el No molestar global ---

    @Test
    fun `activa solo alarmas y al restaurar lo deja como estaba`() {
        val p = PlataformaFalsa()
        val gestor = GestorNoMolestar(p)
        assertTrue(gestor.activar())
        assertEquals(FILTRO_ALARMAS, p.filtroActual)

        gestor.restaurar()
        assertEquals(FILTRO_TODAS, p.filtroActual)
        assertNull(p.filtroGuardado)
    }

    @Test
    fun `activar dos veces no pierde el estado original`() {
        val p = PlataformaFalsa()
        val gestor = GestorNoMolestar(p)
        gestor.activar()
        gestor.activar()
        gestor.restaurar()
        assertEquals(FILTRO_TODAS, p.filtroActual)
    }

    @Test
    fun `si el usuario ya tenia no molestar no se toca`() {
        val p = PlataformaFalsa(filtroActual = FILTRO_NINGUNA)
        val gestor = GestorNoMolestar(p)
        assertTrue(gestor.activar())
        assertEquals("no lo hacemos menos estricto", FILTRO_NINGUNA, p.filtroActual)
        gestor.restaurar()
        assertEquals(FILTRO_NINGUNA, p.filtroActual)
    }

    @Test
    fun `si el usuario lo cambio a mano durante la noche se respeta`() {
        val p = PlataformaFalsa()
        val gestor = GestorNoMolestar(p)
        gestor.activar()
        p.filtroActual = FILTRO_PRIORIDAD // el usuario lo cambió desde el panel rápido
        gestor.restaurar()
        assertEquals(FILTRO_PRIORIDAD, p.filtroActual)
        assertNull(p.filtroGuardado)
    }

    @Test
    fun `restaurar sin haber activado no hace nada`() {
        val p = PlataformaFalsa(filtroActual = FILTRO_PRIORIDAD)
        GestorNoMolestar(p).restaurar()
        assertEquals(FILTRO_PRIORIDAD, p.filtroActual)
    }

    @Test
    fun `el estado guardado sobrevive a un reinicio`() {
        val p = PlataformaFalsa()
        GestorNoMolestar(p).activar()
        // Se crea otro gestor (como tras reiniciar el teléfono): lee lo guardado.
        GestorNoMolestar(p).restaurar()
        assertEquals(FILTRO_TODAS, p.filtroActual)
    }

    // --- Android 15+: modo propio de la app ---

    @Test
    fun `android 15 enciende y apaga el modo propio de la app`() {
        val p = PlataformaFalsa(reglaPropia = true, filtroActual = FILTRO_TODAS)
        val gestor = GestorNoMolestar(p)
        assertTrue(gestor.activar())
        assertEquals(FILTRO_ALARMAS, p.filtroActual)
        gestor.restaurar()
        assertEquals("apaga el modo de la app", FILTRO_TODAS, p.filtroActual)
        assertNull(p.filtroGuardado)
    }

    @Test
    fun `si quitan el permiso a media noche solo se olvida lo guardado`() {
        val p = PlataformaFalsa()
        val gestor = GestorNoMolestar(p)
        gestor.activar()
        p.tienePermiso = false
        gestor.restaurar()
        assertNull(p.filtroGuardado)
    }
}
