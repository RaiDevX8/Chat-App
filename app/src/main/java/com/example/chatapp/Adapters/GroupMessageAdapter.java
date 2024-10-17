package com.example.chatapp.Adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.GroupMessageModel;

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

        // Extract timestamp in milliseconds
        long timestampMillis = message.getTimestamp().toDate().getTime();

        // Format the timestamp
        String formattedTimestamp = formatTimestamp(timestampMillis);

        if (messageText != null && !messageText.isEmpty()) {
            if (message.getSenderId().equals(currentUserId)) {
                // Set sender message
                holder.messageContainerSender.setVisibility(View.VISIBLE);
                holder.messageContainerReceiver.setVisibility(View.GONE);
                holder.messageTextViewSender.setText(messageText); // Set text for sender
                holder.timestampTextViewSender.setText(formattedTimestamp); // Set timestamp
            } else {
                // Set receiver message
                holder.messageContainerSender.setVisibility(View.GONE);
                holder.messageContainerReceiver.setVisibility(View.VISIBLE);
                holder.messageTextViewReceiver.setText(messageText); // Set text for receiver
                holder.timestampTextViewReceiver.setText(formattedTimestamp); // Set timestamp
            }
        } else {
            holder.messageTextViewSender.setText("No message"); // Placeholder for debugging
            holder.messageTextViewReceiver.setText("No message"); // Placeholder for debugging
        }
    }

    private String formatTimestamp(long timestamp) {
        // Format the timestamp to a readable date/time string
        return DateFormat.format("hh:mm a", timestamp).toString(); // Format as needed
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextViewSender;
        TextView messageTextViewReceiver;
        TextView timestampTextViewSender;
        TextView timestampTextViewReceiver;
        LinearLayout messageContainerSender;
        LinearLayout messageContainerReceiver;
        ImageView profileImageViewSender; // Optional, if you have a profile image for sender
        ImageView profileImageViewReceiver; // Optional, if you have a profile image for receiver

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextViewSender = itemView.findViewById(R.id.text_view_message_sender);
            messageTextViewReceiver = itemView.findViewById(R.id.text_view_message_receiver);
            timestampTextViewSender = itemView.findViewById(R.id.text_view_timestamp_sender);
            timestampTextViewReceiver = itemView.findViewById(R.id.text_view_timestamp_receiver);
            messageContainerSender = itemView.findViewById(R.id.message_container_sender);  // Reference the sender message container
            messageContainerReceiver = itemView.findViewById(R.id.message_container_receiver);  // Reference the receiver message container
            profileImageViewSender = itemView.findViewById(R.id.image_view_profile_sender); // Reference sender profile image
            profileImageViewReceiver = itemView.findViewById(R.id.image_view_profile_receiver); // Reference receiver profile image
        }
    }
}
