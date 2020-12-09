package digital.lamp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import digital.lamp.utils.Lamp_Sensor;
import java.util.ArrayList;
import java.util.List;

public class ActivityTransitions extends Lamp_Sensor implements SensorEventListener {

    public static String TAG = "LAMP::Activity Transition";

    // Action fired when transitions are triggered.
    private final String TRANSITIONS_RECEIVER_ACTION = "TRANSITIONS_RECEIVER_ACTION";

    private List<ActivityTransition> activityTransitionList;
    private PendingIntent mActivityTransitionsPendingIntent;
    private TransitionsReceiver mTransitionsReceiver;

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //We log current accuracy on the sensor changed event
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // List of activity transitions to track.
        activityTransitionList = new ArrayList<>();

        //Add activity transitions to track.
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        // TODO: Initialize PendingIntent that will be triggered when a activity transition occurs.
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent =
                PendingIntent.getBroadcast(ActivityTransitions.this, 0, intent, 0);

        // TODO: Create a BroadcastReceiver to listen for activity transitions.
        // The receiver listens for the PendingIntent above that is triggered by the system when an
        // activity transition occurs.
        mTransitionsReceiver = new TransitionsReceiver();

        if (Lamp.DEBUG) Log.d(TAG, "Activity Transition service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Unregister activity transition receiver when user leaves the app.
        unregisterReceiver(mTransitionsReceiver);
        disableActivityTransitions();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.e(TAG, "Started Activity Transitions");

        //Register the BroadcastReceiver to listen for activity transitions.
        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));

        enableActivityTransitions();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void enableActivityTransitions() {

        Log.e(TAG, "enableActivityTransitions()");

        ActivityTransitionRequest request = new ActivityTransitionRequest(activityTransitionList);

        // Register for Transitions Updates.
        Task<Void> task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, mActivityTransitionsPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {

                        Log.e(TAG,"Transitions Api was successfully registered.");
                    }
                });

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Transitions Api could NOT be registered: " + e);
                    }
                });
    }


    private void disableActivityTransitions() {

        Log.e(TAG, "disableActivityTransitions()");
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mActivityTransitionsPendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG,"Transitions successfully unregistered.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Transitions could not be unregistered: " + e);
                    }
                });
    }

    public static class TransitionsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, "onReceive(): " + intent);

            //Extract activity transition information from listener.
            if (ActivityTransitionResult.hasResult(intent)) {

                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                assert result != null;
                for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                    String activity = activityType(event.getActivityType());
                    Boolean transition = transitionType(event.getTransitionType());
                    String message = "Transition: "+activity+" : "+transition;
                    Log.e(TAG," : "+message);
                    awareSensor.onActivityTransitionChanged(activity,transition);
                }
            }
        }
    }

    public static String activityType(Integer activity){
        String str = "";
        switch(activity)
        {
            case  DetectedActivity.WALKING:
                str = "walking";
                break;
            case  DetectedActivity.STILL:
                str = "stationary";
                break;
            case DetectedActivity.IN_VEHICLE:
                str = "in_car";
                break;
            case DetectedActivity.ON_BICYCLE:
                str = "cycling";
                break;
            case DetectedActivity.RUNNING:
                str = "running";
                break;
            case DetectedActivity.ON_FOOT:
                str = "on_foot";
                break;
            default:
                str = "unknown";
        }
        return str;
    }

     public static Boolean transitionType(Integer transitionType){
        boolean str = false;
        switch(transitionType)
        {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                str = true;
                break;
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                str = false;
                break;
        }
        return str;
    }

    private static LAMPSensorObserver awareSensor;
    public static void setSensorObserver(LAMPSensorObserver observer) {
        awareSensor = observer;
    }
    public static LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onActivityTransitionChanged(String activityType, Boolean transitionType);
    }
}
