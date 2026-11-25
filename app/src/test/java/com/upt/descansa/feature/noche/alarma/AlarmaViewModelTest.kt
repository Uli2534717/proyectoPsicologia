package com.upt.descansa.feature.noche.alarma

import app.cash.turbine.test
import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.repository.AjustesRepository
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import com.upt.descansa.domain.usecase.noche.ActivarModoNocheUseCase
import com.upt.descansa.testutil.ControlNoMolestarFalso
import com.upt.descansa.testutil.ControlServicioNocturnoFalso
import com.upt.descansa.testutil.MainDispatcherRule
import com.upt.descansa.testutil.NocheRepositoryFalso
import com.upt.descansa.testutil.ProgramadorAlarmaFalso
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Repositorio FALSO en memoria. Para un repositorio tan simple es más claro que
 * un mock: guarda de verdad y se puede revisar el resultado.
 */
class AjustesRepositoryFalso(inicial: AjustesUsuario = AjustesUsuario()) : AjustesRepository {
    private val estado = MutableStateFlow(inicial)
    override val ajustes: StateFlow<AjustesUsuario> = estado
    override suspend fun actualizar(cambio: (AjustesUsuario) -> AjustesUsuario) {
        estado.value = cambio(estado.value)
    }
}

class AlarmaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repositorio: AjustesRepositoryFalso
    private lateinit var programador: ProgramadorAlarmaFalso
    private lateinit var viewModel: AlarmaViewModel

    @Before
    fun preparar() {
        repositorio = AjustesRepositoryFalso()
        programador = ProgramadorAlarmaFalso()
        val activar = ActivarModoNocheUseCase(repositorio, NocheRepositoryFalso(), programador, ControlServicioNocturnoFalso(), ControlNoMolestarFalso())
        viewModel = AlarmaViewModel(ObservarAjustesUseCase(repositorio), repositorio, activar)
    }

    @Test
    fun `activar modo noche programa la alarma y despues navega`() = runTest {
        var navego = false
        viewModel.alActivarModoNoche { navego = true }
        assertTrue(navego)
        assertNotNull(programador.programadaEn)
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
    fun `cambiar la hora limite se guarda en los ajustes`() = runTest {
        viewModel.alCambiarHoraLimite(6 * 60 + 15)
        assertEquals(6 * 60 + 15, repositorio.ajustes.value.horaLimiteMinutos)
    }

    @Test
    fun `apagar no molestar se guarda`() = runTest {
        viewModel.alCambiarNoMolestar(false)
        assertEquals(false, repositorio.ajustes.value.noMolestarActivado)
    }
}
