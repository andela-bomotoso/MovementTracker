package com.andela.omotoso.bukola.movementtracker.ActivityDetection;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.andela.omotoso.bukola.movementtracker.Utilities.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by GRACE on 1/9/2016.
 */
public class ActivityDetector extends IntentService {

    private static final String TAG = "detection_is";
    private ActivityRecognitionListener listener;

    public ActivityDetector() {
        super(TAG);
    }

    public void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
        //ArrayList<DetectedActivity> detectedActivities = (ArrayList)result.getProbableActivities();
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        listener.onActivityDetected(getActivityType(detectedActivity));
        //Log.i(TAG, "activities detected");
        //localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
       // LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public String getActivityType(DetectedActivity detectedActivity) {

        switch (detectedActivity.getType()){
            case DetectedActivity.IN_VEHICLE:
                return Constants.ON_VEHICLE;
            case DetectedActivity.ON_BICYCLE:
                return Constants.ON_BICYCLE;
            case DetectedActivity.ON_FOOT:
                return Constants.ON_FOOT;
            case DetectedActivity.RUNNING:
                return Constants.RUNNING;
            case DetectedActivity.STILL:
                return Constants.STANDING_STILL;
            case DetectedActivity.TILTING:
                return Constants.TILTING;
            case DetectedActivity.UNKNOWN:
                return Constants.UNKNOWN;
            case DetectedActivity.WALKING:
                return Constants.WALKING;
            default:
                return Constants.UNIDENTIFIABLE;
        }
    }

    public void setListener(ActivityRecognitionListener listener) {
        this.listener = listener;
    }
}
