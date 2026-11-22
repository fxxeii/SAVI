package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// CHILD CLASS 1: GROUP SAVINGS (Inheritance + Polymorphism)
public class GroupSavings extends Savings {
    private String groupName;
    private double expectedPerMember;
    private LocalDate createdDate;
    private final ArrayList<String> members = new ArrayList<>();

    public GroupSavings(String creator, double targetAmount, String groupName, double expectedPerMember) {
        this(creator, targetAmount, groupName, expectedPerMember, LocalDate.now(), 0, groupName, "");
    }

    public GroupSavings(String creator, double targetAmount, String groupName,
                        double expectedPerMember, LocalDate createdDate) {
        this(creator, targetAmount, groupName, expectedPerMember, createdDate, 0, groupName, "");
    }

    public GroupSavings(String creator, double targetAmount, String groupName,
                        double expectedPerMember, LocalDate createdDate,
                        double weeklyTarget, String savingsName, String targetDate) {
        super(creator, targetAmount,
                (savingsName == null || savingsName.trim().isEmpty()) ? groupName : savingsName,
                targetDate, weeklyTarget);
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required.");
        }
        this.groupName = groupName.trim();
        this.expectedPerMember = Math.max(0, expectedPerMember);
        this.createdDate = createdDate != null ? createdDate : LocalDate.now();
        addMember(creator.trim());
    }

    // --- Group Getters & Setters ---
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        if (groupName != null && !groupName.trim().isEmpty()) {
            this.groupName = groupName.trim();
            if (getSavingsName().isEmpty()) {
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
        while (cursor.isBefore(today)) {
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

    // --- Polymorphic Overrides ---
    @Override
    public String getSummaryLabel() {
        return groupName + " (" + members.size() + " members)";
    }

    @Override
    public void displayInfo() {
        System.out.println("Group: " + groupName + " | Target: " + getTargetAmount() + " | Current: " + getCurrentAmount());
    }
}