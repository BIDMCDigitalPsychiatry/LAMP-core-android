package lamp.mindlamp.sensormodule.aware

import android.content.Context
import android.util.Log
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Locations
import com.aware.providers.Locations_Provider
import lamp.mindlamp.sensormodule.aware.AwareLocationListener
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants
import java.lang.Exception


/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class LocationData constructor(awareListener: AwareLocationListener, context: Context, sensorname:String) {
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
                Log.d("Location data", data.toString())
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
                            null, null, null
                        )
                    val sensorEventData =
                        SensorEventData(
                            dimensionData,
                            sensorname, System.currentTimeMillis()
                        )
                    awareListener.getLocationData(Constants.SUCCESS, sensorEventData)

                } else {
                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getLocationData(Constants.FAILURE, sensorEventData)
                }
            }
            android.os.Handler().postDelayed({
                Aware.stopLocations(context)
            }, 3000)
        } catch (ex: Exception) {
            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getLocationData(Constants.ERROR, sensorEventData)
        }
    }
}