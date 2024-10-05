package com.example.chatapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private List<Message> messageList;

    public MessageAdapter(List<Message> messages) {
        this.messageList = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.isSent() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageBody;
        TextView messageTime;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.text_message_body);
            messageTime = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageBody.setText(message.getBody());
            messageTime.setText(message.getTime()); // Show time for sent messages
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageBody;
        TextView messageSenderName;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.text_message_body);
            messageSenderName = itemView.findViewById(R.id.text_sender_name);
        }

        void bind(Message message) {
            messageBody.setText(message.getBody());
            messageSenderName.setText(message.getSenderName()); // Show sender's name for received messages
        }
    }
}
