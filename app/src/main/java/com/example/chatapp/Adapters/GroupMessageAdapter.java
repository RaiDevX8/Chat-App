package com.example.chatapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.models.GroupMessageModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MessageViewHolder> {

    private List<GroupMessageModel> messageList;
    private String currentUserId;

    public GroupMessageAdapter(List<GroupMessageModel> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        GroupMessageModel message = messageList.get(position);
        String messageText = message.getMessageText();
        String senderId = message.getSenderId();

        // Load sender profile image
        loadProfileImage(holder.profileImageViewSender, senderId); // Load sender image

        // Check if the sender is the current user
        if (senderId.equals(currentUserId)) {
            // Show sender message layout
            holder.messageContainerSender.setVisibility(View.VISIBLE);
            holder.messageContainerReceiver.setVisibility(View.GONE);
            holder.messageTextViewSender.setText(messageText); // Set text for sender
        } else {
            // Show receiver message layout
            holder.messageContainerSender.setVisibility(View.GONE);
            holder.messageContainerReceiver.setVisibility(View.VISIBLE);
            holder.messageTextViewReceiver.setText(messageText); // Set text for receiver
            loadProfileImage(holder.profileImageViewReceiver, senderId); // Load receiver image
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageViewSender, profileImageViewReceiver;
        View messageContainerSender, messageContainerReceiver;
        TextView messageTextViewSender, messageTextViewReceiver;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageViewSender = itemView.findViewById(R.id.image_view_profile_sender);
            profileImageViewReceiver = itemView.findViewById(R.id.image_view_profile_receiver);
            messageContainerSender = itemView.findViewById(R.id.message_container_sender);
            messageContainerReceiver = itemView.findViewById(R.id.message_container_receiver);
            messageTextViewSender = itemView.findViewById(R.id.text_view_message_sender);
            messageTextViewReceiver = itemView.findViewById(R.id.text_view_message_receiver);
        }
    }

    // Method to load profile image from Firebase Storage
    private void loadProfileImage(ImageView imageView, String userId) {
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages/" + userId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.person) // Default image while loading
                    .circleCrop()
                    .error(R.drawable.person) // Error image if loading fails
                    .into(imageView);
        }).addOnFailureListener(e -> {
            // Handle the failure
            imageView.setImageResource(R.drawable.person); // Set a default image
        });
    }
}
