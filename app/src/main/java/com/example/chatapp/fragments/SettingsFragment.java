package com.example.chatapp.fragments;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    private static final String PREFS_NAME = "prefs";
    private static final String DARK_MODE_KEY = "dark_mode";
    private TextView user, phone;
    private ImageView img;
    private FirebaseStorage storage;
    // Declare FirebaseStorage here

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        user = view.findViewById(R.id.user);
        phone = view.findViewById(R.id.phone);
        img = view.findViewById(R.id.img);
        LinearLayout profile=view.findViewById(R.id.username);
        LinearLayout help=view.findViewById(R.id.help);
        help.setOnClickListener(v->helping());
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            Log.d("ProfileImage", "Loading profile image for userId: " + userId);
            loadProfileImage(img, userId); // Load the profile image
        }

        // Fetch user details from Firestore
        fetchUserDetails();

        // Initialize the logout button
        LinearLayout logout = view.findViewById(R.id.logout);

        // Initialize the theme toggle button
        toggleButton = view.findViewById(R.id.themeToggleButton);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);

        // Load the saved state and set the mode accordingly
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        setDarkMode(isDarkMode);
        toggleButton.setChecked(isDarkMode);

        // Set a listener for the toggle button to handle dark mode
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkMode(isChecked);
            saveDarkModeState(isChecked);
        });

        // Set the logout button's click listener
        logout.setOnClickListener(view1 -> logoutUser());
        profile.setOnClickListener(v->{
            Intent i=new Intent(getActivity(),Userprofile.class);
            startActivity(i);
        });

        return view;  // Return the view to be rendered
    }

    // Method to fetch user details from Firestore
    private void fetchUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");
                                String phonenum = currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "000-000-0000";

                                user.setText(firstName + " " + lastName);
                                phone.setText(phonenum);
                            } else {
                                user.setText("User not found");
                                phone.setText("Phone number not available");
                            }
                        } else {
                            user.setText("Error fetching user details");
                            phone.setText("Phone number not available");
                        }
                    });
        } else {
            user.setText("Guest");
            phone.setText("Phone number not available");
        }
    }

    // Method to log out the user
    private void logoutUser() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Optionally, clear shared preferences or session data
        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clear session-specific data if needed
        editor.apply();

        // Show a Toast message
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity or any other appropriate screen
        Intent intent = new Intent(getActivity(), loginphone.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
        startActivity(intent);
        getActivity().finish();  // Close the current activity
    }

    // Method to toggle dark mode
    private void setDarkMode(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    // Save dark mode state
    private void saveDarkModeState(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, isDarkMode);
        editor.apply();
    }

    private void loadProfileImage(ImageView imageView, String userId) {
        StorageReference profileImageRef = storage.getReference().child("ProfileImages/" + userId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Firebase Image", "Fetched image URI: " + uri.toString());
            Glide.with(imageView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.person) // Default image while loading
                    .error(R.drawable.person) // Error image if loading fails
                    .into(imageView);
        }).addOnFailureListener(e -> {
            Log.e("Firebase Image", "Error fetching image: ", e);
            imageView.setImageResource(R.drawable.person); // Set default image on error
        });
    }
    private void helping() {
        String url = "https://www.help.com/help"; // Replace with your actual help page URL
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
