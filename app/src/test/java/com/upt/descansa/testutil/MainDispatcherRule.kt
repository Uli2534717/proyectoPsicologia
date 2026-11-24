package com.upt.descansa.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regla de JUnit para probar ViewModels.
 *
 * `viewModelScope` usa `Dispatchers.Main`, que en una prueba unitaria NO existe
 * (no hay teléfono ni hilo de interfaz). Esta regla lo reemplaza por un
 * dispatcher de pruebas antes de cada test y lo restaura al terminar.
 *
 * Uso: `@get:Rule val mainDispatcherRule = MainDispatcherRule()`
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
