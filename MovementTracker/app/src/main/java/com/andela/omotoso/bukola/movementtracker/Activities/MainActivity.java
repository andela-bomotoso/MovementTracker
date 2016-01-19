package com.andela.omotoso.bukola.movementtracker.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.andela.omotoso.bukola.movementtracker.activity_detection.ActivityDetector;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.utilities.DateHandler;
import com.andela.omotoso.bukola.movementtracker.utilities.Launcher;
import com.andela.omotoso.bukola.movementtracker.location_services.LocationServicesListener;
import com.andela.omotoso.bukola.movementtracker.location_services.LocationServicesManager;
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
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {

    private Context context;
    private Button trackerButton;
    private LocationRequest locationRequest;
    private TextView longLatText;
    private TextView currentLocationText;
    private double longitude = 0;
    private double latitude = 0;
    private TextView currentActivityText;
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
    private GoogleApiClient googleApiClient;
    private String activityText = "";
    private String locationText = "";
    private String timeSpent = "";
    private int durationText = 0;
    private String logTimeText = "";
    private TextView appInfoText;
    private Button appInfoOkButton;
    private TimerListener timerListener;

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

       // appInfoText = (TextView)findViewById(R.id.app_info);

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

        if(isOnline(this)) {
            trackerButton.setEnabled(true);
        }
        else {
            Toast.makeText(this,"No internet detected",Toast.LENGTH_SHORT).show();
        }

        longLatText = (TextView) findViewById(R.id.current_longlatText);
        currentLocationText = (TextView) findViewById(R.id.current_locationText);

        currentActivityText = (TextView) findViewById(R.id.current_ActivityText);
        setActivityTextWatcher();

        timeSpentText = (TextView) findViewById(R.id.time_spentText);

        timer = new Timer(this);

        timerListener = new TimerListener() {
            @Override
            public void onTick(String timeSpent) {
                timeSpentText.setText(timeSpent);
            }
        };
        timer.setTimerListener(timerListener);


        context = getApplicationContext();
        initializeActivityDetector();
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
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

        currentActivityText.setText("connecting...");
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
            currentActivityText.setText("connecting...");
            timeSpentText.setText("00:00");
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT);
        }
    }

    public void stopTracking() {
        currentActivityText.setText("Tracking stopped");
        //timer.setTimer(false);
        timer.turnOff();
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

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
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
                if(activityText.equals("connecting...")) {
                    timer.reset();
                }
                if (!currentActivityText.getText().toString().equals(activityText)
                        && !activityText.equals("connecting...") && !activityText.equals("Tracking stopped")) {
                    movementTrackerDbHelper.insertRows(dateHandler.getCurrentDate(),locationText,activityText, timer.timeInSeconds,dateHandler.getCurrentTime());
                    timer.reset();
                }
            }
        });
    }

    public void displayAppInfo() {
        new AlertDialog.Builder(MainActivity.this).setTitle("Application Info")
                .setMessage(Constants.APP_INFO)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void displayHelp() {
        new AlertDialog.Builder(MainActivity.this).setTitle("Help")
                .setMessage(Constants.HELP)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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



