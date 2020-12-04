
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
 * LAMP Gyroscope Content Provider Allows you to access all the recorded
 * gyroscope readings on the database Database is located at the SDCard :
 * /LAMP/gyroscope.db
 *
 * @author denzil
 */
public class Gyroscope_Provider extends ContentProvider {

    /**
     * Authority of Gyroscope content provider
     */
    public static String AUTHORITY = "com.aware.provider.gyroscope";

    // ContentProvider query paths
    private static final int GYRO_DEV = 1;
    private static final int GYRO_DEV_ID = 2;
    private static final int GYRO_DATA = 3;
    private static final int GYRO_DATA_ID = 4;

    /**
     * Accelerometer device info
     *
     * @author denzil
     */
    public static final class Gyroscope_Sensor implements BaseColumns {
        private Gyroscope_Sensor() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Gyroscope_Provider.AUTHORITY + "/sensor_gyroscope");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.gyroscope.sensor";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.gyroscope.sensor";

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

    public static final class Gyroscope_Data implements BaseColumns {
        private Gyroscope_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Gyroscope_Provider.AUTHORITY + "/gyroscope");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.gyroscope.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.gyroscope.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String VALUES_0 = "double_values_0";
        public static final String VALUES_1 = "double_values_1";
        public static final String VALUES_2 = "double_values_2";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "gyroscope.db";

    public static final String[] DATABASE_TABLES = {"sensor_gyroscope",
            "gyroscope"};
    public static final String[] TABLES_FIELDS = {
            // gyroscope device information
            Gyroscope_Sensor._ID + " integer primary key autoincrement,"
                    + Gyroscope_Sensor.TIMESTAMP + " real default 0,"
                    + Gyroscope_Sensor.DEVICE_ID + " text default '',"
                    + Gyroscope_Sensor.MAXIMUM_RANGE + " real default 0,"
                    + Gyroscope_Sensor.MINIMUM_DELAY + " real default 0,"
                    + Gyroscope_Sensor.NAME + " text default '',"
                    + Gyroscope_Sensor.POWER_MA + " real default 0,"
                    + Gyroscope_Sensor.RESOLUTION + " real default 0,"
                    + Gyroscope_Sensor.TYPE + " text default '',"
                    + Gyroscope_Sensor.VENDOR + " text default '',"
                    + Gyroscope_Sensor.VERSION + " text default '',"
                    + "UNIQUE(" + Gyroscope_Sensor.DEVICE_ID + ")",
            // gyroscope data
            Gyroscope_Data._ID + " integer primary key autoincrement,"
                    + Gyroscope_Data.TIMESTAMP + " real default 0,"
                    + Gyroscope_Data.DEVICE_ID + " text default '',"
                    + Gyroscope_Data.VALUES_0 + " real default 0,"
                    + Gyroscope_Data.VALUES_1 + " real default 0,"
                    + Gyroscope_Data.VALUES_2 + " real default 0,"
                    + Gyroscope_Data.ACCURACY + " integer default 0,"
                    + Gyroscope_Data.LABEL + " text default ''"};

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> gyroDeviceMap = null;
    private static HashMap<String, String> gyroDataMap = null;

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case GYRO_DEV:
                return Gyroscope_Sensor.CONTENT_TYPE;
            case GYRO_DEV_ID:
                return Gyroscope_Sensor.CONTENT_ITEM_TYPE;
            case GYRO_DATA:
                return Gyroscope_Data.CONTENT_TYPE;
            case GYRO_DATA_ID:
                return Gyroscope_Data.CONTENT_ITEM_TYPE;
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
        AUTHORITY = context.getPackageName() + ".provider.gyroscope";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.gyroscope";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Gyroscope_Provider.AUTHORITY, DATABASE_TABLES[0],
                GYRO_DEV);
        sUriMatcher.addURI(Gyroscope_Provider.AUTHORITY, DATABASE_TABLES[0]
                + "/#", GYRO_DEV_ID);
        sUriMatcher.addURI(Gyroscope_Provider.AUTHORITY, DATABASE_TABLES[1],
                GYRO_DATA);
        sUriMatcher.addURI(Gyroscope_Provider.AUTHORITY, DATABASE_TABLES[1]
                + "/#", GYRO_DATA_ID);

        gyroDeviceMap = new HashMap<String, String>();
        gyroDeviceMap.put(Gyroscope_Sensor._ID, Gyroscope_Sensor._ID);
        gyroDeviceMap.put(Gyroscope_Sensor.TIMESTAMP,
                Gyroscope_Sensor.TIMESTAMP);
        gyroDeviceMap.put(Gyroscope_Sensor.DEVICE_ID,
                Gyroscope_Sensor.DEVICE_ID);
        gyroDeviceMap.put(Gyroscope_Sensor.MAXIMUM_RANGE,
                Gyroscope_Sensor.MAXIMUM_RANGE);
        gyroDeviceMap.put(Gyroscope_Sensor.MINIMUM_DELAY,
                Gyroscope_Sensor.MINIMUM_DELAY);
        gyroDeviceMap.put(Gyroscope_Sensor.NAME, Gyroscope_Sensor.NAME);
        gyroDeviceMap.put(Gyroscope_Sensor.POWER_MA, Gyroscope_Sensor.POWER_MA);
        gyroDeviceMap.put(Gyroscope_Sensor.RESOLUTION,
                Gyroscope_Sensor.RESOLUTION);
        gyroDeviceMap.put(Gyroscope_Sensor.TYPE, Gyroscope_Sensor.TYPE);
        gyroDeviceMap.put(Gyroscope_Sensor.VENDOR, Gyroscope_Sensor.VENDOR);
        gyroDeviceMap.put(Gyroscope_Sensor.VERSION, Gyroscope_Sensor.VERSION);

        gyroDataMap = new HashMap<String, String>();
        gyroDataMap.put(Gyroscope_Data._ID, Gyroscope_Data._ID);
        gyroDataMap.put(Gyroscope_Data.TIMESTAMP, Gyroscope_Data.TIMESTAMP);
        gyroDataMap.put(Gyroscope_Data.DEVICE_ID, Gyroscope_Data.DEVICE_ID);
        gyroDataMap.put(Gyroscope_Data.VALUES_0, Gyroscope_Data.VALUES_0);
        gyroDataMap.put(Gyroscope_Data.VALUES_1, Gyroscope_Data.VALUES_1);
        gyroDataMap.put(Gyroscope_Data.VALUES_2, Gyroscope_Data.VALUES_2);
        gyroDataMap.put(Gyroscope_Data.ACCURACY, Gyroscope_Data.ACCURACY);
        gyroDataMap.put(Gyroscope_Data.LABEL, Gyroscope_Data.LABEL);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

}