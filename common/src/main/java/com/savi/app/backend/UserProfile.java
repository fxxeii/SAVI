package com.savi.app.backend;

public class UserProfile {
    private String id;
    private String greetingName;
    private String username;
    private String handle;
    private double personalSaved;
    private boolean notificationsEnabled;
    private boolean appLockEnabled;

    public UserProfile(String id, String greetingName, String username, String handle,
                       double personalSaved, boolean notificationsEnabled, boolean appLockEnabled) {
        this.id = id;
        this.greetingName = greetingName;
        this.username = username;
        this.handle = handle;
        this.personalSaved = personalSaved;
        this.notificationsEnabled = notificationsEnabled;
        this.appLockEnabled = appLockEnabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGreetingName() {
        return (greetingName == null || greetingName.trim().isEmpty()) ? "User" : greetingName.trim();
    }

    public void setGreetingName(String greetingName) {
        this.greetingName = greetingName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public double getPersonalSaved() {
        return personalSaved;
    }

    public void setPersonalSaved(double personalSaved) {
        this.personalSaved = personalSaved;
    }

}