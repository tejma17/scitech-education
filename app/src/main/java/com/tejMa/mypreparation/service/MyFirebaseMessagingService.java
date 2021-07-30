package com.tejMa.mypreparation.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tejMa.mypreparation.fragments.Quizzes;
import com.tejMa.mypreparation.R;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public  static int NOTIFICATION_ID = 1;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        generateNotification(Objects.requireNonNull(remoteMessage.getNotification()).getBody(), remoteMessage.getNotification().getTitle());
        
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void generateNotification(String body, String title) {

        Intent intent = new Intent(getApplicationContext(), Quizzes.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (NOTIFICATION_ID > 1073741824) {
           NOTIFICATION_ID = 0;
        }

        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build());


    }
}
