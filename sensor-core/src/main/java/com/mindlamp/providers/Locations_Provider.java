
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
 * LAMP Locations Content Provider Allows you to access all the recorded
 * locations on the database Database is located at the SDCard :
 * /LAMP/locations.db
 *
 * @author denzil
 */
public class Locations_Provider extends ContentProvider {

    /**
     * Authority of Locations content provider
     */
    public static String AUTHORITY = "com.aware.provider.locations";

    // ContentProvider query paths
    private static final int LOCATIONS = 1;
    private static final int LOCATIONS_ID = 2;

    /**
     * Locations content representation
     *
     * @author denzil
     */
    public static final class Locations_Data implements BaseColumns {
        private Locations_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Locations_Provider.AUTHORITY + "/locations");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.locations";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.locations";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String LATITUDE = "double_latitude";
        public static final String LONGITUDE = "double_longitude";
        public static final String BEARING = "double_bearing";
        public static final String SPEED = "double_speed";
        public static final String ALTITUDE = "double_altitude";
        public static final String PROVIDER = "provider";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }

    public static String DATABASE_NAME = "locations.db";

    public static final String[] DATABASE_TABLES = {"locations"};

    public static final String[] TABLES_FIELDS = {
            Locations_Data._ID + " integer primary key autoincrement,"
                    + Locations_Data.TIMESTAMP + " real default 0,"
                    + Locations_Data.DEVICE_ID + " text default '',"
                    + Locations_Data.LATITUDE + " real default 0,"
                    + Locations_Data.LONGITUDE + " real default 0,"
                    + Locations_Data.BEARING + " real default 0,"
                    + Locations_Data.SPEED + " real default 0,"
                    + Locations_Data.ALTITUDE + " real default 0,"
                    + Locations_Data.PROVIDER + " text default '',"
                    + Locations_Data.ACCURACY + " real default 0,"
                    + Locations_Data.LABEL + " text default ''"};

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> locationsProjectionMap = null;

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LOCATIONS:
                return Locations_Data.CONTENT_TYPE;
            case LOCATIONS_ID:
                return Locations_Data.CONTENT_ITEM_TYPE;
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
        AUTHORITY = context.getPackageName() + ".provider.locations";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.locations";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Locations_Provider.AUTHORITY, DATABASE_TABLES[0],
                LOCATIONS);

        locationsProjectionMap = new HashMap<String, String>();
        locationsProjectionMap.put(Locations_Data._ID, Locations_Data._ID);
        locationsProjectionMap.put(Locations_Data.TIMESTAMP,
                Locations_Data.TIMESTAMP);
        locationsProjectionMap.put(Locations_Data.DEVICE_ID,
                Locations_Data.DEVICE_ID);
        locationsProjectionMap.put(Locations_Data.LATITUDE,
                Locations_Data.LATITUDE);
        locationsProjectionMap.put(Locations_Data.LONGITUDE,
                Locations_Data.LONGITUDE);
        locationsProjectionMap.put(Locations_Data.BEARING,
                Locations_Data.BEARING);
        locationsProjectionMap.put(Locations_Data.SPEED, Locations_Data.SPEED);
        locationsProjectionMap.put(Locations_Data.ALTITUDE,
                Locations_Data.ALTITUDE);
        locationsProjectionMap.put(Locations_Data.PROVIDER,
                Locations_Data.PROVIDER);
        locationsProjectionMap.put(Locations_Data.ACCURACY,
                Locations_Data.ACCURACY);
        locationsProjectionMap.put(Locations_Data.LABEL, Locations_Data.LABEL);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

}