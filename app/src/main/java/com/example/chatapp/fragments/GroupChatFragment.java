package com.example.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.GroupChatListAdapter;
import com.example.chatapp.R;

import com.example.chatapp.activity.CreateGroupActivity; // Use this for starting an activity
import com.example.chatapp.models.GroupChat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroupChatListAdapter groupChatListAdapter;
    private List<GroupChat> groupChatList;
    private FirebaseFirestore db;
    private FloatingActionButton fabNewGroup; // Declare here

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabNewGroup = view.findViewById(R.id.fabNewGroup); // Initialize after inflating view
        fabNewGroup.setOnClickListener(v -> {
            // Handle the create new group action, like opening a new activity
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class); // CreateGroupActivity is an activity
            startActivity(intent);
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        groupChatList = new ArrayList<>();

        // Fetch group chats from Firestore
        fetchGroupChats();

        return view;
    }

    private void fetchGroupChats() {
        db.collection("Groups") // Replace with your actual collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String groupId = document.getId();
                            String groupName = document.getString("groupName");
                            String groupDescription = document.getString("groupDescription");

                            // Create a new GroupChat object
                            GroupChat groupChat = new GroupChat(groupId, groupName, groupDescription);
                            groupChatList.add(groupChat);
                        }
                        // Update RecyclerView adapter
                        groupChatListAdapter = new GroupChatListAdapter(getContext(), groupChatList);
                        recyclerView.setAdapter(groupChatListAdapter);
                    } else {
                        Log.d("GroupChatFragment", "Error getting documents: ", task.getException());
                    }
                });
    }
}
