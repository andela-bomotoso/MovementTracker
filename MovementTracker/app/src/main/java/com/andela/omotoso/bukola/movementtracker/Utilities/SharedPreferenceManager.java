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

}
