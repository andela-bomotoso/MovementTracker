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
    private SQLiteDatabase database;
    private Timer timer;

    public MovementTrackerDbHelper(Context context) {

        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
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

    public List<String> queryByStreet(String selectedDate) {

        List<String>records = new ArrayList<>();
        String streetName="";
        String activity = "";
        String duration = "";
        String previousStreet = "";
        String currentRecord = "";
        String previousRecord = "";
        String durationMinutes = "";
        int currentRow = 1;

        Cursor cursor = retrieveDBRows(selectedDate);
        int recordCount = cursor.getCount();

        while (!cursor.isAfterLast()) {

            streetName = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_STREET));
            activity = cursor.getString(cursor.getColumnIndex(MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY));
            duration = cursor.getString(cursor.getColumnIndex("Total_Duration"));
            durationMinutes = timer.formatTime(Integer.parseInt(duration));

            currentRecord += " \n"+activity+" "+durationMinutes;

            if((!streetName.equals(previousStreet) && previousStreet != "") || (currentRow == recordCount)) {

                if(previousStreet == "") {
                    records.add(streetName + previousRecord + "\n" + activity + " " + durationMinutes);
                }
                else {
                    records.add(previousStreet + previousRecord + "\n" + activity + " " + durationMinutes);
                }

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
        String deleteQuery = "SELECT street_name,activity,SUM(activity_duration) AS Total_Duration FROM tracker_trail " +
                "where tracking_date = "+"\'"+selectedDate+"\'" + "GROUP BY street_name,activity order by street_name";

        Cursor cursor = database.rawQuery(deleteQuery, null);
        cursor.moveToFirst();

        return cursor;
    }

    public void deleteTable() {

        database.beginTransaction();
        database.delete(MovementTrackerContract.MovementTracker.TABLE_NAME, null, null);
        database.setTransactionSuccessful();
        database.endTransaction();
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
