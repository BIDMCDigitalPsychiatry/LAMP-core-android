package digital.lamp.mindlamp.sensor

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import com.mindlamp.Lamp
import com.mindlamp.Lamp_Preferences
import com.mindlamp.Communication

/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class SmsData {
    fun addListener(sensorListener: SensorListener, context: Context) {
        //Wifi sensor settings
        Lamp.setSetting(context, Lamp_Preferences.STATUS_COMMUNICATION_EVENTS, true)
        Lamp.setSetting(context, Lamp_Preferences.STATUS_MESSAGES, true)
        Lamp.startCommunication(context)

        //Communication Observer
        Communication.setSensorObserver(object : Communication.LAMPSensorObserver {
            override fun onCall(data: ContentValues?) {
            }

            override fun onMessage(data: ContentValues?) {
            }

            override fun onBusy(number: String?) {
            }

            override fun onRinging(number: String?) {
            }

            override fun onFree(number: String?) {
            }
         }
        )
        Handler().postDelayed({
            Lamp.stopCommunication(context)
        }, 3000)
    }}