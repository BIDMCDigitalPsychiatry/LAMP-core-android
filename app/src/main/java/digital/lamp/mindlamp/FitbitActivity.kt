package digital.lamp.mindlamp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.database.Cursor
import android.net.ParseException
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aware.Aware
import com.aware.providers.Aware_Provider.Aware_Plugins
import com.aware.utils.DatabaseHelper
import com.aware.utils.PluginsManager
import kotlinx.android.synthetic.main.activity_fitbit.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by ZCO Engineering Dept. on 31,January,2020
 */
class  FitbitActivity : AppCompatActivity() {

    companion object{
        const val package_name = "com.aware.plugin.fitbit"
    }
    @SuppressLint("Recycle", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitbit)

        heartBeatBtn.isEnabled = false

        val installedPlugins = contentResolver.query(
            Aware_Plugins.CONTENT_URI,
            null,
            null,
            null,
            Aware_Plugins.PLUGIN_NAME + " ASC"
        )
        try {
            val plugins = JSONArray(DatabaseHelper.cursorToString(installedPlugins))
            val plugin = plugins[0] as JSONObject
            val packageName = plugin.getString(Aware_Plugins.PLUGIN_PACKAGE_NAME)
            val status = plugin.getInt(Aware_Plugins.PLUGIN_STATUS)

            if ((packageName == package_name) && (status == 1)) {
                fitBtn.isEnabled = false
                dectPlugin.isEnabled = true
                heartBeatBtn.isEnabled = true
            } else {
                fitBtn.isEnabled = true
                dectPlugin.isEnabled = false
                heartBeatBtn.isEnabled = false

            }

            Log.e("Hello : ", " " + plugins.length().toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        fitBtn.setOnClickListener {
            Aware.startPlugin(applicationContext,
                package_name
            )
            fitBtn.isEnabled = false
            dectPlugin.isEnabled = true

        }

        dectPlugin.setOnClickListener {
            Aware.stopPlugin(applicationContext,
                package_name
            )
            fitBtn.isEnabled = true
            dectPlugin.isEnabled = false

        }

        //Button for calculating heart rate
        heartBeatBtn.setOnClickListener {

//            val latestHr: Cursor? = contentResolver.query(
//                Provider.Fitbit_Data.CONTENT_URI,
//                null,
//                Provider.Fitbit_Data.DATA_TYPE + " LIKE 'heartrate'",
//                null,
//                Provider.Fitbit_Data.TIMESTAMP + " DESC LIMIT 1"
//            )
//
//            if (latestHr != null && latestHr.moveToFirst()) {
//                try {
//
//                    val hrJSON =
//                        JSONObject(latestHr.getString(latestHr.getColumnIndex(Provider.Fitbit_Data.FITBIT_JSON)))
//
//
//                    val restingHR = hrJSON.getJSONArray("activities-heart").getJSONObject(0)
//                        .getJSONObject("value")
//                        .optInt("restingHeartRate", -1) //today's resting heart rate
//
//                    val hearts = hrJSON.getJSONObject("activities-heart-intraday")
//                        .getJSONArray("dataset") //contains all of today's heart rate, every 5 seconds
//
//
//                    heartbeatTxt.text = "$restingHR Resting Heart Rate"
//                    Log.e("Hello : ", " $hrJSON")
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                } catch (e: ParseException) {
//                    e.printStackTrace()
//                }
//            }
        }

        //Button For calculating daily step count
        dailyStepBtn.setOnClickListener {

//            val latestSteps: Cursor? = contentResolver.query(
//                Provider.Fitbit_Data.CONTENT_URI,
//                null,
//                Provider.Fitbit_Data.DATA_TYPE + " LIKE 'steps'",
//                null,
//                Provider.Fitbit_Data.TIMESTAMP + " DESC LIMIT 1"
//            )
//
//            if (latestSteps != null && latestSteps.moveToFirst()) {
//                try {
//
//                    val stepsJSON =
//                        JSONObject(latestSteps.getString(latestSteps.getColumnIndex(Provider.Fitbit_Data.FITBIT_JSON)))
//
//                    val totalSteps: String =
//                        stepsJSON.getJSONArray("activities-steps").getJSONObject(0)
//                            .getString("value") //today's total steps
//
//                    val steps: JSONArray = stepsJSON.getJSONObject("activities-steps-intraday")
//                        .getJSONArray("dataset") //contains all of today's step count, per 15 minutes
//
//                    heartbeatTxt.text = "$totalSteps Total Steps"
//
//                    Log.e("Hello : ", " $stepsJSON")
//
//                } catch (e : JSONException) {
//                    e.printStackTrace()
//                } catch (e : ParseException) {
//                    e.printStackTrace()
//                }
//            }
        }

        settings.setOnClickListener {
            var bundledPackage = ""
            val pkg =
                PluginsManager.isInstalled(applicationContext,
                    package_name
                )
            if (pkg != null && pkg.versionName == "bundled") {
                bundledPackage = applicationContext.packageName
            }

            val openSettings = Intent()
            openSettings.component = ComponentName(
                if (bundledPackage.isNotEmpty()) bundledPackage else package_name,
                "$package_name.Settings"
            )
            startActivity(openSettings)
        }
    }
}
