package com.savi.app.backend;

import java.util.List;

public class People {

    // Data models (return types for the methods below)
    public static class Group {
        public long id;
        public String name;
        public double totalPool;
        public int contributorCount;
        public long updatedAt;
    }

    public static class Member {
        public long id;
        public String name;
        public String handle;
        public double totalContribution;
        public double missedContribution;
        public int avatarColor;
    }

    public static class Transaction {
        public long id;
        public long memberId;
        public double amount;
        public String status; // "PAID" or "MISSED"
        public long date;
    }

    // Group info for the banner and "Updated Today" pill
    public Group getGroup(long groupId) {
        return null;
    }

    // All members of the group, for the member cards
    public List<Member> getGroupMembers(long groupId) {
        return null;
    }

    // All contributions of one member, for the "View all transactions" page
    public List<Transaction> getMemberTransactions(long groupId, long memberId) {
        return null;
    }

    // Sum of all paid contributions in the group
    public double calculateTotalPool(long groupId) {
        return 0;
    }

    // Sum of one member's paid contributions
    public double calculateMemberTotal(long groupId, long memberId) {
        return 0;
    }

    // Sum of one member's missed contributions (drives the red/green stat box)
    public double calculateMemberMissed(long groupId, long memberId) {
        return 0;
    }

    // Number of members in the group
    public int getContributorCount(long groupId) {
        return 0;
    }
}