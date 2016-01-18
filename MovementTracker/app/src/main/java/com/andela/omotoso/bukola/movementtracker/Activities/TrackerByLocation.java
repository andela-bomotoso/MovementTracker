package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.Dialogs.DateFragment;
import com.andela.omotoso.bukola.movementtracker.Dialogs.DatePickerListener;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.DateHandler;

import java.util.Calendar;
import java.util.Date;

public class TrackerByLocation extends AppCompatActivity{
    private Button cancelButton;
    private Button setButton;
    private Dialog dialog;
    private TextView selectedDateText;
    private DateHandler dateHandler;
    private DateFragment dateFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_by_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateHandler = new DateHandler();
        dateFragment = new DateFragment();

        selectedDateText = (TextView)findViewById(R.id.selected_date);
        selectedDateText.setText(dateHandler.formatDate(new Date()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showDatePickerDialog(View view) {
        DialogFragment datePickerFragment = new DialogFragment();
        datePickerFragment.show(getSupportFragmentManager(), "DateFragment");
        DatePickerListener datePickerListener = new DatePickerListener() {
            @Override
            public void onDatePicked(String dateSelected) {
                selectedDateText.setText(dateSelected);
            }
        };
        dateFragment.setDatePickerListener(datePickerListener);
    }



}
