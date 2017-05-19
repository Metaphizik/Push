package com.example.metaphizik.push;
//TODO: все в String вынести в @new_notification.xml
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.metaphizik.push.auth.EmailPasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

//todo всевынести в Strings и пеервести
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: передалать на фрагменты
                Intent intent = new Intent(MainActivity.this, newNotification.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            ShowAuthForm();
        }

        /*subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().subscribeToTopic("messages");
            }
        });
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regID = FirebaseInstanceId.getInstance().getToken();
                Toast toast = Toast.makeText(getApplicationContext(),
                        regID, Toast.LENGTH_LONG);
                toast.show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data ) {
        if (data == null) {


            mail.setText("Your email");
            navigationView.getMenu().findItem(R.id.login).setVisible(true);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);}
        else {
            mail.setText(data.getStringExtra("email_tag"));
            navigationView.getMenu().findItem(R.id.login).setVisible(false);
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
        }
    }*/

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_signOut);
        if (user != null)
            item.setTitle("Сменить пользователя ("+user.getEmail().toString()+")");

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_signOut);
        if (user != null)
        item.setTitle("Выйти ("+user.getEmail().toString()+")");
        else item.setTitle("Войти");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.menu_signOut){
            ShowAuthForm();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowAuthForm() {
        Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
        startActivityForResult(intent, 1);
        /*if(mail.getText().equals("Your email")) {
            navigationView.getMenu().findItem(R.id.login).setVisible(true);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);

        } else {
            navigationView.getMenu().findItem(R.id.login).setVisible(false);
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
        }*/

    }


}
