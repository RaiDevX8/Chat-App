package com.example.chatapp.models;

public class Chat {
    private String sender;
    private String message;
    private String timestamp;
    private String profilePicUri; // URI for profile picture

    // Constructor for using URI
    public Chat(String sender, String message, String timestamp, String profilePicUri) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.profilePicUri = profilePicUri; // Assign URI
    }

    // Constructor for using drawable resource ID (optional)
    public Chat(String sender, String message, String timestamp, int profilePicResId) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.profilePicUri = null; // No URI, set to null
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
}
