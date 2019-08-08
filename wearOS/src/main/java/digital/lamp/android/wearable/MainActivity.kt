package digital.lamp.android.wearable

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : WearableActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        this.webView = this.findViewById(R.id.webView)
        this.webView!!.settings.javaScriptEnabled = true
        this.webView!!.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        this.webView!!.loadUrl("https://google.com/")
    }
}
