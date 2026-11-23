package com.upt.dormirbien.feature.ajustes

import app.cash.turbine.test
import com.upt.dormirbien.domain.model.ModoTema
import com.upt.dormirbien.domain.model.PreferenciasUsuario
import com.upt.dormirbien.domain.repository.PreferenciasRepository
import com.upt.dormirbien.domain.usecase.preferencias.CambiarModoTemaUseCase
import com.upt.dormirbien.domain.usecase.preferencias.ObservarPreferenciasUseCase
import com.upt.dormirbien.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas del [AjustesViewModel]. Sirven de EJEMPLO para el equipo:
 * - MockK crea un repositorio falso (no se usa DataStore real).
 * - Turbine permite revisar, uno por uno, los valores que emite un Flow.
 *
 * Estructura de cada prueba: Dado (preparar) / Cuando (actuar) / Entonces (verificar).
 */
class AjustesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencias = MutableStateFlow(PreferenciasUsuario())
    private val repositorio: PreferenciasRepository = mockk()

    private lateinit var viewModel: AjustesViewModel

    @Before
    fun preparar() {
        every { repositorio.preferencias } returns preferencias
        coEvery { repositorio.cambiarModoTema(any()) } just runs
        viewModel = AjustesViewModel(
            observarPreferencias = ObservarPreferenciasUseCase(repositorio),
            cambiarModoTema = CambiarModoTemaUseCase(repositorio),
        )
    }

    @Test
    fun `el tema inicial es oscuro`() = runTest {
        viewModel.uiState.test {
            assertEquals(ModoTema.OSCURO, awaitItem().modoTema)
        }
    }

    @Test
    fun `cuando cambian las preferencias el estado se actualiza`() = runTest {
        viewModel.uiState.test {
            awaitItem() // valor inicial

            preferencias.value = PreferenciasUsuario(modoTema = ModoTema.CLARO)

            assertEquals(ModoTema.CLARO, awaitItem().modoTema)
        }
    }

    @Test
    fun `al seleccionar un tema se guarda en el repositorio`() = runTest {
        viewModel.alSeleccionarModoTema(ModoTema.SISTEMA)

        coVerify(exactly = 1) { repositorio.cambiarModoTema(ModoTema.SISTEMA) }
    }
}
