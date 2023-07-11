package digital.lamp.mindlamp.activity

import android.Manifest
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.Wearable
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.app.TAG
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.broadcastreceiver.SensorBroadcastReceiver
import digital.lamp.mindlamp.databinding.ActivityMainWearBinding
import digital.lamp.mindlamp.model.*
import digital.lamp.mindlamp.sensor.health_services.HealthServiceViewModel
import digital.lamp.mindlamp.service.LampForegroundService
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.viewmodels.DataViewModel
import digital.lamp.mindlamp.web.WebConstant
import digital.lamp.mindlamp.web.WebServiceResponseData
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.*
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainWearActivity : FragmentActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    var googleApiClient: GoogleApiClient? = null
    var isconnected: Boolean = false
    var valuesMap: DataMap = DataMap()
    private val LOG_TAG: String = "NETWORK"
    private var toast: Toast? = null;
    private var sensorEventDataList: ArrayList<SensorEventData> = ArrayList<SensorEventData>()

    private var dataViewModel: DataViewModel? = null
    val br: BroadcastReceiver = SensorBroadcastReceiver()
    val messageReceiver: MessageReceiver = MessageReceiver()

    private lateinit var binding: ActivityMainWearBinding
    private val viewModel: HealthServiceViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWearBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        AppState.session.isLoggedIn = true
        FirebaseApp.initializeApp(this);



        try {
            unregisterReceiver(br)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                when (result) {
                    true -> {
                        LampLog.d("New watch", "Body sensors permission granted")
                        //  viewModel.togglePassiveData(true)
                    }

                    false -> {
                        LampLog.d("New watch", "Body sensors permission not granted")
                        // viewModel.togglePassiveData(false)
                    }
                }
            }
        permissionLauncher.launch(android.Manifest.permission.BODY_SENSORS)
        var permissionLauncher2: ActivityResultLauncher<String> =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                when (result) {
                    true -> {
                        LampLog.d("New watch", "Body sensors permission granted")
                        //  viewModel.togglePassiveData(true)

                    }

                    false -> {
                        LampLog.d("New watch", "Body sensors permission not granted")
                        // viewModel.togglePassiveData(false)

                    }
                }
            }
        permissionLauncher2.launch(android.Manifest.permission.BODY_SENSORS_BACKGROUND)
        registerReceiver(
            br,
            IntentFilter("com.from.notification")
        );

        try {
            doLocationPermissions()
        }
        catch (e:Exception){
            LampLog.e(TAG,"exception Location permission",e)
        }


        // Build a new GoogleApiClient that includes the Wearable API
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        initializecontrols()
        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)

        dataViewModel = ViewModelProviders.of(this@MainWearActivity).get(DataViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            viewModel.latestHeartRate.collect {
                Log.d(TAG, "Health data received heart rate: " + it.toString())

            }
        }

        if (!PermissionCheck.checkAndRequestReadWritePermission(this@MainWearActivity))
            PermissionCheck.requestPermissionScreen(this@MainWearActivity)
        else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                //only need to update once for first time
                if (!AppState.session.isLoggedIn)
                    retrieveUpdateCurrentToken(AppState.session.username)
                //will update from here else the token updation to server wont be done
                AppState.session.isLoggedIn = true

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.internet_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if (!this.isServiceRunning(LampForegroundService::class.java)) {
            startLampService()
        }
    }

    @Suppress("DEPRECATION")
    fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }

    private fun startLampService() {
        val serviceIntent = Intent(this, LampForegroundService::class.java).apply {

            putExtra("set_alarm", false)
            putExtra("set_activity_schedule", false)
            putExtra("notification_id", 0)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }
fun doLocationPermissions() {
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.\

            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.

            }

            else -> {
                // No location access granted.
            }
        }
    }
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION


        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            // You can use the API that requires the permission.

        }


        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
            //  showInContextUI(...)
            this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)


        }

        else -> {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            )
        }
    }
}
    fun initializecontrols() {
        binding.btnLogout.setOnClickListener {
            try {
                Lamp.stopLAMP(this)
            } catch (e: Exception) {
                LampLog.e("lampException", e.message, e)
            }
            try {
                stopService(Intent(this, LampForegroundService::class.java))
            } catch (e: Exception) {
                LampLog.e("lampException", e.message, e)
            }
            try {
                viewModel.unregister()
            } catch (e: Exception) {
                LampLog.e("lampException", "viewModel.unregister() " + e.message, e)
            }

            AppState.session.clearData()
            val intent = Intent(this@MainWearActivity, WearLoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }


    fun setObserver() {
        dataViewModel?.webServiceResponseLiveData?.observe(
            this@MainWearActivity,
            object : Observer<WebServiceResponseData> {
                override fun onChanged(t: WebServiceResponseData?) {

                    Utils.displayProgress(binding.progressbar, binding.pgtext, false, "")
                    Log.d("ACTIVITY", "on Changed")
                    if (null != t?.responseBase) {

                        if (t.responseCode == WebConstant.CODE_SUCCESS) {
                        } else {
                            toast = Toast.makeText(
                                this@MainWearActivity,
                                t?.responseMessage,
                                Toast.LENGTH_SHORT
                            )
                            toast?.show()
                        }
                    } else {

                        toast = Toast.makeText(
                            this@MainWearActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT
                        )
                        toast?.show()
                    }
                }
            })

    }

    override fun onResume() {
        super.onResume()
        googleApiClient?.connect()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        googleApiClient!!.disconnect()
        unregisterReceiver(br)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        valuesMap.clear()
        isconnected = false
        super.onDestroy()
    }

    override fun onConnected(p0: Bundle?) {
        isconnected = true

    }


    override fun onConnectionSuspended(p0: Int) {

        Log.d("SENSOR", "Accuracy changed ")
    }


    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("SENSOR", "Accuracy changed ")
        isconnected = false
    }


    private fun retrieveUpdateCurrentToken(un: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                return@OnCompleteListener
            }

            // Get new FCM registration token

            task.result.let {

                // Get new Instance ID token
                val token = it
                Log.e("FCM", "FCM Token : $token")
                val tokenData = TokenData("login", token.toString(), "Google wearOS", UserAgent())
                val sendTokenRequest =
                    SendTokenRequest(
                        tokenData,
                        "lamp.analytics",
                        System.currentTimeMillis()
                    )

                dataViewModel?.addDeviceToken(un, sendTokenRequest)
            }
        })


    }

    inner class MessageReceiver : BroadcastReceiver() {


        fun bringtofrontIfNeeded() {

            val intent = Intent(this@MainWearActivity, MainWearActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent)
        }

        var INTERVAL: Long = 10000 //millisecs
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {


            var bundle: Bundle? = intent.extras
            var title: String? = bundle?.getString("title")
            var actionval = bundle?.getString("action")
            var content = bundle?.getString("content")

            bringtofrontIfNeeded()

            if (null != content && (null != actionval))
                postResponse(content!!, actionval!!)
        }

        fun postResponse(content: String, action: String) {


            if (NetworkUtils.isNetworkAvailable(this@MainWearActivity)) {

                val notdata = NotificationData(action, content)

                var request: NotificatonRequest = NotificatonRequest(
                    notdata,
                    System.currentTimeMillis(),
                    "lamp.analytics"
                )

                dataViewModel?.addNotificationEvent(AppState.session.username, request)

                sensorEventDataList.clear()
            } else {

                Toast.makeText(
                    this@MainWearActivity,
                    getString(R.string.internet_error),
                    Toast.LENGTH_LONG
                ).show()
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
            AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_CONTACTS] =
                    PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_CONTACTS] == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (NetworkUtils.isNetworkAvailable(this)) {
                            //only need to update once for first time
                            if (!AppState.session.isLoggedIn)
                                retrieveUpdateCurrentToken(AppState.session.username)
                            //will update from here else the token updation to server wont be done
                            AppState.session.isLoggedIn = true

                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.internet_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        //else any one or both the permissions are not granted
                    } else {
                        //Now further we check if used denied permanently or not
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_CONTACTS
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            //Now further we check if used denied permanently or not
                            // case 4 User has denied permission but not permanently
                            showDialogOK(getString(R.string.permission_error),
                                DialogInterface.OnClickListener { _, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> PermissionCheck.requestPermissionScreen(
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
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), okListener)
            .create()
            .show()
    }

}
