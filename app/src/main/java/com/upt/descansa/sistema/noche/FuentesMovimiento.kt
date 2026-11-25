package com.upt.descansa.sistema.noche

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.upt.descansa.domain.model.TipoSimulacion
import com.upt.descansa.motor.AgregadorEpocas
import com.upt.descansa.motor.EpocaCruda
import com.upt.descansa.motor.SimuladorNoche
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

/**
 * SensorCollector: lee el acelerómetro y emite una [EpocaCruda] por minuto.
 *
 * Frecuencia `SENSOR_DELAY_NORMAL` (~5 lecturas por segundo): basta para notar
 * una vuelta en la cama y gasta mucho menos batería que leer a máxima velocidad.
 * Si el teléfono no tiene acelerómetro, el Flow termina enseguida (y la alarma de
 * respaldo sigue funcionando).
 */
fun epocasDelAcelerometro(context: Context): Flow<EpocaCruda> {
    val gestor = context.getSystemService(SensorManager::class.java)
    val acelerometro = gestor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return emptyFlow()
    return callbackFlow {
        val agregador = AgregadorEpocas()
        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent) {
                val (x, y, z) = evento.values
                agregador.agregar(System.currentTimeMillis(), x, y, z)?.let { trySend(it) }
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) = Unit
        }
        gestor.registerListener(oyente, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)
        // Cuando el servicio deja de escuchar, se apaga el sensor (ahorra batería).
        awaitClose { gestor.unregisterListener(oyente) }
    }
}

/**
 * Noche simulada y ACELERADA: una época por segundo en vez de una por minuto.
 * El motor no nota la diferencia porque compara marcas de tiempo, no minutos.
 */
fun epocasSimuladas(tipo: TipoSimulacion, ventanaInicioEn: Long): Flow<EpocaCruda> = flow {
    val simulador = SimuladorNoche(tipo)
    var indice = 0
    while (true) {
        delay(1_000L)
        val ahora = System.currentTimeMillis()
        emit(EpocaCruda(ahora, simulador.actividad(indice, enVentana = ahora >= ventanaInicioEn)))
        indice++
    }
}
