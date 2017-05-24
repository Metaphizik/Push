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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewNotificationActivity extends AppCompatActivity {

    private ArrayList<String> users = new ArrayList<>();
    private DatabaseReference studentsRef;
    private DatabaseReference teachersRef;
    private ValueEventListener studentListener;
    private ValueEventListener teacherListener;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;

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

        final MultiAutoCompleteTextView to = (MultiAutoCompleteTextView)
                findViewById(R.id.multiAutoCompleteTextView);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, users);
        to.setAdapter(adapter);
        to.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String currentDate = dateFormat.format(date);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: сменить автора
                //showProgressDialog();
                String author = mAuth.getCurrentUser().getDisplayName();
                Map<String, String> notification = new HashMap<>();
                notification.put("получатель", to.getText().toString());
                notification.put("текст", notification_body.getText().toString());
                notification.put("автор", author);
                notification.put("дата", currentDate);

                //проверяем что бы поля "получитель" и "текст" не были пустыми
                for (Map.Entry to : notification.entrySet()) {
                    if (to.getKey().equals("получатель") && !to.getValue().equals("")) {
                        for (Map.Entry body : notification.entrySet()) {
                            if (body.getKey().equals("текст") && !body.getValue().equals("")) {
                                notificationsRef.push().setValue(notification);
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
                }
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