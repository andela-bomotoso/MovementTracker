package com.andela.omotoso.bukola.movementtracker.Utilities;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

/**
 * Created by GRACE on 1/11/2016.
 */
public class StreetNameHandler {

    public StreetNameHandler() {

    }

    public static String getStreetName(double longitude, double latitude, Activity activity) {
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
            street = address.getThoroughfare();
        }
        return street;
    }
}
