package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.models.Chat;

import java.util.List;

public class ChatsListAdapter extends ArrayAdapter<Chat> {
    private final Context context;
    private final List<Chat> chatList;

    public ChatsListAdapter(Context context, List<Chat> chatList) {
        super(context, R.layout.item_chat, chatList);
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_chat, parent, false);
        }

        Chat chat = chatList.get(position);

        TextView senderTextView = convertView.findViewById(R.id.sender);
        TextView messageTextView = convertView.findViewById(R.id.message);
        TextView timestampTextView = convertView.findViewById(R.id.timestamp);

        senderTextView.setText(chat.getSender());
        messageTextView.setText(chat.getMessage());
        timestampTextView.setText(chat.getTimestamp());

        return convertView;
    }
}
