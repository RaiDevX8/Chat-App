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
import android.widget.SearchView;
import com.example.chatapp.R;
import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private List<Chat> filteredChatList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize chat lists
        chatList = getDummyChatData(); // Replace with actual chat data retrieval
        filteredChatList = new ArrayList<>(chatList);

        // Update chat adapter to pass both contact name and phone number
        chatAdapter = new ChatAdapter(filteredChatList, getActivity(), chat -> {
            String contactName = chat.getSender();
            String phoneNumber = chat.getPhoneNumber(); // Assuming getPhoneNumber() is in your Chat model
            MessageFragment messageFragment = MessageFragment.newInstance(contactName, phoneNumber);
            navigateToMessageFragment(messageFragment);
        });

        recyclerView.setAdapter(chatAdapter);

        // Set up search functionality
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterChats(newText);
                return true;
            }
        });

        return view;
    }

    private void filterChats(String query) {
        filteredChatList.clear();
        if (query.isEmpty()) {
            filteredChatList.addAll(chatList);
        } else {
            for (Chat chat : chatList) {
                if (chat.getSender().toLowerCase().contains(query.toLowerCase())) {
                    filteredChatList.add(chat);
                }
            }
        }
        chatAdapter.notifyDataSetChanged();
    }

    private List<Chat> getDummyChatData() {
        List<Chat> dummyChats = new ArrayList<>();
        dummyChats.add(new Chat("Alice", "Hey, how's it going?", "10:00 AM", R.drawable.person, "1234567890"));
        dummyChats.add(new Chat("Bob", "Did you finish the report?", "10:15 AM", R.drawable.person, "9876543210"));
        // Add more dummy chat data here if needed
        return dummyChats;
    }

    private void navigateToMessageFragment(MessageFragment messageFragment) {
        if (getActivity() instanceof FragmentActivity) {
            ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, messageFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss(); // Prevent state loss
        }
    }
}
