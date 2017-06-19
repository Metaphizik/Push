
package com.example.metaphizik.push.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.metaphizik.push.ComplexPreferences;
import com.example.metaphizik.push.MainActivity;
import com.example.metaphizik.push.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.metaphizik.push.services.MyFirebaseMessagingService.OLD_NOTIFICATIONS;

public class EmailPasswordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";
    public static String TOKEN_PATH_PREFERENCE;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private TextView studentTV;
    private TextView teacherTV;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText displayedName;
    private Spinner spinner;
    public ProgressDialog mProgressDialog;
    private DatabaseReference studentsRef;
    private DatabaseReference teachersRef;
    private ArrayList<String> users = new ArrayList<>();
    ArrayAdapter<String> userAdapter;
    private boolean student;
    String group;

    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        displayedName = (EditText) findViewById(R.id.displayedName);
        spinner = (Spinner) findViewById(R.id.spinner);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);

        findViewById(R.id.sign_in_show).setOnClickListener(this);
        findViewById(R.id.register_show).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://notificationtest-d75ae.firebaseio.com/");
        studentsRef = ref.child("users/студенты");
        teachersRef = ref.child("users/преподаватели");
    }


    @Override
    public void onStart() {
        super.onStart();
        signOut();
        studentTV = (TextView) findViewById(R.id.studentTV);
        teacherTV = (TextView) findViewById(R.id.teacherTV);
        teacherTV.setTypeface(null, Typeface.BOLD);
        findViewById(R.id.down_fieds_and_buttons).setVisibility(View.INVISIBLE);
        findViewById(R.id.register_additional_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.show_buttons).setVisibility(View.VISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        final Switch switch4 = (Switch) findViewById(R.id.switch4);
        switch4.getThumbDrawable().setColorFilter(ContextCompat
                        .getColor(EmailPasswordActivity.this, R.color.switch_off_color),
                PorterDuff.Mode.SRC_IN);
        switch4.getTrackDrawable().setColorFilter(ContextCompat
                        .getColor(EmailPasswordActivity.this, R.color.switch_off_color),
                PorterDuff.Mode.SRC_IN);
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // проверяем какая учетная запись регистрируется
                if (isChecked) {
                    switch4.getThumbDrawable().setColorFilter(ContextCompat
                                    .getColor(EmailPasswordActivity.this, R.color.switch_on_color),
                            PorterDuff.Mode.SRC_IN);
                    switch4.getTrackDrawable().setColorFilter(ContextCompat
                                    .getColor(EmailPasswordActivity.this, R.color.switch_on_color),
                            PorterDuff.Mode.SRC_IN);
                    studentTV.setTypeface(null, Typeface.BOLD);
                    teacherTV.setTypeface(Typeface.DEFAULT);
                    spinner.setVisibility(View.VISIBLE);
                    userAdapter.notifyDataSetChanged();
                    student = true;
                } else {
                    switch4.getThumbDrawable().setColorFilter(ContextCompat
                                    .getColor(EmailPasswordActivity.this, R.color.switch_off_color),
                            PorterDuff.Mode.SRC_IN);
                    switch4.getTrackDrawable().setColorFilter(ContextCompat
                                    .getColor(EmailPasswordActivity.this, R.color.switch_off_color),
                            PorterDuff.Mode.SRC_IN);
                    teacherTV.setTypeface(null, Typeface.BOLD);
                    studentTV.setTypeface(Typeface.DEFAULT);
                    spinner.setVisibility(View.INVISIBLE);
                    student = false;
                }
            }
        });
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        boolean valid = true;
        String name = displayedName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            displayedName.setError("Required.");
            valid = false;
        } else {
            displayedName.setError(null);
        }
        if (!validateForm() && !valid) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            findViewById(R.id.show_buttons).setVisibility(View.GONE);
                            findViewById(R.id.register_additional_layout).setVisibility(View.GONE);
                            //запросна обновление имени
                            UserProfileChangeRequest profileUpdates = new
                                    UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayedName.getText().toString())
                                    .build();
                            if (user != null) {
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //отправка regID и имени в БД
                                                    String token = FirebaseInstanceId.getInstance()
                                                            .getToken();
                                                    if (student) {
                                                        group = spinner.getSelectedItem()
                                                                .toString();
                                                        Map<String, String> user = new HashMap<>();
                                                        user.put("имя", displayedName.getText()
                                                                .toString());
                                                        user.put("regID", token);
                                                        studentsRef.child(group).push()
                                                                .setValue(user);
                                                        //подписываемся на топик своей группы и
                                                        //топик "студенты"
                                                        FirebaseMessaging.getInstance()
                                                                .subscribeToTopic("Students");
                                                        /*FirebaseMessaging.getInstance()
                                                                .subscribeToTopic(group);*/

                                                        //сохраняем пару ключ-значение в память.
                                                        SharedPreferences settings =
                                                                getSharedPreferences(
                                                                        TOKEN_PATH_PREFERENCE, 0);
                                                        SharedPreferences.Editor editor =
                                                                settings.edit();
                                                        editor.putString("who", "студенты");
                                                        editor.putString("group", group);
                                                        editor.putString("push",
                                                                studentsRef.child(group).push()
                                                                        .getKey());
                                                        editor.apply();
                                                    } else {
                                                        Map<String, String> user = new HashMap<>();
                                                        user.put("имя", displayedName.getText()
                                                                .toString());
                                                        user.put("regID", token);
                                                        teachersRef.push().setValue(user);
                                                        //подписываемся на топик "преподаватели"
                                                        FirebaseMessaging.getInstance()
                                                                .subscribeToTopic("Teachers");
                                                        //сохраняем пару ключ-значение в память.
                                                        SharedPreferences settings =
                                                                getSharedPreferences(
                                                                        TOKEN_PATH_PREFERENCE, 0);
                                                        SharedPreferences.Editor editor = settings
                                                                .edit();
                                                        editor.putString("who", "преподаватели");
                                                        editor.putString("push", teachersRef.push()
                                                                .getKey());
                                                        editor.apply();
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null)
                                if (user.isEmailVerified()) {
                                    updateUI(user);
                                    invalidateOptionsMenu();
                                    Intent intent = new Intent(EmailPasswordActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(EmailPasswordActivity.this,
                                            "Email is not verificated",
                                            Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.email_password_buttons)
                                            .setVisibility(View.GONE);
                                    findViewById(R.id.email_password_fields)
                                            .setVisibility(View.GONE);
                                    findViewById(R.id.signed_in_buttons)
                                            .setVisibility(View.VISIBLE);
                                    findViewById(R.id.sign_out_button)
                                            .setVisibility(View.INVISIBLE);

                                }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        findViewById(R.id.show_buttons).setVisibility(View.VISIBLE);
        updateUI(null);
        SharedPreferences settings =
                getSharedPreferences(
                        TOKEN_PATH_PREFERENCE, 0);
        String who = settings.getString("who", null);

        //отписываемся от топиков
        if (who != null) {
            if (who.equals("студенты")) {
                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic("Студенты");
                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(group);
            }
            if (who.equals("преподаватели")) FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic("Преподаватели");
        }
        this.getSharedPreferences(TOKEN_PATH_PREFERENCE, 0).edit().clear().apply();
        //удаляем старые уведомления
        ComplexPreferences complexPreferences = ComplexPreferences
                .getComplexPreferences(this, OLD_NOTIFICATIONS, 0);
        complexPreferences.clear();
        complexPreferences.apply();
    }

    private void sendEmailVerification() {
        findViewById(R.id.verify_email_button).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            findViewById(R.id.verify_email_button).setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(EmailPasswordActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                signOut();
                                Intent intent = new Intent(EmailPasswordActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(EmailPasswordActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Обязательное.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Обязательное.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
            findViewById(R.id.down_fieds_and_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    void readUsers() {
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //получение списка пользователей из узла usersRef
                Iterable<DataSnapshot> dataSnaps = dataSnapshot.getChildren();
                userAdapter.notifyDataSetChanged();
                for (DataSnapshot users : dataSnaps) {
                    String c = users.getKey();
                    EmailPasswordActivity.this.users.add(c);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EmailPasswordActivity.this, "Ошибка получения данных",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_show) {
            findViewById(R.id.down_fieds_and_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.email_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_create_account_button).setVisibility(View.GONE);
            findViewById(R.id.register_additional_layout).setVisibility(View.GONE);
        } else if (i == R.id.register_show) {
            findViewById(R.id.down_fieds_and_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.email_create_account_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.register_additional_layout).setVisibility(View.VISIBLE);
            //заполняем спинер
            userAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, users);
            userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //userAdapter.setNotifyOnChange(true);
            spinner.setAdapter(userAdapter);
            readUsers();
            userAdapter.notifyDataSetChanged();
        } else if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
