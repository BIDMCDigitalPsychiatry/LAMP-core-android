package digital.lamp

import android.accounts.Account
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.UiModeManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import digital.lamp.Accelerometer
import digital.lamp.Barometer
import digital.lamp.Gyroscope

/**
 * Main LAMP framework service. awareContext will start and manage all the services and settings.
 *
 * @author denzil
 */
class Lamp : Service() {
    var AUTHORITY = ""
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        AUTHORITY = "$packageName.provider.aware"
        val foreground = IntentFilter()
        foreground.addAction(ACTION_LAMP_PRIORITY_FOREGROUND)
        foreground.addAction(ACTION_LAMP_PRIORITY_BACKGROUND)
        registerReceiver(foregroundMgr, foreground)
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            stopSelf()
            return
        }

        //Android 8 specific: create notification channels for LAMP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val not_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val aware_channel = NotificationChannel(LAMP_NOTIFICATION_CHANNEL_GENERAL, resources.getString(R.string.app_name), LAMP_NOTIFICATION_IMPORTANCE_GENERAL)
            aware_channel.description = resources.getString(R.string.channel_general_description)
            aware_channel.enableLights(true)
            aware_channel.lightColor = Color.BLUE
            aware_channel.enableVibration(true)
            not_manager.createNotificationChannel(aware_channel)
            val aware_channel_sync = NotificationChannel(LAMP_NOTIFICATION_CHANNEL_DATASYNC, resources.getString(R.string.app_name), LAMP_NOTIFICATION_IMPORTANCE_DATASYNC)
            aware_channel_sync.description = resources.getString(R.string.channel_datasync_description)
            aware_channel_sync.enableLights(false)
            aware_channel_sync.lightColor = Color.BLUE
            aware_channel_sync.enableVibration(false)
            aware_channel_sync.setSound(null, null)
            not_manager.createNotificationChannel(aware_channel_sync)
            val aware_channel_silent = NotificationChannel(LAMP_NOTIFICATION_CHANNEL_SILENT, resources.getString(R.string.app_name), LAMP_NOTIFICATION_IMPORTANCE_SILENT)
            aware_channel_silent.description = resources.getString(R.string.channel_silent_description)
            aware_channel_silent.enableLights(false)
            aware_channel_silent.lightColor = Color.BLUE
            aware_channel_silent.enableVibration(false)
            aware_channel_silent.setSound(null, null)
            not_manager.createNotificationChannel(aware_channel_silent)
        }

        // Start the foreground service only if it's the client or a standalone application
        if (applicationContext.packageName == "com.aware.phone" || applicationContext.applicationContext.resources.getBoolean(R.bool.standalone)) applicationContext.sendBroadcast(Intent(ACTION_LAMP_PRIORITY_FOREGROUND))
        if (DEBUG) Log.d(TAG, "LAMP framework is created!")
        IS_CORE_RUNNING = true
    }

    private val foregroundMgr = Foreground_Priority()

    inner class Foreground_Priority : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //do nothing unless it's the client or a standalone application
            if (!(context.packageName == "com.aware.phone" || context.applicationContext.resources.getBoolean(R.bool.standalone))) return
            if (intent.action.equals(ACTION_LAMP_PRIORITY_FOREGROUND, ignoreCase = true)) {
                if (DEBUG) Log.d(TAG, "Setting LAMP with foreground priority")
                //                foreground(true);
            } else if (intent.action.equals(ACTION_LAMP_PRIORITY_BACKGROUND, ignoreCase = true)) {
                if (DEBUG) Log.d(TAG, "Setting LAMP with background priority")
                //                foreground(false);
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        IS_CORE_RUNNING = false
        try {
            unregisterReceiver(foregroundMgr)
        } catch (e: IllegalArgumentException) {
            //There is no API to check if a broadcast receiver already is registered. Since Aware.java is shared across plugins, the receiver is only registered on the client, not the plugins.
        }
    }

    companion object {
        /**
         * Debug flag (default = false).
         */
        @JvmField
        var DEBUG = false

        /**
         * Debug tag (default = "LAMP").
         */
        @JvmField
        var TAG = "LAMP"

        /**
         * Used to check if the core library is running or not inside individual plugins
         */
        var IS_CORE_RUNNING = false

        /**
         * Broadcasted event: awareContext device information is available
         */
        const val ACTION_LAMP_DEVICE_INFORMATION = "ACTION_LAMP_DEVICE_INFORMATION"

        /**
         * Received broadcast on all modules
         * - Sends the data to the defined webserver
         */
        const val ACTION_LAMP_SYNC_DATA = "ACTION_LAMP_SYNC_DATA"

        /**
         * Received broadcast on all modules
         * - Cleans the data collected on the device
         */
        const val ACTION_LAMP_CLEAR_DATA = "ACTION_LAMP_CLEAR_DATA"

        /**
         * Received broadcast: this broadcast will trigger plugins that implement the CONTEXT_PRODUCER callback.
         */
        const val ACTION_LAMP_CURRENT_CONTEXT = "ACTION_LAMP_CURRENT_CONTEXT"

        /**
         * Stop all plugins
         */
        const val ACTION_LAMP_STOP_PLUGINS = "ACTION_LAMP_STOP_PLUGINS"

        /**
         * Stop all sensors
         */
        const val ACTION_LAMP_STOP_SENSORS = "ACTION_LAMP_STOP_SENSORS"

        /**
         * Set LAMP as a foreground service. This shows a permanent notification on the screen.
         */
        const val ACTION_LAMP_PRIORITY_FOREGROUND = "ACTION_LAMP_PRIORITY_FOREGROUND"

        /**
         * Set LAMP as a standard background service. May be killed or interrupted by Android at any time.
         */
        const val ACTION_LAMP_PRIORITY_BACKGROUND = "ACTION_LAMP_PRIORITY_BACKGROUND"

        /**
         * Used to check users' compliance in a study
         */
        const val ACTION_LAMP_PLUGIN_INSTALLED = "ACTION_LAMP_PLUGIN_INSTALLED"
        const val ACTION_LAMP_PLUGIN_UNINSTALLED = "ACTION_LAMP_PLUGIN_UNINSTALLED"
        const val EXTRA_PLUGIN = "extra_plugin"

        /**
         * Used by Plugin Manager to refresh UI
         */
        const val ACTION_LAMP_UPDATE_PLUGINS_INFO = "ACTION_LAMP_UPDATE_PLUGINS_INFO"

        /**
         * Used when quitting a study. This will reset the device to default settings.
         */
        const val ACTION_QUIT_STUDY = "ACTION_QUIT_STUDY"

        /**
         * Used by the LAMP watchdog
         */
        private const val ACTION_LAMP_KEEP_ALIVE = "ACTION_LAMP_KEEP_ALIVE"

        /**
         * Used by the compliance check scheduler
         */
        private const val ACTION_LAMP_STUDY_COMPLIANCE = "ACTION_LAMP_STUDY_COMPLIANCE"

        /**
         * Notification ID for LAMP service as foreground (to handle Doze, Android O battery optimizations)
         */
        const val LAMP_FOREGROUND_SERVICE = 220882

        /**
         * Android 8 notification channels support
         */
        const val LAMP_NOTIFICATION_CHANNEL_GENERAL = "LAMP_NOTIFICATION_CHANNEL_GENERAL"
        const val LAMP_NOTIFICATION_CHANNEL_SILENT = "LAMP_NOTIFICATION_CHANNEL_SILENT"
        const val LAMP_NOTIFICATION_CHANNEL_DATASYNC = "LAMP_NOTIFICATION_CHANNEL_DATASYNC"
        const val LAMP_NOTIFICATION_IMPORTANCE_GENERAL = NotificationManager.IMPORTANCE_HIGH
        const val LAMP_NOTIFICATION_IMPORTANCE_SILENT = NotificationManager.IMPORTANCE_MIN
        const val LAMP_NOTIFICATION_IMPORTANCE_DATASYNC = NotificationManager.IMPORTANCE_LOW
        private var accelerometerSrv: Intent? = null
        private var activityTransitionSrv: Intent? = null
        private var locationsSrv: Intent? = null
        private var screenSrv: Intent? = null
        private val networkSrv: Intent? = null
        private var gyroSrv: Intent? = null
        private var wifiSrv: Intent? = null
        private var rotationSrv: Intent? = null
        private var lightSrv: Intent? = null
        private val proximitySrv: Intent? = null
        private var magnetoSrv: Intent? = null
        private var barometerSrv: Intent? = null
        private var linear_accelSrv: Intent? = null
        private var significantSrv: Intent? = null
        private val websocket: Intent? = null

        /**
         * Variable for the Doze ignore list
         */
        private const val LAMP_BATTERY_OPTIMIZATION_ID = 567567

        /**
         * Holds a reference to the LAMP account, automatically restore in each plugin.
         */
        private val aware_account: Account? = null

        // set sound/vibration/priority, mainly for android v7 and older as these are handled by channel in 8+
        // TODO potentially add other variables here in the future (e.g., icon, contentTitle, etc.)
        fun setNotificationProperties(builder: NotificationCompat.Builder, notificationImportance: Int): NotificationCompat.Builder {
            return when (notificationImportance) {
                LAMP_NOTIFICATION_IMPORTANCE_DATASYNC -> {
                    builder.setSound(null)
                    builder.setVibrate(null)
                    // priority low but still visible
                    builder.priority = NotificationCompat.PRIORITY_LOW
                    builder
                }
                LAMP_NOTIFICATION_IMPORTANCE_SILENT -> {
                    builder.setSound(null)
                    builder.setVibrate(null)
                    // priority lowest
                    builder.priority = NotificationCompat.PRIORITY_MIN
                    builder
                }
                LAMP_NOTIFICATION_IMPORTANCE_GENERAL -> {
                    // default sound and vibration with HIGH priority
                    builder.priority = NotificationCompat.PRIORITY_HIGH
                    builder
                }
                else -> builder
            }
        }

        /**
         * Identifies if the device is a watch or a phone.
         *
         * @param c
         * @return boolean
         */
        fun is_watch(c: Context): Boolean {
            val uiManager = c.getSystemService(UI_MODE_SERVICE) as UiModeManager
            return uiManager.currentModeType == Configuration.UI_MODE_TYPE_WATCH
        }

        /**
         * Stop all services
         *
         * @param context
         */
        fun stopLAMP(context: Context?) {
            if (context == null) return
            val aware = Intent(context, Lamp::class.java)
            context.stopService(aware)
            stopAccelerometer(context)
            stopLocations(context)
            stopScreen(context)
            stopGyroscope(context)
            stopWiFi(context)
            stopRotation(context)
            stopLight(context)
            stopMagnetometer(context)
            stopBarometer(context)
            stopLinearAccelerometer(context)
            stopActivityTransition(context)
        }

        /**
         * Start Linear Accelerometer module
         */
        fun startLinearAccelerometer(context: Context?) {
            if (context == null) return
            if (linear_accelSrv == null) linear_accelSrv = Intent(context, LinearAccelerometer::class.java)
            context.startService(linear_accelSrv)
        }

        /**
         * Stop Linear Accelerometer module
         */
        private fun stopLinearAccelerometer(context: Context?) {
            if (context == null) return
            if (linear_accelSrv != null) context.stopService(linear_accelSrv)
        }

        /**
         * Start Barometer module
         */
        fun startBarometer(context: Context?) {
            if (context == null) return
            if (barometerSrv == null) barometerSrv = Intent(context, Barometer::class.java)
            context.startService(barometerSrv)
        }

        /**
         * Stop Barometer module
         */
        private fun stopBarometer(context: Context?) {
            if (context == null) return
            if (barometerSrv != null) context.stopService(barometerSrv)
        }

        /**
         * Start Magnetometer module
         */
        fun startMagnetometer(context: Context?) {
            if (context == null) return
            if (magnetoSrv == null) magnetoSrv = Intent(context, Magnetometer::class.java)
            context.startService(magnetoSrv)
        }

        /**
         * Stop Magnetometer module
         */
        private fun stopMagnetometer(context: Context?) {
            if (context == null) return
            if (magnetoSrv != null) context.stopService(magnetoSrv)
        }

        /**
         * Start Light module
         */
        fun startLight(context: Context?) {
            if (context == null) return
            if (lightSrv == null) lightSrv = Intent(context, Light::class.java)
            context.startService(lightSrv)
        }

        /**
         * Stop Light module
         */
        private fun stopLight(context: Context?) {
            if (context == null) return
            if (lightSrv != null) context.stopService(lightSrv)
        }

        /**
         * Start Rotation module
         */
        fun startRotation(context: Context?) {
            if (context == null) return
            if (rotationSrv == null) rotationSrv = Intent(context, Rotation::class.java)
            context.startService(rotationSrv)
        }

        /**
         * Stop Rotation module
         */
        private fun stopRotation(context: Context?) {
            if (context == null) return
            if (rotationSrv != null) context.stopService(rotationSrv)
        }

        /**
         * Start the WiFi module
         */
        fun startWiFi(context: Context?) {
            if (context == null) return
            if (wifiSrv == null) wifiSrv = Intent(context, WiFi::class.java)
            context.startService(wifiSrv)
        }

        private fun stopWiFi(context: Context?) {
            if (context == null) return
            if (wifiSrv != null) context.stopService(wifiSrv)
        }

        /**
         * Start the gyroscope module
         */
        fun startGyroscope(context: Context?) {
            if (context == null) return
            if (gyroSrv == null) gyroSrv = Intent(context, Gyroscope::class.java)
            context.startService(gyroSrv)
        }

        /**
         * Stop the gyroscope module
         */
        private fun stopGyroscope(context: Context?) {
            if (context == null) return
            if (gyroSrv != null) context.stopService(gyroSrv)
        }

        /**
         * Start the accelerometer module
         */
        fun startAccelerometer(context: Context?) {
            if (context == null) return
            if (accelerometerSrv == null) accelerometerSrv = Intent(context, Accelerometer::class.java)
            context.startService(accelerometerSrv)
        }

        /**
         * Stop the accelerometer module
         */
        private fun stopAccelerometer(context: Context?) {
            if (context == null) return
            if (accelerometerSrv != null) context.stopService(accelerometerSrv)
        }

        /**
         * Start the accelerometer module
         */
        fun startActivityTransition(context: Context?) {
            if (context == null) return
            if (activityTransitionSrv == null) activityTransitionSrv = Intent(context, ActivityTransitions::class.java)
            context.startService(activityTransitionSrv)
        }

        /**
         * Stop the accelerometer module
         */
        private fun stopActivityTransition(context: Context?) {
            if (context == null) return
            if (activityTransitionSrv != null) context.stopService(activityTransitionSrv)
        }

        /**
         * Start the locations module
         */
        fun startLocations(context: Context?) {
            if (context == null) return
            if (locationsSrv == null) locationsSrv = Intent(context, Locations::class.java)
            context.startService(locationsSrv)
        }

        /**
         * Stop the locations module
         */
        private fun stopLocations(context: Context?) {
            if (context == null) return
            if (locationsSrv != null) context.stopService(locationsSrv)
        }

        /**
         * Start the screen module
         */
        fun startScreen(context: Context?) {
            if (context == null) return
            if (screenSrv == null) screenSrv = Intent(context, Screen::class.java)
            context.startService(screenSrv)
        }

        /**
         * Stop the screen module
         */
        private fun stopScreen(context: Context?) {
            if (context == null) return
            if (screenSrv != null) context.stopService(screenSrv)
        }
    }
}