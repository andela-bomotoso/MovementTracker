package com.andela.omotoso.bukola.movementtracker.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by GRACE on 1/14/2016.
 */
public class SharedPreferenceManager {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public SharedPreferenceManager(Context context) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
    }

    public void saveDelayTime(String delayText) {

        editor.putString(Constants.DELAY_KEY, delayText);

        editor.commit();
    }

    public String retrieveDelayTime() {

       return sharedPref.getString(Constants.DELAY_KEY, Constants.DEFAULT_DELAY);
    }

    public void saveLongitude(float longitude) {

        editor.putFloat(Constants.LONGITUDE, longitude);

        editor.commit();
    }

    public float retrieveLongitude() {

        return sharedPref.getFloat(Constants.LONGITUDE, 0);
    }

    public void saveLatitude(float longitude) {

        editor.putFloat(Constants.LATITUDE, longitude);

        editor.commit();
    }

    public float retrieveLatitude() {

        return sharedPref.getFloat(Constants.LATITUDE, 0);
    }



}
