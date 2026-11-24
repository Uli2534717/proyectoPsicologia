package com.upt.descansa.feature.inicio

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.upt.descansa.R
import com.upt.descansa.core.navigation.RutaApp
import com.upt.descansa.core.navigation.RutaRespiracion
import com.upt.descansa.core.theme.DescansaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Prueba de UI de Inicio (corre en emulador o teléfono). */
@RunWith(AndroidJUnit4::class)
class InicioScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun texto(id: Int): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun sinNochesMuestraEstadoVacio() {
        composeRule.setContent { DescansaTheme { InicioScreen(onPrepararNoche = {}, onAbrir = {}) } }
        composeRule.onNodeWithText(texto(R.string.inicio_sin_noches)).assertExists()
    }

    @Test
    fun prepararMiNocheIniciaElFlujo() {
        var iniciado = false
        composeRule.setContent { DescansaTheme { InicioScreen(onPrepararNoche = { iniciado = true }, onAbrir = {}) } }
        composeRule.onNodeWithText(texto(R.string.inicio_preparar_noche)).performClick()
        assertTrue(iniciado)
    }

    @Test
    fun herramientaRespiracionAbreSuPantalla() {
        var ruta: RutaApp? = null
        composeRule.setContent { DescansaTheme { InicioScreen(onPrepararNoche = {}, onAbrir = { ruta = it }) } }
        composeRule.onNodeWithText(texto(R.string.herramienta_respiracion)).performClick()
        assertEquals(RutaRespiracion, ruta)
    }
}
