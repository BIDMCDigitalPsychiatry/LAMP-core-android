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
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.LoginResponse
import digital.lamp.mindlamp.network.model.SendTokenRequest
import digital.lamp.mindlamp.network.model.TokenData
import digital.lamp.mindlamp.repository.HomeRepository
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGIN
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGOUT
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.PermissionCheck.checkAndRequestPermissions
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.utils.Utils.isServiceRunning
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object{
        private val TAG = HomeActivity::class.java.simpleName
        private const val REQUEST_OAUTH_REQUEST_CODE = 1010

    }

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CYCLING_WHEEL_RPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_MENSTRUATION, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_VAGINAL_SPOTTING, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CYCLING_PEDALING_CADENCE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_POWER_SAMPLE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        firebaseAnalytics = Firebase.analytics
        if(checkAndRequestPermissions(this)){
            //Fit SignIn Auth
            fitSignIn()
            initializeWebview()
        }

//        AppState.session.isLoggedIn = true
//        startLampService()
//        throw RuntimeException("Test Crash") // Force a crash
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebview() {
        webView.clearCache(true)
        webView.clearHistory()
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        progressBar.visibility = View.VISIBLE;

        webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGOUT)
        webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGIN)

        var url = ""
        if(AppState.session.isLoggedIn){
             url = BuildConfig.MAIN_PAGE_URL+ Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://")
                    .removePrefix(
                        "http://"
                    )
            )
            webView.loadUrl(url)

            //Start Foreground service for retrieving data
            if(!this.isServiceRunning(LampForegroundService::class.java)){
                startLampService()
            }
        }else{

            url = BuildConfig.BASE_URL_WEB
            webView.loadUrl(url)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e(TAG, " : $url")
                progressBar.visibility = View.GONE;
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
                perms[Manifest.permission.ACTIVITY_RECOGNITION] = PackageManager.PERMISSION_GRANTED

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
                        && perms[Manifest.permission.ACTIVITY_RECOGNITION] == PackageManager.PERMISSION_GRANTED
                    ) {
                        //Fit SignIn Auth
                        fitSignIn()
                        initializeWebview()
                        //else any one or both the permissions are not granted
                    } else {
                        //Now further we check if used denied permanently or not
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_CALENDAR
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CAMERA
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_CONTACTS
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_SYNC_SETTINGS
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_SYNC_STATS
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            )
                        ) {
                            // case 4 User has denied permission but not permanently
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { _, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions(
                                            this
                                        )
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
            putExtra("set_alarm", false)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopLampService() {

        val stopIntent = Intent(this, LampForegroundService::class.java)
        stopService(stopIntent)

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
            Log.e(TAG, " : $jsonString")
            try {
                val loginResponse = Gson().fromJson(jsonString, LoginResponse::class.java)
                if(!AppState.session.isLoggedIn && !loginResponse.deleteCache) {
                    homeActivity.onAuthenticationStateChanged(
                        AuthenticationState.StoredCredentials(
                            loginResponse
                        )
                    )
                }else if(loginResponse.deleteCache){
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

        val homeRepository = HomeRepository()
        val tokenData = TokenData()
        tokenData.action = "logout"
        tokenData.device_type = "Android"
        val sendTokenRequest = SendTokenRequest(
            tokenData,
            "lamp.analytics",
            System.currentTimeMillis()
        )
        GlobalScope.launch(Dispatchers.IO){
            try {
                val response = homeRepository.sendTokenData(
                    AppState.session.userId,
                    sendTokenRequest
                )
                AppState.session.clearData()
                if (response.code() == 200)
                    Log.e(TAG, "Token Updated to server")
            }catch (er: Exception){er.printStackTrace()}
        }
        stopLampService()
    }

    private fun showSignedIn(oLoginResponse: LoginResponse) {

        AppState.session.isLoggedIn = true
        AppState.session.token = oLoginResponse.authorizationToken
        AppState.session.userId = oLoginResponse.identityObject.id
        if(!oLoginResponse.serverAddress.contains("https://") && !oLoginResponse.serverAddress.contains(
                "http://"
            )){
            AppState.session.serverAddress = "https://"+oLoginResponse.serverAddress
        }
        else AppState.session.serverAddress = oLoginResponse.serverAddress

        //Start Foreground service for retrieving data
        if(!this.isServiceRunning(LampForegroundService::class.java)){
            startLampService()
        }

        //Updating current user token
        retrieveCurrentToken()

        //Setting User Attributes for Firebase
        firebaseAnalytics.setUserProperty("user_token",oLoginResponse.authorizationToken)
    }

    private fun fitSignIn() {
        if (oAuthPermissionsApproved()) {
            accessGoogleFit()
        } else {
            GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                getGoogleAccount(),
                fitnessOptions
            )
        }
    }

    /**
     * Handles the callback from the OAuth sign in flow, executing the post sign in function
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                    accessGoogleFit()
                }
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }

    private fun accessGoogleFit() {
        LampLog.e(TAG,"Google Fit Connected")
        trackSingleEvent("Fit_Success")
        DebugLogs.writeToFile("Google Fit Connected")
//        Toast.makeText(this,"Google Fit Connected.",Toast.LENGTH_SHORT).show()
    }

    private fun retrieveCurrentToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            // Get new Instance ID token
            val token = it.result?.token
            Log.e(TAG, "FCM Token : $token")
            DebugLogs.writeToFile("Token : $token")
            val homeRepository = HomeRepository()
            val tokenData = TokenData()
            tokenData.action = "login"
            tokenData.device_token = token.toString()
            tokenData.device_type = "Android"
            val sendTokenRequest = SendTokenRequest(
                tokenData,
                "lamp.analytics",
                System.currentTimeMillis()
            )
            GlobalScope.launch(Dispatchers.IO){
                try {
                    val response = homeRepository.sendTokenData(
                        AppState.session.userId,
                        sendTokenRequest
                    )

                    if (response.code() == 200)
                        Log.e(TAG, "Token Updated to server")
                }catch (er: Exception){er.printStackTrace()}
            }
            //Setting User Attributes for Firebase
            firebaseAnalytics.setUserProperty("user_fcm_token",token)
        }
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        LampLog.e(TAG,message)
        DebugLogs.writeToFile(message)
        trackSingleEvent("Fit_ERROR")
    }

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(
        getGoogleAccount(),
        fitnessOptions
    )

    /**
     * Gets a Google account for use in creating the Fitness client.
     */
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    private fun trackSingleEvent(eventName: String) {
        //Firebase Event Tracking
        val params = Bundle()
        firebaseAnalytics.logEvent(eventName, params)
    }
}
