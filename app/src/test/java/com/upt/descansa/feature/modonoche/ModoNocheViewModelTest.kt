package com.upt.descansa.feature.modonoche

import com.upt.descansa.domain.model.AjustesUsuario
import com.upt.descansa.domain.usecase.ajustes.ObservarAjustesUseCase
import com.upt.descansa.domain.usecase.noche.DetenerModoNocheUseCase
import com.upt.descansa.feature.noche.alarma.AjustesRepositoryFalso
import com.upt.descansa.testutil.ControlNoMolestarFalso
import com.upt.descansa.testutil.ControlServicioNocturnoFalso
import com.upt.descansa.testutil.MainDispatcherRule
import com.upt.descansa.testutil.NocheRepositoryFalso
import com.upt.descansa.testutil.ProgramadorAlarmaFalso
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ModoNocheViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var noches: NocheRepositoryFalso
    private lateinit var noMolestar: ControlNoMolestarFalso
    private lateinit var viewModel: ModoNocheViewModel

    @Before
    fun preparar() = runTest {
        val ajustes = AjustesRepositoryFalso()
        noches = NocheRepositoryFalso()
        noches.iniciar(0L, 1_000L, AjustesUsuario())
        noMolestar = ControlNoMolestarFalso(permiso = false)
        val detener = DetenerModoNocheUseCase(noches, ProgramadorAlarmaFalso(), ControlServicioNocturnoFalso(), noMolestar)
        viewModel = ModoNocheViewModel(ObservarAjustesUseCase(ajustes), detener, noches, noMolestar)
    }

    @Test
    fun `sin permiso avisa y no activa`() {
        viewModel.alVolver()
        assertFalse(viewModel.permisoNoMolestar.value)
        assertFalse(noMolestar.activo)
    }

    @Test
    fun `al volver con el permiso recien dado activa el no molestar`() {
        viewModel.alVolver()
        noMolestar.permiso = true // el usuario fue a Ajustes de Android y lo dio
        viewModel.alVolver()
        assertTrue(viewModel.permisoNoMolestar.value)
        assertTrue(noMolestar.activo)
    }

    @Test
    fun `con el permiso de siempre no reactiva lo que el apagado restauro`() {
        noMolestar.permiso = true
        viewModel.alVolver()
        // Esta es la carrera del emulador: la alarma se apagó (restauró) y la
        // pantalla se reanudó mientras la noche aún no se cerraba.
        viewModel.alVolver()
        assertFalse(noMolestar.activo)
    }
}
