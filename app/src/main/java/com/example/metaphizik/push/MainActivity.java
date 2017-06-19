package com.example.metaphizik.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.metaphizik.push.auth.EmailPasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.metaphizik.push.services.MyFirebaseMessagingService.OLD_NOTIFICATIONS;

public class MainActivity extends AppCompatActivity {

    //todo всевынести в Strings и пеервести
    //todo удалить коменты и Log
    //todo совместимость и ворнинги поправитьт
    //todo проверить сотаольные туду
    //todo рассылка с консоли
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton)
                findViewById(R.id.floatingActionButton);

        //получаем список оповещений
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       final FirebaseUser user = FirebaseAuth.getInstance()
                                               .getCurrentUser();
                                       if (user == null) {
                                           ShowAuthForm();
                                       } else {
                                           if (user.isEmailVerified()) {
                                               Intent intent = new Intent(MainActivity.this,
                                                       NewNotificationActivity.class);
                                               startActivity(intent);
                                           } else {
                                               Toast.makeText(MainActivity.this,
                                                       "Email is not verificated",
                                                       Toast.LENGTH_SHORT).show();
                                               ShowAuthForm();
                                           }
                                       }
                                   }
                               }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
        ComplexPreferences complexPreferences = ComplexPreferences
                .getComplexPreferences(this, OLD_NOTIFICATIONS, 0);
        NotificationsList complexObject = complexPreferences
                .getObject("NotificationsList", NotificationsList.class);
        if (complexObject != null) {
            ArrayList<NotificationSample> listOldNotifications =
                    complexObject.getNotificationList();

            //заполянем оповещениями список
            RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            OldNotificationAdapter adapter = new OldNotificationAdapter(listOldNotifications);
            rv.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_signOut);
        if (user != null)
            item.setTitle("Выйти (" + user.getEmail() + ")");
        else item.setTitle("Войти");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_signOut) {
            ShowAuthForm();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowAuthForm() {
        Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
        startActivity(intent);
    }


}
