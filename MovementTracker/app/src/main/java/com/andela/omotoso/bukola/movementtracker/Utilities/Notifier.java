package com.andela.omotoso.bukola.movementtracker.Utilities;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.andela.omotoso.bukola.movementtracker.Activities.MainActivity;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by GRACE on 1/13/2016.
 */
public class Notifier extends IntentService {

    private static final String TAG = "Notifier";
    public Notifier() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onHandleIntent(Intent intent) {

    }

    private void sendNotification(String notification) {
        int mId = 1;
        Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(notificationPendingIntent);
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                builder.setColor(Color.RED)
                .setContentTitle(notification)
                .setContentText("Tracking in Progress")
                .setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId,builder.build());
    }
}
