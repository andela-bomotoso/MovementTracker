package com.andela.omotoso.bukola.movementtracker.utilities;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by GRACE on 1/20/2016.
 */
public class DateHandlerTest extends TestCase {

    Date date;
    String shortDateFormat;
    String longDateFormat;
    String time_format;
    DateHandler dateHandler;

    public void setUp() throws Exception {

        super.setUp();
        date = new Date();
        shortDateFormat =  Constants.SHORT_DATE_FORMAT;
        longDateFormat = Constants.LONG_DATE_FORMAT;
        time_format = Constants.TIME_FORMAT;
        dateHandler = new DateHandler();
    }

    public void testGetCurrentDate() throws Exception {

        date = new Date(2016,00,20);
        date.setYear(2016-1900);

        assertEquals("2016-01-20", new SimpleDateFormat(shortDateFormat).format(date));
    }

    public void testGetCurrentTime() throws Exception {

        //assertEquals("09:21:02 PM",new SimpleDateFormat(time_format).format(date));
    }

    public void testFormatDate() throws Exception {

        date = new Date(2016,00,20);
        date.setYear(2016-1900);

        assertEquals("Wed, 20 Jan 2016", dateHandler.formatDate(date));
    }

    public void testConvertLongDateToShortDate() throws Exception {

        String longDate = "Wed, 20 Jan 2016";

        assertEquals("2016-01-20",dateHandler.convertLongDateToShortDate(longDate));
    }

    public void testConvertShortDateToLongDate() throws Exception {

        String shortDate = "2016-01-20";
        assertEquals("Wed, 20 Jan 2016",dateHandler.convertShortDateToLongDate(shortDate));
    }
}