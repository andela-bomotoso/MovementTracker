package com.andela.omotoso.bukola.movementtracker.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by GRACE on 1/15/2016.
 */
public class MovementTrackerContract {

    public static final String CONTENT_AUTHORITY = "com.andela.omotoso.bukola.movementtracker.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +CONTENT_AUTHORITY);
    public static final String PATH_MOVEMENT_TRACKER = "movement_tracker";

    public static final class MovementTracker implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVEMENT_TRACKER).build();
        public static final String TABLE_NAME = "tracker_trail";
        public static final String COLUMN_DATE = "tracking_date";
        public static final String COLUMN_STREET = "street_name";
        public static final String COLUMN_ACTIVITY = "activity";
        public static final String COLUMN_DURATION = "activity_duration";
    }
}
