package com.andela.omotoso.bukola.movementtracker.utilities;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by GRACE on 1/15/2016.
 */
public class DateHandler {

    private String shortDateFormat;
    private String longDateFormat;
    private Date date;
    private String time_format;

    public DateHandler() {

        date = new Date();
        shortDateFormat =  Constants.SHORT_DATE_FORMAT;
        longDateFormat = Constants.LONG_DATE_FORMAT;
        time_format = Constants.TIME_FORMAT;
    }

    public String getCurrentDate() {

        return new SimpleDateFormat(shortDateFormat).format(date);
    }

    public String getCurrentTime() {

        return new SimpleDateFormat(time_format).format(date);
    }

    public String formatDate(Date dateToFormat) {

        return new SimpleDateFormat(longDateFormat).format(dateToFormat);
    }

    public String convertLongDateToShortDate(String longDate){

        Date convertedDate = date;

        try {
            convertedDate = new SimpleDateFormat(longDateFormat).parse(longDate);
        }
        catch (Exception exception) {

        }

        return new SimpleDateFormat(shortDateFormat).format(convertedDate);
    }

    public String convertShortDateToLongDate(String shortDate){

        Date convertedDate = new Date();

        try {
            convertedDate = new SimpleDateFormat(shortDateFormat).parse(shortDate);
        }
        catch (Exception exception) {

        }

        return new SimpleDateFormat(longDateFormat).format(convertedDate);
    }

}
