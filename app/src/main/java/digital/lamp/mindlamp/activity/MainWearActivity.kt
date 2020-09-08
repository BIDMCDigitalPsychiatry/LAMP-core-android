package digital.lamp.mindlamp.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.Wearable
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.app.App
import digital.lamp.mindlamp.aware.*
import digital.lamp.mindlamp.broadcastreceiver.SensorBroadcastReceiver
import digital.lamp.mindlamp.model.CustomMap
import digital.lamp.mindlamp.model.SendTokenRequest
import digital.lamp.mindlamp.model.TokenData
import digital.lamp.mindlamp.model.UserAgent
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.viewmodels.DataViewModel
import digital.lamp.mindlamp.web.WebConstant
import digital.lamp.mindlamp.web.WebServiceResponseData
import digital.lamp.mindlamp.thread.SendToDataLayerThread
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.PermissionCheck
import digital.lamp.mindlamp.utils.Utils
import kotlinx.android.synthetic.main.activity_login_wear.*
import kotlinx.android.synthetic.main.activity_login_wear.pgtext
import kotlinx.android.synthetic.main.activity_login_wear.progressbar
import kotlinx.android.synthetic.main.activity_main_wear.*
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.*
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("DEPRECATION")
class MainWearActivity : FragmentActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, SensorAwareListener {

    var googleApiClient: GoogleApiClient? = null
    var isconnected: Boolean = false
    var valuesMap: DataMap = DataMap()
    private val LOG_TAG: String = "NETWORK"
    private var toast: Toast? = null;
    private var sensorEventDataList: ArrayList<SensorEventData> = ArrayList<SensorEventData>()
    private var arraySensors: ArrayList<Int> = arrayListOf()
    private var dataViewModel: DataViewModel? = null
    val br: BroadcastReceiver = SensorBroadcastReceiver()
    val messageReceiver: MessageReceiver = MessageReceiver()
    var arrsensorvals = arrayOfNulls<SensorEventData>(14)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)

        FirebaseApp.initializeApp(this);
        initialize()
        // Enables Always-on
//        setAmbientEnabled()

        try {
            unregisterReceiver(br)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        registerReceiver(
            br,
            IntentFilter("com.from.notification")
        );

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

        setObserver()

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

    }

    fun initializecontrols() {
        btnLogout.setOnClickListener {

            //logout
            AppState.session.clearData()
            val intent = Intent(this@MainWearActivity, WearLoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    fun initialize() {

        arraySensors.add(Sensor.TYPE_ACCELEROMETER)
        arraySensors.add(Sensor.TYPE_GYROSCOPE)
        arraySensors.add(Sensor.TYPE_GRAVITY)
        arraySensors.add(Sensor.TYPE_LINEAR_ACCELERATION)
        arraySensors.add(Sensor.TYPE_ROTATION_VECTOR)
        arraySensors.add(Sensor.TYPE_RELATIVE_HUMIDITY)
        arraySensors.add(Sensor.TYPE_AMBIENT_TEMPERATURE)
        arraySensors.add(Sensor.TYPE_STEP_DETECTOR)
        arraySensors.add(Sensor.TYPE_STEP_COUNTER)
        arraySensors.add(Sensor.TYPE_HEART_RATE)
        arraySensors.add(Sensor.TYPE_PRESSURE)
        arraySensors.add(Sensor.TYPE_LIGHT)
        arraySensors.add(Sensor.TYPE_PROXIMITY)
        arraySensors.add(Sensor.TYPE_POSE_6DOF)
        arraySensors.add(Sensor.TYPE_STATIONARY_DETECT)
        arraySensors.add(Sensor.TYPE_MOTION_DETECT)
        arraySensors.add(Sensor.TYPE_HEART_BEAT)
        arraySensors.add(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)
    }

    fun setObserver() {
        dataViewModel?.webServiceResponseLiveData?.observe(
            this@MainWearActivity,
            object : Observer<WebServiceResponseData> {
                override fun onChanged(t: WebServiceResponseData?) {

                    Utils.displayProgress(progressbar, pgtext, false, "")
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

    override fun getWatchData(sensorEventData: ArrayList<SensorEventData>) {
        TODO("Not yet implemented")
    }

    override fun getTemperatureData(sensorEventData: SensorEventData) {

        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Temp Set")
        arrsensorvals[0] = sensorEventData
    }

    override fun getProximityData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Proximity Set")
        arrsensorvals[1] = sensorEventData
    }

    override fun getGravityeData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Gravity Set")
        arrsensorvals[2] = sensorEventData
    }

    override fun getLuxData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Lux Set")
        arrsensorvals[3] = sensorEventData
    }

    override fun getLinearyAccelerationData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[4] = sensorEventData
    }

    override fun getPressureData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "LA Set")
        arrsensorvals[5] = sensorEventData
    }

    override fun getAccelerometerData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[6] = sensorEventData
    }

    override fun getRotationData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Rotation Set")
        arrsensorvals[7] = sensorEventData
    }

    override fun getMagneticData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Magnet Set")
        arrsensorvals[8] = sensorEventData
    }

    override fun getGyroscopeData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        Log.d("myTag", "Gyro Set")
        arrsensorvals[9] = sensorEventData
    }

    override fun getLocationData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[10] = sensorEventData
    }

    override fun getWifiData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[11] = sensorEventData
    }

    override fun getScreenState(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[12] = sensorEventData
    }

    override fun getSmsData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[13] = sensorEventData
    }

    override fun getBluetoothData(sensorEventData: SensorEventData) {
        //we are using this approach bcz else we ill get concurrent modification exception
        arrsensorvals[14] = sensorEventData
    }

    override fun onConnectionSuspended(p0: Int) {

        Log.d("SENSOR", "Accuracy changed ")
    }

    fun populatesensorList() {

        arrsensorvals.forEachIndexed { index, item ->
            if (item != null) {
                sensorEventDataList.add(item)


            }

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("SENSOR", "Accuracy changed ")
        isconnected = false
    }


    private fun retrieveUpdateCurrentToken(un: String) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            // Get new Instance ID token
            val token = it.result?.token
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
    }

    inner class MessageReceiver : BroadcastReceiver() {

        fun fillvalues(item: Int) {
            if (item == Sensor.TYPE_ACCELEROMETER) {
                AccelerometerSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            } else if (item == Sensor.TYPE_GYROSCOPE)
                GyroscopeSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            else if (item == Sensor.TYPE_MAGNETIC_FIELD)
                MagnetometerSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            else if (item == Sensor.TYPE_PRESSURE)
                BarometerSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            else if (item == Sensor.TYPE_LIGHT)
                LightSensor(this@MainWearActivity, this@MainWearActivity, dataViewModel!!)
            else if (item == Sensor.TYPE_AMBIENT_TEMPERATURE)
                TemperatureSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            else if (item == Sensor.TYPE_PROXIMITY)
                ProximitySensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            else if (item == Sensor.TYPE_GRAVITY)
                GravitySensor(this@MainWearActivity, this@MainWearActivity, dataViewModel!!)
            else if (item == Sensor.TYPE_LINEAR_ACCELERATION)
                LinearAccelerometerSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )
            else if (item == Sensor.TYPE_ROTATION_VECTOR)
                RotationSensor(
                    this@MainWearActivity,
                    this@MainWearActivity,
                    dataViewModel!!
                )


        }

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

            arrsensorvals = arrayOfNulls(14)

            bringtofrontIfNeeded()

            Log.v("myTag", "Main activity received message: $arraySensors")

            var isfrompush = intent.getBooleanExtra("isfrompush", false)
            // Display message in UI
            for (item in arraySensors) {

                fillvalues(item)
            }

            sendValues(isfrompush)


        }

        fun sendValues(isfrompush: Boolean) {

            Handler().postDelayed({

//                if (isfrompush) {
                if (NetworkUtils.isNetworkAvailable(this@MainWearActivity)) {

                    populatesensorList()
                    Log.d("myTag", "send values to server")
                    dataViewModel!!.addSensoreEvent(
                        AppState.session.username,
                        sensorEventDataList
                    )

                    sensorEventDataList.clear()
                } else {

                    Toast.makeText(
                        this@MainWearActivity,
                        getString(R.string.internet_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                /*} else {
                    SendToDataLayerThread(
                        "/updatedValues",
                        "",
                        valuesMap,
                        googleApiClient
                    ).run()


                }
*/
            }, INTERVAL)

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
