package digital.lamp.mindlamp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_TELEPHONY_PERMISSIONS

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
object PermissionCheck {

    fun checkAndRequestPermissions(context: Activity): Boolean {
        val cameraPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val contactPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        val permissionLocation =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val readStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val readSyncSettingPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SYNC_SETTINGS)
        val readSyncStatPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SYNC_STATS)
        //Android 10
        val activityRecognitionPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
        val audioRecordPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        val modifyAudioSettingsPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.MODIFY_AUDIO_SETTINGS)
        val writeStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val notificationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        val listPermissionsNeeded = ArrayList<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
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

        if (audioRecordPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (modifyAudioSettingsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
        }
        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.TIRAMISU   &&notificationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }


        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun checkTelephonyPermission(context: Activity): Boolean {

        val listPermissionsNeeded = ArrayList<String>()
        val phoneStatePermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)

        if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)

            ActivityCompat.requestPermissions(
                context,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_TELEPHONY_PERMISSIONS
            )
            return false
        }
        return true

    }

    fun checkSinglePermission(permission: String, context: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) === PackageManager.PERMISSION_GRANTED
    }
}