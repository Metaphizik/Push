package com.example.metaphizik.push.services;

import android.content.SharedPreferences;

import com.example.metaphizik.push.auth.EmailPasswordActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        //это для вовзвращения настроек
        SharedPreferences tokenPathPref = getSharedPreferences(EmailPasswordActivity.TOKEN_PATH_PREFERENCE, 0);
        String who = tokenPathPref.getString("who" + "/", "NO");
        String group = tokenPathPref.getString("group" + "/", null);
        String push = tokenPathPref.getString("push", "NO");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://notificationtest-d75ae.firebaseio.com/");
        DatabaseReference regIdRef = ref.child("users/" + who + "/" + group + push);

        Map<String, String> regID = new HashMap<>();
        regID.put("regID", token);
        regIdRef.setValue(regID);
    }
}