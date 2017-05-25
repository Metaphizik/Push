package com.example.metaphizik.push;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class NewNotificationActivity extends AppCompatActivity {

    private ArrayList<String> users = new ArrayList<>();
    private DatabaseReference studentsRef;
    private DatabaseReference teachersRef;
    private ValueEventListener studentListener;
    private ValueEventListener teacherListener;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private Map<String, Boolean> tokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnotification);

        Button send;
        final EditText notification_body;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://notificationtest-d75ae.firebaseio.com/");
        final DatabaseReference notificationsRef = ref.child("notifications");
        studentsRef = ref.child("users/студенты");
        teachersRef = ref.child("users/преподаватели");
        mAuth = FirebaseAuth.getInstance();

        notification_body = (EditText) findViewById(R.id.notification_body);
        send = (Button) findViewById(R.id.send);

        final MultiAutoCompleteTextView toField = (MultiAutoCompleteTextView)
                findViewById(R.id.multiAutoCompleteTextView);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, users);
        toField.setAdapter(adapter);
        toField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String currentDate = dateFormat.format(date);

        //получние regID преподавателей
        //todo пихать не в лист а в мап по типа имя-айди


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //teachersRef.addListenerForSingleValueEvent(teacherRegIdListener);

                final ArrayList<String> recievers = new ArrayList<>();

                //final ValueEventListener teacherRegIdListener = new ValueEventListener() {
                teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //получение regID's
                        Iterable<DataSnapshot> dataSnaps = dataSnapshot.getChildren();
                        tokens = new HashMap<>();
                        int commas = 0;
                        String to = toField.getText().toString();
                        for (char i : to.toCharArray()) {
                            if (i == ',') commas++;
                        }
                        Collections.addAll(recievers, to.split(", ", commas+1));
                        for (DataSnapshot dst : dataSnaps) {
                            //распарсивание получатеелй
                            for (String receiver : recievers) {
                                if (dst.child("имя").getValue().toString().equals(receiver)) {
                                    String c = dst.child("regID").getValue().toString();
                                    //String c = String.valueOf(tokens.child("regID").getValue());
                                    tokens.put(c, true);
                                }
                            }
                        }
                        NotificationExample notif = new NotificationExample();
                        notif.setAuthor(mAuth.getCurrentUser().getDisplayName());
                        notif.setDate(currentDate);
                        notif.setText(notification_body.getText().toString());
                        //notif.setTo(tokens);
                        Map<String,Boolean> test = new HashMap<>();
                        test.put("e0ZYpKLNxTY:APA91bF9WSp1Az1N0cXf8V1FdyWA-f0IciNTJWGm3mp76iB5M5BxOv0n6ucPVSA-9Z5rfHzOlVv57i-WoeBd5fvuN-pIB60bfhv5GBgo7-dcFbst7lSZeCHcbur9gEdX-g4_LPbNkn7D"
                                ,true);
                        test.put("-9Z5rfHzOlVv57i-WoeBd5fvuN-pIB60bfhv5GBgo7-dcFbst7lSZeCHcbur9gEdX-g4"
                                ,true);
                        notif.setTo(test);

                        notificationsRef.push().setValue(notif);
                        Intent intent = new Intent(NewNotificationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(NewNotificationActivity.this, "Ошибка получения данных",
                                Toast.LENGTH_SHORT).show();
                    }

                });

                /*String author = mAuth.getCurrentUser().getDisplayName();
                Map<String, String> NotificationExample = new HashMap<>();
                Map<String, Map> komu = new HashMap<>();
                //NotificationExample.put("получатель", toField.getText().toString());
                NotificationExample.put("текст", notification_body.getText().toString());
                NotificationExample.put("автор", author);
                NotificationExample.put("дата", currentDate);
                komu.put("получатели", tokens);*/


                //проверяем что бы поля "получитель" и "текст" не были пустыми
                /*for (Map.Entry to : NotificationExample.entrySet()) {
                    if (true) { //верунть на to.getKey().equals("получатель") && !to.getValue().equals("")
                        for (Map.Entry body : NotificationExample.entrySet()) {
                            if (body.getKey().equals("текст") && !body.getValue().equals("")) {
                                notificationsRef.push().setValue(NotificationExample);
                                //String key = notificationsRef.push().getKey();
                                //notificationsRef.child(key).setValue(komu);
                                Intent intent = new Intent(NewNotificationActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else if (body.getKey().equals("текст") && body.getValue().equals("")) {
                                Toast.makeText(NewNotificationActivity.this, "нет body",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (to.getKey().equals("получатель") && to.getValue().equals("")) {
                        Toast.makeText(NewNotificationActivity.this, "нет получателя",
                                Toast.LENGTH_SHORT).show();

                    }
                }*/
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        readUsersList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        studentsRef.removeEventListener(studentListener);
        teachersRef.removeEventListener(teacherListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        studentsRef.removeEventListener(studentListener);
        teachersRef.removeEventListener(teacherListener);
    }

    void readUsersList() {
        studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //получение списка пользователей из узла usersRef
                Iterable<DataSnapshot> dataSnaps = dataSnapshot.getChildren();
                for (DataSnapshot users : dataSnaps) {
                    String c = users.getKey();
                    NewNotificationActivity.this.users.add(c);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewNotificationActivity.this, "Ошибка получения данных",
                        Toast.LENGTH_SHORT).show();
            }

        };
        studentsRef.addListenerForSingleValueEvent(studentListener);
        teacherListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String c = String.valueOf(dsp.child("имя").getValue());
                    //String c = dsp.child("имя").getValue().toString();
                    users.add(c); //add result into array list
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewNotificationActivity.this, "Ошибка получения данных",
                        Toast.LENGTH_SHORT).show();
            }

        };
        teachersRef.addListenerForSingleValueEvent(teacherListener);
    }

}