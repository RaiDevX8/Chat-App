//package com.example.chatapp.fragments;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.SearchView;
//import com.example.chatapp.R;
//import com.example.chatapp.Adapters.ChatAdapter;
//import com.example.chatapp.models.Chat;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class ChatFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private ChatAdapter chatAdapter;
//    private List<Chat> chatList;
//    private List<Chat> filteredChatList;
//    private FirebaseFirestore firestore;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_chats, container, false);
//
//        // Initialize views
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // Initialize Firebase Firestore
//        firestore = FirebaseFirestore.getInstance();
//
//        // Initialize chat lists
//        chatList = new ArrayList<>();
//        filteredChatList = new ArrayList<>();
//
//        // Fetch latest chats from Firestore
//        fetchLatestChatsFromFirestore();
//
//        // Set up chat adapter
//        chatAdapter = new ChatAdapter(filteredChatList, getActivity(), chat -> {
//            String contactName = chat.getSender(); // Sender's name
//            String phoneNumber = chat.getPhoneNumber(); // Sender's phone number
//            String receiverId = chat.getReceiverId(); // Receiver's ID (target)
//            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Current user ID
//
//            // Create an instance of MessageFragment and pass the required arguments
//            MessageFragment messageFragment = MessageFragment.newInstance(contactName, receiverId, senderId);
//            navigateToMessageFragment(messageFragment);
//        });
//
//        recyclerView.setAdapter(chatAdapter);
//
//        // Set up search functionality
//        SearchView searchView = view.findViewById(R.id.search_view);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterChats(newText);
//                return true;
//            }
//        });
//
//        return view;
//    }
//
//    private void fetchLatestChatsFromFirestore() {
//        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        firestore.collection("Messages")
//                .whereEqualTo("receiverId", currentUserId)
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        Log.w("FirestoreError", "Listen failed.", error);
//                        return;
//                    }
//
//                    if (value != null) {
//                        HashMap<String, Chat> latestChatsMap = new HashMap<>();
//                        for (QueryDocumentSnapshot document : value) {
//                            String messageText = document.getString("messageText");
//                            String senderId = document.getString("senderId");
//                            String receiverId = document.getString("receiverId");
//                            Long timestamp = document.getLong("timestamp"); // Ensure this is Long
//
//                            if (messageText == null || senderId == null || receiverId == null || timestamp == null) {
//                                continue; // Skip this iteration if any required field is null
//                            }
//
//                            if (receiverId.equals(currentUserId)) {
//                                // Fetch sender's name from the "Users" collection
//                                firestore.collection("Users")
//                                        .document(senderId)
//                                        .get()
//                                        .addOnCompleteListener(userTask -> {
//                                            if (userTask.isSuccessful() && userTask.getResult() != null) {
//                                                String firstName = userTask.getResult().getString("firstName");
//                                                String lastName = userTask.getResult().getString("lastName");
//                                                String phoneNumber = userTask.getResult().getString("mobileNumber"); // Assuming mobileNumber is stored
//                                                String fullName = firstName + " " + lastName;
//
//                                                if (!latestChatsMap.containsKey(receiverId) || latestChatsMap.get(receiverId).getTimestamp() < timestamp) {
//                                                    Chat chat = new Chat(fullName, messageText, timestamp, R.drawable.person, receiverId, phoneNumber);
//                                                    latestChatsMap.put(receiverId, chat);
//                                                }
//
//                                                chatList.clear();
//                                                chatList.addAll(latestChatsMap.values());
//                                                filteredChatList.clear();
//                                                filteredChatList.addAll(chatList);
//                                                chatAdapter.notifyDataSetChanged();
//                                            } else {
//                                                Log.w("FirestoreError", "Error fetching user: ", userTask.getException());
//                                            }
//                                        });
//                            }
//                        }
//                    } else {
//                        Log.w("FirestoreError", "No documents found.");
//                    }
//                });
//    }
//
//    private void filterChats(String query) {
//        filteredChatList.clear();
//        if (query.isEmpty()) {
//            filteredChatList.addAll(chatList);
//        } else {
//            for (Chat chat : chatList) {
//                if (chat.getSender() != null && chat.getSender().toLowerCase().contains(query.toLowerCase())) {
//                    filteredChatList.add(chat);
//                }
//            }
//        }
//        chatAdapter.notifyDataSetChanged();
//    }
//
//    private void navigateToMessageFragment(MessageFragment messageFragment) {
//        if (getActivity() instanceof FragmentActivity) {
//            ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, messageFragment)
//                    .addToBackStack(null)
//                    .commitAllowingStateLoss();
//        }
//    }
//}
