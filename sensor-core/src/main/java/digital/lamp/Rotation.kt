package digital.lamp

import android.app.Service
import android.content.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import digital.lamp.utils.LampConstants

/**
 * LAMP Rotation module
 * - Rotation raw data
 * - Rotation sensor information
 *
 * @author df
 */
class Rotation : Service(), SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //We log current accuracy on the sensor changed event
    }

    override fun onSensorChanged(event: SensorEvent) {

        val currentTimeStamp = System.currentTimeMillis()
        if (currentTimeStamp - LAST_TS < LampConstants.INTERVAL) return

        val rowData = ContentValues()
        rowData.put(TIMESTAMP, currentTimeStamp)
        rowData.put(VALUES_0, event.values[0])
        rowData.put(VALUES_1, event.values[1])
        rowData.put(VALUES_2, event.values[2])
        if (event.values.size == 4) {
            rowData.put(VALUES_3, event.values[3])
        }
        rowData.put(ACCURACY, event.accuracy)

        callback(rowData)
        LAST_TS = currentTimeStamp

    }


    override fun onCreate() {
        super.onCreate()

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mRotation = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorThread = HandlerThread(TAG)
        sensorThread!!.start()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        wakeLock?.acquire()
        sensorHandler = Handler(sensorThread!!.looper)
        if (Lamp.DEBUG) Log.d(TAG, "Rotation service created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorHandler!!.removeCallbacksAndMessages(null)
        mSensorManager!!.unregisterListener(this, mRotation)
        sensorThread!!.quit()
        wakeLock!!.release()
        if (Lamp.DEBUG) Log.d(TAG, "Rotation service terminated...")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
            if (mRotation == null) {
                stopSelf()
            } else {
                val newFrequency = LampConstants.FREQUENCY_ROTATION
                val newThreshold = LampConstants.THRESHOLD_ROTATION
                if (FREQUENCY != newFrequency
                        || THRESHOLD != newThreshold) {
                    sensorHandler!!.removeCallbacksAndMessages(null)
                    mSensorManager!!.unregisterListener(this, mRotation)
                    FREQUENCY = newFrequency
                    THRESHOLD = newThreshold
                }
                mSensorManager!!.registerListener(this, mRotation, newFrequency, sensorHandler)
                if (Lamp.DEBUG) Log.d(TAG, "Rotation service active...")
            }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        const val TIMESTAMP = "timestamp"
        const val VALUES_0 = "double_values_0"
        const val VALUES_1 = "double_values_1"
        const val VALUES_2 = "double_values_2"
        const val VALUES_3 = "double_values_3"
        const val ACCURACY = "accuracy"

        private var TAG = "LAMP::Rotation"
        private var mSensorManager: SensorManager? = null
        private var mRotation: Sensor? = null
        private var sensorThread: HandlerThread? = null
        private var sensorHandler: Handler? = null
        private var wakeLock: PowerManager.WakeLock? = null
        private var LAST_TS: Long = 0
        private var FREQUENCY = -1
        private var THRESHOLD = 0.0

        lateinit var callback : (ContentValues) -> Unit
        @JvmStatic
        fun setSensorObserver(listener: (ContentValues) -> Unit) {
            callback = listener
        }
    }
}