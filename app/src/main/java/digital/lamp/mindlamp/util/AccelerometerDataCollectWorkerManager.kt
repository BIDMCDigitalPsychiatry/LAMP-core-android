package digital.lamp.mindlamp.util

import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer.Companion.ACCURACY
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer.Companion.TIMESTAMP
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer.Companion.VALUES_0
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer.Companion.VALUES_1
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer.Companion.VALUES_2
import digital.lamp.mindlamp.utils.LampConstants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class AccelerometerDataCollectWorkerManager (
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,":AccelerometerWorker")

    private var accelerometer: Sensor? = null
    private var isDataCollectionPaused = false
    private var collectionIntervalStartTime: Long? = null
    private var pauseIntervalStartTime: Long? = null
    private var lastTimestamp: Long = 0

    override suspend fun doWork(): Result = coroutineScope {
        try {
           /* val notification =
                LampNotificationManager.showNotification(
                    context,
                    context.getString(digital.lamp.mindlamp.R.string.active_data_collection)
                )
            setForeground(ForegroundInfo(1010, notification))*/
            wakeLock.acquire(10 * 60 * 1000L) // 10 minutes max
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            interval = inputData.getLong("interval",LampConstants.INTERVAL)
            Log.d("Accelerometer", "accelerometer ${accelerometer} ✅")
            accelerometer?.let {
                sensorManager.registerListener(this@AccelerometerDataCollectWorkerManager, it, frequency)
                Log.d("total run time", " ${totalRuntime} ✅")
                //delay(totalRuntime)
                delay(5000L)
                sensorManager.unregisterListener(this@AccelerometerDataCollectWorkerManager)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "AccelerometerWorker failed", e)
            Result.failure()
        } finally {
            if (wakeLock.isHeld) wakeLock.release()
        }
    }

    override fun onAccuracyChanged(p0: android.hardware.Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val currentTime = System.currentTimeMillis()
            if (pauseInterval != null && collectionInterval != null) {
                if (!isDataCollectionPaused) {
                    if (collectionIntervalStartTime == null) {
                        collectionIntervalStartTime = currentTime
                    }

                    if (currentTime - lastTimestamp < interval) return
                    if (currentTime - collectionIntervalStartTime!! < collectionInterval!!) {
                        Log.d("send data in if", "$event")
                        sendData(event, currentTime)
                    } else {
                        isDataCollectionPaused = true
                        collectionIntervalStartTime = null
                        pauseIntervalStartTime = currentTime
                        lastTimestamp = 0
                    }
                } else {
                    pauseIntervalStartTime?.let {
                        if (currentTime - it >= pauseInterval!!) {
                            isDataCollectionPaused = false
                            pauseIntervalStartTime = null
                        }
                    }
                }
            } else {
                if (currentTime - lastTimestamp < interval) return
                Log.d("send data in else", "$event")
                sendData(event, currentTime)
            }
        }
    }

    private fun sendData(event: SensorEvent, timestamp: Long) {
        val rowData = ContentValues().apply {
            put(TIMESTAMP, timestamp)
            put(VALUES_0, event.values[0])
            put(VALUES_1, event.values[1])
            put(VALUES_2, event.values[2])
            put(ACCURACY, event.accuracy)
        }
        callback?.invoke(rowData)
        lastTimestamp = timestamp
        Log.d("AccelerometerDataCollectWorkerManager", "$rowData")
    }

    companion object {
        const val TAG = "AccelerometerWorker"

        private var interval: Long = LampConstants.INTERVAL
        private var pauseInterval: Long? = null
        private var collectionInterval: Long? = null
        private var frequency: Int = LampConstants.FREQUENCY_ACCELEROMETER
        private var callback: ((ContentValues) -> Unit)? = null

        @JvmStatic
        fun setSensorObserver(listener: (ContentValues) -> Unit) {
            Log.d("callback", " ${callback} ✅")
            callback = listener
        }

        @JvmStatic
        fun setInterval(value: Long) {
            interval = value
        }

        @JvmStatic
        fun setPauseAndCollectionIntervals(pause: Long, collection: Long) {
            pauseInterval = pause
            collectionInterval = collection
        }

        // Runtime (e.g. max collection duration)
        val totalRuntime = 5 * 60 * 1000L // 5 minutes
    }
}