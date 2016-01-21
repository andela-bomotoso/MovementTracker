package com.andela.omotoso.bukola.movementtracker.utilities;

import android.app.Activity;

/**
 * Created by GRACE on 1/11/2016.
 */
public class Timer {

    public  int timeInSeconds;
    private Activity activity;
    public  Boolean timer;
    public int count;
    private String timeSpent;
    private TimerListener timerListener;

    public Timer(Activity activity) {

        this.activity = activity;
    }

    public Timer() {

    }

    public void setTimerListener(TimerListener timerListener) {

        this.timerListener = timerListener;
    }

    public void turnOn() {
        this.timer = true;
        updateTimer();
    }

    public void turnOff() {
        reset();
        this.timer = false;
    }

    public void reset() {

        count = 0;
        timeInSeconds = 0;
    }

    public void updateTimer() {

        reset();
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted() && timer) {
                        Thread.sleep(1000);
                        count++;
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                timeInSeconds = count;
                                timeSpent = formatTime(count);
                                timerListener.onTick(timeSpent);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public String formatTime(int seconds) {

        int hr = seconds/Constants.SEXONDS_TO_HOUR;
        int rem = seconds%Constants.SEXONDS_TO_HOUR;
        int mn = rem/Constants.SECONDS_TO_MINUTES;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;

        return hrStr+":"+mnStr+":"+secStr;
    }

    public int formatDelayText(String delayText) {

        return Integer.parseInt(delayText.split(" ")[0]);
    }

}
