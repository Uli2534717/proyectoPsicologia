package com.upt.descansa

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.usecase.ajustes.CompletarBienvenidaUseCase
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import com.upt.descansa.feature.noche.alarma.AjustesRepositoryFalso
import com.upt.descansa.testutil.MainDispatcherRule
import com.upt.descansa.testutil.NocheRepositoryFalso
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `primera vez abre en bienvenida`() {
        val vm = MainViewModel(ObservarAjustesUseCase(AjustesRepositoryFalso()), NocheRepositoryFalso())
        assertEquals(EstadoArranque.Listo(bienvenidaCompletada = false), vm.arranque.value)
    }

    @Test
    fun `si ya vio la bienvenida abre en inicio`() {
        val repo = AjustesRepositoryFalso(AjustesUsuario(bienvenidaCompletada = true))
        val vm = MainViewModel(ObservarAjustesUseCase(repo), NocheRepositoryFalso())
        assertEquals(EstadoArranque.Listo(bienvenidaCompletada = true), vm.arranque.value)
    }

    @Test
    fun `con una noche en curso abre en el modo noche`() = runTest {
        val repo = AjustesRepositoryFalso(AjustesUsuario(bienvenidaCompletada = true))
        val noches = NocheRepositoryFalso()
        noches.iniciar(iniciadaEn = 0L, horaLimiteEn = 1_000L, ajustes = AjustesUsuario())
        val vm = MainViewModel(ObservarAjustesUseCase(repo), noches)
        assertEquals(EstadoArranque.Listo(bienvenidaCompletada = true, nocheEnCurso = true), vm.arranque.value)
    }

    @Test
    fun `completar la bienvenida tambien acepta el aviso de salud`() = runTest {
        val repo = AjustesRepositoryFalso()
        CompletarBienvenidaUseCase(repo)()
        assertTrue(repo.ajustes.value.bienvenidaCompletada)
        assertTrue(repo.ajustes.value.avisoSaludAceptado)
    }
}
