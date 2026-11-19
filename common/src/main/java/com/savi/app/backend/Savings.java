package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Savings {

    protected final String savingsName;
    protected final double targetAmount;
    protected double currentAmount;
    protected final ArrayList<Contribution> contributions = new ArrayList<>();

    public Savings(String savingsName, double targetAmount) {
        this.savingsName = savingsName;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public void addContribution(String member, double amount, LocalDate date, String note) {

        if (member == null || member.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required.");
        }

        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
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

    public ArrayList<Contribution> getContributionsOn(LocalDate date) {
        ArrayList<Contribution> result = new ArrayList<>();
        for (Contribution c : contributions) {
            if (c.getDate().equals(date)) {
                result.add(c);
            }
        }
        return result;
    }

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

    public abstract void displayInfo();

    public ArrayList<Contribution> getContributions() {
        return new ArrayList<>(contributions);
    }
}