package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// CHILD CLASS 2: PERSONAL SAVINGS (Inheritance + Polymorphism)
public class PersonalSavings extends Savings {
    /** The tag added to a personal savings in dropdowns (one copy for the whole app). */
    public static final String TAG = " (Personal)";

    private double dailyTarget;
    private LocalDate createdDate;
    private long createdTimestamp;

    public PersonalSavings(String creator, double targetAmount, String savingsName) {
        this(creator, targetAmount, savingsName, 0.0, LocalDate.now(), System.currentTimeMillis());
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName, LocalDate createdDate) {
        this(creator, targetAmount, savingsName, 0.0, createdDate, System.currentTimeMillis());
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName,
                           LocalDate createdDate, long createdTimestamp) {
        this(creator, targetAmount, savingsName, 0.0, createdDate, createdTimestamp);
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName,
                           double dailyTarget, LocalDate createdDate) {
        this(creator, targetAmount, savingsName, dailyTarget, createdDate, System.currentTimeMillis());
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName,
                           double dailyTarget, LocalDate createdDate, long createdTimestamp) {
        super(creator, targetAmount, savingsName);
        if (savingsName == null || savingsName.trim().isEmpty()) {
            throw new IllegalArgumentException("Savings name is required.");
        }
        this.dailyTarget = Math.max(0.0, dailyTarget);
        this.createdDate = createdDate != null ? createdDate : LocalDate.now();
        this.createdTimestamp = Math.max(0L, createdTimestamp);
    }

    public double getDailyTarget() {
        return dailyTarget;
    }

    public void setDailyTarget(double dailyTarget) {
        if (dailyTarget >= 0) {
            this.dailyTarget = dailyTarget;
        }
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        if (createdDate != null) {
            this.createdDate = createdDate;
        }
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public String getDisplayName() {
        return getSavingsName();
    }

    @Override
    public String getTypeLabel() {
        return "Personal";
    }

    @Override
    public boolean isOwnedBy(String username) {
        return username != null && getCreator().equalsIgnoreCase(username.trim());
    }

    @Override
    public List<String> getParticipants() {
        List<String> only = new ArrayList<String>();
        only.add(getCreator());
        return only;
    }

    @Override
    public String getChoiceLabel() {
        return getSavingsName() + TAG;
    }

    @Override
    public boolean isPersonal() {
        return true;
    }
}