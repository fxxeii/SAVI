package com.savi.app.backend;

import java.util.ArrayList;
import java.util.List;

// CHILD CLASS 2: PERSONAL SAVINGS (Inheritance + Polymorphism)
public class PersonalSavings extends Savings {
    /** The tag added to a personal savings in dropdowns (one copy for the whole app). */
    public static final String TAG = " (Personal)";

    public PersonalSavings(String creator, double targetAmount, String savingsName) {
        this(creator, targetAmount, savingsName, "");
    }

    public PersonalSavings(String creator, double targetAmount, String savingsName,
                           String targetDate) {
        super(creator, targetAmount, savingsName, targetDate);
        if (savingsName == null || savingsName.trim().isEmpty()) {
            throw new IllegalArgumentException("Savings name is required.");
        }
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