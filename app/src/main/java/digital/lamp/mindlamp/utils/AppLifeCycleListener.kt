package digital.lamp.mindlamp.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Created by Zco Engineer on 11-06-2018.
 */
class AppLifeCycleListener : DefaultLifecycleObserver {

    var isOnForground: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {

        isOnForground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        isOnForground = false
    }

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
       // isOnForground = false
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
      //  isOnForground = true
    }
}