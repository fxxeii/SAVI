package com.savi.app.backend;

import java.util.ArrayList;

public class GroupSavings extends Savings {

    private String groupName;
    private ArrayList<String> members = new ArrayList<>();
    private double expectedPerMember;

    public GroupSavings(String savingsName, double targetAmount, String groupName, double expectedPerMember) {
        super(savingsName, targetAmount);
        this.groupName = groupName;
        this.expectedPerMember = expectedPerMember;
    }

    public void addMember(String name) {
        members.add(name);
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public double getMissed(String member) {
        double paid = 0;
        for (Contribution c : contributions) {
            if (c.getMember().equalsIgnoreCase(member)) {
                paid += c.getAmount();
            }
        }
        return Math.max(0, expectedPerMember - paid);
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