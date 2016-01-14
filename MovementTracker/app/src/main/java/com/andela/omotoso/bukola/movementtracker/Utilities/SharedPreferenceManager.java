package com.andela.omotoso.bukola.movementtracker.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

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
        editor.putString("DELAY_KEY", delayText);
        editor.commit();
    }

    public String retrieveDelayTime() {
       return sharedPref.getString("DELAY_KEY", Constants.DEFAULT_DELAY);
    }

}
