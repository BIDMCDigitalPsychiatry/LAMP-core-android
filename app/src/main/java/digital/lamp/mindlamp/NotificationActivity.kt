package digital.lamp.mindlamp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.Utils
import kotlinx.android.synthetic.main.custom_webview_layout.*

/**
 * Created by ZCO Engineering Dept. on 24,April,2020
 */
class NotificationActivity : AppCompatActivity() {

    companion object{
        private val TAG = NotificationActivity::class.java.simpleName
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_webview_layout)

        val surveyUrl = intent.getStringExtra("survey_path")
        val notificationId = intent.getIntExtra("notification_id",AppConstants.NOTIFICATION_ID)

        val oSurveyUrl = BuildConfig.BASE_URL_WEB+surveyUrl+"?a="+Utils.toBase64(AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://").removePrefix("http://"))

                customWebview.clearCache(true)
                customWebview.clearHistory()
                customWebview.settings.javaScriptEnabled = true
                customWebview.settings.domStorageEnabled = true
                customWebview.loadUrl(oSurveyUrl)

        NotificationManagerCompat.from(this).cancel(notificationId)

    }

}