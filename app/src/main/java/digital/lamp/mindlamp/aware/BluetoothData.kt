package digital.lamp.mindlamp.aware

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Bluetooth

/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class BluetoothData {
    fun addListener(awareListener: AwareListener, context: Context) {
        //Bluetooth sensor settings
        Aware.setSetting(
            context,
            Aware_Preferences.FREQUENCY_BLUETOOTH,
            60
        ) //60 Sec
        Aware.setSetting(context, Aware_Preferences.STATUS_BLUETOOTH, true)
        Aware.startBluetooth(context)//Start Bluetooth Sensor

        //Sensor Observer
        Bluetooth.setSensorObserver(object : Bluetooth.AWARESensorObserver {
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
            Aware.stopBluetooth(context)
        }, 3000)
    }
}