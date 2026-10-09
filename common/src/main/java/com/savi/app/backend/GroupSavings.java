package com.savi.app.backend;

public class GroupSavings extends Savings {

    private String groupName;

    public GroupSavings(String savingsName, double targetAmount, String groupName) {
        super(savingsName, targetAmount);
        this.groupName = groupName;
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