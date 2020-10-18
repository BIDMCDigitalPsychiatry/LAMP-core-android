package digital.lamp.mindlamp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
object PermissionCheck {

    fun checkAndRequestPermissions(context: Activity) : Boolean  {
        val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val contactPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        val permissionLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val readStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val readSyncSettingPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SYNC_SETTINGS)
        val readSyncStatPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SYNC_STATS)
        //Android 10
        val activityRecognitionPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)

        val listPermissionsNeeded = ArrayList<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (readSyncSettingPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SYNC_SETTINGS)
        }
        if (readSyncStatPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SYNC_STATS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && activityRecognitionPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }
}