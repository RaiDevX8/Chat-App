package com.example.chatapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.main_chat_app;

import java.io.IOException;

public class Userprofile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private ImageView addIcon;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile); // Use your XML layout here

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
            if (firstNameText.isEmpty()) {
                Toast.makeText(Userprofile.this, "First Name is required", Toast.LENGTH_SHORT).show();
            } else {
                // Save the profile data (this part is up to your needs)
                Toast.makeText(Userprofile.this, "Profile saved", Toast.LENGTH_SHORT).show();

                // Navigate to AnotherActivity
                Intent intent = new Intent(Userprofile.this, main_chat_app.class);
                startActivity(intent);
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
}
