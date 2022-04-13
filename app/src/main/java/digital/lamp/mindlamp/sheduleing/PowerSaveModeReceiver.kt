package digital.lamp.mindlamp.sheduleing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.models.LowPowerModeData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class PowerSaveModeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        val pm = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
       val isPowerSaveMode = if(pm.isPowerSaveMode) 1 else 0
        GlobalScope.launch(Dispatchers.IO) {
            val lowPowerModeData =
                    LowPowerModeData("Android", isPowerSaveMode,  Utils.getUserAgent(),"lowpowermode" )

            val lowPowerModeEvent =
                SensorEvent(
                    lowPowerModeData,
                    "lamp.analytics", System.currentTimeMillis().toDouble()
                )
            if(AppState.session.isLoggedIn) {
                if (NetworkUtils.isNetworkAvailable(context)) {

                    val basic = "Basic ${
                        Utils.toBase64(
                                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                                        "https://"
                                ).removePrefix("http://")
                        )
                    }"

                    val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                            AppState.session.userId,
                            lowPowerModeEvent,
                            basic
                    )
                    LampLog.e("PowerSaveModeReceiver", "PowerSaveModeReceiver data send -  $state")
                    DebugLogs.writeToFile("PowerSaveModeReceiver Data Send")
                } else {
                    val oAnalytics = Analytics()
                    val gson = Gson()
                    oAnalytics.analyticsData = gson.toJson(lowPowerModeEvent)
                    val oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
                    GlobalScope.async {
                        val id = oAnalyticsDao.insertAnalytics(oAnalytics)
                    }
                }
            }
        }
    }
}