package com.andela.omotoso.bukola.movementtracker.ActivityDetection;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.Utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.Utilities.Timer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GRACE on 1/11/2016.
 */

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {


        private LocationRequest locationRequest;
        protected static final String TAG = "receiver";
        private TextView currentActivityText;
        private GoogleApiClient googleApiClient;
        private Activity activity;



    public ActivityDetectionBroadcastReceiver(TextView currentActivityText,Activity activity) {
        //this.currentActivityText = currentActivityText;
        this.activity = activity;
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onResult(Status status) {

    }

    public void connect() {
        googleApiClient.connect();
    }

    public void disconnect() {
        googleApiClient.disconnect();
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(activity, ActivityDetector.class);
        return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void onActivityChanged() {
        if(googleApiClient.isConnected()) {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    getActivityDetectionPendingIntent()).setResultCallback(this);
        }
    }


    public void onReceive(Context context, Intent intent) {
            List<Integer> confidenceLevels = new ArrayList<>();
            List<String>activities = new ArrayList<>();
            ArrayList<DetectedActivity> updatedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String status="";

            for(DetectedActivity activity : updatedActivities) {
                status += getActivityType(activity.getType()) + activity.getConfidence()+"%\n";
                activities.add(getActivityType(activity.getType())+"");
                confidenceLevels.add(activity.getConfidence());
            }
            currentActivityText.setText(getHighestActivityConfidence(confidenceLevels, activities));
        }

    public static String getHighestActivityConfidence(List<Integer> confidenceLevels,List<String>activities) {
        double max = confidenceLevels.get(0);
        int count = 0;
        String activity;
        for(Integer value:confidenceLevels){
            if(value > max){
                max = value;
                count++;
            }
        }
        activity = activities.get(count);
        return activity;
    }

    public static String getActivityType(int detectedActivityType) {

        switch (detectedActivityType){
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

}

