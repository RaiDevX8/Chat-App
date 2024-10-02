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

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private Context context;

    public ChatAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false); // Inflate your chat item layout
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.senderTextView.setText(chat.getSender());
        holder.messageTextView.setText(chat.getMessage());
        holder.timestampTextView.setText(chat.getTimestamp());

        // Load profile picture or use default
        String profilePicUri = chat.getProfilePicUri();
        if (profilePicUri != null && !profilePicUri.isEmpty()) {
            holder.profileImageView.setImageURI(Uri.parse(profilePicUri));
        } else {
            holder.profileImageView.setImageResource(R.drawable.person);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, messageTextView, timestampTextView;
        ImageView profileImageView; // ImageView for the profile picture

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.sender);
            messageTextView = itemView.findViewById(R.id.message);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            profileImageView = itemView.findViewById(R.id.profile_image); // Adjust the ID as needed
        }
    }
}
