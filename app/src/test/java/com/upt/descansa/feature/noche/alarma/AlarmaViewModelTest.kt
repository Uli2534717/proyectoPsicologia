package com.upt.descansa.feature.noche.alarma

import app.cash.turbine.test
import com.upt.descansa.domain.alarma.AlarmaProgramada
import com.upt.descansa.domain.alarma.AlarmaRespaldo
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import com.upt.descansa.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Repositorio FALSO en memoria. Para algo tan simple es más claro que un mock:
 * guarda de verdad y se puede revisar el resultado.
 */
class AjustesRepositoryFalso(inicial: AjustesUsuario = AjustesUsuario()) : AjustesRepository {
    private val estado = MutableStateFlow(inicial)
    override val ajustes: StateFlow<AjustesUsuario> = estado
    override suspend fun actualizar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        estado.value = cambio(estado.value)
    }
}

/** Alarma FALSA: recuerda lo que se programó, sin tocar AlarmManager. */
class AlarmaRespaldoFalsa(var exactasPermitidas: Boolean = true) : AlarmaRespaldo {
    private val estado = MutableStateFlow<AlarmaProgramada?>(null)
    override val programada: StateFlow<AlarmaProgramada?> = estado
    override fun programar(alarma: AlarmaProgramada) {
        estado.value = alarma
    }
    override fun cancelar() {
        estado.value = null
    }
    override fun puedeProgramarExactas() = exactasPermitidas
}

class AlarmaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mexico = ZoneId.of("America/Mexico_City")

    /** 23:30 del 24 de noviembre de 2026, hora de México. */
    private val ahora = ZonedDateTime.of(2026, 11, 24, 23, 30, 0, 0, mexico).toInstant().toEpochMilli()

    private lateinit var repositorio: AjustesRepositoryFalso
    private lateinit var alarma: AlarmaRespaldoFalsa
    private lateinit var viewModel: AlarmaViewModel

    @Before
    fun preparar() {
        repositorio = AjustesRepositoryFalso()
        alarma = AlarmaRespaldoFalsa()
        viewModel = AlarmaViewModel(ObservarAjustesUseCase(repositorio), repositorio, alarma, { ahora }, { mexico })
    }

    @Test
    fun `valores por defecto 0700 con ventana de 30 y acelerometro`() = runTest {
        viewModel.uiState.test {
            val estado = awaitItem()
            assertEquals(7 * 60, estado.horaLimiteMinutos)
            assertEquals(30, estado.ventanaMinutos)
            assertEquals(FuenteDatos.ACELEROMETRO_TELEFONO, estado.fuente)
        }
    }

    @Test
    fun `cambiar la ventana se guarda en los ajustes`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.alCambiarVentana(15)
            assertEquals(15, awaitItem().ventanaMinutos)
        }
        assertEquals(15, repositorio.ajustes.value.ventanaMinutos)
    }

    @Test
    fun `apagar no molestar se guarda`() = runTest {
        viewModel.alCambiarNoMolestar(false)
        assertEquals(false, repositorio.ajustes.value.noMolestarActivado)
    }

    @Test
    fun `activar programa la alarma de respaldo para manana a la hora limite`() = runTest {
        repositorio.actualizar { it.copy(horaLimiteMinutos = 6 * 60 + 45, posponerMinutos = 10) }

        val resultado = viewModel.activarModoNoche()

        val esperado = ZonedDateTime.of(2026, 11, 25, 6, 45, 0, 0, mexico).toInstant().toEpochMilli()
        assertEquals(ResultadoActivacion.Programada(esperado), resultado)
        assertEquals(AlarmaProgramada(esperado, 6 * 60 + 45, 10), alarma.programada.value)
    }

    @Test
    fun `sin permiso de alarmas exactas no se activa`() = runTest {
        alarma.exactasPermitidas = false

        assertEquals(ResultadoActivacion.SinPermisoExactas, viewModel.activarModoNoche())
        assertNull(alarma.programada.value)
    }
}
