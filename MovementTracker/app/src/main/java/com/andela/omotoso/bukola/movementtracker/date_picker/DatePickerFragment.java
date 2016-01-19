package com.andela.omotoso.bukola.movementtracker.date_picker;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.andela.omotoso.bukola.movementtracker.utilities.DateHandler;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Calendar calendar;
    private DatePickerListener listener;
    private String selectedDate;
    private DateHandler dateHandler;


    public DatePickerFragment() {

        dateHandler = new DateHandler();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        selectedDate = year+"-"+month+1+"-"+day;
        listener.onDatePicked(dateHandler.convertShortDateToLongDate(selectedDate));
    }
    public void setDatePickerListener(DatePickerListener listener) {

        this.listener = listener;
    }

}
