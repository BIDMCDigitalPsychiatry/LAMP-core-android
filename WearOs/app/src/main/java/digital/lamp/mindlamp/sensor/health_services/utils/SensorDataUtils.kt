package digital.lamp.mindlamp.sensor.health_services.utils

import android.location.Location
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.StepsData
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog
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
    fun getLocationSensorData(location:Location): SensorEventData {
        val dimensionData =
            digital.lamp.lamp_kotlin.lamp_core.models.DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                location.longitude,
                location.latitude,
                location.altitude,
                location.accuracy,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null, null, null, null, null, null
            )
        val sensorEventData =
            SensorEventData(
                dimensionData,
                Sensors.GPS.sensor_name, System.currentTimeMillis()
            )
         return sensorEventData
    }

    fun getStepsSensorData( endTimestamp: Long, stepsValue: Integer): SensorEventData {
        val dimensionData =
            StepsData(
                "count",  stepsValue , "step_count",  "Android Watch"
            )
        return SensorEventData(dimensionData, Sensors.STEPS.sensor_name,endTimestamp)
    }
}