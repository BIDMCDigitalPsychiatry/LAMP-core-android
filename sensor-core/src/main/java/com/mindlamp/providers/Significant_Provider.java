
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
 * database Database is located at the SDCard : /LAMP/temperature.db
 *
 * @author denzil
 */
public class Significant_Provider extends ContentProvider {

    /**
     * Authority of content provider
     */
    public static String AUTHORITY = "com.aware.provider.significant";

    // ContentProvider query paths
    private static final int SENSOR_DATA = 1;
    private static final int SENSOR_DATA_ID = 2;

    /**
     * Logged sensor data
     *
     * @author df
     */
    public static final class Significant_Data implements BaseColumns {
        private Significant_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + Significant_Provider.AUTHORITY + "/significant");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.significant.data";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.significant.data";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String IS_MOVING = "is_moving";
    }

    public static String DATABASE_NAME = "significant.db";

    public static final String[] DATABASE_TABLES = {"significant"};

    public static final String[] TABLES_FIELDS = {
            // sensor data
            Significant_Data._ID + " integer primary key autoincrement,"
                    + Significant_Data.TIMESTAMP + " real default 0,"
                    + Significant_Data.DEVICE_ID + " text default '',"
                    + Significant_Data.IS_MOVING + " integer default 0"
    };

    private UriMatcher sUriMatcher = null;
    private HashMap<String, String> sensorDataMap = null;

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SENSOR_DATA:
                return Significant_Data.CONTENT_TYPE;
            case SENSOR_DATA_ID:
                return Significant_Data.CONTENT_ITEM_TYPE;
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
        AUTHORITY = context.getPackageName() + ".provider.significant";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.significant";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Significant_Provider.AUTHORITY, DATABASE_TABLES[0], SENSOR_DATA);
        sUriMatcher.addURI(Significant_Provider.AUTHORITY, DATABASE_TABLES[0] + "/#", SENSOR_DATA_ID);

        sensorDataMap = new HashMap<>();
        sensorDataMap.put(Significant_Data._ID, Significant_Data._ID);
        sensorDataMap.put(Significant_Data.TIMESTAMP,
                Significant_Data.TIMESTAMP);
        sensorDataMap.put(Significant_Data.DEVICE_ID,
                Significant_Data.DEVICE_ID);
        sensorDataMap.put(Significant_Data.IS_MOVING,
                Significant_Data.IS_MOVING);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }
}
