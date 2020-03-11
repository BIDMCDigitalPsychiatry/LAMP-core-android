package digital.lamp.mindlamp.aware

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.aware.Aware
import com.aware.plugin.fitbit.Provider
import com.aware.providers.Aware_Provider
import com.aware.utils.DatabaseHelper
import digital.lamp.mindlamp.FitbitActivity
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventRequest
import digital.lamp.mindlamp.network.model.UserAgent
import digital.lamp.mindlamp.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class FitbitData constructor(awareListener: AwareListener, context: Context) {

    val listener: AwareListener = awareListener
    companion object{
        const val package_name = "com.aware.plugin.fitbit"
        private val TAG = FitbitData::class.java.simpleName
    }
    init {
        try {
            val installedPlugins = context.contentResolver.query(
                Aware_Provider.Aware_Plugins.CONTENT_URI,
                null,
                null,
                null,
                Aware_Provider.Aware_Plugins.PLUGIN_NAME + " ASC"
            )
            val plugins = JSONArray(DatabaseHelper.cursorToString(installedPlugins))
            val plugin = plugins[0] as JSONObject
            val packageName = plugin.getString(Aware_Provider.Aware_Plugins.PLUGIN_PACKAGE_NAME)
            val status = plugin.getInt(Aware_Provider.Aware_Plugins.PLUGIN_STATUS)

            if ((packageName == FitbitActivity.package_name) && (status == 1)) {
               readHeartBeat(context)
                android.os.Handler().postDelayed({
                    readSteps(context)
                }, 3000)
            } else {
                Aware.startPlugin(context,
                    package_name
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readSteps(context: Context) {
        val latestSteps: Cursor? = context.contentResolver.query(
            Provider.Fitbit_Data.CONTENT_URI,
            null,
            Provider.Fitbit_Data.DATA_TYPE + " LIKE 'steps'",
            null,
            Provider.Fitbit_Data.TIMESTAMP + " DESC LIMIT 1"
        )

        if (latestSteps != null && latestSteps.moveToFirst()) {
            try {

                val stepsJSON =
                    JSONObject(latestSteps.getString(latestSteps.getColumnIndex(Provider.Fitbit_Data.FITBIT_JSON)))

                val totalSteps: String =
                    stepsJSON.getJSONArray("activities-steps").getJSONObject(0)
                        .getString("value") //today's total steps

                val steps: JSONArray = stepsJSON.getJSONObject("activities-steps-intraday")
                    .getJSONArray("dataset") //contains all of today's step count, per 15 minutes

                val data = DimensionData(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    totalSteps.toInt(),
                    null,
                    null
                )
                val sensorEventRequest =
                    SensorEventRequest(
                        data,
                        "lamp.steps",
                        System.currentTimeMillis()
                    )
                listener.getFitbitData(sensorEventRequest)

            } catch (e : JSONException) {
                e.printStackTrace()
                val logEventRequest = LogEventRequest("Exception Caught Fitbit Steps", UserAgent(), AppState.session.userId)
                LogUtils.invokeLogData(Utils.getApplicationName(context), "error", logEventRequest)
            } catch (e : android.net.ParseException) {
                e.printStackTrace()
                val logEventRequest = LogEventRequest("Exception Caught Fitbit Steps", UserAgent(), AppState.session.userId)
                LogUtils.invokeLogData(Utils.getApplicationName(context), "error", logEventRequest)
            }
        }else{
            val logEventRequest = LogEventRequest("Null Caught Last Step Data", UserAgent(), AppState.session.userId)
            LogUtils.invokeLogData(Utils.getApplicationName(context), "warning", logEventRequest)
        }
    }

    @SuppressLint("Recycle")
    private fun readHeartBeat(context: Context) {
        val latestHr: Cursor? = context.contentResolver.query(
            Provider.Fitbit_Data.CONTENT_URI,
            null,
            Provider.Fitbit_Data.DATA_TYPE + " LIKE 'heartrate'",
            null,
            Provider.Fitbit_Data.TIMESTAMP + " DESC LIMIT 1"
        )

        if (latestHr != null && latestHr.moveToFirst()) {
            try {

                val hrJSON =
                    JSONObject(latestHr.getString(latestHr.getColumnIndex(Provider.Fitbit_Data.FITBIT_JSON)))


                val restingHR = hrJSON.getJSONArray("activities-heart").getJSONObject(0)
                    .getJSONObject("value")
                    .optInt("restingHeartRate", -1) //today's resting heart rate

                val hearts = hrJSON.getJSONObject("activities-heart-intraday")
                    .getJSONArray("dataset") //contains all of today's heart rate, every 5 seconds

                val data = DimensionData(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "count/min",
                    restingHR
                )
                val sensorEventRequest =
                    SensorEventRequest(
                        data,
                        "lamp.heart_rate",
                        System.currentTimeMillis()
                    )
                listener.getFitbitData(sensorEventRequest)

                Log.e(TAG,"Hello :  $hrJSON")
            } catch (e: JSONException) {
                e.printStackTrace()
                val logEventRequest = LogEventRequest("Exception Caught Fitbit Heart Beat", UserAgent(), AppState.session.userId)
                LogUtils.invokeLogData(Utils.getApplicationName(context), "error", logEventRequest)
            } catch (e: ParseException) {
                e.printStackTrace()
                val logEventRequest = LogEventRequest("Exception Caught Fitbit Heart Beat", UserAgent(), AppState.session.userId)
                LogUtils.invokeLogData(Utils.getApplicationName(context), "error", logEventRequest)
            }
        }else{
            val logEventRequest = LogEventRequest("Null Caught Heart Beat Data", UserAgent(), AppState.session.userId)
            LogUtils.invokeLogData(Utils.getApplicationName(context), "warning", logEventRequest)
        }
        latestHr!!.close()
    }
}