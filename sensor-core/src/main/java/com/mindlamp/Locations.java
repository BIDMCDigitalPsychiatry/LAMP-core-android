package com.mindlamp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.mindlamp.providers.Locations_Provider;
import com.mindlamp.utils.Lamp_Sensor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Location service for Aware framework
 * Provides mobile device network triangulation and GPS location
 *
 * @author denzil
 */
@SuppressLint("MissingPermission")
public class Locations extends Lamp_Sensor {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    public String TAG = "LAMP Sensor Location";

    private static Locations.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Locations.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Locations.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onLocationChanged(Location data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = Locations_Provider.getAuthority(this);

        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Lamp.DEBUG) Log.d(TAG, "Location sensor is created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            if (Lamp.DEBUG) Log.d(TAG, "Locations service terminated...");
        }
        catch(Exception er){er.printStackTrace();}

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        awareSensor.onLocationChanged(location);
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
        return START_STICKY;
    }
}