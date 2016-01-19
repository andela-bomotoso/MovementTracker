package com.andela.omotoso.bukola.movementtracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andela.omotoso.bukola.movementtracker.Dialogs.DatePickerFragment;
import com.andela.omotoso.bukola.movementtracker.Dialogs.DatePickerListener;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.Utilities.DateHandler;
import com.andela.omotoso.bukola.movementtracker.data.MovementTrackerDbHelper;

import java.util.Date;
import java.util.List;

public class TrackerByLocationActivity extends AppCompatActivity{

    private TextView selectedDateText;
    private DateHandler dateHandler;
    private FrameLayout fragementContainer;
    private ListView dataList;
    private MovementTrackerDbHelper movementTrackerDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_by_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateHandler = new DateHandler();

        selectedDateText = (TextView)findViewById(R.id.selected_date);
        selectedDateText.setText(dateHandler.formatDate(new Date()));
        movementTrackerDbHelper = new MovementTrackerDbHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragementContainer = (FrameLayout)findViewById(R.id.date_picker_container);
        dataList = (ListView)findViewById(R.id.data_list);
        displayData();
        setSelectedDateTextWatcher();

        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(TrackerByLocationActivity.this).setTitle("Delete Logs")
                        .setMessage(R.string.delete_trail)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String selectedDate = dateHandler.convertLongDateToShortDate(selectedDateText.getText().toString());
                                if (movementTrackerDbHelper.tableRows() != 0) {
                                    movementTrackerDbHelper.deleteQuery(selectedDate);
                                    Toast.makeText(getApplicationContext(), "Logs deleted!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "No track trail found!", Toast.LENGTH_LONG).show();
                                }
                                displayData();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });
    }

    public void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        DatePickerListener datePickerListener = new DatePickerListener() {
            @Override
            public void onDatePicked(String dateSelected) {
                selectedDateText.setText(dateSelected);
            }
        };
        datePickerFragment.setDatePickerListener(datePickerListener);

        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void displayData() {
        String no_trail = "No track trail found";
        String selectedDate =  dateHandler.convertLongDateToShortDate(selectedDateText.getText().toString());
        List<String> values  = movementTrackerDbHelper.queryByStreet(selectedDate);
        ArrayAdapter<String>adapter;
        if(values.size() == 0 ) {
            values.add(no_trail);
        }
        adapter = new ArrayAdapter<String>(this,R.layout.data_list_item,R.id.rowData,values);

        dataList.setAdapter(adapter);
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

    private void setSelectedDateTextWatcher() {
        selectedDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                displayData();
            }
        });
    }

}
