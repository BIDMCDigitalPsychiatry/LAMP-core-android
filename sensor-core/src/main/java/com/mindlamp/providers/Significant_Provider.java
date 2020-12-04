
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
public class Significant_Provider {

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


    /**
     * Returns the provider authority that is dynamic
     * @return
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider.significant";
        return AUTHORITY;
    }


}
