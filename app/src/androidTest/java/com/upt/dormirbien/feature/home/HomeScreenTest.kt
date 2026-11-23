package com.upt.dormirbien.feature.home

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.upt.dormirbien.R
import com.upt.dormirbien.core.navigation.RutaApp
import com.upt.dormirbien.core.navigation.RutaVaciadoMental
import com.upt.dormirbien.core.theme.DormirBienTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de UI de [HomeScreen] (corre en emulador o teléfono).
 * Ejemplo de Compose Testing: dibujar la pantalla, buscar un elemento por su
 * texto, tocarlo y revisar qué pasó.
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun texto(id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun muestraElSaludo() {
        composeRule.setContent { DormirBienTheme { HomeScreen(onAbrirModulo = {}) } }

        composeRule.onNodeWithText(texto(R.string.inicio_saludo)).assertExists()
    }

    @Test
    fun alTocarVaciadoMentalPideAbrirEseModulo() {
        var rutaPedida: RutaApp? = null
        composeRule.setContent {
            DormirBienTheme { HomeScreen(onAbrirModulo = { rutaPedida = it }) }
        }

        composeRule.onNodeWithText(texto(R.string.vaciado_titulo)).performClick()

        assertEquals(RutaVaciadoMental, rutaPedida)
    }
}
