package com.savi.app.backend;

public class PersonalSavings extends Savings {

    private String ownerName;
    private double weeklyTarget;   // goal for each week (0 = not set)

    public PersonalSavings(String savingsName, double targetAmount, String owner) {
        super(savingsName, targetAmount);
        this.ownerName = owner;
    }

    public String getOwner() {
        return ownerName;
    }

    public double getWeeklyTarget() {
        return weeklyTarget;
    }

    public void setWeeklyTarget(double weeklyTarget) {
        if (weeklyTarget < 0 || Double.isNaN(weeklyTarget) || Double.isInfinite(weeklyTarget)) {
            throw new IllegalArgumentException("Invalid weekly target.");
        }
        this.weeklyTarget = weeklyTarget;
    }

    @Override
    public void displayInfo() {
        System.out.println("Type: Personal Savings");
        System.out.println("Savings Name: " + savingsName);
        System.out.println("Owner: " + ownerName);
        System.out.println("Target Amount: " + targetAmount);
        System.out.println("Current Amount: " + currentAmount);
    }
}