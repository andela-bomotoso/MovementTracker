package com.andela.omotoso.bukola.movementtracker.Utilities;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by GRACE on 1/11/2016.
 */
public class Timer {

    private Activity activity;
    private Boolean timer;
    private TextView timeSpentText;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Boolean getTimer() {
        return timer;
    }

    public void setTimer(Boolean timer) {
        this.timer = timer;
    }

    public TextView getTimeSpentText() {
        return timeSpentText;
    }

    public void setTimeSpentText(TextView timeSpentText) {
        this.timeSpentText = timeSpentText;
    }

    public void updateTimer() {

        Thread t = new Thread() {
            int count = 0;
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && getTimer()) {
                        Thread.sleep(1000);
                        count++;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeSpentText.setText(formatTime(count));
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
        int hr = seconds/3600;
        int rem = seconds%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;
        return hrStr+":"+mnStr+":"+secStr;
    }

    public int formatTimeText(String delayText) {
        return Integer.parseInt(delayText.split(" ")[0]);
    }

}
