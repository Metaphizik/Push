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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewNotificationActivity extends AppCompatActivity {

    private ArrayList<String> users;
    private DatabaseReference studentsRef, teachersRef, usersRef;
    private ValueEventListener studentListener;
    private ValueEventListener teacherListener;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private Map<String, Boolean> tokens;
    private MultiAutoCompleteTextView ToField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnotification);

        Button send;
        final EditText notification_body;
        notification_body = (EditText) findViewById(R.id.notification_body);
        ToField = (MultiAutoCompleteTextView)
                findViewById(R.id.multiAutoCompleteTextView);
        send = (Button) findViewById(R.id.send);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://notificationtest-d75ae.firebaseio.com/");
        final DatabaseReference notificationsRef = ref.child("notifications");
        studentsRef = ref.child("users/студенты");
        teachersRef = ref.child("users/преподаватели");
        usersRef = ref.child("users");
        mAuth = FirebaseAuth.getInstance();

        users = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, users);
        ToField.setAdapter(adapter);
        ToField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String currentDate = dateFormat.format(date);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<String> recievers = new ArrayList<>();
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //получение regID's
                        tokens = new HashMap<>();
                        int commas = 0;
                        String toField = ToField.getText().toString();
                        for (char i : toField.toCharArray()) {
                            if (i == ',') commas++;
                        }
                        Collections.addAll(recievers, toField.split(", ", commas + 1));
                        //поиск regID в базу по имени/группе
                        for (String receiver : recievers) {
                            String regexp = "^[0-9]{2}[А-Я]{2}[0-9]$";
                            Pattern pattern = Pattern.compile(regexp);
                            Matcher matcher = pattern.matcher(receiver);
                            if (matcher.matches()) {
                                //поиск по студентам
                                Iterable<DataSnapshot> dataSnap2 = dataSnapshot
                                        .child("студенты")
                                        .child(receiver).getChildren();
                                for (DataSnapshot dst : dataSnap2) {
                                    String c = dst.child("regID").getValue().toString();
                                    tokens.put(c, true);
                                }
                            } else {
                                //поиск по преподавателям
                                Iterable<DataSnapshot> dataSnap1 = dataSnapshot
                                        .child("преподаватели")
                                        .getChildren();
                                for (DataSnapshot dst : dataSnap1) {
                                    if (dst.child("имя").getValue().toString().equals(receiver)) {
                                        String c = dst.child("regID").getValue().toString();
                                        tokens.put(c, true);

                                    }
                                }

                            }
                        }
                        NotificationSample notif = new NotificationSample();
                        notif.setAuthor(mAuth.getCurrentUser().getDisplayName());
                        notif.setDate(currentDate);
                        notif.setText(notification_body.getText().toString());
                        notif.setTo(tokens);

                        //Start test
                        Map<String, Boolean> test = new HashMap<>();
                        test.put("e0ZYpKLNxTY:APA91bF9WSp1Az1N0cXf8V1FdyWA-f0IciNTJWGm3mp76iB5M5BxOv0n6ucPVSA-9Z5rfHzOlVv57i-WoeBd5fvuN-pIB60bfhv5GBgo7-dcFbst7lSZeCHcbur9gEdX-g4_LPbNkn7D"
                                , true);
                        notif.setTo(test);
                        //End Test

                        //проверяем что бы поля "получитель" и "текст" не были пустыми
                        if (notif.getTo().size() != 0) {
                            if (!notif.getText().equals("")) {
                                notificationsRef.push().setValue(notif);
                                Intent intent = new Intent(NewNotificationActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                            } else notification_body.setError("Заполните");

                        } else ToField.setError("Заполните");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(NewNotificationActivity.this, "Ошибка получения данных",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToField.setEnabled(false);
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
                adapter.notifyDataSetChanged();
                ToField.setEnabled(true);
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