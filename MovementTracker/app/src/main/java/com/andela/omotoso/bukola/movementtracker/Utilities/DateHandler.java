package com.andela.omotoso.bukola.movementtracker.utilities;

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

    public String formatDate(Date date) {
        String DATE_FORMAT_NOW = "EEE, dd MMM yyyy";
        //Date date = new Date();
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

    public String convertLongDateToShortDate(String longDate){
        String longDateFormat = "EEE, dd MMM yyyy";
        String shortDateFormat = "yyyy-MM-dd";
        SimpleDateFormat ldf = new SimpleDateFormat(longDateFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(shortDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = ldf.parse(longDate);
        }
        catch (Exception exception) {

        }
        return sdf.format(convertedDate);
    }

    public String convertShortDateToLongDate(String shortDate){
        String longDateFormat = "EEE, dd MMM yyyy";
        String shortDateFormat = "dd-MM-yyyy";
        SimpleDateFormat ldf = new SimpleDateFormat(longDateFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(shortDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = sdf.parse(shortDate);
        }
        catch (Exception exception) {

        }
        return ldf.format(convertedDate);
    }


}
