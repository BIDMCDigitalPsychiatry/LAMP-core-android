package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Locations
import com.aware.providers.Locations_Provider
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.SensorEventRequest
import digital.lamp.mindlamp.utils.LampLog


/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class LocationData constructor(awareListener: AwareListener, context: Context){
   init {
        //Location Settings
        Aware.setSetting(context, Aware_Preferences.STATUS_LOCATION_GPS, true)
        Aware.setSetting(context, Aware_Preferences.FREQUENCY_LOCATION_GPS, 0)
        Aware.setSetting(context, Aware_Preferences.STATUS_LOCATION_NETWORK, true)
        Aware.setSetting(context, Aware_Preferences.STATUS_LOCATION_PASSIVE, true)
        Aware.startLocations(context)

        //Location Observer
        Locations.setSensorObserver { data ->
            LampLog.e(data.toString())
            if(data != null){
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
                        null
                    )
                val sensorEventRequest =
                    SensorEventRequest(
                        dimensionData,
                        "lamp.gps",
                        System.currentTimeMillis()
                    )
                awareListener.getLocationData(sensorEventRequest)

            }
        }
        android.os.Handler().postDelayed({
            Aware.stopLocations(context)
        }, 3000)
    }
}