package com.example.chatapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.Message;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageFragment extends Fragment {
    private static final String ARG_RECEIVER_ID = "receiver_id";
    private static final String ARG_CONTACT_NAME = "contact_name";
    private static final String ARG_SENDER_ID = "sender_id"; // Added sender ID

    private String receiverId;
    private String senderId; // Added sender ID
    private String contactName;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageView buttonSend;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;
    private ListenerRegistration messageListener;
    private ImageView imageViewProfile;
    private TextView textViewContactName;

    public static MessageFragment newInstance(String contactName, String receiverId, String senderId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECEIVER_ID, receiverId);
        args.putString(ARG_CONTACT_NAME, contactName);
        args.putString(ARG_SENDER_ID, senderId); // Pass sender ID
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Retrieve arguments
        if (getArguments() != null) {
            receiverId = getArguments().getString(ARG_RECEIVER_ID);
            senderId = getArguments().getString(ARG_SENDER_ID); // Get sender ID
            contactName = getArguments().getString(ARG_CONTACT_NAME);
        }

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        editTextMessage = view.findViewById(R.id.edit_text_message);
        buttonSend = view.findViewById(R.id.image_view_send);
        imageViewProfile = view.findViewById(R.id.image_view_contact_profile);
        textViewContactName = view.findViewById(R.id.text_view_contact_name);

        // Set contact name
        textViewContactName.setText(contactName);

        // Load contact's profile image
        loadContactProfileImage(receiverId);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load messages
        loadMessages();

        // Send button listener
        buttonSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void loadContactProfileImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("ProfileImages/" + userId + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(imageViewProfile.getContext())
                    .load(uri)
                    .placeholder(R.drawable.person) // Placeholder image
                    .into(imageViewProfile);
        }).addOnFailureListener(e -> {
            Log.e("MessageFragment", "Failed to load profile image: " + e.getMessage());
        });
    }

    private void loadMessages() {
        // Listen for messages sent to and from the current user
        messageListener = db.collection("Messages")
                .whereEqualTo("receiverId", senderId)
                .whereEqualTo("senderId", receiverId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("MessageFragment", "Error loading messages: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        List<Message> newMessages = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            newMessages.add(message);
                        }
                        updateMessageList(newMessages);
                    }
                });

        // Also listen for messages sent from the current user to the receiver
        messageListener = db.collection("Messages")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", receiverId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("MessageFragment", "Error loading messages: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        List<Message> newMessages = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            newMessages.add(message);
                        }
                        updateMessageList(newMessages);
                    }
                });
    }

    private void updateMessageList(List<Message> newMessages) {
        messageList.addAll(newMessages);
        Collections.sort(messageList, Comparator.comparingLong(Message::getTimestamp)); // Sort messages by timestamp
        messageAdapter.notifyDataSetChanged(); // Notify adapter of changes
        if (!messageList.isEmpty()) {
            recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the bottom (newest message)
        }
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(senderId, receiverId, messageText, System.currentTimeMillis());
            db.collection("Messages").add(message)
                    .addOnSuccessListener(aVoid -> {
                        editTextMessage.setText(""); // Clear the input
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MessageFragment", "Error sending message: " + e.getMessage());
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove(); // Remove the message listener
        }
    }
}
