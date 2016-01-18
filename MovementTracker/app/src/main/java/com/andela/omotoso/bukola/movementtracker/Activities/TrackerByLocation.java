package com.andela.omotoso.bukola.movementtracker.Activities;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andela.omotoso.bukola.movementtracker.Dialogs.DatePickerFragment;
import com.andela.omotoso.bukola.movementtracker.Dialogs.DatePickerListener;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.DateHandler;

import java.util.Date;

public class TrackerByLocation extends AppCompatActivity{
    private Button cancelButton;
    private Button setButton;
    private Dialog dialog;
    private TextView selectedDateText;
    private DateHandler dateHandler;
    private FrameLayout fragementContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_by_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateHandler = new DateHandler();


        selectedDateText = (TextView)findViewById(R.id.selected_date);
        selectedDateText.setText(dateHandler.formatDate(new Date()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragementContainer = (FrameLayout)findViewById(R.id.date_picker_container);
    }

    public void showDatePickerDialog() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        fragmentTransaction.replace(R.id.date_picker_container, datePickerFragment);
        fragmentTransaction.commit();

        DatePickerListener datePickerListener = new DatePickerListener() {
            @Override
            public void onDatePicked(String dateSelected) {
                selectedDateText.setText(dateSelected);
            }
        };
        datePickerFragment.setDatePickerListener(datePickerListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.date_picker:
                showDatePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
