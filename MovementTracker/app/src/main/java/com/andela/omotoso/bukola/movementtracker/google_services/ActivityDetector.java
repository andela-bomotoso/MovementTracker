package com.andela.omotoso.bukola.movementtracker.google_services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.andela.omotoso.bukola.movementtracker.utilities.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by GRACE on 1/9/2016.
 */
public class ActivityDetector extends IntentService {

    public static final String DETECTED_ACTIVITY = "DETECTED_ACTIVITY";

    public ActivityDetector() {

        super(DETECTED_ACTIVITY);
    }

    public void onHandleIntent(Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        if (result != null) {

            DetectedActivity detectedActivity = result.getMostProbableActivity();

            Intent broadcast = new Intent(Constants.BROADCAST_ACTION);
            broadcast.putExtra(DETECTED_ACTIVITY, getActivityType(detectedActivity));

            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
        }
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
            case DetectedActivity.TILTING:
                return Constants.STANDING_STILL;
            case DetectedActivity.UNKNOWN:
                return Constants.UNKNOWN;
            case DetectedActivity.WALKING:
                return Constants.WALKING;
            default:
                return Constants.UNIDENTIFIABLE;
        }
    }
}
