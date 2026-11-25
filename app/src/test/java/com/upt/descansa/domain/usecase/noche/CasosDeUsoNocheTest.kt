package com.upt.descansa.domain.usecase.noche

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.Disparo
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.domain.model.TipoSimulacion
import com.upt.descansa.feature.noche.alarma.AjustesRepositoryFalso
import com.upt.descansa.testutil.ControlNoMolestarFalso
import com.upt.descansa.testutil.ControlServicioNocturnoFalso
import com.upt.descansa.testutil.NocheRepositoryFalso
import com.upt.descansa.testutil.ProgramadorAlarmaFalso
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/** Pruebas de los casos de uso de la noche, sin Android: con dependencias falsas. */
class CasosDeUsoNocheTest {

    private val zona = ZoneId.of("America/Mexico_City")
    private val once = ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona)
    private lateinit var ajustes: AjustesRepositoryFalso
    private lateinit var noches: NocheRepositoryFalso
    private lateinit var programador: ProgramadorAlarmaFalso
    private lateinit var servicio: ControlServicioNocturnoFalso
    private lateinit var noMolestar: ControlNoMolestarFalso

    @Before
    fun preparar() {
        ajustes = AjustesRepositoryFalso(AjustesUsuario(horaLimiteMinutos = 7 * 60, posponerMinutos = 10))
        noches = NocheRepositoryFalso()
        programador = ProgramadorAlarmaFalso()
        servicio = ControlServicioNocturnoFalso()
        noMolestar = ControlNoMolestarFalso()
    }

    private fun epoch(fecha: ZonedDateTime) = fecha.toInstant().toEpochMilli()
    private suspend fun activar() = ActivarModoNocheUseCase(ajustes, noches, programador, servicio, noMolestar)(once)

    @Test
    fun `activar a las 23 programa la alarma para manana a las 7 y arranca el servicio`() = runTest {
        val horaLimite = activar()

        val esperada = epoch(ZonedDateTime.of(2026, 11, 24, 7, 0, 0, 0, zona))
        assertEquals(esperada, horaLimite)
        assertEquals(esperada, programador.programadaEn)
        assertEquals(esperada, noches.noches.single().horaLimiteEn)
        assertNull(noches.noches.single().terminadaEn)
        assertTrue(servicio.corriendo)
        assertNull("noche real, no simulada", servicio.simulacion)
        assertTrue("No molestar está activado por defecto", noMolestar.activo)
    }

    @Test
    fun `si el usuario apago no molestar en ajustes no se activa`() = runTest {
        ajustes = AjustesRepositoryFalso(AjustesUsuario(noMolestarActivado = false))
        activar()
        assertFalse(noMolestar.activo)
    }

    @Test
    fun `sin permiso de no molestar la noche se activa igual`() = runTest {
        noMolestar = ControlNoMolestarFalso(permiso = false)
        activar()
        assertFalse(noMolestar.activo)
        assertTrue(servicio.corriendo)
        assertEquals(1, noches.noches.size)
    }

    @Test
    fun `activar dos veces deja solo una noche abierta`() = runTest {
        activar()
        ActivarModoNocheUseCase(ajustes, noches, programador, servicio, noMolestar)(once.plusMinutes(5))
        assertEquals(1, noches.noches.count { it.terminadaEn == null })
    }

    @Test
    fun `detener cancela la alarma, el servicio y cierra la noche`() = runTest {
        activar()
        DetenerModoNocheUseCase(noches, programador, servicio, noMolestar)(ahora = 1_000L)

        assertNull(programador.programadaEn)
        assertFalse(servicio.corriendo)
        assertFalse("restaura el No molestar", noMolestar.activo)
        val noche = noches.noches.single()
        assertEquals(1_000L, noche.terminadaEn)
        assertEquals(MotivoDisparo.DETENIDA_MANUALMENTE, noche.motivo)
        assertNull(noche.disparadaEn)
    }

    @Test
    fun `apagar la alarma de respaldo guarda motivo hora limite`() = runTest {
        activar()
        ApagarAlarmaUseCase(noches, programador, servicio, noMolestar)(disparadaEn = 500L, ahora = 800L)

        assertNull(programador.programadaEn)
        assertFalse("al despertar se restaura el No molestar", noMolestar.activo)
        val noche = noches.noches.single()
        assertEquals(MotivoDisparo.HORA_LIMITE, noche.motivo)
        assertEquals(500L, noche.disparadaEn)
        assertEquals(800L, noche.terminadaEn)
    }

    @Test
    fun `apagar despues de sueno ligero conserva el motivo del motor`() = runTest {
        activar()
        // El servicio nocturno anotó que el motor la hizo sonar a las 300.
        noches.registrarDisparo(Disparo(MotivoDisparo.SUENO_LIGERO, 300L))
        ApagarAlarmaUseCase(noches, programador, servicio, noMolestar)(disparadaEn = 301L, ahora = 900L)

        val noche = noches.noches.single()
        assertEquals(MotivoDisparo.SUENO_LIGERO, noche.motivo)
        assertEquals(300L, noche.disparadaEn)
    }

    @Test
    fun `posponer reprograma con los minutos de ajustes y no cierra la noche`() = runTest {
        activar()
        val nueva = PosponerAlarmaUseCase(ajustes, programador)(ahora = 0L)

        assertEquals(10 * 60_000L, nueva)
        assertEquals(10 * 60_000L, programador.programadaEn)
        assertNull(noches.noches.single().terminadaEn)
        assertTrue("al posponer sigue el No molestar", noMolestar.activo)
    }

    @Test
    fun `la simulacion dura un minuto con ventana de 20 y arranca el servicio simulado`() = runTest {
        IniciarSimulacionUseCase(ajustes, noches, programador, servicio, noMolestar)(TipoSimulacion.INQUIETA, ahora = 1_000L)

        assertEquals(61_000L, programador.programadaEn)
        assertEquals(20, noches.noches.single().ventanaMinutos)
        assertEquals(TipoSimulacion.INQUIETA, servicio.simulacion)
    }
}
