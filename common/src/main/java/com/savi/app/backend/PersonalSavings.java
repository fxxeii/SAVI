package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// CHILD CLASS 2: PERSONAL SAVINGS (Inheritance + Polymorphism)
public class PersonalSavings extends Savings {
    /** The tag added to a personal savings in dropdowns (one copy for the whole app). */
    public static final String TAG = " (Personal)";

    private LocalDate createdDate;
    private long createdTimestamp;

    public PersonalSavings(String creator, double targetAmount, String savingsName) {
        this(creator, targetAmount, savingsName, LocalDate.now(), System.currentTimeMillis());
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName, LocalDate createdDate) {
        this(creator, targetAmount, savingsName, createdDate, System.currentTimeMillis());
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName,
                           LocalDate createdDate, long createdTimestamp) {
        super(creator, targetAmount, savingsName);
        if (savingsName == null || savingsName.trim().isEmpty()) {
            throw new IllegalArgumentException("Savings name is required.");
        }
        this.createdDate = createdDate;
        this.createdTimestamp = Math.max(0L, createdTimestamp);
    }

    public LocalDate getCreatedDate() {
        return createdDate;
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