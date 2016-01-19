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
        // Required empty public constructor
        dateHandler = new DateHandler();
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
//        return textView;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        //selectedDate = day+"-"+month+1+"-"+year;
        selectedDate = year+"-"+month+1+"-"+day;
        listener.onDatePicked(dateHandler.convertShortDateToLongDate(selectedDate));

    }
    public void setDatePickerListener(DatePickerListener listener) {
        this.listener = listener;
    }

}
