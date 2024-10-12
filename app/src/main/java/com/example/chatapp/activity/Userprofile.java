package com.example.chatapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.main_chat_app;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;

public class Userprofile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private ImageView addIcon;
    private Uri imageUri;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile); // Use your XML layout here

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages"); // Storage path
        firestore = FirebaseFirestore.getInstance(); // Firestore instance

        profileImageView = findViewById(R.id.profile_image);
        addIcon = findViewById(R.id.add_icon);
        EditText firstName = findViewById(R.id.first_name);
        EditText lastName = findViewById(R.id.last_name);
        Button saveButton = findViewById(R.id.save_button);

        // Set a click listener on the add icon to open gallery
        addIcon.setOnClickListener(v -> openGallery());

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String firstNameText = firstName.getText().toString().trim();
            String lastNameText = lastName.getText().toString().trim();
            if (firstNameText.isEmpty()) {
                Toast.makeText(Userprofile.this, "First Name is required", Toast.LENGTH_SHORT).show();
            } else {
                // Save the profile data
                saveUserProfile(firstNameText, lastNameText);
            }
        });
    }

    // Method to open gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    // This method gets called after an image is selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);  // Set selected image to the ImageView
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserProfile(String firstName, String lastName) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            HashMap<String, String> userDetails = new HashMap<>();
            userDetails.put("userId", userId);
            userDetails.put("firstName", firstName);
            userDetails.put("lastName", lastName);
            userDetails.put("mobileNumber", currentUser.getPhoneNumber());

            // Save user details in Firestore
            firestore.collection("Users").document(userId).set(userDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (imageUri != null) {
                        uploadImageToFirebase(imageUri, userId); // Upload image if selected
                    } else {
                        Toast.makeText(Userprofile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainChat();
                    }
                } else {
                    Toast.makeText(Userprofile.this, "Failed to save user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Uri uri, String userId) {
        StorageReference fileReference = storageReference.child(userId + ".jpg"); // Use userId as the image name
        fileReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(Userprofile.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    navigateToMainChat(); // Navigate to main chat after successful upload
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Upload failed", e);
                    Toast.makeText(Userprofile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMainChat() {
        Intent intent = new Intent(Userprofile.this, main_chat_app.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
