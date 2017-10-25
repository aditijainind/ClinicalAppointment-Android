package aditij.assignment4.services;

/**
 * Created by aditij on 3/13/2015.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import aditij.assignment4.R;

/**
 * Created by aditij on 2/7/2015.
 * This class has the function of receiving the intent broadcast by AlarmService
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = Integer.valueOf(intent.getStringExtra("alarmId"));
        String alarmLabel = intent.getStringExtra("alarmLabel");

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(alarmLabel)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setContentText(alarmLabel)
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(alarmId,builder.build());


    }
}

