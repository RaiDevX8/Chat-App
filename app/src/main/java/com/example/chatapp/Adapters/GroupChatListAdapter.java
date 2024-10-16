package com.example.chatapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.models.GroupChat;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.ViewHolder> {

    private Context context;
    private List<GroupChat> groupChatList;

    public GroupChatListAdapter(Context context, List<GroupChat> groupChatList) {
        this.context = context;
        this.groupChatList = groupChatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupChat groupChat = groupChatList.get(position);
        holder.groupNameTextView.setText(groupChat.getGroupName());
        holder.groupDescriptionTextView.setText(groupChat.getGroupDescription());

        // Load the group profile image
        loadGroupProfileImage(groupChat.getGroupId(), holder.groupImageView);
    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    // Method to load the group profile image from Firebase Storage
    private void loadGroupProfileImage(String groupId, ImageView imageViewGroup) {
        // Load the default image initially
        Glide.with(imageViewGroup.getContext())
                .load(R.drawable.person) // Default image
                .circleCrop()
                .into(imageViewGroup);

        // Fetch the actual image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("GroupProfileImages/" + groupId + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Image found, update the ImageView with the correct image
            Glide.with(imageViewGroup.getContext())
                    .load(uri)
                    .circleCrop()
                    .into(imageViewGroup);
        }).addOnFailureListener(e -> {
            // Failed to load the image, keep the default and log the error
            Log.e("GroupChatListAdapter", "Failed to load group profile image: " + e.getMessage());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView;
        TextView groupDescriptionTextView;
        ImageView groupImageView;

        ViewHolder(View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupName);
            groupDescriptionTextView = itemView.findViewById(R.id.groupDescription);
            groupImageView = itemView.findViewById(R.id.groupImage);
        }
    }
}
