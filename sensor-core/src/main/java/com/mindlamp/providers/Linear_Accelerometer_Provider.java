
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
 * LAMP Content Provider Allows you to access all the recorded readings on the
 * database Database is located at the SDCard : /LAMP/linear_accelerometer.db
 *
 * @author denzil
 */
public class Linear_Accelerometer_Provider extends ContentProvider {

    /**
     * Authority of content provider
     */
    public static String AUTHORITY = "com.mindlamp.provider.sync_accelerometer.linear";

    // ContentProvider query paths
    private static final int ACCEL_DEV = 1;
    private static final int ACCEL_DEV_ID = 2;
    private static final int ACCEL_DATA = 3;
    private static final int ACCEL_DATA_ID = 4;

    /**
     * Sensor device info
     *
     * @author denzil
     */
    public static final class Linear_Accelerometer_Sensor implements
            BaseColumns {
        private Linear_Accelerometer_Sensor() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Linear_Accelerometer_Provider.AUTHORITY
                + "/sensor_linear_accelerometer");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.accelerometer.linear.sensor";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.accelerometer.linear.sensor";

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
     * Logged sensor data
     *
     * @author df
     */
    public static final class Linear_Accelerometer_Data implements BaseColumns {
        private Linear_Accelerometer_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Linear_Accelerometer_Provider.AUTHORITY
                + "/linear_accelerometer");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.accelerometer.linear.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.accelerometer.linear.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String VALUES_0 = "double_values_0";
        public static final String VALUES_1 = "double_values_1";
        public static final String VALUES_2 = "double_values_2";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "linear_accelerometer.db";

    public static final String[] DATABASE_TABLES = {
            "sensor_linear_accelerometer", "linear_accelerometer"};
    public static final String[] TABLES_FIELDS = {
            // sensor information
            Linear_Accelerometer_Sensor._ID + " integer primary key autoincrement,"
                    + Linear_Accelerometer_Sensor.TIMESTAMP + " real default 0,"
                    + Linear_Accelerometer_Sensor.DEVICE_ID + " text default '',"
                    + Linear_Accelerometer_Sensor.MAXIMUM_RANGE + " real default 0,"
                    + Linear_Accelerometer_Sensor.MINIMUM_DELAY + " real default 0,"
                    + Linear_Accelerometer_Sensor.NAME + " text default '',"
                    + Linear_Accelerometer_Sensor.POWER_MA + " real default 0,"
                    + Linear_Accelerometer_Sensor.RESOLUTION + " real default 0,"
                    + Linear_Accelerometer_Sensor.TYPE + " text default '',"
                    + Linear_Accelerometer_Sensor.VENDOR + " text default '',"
                    + Linear_Accelerometer_Sensor.VERSION + " text default '',"
                    + "UNIQUE(" + Linear_Accelerometer_Sensor.DEVICE_ID + ")",
            // sensor data
            Linear_Accelerometer_Data._ID
                    + " integer primary key autoincrement,"
                    + Linear_Accelerometer_Data.TIMESTAMP + " real default 0,"
                    + Linear_Accelerometer_Data.DEVICE_ID + " text default '',"
                    + Linear_Accelerometer_Data.VALUES_0 + " real default 0,"
                    + Linear_Accelerometer_Data.VALUES_1 + " real default 0,"
                    + Linear_Accelerometer_Data.VALUES_2 + " real default 0,"
                    + Linear_Accelerometer_Data.ACCURACY + " integer default 0,"
                    + Linear_Accelerometer_Data.LABEL + " text default ''"};

    private UriMatcher sUriMatcher = null;
    private HashMap<String, String> accelDeviceMap = null;
    private HashMap<String, String> accelDataMap = null;

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ACCEL_DEV:
                return Linear_Accelerometer_Sensor.CONTENT_TYPE;
            case ACCEL_DEV_ID:
                return Linear_Accelerometer_Sensor.CONTENT_ITEM_TYPE;
            case ACCEL_DATA:
                return Linear_Accelerometer_Data.CONTENT_TYPE;
            case ACCEL_DATA_ID:
                return Linear_Accelerometer_Data.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
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

    /**
     * Returns the provider authority that is dynamic
     * @return
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider.accelerometer.linear";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.accelerometer.linear";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Linear_Accelerometer_Provider.AUTHORITY,
                DATABASE_TABLES[0], ACCEL_DEV);
        sUriMatcher.addURI(Linear_Accelerometer_Provider.AUTHORITY,
                DATABASE_TABLES[0] + "/#", ACCEL_DEV_ID);
        sUriMatcher.addURI(Linear_Accelerometer_Provider.AUTHORITY,
                DATABASE_TABLES[1], ACCEL_DATA);
        sUriMatcher.addURI(Linear_Accelerometer_Provider.AUTHORITY,
                DATABASE_TABLES[1] + "/#", ACCEL_DATA_ID);

        accelDeviceMap = new HashMap<String, String>();
        accelDeviceMap.put(Linear_Accelerometer_Sensor._ID,
                Linear_Accelerometer_Sensor._ID);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.TIMESTAMP,
                Linear_Accelerometer_Sensor.TIMESTAMP);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.DEVICE_ID,
                Linear_Accelerometer_Sensor.DEVICE_ID);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.MAXIMUM_RANGE,
                Linear_Accelerometer_Sensor.MAXIMUM_RANGE);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.MINIMUM_DELAY,
                Linear_Accelerometer_Sensor.MINIMUM_DELAY);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.NAME,
                Linear_Accelerometer_Sensor.NAME);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.POWER_MA,
                Linear_Accelerometer_Sensor.POWER_MA);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.RESOLUTION,
                Linear_Accelerometer_Sensor.RESOLUTION);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.TYPE,
                Linear_Accelerometer_Sensor.TYPE);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.VENDOR,
                Linear_Accelerometer_Sensor.VENDOR);
        accelDeviceMap.put(Linear_Accelerometer_Sensor.VERSION,
                Linear_Accelerometer_Sensor.VERSION);

        accelDataMap = new HashMap<String, String>();
        accelDataMap.put(Linear_Accelerometer_Data._ID,
                Linear_Accelerometer_Data._ID);
        accelDataMap.put(Linear_Accelerometer_Data.TIMESTAMP,
                Linear_Accelerometer_Data.TIMESTAMP);
        accelDataMap.put(Linear_Accelerometer_Data.DEVICE_ID,
                Linear_Accelerometer_Data.DEVICE_ID);
        accelDataMap.put(Linear_Accelerometer_Data.VALUES_0,
                Linear_Accelerometer_Data.VALUES_0);
        accelDataMap.put(Linear_Accelerometer_Data.VALUES_1,
                Linear_Accelerometer_Data.VALUES_1);
        accelDataMap.put(Linear_Accelerometer_Data.VALUES_2,
                Linear_Accelerometer_Data.VALUES_2);
        accelDataMap.put(Linear_Accelerometer_Data.ACCURACY,
                Linear_Accelerometer_Data.ACCURACY);
        accelDataMap.put(Linear_Accelerometer_Data.LABEL,
                Linear_Accelerometer_Data.LABEL);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }
}
