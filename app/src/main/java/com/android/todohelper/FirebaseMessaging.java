package com.android.todohelper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FirebaseMessaging extends FirebaseMessagingService {


    private static final String TAG = "FIREBASE MESSAGE";
    private static final String ACTION_COMPLETE = "com.android.todohelper";
    String CHANNEL_ID = "CHANNEL_ID";



    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//          sendRegistrationToServer(token);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(ACTION_COMPLETE));

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                // handleNow();
            }

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Intent intent = new Intent();
        intent.setAction("com.android.todohelper");
        sendBroadcast(intent);


        String CHANNEL_ID = "CHANNEL_ID";
        int notificationID =123;
        createNotificationChannel(getBaseContext());

        Intent actionIntent = new Intent(getBaseContext(), LoginActivity.class);
        int requestCode=new Random().nextInt(1000);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), requestCode, actionIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ok_emoji)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getBaseContext().getResources(),
                        R.drawable.ok_emoji
                ))
                .setContentTitle("Hi!")
                .setContentText(remoteMessage.getData().get("message"))
                .setPriority(2)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true)
                .setLights(Color.WHITE, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                .addAction(R.drawable.ok_emoji, "action",
//                        pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getBaseContext().getSystemService(NotificationManager.class);
        }
        Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
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
