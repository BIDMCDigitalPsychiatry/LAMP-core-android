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
 * LAMP Light module
 * - Light raw data
 * - Light sensor information
 *
 * @author df
 */
class Light : Service(), SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //We log current accuracy on the sensor changed event
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTimeStamp = System.currentTimeMillis()
        if (ENFORCE_FREQUENCY && currentTimeStamp < LAST_TS + FREQUENCY / 1000) return

        val rowData = ContentValues()
        rowData.put(TIMESTAMP, currentTimeStamp)
        rowData.put(LIGHT_LUX, event.values[0])
        rowData.put(ACCURACY, event.accuracy)

        callback(rowData)
        LAST_TS = currentTimeStamp
    }

    override fun onCreate() {
        super.onCreate()

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mLight = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorThread = HandlerThread(TAG)
        sensorThread!!.start()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG)
        wakeLock?.acquire()
        sensorHandler = Handler(sensorThread!!.looper)

        if (Lamp.DEBUG) Log.d(TAG, "Light service created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorHandler!!.removeCallbacksAndMessages(null)
        mSensorManager!!.unregisterListener(this, mLight)
        sensorThread!!.quit()
        wakeLock!!.release()
        if (Lamp.DEBUG) Log.d(TAG, "Light service terminated...")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
            if (mLight == null) {
                stopSelf()
            } else {
                val newFrequency = LampConstants.FREQUENCY_LIGHT
                val newThreshold = LampConstants.THRESHOLD_LIGHT
                if (FREQUENCY != newFrequency
                        || THRESHOLD != newThreshold) {
                    sensorHandler!!.removeCallbacksAndMessages(null)
                    mSensorManager!!.unregisterListener(this, mLight)
                    FREQUENCY = newFrequency
                    THRESHOLD = newThreshold
                }
                mSensorManager!!.registerListener(this, mLight, newFrequency, sensorHandler)
                if (Lamp.DEBUG) Log.d(TAG, "Light service active: " + FREQUENCY + "ms")
            }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        const val TIMESTAMP = "timestamp"
        const val LIGHT_LUX = "double_light_lux"
        const val ACCURACY = "accuracy"

        private const val TAG = "LAMP::Light"
        private var mSensorManager: SensorManager? = null
        private var mLight: Sensor? = null
        private var sensorThread: HandlerThread? = null
        private var sensorHandler: Handler? = null
        private var wakeLock: PowerManager.WakeLock? = null
        private var LAST_TS: Long = 0
        private var FREQUENCY = -1
        private var THRESHOLD = 0.0

        // Reject any data points that come in more often than frequency
        private const val ENFORCE_FREQUENCY = false


        lateinit var callback : (ContentValues) -> Unit
        @JvmStatic
        fun setSensorObserver(listener: (ContentValues) -> Unit) {
            callback = listener
        }
    }
}