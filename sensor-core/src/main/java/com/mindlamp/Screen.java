
package com.mindlamp;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.mindlamp.utils.Lamp_Sensor;

/**
 * Service that logs users' interactions with the screen
 * - on/off events
 * - locked/unlocked events
 *
 * @author denzil
 */
public class Screen extends Lamp_Sensor {

    private static String TAG = "LAMP::Screen";

    /**
     * Used to log the current screen status when starting the sensor
     */
    private static final String ACTION_LAMP_SCREEN_BOOT = "ACTION_LAMP_SCREEN_BOOT";

    /**
     * Broadcasted event: screen is on
     */
    public static final String ACTION_LAMP_SCREEN_ON = "ACTION_LAMP_SCREEN_ON";

    /**
     * Broadcasted event: screen is off
     */
    public static final String ACTION_LAMP_SCREEN_OFF = "ACTION_LAMP_SCREEN_OFF";

    /**
     * Broadcasted event: screen is locked
     */
    public static final String ACTION_LAMP_SCREEN_LOCKED = "ACTION_LAMP_SCREEN_LOCKED";

    /**
     * Broadcasted event: screen is unlocked
     */
    public static final String ACTION_LAMP_SCREEN_UNLOCKED = "ACTION_LAMP_SCREEN_UNLOCKED";

    public static final String ACTION_LAMP_TOUCH_CLICKED = "ACTION_LAMP_TOUCH_CLICKED";
    public static final String ACTION_LAMP_TOUCH_LONG_CLICKED = "ACTION_LAMP_TOUCH_LONG_CLICKED";
    public static final String ACTION_LAMP_TOUCH_SCROLLED_UP = "ACTION_LAMP_TOUCH_SCROLLED_UP";
    public static final String ACTION_LAMP_TOUCH_SCROLLED_DOWN = "ACTION_LAMP_TOUCH_SCROLLED_DOWN";

    /**
     * Screen status: OFF = 0
     */
    public static final int STATUS_SCREEN_OFF = 0;

    /**
     * Screen status: ON = 1
     */
    public static final int STATUS_SCREEN_ON = 1;

    /**
     * Screen status: LOCKED = 2
     */
    public static final int STATUS_SCREEN_LOCKED = 2;

    /**
     * Screen status: UNLOCKED = 3
     */
    public static final int STATUS_SCREEN_UNLOCKED = 3;

    private ScreenMonitor screenMonitor = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static Screen.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Screen.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Screen.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onScreenOn();

        void onScreenOff();

        void onScreenLocked();

        void onScreenUnlocked();
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        AUTHORITY = Screen_Provider.getAuthority(this);

        if (Lamp.DEBUG) Log.d(TAG, "Screen service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (screenMonitor != null) unregisterReceiver(screenMonitor);

//        ContentResolver.setSyncAutomatically(Aware.getLAMPAccount(this), Screen_Provider.getAuthority(this), false);
//        ContentResolver.removePeriodicSync(
//                Aware.getLAMPAccount(this),
//                Screen_Provider.getAuthority(this),
//                Bundle.EMPTY
//        );

        if (Lamp.DEBUG) Log.d(TAG, "Screen service terminated...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null && intent.getAction() != null) {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (intent.getAction().equals(ACTION_LAMP_SCREEN_ON)) {

//                ContentValues rowData = new ContentValues();
//                rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_ON);
                try {
//                    getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                    if (awareSensor != null) awareSensor.onScreenOn();
                } catch (SQLiteException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                } catch (SQLException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                }

                Log.d(TAG, ACTION_LAMP_SCREEN_ON);
                sendBroadcast(new Intent(ACTION_LAMP_SCREEN_ON));

                if (km.isKeyguardLocked()) {
//                    rowData = new ContentValues();
//                    rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                    rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                    rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_LOCKED);
                    try {
//                        getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                        if (awareSensor != null) awareSensor.onScreenUnlocked();
                    } catch (SQLiteException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    } catch (SQLException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    }

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                }

                return START_STICKY;
            }
            if (intent.getAction().equals(ACTION_LAMP_SCREEN_OFF)) {

//                ContentValues rowData = new ContentValues();
//                rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_OFF);
                try {
//                    getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                    if (awareSensor != null) awareSensor.onScreenOff();
                } catch (SQLiteException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                } catch (SQLException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                }

                if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_OFF);
                sendBroadcast(new Intent(ACTION_LAMP_SCREEN_OFF));

                if (km.isKeyguardLocked()) {
//                    rowData = new ContentValues();
//                    rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                    rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                    rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_LOCKED);
                    try {
//                        getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                        if (awareSensor != null) awareSensor.onScreenLocked();
                    } catch (SQLiteException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    } catch (SQLException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    }

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                }
                return START_STICKY;
            }

            if (intent.getAction().equals(ACTION_LAMP_SCREEN_UNLOCKED)) {
                if (!km.isKeyguardLocked()) {
//                    ContentValues rowData = new ContentValues();
//                    rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                    rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                    rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_UNLOCKED);
                    try {
//                        getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                        if (awareSensor != null) awareSensor.onScreenUnlocked();
                    } catch (SQLiteException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    } catch (SQLException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    }

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_UNLOCKED);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_UNLOCKED));
                }
                return START_STICKY;
            }
        }

        if (PERMISSIONS_OK) {
            if (screenMonitor == null) {
                screenMonitor = new ScreenMonitor();

                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                filter.addAction(Intent.ACTION_USER_PRESENT);
                filter.addAction(Screen.ACTION_LAMP_SCREEN_BOOT);
                registerReceiver(screenMonitor, filter);

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if (pm.isInteractive()) {
//                    ContentValues rowData = new ContentValues();
//                    rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                    rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
//                    rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_ON);

                    try {
//                        getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                        if (awareSensor != null) awareSensor.onScreenOn();
                    } catch (SQLiteException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    } catch (SQLException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    }

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_ON);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_ON));

                    if (km.isKeyguardLocked()) {
//                        rowData = new ContentValues();
//                        rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                        rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
//                        rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_LOCKED);

                        try {
//                            getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                            if (awareSensor != null) awareSensor.onScreenUnlocked();
                        } catch (SQLiteException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        } catch (SQLException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        }

                        if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                        sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                    } else {
//                        rowData = new ContentValues();
//                        rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                        rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
//                        rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_UNLOCKED);

                        try {
//                            getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                            if (awareSensor != null) awareSensor.onScreenUnlocked();
                        } catch (SQLiteException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        } catch (SQLException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        }

                        if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_UNLOCKED);
                        sendBroadcast(new Intent(ACTION_LAMP_SCREEN_UNLOCKED));
                    }
                } else {
//                    ContentValues rowData = new ContentValues();
//                    rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                    rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
//                    rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_OFF);

                    try {
//                        getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                        if (awareSensor != null) awareSensor.onScreenOff();
                    } catch (SQLiteException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    } catch (SQLException e) {
                        if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                    }

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_OFF);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_OFF));

                    if (km.isKeyguardLocked()) {
//                        rowData = new ContentValues();
//                        rowData.put(Screen_Data.TIMESTAMP, System.currentTimeMillis());
//                        rowData.put(Screen_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
//                        rowData.put(Screen_Data.SCREEN_STATUS, Screen.STATUS_SCREEN_LOCKED);

                        try {
//                            getContentResolver().insert(Screen_Data.CONTENT_URI, rowData);
                            if (awareSensor != null) awareSensor.onScreenLocked();
                        } catch (SQLiteException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        } catch (SQLException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        }

                        if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                        sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                    }
                }
            }

            DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");
            Lamp.setSetting(this, Lamp_Preferences.STATUS_SCREEN, true);
            if (Lamp.DEBUG) Log.d(TAG, "Screen service active...");

            //We can only get the touch events if accessibility service is enabled.
            if (Lamp.getSetting(this, Lamp_Preferences.STATUS_TOUCH).equals("true")) {
                Applications.isAccessibilityServiceActive(this);
            }

//            if (Aware.isStudy(this)) {
//                ContentResolver.setIsSyncable(Aware.getLAMPAccount(this), Screen_Provider.getAuthority(this), 1);
//                ContentResolver.setSyncAutomatically(Aware.getLAMPAccount(this), Screen_Provider.getAuthority(this), true);
//                long frequency = Long.parseLong(Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60;
//                SyncRequest request = new SyncRequest.Builder()
//                        .syncPeriodic(frequency, frequency / 3)
//                        .setSyncAdapter(Aware.getLAMPAccount(this), Screen_Provider.getAuthority(this))
//                        .setExtras(new Bundle()).build();
//                ContentResolver.requestSync(request);
//            }
        }
        return START_STICKY;
    }

    private class ScreenMonitor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startService(new Intent(context, Screen.class).setAction(Screen.ACTION_LAMP_SCREEN_ON));
            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || !pm.isInteractive()) {
                startService(new Intent(context, Screen.class).setAction(Screen.ACTION_LAMP_SCREEN_OFF));
            }
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT) && !km.isKeyguardLocked()) {
                startService(new Intent(context, Screen.class).setAction(Screen.ACTION_LAMP_SCREEN_UNLOCKED));
            }
        }
    }
}
