package com.andela.omotoso.bukola.movementtracker;


import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.TextView;

import java.util.List;

public class Utilities {

    public Utilities() {

    }

    public static String getStreetName(double longitude, double latitude, Activity activity) {
        //MyActivity myActivity = (MyActivity) getContext();
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
