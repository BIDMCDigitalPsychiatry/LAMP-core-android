package digital.lamp.mindlamp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import digital.lamp.mindlamp.auth.MobileSession
import digital.lamp.mindlamp.databinding.ActivityExceptionBinding

import digital.lamp.mindlamp.databinding.CustomWebviewLayoutBinding
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.DebugLogs
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 24,April,2020
 * This activity is responsible for push notification actions click.
 * while click on push notification corresponding url will be load on the web view of app.
 */
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: CustomWebviewLayoutBinding


    companion object {
        private val TAG = NotificationActivity::class.java.simpleName
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomWebviewLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val surveyUrl = intent.getStringExtra("survey_path")
        val notificationId = intent.getIntExtra("notification_id", AppConstants.NOTIFICATION_ID)
        val remoteMessage = intent.getStringExtra("remote_message")

        binding.customWebview.clearCache(true)
        binding.customWebview.clearHistory()
        binding.customWebview.settings.javaScriptEnabled = true
        binding.customWebview.settings.domStorageEnabled = true
        lifecycleScope.launch {
            binding.customWebview.loadUrl(MobileSession.dashboardUrl(surveyUrl ?: "/"))
        }

        NotificationManagerCompat.from(this).cancel(notificationId)
    }
}