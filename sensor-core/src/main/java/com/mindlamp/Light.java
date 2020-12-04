
package com.mindlamp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.mindlamp.utils.LampConstants;
import com.mindlamp.utils.Lamp_Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * LAMP Light module
 * - Light raw data
 * - Light sensor information
 *
 * @author df
 */
public class Light extends Lamp_Sensor implements SensorEventListener {

    /**
     * Logging tag (default = "LAMP::Light")
     */
    private static String TAG = "LAMP::Light";

    private static SensorManager mSensorManager;
    private static Sensor mLight;

    private static HandlerThread sensorThread = null;
    private static Handler sensorHandler = null;

    private static PowerManager.WakeLock wakeLock = null;

    private static Float LAST_VALUE = null;
    private static long LAST_TS = 0;
    private static long LAST_SAVE = 0;

    private static int FREQUENCY = -1;
    private static double THRESHOLD = 0;
    // Reject any data points that come in more often than frequency
    private static boolean ENFORCE_FREQUENCY = false;

    /**
     * Broadcasted event: new light values
     * ContentProvider: LightProvider
     */
    public static final String ACTION_LAMP_LIGHT = "ACTION_LAMP_LIGHT";
    public static final String ACTION_LAMP_LIGHT_LABEL = "ACTION_LAMP_LIGHT_LABEL";
    public static final String EXTRA_LABEL = "label";

    /**
     * Until today, no available Android phone samples higher than 208Hz (Nexus 7).
     * http://ilessendata.blogspot.com/2012/11/android-accelerometer-sampling-rates.html
     */
    private List<ContentValues> data_values = new ArrayList<ContentValues>();

    private static String LABEL = "";

    private static DataLabel dataLabeler = new DataLabel();

    public static class DataLabel extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LAMP_LIGHT_LABEL)) {
                LABEL = intent.getStringExtra(EXTRA_LABEL);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //We log current accuracy on the sensor changed event
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long TS = System.currentTimeMillis();
        if (ENFORCE_FREQUENCY && TS < LAST_TS + FREQUENCY / 1000)
            return;
        if (LAST_VALUE != null && THRESHOLD > 0 && Math.abs(event.values[0] - LAST_VALUE) < THRESHOLD) {
            return;
        }

        LAST_TS = TS;
        LAST_VALUE = event.values[0];

        ContentValues rowData = new ContentValues();
        rowData.put(Light_Data.TIMESTAMP, TS);
        rowData.put(Light_Data.LIGHT_LUX, event.values[0]);
        rowData.put(Light_Data.ACCURACY, event.accuracy);
        rowData.put(Light_Data.LABEL, LABEL);

        if (awareSensor != null) awareSensor.onLightChanged(rowData);

        data_values.add(rowData);
        LAST_TS = TS;

        if (data_values.size() < 250 && TS < LAST_SAVE + 300000) {
            return;
        }

        final ContentValues[] data_buffer = new ContentValues[data_values.size()];
        data_values.toArray(data_buffer);

//        try {
//            if (!Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEBUG_DB_SLOW).equals("true")) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getContentResolver().bulkInsert(Light_Provider.Light_Data.CONTENT_URI, data_buffer);
//
//                        Intent newData = new Intent(ACTION_LAMP_LIGHT);
//                        sendBroadcast(newData);
//                    }
//                }).run();
//            }
//        } catch (SQLiteException e) {
//            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//        } catch (SQLException e) {
//            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//        }
        data_values.clear();
        LAST_SAVE = TS;
    }

    private static Light.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Light.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Light.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onLightChanged(ContentValues data);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = getPackageName() + ".provider.light";

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorThread = new HandlerThread(TAG);
        sensorThread.start();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        sensorHandler = new Handler(sensorThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LAMP_LIGHT_LABEL);
        registerReceiver(dataLabeler, filter);


        if (Lamp.DEBUG) Log.d(TAG, "Light service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorHandler.removeCallbacksAndMessages(null);
        mSensorManager.unregisterListener(this, mLight);
        sensorThread.quit();

        wakeLock.release();

        unregisterReceiver(dataLabeler);

        if (Lamp.DEBUG) Log.d(TAG, "Light service terminated...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            if (mLight == null) {

                stopSelf();
            } else {

                int new_frequency = LampConstants.FREQUENCY_LIGHT;
                double new_threshold = LampConstants.THRESHOLD_LIGHT;

                if (FREQUENCY != new_frequency
                        || THRESHOLD != new_threshold) {

                    sensorHandler.removeCallbacksAndMessages(null);
                    mSensorManager.unregisterListener(this, mLight);

                    FREQUENCY = new_frequency;
                    THRESHOLD = new_threshold;
                }

                mSensorManager.registerListener(this, mLight, new_frequency, sensorHandler);


                if (Lamp.DEBUG) Log.d(TAG, "Light service active: " + FREQUENCY + "ms");
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final class Light_Data implements BaseColumns {

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String LIGHT_LUX = "double_light_lux";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

}