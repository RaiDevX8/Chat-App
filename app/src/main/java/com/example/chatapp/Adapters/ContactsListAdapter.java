package com.example.chatapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.models.Contact;

import java.util.List;

public class ContactsListAdapter extends BaseAdapter {

    private List<Contact> contactList;
    private LayoutInflater inflater;

    public ContactsListAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder();
            holder.contactName = convertView.findViewById(R.id.contact_name);
            holder.contactNumber = convertView.findViewById(R.id.contact_number);
            holder.contactPhoto = convertView.findViewById(R.id.contact_photo);
            holder.unreadCountBadge = convertView.findViewById(R.id.unread_count_badge); // Add badge
            holder.contactItemLayout = convertView.findViewById(R.id.contact_item_layout); // Layout for background

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactNumber.setText(contact.getPhoneNumber()); // Set phone number

        // Load the contact photo if available
        String photoUriString = contact.getPhotoUri(); // Get photo URI as a String
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString); // Convert String to Uri
            holder.contactPhoto.setImageURI(photoUri); // Set the photo URI
        } else {
            holder.contactPhoto.setImageResource(contact.getPhotoResource()); // Use resource ID if URI is null
        }

        // Show unread message count badge if there are unread messages
        int unreadCount = contact.getUnreadCount(); // Get unread count
        if (unreadCount > 0) {
            holder.unreadCountBadge.setVisibility(View.VISIBLE);
            holder.unreadCountBadge.setText(String.valueOf(unreadCount));
        } else {
            holder.unreadCountBadge.setVisibility(View.GONE);
        }

        // Set a light gray background for each contact item
        holder.contactItemLayout.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.light_gray)); // Replace with actual color resource

        return convertView;
    }

    static class ViewHolder {
        TextView contactName;
        TextView contactNumber;
        ImageView contactPhoto;
        TextView unreadCountBadge; // Add unread count badge
        RelativeLayout contactItemLayout; // Layout for setting background
    }

    public void updateContacts(List<Contact> updatedContactList) {
        this.contactList = updatedContactList;
        notifyDataSetChanged();
    }
}
