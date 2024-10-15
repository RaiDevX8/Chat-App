package com.example.chatapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.chatapp.R;
import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private List<Chat> filteredChatList;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize chat lists
        chatList = new ArrayList<>();
        filteredChatList = new ArrayList<>();

        // Fetch latest chats from Firestore
        fetchLatestChatsFromFirestore();

        // Set up chat adapter
        chatAdapter = new ChatAdapter(filteredChatList, getActivity(), chat -> {
            String contactName = chat.getSender();
            String phoneNumber = chat.getPhoneNumber(); // Assuming getPhoneNumber() is in your Chat model

            // Navigate to MessageFragment (uncomment when needed)
            // MessageFragment messageFragment = MessageFragment.newInstance(contactName, phoneNumber);
            // navigateToMessageFragment(messageFragment);
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

    private void fetchLatestChatsFromFirestore() {
        // Get the current user's ID (Assuming you're using Firebase Auth)
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("Messages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String, Chat> latestChatsMap = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String messageText = document.getString("messageText");
                            String senderId = document.getString("senderId");
                            String receiverId = document.getString("receiverId");
                            Long timestamp = document.getLong("timestamp"); // Ensure this is Long

                            // Check for null values
                            if (messageText == null || senderId == null || receiverId == null || timestamp == null) {
                                continue; // Skip this iteration if any required field is null
                            }

                            // Skip chats where the sender is the current user
                            if (senderId.equals(currentUserId)) {
                                continue; // Skip this chat
                            }

                            // Check if the receiver is the current user
                            if (receiverId.equals(currentUserId)) {
                                // Create a unique key for the receiver
                                String receiverKey = receiverId;

                                // Check if this is the latest message for this receiver
                                if (!latestChatsMap.containsKey(receiverKey) || latestChatsMap.get(receiverKey).getTimestamp() < timestamp) {
                                    // Create a Chat object and store it in the map
                                    Chat chat = new Chat(senderId, messageText, timestamp, R.drawable.person, receiverId);
                                    latestChatsMap.put(receiverKey, chat);
                                }
                            }
                        }

                        // Update chatList and filteredChatList with the latest chats
                        chatList.clear();
                        chatList.addAll(latestChatsMap.values());
                        filteredChatList.clear();
                        filteredChatList.addAll(chatList);
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        // Handle failure
                        Log.w("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private String createChatKey(String senderId, String receiverId) {
        // Create a unique key based on sender and receiver IDs
        return senderId.compareTo(receiverId) < 0 ? senderId + "_" + receiverId : receiverId + "_" + senderId;
    }

    private void filterChats(String query) {
        filteredChatList.clear();
        if (query.isEmpty()) {
            filteredChatList.addAll(chatList);
        } else {
            for (Chat chat : chatList) {
                if (chat.getSender() != null && chat.getSender().toLowerCase().contains(query.toLowerCase())) {
                    filteredChatList.add(chat);
                }
            }
        }
        chatAdapter.notifyDataSetChanged();
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
