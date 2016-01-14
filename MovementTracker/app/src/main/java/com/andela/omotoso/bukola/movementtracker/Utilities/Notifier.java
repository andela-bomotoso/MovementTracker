package com.andela.omotoso.bukola.movementtracker.Utilities;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.andela.omotoso.bukola.movementtracker.Activities.MainActivity;
import com.andela.omotoso.bukola.movementtracker.R;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by GRACE on 1/13/2016.
 */
public class Notifier extends IntentService {

    private static final String TAG = "Notifier";
    private Context context;
    private Activity activity;
    int notificationId;

    public Notifier(Context context,Activity activity) {
        super(TAG);
        this.context = context;
        this.activity = activity;
        this.notificationId = 1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onHandleIntent(Intent intent) {

    }

    public void sendNotification(String notification) {

        Intent notificationIntent = new Intent(context,activity.getClass());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
        Notification myNotification  = new Notification.Builder(context)
                .setContentTitle(notification)
                .setContentText("Tracking in Progress")
                .setSmallIcon(R.drawable.ic_all_out_black_18dp)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setAutoCancel(false).build();
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, myNotification);
    }

    public void cancelNotification(Context ctx, int notificationId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ns);
        notificationManager.cancel(notificationId);
    }
}
