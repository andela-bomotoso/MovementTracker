package com.andela.omotoso.bukola.movementtracker.utilities;

import junit.framework.TestCase;

/**
 * Created by GRACE on 1/11/2016.
 */
public class TimerTest extends TestCase {
    Timer timer;

    public void setUp() throws Exception {
        super.setUp();
        timer = new Timer();

    }

    public void testFormatTimeWhenSecondsHasOnlyMinutes() throws Exception {

        assertEquals("00:01:00",timer.formatTime(60));
    }

    public void testFormatTimeWhenSecondsHasOnlySeconds() throws Exception {

        assertEquals("00:00:56",timer.formatTime(56));
    }

    public void testFormatTimeWhenSecondsHasMinutesAndSeconds() throws Exception {

        assertEquals("00:01:12",timer.formatTime(72));
    }

    public void testFormatTimeWhenSecondsHasMinutesSecondsHours() throws Exception {

        assertEquals("01:03:20",timer.formatTime(3800));
    }

    public void testFormTimeTest() {
        assertEquals(5,timer.formatTimeText("5 minutes"));
    }

}