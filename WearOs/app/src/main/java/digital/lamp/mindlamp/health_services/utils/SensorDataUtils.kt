package digital.lamp.mindlamp.health_services.utils

import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData

import lamp.mindlamp.sensormodule.aware.model.DimensionData

object SensorDataUtils {
    fun  getHeartRateSensorData(heartRateValue:Double): SensorEventData {
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
}