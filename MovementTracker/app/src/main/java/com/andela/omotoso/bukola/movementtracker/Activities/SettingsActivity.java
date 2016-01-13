package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.Constants;

public class SettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private TextView delayText;
    private Button setButton;
    private Button cancelButton;
    private NumberPicker numberPicker;
    private Dialog dialog;
    private static String DELAY_KEY = "";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();

        delayText = (TextView) findViewById(R.id.delay_time);
        delayText.setText(sharedPref.getString("DELAY_KEY", Constants.DEFAULT_DELAY));
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        delayText.setText(newVal + " minutes");
        editor.putString("DELAY_KEY", delayText.getText().toString());
        editor.commit();
    }

    public void showNumberPickerDialog(View view) {
        dialog = new Dialog(this);
        dialog.setTitle("Set Delay Time");
        dialog.setContentView(R.layout.number_picker);
        cancelButton = (Button)dialog.findViewById(R.id.cancel_button);
        final NumberPicker numberPicker = (NumberPicker)dialog.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(0);
        dialog.show();
        numberPicker.setOnValueChangedListener(this);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
