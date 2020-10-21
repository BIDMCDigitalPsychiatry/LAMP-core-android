package com.mindlamp.utils;

/**
 * Created by denzilferreira on 16/02/16.
 */

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mindlamp.Lamp;
import com.mindlamp.Lamp_Preferences;
import com.mindlamp.providers.Lamp_Provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Service that allows plugins/applications to send data to LAMP's dashboard study
 * Note: joins a study without requiring a QRCode, just the study URL
 *
 * TODO: fix parsing of the URL segments that may be missing
 */
public class StudyUtils extends IntentService {

    /**
     * Received broadcast to join a study
     */
    public static final String EXTRA_JOIN_STUDY = "study_url";

    public StudyUtils() {
        super("StudyUtils Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String full_url = intent.getStringExtra(EXTRA_JOIN_STUDY);

        if (Lamp.DEBUG) Log.d(Lamp.TAG, "Joining: " + full_url);

        Uri study_uri = Uri.parse(full_url);

        List<String> path_segments = study_uri.getPathSegments();
        String protocol = study_uri.getScheme();
        String study_api_key = path_segments.get(path_segments.size() - 1);
        String study_id = path_segments.get(path_segments.size() - 2);

        String request;
        if (protocol.equals("https")) {

            SSLManager.handleUrl(getApplicationContext(), full_url, true);

            try {
                request = new Https(SSLManager.getHTTPS(getApplicationContext(), full_url)).dataGET(full_url.substring(0, full_url.indexOf("/index.php")) + "/index.php/webservice/client_get_study_info/" + study_api_key, true);
            } catch (FileNotFoundException e) {
                request = null;
            }
        } else {
            request = new Http().dataGET(full_url.substring(0, full_url.indexOf("/index.php")) + "/index.php/webservice/client_get_study_info/" + study_api_key, true);
        }

        if (request != null) {

            if (request.equals("[]")) return;

            try {
                JSONObject studyInfo = new JSONObject(request);

                //Request study settings
                Hashtable<String, String> data = new Hashtable<>();
                data.put(Lamp_Preferences.DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
                data.put("platform", "android");
                try {
                    PackageInfo package_info = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                    data.put("package_name", package_info.packageName);
                    data.put("package_version_code", String.valueOf(package_info.versionCode));
                    data.put("package_version_name", String.valueOf(package_info.versionName));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d(Lamp.TAG, "Failed to put package info: " + e);
                    e.printStackTrace();
                }

                String answer;
                if (protocol.equals("https")) {
                    try {
                        answer = new Https(SSLManager.getHTTPS(getApplicationContext(), full_url)).dataPOST(full_url, data, true);
                    } catch (FileNotFoundException e) {
                        answer = null;
                    }
                } else {
                    answer = new Http().dataPOST(full_url, data, true);
                }

                if (answer == null) {
                    Toast.makeText(getApplicationContext(), "Failed to connect to server, try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONArray study_config = new JSONArray(answer);

                if (study_config.getJSONObject(0).has("message")) {
                    Toast.makeText(getApplicationContext(), "This study is no longer available.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor dbStudy = Lamp.getStudy(getApplicationContext(), full_url);
                if (Lamp.DEBUG)
                    Log.d(Lamp.TAG, DatabaseUtils.dumpCursorToString(dbStudy));

                if (dbStudy == null || !dbStudy.moveToFirst()) {
                    ContentValues studyData = new ContentValues();
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_TIMESTAMP, System.currentTimeMillis());
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_JOINED, System.currentTimeMillis());
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_KEY, study_id);
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_API, study_api_key);
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_URL, full_url);
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_PI, studyInfo.getString("researcher_first") + " " + studyInfo.getString("researcher_last") + "\nContact: " + studyInfo.getString("researcher_contact"));
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_CONFIG, study_config.toString());
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_TITLE, studyInfo.getString("study_name"));
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_DESCRIPTION, studyInfo.getString("study_description"));

                    getContentResolver().insert(Lamp_Provider.Aware_Studies.CONTENT_URI, studyData);

                    if (Lamp.DEBUG) {
                        Log.d(Lamp.TAG, "New study data: " + studyData.toString());
                    }
                } else {
                    //User rejoined a study he was already part of. Mark as abandoned.
                    ContentValues complianceEntry = new ContentValues();
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_TIMESTAMP, System.currentTimeMillis());
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_KEY, dbStudy.getInt(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_KEY)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_API, dbStudy.getString(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_API)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_URL, dbStudy.getString(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_URL)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_PI, dbStudy.getString(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_PI)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_CONFIG, dbStudy.getString(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_CONFIG)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_JOINED, dbStudy.getLong(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_JOINED)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_EXIT, System.currentTimeMillis());
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_TITLE, dbStudy.getString(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_TITLE)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_DESCRIPTION, dbStudy.getString(dbStudy.getColumnIndex(Lamp_Provider.Aware_Studies.STUDY_DESCRIPTION)));
                    complianceEntry.put(Lamp_Provider.Aware_Studies.STUDY_COMPLIANCE, "rejoined study. abandoning previous");

                    getContentResolver().insert(Lamp_Provider.Aware_Studies.CONTENT_URI, complianceEntry);

                    //Update the information to the latest
                    ContentValues studyData = new ContentValues();
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_TIMESTAMP, System.currentTimeMillis());
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_JOINED, System.currentTimeMillis());
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_KEY, study_id);
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_API, study_api_key);
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_URL, full_url);
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_PI, studyInfo.getString("researcher_first") + " " + studyInfo.getString("researcher_last") + "\nContact: " + studyInfo.getString("researcher_contact"));
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_CONFIG, study_config.toString());
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_TITLE, studyInfo.getString("study_name"));
                    studyData.put(Lamp_Provider.Aware_Studies.STUDY_DESCRIPTION, studyInfo.getString("study_description"));

                    getContentResolver().insert(Lamp_Provider.Aware_Studies.CONTENT_URI, studyData);

                    if (Lamp.DEBUG) {
                        Log.d(Lamp.TAG, "Rejoined study data: " + studyData.toString());
                    }
                }

                if (dbStudy != null && !dbStudy.isClosed()) dbStudy.close();

                applySettings(getApplicationContext(), study_config);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetLogs(Context context){
        context.getContentResolver().delete(Lamp_Provider.Aware_Log.CONTENT_URI, null, null);
        Log.d(Lamp.TAG, "Cleared logs");
    }

    /**
     * Sets first all the settings to the client.
     * If there are plugins, apply the same settings to them.
     * This allows us to add plugins to studies from the dashboard.
     *
     * @param context
     * @param configs
     */
    public static void applySettings(Context context, JSONArray configs) {

        boolean is_developer = Lamp.getSetting(context, Lamp_Preferences.DEBUG_FLAG).equals("true");

        //First reset the client to default settings...
        Lamp.reset(context);

        if (is_developer) Lamp.setSetting(context, Lamp_Preferences.DEBUG_FLAG, true);

        //Now apply the new settings
        JSONArray plugins = new JSONArray();
        JSONArray sensors = new JSONArray();
        JSONArray schedulers = new JSONArray();

        for (int i = 0; i < configs.length(); i++) {
            try {
                JSONObject element = configs.getJSONObject(i);
                if (element.has("plugins")) {
                    plugins = element.getJSONArray("plugins");
                }
                if (element.has("sensors")) {
                    sensors = element.getJSONArray("sensors");
                }
                if (element.has("schedulers")) {
                    schedulers = element.getJSONArray("schedulers");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Set the sensors' settings first
        for (int i = 0; i < sensors.length(); i++) {
            try {
                JSONObject sensor_config = sensors.getJSONObject(i);
                Lamp.setSetting(context, sensor_config.getString("setting"), sensor_config.get("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Set the plugins' settings now
        ArrayList<String> enabled_plugins = new ArrayList<>();
        for (int i = 0; i < plugins.length(); i++) {
            try {
                JSONObject plugin_config = plugins.getJSONObject(i);

                String package_name = plugin_config.getString("plugin");
                JSONArray plugin_settings = plugin_config.getJSONArray("settings");
                for (int j = 0; j < plugin_settings.length(); j++) {
                    JSONObject plugin_setting = plugin_settings.getJSONObject(j);
                    Lamp.setSetting(context, plugin_setting.getString("setting"), plugin_setting.get("value"), package_name);
                    if (plugin_setting.getString("setting").contains("status_") && plugin_setting.get("value").equals("true")) {
                        enabled_plugins.add(package_name);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Set schedulers
        if (schedulers.length() > 0)
            Scheduler.setSchedules(context, schedulers);

        //Start enabled plugins
        for (String package_name : enabled_plugins) {
            PackageInfo installed = PluginsManager.isInstalled(context, package_name);
            if (installed != null) {
                Lamp.startPlugin(context, package_name);
            } else {
                Lamp.downloadPlugin(context, package_name, null, false);
            }
        }

        resetLogs(context);

        Intent aware = new Intent(context, Lamp.class);
        context.startService(aware);

        //Send data to server
        Intent sync = new Intent(Lamp.ACTION_LAMP_SYNC_DATA);
        context.sendBroadcast(sync);
    }
}
