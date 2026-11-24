package com.upt.descansa.domain.usecase.noche

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.MotivoDisparo
import com.upt.descansa.feature.noche.alarma.AjustesRepositoryFalso
import com.upt.descansa.testutil.NocheRepositoryFalso
import com.upt.descansa.testutil.ProgramadorAlarmaFalso
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/** Pruebas de la alarma de respaldo (Fase 1), sin Android: con programador y BD falsos. */
class CasosDeUsoNocheTest {

    private val zona = ZoneId.of("America/Mexico_City")
    private lateinit var ajustes: AjustesRepositoryFalso
    private lateinit var noches: NocheRepositoryFalso
    private lateinit var programador: ProgramadorAlarmaFalso

    @Before
    fun preparar() {
        ajustes = AjustesRepositoryFalso(AjustesUsuario(horaLimiteMinutos = 7 * 60, posponerMinutos = 10))
        noches = NocheRepositoryFalso()
        programador = ProgramadorAlarmaFalso()
    }

    private fun epoch(fecha: ZonedDateTime) = fecha.toInstant().toEpochMilli()

    @Test
    fun `activar a las 23 programa la alarma para manana a las 7`() = runTest {
        val ahora = ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona)
        val horaLimite = ActivarModoNocheUseCase(ajustes, noches, programador)(ahora)

        val esperada = epoch(ZonedDateTime.of(2026, 11, 24, 7, 0, 0, 0, zona))
        assertEquals(esperada, horaLimite)
        assertEquals(esperada, programador.programadaEn)
        assertEquals(1, noches.noches.size)
        assertEquals(esperada, noches.noches.single().horaLimiteEn)
        assertNull(noches.noches.single().terminadaEn)
    }

    @Test
    fun `activar dos veces deja solo una noche abierta`() = runTest {
        val activar = ActivarModoNocheUseCase(ajustes, noches, programador)
        activar(ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona))
        activar(ZonedDateTime.of(2026, 11, 23, 23, 5, 0, 0, zona))
        assertEquals(1, noches.noches.count { it.terminadaEn == null })
    }

    @Test
    fun `detener cancela la alarma y cierra la noche`() = runTest {
        ActivarModoNocheUseCase(ajustes, noches, programador)(ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona))
        DetenerModoNocheUseCase(noches, programador)(ahora = 1_000L)

        assertNull(programador.programadaEn)
        val noche = noches.noches.single()
        assertEquals(1_000L, noche.terminadaEn)
        assertEquals(MotivoDisparo.DETENIDA_MANUALMENTE, noche.motivo)
        assertNull(noche.disparadaEn)
    }

    @Test
    fun `apagar guarda cuando sono y el motivo hora limite`() = runTest {
        ActivarModoNocheUseCase(ajustes, noches, programador)(ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona))
        ApagarAlarmaUseCase(noches, programador)(disparadaEn = 500L, ahora = 800L)

        assertNull(programador.programadaEn)
        val noche = noches.noches.single()
        assertEquals(MotivoDisparo.HORA_LIMITE, noche.motivo)
        assertEquals(500L, noche.disparadaEn)
        assertEquals(800L, noche.terminadaEn)
    }

    @Test
    fun `posponer reprograma con los minutos de ajustes y no cierra la noche`() = runTest {
        ActivarModoNocheUseCase(ajustes, noches, programador)(ZonedDateTime.of(2026, 11, 23, 23, 0, 0, 0, zona))
        val nueva = PosponerAlarmaUseCase(ajustes, programador)(ahora = 0L)

        assertEquals(10 * 60_000L, nueva)
        assertEquals(10 * 60_000L, programador.programadaEn)
        assertNull(noches.noches.single().terminadaEn)
    }
}
