package digital.lamp.mindlamp.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import digital.lamp.lamp_kotlin.lamp_core.apis.ActivityAPI
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorAPI
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.AlarmBroadCastReceiver
import digital.lamp.mindlamp.BuildConfig
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
import digital.lamp.mindlamp.sensor.RotationData
import digital.lamp.mindlamp.sensor.ScreenStateData
import digital.lamp.mindlamp.sheduleing.ActivityReceiver
import digital.lamp.mindlamp.sheduleing.ActivityRepeatReceiver
import digital.lamp.mindlamp.sheduleing.RepeatInterval
import digital.lamp.mindlamp.sheduleing.ScheduleConstants
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.AppConstants.ALARM_INTERVAL
import digital.lamp.mindlamp.utils.AppConstants.DAY_INTERVAL
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class LampForegroundService : Service(),
    SensorListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        private val TAG = LampForegroundService::class.java.simpleName
        private const val TIME_INTERVAL : Long = 3000
        private const val MILLISEC_FUTURE : Long = 60000
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

    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics = Firebase.analytics

        oAnalyticsDao = AppDatabase.getInstance(this).analyticsDao()
        oSensorDao = AppDatabase.getInstance(this).sensorDao()
        oActivityDao = AppDatabase.getInstance(this).activityDao()
        oScope = CoroutineScope(Dispatchers.IO)
        oGson = Gson()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        isAlarm = intent?.extras?.getBoolean("set_alarm")!!
        isActivitySchedule = intent.extras?.getBoolean("set_activity_schedule")!!
        localNotificationId = intent.extras?.getInt("notification_id")!!
        if(!isAlarm && !isActivitySchedule && localNotificationId == 0){
            val notification =
                LampNotificationManager.showNotification(this, "MindLamp Active Data Collection")

            startForeground(1010, notification)
            collectSensorData()
            setAlarmManager()

            invokeSensorSpecData()
//            invokeActivitySchedules()
            setAlarmManagerForEvery24Hours()
        }
        else if(!isAlarm && isActivitySchedule && localNotificationId == AppConstants.REPEAT_DAILY){
            LampLog.e(TAG, "Call Activity Schedule for every 24 hours")
            invokeActivitySchedules()
        }
        else if(!isAlarm && isActivitySchedule && localNotificationId != 0){
            LampLog.e(TAG, "Call for showing up the local notification")
            if(!Utils.isOnline(this)) {
                invokeLocalNotification(localNotificationId)
            }
        }
        else {
            //This will execute every 10 min if logged in
            val sensorEventDataList: ArrayList<SensorEvent> = arrayListOf<SensorEvent>()
            sensorEventDataList.clear()

            val gson = GsonBuilder()
                .create()
            GlobalScope.launch(Dispatchers.IO) {
                val list = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                list.forEach {
                    sensorEventDataList.add(
                        gson.fromJson(
                            it.analyticsData,
                            SensorEvent::class.java
                        )
                    )
                }
                list.let {
                    if(it.isNotEmpty()) {
                        AppState.session.lastAnalyticsTimestamp = it[0].datetimeMillisecond!!
                    }
                }
                LampLog.e("DB : ${list.size} and Sensor : ${sensorEventDataList.size}")
                invokeAddSensorData(sensorEventDataList)
            }

            //Fetch google fit data in 10 min interval
            Lamp.stopLAMP(this)
            collectSensorData()
        }

        return START_STICKY
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManager() {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmBroadCastReceiver::class.java).
        let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + ALARM_INTERVAL,
            ALARM_INTERVAL,
            alarmIntent
        )
    }

    private fun  collectSensorData() {
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
                    1 -> GoogleFit(
                        this@LampForegroundService,
                        applicationContext, sensorSpecList
                    )
                    2 -> {
                        var accelerometerDataRequired =false
                        if(sensorSpecList.isEmpty()){
                            accelerometerDataRequired = true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    accelerometerDataRequired = true
                                }
                            }
                        }
                        //Invoke Accelerometer Call
                        if(accelerometerDataRequired) {
                            AccelerometerData(
                                this@LampForegroundService,
                                applicationContext
                            )
                        }
                    }
                    3 -> {
                        var rotationDataRequird = false
                        if(sensorSpecList.isEmpty()){
                            rotationDataRequird =true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    rotationDataRequird = true

                                } //Invoke Rotation Call
                            }
                        }
                        if(rotationDataRequird){
                            RotationData(
                                this@LampForegroundService,
                                applicationContext
                            )
                        }
                    }
                    4 -> {
                        var magnetometerDataRequired =false
                        if(sensorSpecList.isEmpty()){
                            magnetometerDataRequired =true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    magnetometerDataRequired =true

                                }
                            }
                        }
                        if(magnetometerDataRequired){
                            //Invoke Magnet Call
                            MagnetometerData(
                                this@LampForegroundService,
                                applicationContext
                            )
                        }

                    }
                    5 -> {
                        var gyroscopeDataRequired = false
                        if(sensorSpecList.isEmpty()){
                            gyroscopeDataRequired= true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.DEVICE_MOTION.sensor_name) {
                                    gyroscopeDataRequired =true

                                }//Invoke Gyroscope Call
                            }
                        }
                        if(gyroscopeDataRequired){
                            GyroscopeData(
                                this@LampForegroundService,
                                applicationContext
                            )
                        }

                    }
                    6 -> {
                        var locationDateRequired = false
                        if(sensorSpecList.isEmpty()){
                            locationDateRequired =true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.GPS.sensor_name) {
                                    locationDateRequired =true

                                }
                            }
                        }
                        if(locationDateRequired){
                            //Invoke Location
                            LocationData(
                                this@LampForegroundService,
                                applicationContext
                            )
                        }

                    }
                    7 -> {
                        var wifiDataRequired= false
                        if(sensorSpecList.isEmpty()){
                            wifiDataRequired = true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.NEARBY_DEVICES.sensor_name) {
                                    wifiDataRequired = true

                                }
                            }
                        }
                        if(wifiDataRequired){
                            //Invoke WifiData
                            WifiData(
                                this@LampForegroundService,
                                applicationContext
                            )
                        }

                    }
                    8 -> {
                        var screenStateDataRequired =false
                        if(sensorSpecList.isEmpty()){
                            screenStateDataRequired =true

                        }else {
                            sensorSpecList.forEach {
                                if (it.spec == Sensors.SCREEN_STATE.sensor_name) {
                                    screenStateDataRequired =true

                                }
                            }
                        }
                        if(screenStateDataRequired){
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
                }
            }
            override fun onFinish() {
//                if (!isAlarm) {
//                    setAlarmManager()
//                }
//                stopForeground(true)
//                stopSelf()
            }
        }
        timer.start()
        trackSingleEvent("Service_Started")

    }


    override fun onDestroy() {
        super.onDestroy()
        trackSingleEvent("Service_Stopped")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //Method to perform the Sensor Spec or custom sensor data,
    private fun invokeSensorSpecData(){
        if (NetworkUtils.isNetworkAvailable(this) && NetworkUtils.getBatteryPercentage(this@LampForegroundService) > 15) {
            val sensorSpecsList : ArrayList<SensorSpecs> = arrayListOf()
            val basic = "Basic ${Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                    "https://"
                ).removePrefix("http://")
            )}"

            GlobalScope.launch(Dispatchers.IO) {
                val state = SensorAPI(AppState.session.serverAddress).sensorAll(
                    AppState.session.userId,
                    basic
                )
                val oSensorSpec: SensorSpec = Gson().fromJson(
                    state.toString(),
                    SensorSpec::class.java
                )
                oSensorSpec.data.forEach { sensor ->
                    val sensorSpecs = SensorSpecs(null, sensor.id, sensor.spec, sensor.name)
                    sensorSpecsList.add(sensorSpecs)
                }
                oSensorDao.deleteSensorList()
                oSensorDao.insertAllSensors(sensorSpecsList)
                LampLog.e(TAG, " Sensor Spec Size -  ${oSensorDao.getSensorsList().size}")
            }
        }
    }

    //Method to perform Sensor Data Webservice after fetching the details from DB
    private fun invokeAddSensorData(sensorEventDataList: ArrayList<SensorEvent>) {
        if (NetworkUtils.isNetworkAvailable(this) && NetworkUtils.getBatteryPercentage(this@LampForegroundService) > 15) {
            DebugLogs.writeToFile("API Send : ${sensorEventDataList.size}")
            trackSingleEvent("API_Send_${sensorEventDataList.size}")

            val basic = "Basic ${Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                    "https://"
                ).removePrefix("http://")
            )}"
            val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                AppState.session.userId,
                sensorEventDataList,
                basic
            )
            LampLog.e(TAG, " Lamp Core Response -  $state")
            if(state.isNotEmpty()){
                //Code for drop DB
                GlobalScope.launch(Dispatchers.IO) {
                    oAnalyticsDao.deleteAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                }
            }
        }
    }

    //Method to perform Activity API store details to Activity Table and Schedule Activity Alarm Manager
    private fun invokeActivitySchedules(){
        if(NetworkUtils.isNetworkAvailable(this)){
            DebugLogs.writeToFile("Invoke Activity Schedules")
            val basic = "Basic ${Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                    "https://"
                ).removePrefix("http://")
            )}"


//            val basic = "Basic ${Utils.toBase64(
//                "U0817022664@lamp.com:U0817022664"
//            )}"

            oScope.launch {
                val activityString = ActivityAPI(AppState.session.serverAddress).activityAll(
                    AppState.session.userId,
                    basic
                )
                val activityResponse = Gson().fromJson(
                    activityString.toString(),
                    ActivityResponse::class.java
                )

                LampLog.e(
                    TAG, "Activity Notification Response :-  ${
                        activityResponse.data[0].schedule?.get(
                            0
                        )?.notification_ids?.size.toString()
                    }"
                )


                val oActivityList = arrayListOf<ActivitySchedule>()
                LampLog.e(TAG, " Response Activity Data-  ${activityResponse.data.size}")
                activityResponse.data.forEach {
                    it.schedule.let { oScheduleDataList ->
                        //Update Schedule details to the Activity DB
                        if(oScheduleDataList?.size!! > 0){
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
                                when(durationIntervalLegacy.repeat_interval){
                                    RepeatInterval.CUSTOM.tag -> {
                                        if (null != durationIntervalLegacy.notification_ids && null != durationIntervalLegacy.custom_time && durationIntervalLegacy.notification_ids?.size!! > 0 && durationIntervalLegacy.custom_time?.size!! > 0) {
                                            if (durationIntervalLegacy.notification_ids?.size == durationIntervalLegacy.custom_time?.size) {
                                                durationIntervalLegacy.notification_ids?.forEachIndexed { index, notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerCustom(
                                                        index,
                                                        nId,
                                                        durationIntervalLegacy.custom_time?.get(
                                                            index
                                                        ).toString()
                                                    )
                                                    LampLog.e(
                                                        TAG, "Custom Alarm Manager : $index :  ${
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
                                                    durationIntervalLegacy.time.toString()
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
                                                    durationIntervalLegacy.time.toString()
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
                                                    durationIntervalLegacy.time.toString()
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
                                                    durationIntervalLegacy.time.toString()
                                                )
                                                LampLog.e(
                                                    TAG,
                                                    "EVERY_12H :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                )
                                            }
                                        }
                                    }

                                    RepeatInterval.DAILY.tag -> {
                                        val elapsedTimeMs = Utils.getMilliFromDate(durationIntervalLegacy.time.toString())
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = elapsedTimeMs
                                        val dateNumber = calendar.get(Calendar.DATE)

                                        val currentTime = LocalDateTime.now()
                                        if (dateNumber == currentTime.dayOfMonth) {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerCustom(
                                                        0,
                                                        nId,
                                                        durationIntervalLegacy.time.toString()
                                                    )
                                                    LampLog.e(TAG, "DAILY :- ${durationIntervalLegacy.notification_ids?.size}}")
                                                }
                                            }
                                        }
                                    }
                                    RepeatInterval.BIWEEKLY.tag -> {
                                        val elapsedTimeMs = Utils.getMilliFromDate(durationIntervalLegacy.time.toString())
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = elapsedTimeMs
                                        val dateNumber = calendar.get(Calendar.DATE)

                                        val currentTime = LocalDateTime.now()
                                        if (dateNumber == currentTime.dayOfMonth && (currentTime.dayOfWeek.toString() == "TUESDAY" || currentTime.dayOfWeek.toString() == "THURSDAY")) {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerCustom(
                                                        0,
                                                        nId,
                                                        durationIntervalLegacy.time.toString()
                                                    )
                                                    LampLog.e(
                                                        TAG,
                                                        "BIWEEKLY :- ${durationIntervalLegacy.notification_ids?.size}}"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    RepeatInterval.WEEKLY.tag -> {
                                        val elapsedTimeMs = Utils.getMilliFromDate(durationIntervalLegacy.time.toString())
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = elapsedTimeMs
                                        val dateNumber = calendar.get(Calendar.DATE)

                                        val currentTime = LocalDateTime.now()
                                        if (dateNumber == currentTime.dayOfMonth) {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerCustom(
                                                        0,
                                                        nId,
                                                        durationIntervalLegacy.time.toString()
                                                    )
                                                    LampLog.e(TAG, "WEEKLY :- ${durationIntervalLegacy.notification_ids?.size}}")
                                                }
                                            }
                                        }
                                    }
                                    RepeatInterval.BIMONTHLY.tag -> {
                                        val elapsedTimeMs = Utils.getMilliFromDate(durationIntervalLegacy.time.toString())
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = elapsedTimeMs
                                        val dateNumber = calendar.get(Calendar.DATE)

                                        val currentTime = LocalDateTime.now()
                                        if (dateNumber == currentTime.dayOfMonth && (dateNumber == 10 || dateNumber == 20)) {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerCustom(
                                                        0,
                                                        nId,
                                                        durationIntervalLegacy.time.toString()
                                                    )
                                                    LampLog.e(TAG, "BIMONTHLY :- ${durationIntervalLegacy.notification_ids?.size}}")
                                                }
                                            }
                                        }
                                    }
                                    RepeatInterval.MONTHLY.tag -> {
                                        val elapsedTimeMs = Utils.getMilliFromDate(durationIntervalLegacy.time.toString())
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = elapsedTimeMs
                                        val dateNumber = calendar.get(Calendar.DATE)

                                        val currentTime = LocalDateTime.now()
                                        if (dateNumber == currentTime.dayOfMonth) {
                                            if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                                durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                    val nId = Utils.getMyIntValue(notificationId)
                                                    setAlarmManagerCustom(
                                                        0,
                                                        nId,
                                                        durationIntervalLegacy.time.toString()
                                                    )
                                                    LampLog.e(TAG, "MONTHLY :- ${durationIntervalLegacy.notification_ids?.size}}")
                                                }
                                            }
                                        }
                                    }
                                    RepeatInterval.NONE.tag -> {
                                        if (null != durationIntervalLegacy.notification_ids && durationIntervalLegacy.notification_ids?.size!! > 0) {
                                            durationIntervalLegacy.notification_ids?.forEach { notificationId ->
                                                val nId = Utils.getMyIntValue(notificationId)
                                                setAlarmManagerCustom(
                                                    0,
                                                          nId,
                                                        durationIntervalLegacy.time.toString()
                                                    )
                                                    LampLog.e(TAG, "NONE :- ${durationIntervalLegacy.notification_ids?.size}}")
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
            }
        }
    }

    //Method to fetch details of notification from Activity DB from notification id and show local notification if offline.
    private fun invokeLocalNotification(localNotificationId: Int) {
        oScope.launch {
            val activityList = oActivityDao.getActivityList()
            activityList.forEach { activitySchedule ->
                activitySchedule.schedule?.forEach { durationIntervalLegacy ->
                    durationIntervalLegacy.notification_ids?.forEach {
                        if(Utils.getMyIntValue(it) == localNotificationId){
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

    //Method to schedule Alarm Manager for evey 24 hours.
    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerForEvery24Hours() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, ActivityRepeatReceiver::class.java).apply {
            putExtra("id", AppConstants.REPEAT_DAILY)
        }.let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
        // Set the alarm to start at approximately 12:00 p.m.
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 12)
//        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + 30 * 1000,
             AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    override fun getAccelerometerData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getRotationData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getMagneticData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getGyroscopeData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getLocationData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getWifiData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getScreenState(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getActivityData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        oScope.async{
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

    override fun getGoogleFitData(sensorEventData: ArrayList<SensorEvent>) {
        LampLog.e("Google Fit : ${oGson.toJson(sensorEventData)}")
        val oAnalyticsList: ArrayList<Analytics> = arrayListOf()
        oScope.async{
            sensorEventData.forEach {
                val oAnalytics = Analytics()
                oAnalytics.analyticsData = oGson.toJson(it)
                oAnalyticsList.add(oAnalytics)
            }
            //Insert it into Analytics DB
            oAnalyticsDao.insertAllAnalytics(oAnalyticsList)
        }
    }

    private fun trackSingleEvent(eventName: String) {
        //Firebase Event Tracking
        val params = Bundle()
        firebaseAnalytics.logEvent(eventName, params)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerCustom(oIndex: Int, oNotificationId: Int?, oCustomTime: String) {
        val randomInt = (1..100).shuffled().first()
        val elapsedTimeMs = Utils.getMilliFromDate(oCustomTime)


        val currentTime = LocalDateTime.now()

        if(elapsedTimeMs > System.currentTimeMillis() && elapsedTimeMs < System.currentTimeMillis() + DAY_INTERVAL) {
            val almManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val almIntent = Intent(this, ActivityReceiver::class.java).apply {
                putExtra("id", oNotificationId)
            }.let { intent ->
                PendingIntent.getBroadcast(this, randomInt + oIndex, intent, 0)
            }
            almManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                elapsedTimeMs,
                almIntent
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerHourly(oNotificationId: Int?, oTime: String) {
        val randomInt = (1..100).shuffled().first()
        val elapsedTimeMs = Utils.getMilliFromDate(oTime)
        if(elapsedTimeMs > System.currentTimeMillis() && elapsedTimeMs < System.currentTimeMillis() + DAY_INTERVAL) {
            val almManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val almIntent = Intent(this, ActivityReceiver::class.java).apply {
                putExtra("id", oNotificationId)
            }
                .let { intent ->
                    PendingIntent.getBroadcast(this, randomInt, intent, 0)
                }
            almManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                elapsedTimeMs,
                AlarmManager.INTERVAL_HOUR,
                almIntent
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerEvery3Hourly(oNotificationId: Int?, oTime: String) {
        val randomInt = (1..100).shuffled().first()
        val elapsedTimeMs = Utils.getMilliFromDate(oTime)
        if(elapsedTimeMs > System.currentTimeMillis() && elapsedTimeMs < System.currentTimeMillis() + DAY_INTERVAL) {
            val almManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val almIntent = Intent(this, ActivityReceiver::class.java).apply {
                putExtra("id", oNotificationId)
            }
                .let { intent ->
                    PendingIntent.getBroadcast(this, randomInt, intent, 0)
                }
            almManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                Utils.getMilliFromDate(oTime),
                ScheduleConstants.EVERY_3H,
                almIntent
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerEvery6Hourly(oNotificationId: Int?, oTime: String) {
        val randomInt = (1..100).shuffled().first()
        val elapsedTimeMs = Utils.getMilliFromDate(oTime)
        if(elapsedTimeMs > System.currentTimeMillis() && elapsedTimeMs < System.currentTimeMillis() + DAY_INTERVAL) {
            val almManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val almIntent = Intent(this, ActivityReceiver::class.java).apply {
                putExtra("id", oNotificationId)
            }
                .let { intent ->
                    PendingIntent.getBroadcast(this, randomInt, intent, 0)
                }
            almManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                Utils.getMilliFromDate(oTime),
                ScheduleConstants.EVERY_6H,
                almIntent
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setAlarmManagerEvery12Hourly(oNotificationId: Int?, oTime: String) {
        val randomInt = (1..100).shuffled().first()
        val elapsedTimeMs = Utils.getMilliFromDate(oTime)
        if(elapsedTimeMs > System.currentTimeMillis() && elapsedTimeMs < System.currentTimeMillis() + DAY_INTERVAL) {
            val almManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val almIntent = Intent(this, ActivityReceiver::class.java).apply {
                putExtra("id", oNotificationId)
            }
                .let { intent ->
                    PendingIntent.getBroadcast(this, randomInt, intent, 0)
                }
            almManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                Utils.getMilliFromDate(oTime),
                ScheduleConstants.EVERY_12H,
                almIntent
            )
        }
    }
}