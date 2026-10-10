package com.savi.app.backend;

public class Savings {

    protected String savingsName;
    protected double targetAmount;
    protected double currentAmount;

    public Savings(String savingsName, double targetAmount) {
        this.savingsName = savingsName;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public void addSavings(double amount) {
        currentAmount += amount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void displayInfo() {
        System.out.println("Savings Name: " + savingsName);
        System.out.println("Target Amount: " + targetAmount);
        System.out.println("Current Amount: " + currentAmount);
    }
}