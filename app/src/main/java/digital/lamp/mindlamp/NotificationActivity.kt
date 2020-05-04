package digital.lamp.mindlamp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import digital.lamp.mindlamp.utils.AppConstants
import kotlinx.android.synthetic.main.custom_webview_layout.*

/**
 * Created by ZCO Engineering Dept. on 24,April,2020
 */
class NotificationActivity : AppCompatActivity() {

    companion object{
        private val TAG = NotificationActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_webview_layout)

        val surveyUrl = intent.getStringExtra("survey_path")
        val notificationId = intent.getIntExtra("notification_id",AppConstants.NOTIFICATION_ID)

        customWebview.loadUrl(surveyUrl)
        NotificationManagerCompat.from(this).cancel(notificationId)

    }

}