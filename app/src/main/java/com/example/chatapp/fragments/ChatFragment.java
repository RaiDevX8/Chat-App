package com.example.chatapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chatapp.R;
import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        List<Chat> chatList = getDummyChatData(); // Replace with actual chat data retrieval
        ChatAdapter chatAdapter = new ChatAdapter(chatList, getActivity(), chat -> {
            // Pass the actual contact name from the clicked chat item
            String contactName = chat.getSender(); // Assuming getSender() returns the contact name
            MessageFragment messageFragment = MessageFragment.newInstance(contactName);

            if (getActivity() instanceof FragmentActivity) {
                ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, messageFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        recyclerView.setAdapter(chatAdapter);
    }

    private List<Chat> getDummyChatData() {
        List<Chat> dummyChats = new ArrayList<>();
        dummyChats.add(new Chat("Alice", "Hey, how's it going?", "10:00 AM", R.drawable.person));
        dummyChats.add(new Chat("Bob", "Did you finish the report?", "10:15 AM", R.drawable.person));
        return dummyChats;
    }

    private void navigateToMessageFragment(String sender) {
        MessageFragment messageFragment = MessageFragment.newInstance(sender);
        if (getActivity() instanceof FragmentActivity) {
            ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, messageFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss(); // Prevent state loss
        }
    }
}
