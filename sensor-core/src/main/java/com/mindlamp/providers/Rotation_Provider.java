
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
 * LAMP Rotation Content Provider Allows you to access all the recorded
 * rotation readings on the database Database is located at the SDCard :
 * /LAMP/rotation.db
 *
 * @author denzil
 */
public class Rotation_Provider extends ContentProvider {

    /**
     * Authority of content provider
     */
    public static String AUTHORITY = "com.aware.provider.rotation";

    // ContentProvider query paths
    private static final int SENSOR_DEV = 1;
    private static final int SENSOR_DEV_ID = 2;
    private static final int SENSOR_DATA = 3;
    private static final int SENSOR_DATA_ID = 4;

    /**
     * Sensor device info
     *
     * @author denzil
     */
    public static final class Rotation_Sensor implements BaseColumns {
        private Rotation_Sensor() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Rotation_Provider.AUTHORITY + "/sensor_rotation");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.rotation.sensor";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.rotation.sensor";

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
     * Sensor data
     *
     * @author df
     */
    public static final class Rotation_Data implements BaseColumns {
        private Rotation_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Rotation_Provider.AUTHORITY + "/rotation");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.rotation.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.rotation.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String VALUES_0 = "double_values_0";
        public static final String VALUES_1 = "double_values_1";
        public static final String VALUES_2 = "double_values_2";
        public static final String VALUES_3 = "double_values_3";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "rotation.db";
    public static final String[] DATABASE_TABLES = {"sensor_rotation",
            "rotation"};
    public static final String[] TABLES_FIELDS = {
            // sensor device information
            Rotation_Sensor._ID + " integer primary key autoincrement,"
                    + Rotation_Sensor.TIMESTAMP + " real default 0,"
                    + Rotation_Sensor.DEVICE_ID + " text default '',"
                    + Rotation_Sensor.MAXIMUM_RANGE + " real default 0,"
                    + Rotation_Sensor.MINIMUM_DELAY + " real default 0,"
                    + Rotation_Sensor.NAME + " text default '',"
                    + Rotation_Sensor.POWER_MA + " real default 0,"
                    + Rotation_Sensor.RESOLUTION + " real default 0,"
                    + Rotation_Sensor.TYPE + " text default '',"
                    + Rotation_Sensor.VENDOR + " text default '',"
                    + Rotation_Sensor.VERSION + " text default '',"
                    + "UNIQUE(" + Rotation_Sensor.DEVICE_ID + ")",
            // sensor data
            Rotation_Data._ID + " integer primary key autoincrement,"
                    + Rotation_Data.TIMESTAMP + " real default 0,"
                    + Rotation_Data.DEVICE_ID + " text default '',"
                    + Rotation_Data.VALUES_0 + " real default 0,"
                    + Rotation_Data.VALUES_1 + " real default 0,"
                    + Rotation_Data.VALUES_2 + " real default 0,"
                    + Rotation_Data.VALUES_3 + " real default 0,"
                    + Rotation_Data.ACCURACY + " integer default 0,"
                    + Rotation_Data.LABEL + " text default ''"};

    private UriMatcher sUriMatcher = null;
    private HashMap<String, String> sensorMap = null;
    private HashMap<String, String> sensorDataMap = null;

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SENSOR_DEV:
                return Rotation_Sensor.CONTENT_TYPE;
            case SENSOR_DEV_ID:
                return Rotation_Sensor.CONTENT_ITEM_TYPE;
            case SENSOR_DATA:
                return Rotation_Data.CONTENT_TYPE;
            case SENSOR_DATA_ID:
                return Rotation_Data.CONTENT_ITEM_TYPE;
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
        AUTHORITY = context.getPackageName() + ".provider.rotation";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.rotation";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Rotation_Provider.AUTHORITY, DATABASE_TABLES[0],
                SENSOR_DEV);
        sUriMatcher.addURI(Rotation_Provider.AUTHORITY, DATABASE_TABLES[0]
                + "/#", SENSOR_DEV_ID);
        sUriMatcher.addURI(Rotation_Provider.AUTHORITY, DATABASE_TABLES[1],
                SENSOR_DATA);
        sUriMatcher.addURI(Rotation_Provider.AUTHORITY, DATABASE_TABLES[1]
                + "/#", SENSOR_DATA_ID);

        sensorMap = new HashMap<String, String>();
        sensorMap.put(Rotation_Sensor._ID, Rotation_Sensor._ID);
        sensorMap.put(Rotation_Sensor.TIMESTAMP, Rotation_Sensor.TIMESTAMP);
        sensorMap.put(Rotation_Sensor.DEVICE_ID, Rotation_Sensor.DEVICE_ID);
        sensorMap.put(Rotation_Sensor.MAXIMUM_RANGE,
                Rotation_Sensor.MAXIMUM_RANGE);
        sensorMap.put(Rotation_Sensor.MINIMUM_DELAY,
                Rotation_Sensor.MINIMUM_DELAY);
        sensorMap.put(Rotation_Sensor.NAME, Rotation_Sensor.NAME);
        sensorMap.put(Rotation_Sensor.POWER_MA, Rotation_Sensor.POWER_MA);
        sensorMap.put(Rotation_Sensor.RESOLUTION, Rotation_Sensor.RESOLUTION);
        sensorMap.put(Rotation_Sensor.TYPE, Rotation_Sensor.TYPE);
        sensorMap.put(Rotation_Sensor.VENDOR, Rotation_Sensor.VENDOR);
        sensorMap.put(Rotation_Sensor.VERSION, Rotation_Sensor.VERSION);

        sensorDataMap = new HashMap<String, String>();
        sensorDataMap.put(Rotation_Data._ID, Rotation_Data._ID);
        sensorDataMap.put(Rotation_Data.TIMESTAMP, Rotation_Data.TIMESTAMP);
        sensorDataMap.put(Rotation_Data.DEVICE_ID, Rotation_Data.DEVICE_ID);
        sensorDataMap.put(Rotation_Data.VALUES_0, Rotation_Data.VALUES_0);
        sensorDataMap.put(Rotation_Data.VALUES_1, Rotation_Data.VALUES_1);
        sensorDataMap.put(Rotation_Data.VALUES_2, Rotation_Data.VALUES_2);
        sensorDataMap.put(Rotation_Data.VALUES_3, Rotation_Data.VALUES_3);
        sensorDataMap.put(Rotation_Data.ACCURACY, Rotation_Data.ACCURACY);
        sensorDataMap.put(Rotation_Data.LABEL, Rotation_Data.LABEL);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

}
