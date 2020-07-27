package digital.lamp.mindlamp.standalone.activity

import android.content.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.Wearable
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import digital.lamp.mindlamp.standalone.R
import digital.lamp.mindlamp.standalone.appstate.AppState
import digital.lamp.mindlamp.standalone.broadcastreceiver.SensorBroadcastReceiver
import digital.lamp.mindlamp.standalone.model.*
import digital.lamp.mindlamp.standalone.utils.NetworkUtils
import digital.lamp.mindlamp.standalone.viewmodels.DataViewModel
import digital.lamp.mindlamp.standalone.web.WebConstant
import digital.lamp.mindlamp.standalone.web.WebServiceResponseData
import digital.lamp.mindlamp.standalone.web.pojo.response.ResponseBase

@Suppress("DEPRECATION")
class MainWearActivity : FragmentActivity(), SensorEventListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    var googleApiClient: GoogleApiClient? = null
    var isconnected: Boolean = false
    var valuesMap: DataMap = DataMap()
    private val LOG_TAG: String = "NETWORK"
    private var toast: Toast? = null;
    private var sensorEventDataList: ArrayList<SensorEventData> = arrayListOf()
    private var arraySensors: ArrayList<Int> = arrayListOf()
    private var dataViewModel: DataViewModel? = null
    val br: BroadcastReceiver = SensorBroadcastReceiver()
    val messageReceiver: MessageReceiver = MessageReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)

        initialize()
        // Enables Always-on
//        setAmbientEnabled()

        try {
            unregisterReceiver(br)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        registerReceiver(
            br,
            IntentFilter("com.from.notification")
        );


        mSensorManager =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager?

        // Build a new GoogleApiClient that includes the Wearable API
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)

        dataViewModel = ViewModelProviders.of(this@MainWearActivity).get(DataViewModel::class.java)

        setObserver()

        FirebaseApp.initializeApp(this);

        if (NetworkUtils.isNetworkAvailable(this)) {
            //only need to update once for first time
            if (!AppState.session.isLoggedIn)
                retrieveUpdateCurrentToken(WebConstant.UN)
            //will update from here else the token updation to server wont be done
            AppState.session.isLoggedIn = true
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_LONG).show()
        }
    }

    fun initialize() {

        arraySensors.add(Sensor.TYPE_ACCELEROMETER)
        arraySensors.add(Sensor.TYPE_GYROSCOPE)
        arraySensors.add(Sensor.TYPE_GRAVITY)
        arraySensors.add(Sensor.TYPE_LINEAR_ACCELERATION)
        arraySensors.add(Sensor.TYPE_ROTATION_VECTOR)
        arraySensors.add(Sensor.TYPE_RELATIVE_HUMIDITY)
        arraySensors.add(Sensor.TYPE_AMBIENT_TEMPERATURE)
        arraySensors.add(Sensor.TYPE_STEP_DETECTOR)
        arraySensors.add(Sensor.TYPE_STEP_COUNTER)
        arraySensors.add(Sensor.TYPE_HEART_RATE)
        arraySensors.add(Sensor.TYPE_MAGNETIC_FIELD)
        arraySensors.add(Sensor.TYPE_PRESSURE)
        arraySensors.add(Sensor.TYPE_LIGHT)
        arraySensors.add(Sensor.TYPE_PROXIMITY)
        arraySensors.add(Sensor.TYPE_POSE_6DOF)
        arraySensors.add(Sensor.TYPE_STATIONARY_DETECT)
        arraySensors.add(Sensor.TYPE_MOTION_DETECT)
        arraySensors.add(Sensor.TYPE_HEART_BEAT)
        arraySensors.add(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)
    }

    fun setObserver() {
        dataViewModel?.webServiceResponseLiveData?.observe(
            this@MainWearActivity,
            object : Observer<WebServiceResponseData> {
                override fun onChanged(t: WebServiceResponseData?) {

                    Log.d("ACTIVITY", "on Changed")
                    if (null != t?.responseBase) {

                        if (t.responseCode == WebConstant.CODE_SUCCESS) {
                        } else {
                            toast = Toast.makeText(
                                this@MainWearActivity,
                                t?.responseMessage,
                                Toast.LENGTH_SHORT
                            )
                            toast?.show()
                        }
                    } else {

                        toast = Toast.makeText(
                            this@MainWearActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT
                        )
                        toast?.show()
                    }
                }
            })

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
        unregisterReceiver(br)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        valuesMap.clear()
        isconnected = false
        super.onDestroy()
    }

    override fun onConnected(p0: Bundle?) {
        isconnected = true

    }

    fun registerAllSensors() {

//        var vals: Array<String> = intent.getStringArrayExtra("values")
//        Log.v("myTag", "Main activity received message: $vals")

        // Display message in UI

//        for (item in vals) {

        mSensor = mSensorManager!!.getDefaultSensor(1)
        var b = mSensorManager?.registerListener(
            this@MainWearActivity,
            mSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

//                Log.v("myTag", "Main activity received message: $b")
        if (false == b) {
            Log.d(
                "SENSOR",
                "Failed Sensor " + 1
            )
            var arr: Array<String?> = arrayOfNulls(3)
            valuesMap.putStringArray("1", arr)
        }

//        }

        Handler().postDelayed({
            // populateValues(CustomMap(valuesMap))

            //  sensorviewmodel?.addSensoreEvent(11, sensorEventDataList)
        }, 3000)


//            sendValues(/*vals*/)


    }

    override fun onConnectionSuspended(p0: Int) {

        Log.d("SENSOR", "Accuracy changed ")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("SENSOR", "Accuracy changed ")
        isconnected = false
    }


    private fun retrieveUpdateCurrentToken(un: String) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            // Get new Instance ID token
            val token = it.result?.token
            Log.e("FCM", "FCM Token : $token")
            val tokenData = TokenData("login", token.toString(), "Android", UserAgent())
            val sendTokenRequest =
                SendTokenRequest(
                    tokenData,
                    "lamp.watch.standalone.analytics",
                    System.currentTimeMillis()
                )

            dataViewModel?.addDeviceToken(un, sendTokenRequest)

        }
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

    /* fun requestedSensorArray() {

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
     }
 */
    fun populateValues(vals: CustomMap) {

        if (((vals.dataMap?.get(Sensor.TYPE_ACCELEROMETER.toString()) as Array<String>).size) > 0 &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_ACCELEROMETER.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_ACCELEROMETER.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.accelerometer", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)

        }

        if ((vals.dataMap?.getStringArray(Sensor.TYPE_GYROSCOPE.toString())) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_GYROSCOPE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_GYROSCOPE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.gyroscope", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)

        }
        if (vals.dataMap?.getStringArray(Sensor.TYPE_GRAVITY.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_GRAVITY.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_GRAVITY.toString()) as Array<String>
            val gravityData =
                GravityData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    null,
                    null,
                    gravityData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.gravity", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_LINEAR_ACCELERATION.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_LINEAR_ACCELERATION.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_LINEAR_ACCELERATION.toString()) as Array<String>

            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.linear", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_ROTATION_VECTOR.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_ROTATION_VECTOR.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_ROTATION_VECTOR.toString()) as Array<String>
            val rotationData =
                RotationData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    rotationData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.rotationvector", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_RELATIVE_HUMIDITY.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_RELATIVE_HUMIDITY.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_RELATIVE_HUMIDITY.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.relativehumidity", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_AMBIENT_TEMPERATURE.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_AMBIENT_TEMPERATURE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_AMBIENT_TEMPERATURE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.ambiettemperature", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_DETECTOR.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_DETECTOR.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_STEP_DETECTOR.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.stepdetector", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_COUNTER.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_COUNTER.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_STEP_COUNTER.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.stepcounter", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_RATE.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_RATE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_HEART_RATE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.heartrate", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_MAGNETIC_FIELD.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_MAGNETIC_FIELD.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_MAGNETIC_FIELD.toString()) as Array<String>
            val magnetometerData =
                MagnetData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    magnetometerData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.magneticfield", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_PRESSURE.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_PRESSURE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_PRESSURE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.pressure", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_LIGHT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_LIGHT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_LIGHT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.light", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_PROXIMITY.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_PROXIMITY.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_PROXIMITY.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.proximity", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_POSE_6DOF.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_POSE_6DOF.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_POSE_6DOF.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.pose", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_STATIONARY_DETECT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_STATIONARY_DETECT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_STATIONARY_DETECT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.stationary", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_MOTION_DETECT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_MOTION_DETECT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_MOTION_DETECT.toString()) as Array<String>
            val motionData =
                MotionData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())

            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    null,
                    motionData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.motion", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_BEAT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_BEAT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_HEART_BEAT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.heartbeat", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.latency", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }


    }

    inner class MessageReceiver : BroadcastReceiver() {

        var INTERVAL: Long = 3000 //millisecs
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            Log.v("myTag", "Main activity received message: $arraySensors")

            // Display message in UI

            for (item in arraySensors) {

                mSensor = mSensorManager!!.getDefaultSensor(item)
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
//                    valuesMap.putStringArray(item, arr)
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

                    populateValues(CustomMap(valuesMap))

                    if (NetworkUtils.isNetworkAvailable(this@MainWearActivity)) {
                        dataViewModel!!.addSensoreEvent(WebConstant.UN, sensorEventDataList)
                    } else {

                        Toast.makeText(
                            this@MainWearActivity,
                            getString(R.string.internet_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    unregisterSensors(/*sensorslist*/)

                }
            }, INTERVAL)


        }
    }


}
