package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Locations
import com.aware.providers.Locations_Provider
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils
import java.lang.Exception


/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class LocationData constructor(awareListener: AwareListener, context: Context){
   init {
       try {
           //Location Settings
           Aware.setSetting(context, Aware_Preferences.STATUS_LOCATION_GPS, true)
           Aware.setSetting(context, Aware_Preferences.FREQUENCY_LOCATION_GPS, 0)
           Aware.setSetting(context, Aware_Preferences.STATUS_LOCATION_NETWORK, true)
           Aware.setSetting(context, Aware_Preferences.STATUS_LOCATION_PASSIVE, true)
           Aware.startLocations(context)

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
                           data.getAsDouble(Locations_Provider.Locations_Data.LONGITUDE),
                           data.getAsDouble(Locations_Provider.Locations_Data.LATITUDE),
                           data.getAsDouble(Locations_Provider.Locations_Data.ALTITUDE),
                           null,
                           null,
                           null,
                           null,
                           null,
                           null,null,null
                       )
                   val sensorEventData =
                       SensorEventData(
                           dimensionData,
                           "lamp.gps",System.currentTimeMillis()
                       )
                   LampLog.e("Location : ${sensorEventData.dimensionData?.latitude} : ${sensorEventData.dimensionData?.longitude}")
                   awareListener.getLocationData(sensorEventData)

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