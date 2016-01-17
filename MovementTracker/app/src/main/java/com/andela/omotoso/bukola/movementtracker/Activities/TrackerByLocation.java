package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.DateHandler;

import java.util.Calendar;
import java.util.Date;

public class TrackerByLocation extends AppCompatActivity {
    private Button cancelButton;
    private Button setButton;
    private Dialog dialog;
    private TextView selectedDateText;
    private DateHandler dateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_by_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateHandler = new DateHandler();

        selectedDateText = (TextView)findViewById(R.id.selected_date);
        selectedDateText.setText(dateHandler.formatDate());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void showDatePickerDialog(View view) {
        Date date = new Date();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.calendar);
        cancelButton = (Button)dialog.findViewById(R.id.cancel_button);
        setButton = (Button)dialog.findViewById(R.id.set_button);
        final DatePicker datePicker = (DatePicker)dialog.findViewById(R.id.calendar_picker);
        Calendar calendar = Calendar.getInstance();
        datePicker.setMaxDate(new Date().getTime());
        dialog.show();
        //datePicker.setOnClickListener(this);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
            }
        });
    }

}
