
package com.mindlamp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.mindlamp.providers.Gyroscope_Provider.Gyroscope_Data;
import com.mindlamp.providers.Gyroscope_Provider.Gyroscope_Sensor;
import com.mindlamp.utils.LampConstants;
import com.mindlamp.utils.Lamp_Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that logs gyroscope readings from the device
 *
 * @author df
 */
public class Gyroscope extends Lamp_Sensor implements SensorEventListener {

    /**
     * Logging tag (default = "LAMP::Gyroscope")
     */
    private static String TAG = "LAMP::Gyroscope";

    private static SensorManager mSensorManager;
    private static Sensor mGyroscope;

    private static HandlerThread sensorThread = null;
    private static Handler sensorHandler = null;
    private static PowerManager.WakeLock wakeLock = null;

    private static Float[] LAST_VALUES = null;
    private static long LAST_TS = 0;
    private static long LAST_SAVE = 0;

    private static int FREQUENCY = -1;
    private static double THRESHOLD = 0;
    // Reject any data points that come in more often than frequency
    private static boolean ENFORCE_FREQUENCY = false;

    /**
     * Broadcasted event: new gyroscope values
     * ContentProvider: Gyroscope_Provider
     */
    public static final String ACTION_LAMP_GYROSCOPE = "ACTION_LAMP_GYROSCOPE";
    public static final String EXTRA_SENSOR = "sensor";
    public static final String EXTRA_DATA = "data";

    public static final String ACTION_LAMP_GYROSCOPE_LABEL = "ACTION_LAMP_GYROSCOPE_LABEL";
    public static final String EXTRA_LABEL = "label";

    /**
     * Until today, no available Android phone samples higher than 208Hz (Nexus 7).
     * http://ilessendata.blogspot.com/2012/11/android-accelerometer-sampling-rates.html
     */
    private List<ContentValues> data_values = new ArrayList<>();

    private static String LABEL = "";

    private static DataLabel dataLabeler = new DataLabel();

    public static class DataLabel extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LAMP_GYROSCOPE_LABEL)) {
                LABEL = intent.getStringExtra(EXTRA_LABEL);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //we log accuracy on the sensor changed values
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (SignificantMotion.isSignificantMotionActive && !SignificantMotion.CURRENT_SIGMOTION_STATE) {
            if (data_values.size() > 0) {
                final ContentValues[] data_buffer = new ContentValues[data_values.size()];
                data_values.toArray(data_buffer);
                try {
                    if (!Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEBUG_DB_SLOW).equals("true")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                getContentResolver().bulkInsert(Gyroscope_Provider.Gyroscope_Data.CONTENT_URI, data_buffer);

                                Intent newData = new Intent(ACTION_LAMP_GYROSCOPE);
                                sendBroadcast(newData);
                            }
                        }).run();
                    }
                } catch (SQLiteException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                } catch (SQLException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                }
                data_values.clear();
            }

            return;
        }

        long TS = System.currentTimeMillis();
        if (ENFORCE_FREQUENCY && TS < LAST_TS + FREQUENCY / 1000)
            return;
        if (LAST_VALUES != null && THRESHOLD > 0 && Math.abs(event.values[0] - LAST_VALUES[0]) < THRESHOLD
                && Math.abs(event.values[1] - LAST_VALUES[1]) < THRESHOLD
                && Math.abs(event.values[2] - LAST_VALUES[2]) < THRESHOLD) {
            return;
        }

        LAST_VALUES = new Float[]{event.values[0], event.values[1], event.values[2]};

        // Proceed with saving as usual.
        ContentValues rowData = new ContentValues();
        rowData.put(Gyroscope_Data.DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
        rowData.put(Gyroscope_Data.TIMESTAMP, TS);
        rowData.put(Gyroscope_Data.VALUES_0, event.values[0]);
        rowData.put(Gyroscope_Data.VALUES_1, event.values[1]);
        rowData.put(Gyroscope_Data.VALUES_2, event.values[2]);
        rowData.put(Gyroscope_Data.ACCURACY, event.accuracy);
        rowData.put(Gyroscope_Data.LABEL, LABEL);

        if (awareSensor != null) awareSensor.onGyroscopeChanged(rowData);

        data_values.add(rowData);
        LAST_TS = TS;

        if (data_values.size() < 250 && TS < LAST_SAVE + 300000) {
            return;
        }

        final ContentValues[] data_buffer = new ContentValues[data_values.size()];
        data_values.toArray(data_buffer);
        try {
            if (!Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEBUG_DB_SLOW).equals("true")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        getContentResolver().bulkInsert(Gyroscope_Provider.Gyroscope_Data.CONTENT_URI, data_buffer);

                        Intent newData = new Intent(ACTION_LAMP_GYROSCOPE);
                        sendBroadcast(newData);
                    }
                }).run();
            }
        } catch (SQLiteException e) {
            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
        } catch (SQLException e) {
            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
        }
        data_values.clear();
        LAST_SAVE = TS;
    }

    private static Gyroscope.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Gyroscope.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Gyroscope.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onGyroscopeChanged(ContentValues data);
    }

    /**
     * Calculates the sampling rate in Hz (i.e., how many samples did we collect in the past second)
     *
     * @param context
     * @return hz
     */
    public static int getFrequency(Context context) {
        int hz = 0;
        String[] columns = new String[]{"count(*) as frequency", "datetime(" + Gyroscope_Data.TIMESTAMP + "/1000, 'unixepoch','localtime') as sample_time"};
        Cursor qry = context.getContentResolver().query(Gyroscope_Data.CONTENT_URI, columns, "1) group by (sample_time", null, "sample_time DESC LIMIT 1 OFFSET 2");
        if (qry != null && qry.moveToFirst()) {
            hz = qry.getInt(0);
        }
        if (qry != null && !qry.isClosed()) qry.close();
        return hz;
    }

    private void saveGyroscopeDevice(Sensor gyro) {
        Cursor gyroInfo = getContentResolver().query(Gyroscope_Sensor.CONTENT_URI, null, null, null, null);
        if (gyroInfo == null || !gyroInfo.moveToFirst()) {
            ContentValues rowData = new ContentValues();
            rowData.put(Gyroscope_Sensor.DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
            rowData.put(Gyroscope_Sensor.TIMESTAMP, System.currentTimeMillis());
            rowData.put(Gyroscope_Sensor.MAXIMUM_RANGE, gyro.getMaximumRange());
            rowData.put(Gyroscope_Sensor.MINIMUM_DELAY, gyro.getMinDelay());
            rowData.put(Gyroscope_Sensor.NAME, gyro.getName());
            rowData.put(Gyroscope_Sensor.POWER_MA, gyro.getPower());
            rowData.put(Gyroscope_Sensor.RESOLUTION, gyro.getResolution());
            rowData.put(Gyroscope_Sensor.TYPE, gyro.getType());
            rowData.put(Gyroscope_Sensor.VENDOR, gyro.getVendor());
            rowData.put(Gyroscope_Sensor.VERSION, gyro.getVersion());

            getContentResolver().insert(Gyroscope_Sensor.CONTENT_URI, rowData);

            Intent gyro_dev = new Intent(ACTION_LAMP_GYROSCOPE);
            gyro_dev.putExtra(EXTRA_SENSOR, rowData);
            sendBroadcast(gyro_dev);

            if (Lamp.DEBUG) Log.d(TAG, "Gyroscope info: " + rowData.toString());
        }
        if (gyroInfo != null && !gyroInfo.isClosed()) gyroInfo.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        AUTHORITY = Gyroscope_Provider.getAuthority(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorThread = new HandlerThread(TAG);
        sensorThread.start();


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        sensorHandler = new Handler(sensorThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LAMP_GYROSCOPE_LABEL);
        registerReceiver(dataLabeler, filter);

        if (Lamp.DEBUG) Log.d(TAG, "Gyroscope service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorHandler.removeCallbacksAndMessages(null);
        mSensorManager.unregisterListener(this, mGyroscope);
        sensorThread.quit();

        wakeLock.release();

        unregisterReceiver(dataLabeler);

//        ContentResolver.setSyncAutomatically(Aware.getLAMPAccount(this), Gyroscope_Provider.getAuthority(this), false);
//        ContentResolver.removePeriodicSync(
//                Aware.getLAMPAccount(this),
//                Gyroscope_Provider.getAuthority(this),
//                Bundle.EMPTY
//        );

        if (Lamp.DEBUG) Log.d(TAG, "Gyroscope service terminated...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            if (mGyroscope == null) {
                if (Lamp.DEBUG) Log.w(TAG, "This device does not have a gyroscope!");
                Lamp.setSetting(this, Lamp_Preferences.STATUS_GYROSCOPE, false);
                stopSelf();
            } else {

                DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");

                Lamp.setSetting(this, Lamp_Preferences.STATUS_GYROSCOPE, true);
//                saveGyroscopeDevice(mGyroscope);

                if (Lamp.getSetting(this, Lamp_Preferences.FREQUENCY_GYROSCOPE).length() == 0) {
                    Lamp.setSetting(this, Lamp_Preferences.FREQUENCY_GYROSCOPE, 200000);
                }

                if (Lamp.getSetting(this, Lamp_Preferences.THRESHOLD_GYROSCOPE).length() == 0) {
                    Lamp.setSetting(this, Lamp_Preferences.THRESHOLD_GYROSCOPE, 0.0);
                }

//                int new_frequency = Integer.parseInt(Aware.getSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_GYROSCOPE));
                int new_frequency = LampConstants.FREQUENCY_GYROSCOPE;
//                double new_threshold = Double.parseDouble(Aware.getSetting(getApplicationContext(), Aware_Preferences.THRESHOLD_GYROSCOPE));
                double new_threshold = LampConstants.THRESHOLD_GYROSCOPE;
                boolean new_enforce_frequency = (Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_GYROSCOPE_ENFORCE).equals("true")
                        || Lamp.getSetting(getApplicationContext(), Lamp_Preferences.ENFORCE_FREQUENCY_ALL).equals("true"));

                if (FREQUENCY != new_frequency
                        || THRESHOLD != new_threshold
                        || ENFORCE_FREQUENCY != new_enforce_frequency) {

                    sensorHandler.removeCallbacksAndMessages(null);
                    mSensorManager.unregisterListener(this, mGyroscope);

                    FREQUENCY = new_frequency;
                    THRESHOLD = new_threshold;
                    ENFORCE_FREQUENCY = new_enforce_frequency;
                }

                mSensorManager.registerListener(this, mGyroscope, LampConstants.FREQUENCY_GYROSCOPE, sensorHandler);
                LAST_SAVE = System.currentTimeMillis();
            }

            if (Lamp.DEBUG) Log.d(TAG, "Gyroscope service active: " + FREQUENCY + "ms");

//            if (Aware.isStudy(this)) {
//                ContentResolver.setIsSyncable(Aware.getLAMPAccount(this), Gyroscope_Provider.getAuthority(this), 1);
//                ContentResolver.setSyncAutomatically(Aware.getLAMPAccount(this), Gyroscope_Provider.getAuthority(this), true);
//                long frequency = Long.parseLong(Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60;
//                SyncRequest request = new SyncRequest.Builder()
//                        .syncPeriodic(frequency, frequency / 3)
//                        .setSyncAdapter(Aware.getLAMPAccount(this), Gyroscope_Provider.getAuthority(this))
//                        .setExtras(new Bundle()).build();
//                ContentResolver.requestSync(request);
//            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}