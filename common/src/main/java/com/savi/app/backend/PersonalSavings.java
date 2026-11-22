package com.savi.app.backend;

// CHILD CLASS 2: PERSONAL SAVINGS (Inheritance + Polymorphism)
public class PersonalSavings extends Savings {

    public PersonalSavings(String creator, double targetAmount, String savingsName) {
        this(creator, targetAmount, savingsName, 0, "");
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName, double weeklyTarget) {
        this(creator, targetAmount, savingsName, weeklyTarget, "");
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName,
                           double weeklyTarget, String targetDate) {
        super(creator, targetAmount, savingsName, targetDate, weeklyTarget);
        if (savingsName == null || savingsName.trim().isEmpty()) {
            throw new IllegalArgumentException("Savings name is required.");
        }
    }

    @Override
    public String getSummaryLabel() {
        return getSavingsName() + " (Personal)";
    }

    @Override
    public void displayInfo() {
        System.out.println("Personal Goal: " + getSavingsName() + " | Target: " + getTargetAmount() + " | Current: " + getCurrentAmount());
    }
}