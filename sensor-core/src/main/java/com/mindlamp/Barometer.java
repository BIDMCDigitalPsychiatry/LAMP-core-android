
package com.mindlamp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncRequest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.mindlamp.providers.Barometer_Provider;
import com.mindlamp.providers.Barometer_Provider.Barometer_Data;
import com.mindlamp.providers.Barometer_Provider.Barometer_Sensor;
import com.mindlamp.utils.Lamp_Sensor;

import java.util.ArrayList;
import java.util.List;


/**
 * LAMP Barometer module
 * - Ambient pressure raw data, in mbar
 * - Ambient pressure sensor information
 *
 * @author df
 */
public class Barometer extends Lamp_Sensor implements SensorEventListener {

    public static String TAG = "LAMP::Barometer";

    private static SensorManager mSensorManager;
    private static Sensor mPressure;
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

    public static final String ACTION_LAMP_BAROMETER = "ACTION_LAMP_BAROMETER";
    public static final String ACTION_LAMP_BAROMETER_LABEL = "ACTION_LAMP_BAROMETER_LABEL";
    public static final String EXTRA_LABEL = "label";

    private List<ContentValues> data_values = new ArrayList<ContentValues>();
    private static String LABEL = "";
    private static DataLabel dataLabeler = new DataLabel();

    public static class DataLabel extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LAMP_BAROMETER_LABEL)) {
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

        LAST_VALUE = event.values[0];

        // Proceed with saving as usual.
        ContentValues rowData = new ContentValues();
        rowData.put(Barometer_Data.DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
        rowData.put(Barometer_Data.TIMESTAMP, TS);
        rowData.put(Barometer_Data.AMBIENT_PRESSURE, event.values[0]);
        rowData.put(Barometer_Data.ACCURACY, event.accuracy);
        rowData.put(Barometer_Data.LABEL, LABEL);

        if (awareSensor != null) awareSensor.onBarometerChanged(rowData);

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
//                        getContentResolver().bulkInsert(Barometer_Provider.Barometer_Data.CONTENT_URI, data_buffer);
//
//                        Intent accelData = new Intent(ACTION_LAMP_BAROMETER);
//                        sendBroadcast(accelData);
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

    private static Barometer.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Barometer.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Barometer.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onBarometerChanged(ContentValues data);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = Barometer_Provider.getAuthority(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        sensorThread = new HandlerThread(TAG);
        sensorThread.start();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        sensorHandler = new Handler(sensorThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LAMP_BAROMETER_LABEL);
        registerReceiver(dataLabeler, filter);

        if (Lamp.DEBUG) Log.d(TAG, "Barometer service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorHandler.removeCallbacksAndMessages(null);
        mSensorManager.unregisterListener(this, mPressure);
        sensorThread.quit();

        wakeLock.release();

        unregisterReceiver(dataLabeler);

        if (Lamp.DEBUG) Log.d(TAG, "Barometer service terminated...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            if (mPressure == null) {
                if (Lamp.DEBUG) Log.w(TAG, "This device does not have a barometer sensor!");
                Lamp.setSetting(this, Lamp_Preferences.STATUS_BAROMETER, false);
                stopSelf();
            } else {
                DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");

                Lamp.setSetting(getApplicationContext(), Lamp_Preferences.STATUS_BAROMETER, true);

                if (Lamp.getSetting(this, Lamp_Preferences.FREQUENCY_BAROMETER).length() == 0) {
                    Lamp.setSetting(this, Lamp_Preferences.FREQUENCY_BAROMETER, 200000);
                }

                if (Lamp.getSetting(this, Lamp_Preferences.THRESHOLD_BAROMETER).length() == 0) {
                    Lamp.setSetting(this, Lamp_Preferences.THRESHOLD_BAROMETER, 0.0);
                }

                int new_frequency = Integer.parseInt(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_BAROMETER));
                double new_threshold = Double.parseDouble(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.THRESHOLD_BAROMETER));
                boolean new_enforce_frequency = (Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_BAROMETER_ENFORCE).equals("true")
                        || Lamp.getSetting(getApplicationContext(), Lamp_Preferences.ENFORCE_FREQUENCY_ALL).equals("true"));

                if (FREQUENCY != new_frequency
                        || THRESHOLD != new_threshold
                        || ENFORCE_FREQUENCY != new_enforce_frequency) {

                    sensorHandler.removeCallbacksAndMessages(null);
                    mSensorManager.unregisterListener(this, mPressure);

                    FREQUENCY = new_frequency;
                    THRESHOLD = new_threshold;
                    ENFORCE_FREQUENCY = new_enforce_frequency;
                }

                mSensorManager.registerListener(this, mPressure, Integer.parseInt(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_BAROMETER)), sensorHandler);
                LAST_SAVE = System.currentTimeMillis();

                if (Lamp.DEBUG) Log.d(TAG, "Barometer service active: " + FREQUENCY + "ms");

            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}