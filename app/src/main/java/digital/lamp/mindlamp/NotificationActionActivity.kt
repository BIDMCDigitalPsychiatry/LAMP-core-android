package digital.lamp.mindlamp

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.Utils
import kotlinx.android.synthetic.main.activity_webview_overview.*

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
        webviewOverview.loadUrl(oSurveyUrl);

        NotificationManagerCompat.from(this).cancel(notificationId)

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}