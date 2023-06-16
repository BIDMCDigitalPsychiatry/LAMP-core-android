package digital.lamp.mindlamp.sensor.health_services.utils

import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData

import lamp.mindlamp.sensormodule.aware.model.DimensionData

object SensorDataUtils {
    fun getHeartRateSensorData(heartRateValue: Double): SensorEventData {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null, heartRateValue
            )

        val sensorEventData =
            SensorEventData(
                dimensionData,
                "lamp.android.watch.heart_rate", System.currentTimeMillis()
            )
        return sensorEventData
    }

    fun getSensorEventData(sensorData: SensorEvent): SensorEventData {

        val sensorEventData =
            SensorEventData(
                sensorData.data,
                sensorData.sensor, System.currentTimeMillis()
            )
        return sensorEventData
    }
}