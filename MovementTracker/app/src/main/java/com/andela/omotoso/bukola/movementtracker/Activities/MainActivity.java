package com.andela.omotoso.bukola.movementtracker.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.andela.omotoso.bukola.movementtracker.google_services.ActivityDetector;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.google_services.LocationDetector;
import com.andela.omotoso.bukola.movementtracker.utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.utilities.DateHandler;
import com.andela.omotoso.bukola.movementtracker.utilities.Launcher;
import com.andela.omotoso.bukola.movementtracker.utilities.Notifier;
import com.andela.omotoso.bukola.movementtracker.utilities.SharedPreferenceManager;
import com.andela.omotoso.bukola.movementtracker.utilities.Timer;
import com.andela.omotoso.bukola.movementtracker.data.MovementTrackerDbHelper;
import com.andela.omotoso.bukola.movementtracker.utilities.TimerListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import com.google.android.gms.common.ConnectionResult;

import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    private Context context;
    private Button trackerButton;
    private LocationRequest locationRequest;
    private TextView longLatText;
    private TextView currentLocationText;
    private TextView currentActivityText;
    private final String TAG = Constants.LOCATION_FINDER;
    private TextView timeSpentText;
    private Timer timer;
    private SharedPreferenceManager sharedPreferenceManager;
    private Notifier notifier;
    private String streetName;
    private MovementTrackerDbHelper movementTrackerDbHelper;
    private DateHandler dateHandler;
    private GoogleApiClient googleApiClient;
    private String activityText;
    private String locationText;
    private int durationText;
    private TimerListener timerListener;
    private boolean delayElapsed;
    private LocationDetector locationDetector;

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
        initializeVariables();
        buildGoogleApiClient();
    }

    private void initializeComponents() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        trackerButton = (ToggleButton) findViewById(R.id.tracker_button);

        longLatText = (TextView) findViewById(R.id.current_longlatText);
        currentLocationText = (TextView) findViewById(R.id.current_locationText);

        currentActivityText = (TextView) findViewById(R.id.current_ActivityText);
        setActivityTextWatcher();

        timeSpentText = (TextView) findViewById(R.id.time_spentText);

        context = getApplicationContext();
        initializeActivityDetector();
        //loadLocationValues();
    }

    private void initializeVariables() {

        activityText = "";
        streetName = "";
        locationText = "";
        durationText = 0;
        delayElapsed = false;

        sharedPreferenceManager = new SharedPreferenceManager(this);
        movementTrackerDbHelper = new MovementTrackerDbHelper(this);
        notifier = new Notifier(context,MainActivity.this);
        dateHandler = new DateHandler();
        locationDetector = new LocationDetector();

        timer = new Timer(this);
        timerListener = new TimerListener() {
            @Override
            public void onTick(String timeSpent) {
                timeSpentText.setText(timeSpent);
            }
        };
        timer.setTimerListener(timerListener);
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

        int id = item.getItemId();

        if (id == R.id.action_settings) {
           startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.tracked_location) {
            Launcher.launchActivity(this, TrackerByLocationActivity.class);

        } else if (id == R.id.app_help) {
            displayHelp();

        } else if (id ==R.id.app_info) {
            displayAppInfo();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void onClickTrackerButton(View view) {

        boolean on = ((ToggleButton) view).isChecked();
        if (on) {

                Toast.makeText(context, R.string.tracking_started, Toast.LENGTH_SHORT).show();
                startTracking();

        } else {

            Toast.makeText(context, R.string.tracking_stopped, Toast.LENGTH_SHORT).show();
            stopTracking();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {

        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onLocationChanged(Location location) {

       longLatText.setText(location.getLongitude()+","+location.getLatitude());
       currentLocationText.setText(locationDetector.fetchStreetName(this, location.getLatitude(), location.getLongitude()));
    }

    public void initializeActivityDetector() {

        LocalBroadcastManager.getInstance(this).registerReceiver(new Receiver(), new IntentFilter(Constants.BROADCAST_ACTION));
    }

    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    public void onResult(Status status) {

    }

    public void startTracking() {

        currentActivityText.setText(R.string.connecting);
        timer.turnOn();
        detectActivity();
    }

    public void detectActivity() {

        if ( isOnline(this)) {

            countDown(timer.formatTimeText(sharedPreferenceManager.retrieveDelayTime()));
            Intent service = new Intent(this, ActivityDetector.class);
            startService(service);

            PendingIntent intent = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);

            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 0, intent);
        }
        else  {

            currentActivityText.setText(R.string.connecting);
            timeSpentText.setText(R.string.time_zero);
            Toast.makeText(this,R.string.no_internet,Toast.LENGTH_SHORT);
        }
    }

    public void stopTracking() {

        currentActivityText.setText(R.string.tracking_stopped);
        timer.turnOff();
        notifier.cancelNotification(this, 1);
        delayElapsed = false;
    }

    public void countDown(int delay) {

        new CountDownTimer(delay*Constants.MINUTES_TO_MILLISECONDS, Constants.TICK_IN_MILLISECONDS) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                notifier.sendNotification("Movement Tracker");
                delayElapsed = true;
            }
        }.start();
    }

    public boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }

        return false;
    }

    private void setActivityTextWatcher() {

        currentActivityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                activityText = currentActivityText.getText().toString();
                locationText = currentLocationText.getText().toString();
                durationText = timer.timeInSeconds;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(activityText.equals(R.string.connecting)) {
                    timer.reset();
                }

                if (readyForInsertion()) {

                    movementTrackerDbHelper.insertRows(dateHandler.getCurrentDate(),locationText,activityText,
                            timer.timeInSeconds,dateHandler.getCurrentTime());
                    timer.reset();
                }
            }
        });
    }

    public boolean readyForInsertion() {

        return !currentActivityText.getText().toString().equals(activityText)
                && !activityText.equals(R.string.connecting) && !activityText.equals(R.string.tracking_stopped) && delayElapsed;
    }

    public void displayAppInfo() {

        new AlertDialog.Builder(MainActivity.this)

                .setTitle(R.string.application_info)
                .setMessage(Constants.APP_INFO)
                .setIcon(R.drawable.ic_info_black_24dp)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void displayHelp() {

        new AlertDialog.Builder(MainActivity.this)

                .setTitle(R.string.help)
                .setMessage(Constants.HELP)
                .setIcon(R.drawable.ic_help_black_18dp)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String activity = intent.getStringExtra(ActivityDetector.DETECTED_ACTIVITY);
            currentActivityText.setText(activity);
        }
    }

}



