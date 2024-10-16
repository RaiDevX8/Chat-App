//package com.example.chatapp.Adapters;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.chatapp.R;
//import com.example.chatapp.models.Chat;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
//
//    // Interface for handling chat item click events
//    public interface OnChatClickListener {
//        void onChatClick(Chat chat);
//    }
//
//    private List<Chat> chatList;
//    private Context context;
//    private OnChatClickListener listener;
//    private FirebaseStorage firebaseStorage;
//
//    // Constructor
//    public ChatAdapter(List<Chat> chatList, Context context, OnChatClickListener listener) {
//        this.chatList = chatList;
//        this.context = context;
//        this.listener = listener;
//        this.firebaseStorage = FirebaseStorage.getInstance();  // Initialize Firebase Storage
//    }
//
//    @NonNull
//    @Override
//    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // Inflate the chat item layout
//        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
//        return new ChatViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
//        Chat chat = chatList.get(position);
//        holder.senderTextView.setText(chat.getSender());
//        holder.messageTextView.setText(chat.getMessage());
//
//        // Format and set timestamp
//        if (chat.getTimestamp() != null) {
//            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
//                    .format(new Date(chat.getTimestamp()));
//            holder.timestampTextView.setText(formattedDate);
//        } else {
//            holder.timestampTextView.setText("");  // Clear if no timestamp
//        }
//
//        // Load profile picture using the utility method
//        loadContactProfileImage(chat.getReceiverId(), holder.profilePicImageView);
//
//        // Set click listener for chat item
//        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
//    }
//
//    private void loadContactProfileImage(String userId, ImageView imageViewProfile) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference().child("ProfileImages/" + userId + ".jpg");
//
//        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            Glide.with(imageViewProfile.getContext())
//                    .load(uri)
//                    .placeholder(R.drawable.person) // Placeholder image
//                    .into(imageViewProfile);
//        }).addOnFailureListener(e -> {
//            // Handle the error
//            Log.e("ChatAdapter", "Failed to load profile image: " + e.getMessage());
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatList.size();
//    }
//
//    // ViewHolder class for chat items
//    static class ChatViewHolder extends RecyclerView.ViewHolder {
//        TextView senderTextView;
//        TextView messageTextView;
//        TextView timestampTextView;
//        ImageView profilePicImageView;
//
//        ChatViewHolder(View itemView) {
//            super(itemView);
//            senderTextView = itemView.findViewById(R.id.sender);
//            messageTextView = itemView.findViewById(R.id.message);
//            timestampTextView = itemView.findViewById(R.id.timestamp);
//            profilePicImageView = itemView.findViewById(R.id.profile_image);
//        }
//    }
//}
