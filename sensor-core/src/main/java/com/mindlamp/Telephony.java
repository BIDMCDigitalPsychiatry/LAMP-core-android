
package com.mindlamp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SyncRequest;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.mindlamp.providers.Telephony_Provider;
import com.mindlamp.providers.Telephony_Provider.CDMA_Data;
import com.mindlamp.providers.Telephony_Provider.GSM_Data;
import com.mindlamp.providers.Telephony_Provider.GSM_Neighbors_Data;
import com.mindlamp.providers.Telephony_Provider.Telephony_Data;
import com.mindlamp.utils.Lamp_Sensor;
import com.mindlamp.utils.Encrypter;

import java.util.List;

/**
 * Telephony module. Keeps track of changes in the network operator and information:
 * - Current network operator information
 * - Cell Location ID's
 * - Neighbor cell towers
 * - Signal strength
 *
 * @author denzil
 */
public class Telephony extends Lamp_Sensor {

    private static String TAG = "LAMP::Telephony";

    private TelephonyManager telephonyManager = null;
    private TelephonyState telephonyState = new TelephonyState();
    private static SignalStrength lastSignalStrength = null;

    /**
     * Broadcasted event: new telephony information is available
     */
    public static final String ACTION_LAMP_TELEPHONY = "ACTION_LAMP_TELEPHONY";

    /**
     * Broadcasted event: connected to a new CDMA tower
     */
    public static final String ACTION_LAMP_CDMA_TOWER = "ACTION_LAMP_CDMA_TOWER";

    /**
     * Broadcasted event: connected to a new GSM tower
     */
    public static final String ACTION_LAMP_GSM_TOWER = "ACTION_LAMP_GSM_TOWER";

    /**
     * Broadcasted event: detected GSM tower neighbor
     */
    public static final String ACTION_LAMP_GSM_TOWER_NEIGHBOR = "ACTION_LAMP_GSM_TOWER_NEIGHBOR";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static Telephony.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Telephony.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Telephony.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onSignalStrengthChanged(SignalStrength strength);

        void onCellChanged(CellLocation cellLocation);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = Telephony_Provider.getAuthority(this);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION); //needed to get the cell towers positions
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE); //needed for tracking signal strength

        if (Lamp.DEBUG) Log.d(TAG, "Telephony service created!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");
            Lamp.setSetting(this, Lamp_Preferences.STATUS_TELEPHONY, true);

            telephonyManager.listen(telephonyState, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            if (Lamp.DEBUG) Log.d(TAG, "Telephony service active...");

            if (Lamp.isStudy(this)) {
                ContentResolver.setIsSyncable(Lamp.getLAMPAccount(this), Telephony_Provider.getAuthority(this), 1);
                ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), Telephony_Provider.getAuthority(this), true);
                long frequency = Long.parseLong(Lamp.getSetting(this, Lamp_Preferences.FREQUENCY_WEBSERVICE)) * 60;
                SyncRequest request = new SyncRequest.Builder()
                        .syncPeriodic(frequency, frequency / 3)
                        .setSyncAdapter(Lamp.getLAMPAccount(this), Telephony_Provider.getAuthority(this))
                        .setExtras(new Bundle()).build();
                ContentResolver.requestSync(request);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        telephonyManager.listen(telephonyState, PhoneStateListener.LISTEN_NONE);

        ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), Telephony_Provider.getAuthority(this), false);
        ContentResolver.removePeriodicSync(
                Lamp.getLAMPAccount(this),
                Telephony_Provider.getAuthority(this),
                Bundle.EMPTY
        );

        if (Lamp.DEBUG) Log.d(TAG, "Telephony service terminated...");
    }

    /**
     * Tracks cell location and telephony information:
     * - GSM: CID, LAC, PSC (UMTS Primary Scrambling Code)
     * - CDMA: base station ID, Latitude, Longitude, Network ID, System ID
     * - Telephony: IMEI/MEID/ESN, software version, line number, network MMC, network code, network name, network type, phone type, sim code, sim operator, sim serial, subscriber ID
     *
     * @author df
     */
    public class TelephonyState extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            if (awareSensor != null) awareSensor.onSignalStrengthChanged(signalStrength);

            lastSignalStrength = signalStrength;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);

            if (awareSensor != null) awareSensor.onCellChanged(location);

            if (lastSignalStrength == null) return;

            String device_id = Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID);

            if (location instanceof GsmCellLocation) {
                GsmCellLocation loc = (GsmCellLocation) location;

                long timestamp = System.currentTimeMillis();

                ContentValues rowData = new ContentValues();
                rowData.put(GSM_Data.TIMESTAMP, timestamp);
                rowData.put(GSM_Data.DEVICE_ID, device_id);
                rowData.put(GSM_Data.CID, loc.getCid());
                rowData.put(GSM_Data.LAC, loc.getLac());
                rowData.put(GSM_Data.PSC, loc.getPsc());
                rowData.put(GSM_Data.SIGNAL_STRENGTH, lastSignalStrength.getGsmSignalStrength());
                rowData.put(GSM_Data.GSM_BER, lastSignalStrength.getGsmBitErrorRate());

                try {
                    getContentResolver().insert(GSM_Data.CONTENT_URI, rowData);

                    Intent newGSM = new Intent(Telephony.ACTION_LAMP_GSM_TOWER);
                    sendBroadcast(newGSM);

                    if (Lamp.DEBUG) Log.d(TAG, "GSM tower:" + rowData.toString());
                } catch (SQLiteException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                } catch (SQLException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                }

                List<NeighboringCellInfo> neighbors = null;
                if (neighbors != null && neighbors.size() > 0) {
                    for (NeighboringCellInfo neighbor : neighbors) {
                        rowData = new ContentValues();
                        rowData.put(GSM_Neighbors_Data.TIMESTAMP, timestamp);
                        rowData.put(GSM_Neighbors_Data.DEVICE_ID, device_id);
                        rowData.put(GSM_Neighbors_Data.CID, neighbor.getCid());
                        rowData.put(GSM_Neighbors_Data.LAC, neighbor.getLac());
                        rowData.put(GSM_Neighbors_Data.PSC, neighbor.getPsc());
                        rowData.put(GSM_Neighbors_Data.SIGNAL_STRENGTH, neighbor.getRssi());

                        try {
                            getContentResolver().insert(GSM_Neighbors_Data.CONTENT_URI, rowData);

                            Intent newGSMNeighbor = new Intent(Telephony.ACTION_LAMP_GSM_TOWER_NEIGHBOR);
                            sendBroadcast(newGSMNeighbor);

                            if (Lamp.DEBUG) Log.d(TAG, "GSM tower neighbor:" + rowData.toString());
                        } catch (SQLiteException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        } catch (SQLException e) {
                            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                        }
                    }
                }

            } else {
                CdmaCellLocation loc = (CdmaCellLocation) location;

                long timestamp = System.currentTimeMillis();

                ContentValues rowData = new ContentValues();
                rowData.put(CDMA_Data.TIMESTAMP, timestamp);
                rowData.put(CDMA_Data.DEVICE_ID, device_id);
                rowData.put(CDMA_Data.BASE_STATION_ID, loc.getBaseStationId());
                rowData.put(CDMA_Data.BASE_STATION_LATITUDE, loc.getBaseStationLatitude());
                rowData.put(CDMA_Data.BASE_STATION_LONGITUDE, loc.getBaseStationLongitude());
                rowData.put(CDMA_Data.NETWORK_ID, loc.getNetworkId());
                rowData.put(CDMA_Data.SYSTEM_ID, loc.getSystemId());
                rowData.put(CDMA_Data.SIGNAL_STRENGTH, lastSignalStrength.getCdmaDbm());
                rowData.put(CDMA_Data.CDMA_ECIO, lastSignalStrength.getCdmaEcio());
                rowData.put(CDMA_Data.EVDO_DBM, lastSignalStrength.getEvdoDbm());
                rowData.put(CDMA_Data.EVDO_ECIO, lastSignalStrength.getEvdoEcio());
                rowData.put(CDMA_Data.EVDO_SNR, lastSignalStrength.getEvdoSnr());

                try {
                    getContentResolver().insert(CDMA_Data.CONTENT_URI, rowData);

                    Intent newCDMA = new Intent(Telephony.ACTION_LAMP_CDMA_TOWER);
                    sendBroadcast(newCDMA);

                    if (Lamp.DEBUG) Log.d(TAG, "CDMA tower:" + rowData.toString());
                } catch (SQLiteException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                } catch (SQLException e) {
                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
                }
            }

            long timestamp = System.currentTimeMillis();

            ContentValues rowData = new ContentValues();
            rowData.put(Telephony_Data.TIMESTAMP, timestamp);
            rowData.put(Telephony_Data.DEVICE_ID, device_id);
            rowData.put(Telephony_Data.DATA_ENABLED, telephonyManager.getDataState());
            rowData.put(Telephony_Data.IMEI_MEID_ESN, Encrypter.hash(getApplicationContext(), telephonyManager.getDeviceId()));
            rowData.put(Telephony_Data.SOFTWARE_VERSION, telephonyManager.getDeviceSoftwareVersion());
            rowData.put(Telephony_Data.LINE_NUMBER, Encrypter.hashPhone(getApplicationContext(), telephonyManager.getLine1Number()));
            rowData.put(Telephony_Data.NETWORK_COUNTRY_ISO_MCC, telephonyManager.getNetworkCountryIso());
            rowData.put(Telephony_Data.NETWORK_OPERATOR_CODE, telephonyManager.getNetworkOperator());
            rowData.put(Telephony_Data.NETWORK_OPERATOR_NAME, telephonyManager.getNetworkOperatorName());
            rowData.put(Telephony_Data.NETWORK_TYPE, telephonyManager.getNetworkType());
            rowData.put(Telephony_Data.PHONE_TYPE, telephonyManager.getPhoneType());
            rowData.put(Telephony_Data.SIM_STATE, telephonyManager.getSimState());
            rowData.put(Telephony_Data.SIM_OPERATOR_CODE, telephonyManager.getSimOperator());
            rowData.put(Telephony_Data.SIM_OPERATOR_NAME, telephonyManager.getSimOperatorName());
            rowData.put(Telephony_Data.SIM_SERIAL, Encrypter.hash(getApplicationContext(), telephonyManager.getSimSerialNumber()));
            rowData.put(Telephony_Data.SUBSCRIBER_ID, Encrypter.hash(getApplicationContext(), telephonyManager.getSubscriberId()));

            try {
                getContentResolver().insert(Telephony_Data.CONTENT_URI, rowData);

                Intent newTelephony = new Intent(Telephony.ACTION_LAMP_TELEPHONY);
                sendBroadcast(newTelephony);

                if (Lamp.DEBUG) Log.d(TAG, "Telephony:" + rowData.toString());
            } catch (SQLiteException e) {
                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
            } catch (SQLException e) {
                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
            }
        }
    }
}
