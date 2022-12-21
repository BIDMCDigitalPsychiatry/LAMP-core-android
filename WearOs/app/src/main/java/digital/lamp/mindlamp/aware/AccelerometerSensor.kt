package digital.lamp.mindlamp.aware

import android.content.Context
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.UserAgent
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import digital.lamp.mindlamp.web.WebServiceCallback
import lamp.mindlamp.sensormodule.aware.Accelerometer
import lamp.mindlamp.sensormodule.aware.AwareAccelerometerListener
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class AccelerometerSensor constructor(
    var awareListener: SensorAwareListener,
    var context: Context,
    var dataViewModel: DataViewModel
) :
    AwareAccelerometerListener {


    init {
        Accelerometer(this@AccelerometerSensor, context, "lamp.android.watch.accelerometer")
    }
    override fun getAccelerometerData(status: Int, sensorEventData: SensorEventData) {
        if (status == Constants.SUCCESS) {
            awareListener.getAccelerometerData(sensorEventData)
        } else if (status == Constants.FAILURE) {
            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_accelerometer_null),
                UserAgent(),
                AppState.session.userId
            )
        } else {

            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_accelerometer_error),
                UserAgent(),
                AppState.session.userId
            )
        }
    }

}