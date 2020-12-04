
package com.mindlamp;

import android.accounts.Account;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;

/**
 * Main LAMP framework service. awareContext will start and manage all the services and settings.
 *
 * @author denzil
 */
public class Lamp extends Service {
    /**
     * Debug flag (default = false).
     */
    public static boolean DEBUG = false;

    /**
     * Debug tag (default = "LAMP").
     */
    public static String TAG = "LAMP";

    /**
     * Used to check if the core library is running or not inside individual plugins
     */
    public static boolean IS_CORE_RUNNING = false;

    /**
     * Broadcasted event: awareContext device information is available
     */
    public static final String ACTION_LAMP_DEVICE_INFORMATION = "ACTION_LAMP_DEVICE_INFORMATION";

    /**
     * Received broadcast on all modules
     * - Sends the data to the defined webserver
     */
    public static final String ACTION_LAMP_SYNC_DATA = "ACTION_LAMP_SYNC_DATA";

    /**
     * Received broadcast on all modules
     * - Cleans the data collected on the device
     */
    public static final String ACTION_LAMP_CLEAR_DATA = "ACTION_LAMP_CLEAR_DATA";

    /**
     * Received broadcast: this broadcast will trigger plugins that implement the CONTEXT_PRODUCER callback.
     */
    public static final String ACTION_LAMP_CURRENT_CONTEXT = "ACTION_LAMP_CURRENT_CONTEXT";

    /**
     * Stop all plugins
     */
    public static final String ACTION_LAMP_STOP_PLUGINS = "ACTION_LAMP_STOP_PLUGINS";

    /**
     * Stop all sensors
     */
    public static final String ACTION_LAMP_STOP_SENSORS = "ACTION_LAMP_STOP_SENSORS";

    /**
     * Set LAMP as a foreground service. This shows a permanent notification on the screen.
     */
    public static final String ACTION_LAMP_PRIORITY_FOREGROUND = "ACTION_LAMP_PRIORITY_FOREGROUND";

    /**
     * Set LAMP as a standard background service. May be killed or interrupted by Android at any time.
     */
    public static final String ACTION_LAMP_PRIORITY_BACKGROUND = "ACTION_LAMP_PRIORITY_BACKGROUND";

    /**
     * Used to check users' compliance in a study
     */
    public static final String ACTION_LAMP_PLUGIN_INSTALLED = "ACTION_LAMP_PLUGIN_INSTALLED";
    public static final String ACTION_LAMP_PLUGIN_UNINSTALLED = "ACTION_LAMP_PLUGIN_UNINSTALLED";
    public static final String EXTRA_PLUGIN = "extra_plugin";

    /**
     * Used by Plugin Manager to refresh UI
     */
    public static final String ACTION_LAMP_UPDATE_PLUGINS_INFO = "ACTION_LAMP_UPDATE_PLUGINS_INFO";

    /**
     * Used when quitting a study. This will reset the device to default settings.
     */
    public static final String ACTION_QUIT_STUDY = "ACTION_QUIT_STUDY";


    /**
     * Used by the LAMP watchdog
     */
    private static final String ACTION_LAMP_KEEP_ALIVE = "ACTION_LAMP_KEEP_ALIVE";

    /**
     * Used by the compliance check scheduler
     */
    private static final String ACTION_LAMP_STUDY_COMPLIANCE = "ACTION_LAMP_STUDY_COMPLIANCE";

    /**
     * Notification ID for LAMP service as foreground (to handle Doze, Android O battery optimizations)
     */
    public static final int LAMP_FOREGROUND_SERVICE = 220882;

    /**
     * Android 8 notification channels support
     */
    public static final String LAMP_NOTIFICATION_CHANNEL_GENERAL = "LAMP_NOTIFICATION_CHANNEL_GENERAL";
    public static final String LAMP_NOTIFICATION_CHANNEL_SILENT = "LAMP_NOTIFICATION_CHANNEL_SILENT";
    public static final String LAMP_NOTIFICATION_CHANNEL_DATASYNC = "LAMP_NOTIFICATION_CHANNEL_DATASYNC";

    public static final int LAMP_NOTIFICATION_IMPORTANCE_GENERAL = NotificationManager.IMPORTANCE_HIGH;
    public static final int LAMP_NOTIFICATION_IMPORTANCE_SILENT = NotificationManager.IMPORTANCE_MIN;
    public static final int LAMP_NOTIFICATION_IMPORTANCE_DATASYNC = NotificationManager.IMPORTANCE_LOW;

    private static Intent accelerometerSrv = null;
    private static Intent activityTransitionSrv = null;
    private static Intent locationsSrv = null;
    private static Intent screenSrv = null;
    private static Intent networkSrv = null;
    private static Intent gyroSrv = null;
    private static Intent wifiSrv = null;
    private static Intent rotationSrv = null;
    private static Intent lightSrv = null;
    private static Intent proximitySrv = null;
    private static Intent magnetoSrv = null;
    private static Intent barometerSrv = null;
    private static Intent linear_accelSrv = null;
    private static Intent significantSrv = null;
    private static Intent websocket = null;

    /**
     * Variable for the Doze ignore list
     */
    private static final int LAMP_BATTERY_OPTIMIZATION_ID = 567567;

    /**
     * Holds a reference to the LAMP account, automatically restore in each plugin.
     */
    private static Account aware_account;

    public String AUTHORITY = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = getPackageName() + ".provider.aware";

        IntentFilter foreground = new IntentFilter();
        foreground.addAction(Lamp.ACTION_LAMP_PRIORITY_FOREGROUND);
        foreground.addAction(Lamp.ACTION_LAMP_PRIORITY_BACKGROUND);
        registerReceiver(foregroundMgr, foreground);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            stopSelf();
            return;
        }

        //Android 8 specific: create notification channels for LAMP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager not_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel aware_channel = new NotificationChannel(LAMP_NOTIFICATION_CHANNEL_GENERAL, getResources().getString(R.string.app_name), LAMP_NOTIFICATION_IMPORTANCE_GENERAL);
            aware_channel.setDescription(getResources().getString(R.string.channel_general_description));
            aware_channel.enableLights(true);
            aware_channel.setLightColor(Color.BLUE);
            aware_channel.enableVibration(true);
            not_manager.createNotificationChannel(aware_channel);

            NotificationChannel aware_channel_sync = new NotificationChannel(LAMP_NOTIFICATION_CHANNEL_DATASYNC, getResources().getString(R.string.app_name), LAMP_NOTIFICATION_IMPORTANCE_DATASYNC);
            aware_channel_sync.setDescription(getResources().getString(R.string.channel_datasync_description));
            aware_channel_sync.enableLights(false);
            aware_channel_sync.setLightColor(Color.BLUE);
            aware_channel_sync.enableVibration(false);
            aware_channel_sync.setSound(null, null);
            not_manager.createNotificationChannel(aware_channel_sync);

            NotificationChannel aware_channel_silent = new NotificationChannel(LAMP_NOTIFICATION_CHANNEL_SILENT, getResources().getString(R.string.app_name), LAMP_NOTIFICATION_IMPORTANCE_SILENT);
            aware_channel_silent.setDescription(getResources().getString(R.string.channel_silent_description));
            aware_channel_silent.enableLights(false);
            aware_channel_silent.setLightColor(Color.BLUE);
            aware_channel_silent.enableVibration(false);
            aware_channel_silent.setSound(null, null);
            not_manager.createNotificationChannel(aware_channel_silent);
        }

        // Start the foreground service only if it's the client or a standalone application
        if ((getApplicationContext().getPackageName().equals("com.aware.phone") || getApplicationContext().getApplicationContext().getResources().getBoolean(R.bool.standalone)))
            getApplicationContext().sendBroadcast(new Intent(Lamp.ACTION_LAMP_PRIORITY_FOREGROUND));

        if (Lamp.DEBUG) Log.d(TAG, "LAMP framework is created!");

        IS_CORE_RUNNING = true;
    }


    private final Foreground_Priority foregroundMgr = new Foreground_Priority();

    public class Foreground_Priority extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //do nothing unless it's the client or a standalone application
            if (!(context.getPackageName().equals("com.aware.phone") || context.getApplicationContext().getResources().getBoolean(R.bool.standalone)))
                return;

            if (intent.getAction().equalsIgnoreCase(Lamp.ACTION_LAMP_PRIORITY_FOREGROUND)) {
                if (DEBUG) Log.d(TAG, "Setting LAMP with foreground priority");
//                foreground(true);
            } else if (intent.getAction().equalsIgnoreCase(Lamp.ACTION_LAMP_PRIORITY_BACKGROUND)) {
                if (DEBUG) Log.d(TAG, "Setting LAMP with background priority");
//                foreground(false);
            }
        }
    }

    // set sound/vibration/priority, mainly for android v7 and older as these are handled by channel in 8+
    // TODO potentially add other variables here in the future (e.g., icon, contentTitle, etc.)
    public static NotificationCompat.Builder setNotificationProperties(NotificationCompat.Builder builder, int notificationImportance) {
        switch (notificationImportance) {
            case LAMP_NOTIFICATION_IMPORTANCE_DATASYNC:
                builder.setSound(null);
                builder.setVibrate(null);
                // priority low but still visible
                builder.setPriority(NotificationCompat.PRIORITY_LOW);
                return builder;
            case LAMP_NOTIFICATION_IMPORTANCE_SILENT:
                builder.setSound(null);
                builder.setVibrate(null);
                // priority lowest
                builder.setPriority(NotificationCompat.PRIORITY_MIN);
                return builder;
            case LAMP_NOTIFICATION_IMPORTANCE_GENERAL:
                // default sound and vibration with HIGH priority
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                return builder;
            default:
                return builder;
        }
    }


    /**
     * Identifies if the device is a watch or a phone.
     *
     * @param c
     * @return boolean
     */
    public static boolean is_watch(Context c) {
        UiModeManager uiManager = (UiModeManager) c.getSystemService(Context.UI_MODE_SERVICE);
        return (uiManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_WATCH);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        IS_CORE_RUNNING = false;

        try {
            unregisterReceiver(foregroundMgr);
        } catch (IllegalArgumentException e) {
            //There is no API to check if a broadcast receiver already is registered. Since Aware.java is shared across plugins, the receiver is only registered on the client, not the plugins.
        }
    }

    /**
     * Stop all services
     *
     * @param context
     */
    public static void stopLAMP(Context context) {
        if (context == null) return;

        Intent aware = new Intent(context, Lamp.class);
        context.stopService(aware);

        stopSignificant(context);
        stopAccelerometer(context);
        stopLocations(context);
        stopScreen(context);
        stopGyroscope(context);
        stopWiFi(context);
        stopRotation(context);
        stopLight(context);
        stopMagnetometer(context);
        stopBarometer(context);
        stopLinearAccelerometer(context);

        stopActivityTransition(context);
    }

    /**
     * Start the significant motion service
     *
     * @param context
     */
    public static void startSignificant(Context context) {
        if (context == null) return;
        if (significantSrv == null) significantSrv = new Intent(context, SignificantMotion.class);
        context.startService(significantSrv);
    }

    /**
     * Stop the significant motion service
     *
     * @param context
     */
    public static void stopSignificant(Context context) {
        if (context == null) return;
        if (significantSrv != null) context.stopService(significantSrv);
    }


    /**
     * Start Linear Accelerometer module
     */
    public static void startLinearAccelerometer(Context context) {
        if (context == null) return;
        if (linear_accelSrv == null)
            linear_accelSrv = new Intent(context, LinearAccelerometer.class);
        context.startService(linear_accelSrv);
    }

    /**
     * Stop Linear Accelerometer module
     */
    public static void stopLinearAccelerometer(Context context) {
        if (context == null) return;
        if (linear_accelSrv != null) context.stopService(linear_accelSrv);
    }

    /**
     * Start Barometer module
     */
    public static void startBarometer(Context context) {
        if (context == null) return;
        if (barometerSrv == null) barometerSrv = new Intent(context, Barometer.class);
        context.startService(barometerSrv);
    }

    /**
     * Stop Barometer module
     */
    public static void stopBarometer(Context context) {
        if (context == null) return;
        if (barometerSrv != null) context.stopService(barometerSrv);
    }

    /**
     * Start Magnetometer module
     */
    public static void startMagnetometer(Context context) {
        if (context == null) return;
        if (magnetoSrv == null) magnetoSrv = new Intent(context, Magnetometer.class);
        context.startService(magnetoSrv);
    }

    /**
     * Stop Magnetometer module
     */
    public static void stopMagnetometer(Context context) {
        if (context == null) return;
        if (magnetoSrv != null) context.stopService(magnetoSrv);
    }

    /**
     * Start Light module
     */
    public static void startLight(Context context) {
        if (context == null) return;
        if (lightSrv == null) lightSrv = new Intent(context, Light.class);
        context.startService(lightSrv);
    }

    /**
     * Stop Light module
     */
    public static void stopLight(Context context) {
        if (context == null) return;
        if (lightSrv != null) context.stopService(lightSrv);
    }

    /**
     * Start Rotation module
     */
    public static void startRotation(Context context) {
        if (context == null) return;
        if (rotationSrv == null) rotationSrv = new Intent(context, Rotation.class);
        context.startService(rotationSrv);
    }

    /**
     * Stop Rotation module
     */
    public static void stopRotation(Context context) {
        if (context == null) return;
        if (rotationSrv != null) context.stopService(rotationSrv);
    }

    /**
     * Start the WiFi module
     */
    public static void startWiFi(Context context) {
        if (context == null) return;
        if (wifiSrv == null) wifiSrv = new Intent(context, WiFi.class);
        context.startService(wifiSrv);
    }

    public static void stopWiFi(Context context) {
        if (context == null) return;
        if (wifiSrv != null) context.stopService(wifiSrv);
    }

    /**
     * Start the gyroscope module
     */
    public static void startGyroscope(Context context) {
        if (context == null) return;
        if (gyroSrv == null) gyroSrv = new Intent(context, Gyroscope.class);
        context.startService(gyroSrv);
    }

    /**
     * Stop the gyroscope module
     */
    public static void stopGyroscope(Context context) {
        if (context == null) return;
        if (gyroSrv != null) context.stopService(gyroSrv);
    }

    /**
     * Start the accelerometer module
     */
    public static void startAccelerometer(Context context) {
        if (context == null) return;
        if (accelerometerSrv == null) accelerometerSrv = new Intent(context, Accelerometer.class);
        context.startService(accelerometerSrv);
    }

    /**
     * Stop the accelerometer module
     */
    public static void stopAccelerometer(Context context) {
        if (context == null) return;
        if (accelerometerSrv != null) context.stopService(accelerometerSrv);
    }

    /**
     * Start the accelerometer module
     */
    public static void startActivityTransition(Context context) {
        if (context == null) return;
        if (activityTransitionSrv == null) activityTransitionSrv = new Intent(context, ActivityTransitions.class);
        context.startService(activityTransitionSrv);
    }

    /**
     * Stop the accelerometer module
     */
    public static void stopActivityTransition(Context context) {
        if (context == null) return;
        if (activityTransitionSrv != null) context.stopService(activityTransitionSrv);
    }

    /**
     * Start the locations module
     */
    public static void startLocations(Context context) {
        if (context == null) return;
        if (locationsSrv == null) locationsSrv = new Intent(context, Locations.class);
        context.startService(locationsSrv);
    }

    /**
     * Stop the locations module
     */
    public static void stopLocations(Context context) {
        if (context == null) return;
        if (locationsSrv != null) context.stopService(locationsSrv);
    }

    /**
     * Start the screen module
     */
    public static void startScreen(Context context) {
        if (context == null) return;
        if (screenSrv == null) screenSrv = new Intent(context, Screen.class);
        context.startService(screenSrv);
    }

    /**
     * Stop the screen module
     */
    public static void stopScreen(Context context) {
        if (context == null) return;
        if (screenSrv != null) context.stopService(screenSrv);
    }
}
