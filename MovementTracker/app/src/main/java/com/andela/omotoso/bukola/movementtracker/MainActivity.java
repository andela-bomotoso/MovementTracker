package com.andela.omotoso.bukola.movementtracker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.DetectedActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
LocationListener,ResultCallback<Status> {

    private Context context;
    private Button trackerButton;
    private GoogleApiClient googleApiClient;
    private GoogleApiClient googleApiClientActivity;
    private LocationRequest locationRequest;
    private TextView longLatText;
    private TextView currentLocationText;
    private double longitude = 0;
    private double latitude = 0;
    private TextView currentActivityText;
    private Geocoder geocoder;
    private String street;
    protected ActivityDetectionBroadcastReceiver activityDetectionBroadcastReceiver;
    private final String TAG = "LOCATION_FINDER";
    private TextView timeSpentText;
    private boolean startTimer = true;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initializeComponents();
        initializeActivity();
        googleApiClient = initializeGoogleApiClient(googleApiClient);
    }

    private void initializeActivity() {
        buildGoogleApiClient();
        activityDetectionBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        geocoder = new Geocoder(MainActivity.this);
    }

    private void initializeComponents() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        trackerButton = (ToggleButton)findViewById(R.id.tracker_button);
        longLatText = (TextView)findViewById(R.id.current_longlatText);
        currentLocationText = (TextView)findViewById(R.id.current_locationText);
        currentActivityText = (TextView)findViewById(R.id.current_ActivityText);
        timeSpentText = (TextView)findViewById(R.id.time_spentText);
        context = getApplicationContext();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickTrackerButton(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            Toast.makeText(context, "Tracking Started", Toast.LENGTH_SHORT).show();
            startTracking();
        } else {
            Toast.makeText(context, "Tracking Stopped", Toast.LENGTH_SHORT).show();
            stopTracking();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        googleApiClientActivity.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        googleApiClientActivity.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause()  {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityDetectionBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(activityDetectionBroadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        if(googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void updateTimer() {

        Thread t = new Thread() {
            int count = 0;
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && startTimer) {
                        Thread.sleep(1000);
                        count++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                timeSpentText.setText(formatTime(count));

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public String formatTime(int seconds) {
        int hr = seconds/3600;
        int rem = seconds%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;
        return hrStr+":"+mnStr+":"+secStr;
    }
    public void onLocationChanged(Location location) {
        longLatText.setText(location.getLongitude()+", "+location.getLatitude()+"");
        currentLocationText.setText(getStreetName());
        longitude = location.getLongitude();
        latitude =  location.getLatitude();
    }

    public String getStreetName() {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }
        catch (Exception exception) {
        }
        if(addresses != null && addresses.size() > 0 ){
            Address address = addresses.get(0);
            street = address.getThoroughfare();
        }
            return street;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClientActivity = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    private GoogleApiClient initializeGoogleApiClient(GoogleApiClient googleApiClient1) {
        googleApiClient1 = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        return googleApiClient1;
    }


    public void onResult(Status status) {
        if(status.isSuccess()) {
            Log.e(TAG, "Successfully added activity detection");
        } else
            Log.e(TAG,"Error adding activity detection");
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this,ActivityDetector.class);
        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public void startTracking() {
        startTimer = true;
        updateTimer();
        if(googleApiClientActivity.isConnected()) {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClientActivity, Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    getActivityDetectionPendingIntent()).setResultCallback(this);
        }
    }

    public void stopTracking() {
        startTimer = false;

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClientActivity,getActivityDetectionPendingIntent())
                .setResultCallback(this);

        currentActivityText.setText("tracking not started");
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "receiver";

        public void onReceive(Context context, Intent intent) {
            List<Integer>confidenceLevels = new ArrayList<>();
            List<String>activities = new ArrayList<>();
            ArrayList<DetectedActivity> updatedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String status="";
            for(DetectedActivity activity : updatedActivities) {

                status += getActivityType(activity.getType()) + activity.getConfidence()+"%\n";
                activities.add(getActivityType(activity.getType())+"");
                confidenceLevels.add(activity.getConfidence());
            }
           currentActivityText.setText(getHighestActivityConfidence(confidenceLevels,activities));
        }

        public String getActivityType(int detectedActivityType) {
            Resources resources = MainActivity.this.getResources();
            switch (detectedActivityType){
                case DetectedActivity.IN_VEHICLE:
                    return resources.getString(R.string.on_vehicle);
                case DetectedActivity.ON_BICYCLE:
                    return resources.getString(R.string.on_bicycle);
                case DetectedActivity.ON_FOOT:
                    return resources.getString(R.string.on_foot);
                case DetectedActivity.RUNNING:
                    return resources.getString(R.string.running);
                case DetectedActivity.STILL:
                    return resources.getString(R.string.standing_still);
                case DetectedActivity.TILTING:
                    return resources.getString(R.string.tilting);
                case DetectedActivity.UNKNOWN:
                    return resources.getString(R.string.unknown);
                case DetectedActivity.WALKING:
                    return resources.getString(R.string.walking);
                default:
                    return resources.getString(R.string.unidentifiable);
            }
        }
    }

    public String getHighestActivityConfidence(List<Integer>confidenceLevels,List<String>activities) {
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
}
