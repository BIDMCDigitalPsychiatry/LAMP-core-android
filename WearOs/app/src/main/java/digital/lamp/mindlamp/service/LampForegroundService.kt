package digital.lamp.mindlamp.service


import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.TrafficStats
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorAPI
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.SensorSpec
import digital.lamp.mindlamp.app.App
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.sensor.AccelerometerData
import digital.lamp.mindlamp.sensor.GravityData
import digital.lamp.mindlamp.sensor.MagnetometerData
import digital.lamp.mindlamp.sensor.RotationData
import digital.lamp.mindlamp.sensor.ScreenStateData
import digital.lamp.mindlamp.sensor.SensorListener
import digital.lamp.mindlamp.sensor.TelephonySensorData
import digital.lamp.mindlamp.sensor.WifiData
import digital.lamp.mindlamp.sensor.data.PreferenceDatastore
import digital.lamp.mindlamp.sensor.data.SensorSpecs
import digital.lamp.mindlamp.sensor.health_services.HealthServicesManager
import digital.lamp.mindlamp.sensor.health_services.SensorStore
import digital.lamp.mindlamp.sensor.health_services.utils.SensorDataUtils
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
@AndroidEntryPoint
class LampForegroundService : Service(),
    SensorListener {

    @Inject
    lateinit var preferenceDataStore: PreferenceDatastore
    private lateinit var workManager: WorkManager

    companion object {
        private val TAG = LampForegroundService::class.java.simpleName
        private const val TIME_INTERVAL: Long = 3000
        private const val MILLISEC_FUTURE: Long = 60000
        private const val MINIMUM_DATA_COLLECTION_INTERVAL: Long = 30000
    }

    private var isAlarm: Boolean = false
    private var isActivitySchedule = false
    private var localNotificationId = 0


    private lateinit var oScope: CoroutineScope
    private lateinit var oGson: Gson
    var sensorSpecList = arrayListOf<SensorSpecs>()
    private var lastTimestampAccelerometer: Long = 0
    private var lastTimestampMagnetometer: Long = 0
    private var lastTimestampRotation: Long = 0
    private var lastTimestampGravity: Long = 0
    private var lastTimestampGyroscope: Long = 0
    private var lastTimestampLocation: Long = 0
    private var lastTimestampWifi: Long = 0
    private var lastTimestampScreenState: Long = 0
    private var lastTimestampActivity: Long = 0
    private var lastTimestampTelephony: Long = 0
      private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    private var locationRequest: LocationRequest? = null
    override fun onCreate() {
        super.onCreate()

        workManager = WorkManager.getInstance(App.app)

        oScope = CoroutineScope(Dispatchers.IO)
        oGson = Gson()
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        isAlarm = intent?.extras?.getBoolean("set_alarm") ?: false
        isActivitySchedule = intent?.extras?.getBoolean("set_activity_schedule") ?: false
        localNotificationId = intent?.extras?.getInt("notification_id") ?: 0
        if (!isAlarm && !isActivitySchedule && localNotificationId == 0) {
            val notification =
                LampNotificationManager.showNotification(
                    this,
                    getString(digital.lamp.mindlamp.R.string.app_name)
                )

            startForeground(1010, notification)


            invokeSensorSpecData()
            collectSensorData()
            //setPeriodicSyncWorker()
             //fetch Locations
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build()


            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                if(!AppState.session.isLoggedIn) return
                    for (location in locationResult.locations){
                        LampLog.d(TAG,"Location update:onLocationResult:latitude "+location.latitude)
                        GlobalScope.launch(Dispatchers.IO) {
                            kotlinx.coroutines.internal.synchronized(SensorStore)
                            {

                                SensorStore.storeValue(
                                    SensorDataUtils.getLocationSensorData(
                                        location
                                    )
                                )
                            }
                        }
                    }
                }
            }


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                    }
            fusedLocationClient!!.requestLocationUpdates(
                locationRequest!!,
            locationCallback,
            getMainLooper())

        }

        return START_STICKY
    }


    /* private fun setPeriodicSyncWorker() {
         workManager.cancelAllWorkByTag(SYNC_WORK_MANAGER_TAG)
         val periodicWork =
             PeriodicWorkRequestBuilder<PeriodicDataSyncWorker>(
                 15 * 60 * 1000L, TimeUnit.MILLISECONDS
             )
                 .addTag(SYNC_WORK_MANAGER_TAG)
                 .build()

         WorkManager.getInstance(this)
             .enqueueUniquePeriodicWork(
                 SYNC_DATA_WORK_NAME,
                 ExistingPeriodicWorkPolicy.KEEP,
                 periodicWork
             )
     }*/

    private fun invokeSensorSpecData() {

        if (NetworkUtils.isNetworkAvailable(this)) {
            if (NetworkUtils.getBatteryPercentage(this) > 15) {
                sensorSpecList = arrayListOf()
                val basic = "Basic ${
                    AppState.session.userId.trim()
                }"

                GlobalScope.launch(Dispatchers.IO) {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
                    try {
                        val state = SensorAPI(AppState.session.urlvalue).sensorAll(
                            AppState.session.userId,
                            basic
                        )
                        val oSensorSpec: SensorSpec? = Gson().fromJson(
                            state.toString(),
                            SensorSpec::class.java
                        )
                        LampLog.d("Sensor Spec" + "Sensor spec=" + state)
                        if (oSensorSpec?.data?.isNotEmpty() == true) {
                            // AppState.session.isCellularUploadAllowed =
                            //   oSensorSpec.data.find { it.settings == null || it.settings?.cellular_upload == null || it.settings?.cellular_upload == true } != null
                        }
                        oSensorSpec?.data?.forEach { sensor ->
                            val sensorSpecs = SensorSpecs(
                                null,
                                sensor.id,
                                sensor.spec,
                                sensor.name,
                                sensor.settings?.frequency,
                                sensor.settings?.cellular_upload
                            )
                            sensorSpecList.add(sensorSpecs)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }
    }

    private fun collectSensorData() {
        DebugLogs.writeToFile("collectSensorData")


        LampLog.e(TAG, sensorSpecList.size.toString())
        var count = 0
        val timer = object : CountDownTimer(MILLISEC_FUTURE, TIME_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                if (!AppState.session.isLoggedIn) {
                    try {
                        cancel()
                    }
                    catch (e:Exception){
                        LampLog.e(TAG,"Exception in timer cancel"+e.message,e)
                    }
                }
                count++
                Log.d("Timer", "collectSensorData" + count)
                when (count) {
                    1 -> {
                    }

                    2 -> {
                        try {
                            var accelerometerDataRequired = true
                            var sensorSpec = ""
                            var frequency: Double? = 0.1

                            sensorSpecList.forEach {
                                if (it.spec == Sensors.ACCELEROMETER.sensor_name ||
                                    it.spec == Sensors.DEVICE_MOTION.sensor_name
                                ) {
                                    accelerometerDataRequired = true
                                    if (accelerometerDataRequired)
                                        sensorSpec += it.spec!!
                                    else
                                        sensorSpec = it.spec!!
                                    it.frequency?.let {
                                        if (it != 0.0 && it <= 5)
                                            frequency = it
                                    }
                                }
                            }
                            //Invoke Accelerometer Call
                            if (accelerometerDataRequired) {
                                AccelerometerData(
                                    this@LampForegroundService,
                                    applicationContext,
                                    frequency, sensorSpec
                                )
                            }

                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)
                        }
                    }

                    3 -> {
                        try {
                            var rotationDataRequird = true
                            var frequency: Double? = 0.1

                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    rotationDataRequird = true
                                    it.frequency?.let {
                                        if (it != 0.0 && it <= 5)
                                            frequency = it
                                    }
                                } //Invoke Rotation Call
                            }

                            if (rotationDataRequird) {
                                RotationData(
                                    this@LampForegroundService,
                                    applicationContext, frequency
                                )
                            }

                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)
                        }
                    }

                    4 -> {
                        try {
                            var magnetometerDataRequired = true
                            var frequency: Double? = 0.1
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    magnetometerDataRequired = true
                                    it.frequency?.let {
                                        if (it != 0.0 && it <= 5)
                                            frequency = it
                                    }
                                }
                            }
                            if (magnetometerDataRequired) {
                                //Invoke Magnet Call
                                MagnetometerData(
                                    this@LampForegroundService,
                                    applicationContext, frequency
                                )
                            }

                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)

                        }
                    }

                    5 -> {
                        try {
                            var gravityDataRequired = true

                            var frequency: Double? = 0.1
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    gravityDataRequired = true

                                    it.frequency?.let {
                                        if (it != 0.0 && it <= 5)
                                            frequency = it
                                    }
                                }//Invoke Gyroscope Call
                            }
                            if (gravityDataRequired) {
                                GravityData(
                                    this@LampForegroundService,
                                    applicationContext, frequency
                                )
                            }
                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)
                        }
                    }

                    6 -> {
                        /* var locationDateRequired = true
                         var frequency: Double? = 0.1

                         sensorSpecList.forEach {
                             if (it.spec == Sensors.GPS.sensor_name) {
                                 locationDateRequired = true
                                 it.frequency?.let {
                                     if (it != 0.0 && it <= 1)
                                         frequency = it
                                 }
                             }
                         }
                         if (locationDateRequired) {
                             //Invoke Location
                             LocationData(
                                 this@LampForegroundService,
                                 applicationContext, frequency
                             )
                         } */

                    }

                    7 -> {
                        try {
                            var wifiDataRequired = true
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.NEARBY_DEVICES.sensor_name) {
                                    wifiDataRequired = true

                                }
                            }
                            if (wifiDataRequired) {
                                //Invoke WifiData
                                WifiData(
                                    this@LampForegroundService,
                                    applicationContext
                                )
                            }
                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)
                        }
                    }

                    8 -> {
                        try {
                            var screenStateDataRequired = true
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.SCREEN_STATE.sensor_name || it.spec == Sensors.DEVICE_STATE.sensor_name) {
                                    screenStateDataRequired = true

                                }
                            }
                            if (screenStateDataRequired) {
                                //Invoke screen state Data
                                ScreenStateData(
                                    this@LampForegroundService,
                                    applicationContext
                                )
                            }
                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)
                        }
                    }

                    9 -> {

                    }
                    /*ActivityTransitionData(
                    this@LampForegroundService,
                    applicationContext,
                    sensorSpecList
                )*/

                    10 -> {
                        try {
                            var telephonyDataRequired = true
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.TELEPHONY.sensor_name) {
                                    telephonyDataRequired = true

                                }
                            }
                            if (telephonyDataRequired) {
                                //Invoke screen state Data
                                TelephonySensorData(
                                    this@LampForegroundService,
                                    applicationContext
                                )
                            }

                        } catch (e: Exception) {
                            LampLog.e("LampWatch", e.message, e)
                        }
                    }
                }
            }

            override fun onFinish() {

            }
        }
        timer.start()


    }


    override fun onDestroy() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        catch (e:Exception){
            LampLog.e(TAG,"Exception in location update remove"+e.message,e)
        }
        super.onDestroy()

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //Method to perform the Sensor Spec or custom sensor data,
    private fun invokeSensorSpecData(initialCall: Boolean = false) {
        DebugLogs.writeToFile("Call sensor spec")

        if (NetworkUtils.isNetworkAvailable(this)) {
            if (NetworkUtils.getBatteryPercentage(this@LampForegroundService) > 15) {
                sensorSpecList = arrayListOf()
                val basic = "Basic ${
                    Utils.toBase64(
                        AppState.session.token + ":" + AppState.session.urlvalue.removePrefix(
                            "https://"
                        ).removePrefix("http://")
                    )
                }"

                GlobalScope.launch(Dispatchers.IO) {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---
                    try {

                        val state = SensorAPI(AppState.session.urlvalue).sensorAll(
                            AppState.session.userId,
                            basic
                        )
                        val oSensorSpec: SensorSpec? = Gson().fromJson(
                            state.toString(),
                            SensorSpec::class.java
                        )
                        if (oSensorSpec?.data?.isNotEmpty() == true) {
                            //  AppState.session.isCellularUploadAllowed =
                            //     oSensorSpec.data.find { it.settings == null || it.settings?.cellular_upload == null || it.settings?.cellular_upload == true } != null
                        }
                        oSensorSpec?.data?.forEach { sensor ->
                            val sensorSpecs = SensorSpecs(
                                null,
                                sensor.id,
                                sensor.spec,
                                sensor.name,
                                sensor.settings?.frequency,
                                sensor.settings?.cellular_upload
                            )
                            sensorSpecList.add(sensorSpecs)
                        }

                        GlobalScope.launch(Dispatchers.Main) {
                            if (initialCall)
                                collectSensorData()
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {

                Log.e("Lamp", "Error")
            }
        }

    }


    @OptIn(InternalCoroutinesApi::class)
    override fun getAccelerometerData(sensorData: SensorEvent) {
        //   Log.d("Watch", "Sensor data received: getAccelerometerData ()" )

        if (lastTimestampAccelerometer != 0L && System.currentTimeMillis() - lastTimestampAccelerometer < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampAccelerometer = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }


        // Log.d("NewWatch","getAccelerometerData  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getRotationData(sensorData: SensorEvent) {
        //Log.d("Watch", "Sensor data received: getRotationData ()" )
        if (lastTimestampRotation != 0L && System.currentTimeMillis() - lastTimestampRotation < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampRotation = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }


        // Log.d("NewWatch","getRotationData  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getMagneticData(sensorData: SensorEvent) {
        //  Log.d("Watch", "Sensor data received: getMagneticData ()" )
        if (lastTimestampMagnetometer != 0L && System.currentTimeMillis() - lastTimestampMagnetometer < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampMagnetometer = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        //   Log.d("NewWatch","getMagneticData  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getGyroscopeData(sensorData: SensorEvent) {
        //Log.d("Watch", "Sensor data received: getGyroscopeData ()" )
        if (lastTimestampGyroscope != 0L && System.currentTimeMillis() - lastTimestampGyroscope < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampGyroscope = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        // Log.d("NewWatch","getGyroscopeData  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getLocationData(sensorData: SensorEvent) {
        //Log.d("Watch", "Sensor data received: getLocationData ()" )
        if (lastTimestampLocation != 0L && System.currentTimeMillis() - lastTimestampLocation < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampLocation = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        // Log.d("NewWatch","getLocationData  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getWifiData(sensorData: SensorEvent) {
        // Log.d("Watch", "Sensor data received: getWifiData ()" )
        if (lastTimestampWifi != 0L && System.currentTimeMillis() - lastTimestampWifi < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampWifi = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        // Log.d("NewWatch","getwifi  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getScreenState(sensorData: SensorEvent) {
        //  Log.d("Watch", "Sensor data received: getScreenState ()" )
        if (lastTimestampScreenState != 0L && System.currentTimeMillis() - lastTimestampScreenState < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampScreenState = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        //Log.d("NewWatch","getScreenState  Stored to file")
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getActivityData(sensorData: SensorEvent) {
        /// Log.d("Watch", "Sensor data received: getActivityData ()" )

        if (lastTimestampActivity != 0L && System.currentTimeMillis() - lastTimestampActivity < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampActivity = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        //  Log.d("NewWatch","getActivityData  Stored to file")

    }

    override fun getGoogleFitData(sensorEventData: ArrayList<SensorEvent>) {
        //Log.d("Watch", "Sensor data received: getGoogleFitData ()" )
        //No more implemented
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getTelephonyData(sensorData: SensorEvent) {
        if (lastTimestampTelephony != 0L && System.currentTimeMillis() - lastTimestampTelephony < MINIMUM_DATA_COLLECTION_INTERVAL)
            return
        lastTimestampTelephony = System.currentTimeMillis()

        val sensorEventData = SensorDataUtils.getSensorEventData(sensorData)
        kotlinx.coroutines.internal.synchronized(SensorStore)
        {
            SensorStore.storeValue(sensorEventData)
        }

        //  Log.d("NewWatch","getTelephonyData  Stored to file")
    }

}



