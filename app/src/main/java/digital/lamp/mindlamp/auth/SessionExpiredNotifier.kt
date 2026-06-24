package digital.lamp.mindlamp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Signals that the session can no longer be renewed (the refresh token was
 * rejected), so the user must log in again. Posted from the network layer on a
 * background thread (TokenAuthenticator) and observed by HomeActivity, which
 * clears the UI and returns the web view to login.
 *
 * Android analog of iOS's `.lampSessionExpired` notification.
 */
object SessionExpiredNotifier {
    private val _expired = MutableLiveData<Boolean>()
    val expired: LiveData<Boolean> = _expired

    /** Safe to call from any thread. */
    fun notifyExpired() = _expired.postValue(true)

    /** Clear the flag once handled, so it doesn't re-fire on re-subscribe. */
    fun reset() = _expired.postValue(false)
}
