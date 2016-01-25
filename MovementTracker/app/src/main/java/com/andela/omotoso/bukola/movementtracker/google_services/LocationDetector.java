package com.andela.omotoso.bukola.movementtracker.google_services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

/**
 * Created by GRACE on 1/20/2016.
 */
public class LocationDetector {

    public LocationDetector() {

    }

    public String fetchStreetName(Context context, double latitude, double longitude) {

        String street = "";
        Geocoder geocoder = new Geocoder(context);
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
