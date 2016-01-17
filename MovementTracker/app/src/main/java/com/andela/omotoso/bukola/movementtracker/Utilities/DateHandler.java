package com.andela.omotoso.bukola.movementtracker.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GRACE on 1/15/2016.
 */
public class DateHandler {

    public String getCurrentDate() {
        String DATE_FORMAT_NOW = "yyyy-MM-dd";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String stringDate = sdf.format(date );
       return stringDate;

    }

    public String formatDate() {
        String DATE_FORMAT_NOW = "EEE, dd MMM yyyy";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String stringDate = sdf.format(date );
        return stringDate;

    }

    public String getCurrentTime() {
        String TIME_FORMAT_NOW = "hh:mm:ss a";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_NOW);
        String stringTime = sdf.format(date );
        return stringTime;

    }


}
