package com.example.chatapp.models;

import java.util.List;

public class Group {
    private String groupName;
    private String groupDescription;
    private List<String> members;
    private List<String> groupAdmins;
    private String groupImage; // Optional field for storing group image URL

    // Default constructor required for Firestore
    public Group() {
    }

    public Group(String groupName, String groupDescription, List<String> members, List<String> groupAdmins, String groupImage) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.members = members;
        this.groupAdmins = groupAdmins;
        this.groupImage = groupImage;
    }

    // Getters and setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getGroupAdmins() {
        return groupAdmins;
    }

    public void setGroupAdmins(List<String> groupAdmins) {
        this.groupAdmins = groupAdmins;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }
}
