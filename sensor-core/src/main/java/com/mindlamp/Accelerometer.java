
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
 * LAMP Accelerometer module
 * - Accelerometer raw data
 * - Accelerometer sensor information
 *
 * @author df
 */
public class Accelerometer extends Lamp_Sensor implements SensorEventListener {

    public static String TAG = "LAMP::Accelerometer";

    private static SensorManager mSensorManager;
    private static Sensor mAccelerometer;

    private static HandlerThread sensorThread = null;
    private static Handler sensorHandler = null;
    private static PowerManager.WakeLock wakeLock = null;
    private static String LABEL = "";

    private static Float[] LAST_VALUES = null;
    private static long LAST_TS = 0;
    private static long LAST_SAVE = 0;

    private static int FREQUENCY = -1;
    private static double THRESHOLD = 0;
    private static boolean ENFORCE_FREQUENCY = false;

    public static final String ACTION_LAMP_ACCELEROMETER = "ACTION_LAMP_ACCELEROMETER";
    public static final String ACTION_LAMP_ACCELEROMETER_LABEL = "ACTION_LAMP_ACCELEROMETER_LABEL";
    public static final String EXTRA_LABEL = "label";

    private List<ContentValues> data_values = new ArrayList<>();

    private static DataLabel dataLabeler = new DataLabel();

    public static class DataLabel extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LAMP_ACCELEROMETER_LABEL)) {
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
//        if (SignificantMotion.isSignificantMotionActive && !SignificantMotion.CURRENT_SIGMOTION_STATE) {
//            if (data_values.size() > 0) {
//                final ContentValues[] data_buffer = new ContentValues[data_values.size()];
//                data_values.toArray(data_buffer);
//                try {
//                    if (!Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEBUG_DB_SLOW).equals("true")) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                getContentResolver().bulkInsert(Accelerometer_Data.CONTENT_URI, data_buffer);
//
//                                Intent accelData = new Intent(ACTION_LAMP_ACCELEROMETER);
//                                sendBroadcast(accelData);
//                            }
//                        }).run();
//                    }
//                } catch (SQLiteException e) {
//                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//                } catch (SQLException e) {
//                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//                }
//                data_values.clear();
//            }
//            return;
//        }

        long TS = System.currentTimeMillis();
        if ((TS - LAST_TS) < LampConstants.INTERVAL)
            return;
        if (LAST_VALUES != null && THRESHOLD > 0 && Math.abs(event.values[0] - LAST_VALUES[0]) < THRESHOLD
                && Math.abs(event.values[1] - LAST_VALUES[1]) < THRESHOLD
                && Math.abs(event.values[2] - LAST_VALUES[2]) < THRESHOLD) {
            return;
        }

        LAST_VALUES = new Float[]{event.values[0], event.values[1], event.values[2]};

        ContentValues rowData = new ContentValues();
        rowData.put(Accelerometer_Data.TIMESTAMP, TS);
        rowData.put(Accelerometer_Data.VALUES_0, event.values[0]);
        rowData.put(Accelerometer_Data.VALUES_1, event.values[1]);
        rowData.put(Accelerometer_Data.VALUES_2, event.values[2]);
        rowData.put(Accelerometer_Data.ACCURACY, event.accuracy);
        rowData.put(Accelerometer_Data.LABEL, LABEL);

        if (awareSensor != null) awareSensor.onAccelerometerChanged(rowData);

        data_values.add(rowData);
        LAST_TS = TS;

        if (data_values.size() < 250 && TS < LAST_SAVE + 300000) {
            return;
        }

        final ContentValues[] data_buffer = new ContentValues[data_values.size()];
        data_values.toArray(data_buffer);

        data_values.clear();
        LAST_SAVE = TS;
    }

    private static LAMPSensorObserver awareSensor;
    public static void setSensorObserver(LAMPSensorObserver observer) {
        awareSensor = observer;
    }
    public static LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onAccelerometerChanged(ContentValues data);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorThread = new HandlerThread(TAG);
        sensorThread.start();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        sensorHandler = new Handler(sensorThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LAMP_ACCELEROMETER_LABEL);
        registerReceiver(dataLabeler, filter);

        if (Lamp.DEBUG) Log.d(TAG, "Accelerometer service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorHandler.removeCallbacksAndMessages(null);
        mSensorManager.unregisterListener(this, mAccelerometer);
        sensorThread.quit();
        wakeLock.release();

        unregisterReceiver(dataLabeler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            if (mAccelerometer == null) {
                stopSelf();
            } else {
                int new_frequency = LampConstants.FREQUENCY_ACCELEROMETER;
                double new_threshold = LampConstants.THRESHOLD_ACCELEROMETER;

                if (FREQUENCY != new_frequency
                        || THRESHOLD != new_threshold) {

                    sensorHandler.removeCallbacksAndMessages(null);
                    mSensorManager.unregisterListener(this, mAccelerometer);

                    FREQUENCY = new_frequency;
                    THRESHOLD = new_threshold;
                }

                mSensorManager.registerListener(this, mAccelerometer, FREQUENCY, sensorHandler);
                LAST_SAVE = System.currentTimeMillis();

                if (Lamp.DEBUG) Log.d(TAG, "Accelerometer service active: " + FREQUENCY + " ms");

            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final class Accelerometer_Data implements BaseColumns {

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String VALUES_0 = "double_values_0";
        public static final String VALUES_1 = "double_values_1";
        public static final String VALUES_2 = "double_values_2";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }
}