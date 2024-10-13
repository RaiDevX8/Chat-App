package com.example.chatapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;
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

    private String receiverId;
    private String contactName;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistrationSender;
    private ListenerRegistration listenerRegistrationReceiver;
    private ImageView imageViewProfile;
    private TextView textViewContactName;

    public static MessageFragment newInstance(String contactName, String receiverId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECEIVER_ID, receiverId);
        args.putString(ARG_CONTACT_NAME, contactName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        if (getArguments() != null) {
            receiverId = getArguments().getString(ARG_RECEIVER_ID);
            contactName = getArguments().getString(ARG_CONTACT_NAME);
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        editTextMessage = view.findViewById(R.id.edit_text_message);
        buttonSend = view.findViewById(R.id.button_send);
        imageViewProfile = view.findViewById(R.id.image_view_contact_profile); // Add this line
        textViewContactName = view.findViewById(R.id.text_view_contact_name); // Add this line

        // Set contact name
        textViewContactName.setText(contactName);

        // Load contact's profile image
        loadContactProfileImage(receiverId);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        db = FirebaseFirestore.getInstance();
        loadMessages(); // Load messages in real-time

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
            // Handle the error
            Log.e("MessageFragment", "Failed to load profile image: " + e.getMessage());
        });
    }

    private void loadMessages() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        // Listen for messages sent by the current user to the receiver
        listenerRegistrationSender = db.collection("Messages")
                .whereEqualTo("senderId", currentUserId)
                .whereEqualTo("receiverId", receiverId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        List<Message> newMessages = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            newMessages.add(message);
                        }
                        messageList.addAll(newMessages); // Add new messages to the list
                        sortAndNotify();
                    }
                });

        // Listen for messages sent by the receiver to the current user
        listenerRegistrationReceiver = db.collection("Messages")
                .whereEqualTo("senderId", receiverId)
                .whereEqualTo("receiverId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        List<Message> newMessages = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            newMessages.add(message);
                        }
                        messageList.addAll(newMessages); // Add new messages to the list
                        sortAndNotify();
                    }
                });
    }

    private void sortAndNotify() {
        // Sort messages by timestamp (ascending order)
        Collections.sort(messageList, Comparator.comparingLong(Message::getTimestamp));
        messageAdapter.notifyDataSetChanged(); // Notify adapter of changes
        recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the bottom (newest message)
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID
            Message message = new Message(currentUserId, receiverId, messageText, System.currentTimeMillis());
            db.collection("Messages").add(message).addOnSuccessListener(aVoid -> {
                editTextMessage.setText(""); // Clear the input
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistrationSender != null) {
            listenerRegistrationSender.remove(); // Remove the sender listener
        }
        if (listenerRegistrationReceiver != null) {
            listenerRegistrationReceiver.remove(); // Remove the receiver listener
        }
    }
}
