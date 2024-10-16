package com.example.chatapp.models;

public class Chat {
    private String sender;
    private String message;
    private Long timestamp; // Change to Long (object type)
    private int profileImage; // Replace with the appropriate data type for the profile image if needed
    private String receiverId; // Add this field
    private String phoneNumber; // Add this field for the phone number

        public Chat()
        {}

    // Constructor
    public Chat(String sender, String message, Long timestamp, int profileImage, String receiverId, String phoneNumber) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp; // Initialize timestamp
        this.profileImage = profileImage;
        this.receiverId = receiverId; // Initialize receiverId
        this.phoneNumber = phoneNumber; // Initialize phoneNumber
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() { // Change return type to Long
        return timestamp;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public String getReceiverId() {
        return receiverId; // Add this getter
    }

    public String getPhoneNumber() { // Add this getter
        return phoneNumber;
    }
}
