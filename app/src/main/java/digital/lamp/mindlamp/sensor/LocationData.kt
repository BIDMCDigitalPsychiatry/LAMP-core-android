package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.Lamp
import digital.lamp.Locations
import digital.lamp.mindlamp.utils.LampLog
import java.lang.Exception
import digital.lamp.models.DimensionData
import digital.lamp.models.SensorEvent

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class LocationData constructor(sensorListener: SensorListener, context: Context){
   init {
       try {
           //Location Settings
           Lamp.startLocations(context)

           //Location Observer
           Locations.setSensorObserver { data ->
                LampLog.e(data.toString())
                if (data != null) {
                    val dimensionData =
                        DimensionData(
                            null,
                            null,
                            null,
                            null
                            ,
                            null,
                            null,
                            null,
                            data.longitude,
                            data.latitude,
                            data.altitude,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,null,null,null
                        )
                    val sensorEventData =
                        SensorEvent(
                            dimensionData,
                            "lamp.gps",System.currentTimeMillis().toDouble()
                        )
                    LampLog.e("Location : ${data.latitude} : ${data.longitude}")
                    sensorListener.getLocationData(sensorEventData)

                }
           }
       }catch (ex : Exception){
           ex.printStackTrace()
       }
   }
}

