package com.savi.app.backend;

public class PersonalSavings extends Savings {

    private String ownerName;

    public PersonalSavings(String savingsName, double targetAmount, String owner) {
        super(savingsName, targetAmount);
        this.ownerName = owner;
    }

    public String getOwner() {
        return ownerName;
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