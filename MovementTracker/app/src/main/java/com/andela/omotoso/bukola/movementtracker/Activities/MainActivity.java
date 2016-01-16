package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.andela.omotoso.bukola.movementtracker.ActivityDetection.ActivityDetectionBroadcastReceiver;
import com.andela.omotoso.bukola.movementtracker.ActivityDetection.ActivityDetector;
import com.andela.omotoso.bukola.movementtracker.ActivityDetection.ActivityRecognitionListener;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.Utilities.DateHandler;
import com.andela.omotoso.bukola.movementtracker.Utilities.Launcher;
import com.andela.omotoso.bukola.movementtracker.Utilities.LocationServicesListener;
import com.andela.omotoso.bukola.movementtracker.Utilities.LocationServicesManager;
import com.andela.omotoso.bukola.movementtracker.Utilities.Notifier;
import com.andela.omotoso.bukola.movementtracker.Utilities.SharedPreferenceManager;
import com.andela.omotoso.bukola.movementtracker.Utilities.Timer;
import com.andela.omotoso.bukola.movementtracker.data.MovementTrackerDbHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import com.google.android.gms.common.ConnectionResult;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {

    private Context context;
    private Button trackerButton;
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
    private ActivityRecognitionListener listener;
    private GoogleApiClient googleApiClient;

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
    }

    private void initializeComponents() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        trackerButton = (ToggleButton) findViewById(R.id.tracker_button);

        longLatText = (TextView) findViewById(R.id.current_longlatText);
        currentLocationText = (TextView) findViewById(R.id.current_locationText);
        currentActivityText = (TextView) findViewById(R.id.current_ActivityText);

        timeSpentText = (TextView) findViewById(R.id.time_spentText);

        timer = new Timer();
        timer.setTimeSpentText(timeSpentText);
        timer.setActivity(MainActivity.this);

        context = getApplicationContext();

        loadLocationValues();
        initializeActivityDetector();
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
            //Launcher.launchActivity(this,TrackerByLocation.class);

        } else if (id == R.id.app_help) {

        } else if (id ==R.id.app_info) {

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
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        locationServicesManager.disconnect();
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        trackerButton.setEnabled(true);
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

    public void initializeActivityDetector() {
        trackerButton.setEnabled(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(new Receiver(), new IntentFilter(Constants.BROADCAST_ACTION));
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    public void onResult(Status status) {

    }

    public void startTracking() {
        timer.setTimer(true);
        timer.updateTimer();
        currentActivityText.setText("connecting ...");

        if (googleApiClient.isConnected()) {
            countDown(timer.formatTimeText(sharedPreferenceManager.retrieveDelayTime()));

            Intent service = new Intent(this, ActivityDetector.class);
            startService(service);

            PendingIntent intent = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);

            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 0, intent);
        }
    }

    public void stopTracking() {
        timer.setTimer(false);
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

    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String activity = intent.getStringExtra(ActivityDetector.DETECTED_ACTIVITY);

            MainActivity.this.currentActivityText.setText(activity);
        }
    }

}



