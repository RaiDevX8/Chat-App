package com.example.chatapp.models;

public class Message {
    private String senderId;
    private String receiverId; // Use receiverId for individual messages
    private String groupId; // New field for group messages
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

//    public Message(String senderId, String groupId, String messageText, long timestamp) {
//        this.senderId = senderId;
//        this.groupId = groupId; // Set group ID for group messages
//        this.messageText = messageText;
//        this.timestamp = timestamp;
//    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getGroupId() {
        return groupId; // Getter for group ID
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId; // Setter for group ID
    }
}
