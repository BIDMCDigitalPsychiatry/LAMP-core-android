package digital.lamp.mindlamp.auth

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import digital.lamp.mindlamp.R

/**
 * Shown when the dashboard's login link can't be fetched even after retries.
 * Retry runs [onRetry]; Cancel closes the screen, like the page-load dialog.
 */
fun showLoginRetryDialog(activity: Activity, onRetry: () -> Unit) {
    if (activity.isFinishing || activity.isDestroyed) return
    AlertDialog.Builder(activity)
        .setTitle(activity.getString(R.string.app_name))
        .setMessage(activity.getString(R.string.txt_unable_to_connect))
        .setCancelable(false)
        .setPositiveButton(activity.getString(R.string.retry)) { _, _ -> onRetry() }
        .setNegativeButton(activity.getString(R.string.cancel)) { _, _ -> activity.finish() }
        .show()
}
