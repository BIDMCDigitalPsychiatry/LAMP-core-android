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
 * LAMP Barometer module
 * - Ambient pressure raw data, in mbar
 * - Ambient pressure sensor information
 *
 * @author df
 */
class Barometer : Service(), SensorEventListener {


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //We log current accuracy on the sensor changed event
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTimeStamp = System.currentTimeMillis()
        if (currentTimeStamp - LAST_TS < LampConstants.INTERVAL) return
        if (LAST_VALUE != null && THRESHOLD > 0 && Math.abs(event.values[0] - LAST_VALUE!!) < THRESHOLD) {
            return
        }
        LAST_VALUE = event.values[0]

        // Proceed with saving as usual.
        val rowData = ContentValues()
        rowData.put(TIMESTAMP, currentTimeStamp)
        rowData.put(AMBIENT_PRESSURE, event.values[0])
        rowData.put(ACCURACY, event.accuracy)

        callback(rowData)

        LAST_SAVE = currentTimeStamp
    }

    override fun onCreate() {
        super.onCreate()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mPressure = mSensorManager!!.getDefaultSensor(Sensor.TYPE_PRESSURE)
        sensorThread = HandlerThread(Companion.TAG)
        sensorThread!!.start()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        wakeLock?.acquire()
        if (Lamp.DEBUG) Log.d(Companion.TAG, "Barometer service created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorHandler!!.removeCallbacksAndMessages(null)
        mSensorManager!!.unregisterListener(this, mPressure)
        sensorThread!!.quit()
        wakeLock!!.release()
        if (Lamp.DEBUG) Log.d(Companion.TAG, "Barometer service terminated...")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

            if (mPressure == null) {
                stopSelf()
            } else {
                val newFrequency = LampConstants.FREQUENCY_BAROMETER
                val newThreshold = LampConstants.THRESHOLD_BAROMETER
                if (FREQUENCY != newFrequency
                        || THRESHOLD != newThreshold) {
                    sensorHandler!!.removeCallbacksAndMessages(null)
                    mSensorManager!!.unregisterListener(this, mPressure)
                    FREQUENCY = newFrequency
                    THRESHOLD = newThreshold
                }
                mSensorManager!!.registerListener(this, mPressure, FREQUENCY, sensorHandler)
                LAST_SAVE = System.currentTimeMillis()
                if (Lamp.DEBUG) Log.d(TAG, "Barometer service active: " + FREQUENCY + "ms")
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        const val TIMESTAMP = "timestamp"
        const val AMBIENT_PRESSURE = "double_values_0"
        const val ACCURACY = "accuracy"

        var TAG = "LAMP::Barometer"
        private var mSensorManager: SensorManager? = null
        private var mPressure: Sensor? = null
        private var sensorThread: HandlerThread? = null
        private var sensorHandler: Handler? = null
        private var wakeLock: PowerManager.WakeLock? = null
        private var LAST_VALUE: Float? = null
        private var LAST_TS: Long = 0
        private var LAST_SAVE: Long = 0
        private var FREQUENCY = -1
        private var THRESHOLD = 0.0

        lateinit var callback : (ContentValues) -> Unit
        @JvmStatic
        fun setSensorObserver(listener: (ContentValues) -> Unit) {
            callback = listener
        }
    }
}