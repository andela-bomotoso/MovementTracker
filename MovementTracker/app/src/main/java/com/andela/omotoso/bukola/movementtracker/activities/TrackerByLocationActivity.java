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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andela.omotoso.bukola.movementtracker.date_picker.DatePickerFragment;
import com.andela.omotoso.bukola.movementtracker.date_picker.DatePickerListener;
import com.andela.omotoso.bukola.movementtracker.R;
import com.andela.omotoso.bukola.movementtracker.utilities.DateHandler;
import com.andela.omotoso.bukola.movementtracker.data.MovementTrackerDbHelper;

import java.util.Date;
import java.util.List;

public class TrackerByLocationActivity extends AppCompatActivity{

    private TextView selectedDateText;
    private DateHandler dateHandler;
    private ListView dataList;
    private String selectedDate;
    private MovementTrackerDbHelper movementTrackerDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_by_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeComponents();
        setFabAction();
    }

    public void initializeComponents() {

        dateHandler = new DateHandler();

        selectedDate = "";
        selectedDateText = (TextView)findViewById(R.id.selected_date);
        selectedDateText.setText(dateHandler.formatDate(new Date()));
        selectedDate = dateHandler.convertLongDateToShortDate(selectedDateText.getText().toString());

        movementTrackerDbHelper = new MovementTrackerDbHelper(this);

        dataList = (ListView)findViewById(R.id.data_list);

        displayData();
        setSelectedDateTextWatcher();
    }

    public void setFabAction() {

        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                new AlertDialog.Builder(TrackerByLocationActivity.this)
                        .setTitle(R.string.delete_logs)
                        .setMessage(R.string.delete_trail)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                if (movementTrackerDbHelper.tableRows() != 0) {

                                    movementTrackerDbHelper.deleteQuery(selectedDate);
                                    Toast.makeText(getApplicationContext(), R.string.log_delete, Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.no_track, Toast.LENGTH_LONG).show();
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
        datePickerFragment.show(getSupportFragmentManager(), getString(R.string.datepicker));
    }

    public void displayData() {

        String no_trail = getString(R.string.no_track);
        String selectedDate =  dateHandler.convertLongDateToShortDate(selectedDateText.getText().toString());
        List<String> values  = movementTrackerDbHelper.queryByDate(selectedDate);

        if(values.size() == 0 ) {
            values.add(no_trail);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.data_list_item,R.id.rowData,values);
        dataList.setAdapter(adapter);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) dataList.getItemAtPosition(position);
                String location = itemValue.split("\\r?\\n")[0];
                displayTrailDetails(location);
            }
        });
    }

    private String getMessageFromList(List<String>values) {
        String message = "";
        for(String s: values) {
            message+=s;
        }
        return message;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
                selectedDate = dateHandler.convertLongDateToShortDate(selectedDateText.getText().toString());
                displayData();
            }
        });
    }

    public void displayTrailDetails(String location ) {

        new AlertDialog.Builder(TrackerByLocationActivity.this)

                .setTitle(location)
                .setMessage(getMessageFromList(movementTrackerDbHelper.queryByLocation(location.trim(), selectedDate)))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                    }
                })

                .show();
    }

}
