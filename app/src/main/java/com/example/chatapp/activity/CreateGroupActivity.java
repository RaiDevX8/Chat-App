package com.example.chatapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.models.Contact;
import com.example.chatapp.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ListView lvMembers;
    private List<Contact> contactList;
    private ContactsAdapter contactsAdapter;
    private List<String> selectedMembers = new ArrayList<>();
    private List<String> selectedMemberUserIds = new ArrayList<>(); // List to store userIds
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private Uri groupImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_form);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        auth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage

        lvMembers = findViewById(R.id.lvMembers);

        // Fetch users and populate list
        fetchUsersForGroup();

        findViewById(R.id.buttonCreateGroup).setOnClickListener(v -> {
            if (selectedMembers.isEmpty()) {
                Toast.makeText(CreateGroupActivity.this, "Please select members", Toast.LENGTH_SHORT).show();
            } else {
                // Fetch UserIds for selected members before creating the group
                fetchUserIdsForSelectedMembers();
            }
        });

        // Image section click handler
        findViewById(R.id.imageViewGroup).setOnClickListener(v -> openImageChooser());
    }

    // Method to open image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Group Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            groupImageUri = data.getData(); // Get the URI of the selected image
        }
    }

    // Method to fetch contacts and set them in ListView
    private void fetchUsersForGroup() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        contactList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("firstName") + " " + document.getString("lastName");
                            String phoneNumber = document.getString("mobileNumber");
                            contactList.add(new Contact(name, phoneNumber, null));
                        }
                        runOnUiThread(() -> {
                            contactsAdapter = new ContactsAdapter(contactList);
                            lvMembers.setAdapter(contactsAdapter);
                        });
                    }
                });
    }

    // Fetch UserIds for selected members by their phone numbers
    private void fetchUserIdsForSelectedMembers() {
        selectedMemberUserIds.clear(); // Clear the list before fetching userIds

        for (String phoneNumber : selectedMembers) {
            db.collection("Users")
                    .whereEqualTo("mobileNumber", phoneNumber)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString("userId"); // Get userId
                                selectedMemberUserIds.add(userId); // Add userId to the list
                            }

                            // After fetching all userIds, create the group
                            if (selectedMemberUserIds.size() == selectedMembers.size()) {
                                createGroupWithUserIds();
                            }
                        }
                    });
        }
    }

    // Custom adapter to handle the list of users with checkboxes
    private class ContactsAdapter extends BaseAdapter {

        private final List<Contact> contacts;

        public ContactsAdapter(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_member_checkbox, parent, false);
            }

            Contact contact = contacts.get(position);
            CheckBox checkBox = convertView.findViewById(R.id.checkbox_member);
            checkBox.setText(contact.getName());

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedMembers.add(contact.getPhoneNumber());
                } else {
                    selectedMembers.remove(contact.getPhoneNumber());
                }
            });

            return convertView;
        }
    }

    // Create the group with userIds
    private void createGroupWithUserIds() {
        String groupName = ((EditText) findViewById(R.id.etGroupName)).getText().toString();
        String groupDescription = ((EditText) findViewById(R.id.etGroupDescription)).getText().toString();
        String currentUserId = auth.getCurrentUser().getUid(); // Get the current user ID (admin)

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add current user (admin) to selected members
        selectedMemberUserIds.add(currentUserId);

        // Create a Group object
        Group group = new Group(groupName, groupDescription, selectedMemberUserIds, new ArrayList<>(List.of(currentUserId)), null);

        // Store the group in Firestore
        db.collection("Groups")
                .add(group)
                .addOnSuccessListener(documentReference -> {
                    String groupId = documentReference.getId(); // Get the created group ID

                    // Update the Users collection for each selected member
                    updateUsersWithGroupId(groupId);

                    // Handle image upload if there is a selected group image
                    if (groupImageUri != null) {
                        uploadGroupImage(groupId);
                    } else {
                        Toast.makeText(this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                        redirectToGroupChatFragment(); // Redirect to group chat
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show());
    }

    private void updateUsersWithGroupId(String groupId) {
        for (String userId : selectedMemberUserIds) {
            db.collection("Users")
                    .document(userId) // Use userId directly to update
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Check if the user already has a GroupIds array
                            List<String> groupIds = (List<String>) task.getResult().get("GroupIds");

                            if (groupIds == null) {
                                groupIds = new ArrayList<>(); // Initialize the list if it doesn't exist
                            }

                            // Add the new groupId to the list if it's not already present
                            if (!groupIds.contains(groupId)) {
                                groupIds.add(groupId);
                            }

                            // Update the user's document with the updated GroupIds array
                            db.collection("Users")
                                    .document(userId)
                                    .update("GroupIds", groupIds)
                                    .addOnSuccessListener(aVoid -> {
                                        // Successfully updated the user's group list
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle any failures while updating
                                        Toast.makeText(CreateGroupActivity.this, "Failed to update user with GroupIds", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        }
    }

    // New method to redirect back to GroupChatFragment
    private void redirectToGroupChatFragment() {
        finish(); // Close this activity to go back to the GroupChatFragment
    }

    // Upload the group profile image to Firebase Storage
    private void uploadGroupImage(String groupId) {
        StorageReference storageReference = storage.getReference("GroupProfileImages/" + groupId + ".jpg");
        storageReference.putFile(groupImageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    db.collection("Groups").document(groupId)
                            .update("groupImage", uri.toString())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Group Created Successfully with Image", Toast.LENGTH_SHORT).show();
                                redirectToGroupChatFragment(); // Redirect after successful image upload
                            });
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload group image", Toast.LENGTH_SHORT).show());
    }
}
