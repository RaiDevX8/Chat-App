package com.example.chatapp.models;


import com.google.firebase.Timestamp;

public class GroupMessageModel {
    private String senderId;
    private String senderName;  // You may or may not need this depending on your structure
    private String messageText;
    private Timestamp timestamp; // Add this field if you want to manage timestamps
    public GroupMessageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(GroupMessageModel.class)
    }


    public GroupMessageModel(String senderId, String messageText, Timestamp timestamp) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
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
}
