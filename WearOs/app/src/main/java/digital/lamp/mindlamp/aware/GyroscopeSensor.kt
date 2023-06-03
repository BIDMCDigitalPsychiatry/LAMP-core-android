package digital.lamp.mindlamp.aware

import android.content.Context

import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.UserAgent
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import lamp.mindlamp.sensormodule.aware.AwareGyroscopeListener
import lamp.mindlamp.sensormodule.aware.Gyroscope
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GyroscopeSensor constructor(
    var awareListener: SensorAwareListener,
    var context: Context,
    var dataViewModel: DataViewModel
) :
    AwareGyroscopeListener {

    init {
        Gyroscope(this@GyroscopeSensor, context,"lamp.android.watch.gyroscope")
    }

    override fun getGyroscopeData(status: Int, sensorEventData: SensorEventData) {

        if (status == Constants.SUCCESS) {
            awareListener.getGyroscopeData(sensorEventData)
        } else if (status == Constants.FAILURE) {
            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_gyroscope_null),
                UserAgent(),
                AppState.session.userId
            )

        } else {

            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_gyroscope_error),
                UserAgent(),
                AppState.session.userId
            )
        }
    }

}