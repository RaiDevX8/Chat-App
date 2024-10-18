package com.example.chatapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.activity.Userprofile;
import com.example.chatapp.activity.loginphone;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsFragment extends Fragment {

    private ToggleButton toggleButton;
    private SharedPreferences sharedPreferences;
    private TextView userText, phoneText;
    private ImageView profileImage;
    private FirebaseStorage storage;

    private static final String PREFS_NAME = "prefs";
    private static final String DARK_MODE_KEY = "dark_mode";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize UI elements
        userText = view.findViewById(R.id.user);
        phoneText = view.findViewById(R.id.phone);
        profileImage = view.findViewById(R.id.img);
        LinearLayout profile = view.findViewById(R.id.username);
        LinearLayout help = view.findViewById(R.id.help);
        LinearLayout logout = view.findViewById(R.id.logout);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Fetch user data
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchUserDetails(currentUser.getUid());
            loadProfileImage(currentUser.getUid());
        } else {
            userText.setText("Guest");
            phoneText.setText("Phone number not available");
        }

        // Help button click
        help.setOnClickListener(v -> openHelpPage());

        // Profile button click
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Userprofile.class);
            startActivity(intent);
        });

        // Handle logout
        logout.setOnClickListener(v -> logoutUser());

        // Handle theme toggling
        setupDarkModeToggle(view);

        return view;
    }

    // Fetch user details from Firestore
    private void fetchUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && isAdded()) { // Ensure fragment is attached
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                    userText.setText((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                    phoneText.setText(phoneNumber != null ? phoneNumber : "000-000-0000");
                } else {
                    showError("User not found");
                }
            } else {
                showError("Error fetching user details");
            }
        });
    }

    // Load profile image from Firebase Storage
    private void loadProfileImage(String userId) {
        StorageReference profileImageRef = storage.getReference().child("ProfileImages/" + userId + ".jpg");
        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (isAdded()) { // Ensure fragment is attached
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.person) // Placeholder image
                        .error(R.drawable.person)       // Error image
                        .into(profileImage);
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) { // Ensure fragment is attached
                profileImage.setImageResource(R.drawable.person); // Set default on error
            }
        });
    }
    // Handle dark mode toggle
    private void setupDarkModeToggle(View view) {
        toggleButton = view.findViewById(R.id.themeToggleButton);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);

        // Set the initial toggle state without applying dark mode
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        toggleButton.setChecked(isDarkMode);

        // Handle toggle changes (apply dark mode only on button click)
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Apply dark mode only when the user interacts with the toggle
            setDarkMode(isChecked);
            saveDarkModeState(isChecked);
        });
    }


    // Set dark mode on/off and reload the fragment
    private void setDarkMode(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Recreate the fragment to apply changes
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .detach(this) // Detach current fragment
                    .attach(this) // Attach it again to reload
                    .commit();
        }
    }


    // Save dark mode state to SharedPreferences
    private void saveDarkModeState(boolean isDarkMode) {
        sharedPreferences.edit().putBoolean(DARK_MODE_KEY, isDarkMode).apply();
    }

    // Logout user
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE);
        preferences.edit().clear().apply(); // Clear session data

        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), loginphone.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        startActivity(intent);
        getActivity().finish(); // Finish current activity
    }

    // Open help page
    private void openHelpPage() {
        String url = "https://www.help.com/help"; // Replace with actual help page URL
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    // Show error message
    private void showError(String message) {
        userText.setText(message);
        phoneText.setText("Phone number not available");
    }
}
