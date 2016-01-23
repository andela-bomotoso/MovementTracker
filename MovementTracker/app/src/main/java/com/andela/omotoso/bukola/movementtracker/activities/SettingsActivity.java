package com.andela.omotoso.bukola.movementtracker.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.utilities.DialogDivider;
import com.andela.omotoso.bukola.movementtracker.utilities.SharedPreferenceManager;
import com.andela.omotoso.bukola.movementtracker.utilities.Timer;

public class SettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private TextView delayText;
    private Button cancelButton;
    private Dialog dialog;
    private Timer timer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private SharedPreferenceManager sharedPreferenceManager;
    private int delay;
    private ContextThemeWrapper ctw;
    private DialogDivider dialogDivider;

    @TargetApi(17)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);


        ctw = new ContextThemeWrapper(this, R.style.Theme_Tracker);

        timer = new Timer();

        dialogDivider = new DialogDivider(this,dialog);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        delayText = (TextView) findViewById(R.id.delay_time);
        sharedPreferenceManager= new SharedPreferenceManager(this);
        delayText.setText(sharedPreferenceManager.retrieveDelayTime());
        delay = timer.formatDelayText(sharedPreferenceManager.retrieveDelayTime());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        delayText.setText(newVal + Constants.MINUTES);
        editor.putString(Constants.DELAY_KEY, delayText.getText().toString());
        editor.commit();
        sharedPreferenceManager.saveDelayTime(delayText.getText().toString());
    }

    public void showNumberPickerDialog(View view) {

        dialog = new Dialog(ctw);
        dialog.setTitle(Constants.DIALOG_TITLE);
        dialog.setContentView(R.layout.number_picker);
        cancelButton = (Button)dialog.findViewById(R.id.cancel_button);
        final NumberPicker numberPicker = (NumberPicker)dialog.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(1);
        numberPicker.setValue(delay);
        dialog.show();
        numberPicker.setOnValueChangedListener(this);
        dialogDivider.setDialog(dialog);
        dialogDivider.setDivider();

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
