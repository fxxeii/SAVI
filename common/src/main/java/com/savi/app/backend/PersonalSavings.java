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
}