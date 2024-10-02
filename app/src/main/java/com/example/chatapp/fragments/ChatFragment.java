package com.example.chatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.models.Chat;
import com.example.chatapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the RecyclerView with dummy chat data
        setupRecyclerView();

        return view;
    }
    private void setupRecyclerView() {
        List<Chat> chatList = getDummyChatData(); // Get the dummy chat data
        ChatAdapter chatAdapter = new ChatAdapter(chatList, getContext()); // Pass context to the adapter
        recyclerView.setAdapter(chatAdapter);
    }


    private List<Chat> getDummyChatData() {
        List<Chat> dummyChats = new ArrayList<>();

        // Adding dummy chat messages with drawable resource IDs for profile pictures
        dummyChats.add(new Chat("Alice", "Hey, how's it going?", "10:00 AM", R.drawable.person));
        dummyChats.add(new Chat("Bob", "Did you finish the report?", "10:15 AM", R.drawable.person));
        dummyChats.add(new Chat("Charlie", "Let's meet for lunch!", "10:30 AM", R.drawable.person));
        dummyChats.add(new Chat("Diana", "Can you send me the files?", "10:45 AM", R.drawable.person));
        dummyChats.add(new Chat("Ethan", "Looking forward to the weekend!", "11:00 AM", R.drawable.person));
        dummyChats.add(new Chat("Fiona", "Great job on the presentation!", "11:15 AM", R.drawable.person));
        dummyChats.add(new Chat("George", "What's your plan for today?", "11:30 AM", R.drawable.person));
        dummyChats.add(new Chat("Hannah", "Reminder: Meeting at 3 PM", "11:45 AM", R.drawable.person));

        return dummyChats;
    }
}
