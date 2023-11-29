package digital.lamp.mindlamp.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * Created by Zco Engineer on 11-06-2018.
 * Custom lifecycle observer for application lifecycle events.
 * Extends DefaultLifecycleObserver to observe various lifecycle events.
 */
class AppLifeCycleListener : DefaultLifecycleObserver {

    var isOnForground: Boolean = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isOnForground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        isOnForground = false
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }
}