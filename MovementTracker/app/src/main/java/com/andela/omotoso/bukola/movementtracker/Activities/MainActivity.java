package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;

import com.andela.omotoso.bukola.movementtracker.ActivityDetection.ActivityDetectionBroadcastReceiver;
import com.andela.omotoso.bukola.movementtracker.ActivityDetection.ActivityDetector;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.Utilities.DateHandler;
import com.andela.omotoso.bukola.movementtracker.Utilities.LocationServicesListener;
import com.andela.omotoso.bukola.movementtracker.Utilities.LocationServicesManager;
import com.andela.omotoso.bukola.movementtracker.Utilities.Notifier;
import com.andela.omotoso.bukola.movementtracker.Utilities.SharedPreferenceManager;
import com.andela.omotoso.bukola.movementtracker.Utilities.StreetNameHandler;
import com.andela.omotoso.bukola.movementtracker.Utilities.Timer;
import com.andela.omotoso.bukola.movementtracker.data.MovementTrackerDbHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {

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
    protected ActivityDetectionBroadcastReceiver activityDetectionBroadcastReceiver;
    private final String TAG = "LOCATION_FINDER";
    private TextView timeSpentText;
    private Timer timer;
    private int mId;
    private SharedPreferenceManager sharedPreferenceManager;
    private Notifier notifier;
    private LocationServicesManager locationServicesManager;
    private String streetName = "";
    private MovementTrackerDbHelper movementTrackerDbHelper;
    private DateHandler dateHandler;

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

        sharedPreferenceManager = new SharedPreferenceManager(this);
        movementTrackerDbHelper = new MovementTrackerDbHelper(this);
        notifier = new Notifier(context,MainActivity.this);
        dateHandler = new DateHandler();
    }

    private void initializeActivity() {
        buildGoogleApiClient();
        activityDetectionBroadcastReceiver = new ActivityDetectionBroadcastReceiver(currentActivityText);
    }

    private void initializeComponents() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        trackerButton = (ToggleButton) findViewById(R.id.tracker_button);

        longLatText = (TextView) findViewById(R.id.current_longlatText);
        currentLocationText = (TextView) findViewById(R.id.current_locationText);
        currentActivityText = (TextView) findViewById(R.id.current_ActivityText);
        //textViewWatcher();
        timeSpentText = (TextView) findViewById(R.id.time_spentText);

        timer = new Timer();
        timer.setTimeSpentText(timeSpentText);
        timer.setActivity(MainActivity.this);

        context = getApplicationContext();

        loadLocationValues();
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
           startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.tracked_location) {

        } else if (id == R.id.tracked_time) {

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
        locationServicesManager.connect();
        googleApiClientActivity.connect();
    }

    @Override
    protected void onStop() {
        locationServicesManager.disconnect();
        googleApiClientActivity.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void loadLocationValues() {

        locationServicesManager = new LocationServicesManager(this);
        LocationServicesListener listener = new LocationServicesListener() {
            @Override
            public void onLocationChanged(double longitude, double latitude) {
                streetName = locationServicesManager.getStreet();
                currentLocationText.setText(streetName);
                longitude = locationServicesManager.getLongitude();
                latitude = locationServicesManager.getLatitude();
                longLatText.setText(longitude + ", " + latitude + "");
            }
        };
        locationServicesManager.setListener(listener);
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClientActivity = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    public void onResult(Status status) {

    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityDetector.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void startTracking() {
        timer.setTimer(true);
        timer.updateTimer();
        currentActivityText.setText("connecting ...");
        if (googleApiClientActivity.isConnected()) {
            countDown(timer.formatTimeText(sharedPreferenceManager.retrieveDelayTime()));
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClientActivity, Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    getActivityDetectionPendingIntent()).setResultCallback(this);
        }
    }

    public void stopTracking() {
        timer.setTimer(false);
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClientActivity, getActivityDetectionPendingIntent())
                .setResultCallback(this);
        currentActivityText.setText("tracking not started");
        notifier.cancelNotification(this, 1);
    }

    public void countDown(int delay) {

        new CountDownTimer(delay*Constants.MINUTES_TO_MILLISECONDS, Constants.TICK_IN_MILLISECONDS) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                notifier.sendNotification("Movement Tracker");
            }
        }.start();
    }

    private void textViewWatcher() {
        currentActivityText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                timer.updateTimer();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

                movementTrackerDbHelper.insertRows(dateHandler.getCurrentDate(), currentLocationText.getText().toString(),
                        currentActivityText.getText().toString(), timeSpentText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
               // if(s.length() != 0)
                    //Field2.setText("");

            }
        });

    }
}



