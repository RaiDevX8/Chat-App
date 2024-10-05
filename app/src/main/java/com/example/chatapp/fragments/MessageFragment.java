package com.example.chatapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.chatapp.R;

public class MessageFragment extends Fragment {
    private static final String ARG_CONTACT_NAME = "contact_name";
    private static final String ARG_PHONE_NUMBER = "phone_number";

    public static MessageFragment newInstance(String contactName, String phoneNumber) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_NAME, contactName);
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Retrieve the contact name and phone number from arguments
        if (getArguments() != null) {
            String contactName = getArguments().getString(ARG_CONTACT_NAME);
            String phoneNumber = getArguments().getString(ARG_PHONE_NUMBER);

            TextView contactNameTextView = view.findViewById(R.id.contact_name);
            TextView phoneNumberTextView = view.findViewById(R.id.phone_number);

            contactNameTextView.setText(contactName);
            phoneNumberTextView.setText(phoneNumber);
        }

        return view;
    }
}
