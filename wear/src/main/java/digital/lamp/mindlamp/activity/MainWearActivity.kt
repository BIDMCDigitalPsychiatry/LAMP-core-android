package digital.lamp.mindlamp.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.Wearable
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.thread.SendToDataLayerThread

@Suppress("DEPRECATION")
class MainWearActivity : WearableActivity(), SensorEventListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    var googleApiClient: GoogleApiClient? = null
    var isconnected: Boolean = false
    var valuesMap: DataMap = DataMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)

        // Enables Always-on
        setAmbientEnabled()
        mSensorManager =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager?


        // Build a new GoogleApiClient that includes the Wearable API
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver: MessageReceiver = MessageReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)


    }

    override fun onResume() {
        super.onResume()
        googleApiClient?.connect()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        googleApiClient!!.disconnect()
        valuesMap.clear()
        isconnected = false
        super.onDestroy()
    }

    override fun onConnected(p0: Bundle?) {
        isconnected = true

    }

    override fun onConnectionSuspended(p0: Int) {

        Log.d("SENSOR", "Accuracy changed ")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("SENSOR", "Accuracy changed ")
        isconnected = false
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometerValues(event)
        } else if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            getGyroscopeVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            detectMagnetometerVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
            detectPressureSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            detectLightSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
            detectHumiditySensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            detectTemperatureSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            detectProximitySensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
            detectGravitySensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            detectLinearAccelSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            detectStepDetectorSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            detectStepCounterSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            detectHeartRateSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_POSE_6DOF) {
            detectPose6DofSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_STATIONARY_DETECT) {
            detectStationaryDetectSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_MOTION_DETECT) {
            detectMotionDetectSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_HEART_BEAT) {
            detectHeartBeatSensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT) {
            detectLowLatencyOffBodySensorVals(event)
        } else if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            detectRotationVectorSensorVals(event)
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        Log.d("SENSOR", "Accuracy changed ")
    }

    private fun detectMagnetometerVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Magnetometer Vals " + event.values[0] + " " + event.values[1] + " " + event.values[2]
        )

        if (null != event.values) {
            var magnetovals = Array<String>(3, { "0" })
            magnetovals[0] = event.values[0].toString()
            magnetovals[1] = event.values[1].toString()
            magnetovals[2] = event.values[2].toString()

            valuesMap.putStringArray(Sensor.TYPE_MAGNETIC_FIELD.toString(), magnetovals)

        }


    }

    private fun detectGravitySensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Gravity Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var gravityvals = Array<String>(1, { "0" })
            gravityvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_GRAVITY.toString(), gravityvals)

        }

    }

    private fun detectLinearAccelSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Linear Accel Sensor Vals " + event.values[0]
        )

        if (null != event.values) {
            var linearvals = Array<String>(1, { "0" })
            linearvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_LINEAR_ACCELERATION.toString(), linearvals)

        }

    }

    private fun detectStepDetectorSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Step Detector Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var stepdetectorvals = Array<String>(1, { "0" })
            stepdetectorvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_STEP_DETECTOR.toString(), stepdetectorvals)

        }

    }

    private fun detectStepCounterSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Step Counter Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var stepcountervals = Array<String>(1, { "0" })
            stepcountervals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_STEP_COUNTER.toString(), stepcountervals)

        }

    }

    private fun detectHeartRateSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Heart Rate Sensor Vals " + event.values[0]
        )

        if (null != event.values) {
            var heartratevals = Array<String>(1, { "0" })
            heartratevals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_HEART_RATE.toString(), heartratevals)

        }

    }

    private fun detectPose6DofSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Pose6Dof Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var posvals = Array<String>(1, { "0" })
            posvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_POSE_6DOF.toString(), posvals)

        }

    }

    private fun detectStationaryDetectSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Stationary Detect Sensor Vals " + event.values[0]
        )

        if (null != event.values) {
            var stationaryvals = Array<String>(1, { "0" })
            stationaryvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_STATIONARY_DETECT.toString(), stationaryvals)

        }

    }

    private fun detectMotionDetectSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Motion Detect Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var motionvals = Array<String>(1, { "0" })
            motionvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_MOTION_DETECT.toString(), motionvals)

        }

    }

    private fun detectHeartBeatSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Heart Beat Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var heartBeatvals = Array<String>(1, { "0" })
            heartBeatvals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_HEART_BEAT.toString(), heartBeatvals)

        }

    }

    private fun detectLowLatencyOffBodySensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Low Latency Off Body Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var lowlatencyvals = Array<String>(1, { "0" })
            lowlatencyvals[0] = event.values[0].toString()

            valuesMap.putStringArray(
                Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString(),
                lowlatencyvals
            )

        }

    }

    private fun detectPressureSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Pressure Sensor Vals " + event.values[0]
        )

        if (null != event.values) {
            var pressureVals = Array<String>(1, { "0" })
            pressureVals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_PRESSURE.toString(), pressureVals)

        }

    }

    private fun detectRotationVectorSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Rotation Vector Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var rotationvectorVals = Array<String>(1, { "0" })
            rotationvectorVals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_ROTATION_VECTOR.toString(), rotationvectorVals)

        }

    }

    private fun detectLightSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Light Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var lightVals = Array<String>(1, { "0" })
            lightVals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_LIGHT.toString(), lightVals)

        }


    }

    private fun detectHumiditySensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Humidity Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var humidityVals = Array<String>(1, { "0" })
            humidityVals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_RELATIVE_HUMIDITY.toString(), humidityVals)

        }

    }

    private fun detectTemperatureSensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Temperature Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var tempVals = Array<String>(1, { "0" })
            tempVals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_AMBIENT_TEMPERATURE.toString(), tempVals)

        }

    }

    private fun detectProximitySensorVals(event: SensorEvent) {

        Log.d(
            "SENSOR",
            "Proximity Sensor Vals " + event.values[0]
        )
        if (null != event.values) {
            var proximityVals = Array<String>(1, { "0" })
            proximityVals[0] = event.values[0].toString()

            valuesMap.putStringArray(Sensor.TYPE_PROXIMITY.toString(), proximityVals)

        }

    }

    private fun getAccelerometerValues(event: SensorEvent) {

        if (null != event.values) {
            var acceleroVals = Array<String>(3, { "0" })
            acceleroVals[0] = event.values[0].toString()
            acceleroVals[1] = event.values[1].toString()
            acceleroVals[2] = event.values[2].toString()

            valuesMap.putStringArray(Sensor.TYPE_ACCELEROMETER.toString(), acceleroVals)

        }
        Log.d(
            "SENSOR",
            "Accelerometer Vals " + event.values[0] + " " + event.values[1] + " " + event.values[2]
        )
    }

    private fun getGyroscopeVals(event: SensorEvent) {

        if (null != event.values) {
            var gyroscopeVals = Array<String>(3, { "0" })
            gyroscopeVals[0] = event.values[0].toString()
            gyroscopeVals[1] = event.values[1].toString()
            gyroscopeVals[2] = event.values[2].toString()

            valuesMap.putStringArray(Sensor.TYPE_GYROSCOPE.toString(), gyroscopeVals)

        }
        Log.d(
            "SENSOR",
            "Gyroscope Vals " + event.values[0] + " " + event.values[1] + " " + event.values[2]
        )
    }

    inner class MessageReceiver : BroadcastReceiver() {

        var INTERVAL: Long = 3000 //millisecs
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            var vals: Array<String> = intent.getStringArrayExtra("values")
            Log.v("myTag", "Main activity received message: $vals")

            // Display message in UI

            for (item in vals) {

                mSensor = mSensorManager!!.getDefaultSensor(item.toInt())
                var b = mSensorManager?.registerListener(
                    this@MainWearActivity,
                    mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )

//                Log.v("myTag", "Main activity received message: $b")
                if (false == b) {
                    Log.d(
                        "SENSOR",
                        "Failed Sensor " + item.toInt()
                    )
                    var arr: Array<String?> = arrayOfNulls(3)
                    valuesMap.putStringArray(item, arr)
                }

            }

            sendValues(/*vals*/)

        }

        fun unregisterSensors(/*sensorslist: Array<String>*/) {

            mSensorManager?.unregisterListener(
                this@MainWearActivity
            )

        }

        fun sendValues(/*sensorslist: Array<String>*/) {


            Handler().postDelayed({

                if (isconnected) {

                    SendToDataLayerThread(
                        "/updatedValues",
                        "",
                        valuesMap,
                        googleApiClient
                    ).run()
                    unregisterSensors(/*sensorslist*/)

                }
            }, INTERVAL)


        }
    }


}
