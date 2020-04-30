package digital.lamp.mindlamp

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.custom_webview_layout.*

/**
 * Created by ZCO Engineering Dept. on 24,April,2020
 */
class CustomWebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_webview_layout)

        val surveyUrl = intent.getStringExtra("survey_path")
        customWebview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        customWebview.loadUrl(surveyUrl)
    }

}