package com.andela.omotoso.bukola.movementtracker.Utilities;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by GRACE on 1/14/2016.
 */
public class LocationServicesManager implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double longitude;
    private double latitude;
    private String streetName;
    private Activity activity;
    private LocationServicesListener locationServicesListener;

    public LocationServicesManager(Activity activity) {
        this.activity = activity;
        longitude = 0;
        latitude = 0;
        streetName = "";

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public String getStreet() {
        return streetName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void connect() {
        googleApiClient.connect();
    }

    public void disconnect() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000 );
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        streetName = fetchStreetName();

        locationServicesListener.onLocationChanged(longitude,latitude);
        //listener.onCountryDetected(countryName);
    }

    public void setListener(LocationServicesListener listener) {
        this.locationServicesListener = listener;
    }

//    public String detectCountry() {
//        String country = "";
//        Geocoder geocoder = new Geocoder(activity);
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//        }
//        catch (Exception exception) {
//        }
//        if(addresses != null && addresses.size() > 0 ){
//
//            Address address = addresses.get(0);
//            country = address.getCountryName();
//        }
//
//        return country;
//    }

    public String fetchStreetName() {
        String street = "";
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }
        catch (Exception exception) {
        }
        if(addresses != null && addresses.size() > 0 ){
            Address address = addresses.get(0);
            street = address.getThoroughfare()+ " "+address.getAdminArea();
        }
        return street;
    }

}
