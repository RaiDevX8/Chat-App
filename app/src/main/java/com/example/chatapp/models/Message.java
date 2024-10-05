package com.example.chatapp.models;

public class Message {
    private String body;
    private String time;
    private boolean isSent;
    private String senderName;
    private int profileImageResource;

    public Message(String body, String time, boolean isSent, String senderName, int profileImageResource) {
        this.body = body;
        this.time = time;
        this.isSent = isSent;
        this.senderName = senderName;
        this.profileImageResource = profileImageResource;
    }

    public String getBody() {
        return body;
    }

    public String getTime() {
        return time;
    }

    public boolean isSent() {
        return isSent;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getProfileImageResource() {
        return profileImageResource;
    }
}
