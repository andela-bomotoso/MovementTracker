package com.andela.omotoso.bukola.movementtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
LocationListener,ResultCallback<Status> {

    private Context context;
    private Button trackerButton;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private TextView longLatText;
    private TextView currentLocationText;
    private double longitude = 0;
    private double latitude = 0;
    private Geocoder geocoder;
    private String street;
    protected ActivityDetectionBroadcastReceiver activityDetectionBroadcastReceiver;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        geocoder = new Geocoder(MainActivity.this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        longLatText = (TextView)findViewById(R.id.current_longlatText);
        currentLocationText = (TextView)findViewById(R.id.current_locationText);
        context = getApplicationContext();
        trackerButton = (ToggleButton)findViewById(R.id.tracker_button);
        trackerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                boolean on = ((ToggleButton) view).isChecked();
                if(on) {
                    Toast.makeText(context,"Tracking Started",Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context,"Tracking Stopped", Toast.LENGTH_SHORT).show();
            }
        });
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
        longLatText.setText(location.getLongitude()+", "+location.getLatitude()+"");
        currentLocationText.setText(getStreetName());
        longitude = location.getLongitude();
        latitude = location.getLatitude();
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

    public void onResult(Status status) {
        if(status.isSuccess()) {
            Log.e(TAG, "Successfully added activity detection");
        } else
            Log.e(TAG,"Error adding activity detection");
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this,DetailedActivitiesIntentService.class);
        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public void requestActivityUpdate(View view) {
        if(!googleApiClient.isConnected()) {
            Toast.makeText(this,getString(R.string.error_msg),Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient,Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()).setResultCallback(this);
        requestUpdates.setEnabled(false);
        removeUpdates.setEnabled(true);
    }

    public void removeActivityUpdate(View view) {
        if(!googleApiClient.isConnected()) {
            Toast.makeText(this,getString(R.string.error_msg),Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,getActivityDetectionPendingIntent())
                .setResultCallback(this);
        removeUpdates.setEnabled(false);
        requestUpdates.setEnabled(true);
        statusTextView.setText("");
    }

}
