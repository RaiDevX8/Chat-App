package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.activity.main_chat_app;
import com.example.chatapp.activity.loginphone;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.splashscreen); // Replace with your actual splash screen layout

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is already logged in
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // User is logged in, navigate to Userprofile activity
                    startActivity(new Intent(MainActivity.this, main_chat_app.class));
                } else {
                    // User is not logged in, navigate to loginphone activity
                    startActivity(new Intent(MainActivity.this, loginphone.class));
                }
                finish();
            }
        }, 3000);
    }
}
