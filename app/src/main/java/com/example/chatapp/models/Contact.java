package com.example.chatapp.models;

public class Contact {
    private String name;
    private String phoneNumber;
    private String photoUri; // Uri of the contact's photo (for external images)
    private int photoResource; // Resource ID for the contact's photo (for internal drawable)
    private String lastMessage; // Last message preview
    private String timestamp; // Time of the last message
    private int unreadCount; // Unread message count

    // Constructor for using photo resource
    public Contact(String name, String phoneNumber, int photoResource, String lastMessage, String timestamp, int unreadCount) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoResource = photoResource;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.photoUri = null; // No URI used in this case
    }

    // Constructor for using photo URI
    public Contact(String name, String phoneNumber, String photoUri, String lastMessage, String timestamp, int unreadCount) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.photoResource = 0; // No resource ID used in this case
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public int getPhotoResource() {
        return photoResource;
    }

    public String getLastMessage() {
        return lastMessage;
    } // Get last message

    public String getTimestamp() {
        return timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
