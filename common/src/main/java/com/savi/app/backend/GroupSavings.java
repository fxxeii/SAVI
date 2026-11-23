package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// CHILD CLASS 1: GROUP SAVINGS (Inheritance + Polymorphism)
public class GroupSavings extends Savings {
    private String groupName;
    private double expectedPerMember;
    private LocalDate createdDate;
    private long createdTimestamp;
    private final ArrayList<String> members = new ArrayList<>();

    public GroupSavings(String creator, double targetAmount, String groupName, double expectedPerMember) {
        this(creator, targetAmount, groupName, expectedPerMember, LocalDate.now(), groupName, System.currentTimeMillis());
    }

    public GroupSavings(String creator, double targetAmount, String groupName,
                        double expectedPerMember, LocalDate createdDate) {
        this(creator, targetAmount, groupName, expectedPerMember, createdDate, groupName, System.currentTimeMillis());
    }

    public GroupSavings(String creator, double targetAmount, String groupName,
                        double expectedPerMember, LocalDate createdDate,
                        String savingsName) {
        this(creator, targetAmount, groupName, expectedPerMember, createdDate, savingsName, System.currentTimeMillis());
    }

    public GroupSavings(String creator, double targetAmount, String groupName,
                        double expectedPerMember, LocalDate createdDate,
                        String savingsName, long createdTimestamp) {
        super(creator, targetAmount,
                (savingsName == null || savingsName.trim().isEmpty()) ? groupName : savingsName);
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required.");
        }
        this.groupName = groupName.trim();
        this.expectedPerMember = Math.max(0, expectedPerMember);
        this.createdDate = createdDate != null ? createdDate : LocalDate.now();
        this.createdTimestamp = createdTimestamp > 0 ? createdTimestamp : System.currentTimeMillis();
        addMember(creator.trim());
    }

    // --- Group Getters & Setters ---
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        if (groupName != null && !groupName.trim().isEmpty()) {
            // savingsName is the same text as groupName, so they must change together
            boolean linked = getSavingsName().isEmpty() || getSavingsName().equals(this.groupName);
            this.groupName = groupName.trim();
            if (linked) {
                setSavingsName(this.groupName);
            }
        }
    }

    public double getExpectedPerMember() {
        return expectedPerMember;
    }

    public void setExpectedPerMember(double expectedPerMember) {
        if (expectedPerMember >= 0) {
            this.expectedPerMember = expectedPerMember;
        }
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    // --- Member Management ---
    public void addMember(String memberName) {
        if (memberName == null || memberName.trim().isEmpty()) {
            return;
        }
        String clean = memberName.trim();
        if (!hasMember(clean)) {
            members.add(clean);
        }
    }

    public boolean removeMember(String memberName) {
        if (memberName == null || memberName.trim().isEmpty()) {
            return false;
        }
        String target = memberName.trim();
        if (getCreator().equalsIgnoreCase(target)) {
            return false;
        }
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equalsIgnoreCase(target)) {
                members.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean hasMember(String memberName) {
        if (memberName == null) {
            return false;
        }
        String clean = memberName.trim();
        for (String m : members) {
            if (m.equalsIgnoreCase(clean)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getMembers() {
        return new ArrayList<>(members);
    }

    public int getMemberCount() {
        return members.size();
    }

    // --- Missed / Pending Calculations ---
    public double getTotalMissed(String member) {
        if (member == null || !hasMember(member) || expectedPerMember <= 0) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        long days = 0;
        LocalDate cursor = createdDate;
        while (cursor.toString().compareTo(today.toString()) < 0) {
            days++;
            cursor = cursor.plusDays(1);
        }
        double totalExpected = (days + 1) * expectedPerMember;

        double totalContributed = 0;
        for (Contribution c : getContributions()) {
            if (c.getMember().equalsIgnoreCase(member.trim())) {
                totalContributed += c.getAmount();
            }
        }
        return Math.max(0, totalExpected - totalContributed);
    }

    // --- Polymorphic Overrides (each one replaces an abstract method of Savings) ---
    @Override
    public String getDisplayName() {
        return groupName;
    }

    @Override
    public String getTypeLabel() {
        return "Group";
    }

    @Override
    public boolean isOwnedBy(String username) {
        return username != null && (getCreator().equalsIgnoreCase(username.trim()) || hasMember(username));
    }

    @Override
    public List<String> getParticipants() {
        return getMembers();
    }

    @Override
    public void renameMember(String oldName, String newName) {
        super.renameMember(oldName, newName);          // creator + contributions
        if (oldName == null || newName == null || newName.trim().isEmpty()) {
            return;
        }
        String clean = newName.trim();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equalsIgnoreCase(oldName.trim())) {
                members.set(i, clean);
            }
        }
    }
}