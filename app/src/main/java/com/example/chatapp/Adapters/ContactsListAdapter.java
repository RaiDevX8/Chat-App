package com.example.chatapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.models.Contact;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ContactsListAdapter extends BaseAdapter {

    private List<Contact> contactList;
    private LayoutInflater inflater;
    private FirebaseStorage storage;

    public ContactsListAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.inflater = LayoutInflater.from(context);
        this.storage = FirebaseStorage.getInstance();
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
            holder.unreadCountBadge = convertView.findViewById(R.id.unread_count_badge);
            holder.contactItemLayout = convertView.findViewById(R.id.contact_item_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactNumber.setText(contact.getPhoneNumber());

        String userId = contact.getUserId();
        if (userId != null) {
            loadProfileImage(holder.contactPhoto, userId);
        } else {
            holder.contactPhoto.setImageResource(R.drawable.person); // Default image
        }

        int unreadCount = contact.getUnreadCount();
        if (unreadCount > 0) {
            holder.unreadCountBadge.setVisibility(View.VISIBLE);
            holder.unreadCountBadge.setText(String.valueOf(unreadCount));
        } else {
            holder.unreadCountBadge.setVisibility(View.GONE);
        }

        holder.contactItemLayout.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.light_gray));

        return convertView;
    }

    private void loadProfileImage(ImageView imageView, String userId) {
        StorageReference profileImageRef = storage.getReference().child("ProfileImages/" + userId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Firebase Image", "Fetched image URI: " + uri.toString());
            Glide.with(imageView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.person) // Default image while loading
                    .error(R.drawable.person) // Error image if loading fails
                    .into(imageView);
        }).addOnFailureListener(e -> {
            Log.e("Firebase Image", "Error fetching image: ", e);
            imageView.setImageResource(R.drawable.person); // Set default image on error
        });
    }

    static class ViewHolder {
        TextView contactName;
        TextView contactNumber;
        ImageView contactPhoto;
        TextView unreadCountBadge;
        RelativeLayout contactItemLayout;
    }

    public void updateContacts(List<Contact> updatedContactList) {
        this.contactList = updatedContactList;
        notifyDataSetChanged();
    }
}
