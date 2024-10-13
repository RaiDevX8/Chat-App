package com.example.chatapp.models;

public class Message {
    private String senderId;
    private String receiverId;
    private String messageText;
    private long timestamp;

    public Message() {
        // Empty constructor required for Firestore
    }

    public Message(String senderId, String receiverId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
