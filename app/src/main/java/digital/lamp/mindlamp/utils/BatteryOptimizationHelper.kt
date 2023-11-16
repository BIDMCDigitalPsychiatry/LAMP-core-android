package digital.lamp.mindlamp.utils

import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState

/**
 * This class represents enable or disable the battery optimization used by the application
 */
class BatteryOptimizationHelper(private val activity: AppCompatActivity) {

    /**
     * check battery optimization is enabled for app
     * if enabled, then ask to user.
     */
    fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = activity.packageName
            val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                    requestBatteryOptimizations()
                }

                val builder = AlertDialog.Builder(activity)

                with(builder)
                {
                    setTitle(activity.getString(R.string.app_name))
                    setMessage(activity.getString(R.string.battery_optimization_info))
                    setCancelable(false)
                    setPositiveButton(
                        activity.getString(R.string.ok),
                        DialogInterface.OnClickListener(function = positiveButtonClick)
                    )
                    show()
                }

            }
        }
    }

    /**
     * request battery optimization for app
     */
    private fun requestBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = android.net.Uri.parse("package:${activity.packageName}")
            activity.startActivity(intent)
        }
    }
}
