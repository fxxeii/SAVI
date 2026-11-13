package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Savings {

    protected String savingsName;
    protected double targetAmount;
    protected double currentAmount;
    protected ArrayList<Contribution> contributions = new ArrayList<>();

    public Savings(String savingsName, double targetAmount) {
        this.savingsName = savingsName;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public void addContribution(String member, double amount, LocalDate date, String note) {
        contributions.add(new Contribution(member, amount, date, note));
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
}