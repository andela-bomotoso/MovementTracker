package com.andela.omotoso.bukola.movementtracker.activities;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.andela.omotoso.bukola.movementtracker.utilities.DialogDivider;
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
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.view.ContextThemeWrapper;
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
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    private Context context;
    private Button trackerButton;
    private LocationRequest locationRequest;
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
    private int durationText;
    private TimerListener timerListener;
    private boolean delayElapsed;
    private LocationDetector locationDetector;
    private DrawerLayout drawer;
    private String activity;
    private ConnectivityManager connectivityManager;
    private NetworkInfo netInfo;
    private String locationText;
    private String location;
    private CountDownTimer countDownTimer;
    private ContextThemeWrapper ctw;
    private DialogDivider dialogDivider;
    private Dialog dialog;
    private boolean sendNotification;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ctw = new ContextThemeWrapper(this, R.style.Theme_Tracker);

        initializeComponents();
        initializeVariables();
        buildGoogleApiClient();

        if(!checkGPS()) {

            getToastMessage(getString(R.string.gps_not_detected));
        }
    }

    private void initializeComponents() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        dialogDivider = new DialogDivider(this,dialog);

        trackerButton = (ToggleButton) findViewById(R.id.tracker_button);
        trackerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                boolean on = ((ToggleButton) view).isChecked();

                if (!isOnline() && (!checkGPS()) && on) {

                    getToastMessage(getString(R.string.gps_internet_not_detected));
                    ((ToggleButton) view).setChecked(false);
                    return;
                }

                else if (!isOnline() && (checkGPS()) && on) {

                    getToastMessage(getString(R.string.no_internet));
                    ((ToggleButton) view).setChecked(false);
                    return;
                }

                else if (isOnline() && (!checkGPS()) && on) {

                    getToastMessage(getString(R.string.gps_not_detected));
                }

                else if (on) {

                    getToastMessage(getString(R.string.tracking_started));
                    startTracking();

                } else {

                    stopTracking();
                    getToastMessage(getString(R.string.tracking_stopped));
                }
            }
        });

        currentLocationText = (TextView) findViewById(R.id.current_locationText);
        setLocationTextWatcher();

        currentActivityText = (TextView) findViewById(R.id.current_ActivityText);
        setActivityTextWatcher();

        timeSpentText = (TextView) findViewById(R.id.time_spentText);

        context = getApplicationContext();
        initializeActivityDetector();
    }

    private void initializeVariables() {

        activityText = currentActivityText.getText().toString();
        locationText = currentLocationText.getText().toString();
        streetName = "";
        activity = "";
        durationText = 0;
        delayElapsed = false;
        sendNotification = true;

        sharedPreferenceManager = new SharedPreferenceManager(this);
        movementTrackerDbHelper = new MovementTrackerDbHelper(this);
        notifier = new Notifier(context, MainActivity.this);
        dateHandler = new DateHandler();
        locationDetector = new LocationDetector();
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        timer = new Timer(this);
        timerListener = new TimerListener() {
            @Override
            public void onTick(String timeSpent) {
                if(timer.timer) {

                    timeSpentText.setText(timeSpent);
                }
                else {

                    timeSpentText.setText(R.string.time_zero);
                }
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

        if (id == R.id.app_info) {
            displayAppInfo();
            return true;
        }

        if (id == R.id.app_help) {
            displayHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.app_display) {

    }

        if (id == R.id.tracked_location) {

            Launcher.launchActivity(this, TrackerByLocationActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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

        if (isOnline()) {

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            } catch (SecurityException exception){

                System.out.println("security exception");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onLocationChanged(Location location) {

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

    public void detectActivity() {

        if (isOnline() ) {

            Intent service = new Intent(this, ActivityDetector.class);
            startService(service);

            PendingIntent intent = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);

            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 0, intent);
        }

        else {

            getToastMessage(getString(R.string.no_internet));
        }
    }

    public void startTracking() {

        currentActivityText.setText(R.string.connecting);
        sendNotification = true;
        timer.turnOn();

        if (!delayElapsed) {

            countDown(timer.formatDelayText(sharedPreferenceManager.retrieveDelayTime()));
        }

        detectActivity();
    }

    public void stopTracking() {

        currentActivityText.setText(R.string.tracking_not_started);
        timer.turnOff();
        notifier.cancelNotification(this, 1);
        delayElapsed = false;
        sendNotification = false;
    }

    public void countDown(int delay) {

        if(!activity.equals("connecting") && sendNotification) {
          countDownTimer =  new CountDownTimer(delay * Constants.MINUTES_TO_MILLISECONDS, Constants.TICK_IN_MILLISECONDS) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    if ((!activity.equals("connecting ...") && (!activity.equals("tracking stopped")))&& sendNotification) {
                        delayElapsed = true;
                        notifier.sendNotification("Movement Tracker");
                    }
                }
            }.start();
        }
    }

    public boolean isOnline() {

        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = connectivityManager.getActiveNetworkInfo();

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

                locationText = currentLocationText.getText().toString();
                activityText = currentActivityText.getText().toString();
                durationText = timer.timeInSeconds;
            }

            @Override
            public void afterTextChanged(Editable s) {

                activity = currentActivityText.getText().toString();

                if(activityText.equals("connecting...")) {

                    timer.reset();
                }

                if (readyForInsertion()) {

                    movementTrackerDbHelper.insertRows(dateHandler.getCurrentDate(),checkLocation(locationText),activityText,
                            timer.timeInSeconds,dateHandler.getCurrentTime());
                    timer.reset();
                }
            }
        });
    }


    private void setLocationTextWatcher() {

        currentLocationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(currentLocationText.getText().toString().isEmpty()) {
                    currentLocationText.setText("Unknown Location");
                }
            }
        });
    }

    public boolean readyForInsertion() {

        return (!currentActivityText.getText().toString().equals(activityText))
                && !activityText.equals(R.string.connecting) && !activityText.equals(R.string.tracking_stopped);
    }

    public String checkLocation(String locationText) {

        if (locationText.equals("Searching Location...") || locationText.isEmpty()) {

            return "Unknown Location";
        }
        else {

            return locationText;
        }
    }

    public void displayAppInfo() {

          dialog =  new AlertDialog.Builder(ctw)
                .setTitle(R.string.application_info)
                .setMessage(Constants.APP_INFO)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

            dialogDivider.setDialog(dialog);
            dialogDivider.setDivider();

    }

    public void displayHelp() {

       dialog = new AlertDialog.Builder(ctw)

                .setTitle(R.string.help)
                .setMessage(Constants.HELP)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        dialogDivider.setDialog(dialog);
        dialogDivider.setDivider();
    }

    public void getToastMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private boolean checkGPS() {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String activity = intent.getStringExtra(ActivityDetector.DETECTED_ACTIVITY);
            currentActivityText.setText(activity);
        }
    }

}



