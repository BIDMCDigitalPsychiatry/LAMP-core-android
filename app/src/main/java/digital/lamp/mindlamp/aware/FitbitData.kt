package digital.lamp.mindlamp.aware

import android.annotation.SuppressLint
import android.content.Context
import com.mindlamp.Lamp
import com.mindlamp.providers.Lamp_Provider
import com.mindlamp.utils.DatabaseHelper
import digital.lamp.mindlamp.FitbitActivity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class FitbitData constructor(sensorListener: SensorListener, context: Context) {

    val listener: SensorListener = sensorListener
    companion object{
        const val package_name = "com.aware.plugin.fitbit"
        private val TAG = FitbitData::class.java.simpleName
    }
    init {
        try {
            val installedPlugins = context.contentResolver.query(
                Lamp_Provider.Aware_Plugins.CONTENT_URI,
                null,
                null,
                null,
                Lamp_Provider.Aware_Plugins.PLUGIN_NAME + " ASC"
            )
            val plugins = JSONArray(DatabaseHelper.cursorToString(installedPlugins))
            val plugin = plugins[0] as JSONObject
            val packageName = plugin.getString(Lamp_Provider.Aware_Plugins.PLUGIN_PACKAGE_NAME)
            val status = plugin.getInt(Lamp_Provider.Aware_Plugins.PLUGIN_STATUS)

            if ((packageName == FitbitActivity.package_name) && (status == 1)) {
               readHeartBeat(context)
                android.os.Handler().postDelayed({
                    readSteps(context)
                }, 3000)
            } else {
                Lamp.startPlugin(context,
                    package_name
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readSteps(context: Context) {
//        val latestSteps: Cursor? = context.contentResolver.query(
//            Provider.Fitbit_Data.CONTENT_URI,
//            null,
//            Provider.Fitbit_Data.DATA_TYPE + " LIKE 'steps'",
//            null,
//            Provider.Fitbit_Data.TIMESTAMP + " DESC LIMIT 1"
//        )
//
//        if (latestSteps != null && latestSteps.moveToFirst()) {
//            try {
//
//                val stepsJSON =
//                    JSONObject(latestSteps.getString(latestSteps.getColumnIndex(Provider.Fitbit_Data.FITBIT_JSON)))
//
//                val totalSteps: String =
//                    stepsJSON.getJSONArray("activities-steps").getJSONObject(0)
//                        .getString("value") //today's total steps
//
//                val steps: JSONArray = stepsJSON.getJSONObject("activities-steps-intraday")
//                    .getJSONArray("dataset") //contains all of today's step count, per 15 minutes
//
//                val data = DimensionData(
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    totalSteps.toInt(),
//                    null,
//                    null,null,null
//                )
//
//
//            } catch (e : JSONException) {
//                e.printStackTrace()
//                val logEventRequest = LogEventRequest()
//                logEventRequest.message = "Exception Caught Fitbit Steps"
//                LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
//            } catch (e : android.net.ParseException) {
//                e.printStackTrace()
//                val logEventRequest = LogEventRequest()
//                logEventRequest.message = "Exception Caught Fitbit Steps"
//                LogUtils.invokeLogData(Utils.getApplicationName(context),  context.getString(R.string.error), logEventRequest)
//            }
//        }else{
//            val logEventRequest = LogEventRequest()
//            logEventRequest.message = "Null Caught Last Step Data"
//            LogUtils.invokeLogData(Utils.getApplicationName(context),  context.getString(R.string.warning), logEventRequest)
//        }
    }

    @SuppressLint("Recycle")
    private fun readHeartBeat(context: Context) {
//        val latestHr: Cursor? = context.contentResolver.query(
//            Provider.Fitbit_Data.CONTENT_URI,
//            null,
//            Provider.Fitbit_Data.DATA_TYPE + " LIKE 'heartrate'",
//            null,
//            Provider.Fitbit_Data.TIMESTAMP + " DESC LIMIT 1"
//        )
//
//        if (latestHr != null && latestHr.moveToFirst()) {
//            try {
//
//                val hrJSON =
//                    JSONObject(latestHr.getString(latestHr.getColumnIndex(Provider.Fitbit_Data.FITBIT_JSON)))
//
//
//                val restingHR = hrJSON.getJSONArray("activities-heart").getJSONObject(0)
//                    .getJSONObject("value")
//                    .optInt("restingHeartRate", -1) //today's resting heart rate
//
//                val hearts = hrJSON.getJSONObject("activities-heart-intraday")
//                    .getJSONArray("dataset") //contains all of today's heart rate, every 5 seconds
//
//                val data = DimensionData(
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,null,null
//                )
//
//                Log.e(TAG,"Hello :  $hrJSON")
//            } catch (e: JSONException) {
//                e.printStackTrace()
//                val logEventRequest = LogEventRequest()
//                logEventRequest.message = "Aware error Fitbit Heart Beat"
//                LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
//            } catch (e: ParseException) {
//                e.printStackTrace()
//                val logEventRequest = LogEventRequest()
//                logEventRequest.message = "Aware error Fitbit Heart Beat"
//                LogUtils.invokeLogData(Utils.getApplicationName(context),context.getString(R.string.error) , logEventRequest)
//            }
//        }else{
//            val logEventRequest = LogEventRequest()
//            logEventRequest.message = "Aware error Heart Beat Data"
//            LogUtils.invokeLogData(Utils.getApplicationName(context),  context.getString(R.string.warning), logEventRequest)
//        }
//        latestHr!!.close()
    }
}