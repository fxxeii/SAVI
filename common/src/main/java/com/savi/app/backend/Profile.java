package com.savi.app.backend;

import java.util.List;

public class Profile {

    // Data models (return types for the methods below)
    public static class User {
        public long id;
        public String name;      // "User2026-Trial"
        public String handle;    // "user.2026" (the @ is added by the page)
        public String avatarUrl; // null = default person icon
    }

    public static class Stats {
        public int groupCount;     // GROUPS
        public double totalSaved;  // TOTAL SAVED
        public int peopleCount;    // PEOPLE
    }

    public static class GroupSetting {
        public long id;
        public String name;        // "December Vacation"
        public long targetDate;    // epoch millis, shown as "Target: Dec 10, 2026"
        public double dailyTarget; // DAILY TARGET
        public double totalTarget; // TOTAL TARGET
    }

    // User info for the header (name, handle, avatar)
    public User getUser(long userId) {
        return null;
    }

    // Numbers for the stats card (groups, total saved, people)
    public Stats getStats(long userId) {
        return null;
    }

    // Groups listed under "GROUP SETTINGS"
    public List<GroupSetting> getUserGroups(long userId) {
        return null;
    }

    // Pencil icon next to the name
    public boolean updateName(long userId, String newName) {
        return false;
    }

    // Camera badge on the avatar
    public boolean updateAvatar(long userId, byte[] imageData) {
        return false;
    }

    // "+ Add Group" pill. Returns the new group's id
    public long addGroup(long userId, String name, long targetDate, double dailyTarget, double totalTarget) {
        return 0;
    }

    // "Edit" pill on a group card
    public boolean updateGroup(long groupId, String name, long targetDate, double dailyTarget, double totalTarget) {
        return false;
    }

    // Count of groups the user belongs to
    public int getGroupCount(long userId) {
        return 0;
    }

    // Sum of everything the user has saved across groups
    public double calculateTotalSaved(long userId) {
        return 0;
    }

    // Count of distinct people across the user's groups
    public int getPeopleCount(long userId) {
        return 0;
    }

    // "Notifications" row badge ("Active" when true)
    public boolean isNotificationsEnabled(long userId) {
        return false;
    }

    // Toggle from the Notifications setting
    public boolean setNotificationsEnabled(long userId, boolean enabled) {
        return false;
    }
}