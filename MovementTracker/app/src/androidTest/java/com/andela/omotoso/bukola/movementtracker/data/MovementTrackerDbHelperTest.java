package com.andela.omotoso.bukola.movementtracker.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by GRACE on 1/15/2016.
 */
public class MovementTrackerDbHelperTest extends AndroidTestCase {

    MovementTrackerDbHelper movementTrackerDbHelper;

    public MovementTrackerDbHelperTest() {

    }

    public void setUp() {
       //deleteTheDatabase();
        movementTrackerDbHelper = new MovementTrackerDbHelper(getContext());
    }

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovementTrackerDbHelper.DATABASE_NAME);
    }

    public void testOnCreate() throws Exception {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovementTrackerContract.MovementTracker.TABLE_NAME);

        SQLiteDatabase db = new MovementTrackerDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Database was created without the tracker_trail table",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovementTrackerContract.MovementTracker.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> TrackerColumnHashSet = new HashSet<String>();
        TrackerColumnHashSet.add(MovementTrackerContract.MovementTracker.COLUMN_DATE);
        TrackerColumnHashSet.add(MovementTrackerContract.MovementTracker.COLUMN_STREET);
        TrackerColumnHashSet.add(MovementTrackerContract.MovementTracker.COLUMN_ACTIVITY);
        TrackerColumnHashSet.add(MovementTrackerContract.MovementTracker.COLUMN_DURATION);
        TrackerColumnHashSet.add(MovementTrackerContract.MovementTracker.COLUMN_LOG_TIME);


        int columnNameIndex = c.getColumnIndex("name");

        do {
            String columnName = c.getString(columnNameIndex);
            TrackerColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required tracker entry columns",
                TrackerColumnHashSet.isEmpty());
        db.close();
    }
    

}