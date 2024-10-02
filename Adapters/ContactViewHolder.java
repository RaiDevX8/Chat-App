package com.example.chatapp.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    TextView contactName;
    TextView contactNumber;
    ImageView contactPhoto;

    public ContactViewHolder(View itemView) {
        super(itemView);

        // Initialize views from item_contact.xml
        contactName = itemView.findViewById(R.id.contact_name);
        contactNumber = itemView.findViewById(R.id.contact_number);
        contactPhoto = itemView.findViewById(R.id.contact_photo);
    }
}
