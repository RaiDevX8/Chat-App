package com.example.chatapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapp.Adapters.ChatListAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.ChatItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private FirebaseFirestore db;
    private String currentUserId;
    private List<ChatItem> chatList;
    private ChatListAdapter chatListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList, requireActivity(), new ChatListAdapter.OnChatClickListener() {
            @Override
            public void onChatClick(ChatItem chatItem) {
                onChatItemClick(chatItem);  // Handle normal click
            }

            @Override
            public void onChatLongClick(ChatItem chatItem, int position) {
                onChatItemLongClick(chatItem, position);  // Handle long click for delete
            }
        });
        chatRecyclerView.setAdapter(chatListAdapter);

        fetchRecentConversations();

        return view;
    }

    // Handle chat item click
    private void onChatItemClick(ChatItem chatItem) {
        Fragment messageFragment = MessageFragment.newInstance(
                chatItem.getContactName(),
                chatItem.getReceiverId(),
                currentUserId
        );

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, messageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Handle chat item long click for deletion
    private void onChatItemLongClick(ChatItem chatItem, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Chat")
                .setMessage("Are you sure you want to delete this chat?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Call the method to delete the chat
                    deleteChat(chatItem.getReceiverId(), position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to delete a chat
    private void deleteChat(String chatPartnerId, int position) {
        CollectionReference messagesRef = db.collection("Messages");

        // Delete all messages where the current user is either the sender or receiver
        messagesRef
                .whereEqualTo("senderId", currentUserId)
                .whereEqualTo("receiverId", chatPartnerId)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        messagesRef.document(document.getId()).delete();
                    }

                    messagesRef
                            .whereEqualTo("senderId", chatPartnerId)
                            .whereEqualTo("receiverId", currentUserId)
                            .get().addOnSuccessListener(receivedMessages -> {
                                for (QueryDocumentSnapshot doc : receivedMessages) {
                                    messagesRef.document(doc.getId()).delete();
                                }

                                // Remove the chat item from the list and update the adapter
                                chatList.remove(position);
                                chatListAdapter.notifyItemRemoved(position);
                            });
                });
    }

    private void fetchRecentConversations() {
        CollectionReference messagesRef = db.collection("Messages");

        messagesRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatList.clear();
                        HashMap<String, ChatItem> latestChatMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String messageText = document.getString("messageText");
                            String receiverId = document.getString("receiverId");
                            String senderId = document.getString("senderId");
                            long timestamp = document.getLong("timestamp");

                            if (currentUserId.equals(senderId) || currentUserId.equals(receiverId)) {
                                String chatPartnerId = currentUserId.equals(senderId) ? receiverId : senderId;

                                db.collection("Users").document(chatPartnerId).get().addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String firstName = userDoc.getString("firstName");
                                        String lastName = userDoc.getString("lastName");
                                        String chatPartnerName = firstName + " " + lastName;
                                        String chatPartnerProfilePic = userDoc.getString("profilePic");

                                        ChatItem chatItem = new ChatItem(chatPartnerName, chatPartnerProfilePic, messageText, timestamp, 0);
                                        chatItem.setReceiverId(chatPartnerId);

                                        if (!latestChatMap.containsKey(chatPartnerId)) {
                                            latestChatMap.put(chatPartnerId, chatItem);
                                        }
                                    }

                                    chatList.clear();
                                    chatList.addAll(latestChatMap.values());
                                    chatListAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                    }
                });
    }
}
