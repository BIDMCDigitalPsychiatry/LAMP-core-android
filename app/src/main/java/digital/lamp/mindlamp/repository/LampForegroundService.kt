package digital.lamp.mindlamp.repository

import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.TrafficStats
import android.os.*
import androidx.work.*
import androidx.work.PeriodicWorkRequest.Companion.MIN_PERIODIC_INTERVAL_MILLIS
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import digital.lamp.lamp_kotlin.lamp_core.apis.ActivityAPI
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorAPI
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ClientException
import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ServerException
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.AlarmBroadCastReceiver
import digital.lamp.mindlamp.ExceptionActivity
import digital.lamp.mindlamp.app.App
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.*
import digital.lamp.mindlamp.database.dao.ActivityDao
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.dao.SensorDao
import digital.lamp.mindlamp.database.entity.ActivitySchedule
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.sensor.*
import digital.lamp.mindlamp.sensor.GravityData
import digital.lamp.mindlamp.sensor.RotationData
import digital.lamp.mindlamp.sensor.healthconnect.GoogleHealthConnect
import digital.lamp.mindlamp.sheduleing.*
import digital.lamp.mindlamp.sheduleing.ScheduleConstants.SYNC_DATA_WORK_NAME
import digital.lamp.mindlamp.sheduleing.ScheduleConstants.SYNC_WORK_MANAGER_TAG
import digital.lamp.mindlamp.sheduleing.ScheduleConstants.WORK_MANAGER_TAG
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.AppConstants.ALARM_INTERVAL
import digital.lamp.mindlamp.utils.AppConstants.SYNC_SENSOR_SPEC_INTERVAL
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException
import kotlin.math.max
import kotlin.math.min


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class LampForegroundService : Service(),
    SensorListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var workManager: WorkManager

    companion object {
        private val TAG = LampForegroundService::class.java.simpleName
        private const val TIME_INTERVAL: Long = 3000
        private const val MILLISEC_FUTURE: Long = 60000
    }

    private var isAlarm: Boolean = false
    private var isActivitySchedule = false
    private var localNotificationId = 0
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var oAnalyticsDao: AnalyticsDao
    private lateinit var oActivityDao: ActivityDao
    private lateinit var oSensorDao: SensorDao
    private lateinit var oScope: CoroutineScope
    private lateinit var oGson: Gson


    // Called when the service is created.
    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics = Firebase.analytics
        workManager = WorkManager.getInstance(App.app)

        oAnalyticsDao = AppDatabase.getInstance(this).analyticsDao()
        oSensorDao = AppDatabase.getInstance(this).sensorDao()
        oActivityDao = AppDatabase.getInstance(this).activityDao()
        oScope = CoroutineScope(Dispatchers.IO)
        oGson = Gson()
    }

    /**
     *    Called when the service is started.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        isAlarm = intent?.extras?.getBoolean("set_alarm") ?: false
        isActivitySchedule = intent?.extras?.getBoolean("set_activity_schedule") ?: false
        localNotificationId = intent?.extras?.getInt("notification_id") ?: 0
        if (!isAlarm && !isActivitySchedule && localNotificationId == 0) {
            try {
                // Create and show the notification to make the service run in the foreground.
                val notification =
                    LampNotificationManager.showNotification(
                        this,
                        getString(digital.lamp.mindlamp.R.string.active_data_collection)
                    )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(1010, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                } else {
                    startForeground(1010, notification)
                }

                setAlarmManager()

                invokeSensorSpecData(true)
                invokeActivitySchedules()

                setAlarmManagerForEvery24Hours()
                setPeriodicSyncWorker()
            }catch (e:Exception){
                DebugLogs.writeToFile("foreground exception "+e.message)
            }

        } else if (!isAlarm && isActivitySchedule && localNotificationId == AppConstants.REPEAT_DAILY) {
            LampLog.e(TAG, "Call Activity Schedule for every 24 hours")
            invokeActivitySchedules()

        } else if (!isAlarm && isActivitySchedule && localNotificationId == AppConstants.REPEAT_HOURLY) {
            LampLog.e(TAG, "Call sensor spec every 1 hours")
            DebugLogs.writeToFile("Call sensor spec every 1 hours")
            invokeSensorSpecData()

        } else if (!isAlarm && isActivitySchedule && localNotificationId != 0) {
            LampLog.e(TAG, "Call for showing up the local notification")
            LampLog.e("BROADCASTRECEIVER", "invokeLocalNotification ")
            invokeLocalNotification(localNotificationId)
        } else {
            //This will execute every 10 min if logged in
            LampLog.e("Sensor : Trigger")
            syncAnalyticsData()
            //Fetch google fit data in 5 min interval
            Lamp.stopLAMP(this)
            collectSensorData()
        }
        // If the system kills the service, it will be restarted.
        return START_STICKY
    }


    /**
     * Send analytics data from db to server.
     */
    private fun syncAnalyticsData() {
        val sensorEventDataList: ArrayList<SensorEvent> = arrayListOf<SensorEvent>()
        sensorEventDataList.clear()

        val googleHealthConnectSensorEventDataList: ArrayList<SensorEvent> = arrayListOf<SensorEvent>()
        googleHealthConnectSensorEventDataList.clear()

        val gson = GsonBuilder()
            .create()
        val gsonWithNull = GsonBuilder().serializeNulls()
            .create()

        GlobalScope.launch(Dispatchers.IO) {
            val list: List<Analytics>
            LampLog.e("Sensor : START TIME ${AppState.session.lastAnalyticsTimestamp}")
            if (AppState.session.lastAnalyticsTimestamp == 1L) {
                val analytics =
                    oAnalyticsDao.getFirstAnalyticsRecord(AppState.session.lastAnalyticsTimestamp)
                AppState.session.lastAnalyticsTimestamp = analytics?.datetimeMillisecond ?: 1L
            }
            LampLog.e("Sensor : START TIME ${AppState.session.lastAnalyticsTimestamp}")
            val endTime =
                AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL
            LampLog.e("Sensor : END TIME $endTime")
            list = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp, endTime)

            list.forEach {
                val sensorEvent = gson.fromJson(
                    it.analyticsData,
                    SensorEvent::class.java
                )

                sensorEvent?.sensor?.let {sensor->
                    if (sensorEvent.sensor == Sensors.SLEEP.sensor_name || sensorEvent.sensor == Sensors.NUTRITION.sensor_name ||
                        sensorEvent.sensor == Sensors.STEPS.sensor_name || sensorEvent.sensor == Sensors.HEART_RATE.sensor_name ||
                        sensorEvent.sensor == Sensors.BLOOD_GLUCOSE.sensor_name || sensorEvent.sensor == Sensors.BLOOD_PRESSURE.sensor_name
                        || sensorEvent.sensor == Sensors.OXYGEN_SATURATION.sensor_name || sensorEvent.sensor == Sensors.BODY_TEMPERATURE.sensor_name
                    ) {
                        val googleHealthConnectData = gsonWithNull.fromJson(
                            it.analyticsData,
                            SensorEvent::class.java
                        )

                        googleHealthConnectSensorEventDataList.add(
                            googleHealthConnectData
                        )
                        LampLog.e("Google Fit sync: ${gsonWithNull.toJson(googleHealthConnectData)}")
                    } else {
                        sensorEventDataList.add(
                            sensorEvent
                        )
                    }
                }

            }
            list.let {
                if (it.isNotEmpty()) {
                    AppState.session.lastAnalyticsTimestamp = it[0].datetimeMillisecond!!
                    AppState.session.lastSyncWorkerTimestamp = it[0].datetimeMillisecond!!
                }
            }
            LampLog.e("DB : ${list.size} and Sensor : ${sensorEventDataList.size}")
            if (sensorEventDataList.isNotEmpty())
                invokeAddSensorData(sensorEventDataList, false)
            else {
                try {
                    val dbList = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                    val anayticsRowCount = oAnalyticsDao.getNumberOfRecordsToSync(AppState.session.lastAnalyticsTimestamp)
                    if (anayticsRowCount>0) {
                        AppState.session.lastAnalyticsTimestamp =
                            AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL
                        syncAnalyticsData()
                    }

                }catch (e:Exception){
                    DebugLogs.writeToFile("${e.message}")
                }
            }
            if (googleHealthConnectSensorEventDataList.isNotEmpty())
                invokeAddSensorData(googleHealthConnectSensorEventDataList, true)

        }
    }

    /**
     * Set alarm manager for a specific time
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManager() {
        // Get the AlarmManager system service.
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Create an intent for the AlarmBroadCastReceiver.
        alarmIntent = Intent(this, AlarmBroadCastReceiver::class.java).let { intent ->
            // Create a PendingIntent for the AlarmBroadCastReceiver.
            PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE)
        }
        // Set an inexact repeating alarm for a specific interval for the AlarmBroadCastReceiver.
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + ALARM_INTERVAL,
            ALARM_INTERVAL,
            alarmIntent
        )
        // Create an intent for the ActivityRepeatReceiver.
        val intent = Intent(this, ActivityRepeatReceiver::class.java)
        intent.putExtra("id", AppConstants.REPEAT_HOURLY)
        // Create a PendingIntent for the ActivityRepeatReceiver.
        val pendingIntent = PendingIntent.getBroadcast(this, 120, intent, FLAG_IMMUTABLE)
        // Set an inexact repeating alarm for a specific interval for the ActivityRepeatReceiver.
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SYNC_SENSOR_SPEC_INTERVAL,
            SYNC_SENSOR_SPEC_INTERVAL,
            pendingIntent
        )
    }

    /**
     * To start a workmanager to sync analytics data
     */
    private fun setPeriodicSyncWorker() {
        workManager.cancelAllWorkByTag(SYNC_WORK_MANAGER_TAG)
        val periodicWork =
            PeriodicWorkRequestBuilder<PeriodicDataSyncWorker>(
                MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS
            )
                .addTag(SYNC_WORK_MANAGER_TAG)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                SYNC_DATA_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
    }

    /**
     * Fetch google sign in or not
     */
    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    /**
     * Fetch sensor associated with the LAMP account
     * after that start that sensor service
     */
    private fun collectSensorData() {
        DebugLogs.writeToFile("collectSensorData")
        var sensorSpecList = arrayListOf<SensorSpecs>()
        oScope.launch(Dispatchers.IO) {
            sensorSpecList = oSensorDao.getSensorsList() as ArrayList<SensorSpecs>
        }
        LampLog.e(TAG, sensorSpecList.size.toString())
        var count = 0
        val timer = object : CountDownTimer(MILLISEC_FUTURE, TIME_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                when (count) {
                    1 -> {/*if (isSignedIn() && AppState.session.isGoogleFitConnected) {
                        GoogleFit(
                            this@LampForegroundService,
                            applicationContext, sensorSpecList
                        )*/

                       /* if (AppState.session.isGoogleHealthConnectConnected) {
                            GoogleHealthConnect(
                                applicationContext,
                                this@LampForegroundService,
                                sensorSpecList
                            )
                        }*/
                    }

                    2 -> {
                        var accelerometerDataRequired = false
                        var sensorSpec = ""
                        var frequency: Double? = null

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
                    }

                    3 -> {
                        var rotationDataRequird = false
                        var frequency: Double? = null

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
                    }

                    4 -> {
                        var magnetometerDataRequired = false
                        var frequency: Double? = null
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

                    }

                    5 -> {
                        var gravityDataRequired = false
                        var frequency: Double? = null
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
                    }

                    6 -> {
                        var locationDateRequired = false
                        var frequency: Double? = null

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
                        }

                    }

                    7 -> {
                        var wifiDataRequired = false
                        var frequency: Double? = null
                        sensorSpecList.forEach {
                            if (it.spec == Sensors.NEARBY_DEVICES.sensor_name) {
                                wifiDataRequired = true

                            }
                            it.frequency?.let {
                                if (it != 0.0 && it <= 1)
                                    frequency = it
                            }
                        }
                        if (wifiDataRequired) {
                            //Invoke WifiData
                            WifiData(
                                this@LampForegroundService,
                                applicationContext, frequency
                            )
                        }

                    }

                    8 -> {
                        var screenStateDataRequired = false
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

                    }

                    9 -> ActivityTransitionData(
                        this@LampForegroundService,
                        applicationContext,
                        sensorSpecList
                    )

                    10 -> {
                        var telephonyDataRequired = false
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

                    }
                    11->{
                        if (AppState.session.isGoogleHealthConnectConnected) {
                            GoogleHealthConnect(applicationContext,this@LampForegroundService,sensorSpecList)
                        }
                    }
                }
            }

            override fun onFinish() {

            }
        }
        timer.start()
        trackSingleEvent("Service_Started")

    }


    /**
     * called when the service is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        trackSingleEvent("Service_Stopped")
    }

    /**
     * The system calls this method when another component wants to bind with the service.
     */
    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /**Method to perform the Sensor Spec or custom sensor data,
     *
     */
    private fun invokeSensorSpecData(initialCall: Boolean = false) {
        DebugLogs.writeToFile("Call sensor spec")

        if (NetworkUtils.isNetworkAvailable(this)) {
            if (NetworkUtils.getBatteryPercentage(this@LampForegroundService) > 15) {
                val sensorSpecsList: ArrayList<SensorSpecs> = arrayListOf()
                val basic = "Basic ${
                    Utils.toBase64(
                        AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                            "https://"
                        ).removePrefix("http://")
                    )
                }"

                GlobalScope.launch(Dispatchers.IO) {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---
                    try {

                        val state = SensorAPI(AppState.session.serverAddress).sensorAll(
                            AppState.session.userId,
                            basic
                        )
                        val oSensorSpec: SensorSpec? = Gson().fromJson(
                            state.toString(),
                            SensorSpec::class.java
                        )
                        if (oSensorSpec?.data?.isNotEmpty() == true) {
                            AppState.session.isCellularUploadAllowed =
                                oSensorSpec.data.find { it.settings == null || it.settings?.cellular_upload == null || it.settings?.cellular_upload == true } != null
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
                            sensorSpecsList.add(sensorSpecs)
                        }
                        oSensorDao.deleteSensorList()
                        oSensorDao.insertAllSensors(sensorSpecsList)
                        GlobalScope.launch(Dispatchers.Main) {
                            if (initialCall)
                                collectSensorData()
                        }
                        LampLog.e(TAG, " Sensor Spec Size -  ${oSensorDao.getSensorsList().size}")

                    } catch (e: SSLHandshakeException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.server_un_rechable)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: ClientException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            DebugLogs.writeToFile("invokeSensorSpecData client exception")
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                            )
                            mainIntent.putExtra("code", e.statusCode)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: ServerException) {
                        GlobalScope.launch(Dispatchers.Main) {

                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: UnsupportedOperationException) {
                        GlobalScope.launch(Dispatchers.Main) {

                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: HttpException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            var message =
                                Utils.getHttpErrorMessage(e.code(), this@LampForegroundService)
                            if (message.isEmpty())
                                message = e.message()
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra("message", message)
                            mainIntent.putExtra("code", e.code())
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: SocketTimeoutException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: NetworkErrorException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: UnknownHostException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    } catch (e: Exception) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val mainIntent =
                                Intent(this@LampForegroundService, ExceptionActivity::class.java)
                            mainIntent.putExtra(
                                "message",
                                getString(digital.lamp.mindlamp.R.string.server_un_rechable)
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (App.app.isApplicationInForeground())
                                startActivity(mainIntent)
                        }
                    }
                }
            }
        } else {

            GlobalScope.launch(Dispatchers.Main) {
                val mainIntent =
                    Intent(this@LampForegroundService, ExceptionActivity::class.java)
                mainIntent.putExtra(
                    "message",
                    getString(digital.lamp.mindlamp.R.string.you_are_not_connected)
                )
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (App.app.isApplicationInForeground()) {
                    startActivity(mainIntent)
                }
            }
        }
    }

    /**Method to perform Sensor Data Webservice after fetching the details from DB
     *
     */
    private fun invokeAddSensorData(
        sensorEventDataList: ArrayList<SensorEvent>,
        isGogolefitData: Boolean
    ) {
        if (!AppState.session.isCellularUploadAllowed && !NetworkUtils.isWifiNetworkAvailable(this))
            return
        if (NetworkUtils.isNetworkAvailable(this)) {
            if (NetworkUtils.getBatteryPercentage(this@LampForegroundService) > 15) {
                trackSingleEvent("API_Send_${sensorEventDataList.size}")
                val basic = "Basic ${
                    Utils.toBase64(
                        AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                            "https://"
                        ).removePrefix("http://")
                    )
                }"

                TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
                try {
                    val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                        AppState.session.userId,
                        sensorEventDataList,
                        basic, isGogolefitData
                    )

                    LampLog.e(TAG, " Lamp Core Response -  $state")
                    if (state.isNotEmpty()) {
                        //Code for drop DB
                        GlobalScope.launch(Dispatchers.IO) {
                            oAnalyticsDao.deleteAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                            LampLog.e("Sensor : invokeAddSensorData")
                            syncAnalyticsData()
                        }
                    }
                } catch (e: ClientException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.putExtra("code", e.statusCode)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: ServerException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: UnsupportedOperationException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: HttpException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        var message =
                            Utils.getHttpErrorMessage(e.code(), this@LampForegroundService)
                        if (message.isEmpty())
                            message = e.message()
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra("message", message)
                        mainIntent.putExtra("code", e.code())
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: SocketTimeoutException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: NetworkErrorException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: UnknownHostException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: SSLHandshakeException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.server_not_reachable)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: Exception) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.server_not_reachable)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                }
            }

        } else {

            GlobalScope.launch(Dispatchers.Main) {
                val mainIntent =
                    Intent(this@LampForegroundService, ExceptionActivity::class.java)
                mainIntent.putExtra(
                    "message",
                    getString(digital.lamp.mindlamp.R.string.you_are_not_connected)
                )
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (App.app.isApplicationInForeground()) {
                    startActivity(mainIntent)
                }
            }
        }
    }

    /**
     * Method to perform Activity API store details to Activity Table and Schedule Activity Alarm Manager
     *
     */
    private fun invokeActivitySchedules() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            DebugLogs.writeToFile("Invoke Activity Schedules")
            val basic = "Basic ${
                Utils.toBase64(
                    AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                        "https://"
                    ).removePrefix("http://")
                )
            }"

            oScope.launch {
                TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---
                try {
                    val activityString = ActivityAPI(AppState.session.serverAddress).activityAll(
                        AppState.session.userId,
                        basic
                    )
                    workManager.cancelAllWorkByTag(WORK_MANAGER_TAG)
                    val activityResponse = Gson().fromJson(
                        activityString.toString(),
                        ActivityResponse::class.java
                    )

                    val oActivityList = arrayListOf<ActivitySchedule>()
                    LampLog.e(TAG, " Response Activity Data-  ${activityResponse.data.size}")
                    activityResponse.data.forEach {
                        it.schedule.let { oScheduleDataList ->
                            //Update Schedule details to the Activity DB
                            if (oScheduleDataList?.size!! > 0) {
                                val activitySchedule = ActivitySchedule(
                                    null,
                                    it.id,
                                    it.spec,
                                    it.name,
                                    it.schedule
                                )
                                oActivityList.add(activitySchedule)
                                //For scheduling the alarm manager for local notification.
                                oScheduleDataList.forEach { durationIntervalLegacy ->
                                    when (durationIntervalLegacy.repeat_interval) {
                                        RepeatInterval.CUSTOM.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && null != durationIntervalLegacy.custom_time && durationIntervalLegacy.notification_ids?.size!! > 0 && durationIntervalLegacy.custom_time?.size!! > 0) {
                                                if (durationIntervalLegacy.notification_ids?.size == durationIntervalLegacy.custom_time?.size) {
                                                    durationIntervalLegacy.notification_ids?.forEachIndexed { index, notificationId ->
                                                        val nId =
                                                            Utils.getMyIntValue(notificationId)
                                                        setAlarmManagerCustom(
                                                            index,
                                                            nId,
                                                            durationIntervalLegacy.custom_time?.get(
                                                                index
                                                            ).toString()
                                                        )
                                                        LampLog.e(
                                                            TAG,
                                                            "Custom Alarm Manager : $index :  ${
                                                                durationIntervalLegacy.custom_time?.get(
                                                                    index
                                                                )
                                                            } :: $nId"
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        RepeatInterval.HOURLY.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerHourly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "HOURLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }
                                        }

                                        RepeatInterval.EVERY_3H.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerEvery3Hourly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "EVERY_3H :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }
                                        }

                                        RepeatInterval.EVERY_6H.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerEvery6Hourly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "EVERY_6H :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }
                                        }

                                        RepeatInterval.EVERY_12H.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerEvery12Hourly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "EVERY_12H :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }
                                        }

                                        RepeatInterval.DAILY.tag -> {

                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerDaily(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "Daily :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }

                                        }

                                        RepeatInterval.BIWEEKLY.tag -> {
                                            val elapsedTimeMs = Utils.getMilliFromDate(
                                                durationIntervalLegacy.time.toString()
                                            )
                                            val calendar = Calendar.getInstance()
                                            calendar.timeInMillis = elapsedTimeMs
                                            val dateNumber = calendar.get(Calendar.DATE)

                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setLocalNotificationBiWeekly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "BIWEEKLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }

                                        }

                                        RepeatInterval.TRIWEEKLY.tag -> {
                                            val elapsedTimeMs = Utils.getMilliFromDate(
                                                durationIntervalLegacy.time.toString()
                                            )
                                            val calendar = Calendar.getInstance()
                                            calendar.timeInMillis = elapsedTimeMs
                                            val dateNumber = calendar.get(Calendar.DATE)

                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setLocalNotificationTriWeekly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "BIWEEKLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }

                                        }

                                        RepeatInterval.WEEKLY.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setLocalNotificationWeekly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "WEEKLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }


                                        }

                                        RepeatInterval.FORTNIGHTLY.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setLocalNotificationFortnightly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "FORTNIGHTLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }


                                        }

                                        RepeatInterval.BIMONTHLY.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setLocalNotificationBiMonthly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "BIMONTHLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }

                                        }

                                        RepeatInterval.MONTHLY.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setLocalNotificationMonthly(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "MONTHLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }

                                        }

                                        RepeatInterval.NONE.tag -> {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setDoNotRepeatNotification(
                                                        nId,
                                                        durationIntervalLegacy.time.toString(),
                                                        durationIntervalLegacy.start_date.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "NONE :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    oActivityDao.deleteActivityList()
                    oActivityDao.insertAllActivity(oActivityList)
                    LampLog.e(
                        TAG,
                        "Activity DB Size : ${oActivityDao.getActivityList().size.toString()}"
                    )
                } catch (e: ClientException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.putExtra("code", e.statusCode)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: ServerException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: UnsupportedOperationException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: HttpException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        var message =
                            Utils.getHttpErrorMessage(e.code(), this@LampForegroundService)
                        if (message.isEmpty())
                            message = e.message()
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra("message", message)
                        mainIntent.putExtra("code", e.code())
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground()) {
                            startActivity(mainIntent)
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.something_went_wrong)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground()) {
                            startActivity(mainIntent)
                        }
                    }
                } catch (e: NetworkErrorException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: UnknownHostException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.txt_unable_to_connect)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: SSLHandshakeException) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.server_not_reachable)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                } catch (e: Exception) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val mainIntent =
                            Intent(this@LampForegroundService, ExceptionActivity::class.java)
                        mainIntent.putExtra(
                            "message",
                            getString(digital.lamp.mindlamp.R.string.server_not_reachable)
                        )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (App.app.isApplicationInForeground())
                            startActivity(mainIntent)
                    }
                }
            }
        } else {

            GlobalScope.launch(Dispatchers.Main) {
                val mainIntent =
                    Intent(this@LampForegroundService, ExceptionActivity::class.java)
                mainIntent.putExtra(
                    "message",
                    getString(digital.lamp.mindlamp.R.string.you_are_not_connected)
                )
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (App.app.isApplicationInForeground()) {
                    startActivity(mainIntent)
                }
            }
        }
    }

    /**Method to fetch details of notification from Activity DB from notification id and show local notification if offline.
     *
     */
    fun invokeLocalNotification(localNotificationId: Int) {
        oScope.launch {
            LampLog.e("BROADCASTRECEIVER", "invokeLocalNotification 1")
            val activityList = oActivityDao.getActivityList()
            activityList.forEach { activitySchedule ->
                activitySchedule.schedule?.forEach { durationIntervalLegacy ->
                    LampLog.e("BROADCASTRECEIVER", "invokeLocalNotification 2")
                    durationIntervalLegacy.notification_ids?.forEach {
                        if (Utils.getMyIntValue(it) == localNotificationId) {
                            LampLog.e("BROADCASTRECEIVER", "invokeLocalNotification 3")
                            LampLog.e(
                                TAG,
                                "Activity Name :: - ${activitySchedule.name} ---- $localNotificationId"
                            )
                            LampNotificationManager.showActivityNotification(
                                this@LampForegroundService,
                                activitySchedule,
                                localNotificationId
                            )
                        }
                    }
                }
            }
        }
    }

    /**Method to schedule Alarm Manager for evey 24 hours.
     *
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerForEvery24Hours() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, ActivityRepeatReceiver::class.java).apply {
            putExtra("id", AppConstants.REPEAT_DAILY)
        }.let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + 30 * 1000,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    /**
     * callback method implementation for Accelerometer data
     */
    override fun getAccelerometerData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            val id = oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Rotation data
     */
    override fun getRotationData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Magnetic data
     */
    override fun getMagneticData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Gyroscope data
     */
    override fun getGyroscopeData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Location  data
     */
    override fun getLocationData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Wifi data
     */
    override fun getWifiData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Screen state data
     */
    override fun getScreenState(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Activity data
     */
    override fun getActivityData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    /**
     * callback method implementation for Telephony  data
     */
    override fun getTelephonyData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        GlobalScope.async {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getGoogleHealthConnect(sensorEventData: ArrayList<SensorEvent>) {
        val gson = GsonBuilder().serializeNulls().create()
        LampLog.e("Google Health connect 1: ${gson.toJson(sensorEventData)}")

        val oAnalyticsList: ArrayList<Analytics> = arrayListOf()
        GlobalScope.async {
            sensorEventData.forEach {
                val oAnalytics = Analytics()
                oAnalytics.analyticsData = gson.toJson(it)
                oAnalyticsList.add(oAnalytics)
            }
            //Insert it into Analytics DB
            oAnalyticsDao.insertAllAnalytics(oAnalyticsList)
            LampLog.e("Google Health Connect : ${gson.toJson(sensorEventData)}")
            DebugLogs.writeToFile("Google Health Connect : ${gson.toJson(sensorEventData)}")
        }
    }

    /**
     * Log events in firebase crashlytics
     */
    private fun trackSingleEvent(eventName: String) {
        //Firebase Event Tracking
        val params = Bundle()
        firebaseAnalytics.logEvent(eventName, params)
    }

    /**
     * Set custom alarm manager
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerCustom(oIndex: Int, oNotificationId: Int?, oCustomTime: String) {
        val elapsedTimeMs = Utils.getMilliFromDate(oCustomTime)
        var delay = 0L
        if (elapsedTimeMs > System.currentTimeMillis()) {
            delay = elapsedTimeMs - System.currentTimeMillis()
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = elapsedTimeMs
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            var nextNotificationTime = calendar.timeInMillis
            while (nextNotificationTime < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                nextNotificationTime = calendar.timeInMillis
            }
            delay = nextNotificationTime - System.currentTimeMillis()
        }

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.CUSTOM.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }
        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)

    }

    private fun setDoNotRepeatNotification(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        var delay = 0L
        val remnderTime = getAlarmStartTime(oTime, startTime)
        if (remnderTime > System.currentTimeMillis()) {
            delay = remnderTime - System.currentTimeMillis()
            val data = Data.Builder()
            data.putString(
                ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
                RepeatInterval.NONE.tag
            )
            oNotificationId?.let {
                data.putInt(
                    ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                    it
                )
            }
            val work =
                OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data.build())
                    .addTag(WORK_MANAGER_TAG)
                    .build()

            workManager.enqueue(work)
        }
    }

    /**
     * Set local notification
     */
    private fun setLocalNotification(
        oNotificationId: Int?,
        oTime: String,
        startTime: String,
        repeatInterval: String
    ) {
        var delay = 0L
        val remnderTime = getAlarmStartTime(oTime, startTime)
        if (remnderTime > System.currentTimeMillis()) {
            delay = remnderTime - System.currentTimeMillis()
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = remnderTime
            var repeatTime = when (repeatInterval) {
                RepeatInterval.HOURLY.tag -> 1
                RepeatInterval.EVERY_3H.tag -> 3
                RepeatInterval.EVERY_6H.tag -> 6
                RepeatInterval.EVERY_12H.tag -> 12
                else -> 24
            }
            calendar.add(Calendar.HOUR, repeatTime)
            var nextReminderTime = calendar.timeInMillis
            while (nextReminderTime < System.currentTimeMillis()) {
                calendar.add(Calendar.HOUR, repeatTime)
                nextReminderTime = calendar.timeInMillis
            }
            delay = nextReminderTime - System.currentTimeMillis()

        }

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.HOURLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }
        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)
    }

    /**
     * set hourly notification
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerHourly(oNotificationId: Int?, oTime: String, startTime: String) {
        setLocalNotification(oNotificationId, oTime, startTime, RepeatInterval.HOURLY.tag)

    }

    /**
     * set 3 hour repeat notification
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerEvery3Hourly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {

        setLocalNotification(oNotificationId, oTime, startTime, RepeatInterval.EVERY_3H.tag)
    }

    /**
     * set 6 hour repeat notification
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerEvery6Hourly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        setLocalNotification(oNotificationId, oTime, startTime, RepeatInterval.EVERY_6H.tag)
    }

    /**
     * set 12 hour repeat notification
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerEvery12Hourly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        setLocalNotification(oNotificationId, oTime, startTime, RepeatInterval.EVERY_12H.tag)
    }

    /**
     * set daily repeating notification
     */
    private fun setAlarmManagerDaily(oNotificationId: Int?, oTime: String, startTime: String) {
        setLocalNotification(oNotificationId, oTime, startTime, RepeatInterval.DAILY.tag)
    }

    /**
     * set bi weekly repeating notification.
     */
    private fun setLocalNotificationBiWeekly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        val reminderTime = getAlarmStartTime(oTime, startTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = reminderTime
        var tuesdayTimeMillis = 0L
        var thursdayTimeMillis = 0L
        var currentDayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.TUESDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        tuesdayTimeMillis = calendar.timeInMillis

        calendar.timeInMillis = reminderTime
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.THURSDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        thursdayTimeMillis = calendar.timeInMillis

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.BIWEEKLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }

        var smallestInterval = Math.min(tuesdayTimeMillis, thursdayTimeMillis)
        var highestInterval = Math.max(tuesdayTimeMillis, thursdayTimeMillis)
        var delay = highestInterval
        if (smallestInterval > System.currentTimeMillis())
            delay = smallestInterval

        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)

    }

    /**
     * set weekly repeating notification
     */
    private fun setLocalNotificationWeekly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        val reminderTime = getAlarmStartTime(oTime, startTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = reminderTime

        var delay = 0L
        if (reminderTime > System.currentTimeMillis()) {
            delay = reminderTime - System.currentTimeMillis()
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = reminderTime
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
            var nextReminderTime = calendar.timeInMillis
            while (nextReminderTime < System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_MONTH, 1)
                nextReminderTime = calendar.timeInMillis
            }
            delay = nextReminderTime - System.currentTimeMillis()
        }

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.WEEKLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }
        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)
    }

    /**
     * set for nightly repeating notification.
     */
    private fun setLocalNotificationFortnightly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        val reminderTime = getAlarmStartTime(oTime, startTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = reminderTime

        var delay = 0L
        if (reminderTime > System.currentTimeMillis()) {
            delay = reminderTime - System.currentTimeMillis()
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = reminderTime
            calendar.add(Calendar.WEEK_OF_MONTH, 2)
            var nextReminderTime = calendar.timeInMillis



            while (nextReminderTime < System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_MONTH, 2)
                nextReminderTime = calendar.timeInMillis
            }

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val calendar1 = Calendar.getInstance()
            calendar1.timeInMillis = nextReminderTime
            val twoWeektime = formatter.format(calendar.time)
            DebugLogs.writeToFile("setLocalNotificationFortnightly @ $twoWeektime")

            delay = nextReminderTime - System.currentTimeMillis()
        }

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.FORTNIGHTLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }
        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)
    }

    /**
     * set tri weekly repeating notification
     */
    private fun setLocalNotificationTriWeekly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {
        val reminderTime = getAlarmStartTime(oTime, startTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = reminderTime
        var mondayTimeMillis = 0L
        var wednesdayTimeMillis = 0L
        var fridayTimeMillis = 0L
        var currentDayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        mondayTimeMillis = calendar.timeInMillis

        calendar.timeInMillis = reminderTime
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        wednesdayTimeMillis = calendar.timeInMillis

        calendar.timeInMillis = reminderTime
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.FRIDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        fridayTimeMillis = calendar.timeInMillis


        val highestInterval = Math.max(
            mondayTimeMillis, Math.max(
                wednesdayTimeMillis,
                fridayTimeMillis
            )
        )
        val secondSmallestInterval = Math.max(
            mondayTimeMillis, Math.min(
                wednesdayTimeMillis,
                fridayTimeMillis
            )
        )
        val smallestInterval = Math.min(
            mondayTimeMillis, Math.min(
                wednesdayTimeMillis,
                fridayTimeMillis
            )
        )

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.TRIWEEKLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }

        var delay = highestInterval
        if (smallestInterval > System.currentTimeMillis())
            delay = smallestInterval
        else if (secondSmallestInterval > System.currentTimeMillis())
            delay = secondSmallestInterval


        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)

    }

    /**
     * set bi monthly repeating local notification
     */
    private fun setLocalNotificationBiMonthly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {

        val reminderTime = getAlarmStartTime(oTime, startTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = reminderTime
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val mins = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONDAY)
        calendar.set(year, month, 10, hour, mins, seconds)
        val dateTenMillis = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 10)
        val dateTwentyMillis = calendar.timeInMillis

        val shortestInterval = min(dateTenMillis, dateTwentyMillis)
        val longestInterval = max(dateTenMillis, dateTwentyMillis)

        var delay = longestInterval
        if (shortestInterval > System.currentTimeMillis())
            delay = shortestInterval

        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.BIMONTHLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }

        val work =
            OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .addTag(WORK_MANAGER_TAG)
                .build()

        workManager.enqueue(work)

    }

    /**
     * set monthly repeating notification.
     */
    private fun setLocalNotificationMonthly(
        oNotificationId: Int?,
        oTime: String,
        startTime: String
    ) {

        val reminderTime = getAlarmStartTime(oTime, startTime)
        val todaysDate = Calendar.getInstance()
        todaysDate.timeInMillis = reminderTime

        val calendar = Calendar.getInstance()
        var delay = 0L
        if (reminderTime > System.currentTimeMillis()) {
            delay = reminderTime - System.currentTimeMillis()
        } else {
            calendar.timeInMillis = reminderTime
            calendar.add(Calendar.MONTH, 1)
            val nextReminderTime = calendar.timeInMillis
            delay = nextReminderTime - System.currentTimeMillis()
        }


        val data = Data.Builder()
        data.putString(
            ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
            RepeatInterval.MONTHLY.tag
        )
        oNotificationId?.let {
            data.putInt(
                ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                it
            )
        }


        if (todaysDate.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
            todaysDate.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance()
                .get(Calendar.DAY_OF_MONTH) &&
            todaysDate.get(Calendar.HOUR_OF_DAY) == Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY) &&
            todaysDate.get(Calendar.MINUTE) == Calendar.getInstance().get(Calendar.MINUTE)
        ) {
            val work =
                OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data.build())
                    .addTag(WORK_MANAGER_TAG)
                    .build()

            WorkManager.getInstance(this).enqueue(work)
        }

    }

    /**
     * get alarm start time.
     */
    private fun getAlarmStartTime(oTime: String, startTime: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val sdfStart = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        val dateSdf = SimpleDateFormat("yyyy-MM-dd")
        val timeSdf = SimpleDateFormat("HH:mm:ss.SSS")
        sdf.timeZone = TimeZone.getDefault()//TimeZone.getTimeZone("UTC")
        sdfStart.timeZone = TimeZone.getDefault()//TimeZone.getTimeZone("UTC")

        try {
            val mDate = sdf.parse(startTime)!!
            val formattedDate: String = dateSdf.format(mDate)

            val time = sdf.parse(oTime)!!
            val formattedTime: String = timeSdf.format(time)
            val start = "$formattedDate $formattedTime"
            sdfStart.timeZone = TimeZone.getDefault()
            val gmtTime = sdfStart.parse(start)!!

            return gmtTime.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }


}