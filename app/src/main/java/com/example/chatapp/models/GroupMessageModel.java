package com.example.chatapp.models;

import com.google.firebase.Timestamp;

public class GroupMessageModel {
    private String senderId;
    private String senderName;  // Optional, depending on your needs
    private String messageText;
    private Timestamp timestamp; // Manage timestamps
    private String senderProfileImageUrl; // Add this field for the sender's profile image

    public GroupMessageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(GroupMessageModel.class)
    }

    public GroupMessageModel(String senderId, String messageText, Timestamp timestamp, String senderProfileImageUrl) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderProfileImageUrl = senderProfileImageUrl; // Initialize the profile image URL
    }

    // Getters and Setters
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderProfileImageUrl() {
        return senderProfileImageUrl; // Getter for the profile image URL
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl; // Setter for the profile image URL
    }
}
