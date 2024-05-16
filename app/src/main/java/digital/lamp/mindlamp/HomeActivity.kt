package digital.lamp.mindlamp

import android.Manifest
import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.health.connect.HealthConnectManager
import android.location.LocationManager
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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
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
import digital.lamp.mindlamp.databinding.ActivityHomeBinding
import digital.lamp.mindlamp.model.LoginResponse
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.sensor.healthconnect.viewmodel.HealthConnectViewModel
import digital.lamp.mindlamp.sheduleing.NetworkConnectionLiveData
import digital.lamp.mindlamp.sheduleing.PowerSaveModeReceiver
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.AppConstants.BLUETOOTH_REQUEST_CODE
import digital.lamp.mindlamp.utils.AppConstants.BLUETOOTH_REQUEST_RESULT_CODE
import digital.lamp.mindlamp.utils.AppConstants.HEALTH_CONNECT_PERMISSION_RESULT_CODE
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGIN
import digital.lamp.mindlamp.utils.AppConstants.JAVASCRIPT_OBJ_LOGOUT
import digital.lamp.mindlamp.utils.AppConstants.LOCATION_REQUEST_CODE
import digital.lamp.mindlamp.utils.AppConstants.PERMISSION_REQUEST_CODE
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import digital.lamp.mindlamp.utils.PermissionCheck.checkAndRequestPermissions
import digital.lamp.mindlamp.utils.PermissionCheck.checkSinglePermission
import digital.lamp.mindlamp.utils.PermissionCheck.checkTelephonyPermission
import digital.lamp.mindlamp.utils.Utils.isServiceRunning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.reflect.Field
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import javax.net.ssl.*


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var oSensorDao: SensorDao
    private lateinit var oActivityDao: ActivityDao
    private lateinit var oAnalyticsDao: AnalyticsDao
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var reloadWebpageTimer: Timer? = null
    private var wepPageLoadingTimerIsRunning = false

    private var mSensorSpecsList: ArrayList<SensorSpecs> = arrayListOf()
    private var isPageLoadedComplete = false

    private lateinit var binding: ActivityHomeBinding
    private val permissionChecker by lazy { PermissionChecker(this) }
    private val viewModel: HealthConnectViewModel by viewModels()
    val PERMISSIONS =
        setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(SpeedRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getReadPermission(BasalMetabolicRateRecord::class),
            HealthPermission.getReadPermission(BodyFatRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getReadPermission(BloodGlucoseRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getReadPermission(BodyTemperatureRecord::class),
            HealthPermission.getReadPermission(StepsCadenceRecord::class),
            HealthPermission.getReadPermission(SleepStageRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        )
    companion object {
        private val TAG = HomeActivity::class.java.simpleName
        private const val REQUEST_OAUTH_REQUEST_CODE = 1010
        private const val REQUEST_LOCATION_REQUEST_CODE = 1011
        private const val REQUEST_PERMISSION_SETTING = 1012
        private const val REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE = 1013
        private const val WEBPAGE_RELOAD_INTERVAL_TIMER = 20 * 1000L
        private const val WEBPAGE_BEGINNING_DELAY = 30 * 1000L

        var permList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        var backgroundPermission = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        var locationPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    /**
     * webview client object lazy initialization
     */
    private val myWebViewClient: WebViewClient by lazy {
        object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e(TAG, "webview progress${view.progress}")
                if (view.progress == 100) {
                    isPageLoadedComplete = true
                    Log.e(TAG, " : $url")
                    binding.progressBar.visibility = View.GONE
                    if (wepPageLoadingTimerIsRunning) {
                        reloadWebpageTimer?.cancel()
                    }
                    wepPageLoadingTimerIsRunning = false
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                return if (url == null || url.startsWith("http://") || url.startsWith("https://")) false else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } catch (e: java.lang.Exception) {
                    LampLog.printStackTrace(e)
                    true
                }
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                showApiErrorAlert(getString(R.string.server_unreachable))
            }

            override fun onReceivedError(
                view: WebView?,
                errorCod: Int,
                description: String,
                failingUrl: String?
            ) {
                isPageLoadedComplete = false
                DebugLogs.writeToFile(description)
                if (description.isNotEmpty()) {
                    showApiErrorAlert(getString(R.string.server_unreachable) + " :$description")
                } else {
                    showApiErrorAlert(getString(R.string.server_unreachable))
                }

            }

        }

    }

    /**
     *  Lazily initialize the FitnessOptions using the FitnessOptions.builder()
     */
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

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics
        oSensorDao = AppDatabase.getInstance(this).sensorDao()
        oActivityDao = AppDatabase.getInstance(this).activityDao()
        oAnalyticsDao = AppDatabase.getInstance(this).analyticsDao()
        val filter = IntentFilter()
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        registerReceiver(PowerSaveModeReceiver(), filter)

        if (AppState.session.showDisclosureAlert) {
            binding.progressBar.visibility = View.GONE
            populateOnDisclosureARAlert()
        } else {
            checkAndRequestPermissions(this)
            if (intent.action == "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE" || intent.action == "android.intent.action.VIEW_PERMISSION_USAGE"){
                initializePrivacyPolicyWebview()
            }else {
                initializeWebview()
            }

        }
        handleNotification(intent)
        // Fix for SqlBlobTooBigException
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) // 100MB is the new size
        } catch (e: java.lang.Exception) {
            DebugLogs.writeToFile("Exception: ${e.message}")
        }
        if (!AppState.session.showDisclosureAlert) {
            val batteryOptimizationHelper = BatteryOptimizationHelper(this)
            batteryOptimizationHelper.checkBatteryOptimization()
        }

    }
    /**
     * check location permission
     */
    private fun checkLocationPermission(): Boolean {
        val locationPermission = doHaveAllLocationPermissions()
        return locationPermission
    }

    /**
     * check all permissions are granted
     */
    private fun doHaveAllLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this)
        } else {
            checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this) &&
                    checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this)
        }
    }


    /**
     * Request for location permission
     */
    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var rationale = false
            requestPermissionAsPerVersion(REQUEST_LOCATION_REQUEST_CODE)
        }

    }

    /**
     * request permission as per the android version
     */
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

    /**
     * location permission for api 28
     */
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

    /**
     * location permission for api 29
     */
    @TargetApi(29)
    private fun checkLocationPermissionAPI29(locationRequestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, this) &&

            checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this)
        ) {
            return
        }
        requestPermissions(permList, locationRequestCode)
    }

    /**
     * location permission for api 30
     */
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

    private fun initializePrivacyPolicyWebview(){
        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        binding.progressBar.visibility = View.VISIBLE

        binding.webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGOUT)
        binding.webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGIN)

        var url = BuildConfig.PRIVACY_POLICY_PAGE_URL
        binding.webView.loadUrl(url)

        binding.webView.webViewClient = myWebViewClient
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }
    }
    /**
     * initialize webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebview() {
        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        binding.progressBar.visibility = View.VISIBLE

        binding.webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGOUT)
        binding.webView.addJavascriptInterface(WebAppInterface(this), JAVASCRIPT_OBJ_LOGIN)

        var url = ""
        if (AppState.session.isLoggedIn) {
            url = BuildConfig.MAIN_PAGE_URL + Utils.toBase64(
                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://")
                    .removePrefix(
                        "http://"
                    )
            )
            binding.webView.loadUrl(url)

        } else {

            url = BuildConfig.BASE_URL_WEB
            binding.webView.loadUrl(url)
        }

        if (!isPageLoadedComplete)
            startTimerForReloadWebpage(getString(R.string.txt_unable_to_connect))
        binding.webView.webViewClient = myWebViewClient
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }
    }

    /**
     * reload webpage after some time
     */
    private fun startTimerForReloadWebpage(errorMessage: String) {
        reloadWebpageTimer?.cancel()
        reloadWebpageTimer?.purge()

        val actionTask: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (isPageLoadedComplete) {
                    } else {
                        if (binding.progressBar.visibility == View.VISIBLE) {
                            val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                                if (!isPageLoadedComplete) {
                                    binding.webView.loadUrl("javascript:window.location.reload( true )")
                                }

                            }
                            val negativeButtonClick = { dialog: DialogInterface, _: Int ->
                                binding.progressBar.visibility = View.GONE
                                dialog.cancel()
                                finish()
                            }
                            val builder = AlertDialog.Builder(this@HomeActivity)

                            with(builder)
                            {
                                setTitle(getString(R.string.app_name))
                                setMessage(errorMessage)
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
        }

        wepPageLoadingTimerIsRunning = true
        reloadWebpageTimer = Timer()
        reloadWebpageTimer!!.scheduleAtFixedRate(
            actionTask,
            WEBPAGE_BEGINNING_DELAY,
            WEBPAGE_RELOAD_INTERVAL_TIMER
        )
    }

    /**
     * activity result handler
     */
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                if (intent.action == "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE" || intent.action == "android.intent.action.VIEW_PERMISSION_USAGE"){
                    initializePrivacyPolicyWebview()
                }else {
                    val perms = HashMap<String, Int>()
                    // Initialize the map with both permissions
                    perms[Manifest.permission.ACTIVITY_RECOGNITION] =
                        PackageManager.PERMISSION_GRANTED

                    if (grantResults.isNotEmpty()) {
                        for (i in permissions.indices)
                            perms[permissions[i]] = grantResults[i]
                        // Check for both permissions
                        if (perms[Manifest.permission.ACTIVITY_RECOGNITION] == PackageManager.PERMISSION_GRANTED) {
                            initializeWebview()
                            //else any one or both the permissions are not granted
                        } else {
                            initializeWebview()
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
                                //  DebugLogs.writeToFile("Display settings screen")
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
            REQUEST_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        AppState.session.isLocationPermissionAllowed = true
                        val specList = mSensorSpecsList.map { it.spec }
                        if (specList.contains(Sensors.NEARBY_DEVICES.sensor_name)) {
                            checkLocationAndBluetoothPermission()
                        }
                        checkGoogleFit()
                    } else {
                        checkGoogleFit()
                    }
                }
            }
            REQUEST_LOCATION_ACCESSFINE_REQUEST_CODE -> {
                checkBackgroundLocationPermissionAPI30()
            }
            PERMISSION_REQUEST_CODE -> {
                val specList = mSensorSpecsList.map { it.spec }
                if (specList.contains(Sensors.NEARBY_DEVICES.sensor_name)) {
                    checkLocationAndBluetoothPermission()
                }
            }
            AppConstants.REQUEST_ID_TELEPHONY_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        AppState.session.isTelephonyPermissionAllowed = true
                    }
                    val specList = mSensorSpecsList.map { it.spec }
                    if (specList.contains(Sensors.GPS.sensor_name)) {
                        checkLocation()

                    } else if (specList.contains(Sensors.NEARBY_DEVICES.sensor_name)) {
                        checkLocationAndBluetoothPermission()
                    } else {
                        checkGoogleFit()
                    }
                }

            }

            AppConstants.REQUEST_ID_WIFI_PERMISSIONS -> {
                val specList = mSensorSpecsList.map { it.spec }
                if (specList.contains(Sensors.GPS.sensor_name)) {
                    checkLocation()
                } else {
                    checkGoogleFit()
                }
            }

            BLUETOOTH_REQUEST_CODE -> {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                    requestBluetooth()
                } else {
                    checkGoogleFit()
                }
            }
        }
    }

    /**
     * check location permission is available
     */
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

    /**
     * check google fit sensors
     */
    private fun checkGoogleFit() {
        val specList = mSensorSpecsList.map { it.spec }

        if (specList.contains(Sensors.NEARBY_DEVICES.sensor_name) ||
            specList.contains(Sensors.NUTRITION.sensor_name) ||
            specList.contains(Sensors.STEPS.sensor_name) ||
            specList.contains(Sensors.HEART_RATE.sensor_name) ||
            specList.contains(Sensors.BLOOD_GLUCOSE.sensor_name) ||
            specList.contains(Sensors.BLOOD_PRESSURE.sensor_name) ||
            specList.contains(Sensors.OXYGEN_SATURATION.sensor_name) ||
            specList.contains(Sensors.BODY_TEMPERATURE.sensor_name)
        ) {
           // fitSignIn()
            checkGoogleConnectAvailable()

        } else {
            checkGPSPermission()
            if (!this.isServiceRunning(LampForegroundService::class.java)) {
                startLampService()
            }
        }

    }
    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()
    val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                // Permissions successfully granted
                viewModel.permissionsGranted.value = true
                AppState.session.isGoogleHealthConnectConnected = true
                if (!this.isServiceRunning(LampForegroundService::class.java)) {
                    startLampService()
                }
            } else {
                // Lack of required permissions
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Intent(HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS)
                        .putExtra(Intent.EXTRA_PACKAGE_NAME, BuildConfig.APPLICATION_ID)
                } else {
                    Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
                }
                startActivity(intent)
            }
        }

    fun checkPermissionsAndRun(client: HealthConnectClient) {
        lifecycleScope.launch {
            val granted = client.permissionController.getGrantedPermissions()
            if (granted.containsAll(PERMISSIONS)) {
                // Permissions already granted
                viewModel.permissionsGranted.value = true
                AppState.session.isGoogleHealthConnectConnected = true
                if (!isServiceRunning(LampForegroundService::class.java)) {
                    startLampService()
                }
            } else {
                AppState.session.isGoogleHealthConnectConnected = false
                requestPermissions.launch(PERMISSIONS)
            }
        }
    }

    /**
     *to show dialog
     */
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), okListener)
            .create()
            .show()
    }


    /**
     * to start service
     */
    private fun startLampService() {
        val batteryOptimizationHelper = BatteryOptimizationHelper(this)
        batteryOptimizationHelper.checkBatteryOptimization()
        val serviceIntent = Intent(this, LampForegroundService::class.java).apply {
            putExtra("inputExtra", "Foreground Service Example in Android")
            putExtra("set_alarm", false)
            putExtra("set_activity_schedule", false)
            putExtra("notification_id", 0)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    /**
     * to stop service
     */
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
                LampLog.printStackTrace(ex)
            }

        }
    }


    /**
     * check authentication status and fetch username and password from server
     */
    private fun onAuthenticationStateChanged(newState: AuthenticationState) = when (newState) {
        is AuthenticationState.SignedIn -> showSignedIn()
        is AuthenticationState.StoredCredentials -> showSignedIn(newState.credentials)
        AuthenticationState.SignedOut -> showSignedOut()
    }

    private fun showSignedIn() {
    }

    /**
     * to logout from current user
     */
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
                LampLog.printStackTrace(e)
                AppState.session.clearData()
            }


            oSensorDao.deleteSensorList()
            oActivityDao.deleteActivityList()
            oAnalyticsDao.dropAnalyticsList()
            NotificationManagerCompat.from(this@HomeActivity).cancelAll();
        }
    }

    /**
     * to sign into the user dashboard
     */
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

    /**
     * google fit sign in
     */
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

    private fun  checkGoogleConnectAvailable(){
        val availabilityStatus = HealthConnectClient.getSdkStatus(this, "com.google.android.apps.healthdata")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return // early return as there is no viable integration
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                // Optionally redirect to package installer to find a provider, for example:
                val uriString = "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
                startActivityForResult(Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)

                }, HEALTH_CONNECT_PERMISSION_RESULT_CODE)
            }

            val builder = AlertDialog.Builder(this)

            with(builder)
            {
                setTitle(getString(R.string.app_name))
                setMessage(getString(R.string.not_installed_description))
                setCancelable(false)
                setPositiveButton(
                    getString(R.string.ok),
                    DialogInterface.OnClickListener(function = positiveButtonClick)
                )
                show()
            }
            return
        }else if (availabilityStatus == HealthConnectClient.SDK_AVAILABLE){

            if (viewModel.permissionsGranted.value == true){
                AppState.session.isGoogleHealthConnectConnected = true
                if (!this.isServiceRunning(LampForegroundService::class.java)) {
                    startLampService()
                }
            }else{
                AppState.session.isGoogleHealthConnectConnected = false
                checkPermissionsAndRun(healthConnectClient)
            }
        }

    }

    /**
     * Handles the callback from the OAuth sign in flow, executing the post sign in function
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_OAUTH_REQUEST_CODE -> {
                accessGoogleFit()
                if (!this.isServiceRunning(LampForegroundService::class.java)) {
                    startLampService()
                }
            }
            LOCATION_REQUEST_CODE -> {
                // Check if the user enabled location after the prompt
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val isLocationEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                if (isLocationEnabled) {
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                        requestBluetooth()
                    } else {
                        checkGoogleFit()
                    }
                }

            }
            BLUETOOTH_REQUEST_RESULT_CODE -> {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                    requestBluetooth()
                } else {
                    checkGoogleFit()
                }
            }
            HEALTH_CONNECT_PERMISSION_RESULT_CODE ->{
                if (viewModel.permissionsGranted.value == false) {
                    AppState.session.isGoogleHealthConnectConnected = false
                    checkPermissionsAndRun(healthConnectClient)
                }
                else {
                    AppState.session.isGoogleHealthConnectConnected = true
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

    /**
     *  Access the google fit and checks permission for listed sensors
     */
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

    /**
     * fetch current token from server
     */
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
                        LampLog.printStackTrace(e)
                    }
                }
                //Setting User Attributes for Firebase
                firebaseAnalytics.setUserProperty("user_fcm_token", token)
            }

        }
    }

    /**
     * Handles error message from google fit
     */
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

    /**
     * handles the actions  after the google fit permission approved
     */
    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(
        getGoogleAccount(),
        fitnessOptions
    )

    /**
     * Gets a Google account for use in creating the Fitness client.
     */
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    /**
     * log events on firebase analytics
     */
    private fun trackSingleEvent(eventName: String) {
        //Firebase Event Tracking
        val params = Bundle()
        firebaseAnalytics.logEvent(eventName, params)
    }

    /**
     * To display the app disclosure while first time app opening
     */
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
            setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            show()
        }
    }

    /**
     * handles the app back button press or device back button press
     */
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed() // finishes activity
        }
    }

    /**
     * fetch intent data
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotification(intent)
    }

    /**
     * Handles notifications
     */
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

            binding.webView.clearCache(true)
            binding.webView.clearHistory()
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.domStorageEnabled = true
            binding.webView.settings.allowFileAccess = true
            binding.webView.clearHistory()
            binding.webView.loadUrl(oSurveyUrl);

            binding.webView.webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    request.grant(request.resources)
                }
            }

            NotificationManagerCompat.from(this).cancel(notificationId)

        }
    }

    /**
     * Handles keyboard hiding
     */
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.webView.getWindowToken(), 0)
    }

    /**
     * fetch sensors from server
     */
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
                                    } else if (specList.contains(Sensors.NEARBY_DEVICES.sensor_name)) {
                                        checkLocationAndBluetoothPermission()
                                    } else {
                                        checkGoogleFit()
                                    }

                                }

                            } else if (specList.contains(Sensors.GPS.sensor_name)) {
                                checkLocation()
                            } else if (specList.contains(Sensors.NEARBY_DEVICES.sensor_name)) {
                                checkLocationAndBluetoothPermission()
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
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.user_not_found), e.statusCode)
                        }


                    } catch (e: ServerException) {
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.something_went_wrong))

                        }
                    } catch (e: UnsupportedOperationException) {
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {

                            showApiErrorAlert(getString(R.string.something_went_wrong_on_server))
                        }
                    } catch (e: HttpException) {
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {
                            var message = Utils.getHttpErrorMessage(e.code(), this@HomeActivity)
                            if (message.isEmpty())
                                message = e.message()
                            showApiErrorAlert(message, e.code())
                        }
                    } catch (e: SocketTimeoutException) {
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }
                    } catch (e: NetworkErrorException) {
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }

                    } catch (e: UnknownHostException) {
                        LampLog.printStackTrace(e)
                        GlobalScope.launch(Dispatchers.Main) {
                            showApiErrorAlert(getString(R.string.txt_unable_to_connect))
                        }

                    } catch (e: Exception) {
                        LampLog.printStackTrace(e)
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkLocationAndBluetoothPermission() {
        if (checkPermissions()) {
            // Both WiFi and Bluetooth permissions are granted, perform your task
            // Check if location services are enabled
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isLocationEnabled) {
                requestLocation()
            } else {
                // Check if Bluetooth is enabled
                val bluetoothManager =
                    getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                val bluetoothAdapter = bluetoothManager.adapter
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                    requestBluetooth()
                } else {
                    checkGoogleFit()
                }
            }


        } else {
            // Request the necessary permissions
            requestPermissions()
        }
    }

    private fun requestLocation() {
        val positiveButtonClick = { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle(getString(R.string.app_name))
            setMessage(getString(R.string.location_permission_info_for_wifi_access))
            setCancelable(false)
            setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            show()
        }

    }

    private fun requestBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_RESULT_CODE)
                }
            }else{
                startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_RESULT_CODE)
            }

        } else {
            checkGoogleFit()
        }

    }

    private fun checkPermissions(): Boolean {
        return permissionChecker.hasWifiPermissions() && permissionChecker.hasBluetoothPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!permissionChecker.hasWifiPermissions()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_WIFI_STATE)
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!permissionChecker.hasBluetoothPermissions()) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }


    /**
     * To check GPS permission is allowed or not
     */
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

    /**
     * Displays error messages
     */
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

    /**
     * Check network connection
     */
    override fun onResume() {
        super.onResume()
        val netwokStatus = NetworkConnectionLiveData(this)
        netwokStatus.observe(this) {
            if (it) {
                Log.e("eee", "net connected")
            } else {
                Log.e("eee", "no net connected")
                GlobalScope.launch(Dispatchers.Main) {
                    showApiErrorAlert(getString(R.string.txt_no_internet))
                }
            }
        }
    }
}

