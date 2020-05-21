package digital.lamp.mindlamp.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import digital.lamp.mindlamp.AlarmBroadCastReceiver
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.aware.*
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.network.model.SensorEventRequest
import digital.lamp.mindlamp.network.model.UserAgent
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.AppConstants.ALARM_INTERVAL
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class LampForegroundService : Service(),
    AwareListener {

    companion object {
        private val TAG = LampForegroundService::class.java.simpleName
        private val TIME_INTERVAL : Long = 3000
        private val MILLISEC_FUTURE : Long = 60000
    }

    private var isAlarm: Boolean = false
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private var sensorEventDataList: ArrayList<SensorEventData> = arrayListOf<SensorEventData>()

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isAlarm = intent?.extras?.getBoolean("set_alarm")!!
        val notification =
            LampNotificationManager.showNotification(this, "MindLamp Active Data Collection")

        startForeground(1, notification)
        collectSensorData()

        return START_NOT_STICKY
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
        val timer = object : CountDownTimer(MILLISEC_FUTURE,TIME_INTERVAL) {
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
                    )
                    9 -> {
                        val sensorEventRequest = SensorEventRequest(System.currentTimeMillis(),sensorEventDataList)
                        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
                    }
                }

            }

            override fun onFinish() {
                if (isAlarm) {
                    setAlarmManager()
                }
                stopForeground(true)
                stopSelf()
            }
        }
        timer.start()

        //check for GPS
        if(!NetworkUtils.isGPSEnabled(this@LampForegroundService)){
            val logEventRequest = LogEventRequest(
                getString(R.string.gps_off),
                UserAgent(),
                AppState.session.userId
            )
            LogUtils.invokeLogData(
                Utils.getApplicationName(this@LampForegroundService),
                "info",
                logEventRequest
            )
        }
        //Battery value
        if(NetworkUtils.getBatteryPercentage(this@LampForegroundService) < 15){
            val logEventRequest = LogEventRequest(
                getString(R.string.battery_low),
                UserAgent(),
                AppState.session.userId
            )
            LogUtils.invokeLogData(
                Utils.getApplicationName(this@LampForegroundService),
                getString(R.string.info),
                logEventRequest
            )
        }
        //Upload Crash Details
        if(AppState.session.crashValue.isNotEmpty()){
            val logEventRequest = LogEventRequest(
                getString(R.string.app_crash)+" : "+AppState.session.crashValue,
                UserAgent(),
                AppState.session.userId
            )
            LogUtils.invokeLogData(
                Utils.getApplicationName(this@LampForegroundService),
                getString(R.string.error),
                logEventRequest
            )
            AppState.session.crashValue = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun invokeAddSensorData(participantId: String, sensorEventRequest: SensorEventRequest) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            val homeRepository = HomeRepository()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = homeRepository.addSensorData(participantId, sensorEventRequest)
                    when (response.code()) {
                        400 -> {
                            val logEventRequest = LogEventRequest(
                                "Network error - 400 Bad Request",
                                UserAgent(),
                                AppState.session.userId
                            )
                            LogUtils.invokeLogData(
                                Utils.getApplicationName(this@LampForegroundService),
                                "error",
                                logEventRequest
                            )
                        }
                        401 -> {
                            val logEventRequest = LogEventRequest(
                                "Network error - 401 Unauthorized",
                                UserAgent(),
                                AppState.session.userId
                            )
                            LogUtils.invokeLogData(
                                Utils.getApplicationName(this@LampForegroundService),
                                "error",
                                logEventRequest
                            )
                        }
                        403 -> {
                            val logEventRequest = LogEventRequest(
                                "Network error - 403 Forbidden",
                                UserAgent(),
                                AppState.session.userId
                            )
                            LogUtils.invokeLogData(
                                Utils.getApplicationName(this@LampForegroundService),
                                "error",
                                logEventRequest
                            )
                        }
                        404 -> {
                            val logEventRequest = LogEventRequest(
                                "Network error - 404 Not Found",
                                UserAgent(),
                                AppState.session.userId
                            )
                            LogUtils.invokeLogData(
                                Utils.getApplicationName(this@LampForegroundService),
                                "error",
                                logEventRequest
                            )
                        }
                        500 -> {
                            val logEventRequest = LogEventRequest(
                                "Network error - 500 Internal Server Error",
                                UserAgent(),
                                AppState.session.userId
                            )
                            LogUtils.invokeLogData(
                                Utils.getApplicationName(this@LampForegroundService),
                                "error",
                                logEventRequest
                            )
                        }

                    }
                } catch (er: Exception) {
                    er.printStackTrace()
                    val logEventRequest = LogEventRequest(
                        "Network error - 500 Internal Server Error",
                        UserAgent(),
                        AppState.session.userId
                    )
                    LogUtils.invokeLogData(
                        Utils.getApplicationName(this@LampForegroundService),
                        "error",
                        logEventRequest
                    )
                }
                stopForeground(true)
                stopSelf()
            }
        }
    }

    fun invokeLogData(origin:String, level:String, logEventRequest: LogEventRequest) {
        val homeRepository = HomeRepository()
        GlobalScope.launch(Dispatchers.IO){
            try {
                val addLogEventResult = homeRepository.addLogData(origin, level, logEventRequest)
                LampLog.e(TAG," : $addLogEventResult")
            }catch (er: Exception){er.printStackTrace()}
        }
    }

    override fun getAccelerometerData(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)
    }

    override fun getRotationData(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)

    }

    override fun getMagneticData(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)
    }

    override fun getGyroscopeData(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)
    }

    override fun getLocationData(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)
    }

    override fun getWifiData(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)
    }

    override fun getScreenState(sensorEventData: SensorEventData) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.add(sensorEventData)
    }

    override fun getSmsData(sensorEventData: SensorEventData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBluetoothData(sensorEventData: SensorEventData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGoogleFitData(sensorEventData: ArrayList<SensorEventData>) {
//        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
        sensorEventDataList.addAll(sensorEventData)
    }

}