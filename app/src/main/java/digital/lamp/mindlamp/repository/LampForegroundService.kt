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
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.aware.*
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventRequest
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.AppConstants.ALARM_INTERVAL
import digital.lamp.mindlamp.utils.LampLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class LampForegroundService : Service(),
    AwareListener {

    companion object{
        private val TAG = LampForegroundService::class.java.simpleName
    }

    private var isAlarm: Boolean = false
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isAlarm = intent?.extras?.getBoolean("set_alarm")!!
        val notification = LampNotificationManager.showNotification(this, "MindLamp Active Data Collection")

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
        val timer = object: CountDownTimer(60000, 3000) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                when(count){
                    1 -> FitbitData(
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
                }
            }
            override fun onFinish() {
                if(isAlarm) {
                    setAlarmManager()
                }
                stopForeground(true)
                stopSelf()
            }
        }
        timer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun invokeAddSensorData(participantId:String,sensorEventRequest: SensorEventRequest) {
        val homeRepository = HomeRepository()
        GlobalScope.launch(Dispatchers.IO){
            try {
                val addSensorEventResult = homeRepository.addSensorData(participantId,sensorEventRequest)
                LampLog.e(TAG," : $addSensorEventResult")
            }catch (er: Exception){er.printStackTrace()}
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

    override fun getAccelerometerData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getRotationData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getMagneticData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getGyroscopeData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getLocationData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getWifiData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getScreenState(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

    override fun getSmsData(sensorEventRequest: SensorEventRequest) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBluetoothData(sensorEventRequest: SensorEventRequest) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFitbitData(sensorEventRequest: SensorEventRequest) {
        invokeAddSensorData(AppState.session.userId,sensorEventRequest)
    }

}