package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.Lamp
import digital.lamp.Locations
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils
import java.lang.Exception

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
                        SensorEventData(
                            dimensionData,
                            "lamp.gps",System.currentTimeMillis().toDouble()
                        )
                    LampLog.e("Location : ${sensorEventData.dimensionData?.latitude} : ${sensorEventData.dimensionData?.longitude}")
                    sensorListener.getLocationData(sensorEventData)

                }else{
                    val logEventRequest = LogEventRequest()
                    logEventRequest.message = context.getString(R.string.log_location_null)
                    LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.warning), logEventRequest)
                }
           }

//           android.os.Handler().postDelayed({
//               Aware.stopLocations(context)
//           }, 3000)
       }catch (ex : Exception){
           val logEventRequest = LogEventRequest()
           logEventRequest.message = context.getString(R.string.log_location_error)
           LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
       }
   }
}

