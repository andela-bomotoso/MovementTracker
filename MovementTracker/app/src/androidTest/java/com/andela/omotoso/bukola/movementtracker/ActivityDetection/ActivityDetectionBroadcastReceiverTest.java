package com.andela.omotoso.bukola.movementtracker.ActivityDetection;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by GRACE on 1/11/2016.
 */
public class ActivityDetectionBroadcastReceiverTest extends TestCase {
   // ActivityDetectionBroadcastReceiver activityDetectionBroadcastReceiver;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testGetHighestActivityConfidence() throws Exception {
        List<String>activities = new ArrayList<>(Arrays.asList("Walking","Standing Still","In a Vehicle"));
        List<Integer>confidence = new ArrayList<>(Arrays.asList(34,50,16));
        //assertEquals("Standing Still",ActivityDetectionBroadcastReceiver.getHighestActivityConfidence(confidence,activities));
    }





    public void testGetActivityType() throws Exception {

    }
}