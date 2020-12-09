
package digital.lamp;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import digital.lamp.utils.Lamp_Sensor;

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

        if (Lamp.DEBUG) Log.d(TAG, "Screen service terminated...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null && intent.getAction() != null) {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (intent.getAction().equals(ACTION_LAMP_SCREEN_ON)) {

                if (awareSensor != null) awareSensor.onScreenOn();
                Log.d(TAG, ACTION_LAMP_SCREEN_ON);
                sendBroadcast(new Intent(ACTION_LAMP_SCREEN_ON));

                if (km.isKeyguardLocked()) {
                        if (awareSensor != null) awareSensor.onScreenUnlocked();
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                }

                return START_STICKY;
            }
            if (intent.getAction().equals(ACTION_LAMP_SCREEN_OFF)) {
                if (awareSensor != null) awareSensor.onScreenOff();

                if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_OFF);
                sendBroadcast(new Intent(ACTION_LAMP_SCREEN_OFF));

                if (km.isKeyguardLocked()) {
                    if (awareSensor != null) awareSensor.onScreenLocked();

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                }
                return START_STICKY;
            }

            if (intent.getAction().equals(ACTION_LAMP_SCREEN_UNLOCKED)) {
                if (!km.isKeyguardLocked()) {
                    if (awareSensor != null) awareSensor.onScreenUnlocked();

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
                    if (awareSensor != null) awareSensor.onScreenOn();

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_ON);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_ON));

                    if (km.isKeyguardLocked()) {
                        if (awareSensor != null) awareSensor.onScreenUnlocked();

                        if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                        sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                    } else {
                        if (awareSensor != null) awareSensor.onScreenUnlocked();

                        if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_UNLOCKED);
                        sendBroadcast(new Intent(ACTION_LAMP_SCREEN_UNLOCKED));
                    }
                } else {
                    if (awareSensor != null) awareSensor.onScreenOff();

                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_OFF);
                    sendBroadcast(new Intent(ACTION_LAMP_SCREEN_OFF));

                    if (km.isKeyguardLocked()) {
                            if (awareSensor != null) awareSensor.onScreenLocked();

                        if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED);
                        sendBroadcast(new Intent(ACTION_LAMP_SCREEN_LOCKED));
                    }
                }
            }

            if (Lamp.DEBUG) Log.d(TAG, "Screen service active...");
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
