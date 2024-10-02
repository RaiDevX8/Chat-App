package com.example.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatapp.fragments.ChatFragment;
import com.example.chatapp.fragments.ContactsFragment;
import com.example.chatapp.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerText = findViewById(R.id.header_text);

        // Default fragment when opening the app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                    Fragment selectedFragment = null;

                    // Use if-else instead of switch-case
                    if (item.getItemId() == R.id.nav_chats) {
                        selectedFragment = new ChatFragment();
                        headerText.setText("Chats"); // Update header
                    } else if (item.getItemId() == R.id.nav_contacts) {
                        selectedFragment = new ContactsFragment();
                        headerText.setText("Contacts"); // Update header
                    } else if (item.getItemId() == R.id.nav_settings) {
                        selectedFragment = new SettingsFragment();
                        headerText.setText("Settings"); // Update header
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };
}
