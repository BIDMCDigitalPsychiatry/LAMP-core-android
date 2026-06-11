package digital.lamp.mindlamp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import digital.lamp.mindlamp.databinding.ActivityExceptionBinding
import digital.lamp.mindlamp.repository.LampForegroundService

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
            finish()
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