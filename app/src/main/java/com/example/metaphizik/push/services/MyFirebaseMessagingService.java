package com.example.metaphizik.push.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.metaphizik.push.ComplexPreferences;
import com.example.metaphizik.push.MainActivity;
import com.example.metaphizik.push.NotificationSample;
import com.example.metaphizik.push.NotificationsList;
import com.example.metaphizik.push.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static String OLD_NOTIFICATIONS;
    private NotificationSample data;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        Map<String, String> payload;
        payload = remoteMessage.getData();
        data = new NotificationSample();
        ArrayList<NotificationSample> oldNotifications = new ArrayList<>();
        String value, key;
        for (Map.Entry field : payload.entrySet()) {
            key = field.getKey().toString();
            value = field.getValue().toString();
            switch (key) {
                case "author":
                    data.setAuthor(value);
                    break;
                case "date":
                    data.setDate(value);
                    break;
                case "text":
                    data.setText(value);
                    break;
            }
        }
        //получаем старых уведомлений
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, OLD_NOTIFICATIONS, 0);
        NotificationsList complexObject = complexPreferences.getObject("NotificationsList", NotificationsList.class);
        if (complexObject != null) {
            oldNotifications = complexObject.getNotificationList();
        }
        //добавляем свежепринятое уведомление в список
        oldNotifications.add(data);
        complexObject = new NotificationsList();
        complexObject.setNotificationList(oldNotifications);
        complexPreferences = ComplexPreferences.getComplexPreferences(this, OLD_NOTIFICATIONS, 0);
        complexPreferences.putObject("NotificationsList", complexObject);
        complexPreferences.apply();

        sendNotification();
    }


    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        /* [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
         [END dispatch_job]*/
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(RemoteMessage remoteMessage) {

    }

    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data.getAuthor())
                .setContentText(data.getText())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of NotificationSample */, notificationBuilder.build());
    }
}