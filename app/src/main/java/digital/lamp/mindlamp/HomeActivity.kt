package digital.lamp.mindlamp

import android.Manifest
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
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.*
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
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.TokenData
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.ActivityDao
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.dao.SensorDao
import digital.lamp.mindlamp.model.LoginResponse
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.sheduleing.PowerSaveModeReceiver
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGIN
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGOUT
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.PermissionCheck.checkAndRequestPermissions
import digital.lamp.mindlamp.utils.PermissionCheck.checkSinglePermission
import digital.lamp.mindlamp.utils.Utils.isServiceRunning
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_webview_overview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */

class HomeActivity : AppCompatActivity() {

    private lateinit var oSensorDao: SensorDao
    private lateinit var oActivityDao: ActivityDao
    private lateinit var oAnalyticsDao: AnalyticsDao
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        private val TAG = HomeActivity::class.java.simpleName
        private const val REQUEST_OAUTH_REQUEST_CODE = 1010
        private const val REQUEST_LOCATION_REQUEST_CODE = 1011
        private const val REQUEST_PERMISSION_SETTING = 1012
        private const val REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE = 1013
        var permList = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
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
                if (checkLocationPermission()) {
                    //Fit SignIn Auth
                    fitSignIn()
                    initializeWebview()
                } else {
                    requestLocationPermission()
                }
            }
        }

//        AppState.session.isLoggedIn = true
//        allocateActivitySchedules()
//        startLampService()
//        throw RuntimeException("Test Crash") // Force a crash
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
            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // rationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            requestPermissionAsPerVersion(REQUEST_LOCATION_REQUEST_CODE)
            //   }
            /* if (rationale) {
                 //Never ask again is not checked
                 requestPermissionAsPerVersion(REQUEST_LOCATION_REQUEST_CODE)
             } else {
                 //User checked never ask again
                 showRationaleDialog()
             }*/
        }

    }

    fun showRationaleDialog() {
        android.app.AlertDialog.Builder(this)
                .setTitle(R.string.location_permission)
                .setMessage(R.string.app_disclosure)
                .setPositiveButton(R.string.settings, DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                })
                .setNegativeButton(R.string.ok, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()
                .show()
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
        if (!PermissionCheck.checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            val permList = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
            )
            requestPermissions(permList, locationRequestCode)
        }
    }

    @TargetApi(29)
    private fun checkLocationPermissionAPI29(locationRequestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this) &&

                checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this)) {
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
            /*DebugLogs.writeToFile("checkBackgroundLocationPermissionAPI30 false")
            val rationale =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            if (rationale) {
                DebugLogs.writeToFile("checkBackgroundLocationPermissionAPI30 rationale true")
                //Never ask again is not checked
                //  requestPermissionAsPerVersion(REQUEST_LOCATION_REQUEST_CODE)
                showRationaleDialog()
            } else {
                DebugLogs.writeToFile("checkBackgroundLocationPermissionAPI30 rationale false")
                //User checked never ask again
                //showRationaleDialog()
            }*/
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
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        progressBar.visibility = View.VISIBLE;

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

            //Start Foreground service for retrieving data
            if (!this.isServiceRunning(LampForegroundService::class.java)) {
                startLampService()
            }
        } else {

            url = BuildConfig.BASE_URL_WEB
            webView.loadUrl(url)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e(TAG, " : $url")
                progressBar.visibility = View.GONE;
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                return if (url == null || url.startsWith("http://") || url.startsWith("https://")) false else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } catch (e: java.lang.Exception) {
                    Log.i(TAG, "shouldOverrideUrlLoading Exception:$e")
                    true
                }
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
                /* perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED*/
                perms[Manifest.permission.ACTIVITY_RECOGNITION] = PackageManager.PERMISSION_GRANTED
                /* perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] = PackageManager.PERMISSION_GRANTED*/

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (/*perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                            && */perms[Manifest.permission.ACTIVITY_RECOGNITION] == PackageManager.PERMISSION_GRANTED
                    /* && perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == PackageManager.PERMISSION_GRANTED*/

                    ) {
                        if (checkLocationPermission()) {
                            //Fit SignIn Auth
                            fitSignIn()
                            initializeWebview()
                        } else {
                            requestLocationPermission()
                        }
                        //else any one or both the permissions are not granted
                    } else {
                        //Now further we check if used denied permanently or not
                        if (/*ActivityCompat.shouldShowRequestPermissionRationale(
                                        this,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                )
                                ||*/ ActivityCompat.shouldShowRequestPermissionRationale(
                                        this,
                                        Manifest.permission.ACTIVITY_RECOGNITION
                                ) /*|| ActivityCompat.shouldShowRequestPermissionRationale(
                                        this,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                )*/
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
            REQUEST_LOCATION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fitSignIn()
                    initializeWebview()
                }
            }
            REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE -> {
                checkBackgroundLocationPermissionAPI30()
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
        tokenData.action = "logout"
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
        }
    }

    private fun showSignedIn(oLoginResponse: LoginResponse) {

        AppState.session.isLoggedIn = true
        AppState.session.token = oLoginResponse.authorizationToken
        AppState.session.userId = oLoginResponse.identityObject.id
        if (!oLoginResponse.serverAddress.contains("https://") && !oLoginResponse.serverAddress.contains(
                        "http://"
                )) {
            AppState.session.serverAddress = "https://" + oLoginResponse.serverAddress
        } else AppState.session.serverAddress = oLoginResponse.serverAddress

        //Start Foreground service for retrieving data
        if (!this.isServiceRunning(LampForegroundService::class.java)) {
            startLampService()
        }

        //Updating current user token
        retrieveCurrentToken()

        //Setting User Attributes for Firebase
        firebaseAnalytics.setUserProperty("user_token", oLoginResponse.authorizationToken)
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
        LampLog.e(TAG, "Google Fit Connected")
        trackSingleEvent("Fit_Success")
        DebugLogs.writeToFile("Google Fit Connected")
//        Toast.makeText(this,"Google Fit Connected.",Toast.LENGTH_SHORT).show()
    }

    private fun retrieveCurrentToken() {

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (token != null) {
                Log.e(TAG, "FCM Token : $token")
                DebugLogs.writeToFile("Token : $token")

                val tokenData = TokenData()
                tokenData.action = "login"
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
                        val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                                AppState.session.userId,
                                sendTokenRequest,
                                basic
                        )
                        LampLog.e(TAG, " Token Send Response -  $state")
                    } catch (e: Exception) {
                        DebugLogs.writeToFile("Exception :${e.printStackTrace()}")
                    }
                }
                //Setting User Attributes for Firebase
                firebaseAnalytics.setUserProperty("user_fcm_token", token)
            }

        }
        /*   FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
               if (!it.isSuccessful) {
                   return@addOnCompleteListener
               }
               // Get new Instance ID token
               val token = it.result?.token
               Log.e(TAG, "FCM Token : $token")
               DebugLogs.writeToFile("Token : $token")

               val tokenData = TokenData()
               tokenData.action = "login"
               tokenData.device_token = token.toString()
               tokenData.device_type = "Android"
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

                   val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                           AppState.session.userId,
                           sendTokenRequest,
                           basic
                   )
                   LampLog.e(TAG, " Token Send Response -  $state")
               }
               //Setting User Attributes for Firebase
               firebaseAnalytics.setUserProperty("user_fcm_token", token)
           }*/
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
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
                //Fit SignIn Auth
                fitSignIn()
                initializeWebview()
            }
        }

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle(getString(R.string.app_name))
            setMessage(getString(R.string.app_disclosure))
            setCancelable(false)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
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

    /*override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.hasExtra("survey_path")==true){
            val surveyUrl = intent.getStringExtra("survey_path")
            val notificationId = intent.getIntExtra("notification_id", AppConstants.NOTIFICATION_ID)

            val oSurveyUrl = BuildConfig.BASE_URL_WEB.dropLast(1)+surveyUrl+"?a="+Utils.toBase64(AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://").removePrefix("http://"))

            DebugLogs.writeToFile("URL : $oSurveyUrl")

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)

           *//* webView.clearCache(true)
            webView.clearHistory()
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.allowFileAccess = true*//*
            webView.clearHistory()
            webView.loadUrl(oSurveyUrl);

        *//*    webviewOverview.webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    request.grant(request.resources)
                }
            }
*//*
            NotificationManagerCompat.from(this).cancel(notificationId)

        }
    }*/
}
