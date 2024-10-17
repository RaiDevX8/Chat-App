package com.example.chatapp.models;

import java.util.List;

public class GroupChat {
    private String groupId;
    private String groupName;
    private String groupDescription;
    private List<String> members; // List to store members (e.g., phone numbers or userIds)

    // Default constructor required for Firestore deserialization
    public GroupChat() {}

    // Constructor with all fields
    public GroupChat(String groupId, String groupName, String groupDescription, List<String> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.members = members; // Initialize members list
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public List<String> getMembers() {
        return members;
    }

    // Setters
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
