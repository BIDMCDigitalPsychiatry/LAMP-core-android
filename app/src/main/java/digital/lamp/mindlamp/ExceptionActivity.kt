package digital.lamp.mindlamp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.databinding.ActivityExceptionBinding
import digital.lamp.mindlamp.repository.LampForegroundService

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 * This class is responsible for error handling
 * when an error occurs, error message will shown to users.
 */
class ExceptionActivity : AppCompatActivity() {
    private var errorCode: Int = 0
    private lateinit var binding: ActivityExceptionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExceptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        binding.buttonOk.setOnClickListener {
            if (errorCode == 404) {
                AppState.session.clearData()
                stopLampService()
                GlobalScope.launch(Dispatchers.IO) {
                    val oSensorDao = AppDatabase.getInstance(this@ExceptionActivity).sensorDao()
                    val oActivityDao = AppDatabase.getInstance(this@ExceptionActivity).activityDao()
                    val oAnalyticsDao =
                        AppDatabase.getInstance(this@ExceptionActivity).analyticsDao()
                    oSensorDao.deleteSensorList()
                    oActivityDao.deleteActivityList()
                    oAnalyticsDao.dropAnalyticsList()
                    NotificationManagerCompat.from(this@ExceptionActivity).cancelAll();
                }
            }
            val packageManager: PackageManager = packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            startActivity(mainIntent)
            exitProcess(0)
        }
        if (intent.hasExtra("message")) {
            binding.tvMessage.text = intent.getStringExtra("message")
        } else {
            binding.tvMessage.text = getString(R.string.unexpected_error)
        }

        if (intent.hasExtra("code")) {
            errorCode = intent.getIntExtra("code", 0)
        }

    }

    /**
     * To stop lamp service and cancel broadcast receivers.
     */
    private fun stopLampService() {

        val stopIntent = Intent(this, LampForegroundService::class.java)
        stopService(stopIntent)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmBroadCastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}