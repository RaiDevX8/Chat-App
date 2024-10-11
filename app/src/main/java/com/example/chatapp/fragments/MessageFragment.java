package com.example.chatapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageFragment extends Fragment {
    private static final String ARG_CONTACT_NAME = "contact_name";
    private static final String ARG_PHONE_NUMBER = "phone_number";

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText inputMessage;
    private ImageButton sendButton;

    // Static method to create a new instance with arguments
    public static MessageFragment newInstance(String contactName, String phoneNumber) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_NAME, contactName);
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        inputMessage = view.findViewById(R.id.input_message);
        sendButton = view.findViewById(R.id.send_button);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        // Retrieve the contact name and phone number from arguments and handle null values
        if (getArguments() != null) {
            String contactName = getArguments().getString(ARG_CONTACT_NAME, "Unknown");
            String phoneNumber = getArguments().getString(ARG_PHONE_NUMBER, "Unknown");

            TextView contactNameTextView = view.findViewById(R.id.contact_name);
            TextView phoneNumberTextView = view.findViewById(R.id.phone_number);

            contactNameTextView.setText(contactName);
            phoneNumberTextView.setText(phoneNumber);
        }

        // Set up send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = inputMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);  // Send the message
                    inputMessage.setText("");  // Clear input field
                }
            }
        });

        return view;
    }

    // Method to send a message
    private void sendMessage(String messageText) {
        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Create a new Message object for the sent message
        Message sentMessage = new Message(messageText, currentTime, true, "You", R.drawable.person);

        // Add sent message to the list and notify the adapter
        messageList.add(sentMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Simulate a received message after sending
        receiveMessage("Okay, got your message!");

        // Scroll to the latest message
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    // Simulate receiving a message
    private void receiveMessage(String messageText) {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Create a new Message object for the received message
        Message receivedMessage = new Message(messageText, currentTime, false, "John", R.drawable.person);

        // Add received message to the list and notify the adapter
        messageList.add(receivedMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Scroll to the latest message
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}
