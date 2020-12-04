
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
 * database Database is located at the SDCard : /LAMP/barometer.db
 *
 * @author denzil
 */
public class Barometer_Provider extends ContentProvider {

    /**
     * Authority of content provider
     */
    public static String AUTHORITY = "com.aware.provider.barometer";

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
    public static final class Barometer_Sensor implements BaseColumns {
        private Barometer_Sensor() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Barometer_Provider.AUTHORITY + "/sensor_barometer");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.barometer.sensor";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.barometer.sensor";

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
    public static final class Barometer_Data implements BaseColumns {
        private Barometer_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Barometer_Provider.AUTHORITY + "/barometer");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.barometer.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.barometer.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String AMBIENT_PRESSURE = "double_values_0";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "barometer.db";
    public static final String[] DATABASE_TABLES = {"sensor_barometer",
            "barometer"};
    public static final String[] TABLES_FIELDS = {
            // sensor device information
            Barometer_Sensor._ID + " integer primary key autoincrement,"
                    + Barometer_Sensor.TIMESTAMP + " real default 0,"
                    + Barometer_Sensor.DEVICE_ID + " text default '',"
                    + Barometer_Sensor.MAXIMUM_RANGE + " real default 0,"
                    + Barometer_Sensor.MINIMUM_DELAY + " real default 0,"
                    + Barometer_Sensor.NAME + " text default '',"
                    + Barometer_Sensor.POWER_MA + " real default 0,"
                    + Barometer_Sensor.RESOLUTION + " real default 0,"
                    + Barometer_Sensor.TYPE + " text default '',"
                    + Barometer_Sensor.VENDOR + " text default '',"
                    + Barometer_Sensor.VERSION + " text default '',"
                    + "UNIQUE(" + Barometer_Sensor.DEVICE_ID + ")",
            // sensor data
            Barometer_Data._ID + " integer primary key autoincrement,"
                    + Barometer_Data.TIMESTAMP + " real default 0,"
                    + Barometer_Data.DEVICE_ID + " text default '',"
                    + Barometer_Data.AMBIENT_PRESSURE + " real default 0,"
                    + Barometer_Data.ACCURACY + " integer default 0,"
                    + Barometer_Data.LABEL + " text default ''"};

    private UriMatcher sUriMatcher = null;
    private HashMap<String, String> sensorMap = null;
    private HashMap<String, String> sensorDataMap = null;

    /**
     * Returns the provider authority that is dynamic
     * @return
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider.barometer";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.barometer";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Barometer_Provider.AUTHORITY, DATABASE_TABLES[0],
                SENSOR_DEV);
        sUriMatcher.addURI(Barometer_Provider.AUTHORITY, DATABASE_TABLES[0]
                + "/#", SENSOR_DEV_ID);
        sUriMatcher.addURI(Barometer_Provider.AUTHORITY, DATABASE_TABLES[1],
                SENSOR_DATA);
        sUriMatcher.addURI(Barometer_Provider.AUTHORITY, DATABASE_TABLES[1]
                + "/#", SENSOR_DATA_ID);

        sensorMap = new HashMap<String, String>();
        sensorMap.put(Barometer_Sensor._ID, Barometer_Sensor._ID);
        sensorMap.put(Barometer_Sensor.TIMESTAMP, Barometer_Sensor.TIMESTAMP);
        sensorMap.put(Barometer_Sensor.DEVICE_ID, Barometer_Sensor.DEVICE_ID);
        sensorMap.put(Barometer_Sensor.MAXIMUM_RANGE,
                Barometer_Sensor.MAXIMUM_RANGE);
        sensorMap.put(Barometer_Sensor.MINIMUM_DELAY,
                Barometer_Sensor.MINIMUM_DELAY);
        sensorMap.put(Barometer_Sensor.NAME, Barometer_Sensor.NAME);
        sensorMap.put(Barometer_Sensor.POWER_MA, Barometer_Sensor.POWER_MA);
        sensorMap.put(Barometer_Sensor.RESOLUTION, Barometer_Sensor.RESOLUTION);
        sensorMap.put(Barometer_Sensor.TYPE, Barometer_Sensor.TYPE);
        sensorMap.put(Barometer_Sensor.VENDOR, Barometer_Sensor.VENDOR);
        sensorMap.put(Barometer_Sensor.VERSION, Barometer_Sensor.VERSION);

        sensorDataMap = new HashMap<String, String>();
        sensorDataMap.put(Barometer_Data._ID, Barometer_Data._ID);
        sensorDataMap.put(Barometer_Data.TIMESTAMP, Barometer_Data.TIMESTAMP);
        sensorDataMap.put(Barometer_Data.DEVICE_ID, Barometer_Data.DEVICE_ID);
        sensorDataMap.put(Barometer_Data.AMBIENT_PRESSURE,
                Barometer_Data.AMBIENT_PRESSURE);
        sensorDataMap.put(Barometer_Data.ACCURACY, Barometer_Data.ACCURACY);
        sensorDataMap.put(Barometer_Data.LABEL, Barometer_Data.LABEL);

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