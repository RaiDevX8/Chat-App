package com.example.chatapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // Interface to handle chat item click events
    public interface OnChatClickListener {
        void onChatClick(Chat chat);  // Listener method for clicks
    }

    private List<Chat> chatList;
    private Context context;
    private OnChatClickListener listener;

    // Constructor for adapter
    public ChatAdapter(List<Chat> chatList, Context context, OnChatClickListener listener) {
        this.chatList = chatList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.senderTextView.setText(chat.getSender());
        holder.messageTextView.setText(chat.getMessage());

        // Format timestamp to a readable string
        if (chat.getTimestamp() != null) {
            // Convert Long timestamp to formatted String
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date(chat.getTimestamp()));
            holder.timestampTextView.setText(formattedDate);
        } else {
            holder.timestampTextView.setText(""); // Handle null timestamp if needed
        }

        // Load profile picture or use default image
        String profilePicUri = chat.getProfilePicUri();
        if (profilePicUri != null && !profilePicUri.isEmpty()) {
            holder.profileImageView.setImageURI(Uri.parse(profilePicUri));
        } else {
            holder.profileImageView.setImageResource(R.drawable.person); // Default profile picture
        }

        // Set item click listener
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // ViewHolder class for RecyclerView items
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, messageTextView, timestampTextView;
        ImageView profileImageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.sender);
            messageTextView = itemView.findViewById(R.id.message);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            profileImageView = itemView.findViewById(R.id.profile_image);
        }
    }

    // Method to update the chat list after filtering
    public void updateList(List<Chat> newList) {
        chatList = newList;
        notifyDataSetChanged();
    }
}
