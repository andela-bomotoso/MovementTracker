package com.andela.omotoso.bukola.movementtracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andela.omotoso.bukola.movementtracker.utilities.Constants;
import com.andela.omotoso.bukola.movementtracker.utilities.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GRACE on 1/15/2016.
 */
public class MovementTrackerDbHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "tracker.db";
    private SQLiteDatabase database;
    private Timer timer;

    public MovementTrackerDbHelper(Context context) {

        super(context, DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
        database = getReadableDatabase();
        timer = new Timer();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TRACKER_TABLE = "CREATE TABLE " + MovementTrackerContract.MovementTracker.TABLE_NAME + " (" +
                MovementTrackerContract.MovementTracker.COLUMN_DATE + " TEXT, " +
                MovementTrackerContract.MovementTracker.COLUMN_STREET + " TEXT, " +
                MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY + " TEXT, " +
                MovementTrackerContract.MovementTracker.COLUMN_DURATION + " INT, " +
                MovementTrackerContract.MovementTracker.COLUMN_LOG_TIME+ " TEXT)";
        sqLiteDatabase.execSQL(SQL_CREATE_TRACKER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovementTrackerContract.MovementTracker.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public List<String> queryByDate(String selectedDate) {

        List<String> records = new ArrayList<>();
        String streetName = "";
        String activity = "";
        String duration = "";
        String currentRecord = "";
        String durationMinutes = "";

        Cursor cursor = retrieveDBRows(selectedDate);

        while (!cursor.isAfterLast()) {

            streetName = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_STREET));
            activity = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY));
            duration = cursor.getString(cursor.getColumnIndex("Total_Duration"));
            durationMinutes = timer.formatTime(Integer.parseInt(duration));

            currentRecord = streetName + "\n" + activity + " " + durationMinutes;
            records.add(currentRecord);

            cursor.moveToNext();
        }
            return mapStreetNames(records);
    }

    private List<String>mapStreetNames(List<String>trackTrail) {

        String streetName = "";
        String activity = "";
        List<String>mappedStreetNames = new ArrayList<>();

        if(trackTrail.size()>0) {

            String previousStreet = trackTrail.get(0).split("\\r?\\n")[0];
            String previousActivity = trackTrail.get(0).split("\\r?\\n")[1];
            String previousRecord = "\n"+previousActivity;

            for (int i = 1; i < trackTrail.size(); i++) {

                streetName = trackTrail.get(i).split("\\r?\\n")[0];
                activity = trackTrail.get(i).split("\\r?\\n")[1];

                if ((!previousStreet.equals(streetName)) && !previousStreet.isEmpty()) {

                    mappedStreetNames.add(previousStreet + previousRecord);
                    previousRecord = "";
                }

                previousStreet = streetName;
                previousRecord += "\n" + activity;

            }
            if(!previousRecord.isEmpty()) {

                mappedStreetNames.add(previousStreet+previousRecord);
            }
        }

        return mappedStreetNames;
    }

    public List<String> queryByLocation(String selectedLocation, String selectedDate) {

        List<String>records = new ArrayList<>();
        String streetName="";
        String activity = "";
        String log_time = "";
        String duration = "";
        String previousStreet = "";
        String currentRecord = "";
        String previousRecord = "";
        String durationMinutes = "";
        int currentRow = 1;

        Cursor cursor = retrieveDbRows(selectedDate, selectedLocation);
        int recordCount = cursor.getCount();

        while (!cursor.isAfterLast()) {

            streetName = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_STREET));
            activity = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY));
            log_time = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_LOG_TIME));
            duration = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_DURATION));
            durationMinutes = timer.formatTime(Integer.parseInt(duration));

            currentRecord += " \n"+activity+" "+ durationMinutes+" "+ log_time;

            if((!streetName.equals(previousStreet) && previousStreet != "") || (currentRow == recordCount)) {

                records.add(previousRecord + "\n" + activity + " " + durationMinutes + " "+log_time);

                previousRecord = "";
                previousStreet = "";
                currentRecord = "";
            }
            else {

                previousStreet = streetName;
                previousRecord = currentRecord;
            }

            cursor.moveToNext();
            currentRow ++;
        }

        return records;
    }

    public Cursor retrieveDBRows(String selectedDate) {

        String selectQuery = "";

        selectQuery = "SELECT street_name,activity,SUM(activity_duration) AS Total_Duration FROM tracker_trail " +
                "where tracking_date = " + "\'" + selectedDate + "\'" +" and activity_duration !=0  GROUP BY street_name,activity order by street_name";

        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        return cursor;
    }

    public Cursor retrieveDbRows(String selectedDate,String selectedLocation) {

        String selectQuery = "SELECT street_name,activity,activity_duration,log_time FROM tracker_trail " +
                    "where rtrim(street_name) = " +"\'" +selectedLocation  + "\' " +" and tracking_date = " +"\'"+selectedDate +"\'"+" and activity_duration != "+0+" order by log_time";

        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        return cursor;
    }

    public void deleteQuery(String selectedDate) {

        String deleteQuery =  "delete from tracker_trail where tracking_date = "+"\'"+selectedDate+"\'";
        database.beginTransaction();
        database.execSQL(deleteQuery);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public int tableRows() {

        String query = "SELECT * FROM " + MovementTrackerContract.MovementTracker.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        int rowNum = cursor.getCount();
        cursor.close();

        return rowNum;
    }

    public void insertRows(String trackingDate, String streetName, String activity, int activityDuration, String logTime ) {

        ContentValues values = new ContentValues();
        values.put(MovementTrackerContract.MovementTracker.COLUMN_DATE, trackingDate);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_STREET, streetName);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY, activity);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_DURATION, activityDuration);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_LOG_TIME,logTime);
        database.beginTransaction();

        try {

           database.insert(MovementTrackerContract.MovementTracker.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        } finally {

            database.endTransaction();
        }
    }

}
