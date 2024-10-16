package com.example.chatapp.models;

public class GroupChat {
    private String groupId;
    private String groupName;
    private String groupDescription;
public GroupChat()
{}

    public GroupChat(String groupId, String groupName, String groupDescription) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
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
}
