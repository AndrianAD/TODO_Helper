package com.android.todohelper.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;


import com.android.todohelper.R;
import com.android.todohelper.activity.LoginActivity;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    String CHANNEL_ID = "Chanel EVENT";
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        int notificationID = Integer.parseInt(intent.getStringExtra("notificationId"));
        createNotificationChannel(context);


        Intent actionIntent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int requestCode=new Random().nextInt(1000);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, actionIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ok_emoji)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.ok_emoji
                ))
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(2)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true)
                .setLights(Color.WHITE, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSound(alarmSound);
//                .addAction(R.drawable.ok_emoji, "action",
//                        pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = context.getSystemService(NotificationManager.class);
        }
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(250);
        }
        notificationManager.notify(notificationID, builder.build());

    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chanel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
