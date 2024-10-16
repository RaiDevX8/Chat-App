package com.example.chatapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.models.ChatItem;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;
    private Context context;
    private OnChatClickListener onChatClickListener;

    // Interface for handling click events
    public interface OnChatClickListener {
        void onChatClick(ChatItem chatItem);
        void onChatLongClick(ChatItem chatItem, int position); // Added for long-click (delete functionality)
    }

    // Constructor
    public ChatListAdapter(List<ChatItem> chatList, Context context, OnChatClickListener listener) {
        this.chatList = chatList;
        this.context = context;
        this.onChatClickListener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chatItem = chatList.get(position);

        holder.contactName.setText(chatItem.getContactName());
        holder.lastMessage.setText(chatItem.getLastMessage());
        holder.messageTime.setText(formatTimestamp(chatItem.getTimestamp()));

        // Load the profile image using receiverId
        loadProfileImage(chatItem.getReceiverId(), holder.profileImageView);

        holder.itemView.setOnClickListener(v -> {
            // Notify the fragment that a chat was clicked
            onChatClickListener.onChatClick(chatItem);
        });

        // Set long click listener for deleting a chat
        holder.itemView.setOnLongClickListener(v -> {
            onChatClickListener.onChatLongClick(chatItem, position);
            return true; // Indicate that the long-click event was handled
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView contactName, lastMessage, messageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            contactName = itemView.findViewById(R.id.contactName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    // Method to load profile image using Firebase Storage
    private void loadProfileImage(String userId, ImageView imageViewProfile) {
        // Load the default image initially
        Glide.with(imageViewProfile.getContext())
                .load(R.drawable.person) // Default image
                .circleCrop() // Make it circular
                .into(imageViewProfile);

        // Now try to fetch the correct image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("ProfileImages/" + userId + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Image found, update the ImageView with the correct image
            Glide.with(imageViewProfile.getContext())
                    .load(uri) // Load the correct image
                    .circleCrop()
                    .into(imageViewProfile);
        }).addOnFailureListener(e -> {
            // If the image is not found, do nothing, keep the default image
            Log.e("ChatListAdapter", "Failed to load profile image: " + e.getMessage());
        });
    }
}
