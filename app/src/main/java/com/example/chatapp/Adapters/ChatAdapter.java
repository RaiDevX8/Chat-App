package com.example.chatapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.fragments.MessageFragment;
import com.example.chatapp.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(Chat chat);  // Listener interface for clicks
    }

    private List<Chat> chatList;
    private Context context;
    private OnChatClickListener listener;

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
        holder.timestampTextView.setText(chat.getTimestamp());

        // Load profile picture or use default
        String profilePicUri = chat.getProfilePicUri();
        if (profilePicUri != null && !profilePicUri.isEmpty()) {
            holder.profileImageView.setImageURI(Uri.parse(profilePicUri));
        } else {
            holder.profileImageView.setImageResource(R.drawable.person);
        }

        // Set an onClickListener for the item
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat)); // Pass the chat object to the listener
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

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
}
