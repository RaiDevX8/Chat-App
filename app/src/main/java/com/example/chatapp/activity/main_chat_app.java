package com.example.chatapp.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.fragments.ChatFragment;
import com.example.chatapp.fragments.ContactsFragment;
import com.example.chatapp.fragments.MessageFragment;
import com.example.chatapp.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
            loadFragment(new ChatFragment());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    // Bottom navigation listener using if-else
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                // Using if-else statements to handle the item selection
                if (item.getItemId() == R.id.nav_chats) {
                    selectedFragment = new ChatFragment();
                    headerText.setText("Chats");
                } else if (item.getItemId() == R.id.nav_contacts) {
                    selectedFragment = new ContactsFragment();
                    headerText.setText("Contacts");
                } else if (item.getItemId() == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    headerText.setText("Settings");
                } else if (item.getItemId() == R.id.nav_message) {
                    String contactName = "Actual Contact Name"; // Replace with actual contact name logic
                    Bundle args = new Bundle();
                    args.putString("contactName", contactName);

                    MessageFragment messageFragment = new MessageFragment();
                    messageFragment.setArguments(args);
                    selectedFragment = messageFragment;

                    headerText.setText(contactName); // Set header text to the contact name
                }

                // Load the selected fragment if it's not null
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true; // Indicate that the event was handled
                }

                return false; // If none match, return false
            };

    // Helper method to load fragments
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss(); // Prevent state loss
    }
}
