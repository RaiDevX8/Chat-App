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
import com.example.chatapp.fragments.MessageFragment;
import com.example.chatapp.models.Contact;
import com.example.chatapp.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ListView lvMembers;
    private List<Contact> contactList;
    private ContactsAdapter contactsAdapter;
    private List<String> selectedMembers = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private Uri groupImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_form);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        auth = FirebaseAuth.getInstance(); // Initialize Firebase Auth to get current user
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage

        lvMembers = findViewById(R.id.lvMembers);

        // Fetch users and populate list
        fetchUsersForGroup();

        findViewById(R.id.buttonCreateGroup).setOnClickListener(v -> {
            if (selectedMembers.isEmpty()) {
                Toast.makeText(CreateGroupActivity.this, "Please select members", Toast.LENGTH_SHORT).show();
            } else {
                createGroup();
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

    // Create the group with the selected members and save it in Firestore
    private void createGroup() {
        String groupName = ((EditText) findViewById(R.id.etGroupName)).getText().toString();
        String groupDescription = ((EditText) findViewById(R.id.etGroupDescription)).getText().toString();
        String currentUserId = auth.getCurrentUser().getUid(); // Get the current user ID (admin)

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add current user (admin) to selected members and groupAdmins
        selectedMembers.add(currentUserId);

        // Create a Group object
        Group group = new Group(groupName, groupDescription, selectedMembers, new ArrayList<>(List.of(currentUserId)), null);

        // Store the group in Firestore
        db.collection("Groups")
                .add(group)
                .addOnSuccessListener(documentReference -> {
                    if (groupImageUri != null) {
                        uploadGroupImage(documentReference.getId());
                    } else {
                        Toast.makeText(this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                        redirectToMessageFragment(documentReference.getId(), groupName); // Redirect after successful group creation
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show());
    }


    // Method to redirect to the MessageFragment
    private void redirectToMessageFragment(String groupId, String groupName) {
        Intent intent = new Intent(CreateGroupActivity.this, MessageFragment.class); // Replace with your MessageActivity
        intent.putExtra("groupId", groupId); // Pass the group ID
        intent.putExtra("groupName", groupName); // Pass the group name
        startActivity(intent); // Start the MessageActivity
        finish(); // Optional: finish the current activity if you don't want to return to it
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
                                finish();
                            });
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload group image", Toast.LENGTH_SHORT).show());
    }
}
