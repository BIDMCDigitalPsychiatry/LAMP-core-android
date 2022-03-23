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
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.Utils
import kotlinx.android.synthetic.main.activity_webview_overview.*
import kotlinx.android.synthetic.main.custom_webview_layout.*


class NotificationActionActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_overview)

        val surveyUrl = intent.getStringExtra("survey_path")
        val notificationId = intent.getIntExtra("notification_id", AppConstants.NOTIFICATION_ID)

        val oSurveyUrl = BuildConfig.BASE_URL_WEB.dropLast(1)+surveyUrl+"?a="+Utils.toBase64(AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://").removePrefix("http://"))

        DebugLogs.writeToFile("URL : $oSurveyUrl")

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)

        webviewOverview.clearCache(true)
        webviewOverview.clearHistory()
        webviewOverview.settings.javaScriptEnabled = true
        webviewOverview.settings.domStorageEnabled = true
        webviewOverview.settings.allowFileAccess = true
        webviewOverview.loadUrl(oSurveyUrl);

        webviewOverview.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }

        NotificationManagerCompat.from(this).cancel(notificationId)

        btnBack.setOnClickListener {
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