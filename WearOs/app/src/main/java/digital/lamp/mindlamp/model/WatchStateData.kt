package digital.lamp.mindlamp.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataMap
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.*


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
@Suppress("DEPRECATION")
class WatchStateData constructor(
    awareListener: View.OnClickListener,
    context: Context,
    googleApiClient: GoogleApiClient
) {
    private var awllistener = awareListener
    private var requestedsensors: DataMap = DataMap()
    private var sensorEventDataList: ArrayList<SensorEventData> = arrayListOf()

    init {

        var arraysensors = arrayOf(
            Sensor.TYPE_ACCELEROMETER.toString(),
            Sensor.TYPE_GYROSCOPE.toString(),
            Sensor.TYPE_GRAVITY.toString(),
            Sensor.TYPE_LINEAR_ACCELERATION.toString(),
            Sensor.TYPE_ROTATION_VECTOR.toString(),
            Sensor.TYPE_RELATIVE_HUMIDITY.toString(),
            Sensor.TYPE_AMBIENT_TEMPERATURE.toString(),
            Sensor.TYPE_STEP_DETECTOR.toString(),
            Sensor.TYPE_STEP_COUNTER.toString(),
            Sensor.TYPE_HEART_RATE.toString(),
            Sensor.TYPE_MAGNETIC_FIELD.toString(),
            Sensor.TYPE_PRESSURE.toString(),
            Sensor.TYPE_LIGHT.toString(),
            Sensor.TYPE_PROXIMITY.toString(),
            Sensor.TYPE_POSE_6DOF.toString(),
            Sensor.TYPE_STATIONARY_DETECT.toString(),
            Sensor.TYPE_MOTION_DETECT.toString(),
            Sensor.TYPE_HEART_BEAT.toString(),
            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()
        )
        requestedsensors.putStringArray("reqeustedsensors", arraysensors)

    }

 


}