package com.example.chatapp.models;

public class ChatItem {
    private String contactName;
    private String profilePicUrl;
    private String lastMessage;
    private long timestamp;
    private int unreadCount; // Optional: Number of unread messages for this chat
    private String receiverId;  // New field for receiver ID

    // Default constructor
    public ChatItem() {
    }

    // Constructor without unreadCount and receiverId
    public ChatItem(String contactName, String profilePicUrl, String lastMessage, long timestamp) {
        this.contactName = contactName;
        this.profilePicUrl = profilePicUrl;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = 0; // Initialize unreadCount to zero
    }

    // Constructor with all fields
    public ChatItem(String contactName, String profilePicUrl, String lastMessage, long timestamp, int unreadCount, String receiverId) {
        this.contactName = contactName;
        this.profilePicUrl = profilePicUrl;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;  // Initialize unreadCount
        this.receiverId = receiverId;    // Initialize receiverId
    }

    // New constructor to accommodate your requirement
    public ChatItem(String contactName, String profilePicUrl, String lastMessage, long timestamp, int unreadCount) {
        this.contactName = contactName;
        this.profilePicUrl = profilePicUrl;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount; // Initialize unreadCount
        this.receiverId = ""; // Initialize receiverId to an empty string or null if you prefer
    }

    // Getters
    public String getContactName() {
        return contactName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getReceiverId() {
        return receiverId;  // Getter for receiver ID
    }

    // Setters
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
