package com.example.chatapp.models;

public class Chat {
    private String sender;
    private String message;
    private String timestamp;
    private String profilePicUri; // URI for profile picture
    private String phoneNumber; // Add phone number field

    // Constructor for using URI
    public Chat(String sender, String message, String timestamp, String profilePicUri, String phoneNumber) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.profilePicUri = profilePicUri; // Assign URI
        this.phoneNumber = phoneNumber; // Assign phone number
    }

    // Constructor for using drawable resource ID (optional)
    public Chat(String sender, String message, String timestamp, int profilePicResId, String phoneNumber) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.profilePicUri = null; // No URI, set to null
        this.phoneNumber = phoneNumber; // Assign phone number
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProfilePicUri() {
        return profilePicUri; // URI getter
    }

    public String getPhoneNumber() {
        return phoneNumber; // Phone number getter
    }
}
