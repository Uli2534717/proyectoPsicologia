package com.upt.dormirbien.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.upt.dormirbien.data.local.dao.EntradaVaciadoDao
import com.upt.dormirbien.data.local.entity.EntradaVaciadoEntity
import com.upt.dormirbien.data.local.entity.TipoEntradaVaciado
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de Room con una base de datos EN MEMORIA: se crea vacía en cada
 * prueba y desaparece al terminar, así que no toca los datos reales del teléfono.
 */
@RunWith(AndroidJUnit4::class)
class EntradaVaciadoDaoTest {

    private lateinit var db: DormirBienDatabase
    private lateinit var dao: EntradaVaciadoDao

    @Before
    fun crearBase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, DormirBienDatabase::class.java).build()
        dao = db.entradaVaciadoDao()
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    @Test
    fun lasEntradasArchivadasNoAparecenEnActivas() = runTest {
        val id = dao.insertar(
            EntradaVaciadoEntity(texto = "Tarea de cálculo", tipo = TipoEntradaVaciado.PENDIENTE_MANANA, fechaCreacion = 1L),
        )
        dao.insertar(
            EntradaVaciadoEntity(texto = "Día pesado", tipo = TipoEntradaVaciado.SOLO_DESAHOGO, fechaCreacion = 2L),
        )

        // "Soltar" la primera entrada.
        dao.actualizar(
            EntradaVaciadoEntity(id = id, texto = "Tarea de cálculo", tipo = TipoEntradaVaciado.PENDIENTE_MANANA, fechaCreacion = 1L, archivada = true),
        )

        val activas = dao.observarActivas().first()
        assertEquals(listOf("Día pesado"), activas.map { it.texto })
    }
}
