package com.savi.app.backend;

import java.util.List;

// not done

public class Profile {

    public static class User {
        public long id;
        public String name;
        public String handle;
        public String avatarUrl;
    }

    public static class Stats {
        public int groupCount;
        public double totalSaved;
        public int peopleCount;
    }

    public static class GroupSetting {
        public long id;
        public String name;
        public long targetDate;
        public double dailyTarget;
        public double totalTarget;
    }

    public User getUser(long userId) {
        return null;
    }

    public Stats getStats(long userId) {
        return null;
    }

    public List<GroupSetting> getUserGroups(long userId) {
        return null;
    }

    public boolean updateName(long userId, String newName) {
        return false;
    }

    public boolean updateAvatar(long userId, byte[] imageData) {
        return false;
    }

    public long addGroup(long userId, String name, long targetDate, double dailyTarget, double totalTarget) {
        return 0;
    }

    public boolean updateGroup(long groupId, String name, long targetDate, double dailyTarget, double totalTarget) {
        return false;
    }

    public int getGroupCount(long userId) {
        return 0;
    }

    public double calculateTotalSaved(long userId) {
        return 0;
    }

    public int getPeopleCount(long userId) {
        return 0;
    }

    public boolean isNotificationsEnabled(long userId) {
        return false;
    }

    public boolean setNotificationsEnabled(long userId, boolean enabled) {
        return false;
    }
}