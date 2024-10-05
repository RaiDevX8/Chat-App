package com.example.chatapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
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

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText inputMessage;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_message_activity);

        // Initialize views
        recyclerView = findViewById(R.id.chat_recycler_view);
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Set up send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = inputMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);  // User sends a message
                    inputMessage.setText("");  // Clear the input field
                }
            }
        });
    }

    // Method to add a sent message
    private void sendMessage(String messageText) {
        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Create a new Message object for the sent message
        Message sentMessage = new Message(messageText, currentTime, true, "You", R.drawable.person);

        // Add the sent message to the list and notify the adapter
        messageList.add(sentMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Simulate a received message after sending
        receiveMessage("Okay, got your message!");

        // Scroll to the latest message
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    // Method to simulate receiving a message
    private void receiveMessage(String messageText) {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        // Create a new Message object for the received message
        Message receivedMessage = new Message(messageText, currentTime, false, "John", R.drawable.person);

        // Add the received message to the list and notify the adapter
        messageList.add(receivedMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Scroll to the latest message
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}
