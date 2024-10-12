package com.example.chatapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.chatapp.Adapters.ContactsListAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.Contact;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
    private static final int REQUEST_CONTACTS_PERMISSION = 100; // Define the constant for permission request

    private ListView listView;
    private ContactsListAdapter contactsListAdapter;
    private List<Contact> contactList;
    private ProgressBar progressBar;
    private SearchView searchView;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        listView = view.findViewById(R.id.list_view);
        progressBar = view.findViewById(R.id.progress_bar);
        searchView = view.findViewById(R.id.search_view);
        contactList = new ArrayList<>();

        db = FirebaseFirestore.getInstance(); // Initialize FirebaseFirestore

        // Check permissions and fetch contacts
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_PERMISSION);
        } else {
            fetchAndMatchContacts(); // Fetch and match contacts with Firebase users
        }

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Contact selectedContact = contactList.get(position);

            // Pass both the contact name and phone number to the MessageFragment
            MessageFragment messageFragment = MessageFragment.newInstance(
                    selectedContact.getName(),
                    selectedContact.getPhoneNumber());

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, messageFragment)
                    .addToBackStack(null)
                    .commit();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        return view;
    }

    // Function to normalize phone numbers by removing non-digit characters and handling country code
    private String normalizePhoneNumber(String phoneNumber) {
        String normalizedNumber = phoneNumber.replaceAll("[^\\d]", "");
        if (normalizedNumber.startsWith("91")) {
            return normalizedNumber; // Already includes country code
        } else {
            return "91" + normalizedNumber; // Add country code for India
        }
    }

    // Fetch contacts from the device and match them with Firestore users
    private void fetchAndMatchContacts() {
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar

        // Fetch contacts from device in a background thread
        new Thread(() -> {
            List<String> deviceContacts = new ArrayList<>();
            Cursor cursor = requireContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    if (phoneNumberIndex != -1) {
                        String phoneNumber = cursor.getString(phoneNumberIndex);
                        deviceContacts.add(normalizePhoneNumber(phoneNumber));
                    }
                }
                cursor.close(); // Close cursor
            }

            // Fetch users from Firestore and match them with device contacts
            db.collection("Users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            contactList.clear(); // Clear the list before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String firebasePhoneNumber = document.getString("mobileNumber");
                                String normalizedFirebaseNumber = normalizePhoneNumber(firebasePhoneNumber);

                                // Check if any device contact matches the Firebase user's number
                                for (String contactPhoneNumber : deviceContacts) {
                                    if (normalizedFirebaseNumber.equals(contactPhoneNumber)) {
                                        String name = document.getString("firstName") + " " + document.getString("lastName");
                                        contactList.add(new Contact(name, firebasePhoneNumber, null)); // Add contact to list
                                        break;
                                    }
                                }
                            }

                            // Update the UI on the main thread
                            requireActivity().runOnUiThread(() -> {
                                contactsListAdapter = new ContactsListAdapter(getContext(), contactList);
                                listView.setAdapter(contactsListAdapter);
                                progressBar.setVisibility(View.GONE); // Hide progress bar
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE); // Hide progress bar on error
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                        });
                    });
        }).start();
    }

    private void filterContacts(String query) {
        List<Contact> filteredContacts = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredContacts.add(contact);
            }
        }
        contactsListAdapter.updateContacts(filteredContacts);
    }
}
