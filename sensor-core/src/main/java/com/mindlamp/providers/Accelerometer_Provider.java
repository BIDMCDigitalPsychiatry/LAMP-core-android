
package com.mindlamp.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;

/**
 * LAMP Accelerometer Content Provider Allows you to access all the recorded
 * sync_accelerometer readings on the database Database is located at the SDCard :
 * /LAMP/sync_accelerometer.db
 *
 * @author denzil
 */
public class Accelerometer_Provider extends ContentProvider {

    /**
     * Authority of content provider
     */
    public static String AUTHORITY = "com.aware.provider.accelerometer";

    // ContentProvider query paths
    private final int ACCEL_DEV = 1;
    private final int ACCEL_DEV_ID = 2;
    private final int ACCEL_DATA = 3;
    private final int ACCEL_DATA_ID = 4;

    private UriMatcher sUriMatcher;
    private HashMap<String, String> accelDeviceMap;
    private HashMap<String, String> accelDataMap;

    /**
     * Accelerometer device info
     *
     * @author denzil
     */
    public static final class Accelerometer_Sensor implements BaseColumns {
        private Accelerometer_Sensor() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + Accelerometer_Provider.AUTHORITY + "/sensor_accelerometer");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.accelerometer.sensor";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.accelerometer.sensor";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String MAXIMUM_RANGE = "double_sensor_maximum_range";
        public static final String MINIMUM_DELAY = "double_sensor_minimum_delay";
        public static final String NAME = "sensor_name";
        public static final String POWER_MA = "double_sensor_power_ma";
        public static final String RESOLUTION = "double_sensor_resolution";
        public static final String TYPE = "sensor_type";
        public static final String VENDOR = "sensor_vendor";
        public static final String VERSION = "sensor_version";
    }

    /**
     * Logged sync_accelerometer data
     *
     * @author df
     */
    public static final class Accelerometer_Data implements BaseColumns {
        private Accelerometer_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Accelerometer_Provider.AUTHORITY + "/accelerometer");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.accelerometer.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.accelerometer.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String VALUES_0 = "double_values_0";
        public static final String VALUES_1 = "double_values_1";
        public static final String VALUES_2 = "double_values_2";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "accelerometer.db";
    public static final String[] DATABASE_TABLES = {"sensor_accelerometer", "accelerometer"};
    public static final String[] TABLES_FIELDS = {
            // sync_accelerometer device information
            Accelerometer_Sensor._ID + " integer primary key autoincrement,"
                    + Accelerometer_Sensor.TIMESTAMP + " real default 0,"
                    + Accelerometer_Sensor.DEVICE_ID + " text default '',"
                    + Accelerometer_Sensor.MAXIMUM_RANGE + " real default 0,"
                    + Accelerometer_Sensor.MINIMUM_DELAY + " real default 0,"
                    + Accelerometer_Sensor.NAME + " text default '',"
                    + Accelerometer_Sensor.POWER_MA + " real default 0,"
                    + Accelerometer_Sensor.RESOLUTION + " real default 0,"
                    + Accelerometer_Sensor.TYPE + " text default '',"
                    + Accelerometer_Sensor.VENDOR + " text default '',"
                    + Accelerometer_Sensor.VERSION + " text default '',"
                    + "UNIQUE(" + Accelerometer_Sensor.DEVICE_ID + ")",

            // sync_accelerometer data
            Accelerometer_Data._ID + " integer primary key autoincrement,"
                    + Accelerometer_Data.TIMESTAMP + " real default 0,"
                    + Accelerometer_Data.DEVICE_ID + " text default '',"
                    + Accelerometer_Data.VALUES_0 + " real default 0,"
                    + Accelerometer_Data.VALUES_1 + " real default 0,"
                    + Accelerometer_Data.VALUES_2 + " real default 0,"
                    + Accelerometer_Data.ACCURACY + " integer default 0,"
                    + Accelerometer_Data.LABEL + " text default ''"};

    /**
     * Returns the provider authority that is dynamic
     * @return
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider.accelerometer";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.accelerometer";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Accelerometer_Provider.AUTHORITY, DATABASE_TABLES[0], ACCEL_DEV);
        sUriMatcher.addURI(Accelerometer_Provider.AUTHORITY, DATABASE_TABLES[0] + "/#", ACCEL_DEV_ID);
        sUriMatcher.addURI(Accelerometer_Provider.AUTHORITY, DATABASE_TABLES[1], ACCEL_DATA);
        sUriMatcher.addURI(Accelerometer_Provider.AUTHORITY, DATABASE_TABLES[1] + "/#", ACCEL_DATA_ID);

        accelDeviceMap = new HashMap<>();
        accelDeviceMap.put(Accelerometer_Sensor._ID, Accelerometer_Sensor._ID);
        accelDeviceMap.put(Accelerometer_Sensor.TIMESTAMP, Accelerometer_Sensor.TIMESTAMP);
        accelDeviceMap.put(Accelerometer_Sensor.DEVICE_ID, Accelerometer_Sensor.DEVICE_ID);
        accelDeviceMap.put(Accelerometer_Sensor.MAXIMUM_RANGE, Accelerometer_Sensor.MAXIMUM_RANGE);
        accelDeviceMap.put(Accelerometer_Sensor.MINIMUM_DELAY, Accelerometer_Sensor.MINIMUM_DELAY);
        accelDeviceMap.put(Accelerometer_Sensor.NAME, Accelerometer_Sensor.NAME);
        accelDeviceMap.put(Accelerometer_Sensor.POWER_MA, Accelerometer_Sensor.POWER_MA);
        accelDeviceMap.put(Accelerometer_Sensor.RESOLUTION, Accelerometer_Sensor.RESOLUTION);
        accelDeviceMap.put(Accelerometer_Sensor.TYPE, Accelerometer_Sensor.TYPE);
        accelDeviceMap.put(Accelerometer_Sensor.VENDOR, Accelerometer_Sensor.VENDOR);
        accelDeviceMap.put(Accelerometer_Sensor.VERSION, Accelerometer_Sensor.VERSION);

        accelDataMap = new HashMap<>();
        accelDataMap.put(Accelerometer_Data._ID, Accelerometer_Data._ID);
        accelDataMap.put(Accelerometer_Data.TIMESTAMP, Accelerometer_Data.TIMESTAMP);
        accelDataMap.put(Accelerometer_Data.DEVICE_ID, Accelerometer_Data.DEVICE_ID);
        accelDataMap.put(Accelerometer_Data.VALUES_0, Accelerometer_Data.VALUES_0);
        accelDataMap.put(Accelerometer_Data.VALUES_1, Accelerometer_Data.VALUES_1);
        accelDataMap.put(Accelerometer_Data.VALUES_2, Accelerometer_Data.VALUES_2);
        accelDataMap.put(Accelerometer_Data.ACCURACY, Accelerometer_Data.ACCURACY);
        accelDataMap.put(Accelerometer_Data.LABEL, Accelerometer_Data.LABEL);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
