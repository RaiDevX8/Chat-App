package com.example.chatapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.GroupMessageAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.GroupMessageModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroupMessageAdapter adapter;
    private List<GroupMessageModel> messageList;
    private String currentUserId;  // Will be fetched from Firebase Authentication
    private String groupId;        // Passed from the previous activity
    private String groupName;      // Passed from the previous activity

    private EditText messageEditText;
    private ImageView sendButton;
    private TextView groupNameTextView;
    private TextView groupDescriptionTextView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_messages, container, false);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Fetch current user's ID from FirebaseAuth
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();  // Get the actual user ID
        }

        // Fetch arguments passed from GroupChatActivity
        if (getArguments() != null) {
            groupId = getArguments().getString("groupId");
            groupName = getArguments().getString("groupName");
        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        adapter = new GroupMessageAdapter(messageList, currentUserId);
        recyclerView.setAdapter(adapter);

        // Initialize UI elements for message input
        messageEditText = view.findViewById(R.id.edit_text_message);
        sendButton = view.findViewById(R.id.image_view_send);
        groupNameTextView = view.findViewById(R.id.text_view_group_name);
        groupDescriptionTextView = view.findViewById(R.id.text_view_group_description);

        // Set group name in the UI
        groupNameTextView.setText(groupName);

        // Set up send button click listener
        sendButton.setOnClickListener(v -> sendMessage());

        // Start listening for real-time messages from Firebase
        listenForMessages();

        return view;
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Create message data to be sent
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("senderId", currentUserId);
            messageData.put("message", messageText);
            messageData.put("timestamp", Timestamp.now());

            // Save message to Firestore under the current group
            db.collection("Groups").document(groupId)
                    .collection("Messages")
                    .add(messageData)
                    .addOnSuccessListener(documentReference -> {
                        // Clear the input field after the message is sent
                        messageEditText.setText("");
                        recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
                    })
                    .addOnFailureListener(e -> Log.e("GroupMessageFragment", "Error sending message", e));
        }
    }

    // Listen for real-time message updates from Firebase Firestore
    private void listenForMessages()    {
        db.collection("Groups").document(groupId)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        messageList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            // Create a new message from the document
                            String senderId = document.getString("senderId");
                            String messageText = document.getString("message");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            // Create a new GroupMessageModel
                            GroupMessageModel message = new GroupMessageModel(senderId, messageText, timestamp);
                            messageList.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
                    }
                });
    }

}