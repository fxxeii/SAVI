package com.savi.app.backend;
// this class represents one actual group savings record.
// this stores the group's information, members, and contributions, and performs group-related calculations.

import java.time.LocalDate;
import java.util.ArrayList;

public class GroupSavings extends Savings {

    private final String groupName;
    private final ArrayList<String> members = new ArrayList<>();
    private final double expectedPerMember;
    private final LocalDate createdDate;

    // implements constructor overloading
    public GroupSavings(String savingsName, double targetAmount,
                        String groupName, double expectedPerMember) {

        this(savingsName, targetAmount, groupName,
                expectedPerMember, LocalDate.now());
    }

    public GroupSavings(String savingsName, double targetAmount,
                        String groupName, double expectedPerMember,
                        LocalDate createdDate) {

        super(savingsName, targetAmount);

        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required.");
        }

        if (expectedPerMember < 0 ||
                Double.isNaN(expectedPerMember) ||
                Double.isInfinite(expectedPerMember)) {
            throw new IllegalArgumentException("Invalid expected contribution.");
        }

        if (createdDate == null) {
            throw new IllegalArgumentException("Creation date is required.");
        }

        this.groupName = groupName.trim();
        this.expectedPerMember = expectedPerMember;
        this.createdDate = createdDate;
    }

    public void addMember(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String cleanName = name.trim();

        for (String m : members) {
            if (m.equalsIgnoreCase(cleanName)) {
                return;
            }
        }

        members.add(cleanName);
    }

    public boolean hasMember(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        for (String m : members) {
            if (m.equalsIgnoreCase(name.trim())) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getMembers() {
        return new ArrayList<>(members);
    }

    public double getExpectedPerMember() {
        return expectedPerMember;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public double getMissed(String member, LocalDate date) {
        double contributed = 0;

        for (Contribution c : contributions) {
            if (c.getMember().equalsIgnoreCase(member) && c.getDate().equals(date)) {
                contributed += c.getAmount();
            }
        }

        return Math.max(0, expectedPerMember - contributed);
    }

    public double getTotalMissed(String member) {
        double totalMissed = 0;
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        LocalDate date = createdDate;

        while (date.toString().compareTo(todayStr) <= 0) {
            totalMissed += getMissed(member, date);
            date = date.plusDays(1);
        }

        return totalMissed;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public void displayInfo() {
        System.out.println("Type: Group Savings");
        System.out.println("Group Name: " + groupName);
        System.out.println("Savings Name: " + savingsName);
        System.out.println("Target Amount: " + targetAmount);
        System.out.println("Current Amount: " + currentAmount);
    }
}