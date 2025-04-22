package digital.lamp.mindlamp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.TelephonyData
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.TelephonySensor
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.sensor.constants.SensorConstants
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TelephonyWorkManager(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private lateinit var oGson: Gson
    private lateinit var oAnalyticsDao: AnalyticsDao
    override suspend fun doWork(): Result {
        val notification =
            LampNotificationManager.showNotification(
                context,
                context.getString(digital.lamp.mindlamp.R.string.active_data_collection)
            )
        setForeground(ForegroundInfo(1010, notification))
        oGson = Gson()
        oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
        val frequency = inputData.getDouble("frequency", 0.0)
        try {
            Lamp.startTelephony(context)//Start Screen Sensor
            //Sensor Observer
            TelephonySensor.setSensorObserver(object : TelephonySensor.TelephonyListener {
                override fun onIncomingCallEnded(callDuration: Long?) {
                    callDuration?.let {
                        DebugLogs.writeToFile(
                            "Incoming cAll : $callDuration time ${
                                System.currentTimeMillis().toDouble()
                            }"
                        )
                        val telephonyData = TelephonyData(
                            callDuration,
                            SensorConstants.CallState.INCOMING.value
                        )
                        val sensorEventData =
                            SensorEvent(
                                telephonyData,
                                Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                            )
                        getTelephonyData(sensorEventData)
                    }
                }

                override fun onOutgoingCallEnded(callDuration: Long?) {
                    callDuration?.let {
                        DebugLogs.writeToFile(
                            "Outgoing cAll : $callDuration time ${
                                System.currentTimeMillis().toDouble()
                            }"
                        )
                        val telephonyData =
                            TelephonyData(callDuration, SensorConstants.CallState.OUTGOING.value)
                        val sensorEventData =
                            SensorEvent(
                                telephonyData,
                                Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                            )
                        getTelephonyData(sensorEventData)
                    }
                }

                override fun onMissedCall() {
                    DebugLogs.writeToFile(
                        "missed cAll :  time ${
                            System.currentTimeMillis().toDouble()
                        }"
                    )
                    val telephonyData = TelephonyData(0, SensorConstants.CallState.MISSED.value)
                    val sensorEventData =
                        SensorEvent(
                            telephonyData,
                            Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    getTelephonyData(sensorEventData)
                }

            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Result.success()
    }

    private fun getTelephonyData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }
}