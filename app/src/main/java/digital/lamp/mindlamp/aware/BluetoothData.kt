package digital.lamp.mindlamp.aware

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import com.mindlamp.Lamp
import com.mindlamp.Lamp_Preferences
import com.mindlamp.Bluetooth

/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class BluetoothData {
    fun addListener(sensorListener: SensorListener, context: Context) {
        //Bluetooth sensor settings
        Lamp.setSetting(
            context,
            Lamp_Preferences.FREQUENCY_BLUETOOTH,
            60
        ) //60 Sec
        Lamp.setSetting(context, Lamp_Preferences.STATUS_BLUETOOTH, true)
        Lamp.startBluetooth(context)//Start Bluetooth Sensor

        //Sensor Observer
        Bluetooth.setSensorObserver(object : Bluetooth.LAMPSensorObserver {
            override fun onBLEScanStarted() {
            }

            override fun onBluetoothBLEDetected(data: ContentValues?) {
            }

            override fun onBLEScanEnded() {
            }

            override fun onBluetoothDetected(data: ContentValues?) {
            }

            override fun onBluetoothDisabled() {
            }

            override fun onScanStarted() {
            }

            override fun onScanEnded() {
            }

        })
        Handler().postDelayed({
            Lamp.stopBluetooth(context)
        }, 3000)
    }
}