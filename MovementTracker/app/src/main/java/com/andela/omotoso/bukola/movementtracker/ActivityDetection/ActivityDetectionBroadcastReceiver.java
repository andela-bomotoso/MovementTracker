package com.andela.omotoso.bukola.movementtracker.ActivityDetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.Utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.Utilities.Timer;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GRACE on 1/11/2016.
 */

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "receiver";
        private TextView currentActivityText;


    public ActivityDetectionBroadcastReceiver(TextView currentActivityText) {
        this.currentActivityText = currentActivityText;
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

