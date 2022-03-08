package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Locations
import com.aware.providers.Locations_Provider
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.UserAgent
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import lamp.mindlamp.sensormodule.aware.Accelerometer
import lamp.mindlamp.sensormodule.aware.AwareLocationListener
import lamp.mindlamp.sensormodule.aware.LocationData
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants
import java.lang.Exception


/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class LocationSensor constructor(
    var awareListener: SensorAwareListener,
    var context: Context,
    var dataViewModel: DataViewModel
) :
    AwareLocationListener {


    init {
        LocationData(this@LocationSensor, context,"lamp.android.watch.gps")
    }

    override fun getLocationData(status: Int, sensorEventData: SensorEventData) {

        if (status == Constants.SUCCESS) {
            awareListener?.getLocationData(sensorEventData)
        } else if (status == Constants.FAILURE) {
            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_location_null),
                UserAgent(),
                AppState.session.userId
            )
           /* dataViewModel.addLogEvent(
                Utils.getApplicationName(context),
                context.getString(R.string.warning),
                logEventRequest
            )*/

        } else {
            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_location_error),
                UserAgent(),
                AppState.session.userId
            )
            /*dataViewModel.addLogEvent(
                Utils.getApplicationName(context),
                context.getString(R.string.error),
                logEventRequest
            )*/
        }


    }

}