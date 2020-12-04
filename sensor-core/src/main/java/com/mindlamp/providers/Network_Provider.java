
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
 * LAMP Network Content Provider Allows you to access all the recorded network
 * events on the database Database is located at the SDCard : /LAMP/network.db
 *
 * @author denzil
 */
public class Network_Provider extends ContentProvider {

    public static final int DATABASE_VERSION = 3;

    /**
     * Authority of Screen content provider
     */
    public static String AUTHORITY = "com.aware.provider.network";

    // ContentProvider query paths
    private static final int NETWORK = 1;
    private static final int NETWORK_ID = 2;

    /**
     * Network content representation
     *
     * @author denzil
     */
    public static final class Network_Data implements BaseColumns {
        private Network_Data() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Network_Provider.AUTHORITY + "/network");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.network";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.network";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String TYPE = "network_type";
        public static final String SUBTYPE = "network_subtype";
        public static final String STATE = "network_state";
    }

    public static String DATABASE_NAME = "network.db";

    public static final String[] DATABASE_TABLES = {"network"};
    public static final String[] TABLES_FIELDS = {
            // network
            Network_Data._ID + " integer primary key autoincrement,"
                    + Network_Data.TIMESTAMP + " real default 0,"
                    + Network_Data.DEVICE_ID + " text default '',"
                    + Network_Data.TYPE + " integer default 0,"
                    + Network_Data.SUBTYPE + " text default '',"
                    + Network_Data.STATE + " integer default 0"
    };

    private UriMatcher sUriMatcher = null;
    private HashMap<String, String> networkProjectionMap = null;

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NETWORK:
                return Network_Data.CONTENT_TYPE;
            case NETWORK_ID:
                return Network_Data.CONTENT_ITEM_TYPE;
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
        AUTHORITY = context.getPackageName() + ".provider.network";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.network";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Network_Provider.AUTHORITY, DATABASE_TABLES[0],
                NETWORK);
        sUriMatcher.addURI(Network_Provider.AUTHORITY, DATABASE_TABLES[0]
                + "/#", NETWORK_ID);

        networkProjectionMap = new HashMap<String, String>();
        networkProjectionMap.put(Network_Data._ID, Network_Data._ID);
        networkProjectionMap
                .put(Network_Data.TIMESTAMP, Network_Data.TIMESTAMP);
        networkProjectionMap
                .put(Network_Data.DEVICE_ID, Network_Data.DEVICE_ID);
        networkProjectionMap.put(Network_Data.TYPE, Network_Data.TYPE);
        networkProjectionMap.put(Network_Data.SUBTYPE, Network_Data.SUBTYPE);
        networkProjectionMap.put(Network_Data.STATE, Network_Data.STATE);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

}