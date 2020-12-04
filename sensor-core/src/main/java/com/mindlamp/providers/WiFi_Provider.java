
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
 * LAMP WiFi Content Provider Allows you to access all the recorded wifi AP
 * devices on the database Database is located at the SDCard : /LAMP/wifi.db
 *
 * @author denzil
 */
public class WiFi_Provider extends ContentProvider {

    /**
     * Authority of WiFi content provider
     */
    public static String AUTHORITY = "com.aware.provider.wifi";

    // ContentProvider query paths
    private static final int WIFI_DATA = 1;
    private static final int WIFI_DATA_ID = 2;
    private static final int WIFI_DEV = 3;
    private static final int WIFI_DEV_ID = 4;

    /**
     * WiFi device info
     *
     * @author df
     */
    public static final class WiFi_Sensor implements BaseColumns {
        private WiFi_Sensor() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + WiFi_Provider.AUTHORITY + "/sensor_wifi");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.wifi.sensor";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.wifi.sensor";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String MAC_ADDRESS = "mac_address";
        public static final String BSSID = "bssid";
        public static final String SSID = "ssid";
    }

    /**
     * Logged WiFi data
     *
     * @author df
     */
    public static final class WiFi_Data implements BaseColumns {
        private WiFi_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + WiFi_Provider.AUTHORITY + "/wifi");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.wifi.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.wifi.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String BSSID = "bssid";
        public static final String SSID = "ssid";
        public static final String SECURITY = "security";
        public static final String FREQUENCY = "frequency";
        public static final String RSSI = "rssi";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "wifi.db";

    public static final String[] DATABASE_TABLES = {"wifi", "sensor_wifi"};

    public static final String[] TABLES_FIELDS = {
            // data
            WiFi_Data._ID + " integer primary key autoincrement,"
                    + WiFi_Data.TIMESTAMP + " real default 0,"
                    + WiFi_Data.DEVICE_ID + " text default '',"
                    + WiFi_Data.BSSID + " text default '',"
                    + WiFi_Data.SSID + " text default '',"
                    + WiFi_Data.SECURITY + " text default '',"
                    + WiFi_Data.FREQUENCY + " integer default 0,"
                    + WiFi_Data.RSSI + " integer default 0,"
                    + WiFi_Data.LABEL + " text default ''",
            // device
            WiFi_Sensor._ID + " integer primary key autoincrement,"
                    + WiFi_Sensor.TIMESTAMP + " real default 0,"
                    + WiFi_Sensor.DEVICE_ID + " text default '',"
                    + WiFi_Sensor.MAC_ADDRESS + " text default '',"
                    + WiFi_Data.SSID + " text default '',"
                    + WiFi_Data.BSSID + " text default ''"};

    private UriMatcher sUriMatcher = null;
    private HashMap<String, String> wifiDataMap = null;
    private HashMap<String, String> wifiDeviceMap = null;


    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WIFI_DATA:
                return WiFi_Data.CONTENT_TYPE;
            case WIFI_DATA_ID:
                return WiFi_Data.CONTENT_ITEM_TYPE;
            case WIFI_DEV:
                return WiFi_Data.CONTENT_TYPE;
            case WIFI_DEV_ID:
                return WiFi_Data.CONTENT_ITEM_TYPE;
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
        AUTHORITY = context.getPackageName() + ".provider.wifi";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.wifi";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(WiFi_Provider.AUTHORITY, DATABASE_TABLES[0],
                WIFI_DATA);
        sUriMatcher.addURI(WiFi_Provider.AUTHORITY, DATABASE_TABLES[0] + "/#",
                WIFI_DATA_ID);
        sUriMatcher.addURI(WiFi_Provider.AUTHORITY, DATABASE_TABLES[1],
                WIFI_DEV);
        sUriMatcher.addURI(WiFi_Provider.AUTHORITY, DATABASE_TABLES[1] + "/#",
                WIFI_DEV_ID);

        wifiDataMap = new HashMap<String, String>();
        wifiDataMap.put(WiFi_Data._ID, WiFi_Data._ID);
        wifiDataMap.put(WiFi_Data.TIMESTAMP, WiFi_Data.TIMESTAMP);
        wifiDataMap.put(WiFi_Data.DEVICE_ID, WiFi_Data.DEVICE_ID);
        wifiDataMap.put(WiFi_Data.BSSID, WiFi_Data.BSSID);
        wifiDataMap.put(WiFi_Data.SSID, WiFi_Data.SSID);
        wifiDataMap.put(WiFi_Data.SECURITY, WiFi_Data.SECURITY);
        wifiDataMap.put(WiFi_Data.FREQUENCY, WiFi_Data.FREQUENCY);
        wifiDataMap.put(WiFi_Data.RSSI, WiFi_Data.RSSI);
        wifiDataMap.put(WiFi_Data.LABEL, WiFi_Data.LABEL);

        wifiDeviceMap = new HashMap<String, String>();
        wifiDeviceMap.put(WiFi_Sensor._ID, WiFi_Sensor._ID);
        wifiDeviceMap.put(WiFi_Sensor.DEVICE_ID, WiFi_Sensor.DEVICE_ID);
        wifiDeviceMap.put(WiFi_Sensor.TIMESTAMP, WiFi_Sensor.TIMESTAMP);
        wifiDeviceMap.put(WiFi_Sensor.MAC_ADDRESS, WiFi_Sensor.MAC_ADDRESS);
        wifiDeviceMap.put(WiFi_Sensor.BSSID, WiFi_Sensor.BSSID);
        wifiDeviceMap.put(WiFi_Sensor.SSID, WiFi_Sensor.SSID);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

}
