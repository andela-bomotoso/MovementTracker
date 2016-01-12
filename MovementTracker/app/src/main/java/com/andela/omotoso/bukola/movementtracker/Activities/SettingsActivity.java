package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.R;

public class SettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private TextView delayText;
    private Button setButton;
    private Button cancelButton;
    private NumberPicker numberPicker;
    static Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        delayText = (TextView) findViewById(R.id.delay_time);
        setButton = (Button)findViewById(R.id.set_button);
        cancelButton = (Button)findViewById(R.id.cancel_button);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public void showNumberPickerDialog(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Set Delay Time");
        dialog.setContentView(R.layout.number_picker);
        final NumberPicker numberPicker = (NumberPicker)dialog.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);

        dialog.show();
        numberPicker.setOnValueChangedListener(this);
        delayText.setText(String.valueOf(numberPicker.getValue() + " minutes"));
    }
    
}
