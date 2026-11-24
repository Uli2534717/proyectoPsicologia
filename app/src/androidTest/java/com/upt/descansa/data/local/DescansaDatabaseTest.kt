package com.upt.descansa.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.upt.descansa.data.local.entity.EpocaEntity
import com.upt.descansa.data.local.entity.NocheEntity
import com.upt.descansa.data.local.entity.PendienteEntity
import com.upt.descansa.domain.model.FuenteDatos
import com.upt.descansa.domain.model.MarcaEpoca
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Pruebas de Room con base EN MEMORIA (no toca los datos reales del teléfono). */
@RunWith(AndroidJUnit4::class)
class DescansaDatabaseTest {

    private lateinit var db: DescansaDatabase

    @Before
    fun crear() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, DescansaDatabase::class.java).build()
    }

    @After
    fun cerrar() = db.close()

    private fun noche(iniciada: Long, terminada: Long? = null) = NocheEntity(
        iniciadaEn = iniciada,
        terminadaEn = terminada,
        horaLimiteEn = iniciada + 8 * 3_600_000,
        ventanaMinutos = 30,
        fuente = FuenteDatos.ACELEROMETRO_TELEFONO,
        noMolestarActivado = true,
        versionMotor = 1,
    )

    @Test
    fun soloLaNocheSinTerminarEsLaActiva() = runTest {
        db.nocheDao().insertar(noche(iniciada = 1_000, terminada = 2_000))
        val idActiva = db.nocheDao().insertar(noche(iniciada = 3_000))
        assertEquals(idActiva, db.nocheDao().obtenerActiva()?.id)
    }

    @Test
    fun alBorrarUnaNocheSeBorranSusEpocasPeroNoSusPendientes() = runTest {
        val id = db.nocheDao().insertar(noche(iniciada = 1_000, terminada = 2_000))
        db.epocaDao().insertarTodas(listOf(EpocaEntity(id, 60_000, 0.01, null, MarcaEpoca.TRANQUILA)))
        val idPendiente = db.pendienteDao().insertar(PendienteEntity(nocheId = id, texto = "Estudiar", creadoEn = 1_500))

        // Aún no hay función de borrar noches en el DAO (llega con "Borrar mis datos"), así que usamos SQL.
        db.openHelper.writableDatabase.execSQL("DELETE FROM noches WHERE id = ?", arrayOf<Any>(id))

        assertEquals(emptyList<EpocaEntity>(), db.epocaDao().observarDeNoche(id).first())
        val pendiente = db.pendienteDao().observarVisibles(hechosDesde = 0).first().single()
        assertEquals(idPendiente, pendiente.id)
        assertNull(pendiente.nocheId)
    }
}
