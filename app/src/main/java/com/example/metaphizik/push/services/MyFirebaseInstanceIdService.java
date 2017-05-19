package com.example.metaphizik.push.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.

        //Единмтсенное что нам отсюда надо, переполучать regID при
        //его изменении, посылать его в БД и, возможно подписыавться на топик
        String token = FirebaseInstanceId.getInstance().getToken();

        //Log.d(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        //FirebaseMessaging.getInstance()
        //        .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }
}