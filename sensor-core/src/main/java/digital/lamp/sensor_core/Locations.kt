package digital.lamp.sensor_core

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

/**
 * Location service for Aware framework
 * Provides mobile device network triangulation and GPS location
 *
 * @author denzil
 */
@SuppressLint("MissingPermission")
class Locations : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationRequest = LocationRequest.create()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Lamp.DEBUG) Log.d(TAG, "Location sensor is created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback)
            if (Lamp.DEBUG) Log.d(TAG, "Locations service terminated...")
        } catch (er: Exception) {
            er.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                    callback(location)
                }
            }
        }
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        return START_STICKY
    }

    companion object {
        private const val TAG = "LAMP::Location"
        private var fusedLocationClient: FusedLocationProviderClient? = null
        private var locationCallback: LocationCallback? = null
        private var locationRequest: LocationRequest? = null

        lateinit var callback : (Location) -> Unit
        @JvmStatic
        fun setSensorObserver(listener: (Location) -> Unit) {
            callback = listener
        }
    }
}