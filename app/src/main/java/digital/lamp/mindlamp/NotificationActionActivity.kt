package digital.lamp.mindlamp

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.databinding.ActivityWebviewOverviewBinding
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.Utils

/**
 * This activity is responsible for notification action.
 * url will load in to webview
 */

class NotificationActionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewOverviewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebviewOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val surveyUrl = intent.getStringExtra("survey_path")
        val notificationId = intent.getIntExtra("notification_id", AppConstants.NOTIFICATION_ID)

        val oSurveyUrl = BuildConfig.BASE_URL_WEB.dropLast(1) + surveyUrl + "?a=" + Utils.toBase64(
            AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://")
                .removePrefix("http://")
        )

        DebugLogs.writeToFile("URL : $oSurveyUrl")

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)

        binding.webviewOverview.clearCache(true)
        binding.webviewOverview.clearHistory()
        binding.webviewOverview.settings.javaScriptEnabled = true
        binding.webviewOverview.settings.domStorageEnabled = true
        binding.webviewOverview.settings.allowFileAccess = true
        binding.webviewOverview.loadUrl(oSurveyUrl)

        binding.webviewOverview.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }

        NotificationManagerCompat.from(this).cancel(notificationId)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}