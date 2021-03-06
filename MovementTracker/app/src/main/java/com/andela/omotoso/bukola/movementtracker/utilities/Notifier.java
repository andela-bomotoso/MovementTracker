package com.andela.omotoso.bukola.movementtracker.utilities;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import com.andela.omotoso.bukola.movementtracker.R;

/**
 * Created by GRACE on 1/13/2016.
 */
public class Notifier extends IntentService {

    private static final String TAG = Constants.NOTIFIER_TAG;
    private Context context;
    private Activity activity;
    private int notificationId;

    public Notifier(Context context,Activity activity) {

        super(TAG);
        this.context = context;
        this.activity = activity;
        this.notificationId = Constants.NOTIFICATION_ID;
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
                .setContentText(Constants.NOTIFICATION_TITLE)
                .setSmallIcon(R.drawable.ic_all_out_black_18dp)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setAutoCancel(false).build();

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, myNotification);
    }

    public void cancelNotification(Context ctx, int notificationId) {

        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(notificationService);
        notificationManager.cancel(notificationId);
    }

}
