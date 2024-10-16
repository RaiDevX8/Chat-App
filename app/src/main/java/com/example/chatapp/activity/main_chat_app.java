package com.example.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.fragments.ChatListFragment;
import com.example.chatapp.fragments.ContactsFragment;
import com.example.chatapp.fragments.CreateGroupFragment;
import com.example.chatapp.fragments.GroupChatFragment;
import com.example.chatapp.fragments.MessageFragment;
import com.example.chatapp.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.widget.TextView;

public class main_chat_app extends AppCompatActivity {

    private TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerText = findViewById(R.id.header_text);

        // Load ChatFragment by default
        if (savedInstanceState == null) {
            loadFragment(new ChatListFragment());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_chats);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_chats) {
                    selectedFragment = new ChatListFragment();
                    headerText.setText("Chats");
                } else if (item.getItemId() == R.id.nav_contacts) {
                    selectedFragment = new ContactsFragment();
                    headerText.setText("Contacts");
                } else if (item.getItemId() == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    headerText.setText("Settings");
                }  else if (item.getItemId() == R.id.nav_groupchats) {
                    selectedFragment = new GroupChatFragment();
                }

                // Log the selected fragment
                Log.d("MainChatApp", "Selected Fragment: " + selectedFragment);

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }

                return false;
            };


    // Helper method to load fragments
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss(); // Prevent state loss
    }
}
