package digital.lamp.mindlamp.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import digital.lamp.sensor.Lamp
import digital.lamp.service.apis.SensorEventAPI
import digital.lamp.mindlamp.AlarmBroadCastReceiver
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.Analytics
import digital.lamp.mindlamp.database.AnalyticsDao
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.sensor.*
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.AppConstants.ALARM_INTERVAL
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.service.models.SensorEvent
import kotlinx.coroutines.*


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class LampForegroundService : Service(),
    SensorListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        private val TAG = LampForegroundService::class.java.simpleName
        private val TIME_INTERVAL : Long = 3000
        private val MILLISEC_FUTURE : Long = 60000
    }

    private var isAlarm: Boolean = false
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var oAnalyticsDao: AnalyticsDao
    private lateinit var oScope: CoroutineScope
    private lateinit var oGson: Gson

    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics = Firebase.analytics

        oAnalyticsDao = AppDatabase.getInstance(this).analyticsDao()
        oScope = CoroutineScope(Dispatchers.IO)
        oGson = Gson()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isAlarm = intent?.extras?.getBoolean("set_alarm")!!
        if(!isAlarm){
            val notification =
                LampNotificationManager.showNotification(this, "MindLamp Active Data Collection")

            startForeground(1010, notification)
            collectSensorData()
            setAlarmManager()
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
        alarmIntent = Intent(this, AlarmBroadCastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
        alarmManager.cancel(alarmIntent)
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + ALARM_INTERVAL,
            ALARM_INTERVAL,
            alarmIntent
        )
    }

    private fun collectSensorData() {
        var count = 0
        val timer = object : CountDownTimer(MILLISEC_FUTURE, TIME_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                when (count) {
                    1 -> GoogleFit(
                        this@LampForegroundService,
                        applicationContext
                    )
                    2 -> AccelerometerData(
                        this@LampForegroundService,
                        applicationContext
                    )//Invoke Accelerometer Call
                    3 -> RotationData(
                        this@LampForegroundService,
                        applicationContext
                    ) //Invoke Rotation Call
                    4 -> MagnetometerData(
                        this@LampForegroundService,
                        applicationContext
                    ) //Invoke Magnet Call
                    5 -> GyroscopeData(
                        this@LampForegroundService,
                        applicationContext
                    )//Invoke Gyroscope Call
                    6 -> LocationData(
                        this@LampForegroundService,
                        applicationContext
                    )//Invoke Location
                    7 -> WifiData(
                        this@LampForegroundService,
                        applicationContext
                    )//Invoke WifiData
                    8 -> ScreenStateData(
                        this@LampForegroundService,
                        applicationContext
                    )//Invoke Activity Data
                    9 -> ActivityTransitionData(
                        this@LampForegroundService,
                        applicationContext
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


    private fun invokeAddSensorData(sensorEventDataList: ArrayList<SensorEvent>) {
        if (NetworkUtils.isNetworkAvailable(this) && NetworkUtils.getBatteryPercentage(this@LampForegroundService) > 15) {
            DebugLogs.writeToFile("API Send : ${sensorEventDataList.size}")
            trackSingleEvent("API_Send_${sensorEventDataList.size}")

            val basic = "Basic ${Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                    "https://"
                ).removePrefix("http://")
            )}"
            val state = SensorEventAPI(BuildConfig.HOST).sensorEventCreate(
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

}