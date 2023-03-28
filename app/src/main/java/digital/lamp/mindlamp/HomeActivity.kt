package digital.lamp.mindlamp

import android.Manifest
import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.TrafficStats
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorAPI
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ClientException
import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ServerException
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.SensorSpec
import digital.lamp.lamp_kotlin.lamp_core.models.TokenData
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.ActivityDao
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.dao.SensorDao
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.model.LoginResponse
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.sheduleing.NetworkConnectionLiveData
import digital.lamp.mindlamp.sheduleing.PowerSaveModeReceiver
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGIN
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGOUT
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import digital.lamp.mindlamp.utils.PermissionCheck.checkAndRequestPermissions
import digital.lamp.mindlamp.utils.PermissionCheck.checkSinglePermission
import digital.lamp.mindlamp.utils.PermissionCheck.checkTelephonyPermission
import digital.lamp.mindlamp.utils.Utils.isServiceRunning

import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import javax.net.ssl.*


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */

class HomeActivity : AppCompatActivity() {

    private lateinit var oSensorDao: SensorDao
    private lateinit var oActivityDao: ActivityDao
    private lateinit var oAnalyticsDao: AnalyticsDao
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var mSensorSpecsList: ArrayList<SensorSpecs> = arrayListOf()
    private var isPageLoadedComplete = false


    companion object {
        private val TAG = HomeActivity::class.java.simpleName
        private const val REQUEST_OAUTH_REQUEST_CODE = 1010
        private const val REQUEST_LOCATION_REQUEST_CODE = 1011
        private const val REQUEST_PERMISSION_SETTING = 1012
        private const val REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE = 1013
        var permList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        var backgroundPermission = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        var locationPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

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
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        firebaseAnalytics = Firebase.analytics
        oSensorDao = AppDatabase.getInstance(this).sensorDao()
        oActivityDao = AppDatabase.getInstance(this).activityDao()
        oAnalyticsDao = AppDatabase.getInstance(this).analyticsDao()

        val filter = IntentFilter()
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        registerReceiver(PowerSaveModeReceiver(), filter)

        if (AppState.session.showDisclosureAlert) {
            progressBar.visibility = View.GONE
            populateOnDisclosureARAlert()
        } else {
            if (checkAndRequestPermissions(this)) {
                initializeWebview()
            }
        }
        handleNotification(intent)

    }

    private fun checkLocationPermission(): Boolean {
        val locationPermission = doHaveAllLocationPermissions()
        return locationPermission
    }

    private fun doHaveAllLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this)
        } else {
            checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this) &&
                    checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this)
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var rationale = false
            requestPermissionAsPerVersion(REQUEST_LOCATION_REQUEST_CODE)
        }

    }

    private fun requestPermissionAsPerVersion(locationRequestCode: Int) {
        when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                checkLocationPermissionAPI28(locationRequestCode)
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                checkLocationPermissionAPI29(locationRequestCode)
            }
            Build.VERSION.SDK_INT >= 30 -> {
                checkBackgroundLocationPermissionAPI30()
            }
        }
    }

    @TargetApi(28)
    fun checkLocationPermissionAPI28(locationRequestCode: Int) {
        if (!PermissionCheck.checkSinglePermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                this
            )
        ) {
            val permList = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            requestPermissions(permList, locationRequestCode)
        }
    }

    @TargetApi(29)
    private fun checkLocationPermissionAPI29(locationRequestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this) &&

            checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this)
        ) {
            return
        }
        requestPermissions(permList, locationRequestCode)
    }

    @TargetApi(30)
    private fun checkBackgroundLocationPermissionAPI30() {
        if (checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            if (checkSinglePermission(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    this
                )
            ) {
                return
            }
            requestPermissions(backgroundPermission, REQUEST_LOCATION_REQUEST_CODE)

        } else {
            requestPermissions(locationPermission, REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebview() {
        webView.clearCache(true)
        webView.clearHistory()
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        progressBar.visibility = View.VISIBLE

        webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGOUT)
        webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGIN)

        var url = ""
        if (AppState.session.isLoggedIn) {
            url = BuildConfig.MAIN_PAGE_URL + Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://")
                    .removePrefix(
                        "http://"
                    )
            )
            webView.loadUrl(url)

        } else {

            url = BuildConfig.BASE_URL_WEB
            webView.loadUrl(url)
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if(isPageLoadedComplete){
                    }else{
                        if (progressBar.visibility == View.VISIBLE) {
                            progressBar.visibility = View.GONE
                            webView.isEnabled = true
                            val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                                webView.clearCache(true)
                                webView.clearHistory()
                                dialog.cancel()
                                initializeWebview()
                            }
                            val negativeButtonClick = { dialog:DialogInterface, _:Int ->
                                dialog.cancel()
                                finish()
                            }
                            val builder = AlertDialog.Builder(this@HomeActivity)

                            with(builder)
                            {
                                setTitle(getString(R.string.app_name))
                                setMessage(getString(R.string.txt_unable_to_connect))
                                setCancelable(false)
                                setPositiveButton(
                                    getString(R.string.retry),
                                    DialogInterface.OnClickListener(function = positiveButtonClick)
                                )
                                setNegativeButton(
                                    getString(R.string.cancel),
                                    DialogInterface.OnClickListener(negativeButtonClick)
                                )
                                show()
                            }
                        }
                    }
                }
            }
        }, 20000)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                isPageLoadedComplete = true
                Log.e(TAG, " : $url")
                progressBar.visibility = View.GONE;
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                return if (url == null || url.startsWith("http://") || url.startsWith("https://")) false else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } catch (e: java.lang.Exception) {
                    true
                }
            }
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.ssl_error),
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onReceivedError(
                view: WebView?,
                errorCod: Int,
                description: String,
                failingUrl: String?
            ) {
                Toast.makeText(
                     this@HomeActivity,
                   getString(R.string.txt_no_internet),
                    Toast.LENGTH_LONG
                ).show()
            }

        }


        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
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
                perms[Manifest.permission.ACTIVITY_RECOGNITION] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.ACTIVITY_RECOGNITION] == PackageManager.PERMISSION_GRANTED) {
                        initializeWebview()
                        //else any one or both the permissions are not granted
                    } else {
                        //Now further we check if used denied permanently or not
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            )
                        ) {
                            // case 4 User has denied permission but not permanently
                            showDialogOK(getString(R.string.dialog_message_service_permissions_are_required_for_this_app),
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
            REQUEST_LOCATION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppState.session.isLocationPermissionAllowed = true
                    checkGoogleFit()
                } else {
                    checkGoogleFit()
                }
            }
            REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE -> {
                checkBackgroundLocationPermissionAPI30()
            }
            AppConstants.REQUEST_ID_TELEPHONY_PERMISSIONS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppState.session.isTelephonyPermissionAllowed = true
                }
                val specList = mSensorSpecsList.map { it.spec }
                if (specList.contains(Sensors.GPS.sensor_name)) {
                    checkLocation()
                } else {
                    checkGoogleFit()
                }

            }
        }
    }

    private fun checkLocation() {
        if (checkLocationPermission()) {
            AppState.session.isLocationPermissionAllowed = true
            //Fit SignIn Auth
            checkGoogleFit()
        } else {
            AppState.session.isLocationPermissionAllowed = false
            requestLocationPermission()
        }
    }

    private fun checkGoogleFit() {
        val specList = mSensorSpecsList.map { it.spec }

        if (specList.contains(Sensors.SLEEP.sensor_name) ||
            specList.contains(Sensors.NUTRITION.sensor_name) ||
            specList.contains(Sensors.STEPS.sensor_name) ||
            specList.contains(Sensors.HEART_RATE.sensor_name) ||
            specList.contains(Sensors.BLOOD_GLUCOSE.sensor_name) ||
            specList.contains(Sensors.BLOOD_PRESSURE.sensor_name) ||
            specList.contains(Sensors.OXYGEN_SATURATION.sensor_name) ||
            specList.contains(Sensors.BODY_TEMPERATURE.sensor_name)
        ) {
            fitSignIn()
        } else {
            checkGPSPermission()
            if (!this.isServiceRunning(LampForegroundService::class.java)) {
                startLampService()
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), okListener)
            .create()
            .show()
    }


    private fun startLampService() {
        val serviceIntent = Intent(this, LampForegroundService::class.java).apply {
            putExtra("inputExtra", "Foreground Service Example in Android")
            putExtra("set_alarm", false)
            putExtra("set_activity_schedule", false)
            putExtra("notification_id", 0)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopLampService() {

        val stopIntent = Intent(this, LampForegroundService::class.java)
        stopService(stopIntent)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmBroadCastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
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
                if (loginResponse != null && loginResponse.authorizationToken != null && !loginResponse.deleteCache) {
                    homeActivity.onAuthenticationStateChanged(
                        AuthenticationState.StoredCredentials(
                            loginResponse
                        )
                    )
                } else if (loginResponse.deleteCache) {
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

        val tokenData = TokenData()
        tokenData.type = "logout"
        tokenData.device_type = "Android"
        tokenData.user_agent = Utils.getUserAgent()
        val sendTokenRequest = SensorEvent(
            tokenData,
            "lamp.analytics",
            System.currentTimeMillis().toDouble()
        )

        val basic = "Basic ${
            Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                    "https://"
                ).removePrefix("http://")
            )
        }"

        Lamp.stopLAMP(this)
        stopLampService()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                    AppState.session.userId,
                    sendTokenRequest,
                    basic
                )
                if (state.isNotEmpty()) {
                    //Code for drop DB
                    AppState.session.clearData()
                    LampLog.e(TAG, " Logout Response -  $state")
                }
            } catch (e: Exception) {
                AppState.session.clearData()
            }


            oSensorDao.deleteSensorList()
            oActivityDao.deleteActivityList()
            oAnalyticsDao.dropAnalyticsList()
            NotificationManagerCompat.from(this@HomeActivity).cancelAll();
        }
    }

    private fun showSignedIn(oLoginResponse: LoginResponse) {

        AppState.session.isLoggedIn = true
        AppState.session.token = oLoginResponse.authorizationToken
        AppState.session.userId = oLoginResponse.identityObject.id
        if (!oLoginResponse.serverAddress.contains("https://") && !oLoginResponse.serverAddress.contains(
                "http://"
            )
        ) {
            AppState.session.serverAddress = "https://" + oLoginResponse.serverAddress
        } else AppState.session.serverAddress = oLoginResponse.serverAddress

        //Updating current user token
        retrieveCurrentToken()

        //Setting User Attributes for Firebase
        firebaseAnalytics.setUserProperty("user_token", oLoginResponse.authorizationToken)
        invokeSensorSpecData()
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
                    if (!this.isServiceRunning(LampForegroundService::class.java)) {
                        startLampService()
                    }
                }
            }
            else -> {
                oAuthErrorMsg(requestCode, resultCode)
                if (!this.isServiceRunning(LampForegroundService::class.java)) {
                    startLampService()
                }
            }
        }
    }

    private fun accessGoogleFit() {
        AppState.session.isGoogleFitConnected = true
        LampLog.e(TAG, "Google Fit Connected")
        trackSingleEvent("Fit_Success")
        DebugLogs.writeToFile("Google Fit Connected")
        mSensorSpecsList.forEach {
            if (it.spec == Sensors.GPS.sensor_name) {
                checkGPSPermission()
            }
        }
    }

    private fun retrieveCurrentToken() {

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (token != null) {
                Log.e(TAG, "FCM Token : $token")
                DebugLogs.writeToFile("Token : $token")

                val tokenData = TokenData()
                tokenData.type = "login"
                tokenData.device_token = token.toString()
                tokenData.device_type = "Android"
                tokenData.user_agent = Utils.getUserAgent()
                val sendTokenRequest = SensorEvent(
                    tokenData,
                    "lamp.analytics",
                    System.currentTimeMillis().toDouble()
                )

                val basic = "Basic ${
                    Utils.toBase64(
                        AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                            "https://"
                        ).removePrefix("http://")
                    )
                }"

                GlobalScope.launch {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---
                    try {
                        val state =
                            SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                                AppState.session.userId,
                                sendTokenRequest,
                                basic
                            )
                        LampLog.e(TAG, " Token Send Response -  $state")
                    } catch (e: Exception) {
                        DebugLogs.writeToFile("Exception SensorEventAPI HomeActivity retrieveCurrentToken:${e.printStackTrace()}\n ${e.message}")
                    }
                }
                //Setting User Attributes for Firebase
                firebaseAnalytics.setUserProperty("user_fcm_token", token)
            }

        }
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        AppState.session.isGoogleFitConnected = false
        mSensorSpecsList.forEach {
            if (it.spec == Sensors.GPS.sensor_name) {
                checkGPSPermission()
            }
        }
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        LampLog.e(TAG, message)
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

    private fun populateOnDisclosureARAlert() {
        val positiveButtonClick = { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            AppState.session.showDisclosureAlert = false
            if (checkAndRequestPermissions(this)) {
                initializeWebview()
            }
        }

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle(getString(R.string.app_name))
            setMessage(getString(R.string.app_disclosure))
            setCancelable(false)
            setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }
    }

    override fun onBackPressed() {
        if (webView.copyBackForwardList().getCurrentIndex() > 0) {
            webView.goBack()
        } else {
            super.onBackPressed() // finishes activity
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotification(intent)
    }

    private fun handleNotification(intent: Intent?) {
        hideKeyboard()
        if (intent?.hasExtra("survey_path") == true) {
            val surveyUrl = intent.getStringExtra("survey_path")
            val notificationId = intent.getIntExtra("notification_id", AppConstants.NOTIFICATION_ID)

            val oSurveyUrl =
                BuildConfig.BASE_URL_WEB.dropLast(1) + surveyUrl + "?a=" + Utils.toBase64(
                    AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://")
                        .removePrefix("http://")
                )

            DebugLogs.writeToFile("URL : $oSurveyUrl")

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)

            webView.clearCache(true)
            webView.clearHistory()
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.allowFileAccess = true
            webView.clearHistory()
            webView.loadUrl(oSurveyUrl);

            webView.webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    request.grant(request.resources)
                }
            }

            NotificationManagerCompat.from(this).cancel(notificationId)

        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(webView.getWindowToken(), 0)
    }

    private fun invokeSensorSpecData() {

        if (NetworkUtils.isNetworkAvailable(this)) {
            if (NetworkUtils.getBatteryPercentage(this) > 15) {
                val sensorSpecsList: ArrayList<SensorSpecs> = arrayListOf()
                val basic = "Basic ${
                    Utils.toBase64(
                        AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                            "https://"
                        ).removePrefix("http://")
                    )
                }"

                GlobalScope.launch(Dispatchers.IO) {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
                    try {
                        val state = SensorAPI(AppState.session.serverAddress).sensorAll(
                            AppState.session.userId,
                            basic
                        )
                        val oSensorSpec: SensorSpec? = Gson().fromJson(
                            state.toString(),
                            SensorSpec::class.java
                        )
                        if (oSensorSpec?.data?.isNotEmpty() == true) {
                            AppState.session.isCellularUploadAllowed =
                                oSensorSpec.data.find { it.settings == null || it.settings?.cellular_upload == null || it.settings?.cellular_upload == true } != null
                        }
                        oSensorSpec?.data?.forEach { sensor ->
                            val sensorSpecs = SensorSpecs(
                                null,
                                sensor.id,
                                sensor.spec,
                                sensor.name,
                                sensor.settings?.frequency,
                                sensor.settings?.cellular_upload
                            )
                            sensorSpecsList.add(sensorSpecs)
                        }
                        oSensorDao.deleteSensorList()
                        oSensorDao.insertAllSensors(sensorSpecsList)
                        mSensorSpecsList = sensorSpecsList
                        val specList = mSensorSpecsList.map { it.spec }
                        GlobalScope.launch(Dispatchers.Main) {
                            if (specList.contains(Sensors.TELEPHONY.sensor_name)) {
                                if (checkTelephonyPermission(this@HomeActivity)) {
                                    AppState.session.isTelephonyPermissionAllowed = true
                                    if (specList.contains(Sensors.GPS.sensor_name)) {
                                        checkLocation()
                                    } else {
                                        checkGoogleFit()
                                    }
                                }

                            } else if (specList.contains(Sensors.GPS.sensor_name)) {
                                checkLocation()
                            } else {
                                checkGoogleFit()
                            }
                        }
                        LampLog.e(TAG, " Sensor Spec Size -  ${oSensorDao.getSensorsList().size}")
                    } catch (e: SSLHandshakeException) {
                        GlobalScope.launch(Dispatchers.Main) {

                            showApiErrorAlert(getString(R.string.server_unreachable))
                        }
                    } catch (e: ClientException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.user_not_found), e.statusCode)
                        }


                    } catch (e: ServerException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.something_went_wrong))

                        }
                    } catch (e: UnsupportedOperationException) {
                        GlobalScope.launch(Dispatchers.Main) {

                            showApiErrorAlert(getString(R.string.something_went_wrong_on_server))
                        }
                    } catch (e: HttpException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            var message = Utils.getHttpErrorMessage(e.code(),this@HomeActivity)
                            if (message.isEmpty())
                                message = e.message()
                            showApiErrorAlert(message, e.code())
                        }
                    } catch (e: SocketTimeoutException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }
                    } catch (e: NetworkErrorException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }

                    } catch (e: UnknownHostException) {
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }

                    }
                    catch (e:Exception){
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }
                    }
                }
            }
        } else {

            GlobalScope.launch(Dispatchers.Main) {

                GlobalScope.launch(Dispatchers.Main) {
                    showApiErrorAlert(getString(R.string.txt_no_internet))
                }
            }
        }
    }

    private fun checkGPSPermission() {
        val specList = mSensorSpecsList.map { it.spec }
        if (!isFinishing && checkLocationPermission() && specList.contains(Sensors.GPS.sensor_name)) {
            if (Utils.isGPSEnabled(this)) {

            } else {
                val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }

                val builder = AlertDialog.Builder(this)

                with(builder)
                {
                    setTitle(getString(R.string.app_name))
                    setMessage(getString(R.string.gps_enable))
                    setCancelable(false)
                    setPositiveButton(
                        getString(R.string.ok),
                        DialogInterface.OnClickListener(function = positiveButtonClick)
                    )
                    show()
                }
            }
        }
    }

    private fun showApiErrorAlert(message: String, errorCode: Int = 0) {
        if (!isFinishing) {
            val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                if (errorCode == 404) {
                    GlobalScope.launch(Dispatchers.IO) {
                        AppState.session.clearData()
                        val oSensorDao = AppDatabase.getInstance(this@HomeActivity).sensorDao()
                        val oActivityDao = AppDatabase.getInstance(this@HomeActivity).activityDao()
                        val oAnalyticsDao =
                            AppDatabase.getInstance(this@HomeActivity).analyticsDao()
                        oSensorDao.deleteSensorList()
                        oActivityDao.deleteActivityList()
                        oAnalyticsDao.dropAnalyticsList()
                        NotificationManagerCompat.from(this@HomeActivity).cancelAll();
                    }
                }
                dialog.cancel()

            }

            val builder = AlertDialog.Builder(this)

            with(builder)
            {
                setTitle(getString(R.string.app_name))
                setMessage(message)
                setCancelable(false)
                setPositiveButton(
                    getString(R.string.ok),
                    DialogInterface.OnClickListener(function = positiveButtonClick)
                )
                show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val netwokStatus = NetworkConnectionLiveData(this)
        netwokStatus.observe(this){
            if (it){
                Log.e("eee","net connected")
            }else{
                Log.e("eee","no net connected")
                GlobalScope.launch(Dispatchers.Main) {
                    showApiErrorAlert(getString(R.string.txt_no_internet))
                }
            }
        }
    }
}

