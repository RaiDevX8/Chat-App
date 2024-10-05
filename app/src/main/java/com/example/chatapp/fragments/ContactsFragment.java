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

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
    private static final int REQUEST_CONTACTS_PERMISSION = 100; // Define the constant for permission request

    private ListView listView;
    private ContactsListAdapter contactsListAdapter;
    private List<Contact> contactList;
    private ProgressBar progressBar;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        listView = view.findViewById(R.id.list_view);
        progressBar = view.findViewById(R.id.progress_bar);
        searchView = view.findViewById(R.id.search_view);
        contactList = new ArrayList<>();

        // Check permissions and fetch contacts
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_PERMISSION);
        } else {
            fetchContacts(); // Fetch contacts in a separate thread
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

    private void fetchContacts() {
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar
        new Thread(() -> {
            Cursor cursor = null;
            try {
                cursor = requireContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String id = getColumnValue(cursor, ContactsContract.Contacts._ID);
                        String name = getColumnValue(cursor, ContactsContract.Contacts.DISPLAY_NAME);
                        String photoUri = getColumnValue(cursor, ContactsContract.Contacts.PHOTO_URI);

                        // Fetch phone numbers for the contact
                        Cursor phones = requireContext().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);

                        if (phones != null) {
                            while (phones.moveToNext()) {
                                String phoneNumber = getColumnValue(phones, ContactsContract.CommonDataKinds.Phone.NUMBER);
                                if (phoneNumber != null) {
                                    contactList.add(new Contact(name, phoneNumber, photoUri));
                                }
                            }
                            phones.close(); // Close the phones cursor
                        }
                    }
                }
                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    contactsListAdapter = new ContactsListAdapter(getContext(), contactList);
                    listView.setAdapter(contactsListAdapter);
                    progressBar.setVisibility(View.GONE); // Hide the progress bar after loading
                });
            } catch (Exception e) {
                e.printStackTrace(); // Log any exceptions for debugging
            } finally {
                if (cursor != null) {
                    cursor.close(); // Ensure the cursor is closed
                }
            }
        }).start();
    }

    private String getColumnValue(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getString(columnIndex);
        }
        return null; // Return null if the column doesn't exist
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
