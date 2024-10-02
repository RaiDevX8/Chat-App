package com.example.chatapp.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.Contact;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contactList;

    public ContactsAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.contactName.setText(contact.getName());
        holder.messagePreview.setText(contact.getLastMessage()); // Update to get last message
        holder.messageTimestamp.setText(contact.getTimestamp());

        // Check if photoResource is valid and use it, otherwise use photoUri
        if (contact.getPhotoResource() != 0) {
            holder.contactPhoto.setImageResource(contact.getPhotoResource()); // Use resource ID for the photo
        } else if (contact.getPhotoUri() != null) {
            holder.contactPhoto.setImageURI(Uri.parse(contact.getPhotoUri())); // Use URI for the photo
        } else {
            // Set a placeholder or default image if no photo is available
            holder.contactPhoto.setImageResource(R.drawable.person); // Use your default image here
        }

        // Show unread message count badge if there are unread messages
        int unreadCount = contact.getUnreadCount();
        if (unreadCount > 0) {
            holder.unreadCountBadge.setVisibility(View.VISIBLE);
            holder.unreadCountBadge.setText(String.valueOf(unreadCount));
        } else {
            holder.unreadCountBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView messagePreview; // TextView for message preview
        TextView messageTimestamp; // TextView for timestamp
        TextView unreadCountBadge;
        ImageView contactPhoto;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            messagePreview = itemView.findViewById(R.id.message_preview); // Initialize message preview
            messageTimestamp = itemView.findViewById(R.id.message_timestamp); // Initialize timestamp
            unreadCountBadge = itemView.findViewById(R.id.unread_count_badge);
            contactPhoto = itemView.findViewById(R.id.contact_photo);
        }
    }
}
