package com.example.conversation.model;

public class User {
    private String displayName;
    private String status;
    private String image;

    public User() {
    }

    public User(String displayName, String status, String image) {
        this.displayName = displayName;
        this.status = status;
        this.image = image;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
