package com.andela.omotoso.bukola.movementtracker.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.andela.omotoso.bukola.movementtracker.R;
import com.robotium.solo.Solo;

import java.util.Timer;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    Solo solo;
    ToggleButton trackerButton;

    TextView timeSpentText;
    TextView currentActivityText;

    com.andela.omotoso.bukola.movementtracker.utilities.Timer timer;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {

        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());

        activity = getActivity();

        trackerButton = (ToggleButton) activity.findViewById(R.id.tracker_button);
        timer = new com.andela.omotoso.bukola.movementtracker.utilities.Timer();

        timeSpentText = (TextView)activity.findViewById(R.id.time_spentText);
        currentActivityText = (TextView)activity.findViewById(R.id.current_ActivityText);
    }

    public void testTextViews() {

    }

    public void testTracking() throws Exception {

        String timeSpent =  timeSpentText.getText().toString();
        String currentActivity =  currentActivityText.getText().toString();

        assertEquals(timeSpent, "00:00:00");
        assertEquals(currentActivity,"tracking not started");

        TouchUtils.clickView(this, trackerButton);
        //assertFalse(timeSpentText.getText().toString().equals("00:00:00"));
        assertFalse(currentActivity.equals("connecting..."));

        TouchUtils.clickView(this, trackerButton);
        assertTrue(timeSpentText.getText().toString().equals("00:00:00"));
        assertEquals(currentActivity,"tracking not started");

    }

    public void testCheckLocationWhenLocationIsValid() throws Exception {
            String locationText = "Adesina Street, Lagos";
        assertEquals("Adesina Street, Lagos",activity.checkLocation(locationText));
    }

    public void testCheckLocationWhenLocationIsRetrieving() throws Exception {
        String locationText = "searching location...";
        assertEquals("Unknown Location",activity.checkLocation(locationText));
    }

    public void testCheckLocationWhenLocationIsEmpty() throws Exception {
        String locationText = "";
        assertEquals("Unknown Location",activity.checkLocation(locationText));
    }

}