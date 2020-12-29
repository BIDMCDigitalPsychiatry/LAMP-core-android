package digital.lamp

import android.hardware.SensorEventListener
import com.google.android.gms.location.ActivityTransition
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.hardware.SensorEvent
import com.google.android.gms.location.DetectedActivity
import android.os.IBinder
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityRecognition
import android.hardware.Sensor
import android.util.Log
import com.google.android.gms.location.ActivityTransitionResult

class ActivityTransitions : Service(), SensorEventListener {
    // Action fired when transitions are triggered.
    private val actions = "TRANSITIONS_RECEIVER_ACTION"
    private var activityTransitionList: MutableList<ActivityTransition> = arrayListOf()
    private var mActivityTransitionsPendingIntent: PendingIntent? = null
    private var mTransitionsReceiver: TransitionsReceiver? = null
    override fun onSensorChanged(event: SensorEvent) {}
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //We log current accuracy on the sensor changed event
    }

    override fun onCreate() {
        super.onCreate()

        // List of activity transitions to track.
        activityTransitionList

        //Add activity transitions to track.
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        activityTransitionList.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())

        // TODO: Initialize PendingIntent that will be triggered when a activity transition occurs.
        val intent = Intent(actions)
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(this@ActivityTransitions, 0, intent, 0)

        // TODO: Create a BroadcastReceiver to listen for activity transitions.
        // The receiver listens for the PendingIntent above that is triggered by the system when an
        // activity transition occurs.
        mTransitionsReceiver = TransitionsReceiver()
        if (Lamp.DEBUG) Log.d(Companion.TAG, "Activity Transition service created!")
    }

    override fun onDestroy() {
        super.onDestroy()

        //Unregister activity transition receiver when user leaves the app.
        unregisterReceiver(mTransitionsReceiver)
        disableActivityTransitions()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.e(Companion.TAG, "Started Activity Transitions")

        //Register the BroadcastReceiver to listen for activity transitions.
        registerReceiver(mTransitionsReceiver, IntentFilter(actions))
        enableActivityTransitions()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun enableActivityTransitions() {
        Log.e(Companion.TAG, "enableActivityTransitions()")
        val request = ActivityTransitionRequest(activityTransitionList)

        // Register for Transitions Updates.
        val task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, mActivityTransitionsPendingIntent)
        task.addOnSuccessListener { Log.e(Companion.TAG, "Transitions Api was successfully registered.") }
        task.addOnFailureListener { e -> Log.e(Companion.TAG, "Transitions Api could NOT be registered: $e") }
    }

    private fun disableActivityTransitions() {
        Log.e(Companion.TAG, "disableActivityTransitions()")
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mActivityTransitionsPendingIntent)
                .addOnSuccessListener { Log.e(Companion.TAG, "Transitions successfully unregistered.") }
                .addOnFailureListener { e -> Log.e(Companion.TAG, "Transitions could not be unregistered: $e") }
    }

    class TransitionsReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "onReceive(): $intent")

            //Extract activity transition information from listener.
            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent)!!
                for (event in result.transitionEvents) {
                    val activity = activityType(event.activityType)
                    val transition = transitionType(event.transitionType)
                    val message = "Transition: $activity : $transition"
                    Log.e(TAG, " : $message")

                    callback(activity,transition)
                }
            }
        }

        private fun activityType(activity: Int?): String {
            var str = ""
            str = when (activity) {
                DetectedActivity.WALKING -> "walking"
                DetectedActivity.STILL -> "stationary"
                DetectedActivity.IN_VEHICLE -> "in_car"
                DetectedActivity.ON_BICYCLE -> "cycling"
                DetectedActivity.RUNNING -> "running"
                DetectedActivity.ON_FOOT -> "on_foot"
                else -> "unknown"
            }
            return str
        }

        private fun transitionType(transitionType: Int?): Boolean {
            var str = false
            when (transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> str = true
                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> str = false
            }
            return str
        }
    }
    companion object {
        var TAG = "LAMP::Activity Transition"
        lateinit var callback : (String,Boolean) -> Unit
        @JvmStatic
        fun setSensorObserver(listener: (String,Boolean) -> Unit) {
            callback = listener
        }
    }
}