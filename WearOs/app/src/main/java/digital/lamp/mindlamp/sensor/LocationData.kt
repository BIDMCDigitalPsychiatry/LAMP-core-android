package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Locations
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class LocationData constructor(
    sensorListener: SensorListener,
    context: Context,
    frequency: Double?
) {
    companion object {
        private val TAG = LocationData::class.java.simpleName
    }

    init {
        try {
            //Location Settings
            Lamp.startLocations(context)
            frequency?.let {
                val interval = (1 / frequency!!) * 1000
                Locations.setInterval(interval.toLong())
            }
            //Location Observer
            Locations.setSensorObserver { data ->
                LampLog.e(data.toString())
                if (data != null) {
                    val dimensionData =
                        DimensionData(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            data.longitude,
                            data.latitude,
                            data.altitude,
                            data.accuracy,
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
                        SensorEvent(
                            dimensionData,
                            Sensors.GPS.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    LampLog.e("Location : ${data.latitude} : ${data.longitude}")
                    sensorListener.getLocationData(sensorEventData)
                } else {

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()


        }
    }


}

