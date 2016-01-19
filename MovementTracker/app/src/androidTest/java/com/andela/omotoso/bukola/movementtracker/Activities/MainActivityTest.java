package com.andela.omotoso.bukola.movementtracker.activities;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
   Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {

        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());

        activity = getActivity();
    }

    public void testStartTracking() throws Exception {

    }

    public void testStopTracking() throws Exception {

    }
}