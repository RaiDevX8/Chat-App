package com.example.chatapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.fragments.GroupMessageFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GroupChatActivity extends AppCompatActivity {

    public ImageView image ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_messages);

        // Fetch the group name from the intent
        String groupName = getIntent().getStringExtra("groupName");


        // Set the group name in the TextView
        TextView groupNameTextView = findViewById(R.id.group_name);
        groupNameTextView.setText(groupName);

        // Initialize fragment for displaying group messages
        if (savedInstanceState == null) {
            GroupMessageFragment groupMessageFragment = new GroupMessageFragment();

            // Pass the groupId and groupName as arguments to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("groupId", getIntent().getStringExtra("groupId"));
            bundle.putString("groupName", groupName);
            groupMessageFragment.setArguments(bundle);
            image=(ImageView) findViewById(R.id.group_icon);
            loadGroupProfileImage(getIntent().getStringExtra("groupId"),image);

            // Perform the fragment transaction to load the GroupMessageFragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, groupMessageFragment);
            fragmentTransaction.commit();
        }


    }
    // Method to load the group profile image from Firebase Storage
    public void loadGroupProfileImage(String groupId, ImageView imageViewGroup) {
        // Load the default image initially
        Glide.with(imageViewGroup.getContext())
                .load(R.drawable.person) // Default image
                .circleCrop()
                .into(imageViewGroup);

        // Fetch the actual image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("GroupProfileImages/" + groupId + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Image found, update the ImageView with the correct image
            Glide.with(imageViewGroup.getContext())
                    .load(uri)
                    .circleCrop()
                    .into(imageViewGroup);
        }).addOnFailureListener(e -> {
            // Failed to load the image, keep the default and log the error
            Log.e("GroupChatListAdapter", "Failed to load group profile image: " + e.getMessage());
        });
    }
}
