package com.example.chatapp.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapp.R;
import com.example.chatapp.fragments.GroupMessageFragment;

public class GroupChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_messages);

        // Fetch the group name from the intent
        String groupName = getIntent().getStringExtra("groupName");

        // Set the group name in the TextView
        TextView groupNameTextView = findViewById(R.id.group_name);
        groupNameTextView.setText(groupName);

        // Initialize fragment for displaying group messages
        if (savedInstanceState == null) {
            GroupMessageFragment groupMessageFragment = new GroupMessageFragment();

            // Pass the groupId and groupName as arguments to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("groupId", getIntent().getStringExtra("groupId"));
            bundle.putString("groupName", groupName);
            groupMessageFragment.setArguments(bundle);

            // Perform the fragment transaction to load the GroupMessageFragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, groupMessageFragment);
            fragmentTransaction.commit();
        }
    }
}
