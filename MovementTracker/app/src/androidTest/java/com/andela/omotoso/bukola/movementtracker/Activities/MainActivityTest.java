package com.andela.omotoso.bukola.movementtracker.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.andela.omotoso.bukola.movementtracker.R;
import com.robotium.solo.Solo;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    Solo solo;
    ToggleButton trackerButton;

    TextView timeSpentText;
    TextView currentActivityText;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {

        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());

        activity = getActivity();

        trackerButton = (ToggleButton) activity.findViewById(R.id.tracker_button);

        timeSpentText = (TextView)activity.findViewById(R.id.time_spentText);
        currentActivityText = (TextView)activity.findViewById(R.id.current_ActivityText);
    }

    public void testTextViews() {

    }

    public void testStartTracking() throws Exception {

        String timeSpent =  timeSpentText.getText().toString();
        String currentActivity =  currentActivityText.getText().toString();

        assertEquals(timeSpent,"00:00:00");
        assertEquals(currentActivity,"connecting...");

        TouchUtils.clickView(this, trackerButton);
        assertFalse(timeSpentText.getText().toString().equals("00:00:00"));

    }

    public void testStopTracking() throws Exception {

    }
}