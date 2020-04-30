package digital.lamp.mindlamp.aware

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Communication

/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class SmsData {
    fun addListener(awareListener: AwareListener, context: Context) {
        //Wifi sensor settings
        Aware.setSetting(context, Aware_Preferences.STATUS_COMMUNICATION_EVENTS, true)
        Aware.setSetting(context, Aware_Preferences.STATUS_MESSAGES, true)
        Aware.startCommunication(context)

        //Communication Observer
        Communication.setSensorObserver(object : Communication.AWARESensorObserver {
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
            Aware.stopCommunication(context)
        }, 3000)
    }}