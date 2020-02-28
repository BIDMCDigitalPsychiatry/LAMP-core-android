package digital.lamp.mindlamp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.LoginResponse
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.AppConstants.BASE_URL_WEB
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGIN
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGOUT
import digital.lamp.mindlamp.utils.AppConstants.MAIN_PAGE_URL
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import digital.lamp.mindlamp.utils.PermissionCheck.checkAndRequestPermissions
import digital.lamp.mindlamp.utils.Utils
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */

class HomeActivity : AppCompatActivity() {

    companion object{
        private val TAG = HomeActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if(checkAndRequestPermissions(this))
            initializeWebview()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebview() {
        webView.clearCache(true)
        webView.clearHistory()
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        if(AppState.session.isLoggedIn){
            webView.addJavascriptInterface(
                WebAppInterface(
                    this
                ), JAVASCRIPT_OBJ_LOGOUT)
            val url = MAIN_PAGE_URL+ Utils.toBase64(
                AppState.session.token)
            webView.loadUrl(url)
        }else{
            webView.addJavascriptInterface(
                WebAppInterface(
                    this
                ), JAVASCRIPT_OBJ_LOGIN)
            webView.loadUrl(BASE_URL_WEB)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e(TAG, " : $url")
                view.clearCache(true)
                view.clearHistory()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_CALENDAR] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_CONTACTS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_SYNC_SETTINGS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_SYNC_STATS] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_CALENDAR] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_CONTACTS] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_SYNC_SETTINGS] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_SYNC_STATS] == PackageManager.PERMISSION_GRANTED
                    ) {
                        initializeWebview()
                        //else any one or both the permissions are not granted
                    }
                    else{
                        //Now further we check if used denied permanently or not
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SYNC_SETTINGS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SYNC_STATS)
                        ){
                            // case 4 User has denied permission but not permanently
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { _, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions(this)
                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                            // case 5. Permission denied permanently.
                            // You can open Permission setting's page from here now.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }


    private fun startLampService() {
        val serviceIntent = Intent(this, LampForegroundService::class.java).apply {
            putExtra("inputExtra", "Foreground Service Example in Android")
            putExtra("set_alarm",true)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopLampService() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmBroadCastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        alarmManager.cancel(pendingIntent)
    }

    /** Instantiate the interface and set the context  */
    class WebAppInterface(private val homeActivity: HomeActivity) {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun postMessage(jsonString: String) {
            Log.e(TAG," : $jsonString")
            try {
                if(!AppState.session.isLoggedIn) {
                    val loginResponse = Gson().fromJson(jsonString, LoginResponse::class.java)
                    homeActivity.onAuthenticationStateChanged(
                        AuthenticationState.StoredCredentials(
                            loginResponse
                        )
                    )
                }else{
                    homeActivity.onAuthenticationStateChanged(AuthenticationState.SignedOut)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }


    private fun onAuthenticationStateChanged(newState: AuthenticationState) = when (newState) {
        is AuthenticationState.SignedIn -> showSignedIn()
        is AuthenticationState.StoredCredentials -> showSignedIn(newState.credentials)
        AuthenticationState.SignedOut -> showSignedOut()
    }

    private fun showSignedIn() {

    }

    private fun showSignedOut() {
        AppState.session.clearData()
        stopLampService()

    }

    private fun showSignedIn(oLoginResponse: LoginResponse) {

        AppState.session.isLoggedIn = true
        AppState.session.token = oLoginResponse.authorizationToken
        AppState.session.userId = oLoginResponse.identityObject.id

        startLampService()
    }

}
