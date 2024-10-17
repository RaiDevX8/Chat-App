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
import com.example.chatapp.activity.CreateGroupActivity;
import com.example.chatapp.models.GroupChat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroupChatListAdapter groupChatListAdapter;
    private List<GroupChat> groupChatList;
    private FirebaseFirestore db;
    private FloatingActionButton fabNewGroup;
    private String currentUserId; // Store current user ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabNewGroup = view.findViewById(R.id.fabNewGroup);
        fabNewGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        groupChatList = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID

        // Fetch group chats from Firestore
        fetchGroupChats();

        return view;
    }

    private void fetchGroupChats() {
        db.collection("Groups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        groupChatList.clear(); // Clear the list to avoid duplication
                        for (QueryDocumentSnapshot groupDoc : task.getResult()) {
                            String groupId = groupDoc.getId();
                            String groupName = groupDoc.getString("groupName");
                            String groupDescription = groupDoc.getString("groupDescription");
                            List<String> members = (List<String>) groupDoc.get("members");

                            // Check if the current user is a member
                            if (members.contains(currentUserId)) {
                                GroupChat groupChat = new GroupChat(groupId, groupName, groupDescription, members);
                                fetchMembersUserIds(members, groupChat);
                            }
                        }
                    } else {
                        Log.d("GroupChatFragment", "Error getting groups: ", task.getException());
                    }
                });
    }

    private void fetchMembersUserIds(List<String> members, GroupChat groupChat) {
        List<String> memberDetailsList = new ArrayList<>();

        // Loop through the members and fetch userId or mobileNumber
        for (String member : members) {
            db.collection("Users")
                    .whereEqualTo("mobileNumber", member)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot userDoc : task.getResult()) {
                                String userId = userDoc.getString("userId");
                                memberDetailsList.add(userId); // Add userId to the member list
                            }
                        } else {
                            db.collection("Users")
                                    .whereEqualTo("userId", member)
                                    .get()
                                    .addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot userDoc : userTask.getResult()) {
                                                String userId = userDoc.getString("userId");
                                                memberDetailsList.add(userId); // Add userId to the member list
                                            }
                                        }

                                        // Once all members are fetched, update the group
                                        if (!memberDetailsList.isEmpty()) {
                                            groupChat.setMembers(memberDetailsList);
                                        }

                                        // Add groupChat to list and update RecyclerView only once
                                        if (!groupChatList.contains(groupChat)) {
                                            groupChatList.add(groupChat);
                                        }

                                        // Update RecyclerView adapter here after fetching all members
                                        groupChatListAdapter = new GroupChatListAdapter(getContext(), groupChatList);
                                        recyclerView.setAdapter(groupChatListAdapter);
                                    });
                        }
                    });
        }
    }
}
