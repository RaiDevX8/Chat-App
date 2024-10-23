package com.example.chatapp.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GroupDetailActivity extends AppCompatActivity {

    private ImageView groupImageView;
    private TextView groupNameTextView, groupMemberCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_discription);

        // Get group details from the intent
        String groupName = getIntent().getStringExtra("groupName");
        String groupId = getIntent().getStringExtra("groupId");

        // Find views
        groupImageView = findViewById(R.id.group_icon);
        groupNameTextView = findViewById( R.id.group_name);
        groupMemberCountTextView = findViewById(R.id.group_member_count);

        // Set group name
        groupNameTextView.setText(groupName);

        // Load group image
        loadGroupProfileImage(groupId);

        // Set the member count (example: assuming static count for now)
        groupMemberCountTextView.setText("4 members");
    }

    private void loadGroupProfileImage(String groupId) {
        // Load default image
        Glide.with(this)
                .load(R.drawable.person)
                .circleCrop()
                .into(groupImageView);

        // Fetch actual image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("GroupProfileImages/" + groupId + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Image found, update the ImageView
            Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(groupImageView);
        }).addOnFailureListener(e -> {
            // Log error if failed to load image
        });
    }
}