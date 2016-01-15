package com.andela.omotoso.bukola.movementtracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GRACE on 1/15/2016.
 */
public class MovementTrackerDbHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "tracker.db";
    static final int DATABASE_VERSION = 2;

    public MovementTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TRACKER_TABLE = "CREATE TABLE " + MovementTrackerContract.MovementTracker.TABLE_NAME + " (" +
                MovementTrackerContract.MovementTracker.COLUMN_DATE + " TEXT, " +
                MovementTrackerContract.MovementTracker.COLUMN_STREET + " TEXT, " +
                MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY + " TEXT, " +
                MovementTrackerContract.MovementTracker.COLUMN_DURATION + " TEXT)";
        sqLiteDatabase.execSQL(SQL_CREATE_TRACKER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovementTrackerContract.MovementTracker.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public String queryByDate(String tracking_date) {

        String query = "SELECT tracking_date,street_name,activity,activity_duration FROM tracker_trail where TRACKING_DATE ='" + tracking_date + "'";
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("tracking_date"));
        } else {
            //return Utilities.retrieveSavedData(source,destination);
            return "";
        }

    }

    public String queryByStreet() {

        String query = "SELECT tracking_date,street_name,activity,activity_duration FROM tracker_trail";
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("street"));
        } else {
            //return Utilities.retrieveSavedData(source,destination);
            return "";
        }
    }

    public void deleteTable() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MovementTrackerContract.MovementTracker.TABLE_NAME, null, null);
    }

    public int tableRows() {
        String query = "SELECT * FROM " + MovementTrackerContract.MovementTracker.TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        int rowNum = cursor.getCount();
        cursor.close();

        return rowNum;
    }

    public void insertRows(String trackingDate, String streetName, String activity, String activityDuration ) {
        ContentValues values = new ContentValues();
        values.put(MovementTrackerContract.MovementTracker.COLUMN_DATE,trackingDate);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_STREET,streetName);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY,activity);
        values.put(MovementTrackerContract.MovementTracker.COLUMN_DURATION,activityDuration);
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            database.insertOrThrow(MovementTrackerContract.MovementTracker.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public String checkTableExistence() {
        String name = "";
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT name FROM sqlite_master WHERE type="+"'table'";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        return name;
    }




}
