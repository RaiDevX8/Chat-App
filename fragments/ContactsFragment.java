package com.example.chatapp.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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

import com.example.chatapp.R;
import com.example.chatapp.Adapters.ContactsListAdapter;
import com.example.chatapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private static final int REQUEST_CONTACTS_PERMISSION = 100;
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

        // Check for permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_PERMISSION);
        } else {
            new FetchContactsTask().execute();
        }

        // Set up search functionality
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

    private void filterContacts(String query) {
        List<Contact> filteredContactList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredContactList.addAll(contactList); // Show all contacts if the query is empty
        } else {
            for (Contact contact : contactList) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredContactList.add(contact);
                }
            }
        }
        if (contactsListAdapter != null) {
            contactsListAdapter.updateContacts(filteredContactList); // Update the adapter with filtered contacts
        }
    }

    private class FetchContactsTask extends AsyncTask<Void, Void, List<Contact>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            List<Contact> contacts = new ArrayList<>();
            ContentResolver contentResolver = requireContext().getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                        int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        int photoUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

                        // Validate indexes before accessing cursor
                        if (idIndex != -1 && nameIndex != -1) {
                            String id = cursor.getString(idIndex);
                            String name = cursor.getString(nameIndex);
                            String photoUri = (photoUriIndex != -1) ? cursor.getString(photoUriIndex) : null;

                            // Query for phone numbers
                            Cursor phones = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id},
                                    null);

                            if (phones != null) {
                                while (phones.moveToNext()) {
                                    int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                    if (phoneIndex != -1) {
                                        String phoneNumber = phones.getString(phoneIndex);
                                        // Pass the default values for messagePreview, timestamp, and unreadCount
                                        String messagePreview = ""; // Default message preview
                                        String timestamp = ""; // Default timestamp
                                        int unreadCount = 0; // Default unread count

                                        contacts.add(new Contact(name, phoneNumber, photoUri, messagePreview, timestamp, unreadCount));
                                    }
                                }
                                phones.close();
                            }
                        }
                    }
                }
                cursor.close();
            }

            return contacts;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);
            progressBar.setVisibility(View.GONE);
            contactList = contacts;
            contactsListAdapter = new ContactsListAdapter(getContext(), contactList);
            listView.setAdapter(contactsListAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new FetchContactsTask().execute();
            } else {
                Log.d("ContactsFragment", "Permission denied to read contacts");
                // Optionally, show a message to the user about why you need the permission
            }
        }
    }
}
