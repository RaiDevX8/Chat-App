package com.example.chatapp.models;

public class Chat {
    private String sender;
    private String message;
    private Long timestamp; // Change to Long
    private String profilePicUri; // URI for profile picture
    private String phoneNumber; // Add phone number field

    public Chat() {}

    // Constructor for using URI
    public Chat(String sender, String message, Long timestamp, String profilePicUri, String phoneNumber) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp; // Assign Long
        this.profilePicUri = profilePicUri; // Assign URI
        this.phoneNumber = phoneNumber; // Assign phone number
    }

    // Constructor for using drawable resource ID (optional)
    public Chat(String sender, String message, Long timestamp, int profilePicResId, String phoneNumber) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp; // Assign Long
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

    public Long getTimestamp() {
        return timestamp; // Return Long
    }

    public String getProfilePicUri() {
        return profilePicUri; // URI getter
    }

    public String getPhoneNumber() {
        return phoneNumber; // Phone number getter
    }
}
