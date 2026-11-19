package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;

// SAVINGS - the PARENT class of GroupSavings and PersonalSavings.
// ABSTRACTION: it is "abstract", so we can never create a plain Savings. It only defines what
// every kind of savings has (name, target, contributions) and what it must be able to do.
// INHERITANCE: the children get addContribution, getContributionsOn and getTotalOn for free.
public abstract class Savings {

    // "protected" = visible to this class and its children (GroupSavings, PersonalSavings)
    protected final String savingsName;
    protected final double targetAmount;
    protected double currentAmount;                      // total saved so far
    protected final ArrayList<Contribution> contributions = new ArrayList<>();

    public Savings(String savingsName, double targetAmount) {
        this.savingsName = savingsName;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    // The only way to add money: it checks the data first, then records it and updates the total.
    public void addContribution(String member, double amount, LocalDate date, String note) {

        if (member == null || member.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required.");
        }

        if (amount == 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Contribution must be positive.");
        }

        if (date == null) {
            throw new IllegalArgumentException("Contribution date is required.");
        }

        Contribution contribution = new Contribution(
                member.trim(), amount, date, note
        );

        contributions.add(contribution);
        currentAmount += amount;
    }

    // used by the Calendar page: all contributions recorded on one date
    public ArrayList<Contribution> getContributionsOn(LocalDate date) {
        ArrayList<Contribution> result = new ArrayList<>();
        for (Contribution c : contributions) {
            if (c.getDate().equals(date)) {
                result.add(c);
            }
        }
        return result;
    }

    // used by the Calendar page: "Amount Saved" for one date (adds up the list above)
    public double getTotalOn(LocalDate date) {
        double total = 0;
        for (Contribution c : getContributionsOn(date)) {
            total += c.getAmount();
        }
        return total;
    }

    public String getSavingsName() {
        return savingsName;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    // POLYMORPHISM: every child must write its own version of this method
    public abstract void displayInfo();

    // ENCAPSULATION: returns a COPY of the list, so outside code cannot change
    // the real contributions without going through addContribution()
    public ArrayList<Contribution> getContributions() {
        return new ArrayList<>(contributions);
    }
}