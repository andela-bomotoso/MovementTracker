package com.andela.omotoso.bukola.movementtracker;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by GRACE on 1/9/2016.
 */
public class ActivityDetector extends IntentService {

    private static final String TAG = "detection_is";

    public ActivityDetector() {
        super(TAG);
    }

    public void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
        ArrayList<DetectedActivity> detectedActivities = (ArrayList)result.getProbableActivities();
        Log.i(TAG, "activities detected");
        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
