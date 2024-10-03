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

    private static final String ARG_CONTACT_NAME = "contactName";

    public static MessageFragment newInstance(String contactName) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_NAME, contactName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the contact name from the arguments
        if (getArguments() != null) {
            String contactName = getArguments().getString(ARG_CONTACT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        TextView contactNameTextView = view.findViewById(R.id.contact_name);

        // Display the contact name in the fragment's UI
        contactNameTextView.setText(getArguments().getString(ARG_CONTACT_NAME));

        return view;
    }
}
