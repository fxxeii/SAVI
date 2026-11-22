package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// PARENT CLASS (Abstraction + Inheritance + Encapsulation)
public abstract class Savings {
    private String creator;
    private double targetAmount;
    private String savingsName;
    private String targetDate;
    private double weeklyTarget;
    private final ArrayList<Contribution> contributions = new ArrayList<>();

    public Savings(String creator, double targetAmount) {
        this(creator, targetAmount, "", "", 0);
    }

    public Savings(String creator, double targetAmount, String savingsName, String targetDate, double weeklyTarget) {
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator is required.");
        }
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("Target amount must be positive.");
        }
        this.creator = creator.trim();
        this.targetAmount = targetAmount;
        this.savingsName = savingsName != null ? savingsName.trim() : "";
        this.targetDate = targetDate != null ? targetDate.trim() : "";
        this.weeklyTarget = Math.max(0, weeklyTarget);
    }

    // --- Encapsulated Getters & Setters ---
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        if (creator != null && !creator.trim().isEmpty()) {
            this.creator = creator.trim();
        }
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        if (targetAmount > 0) {
            this.targetAmount = targetAmount;
        }
    }

    public String getSavingsName() {
        return savingsName;
    }

    public void setSavingsName(String savingsName) {
        this.savingsName = savingsName != null ? savingsName.trim() : "";
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate != null ? targetDate.trim() : "";
    }

    public double getWeeklyTarget() {
        return weeklyTarget;
    }

    public void setWeeklyTarget(double weeklyTarget) {
        if (weeklyTarget >= 0) {
            this.weeklyTarget = weeklyTarget;
        }
    }

    // --- Shared Contribution Logic ---
    public void addContribution(String member, double amount, LocalDate date, String note) {
        contributions.add(new Contribution(member, amount, date, note));
    }

    public void addContribution(String member, double amount, LocalDate date, String note, long timestamp) {
        contributions.add(new Contribution(member, amount, date, note, timestamp));
    }

    public List<Contribution> getContributions() {
        return new ArrayList<>(contributions);
    }

    public double getCurrentAmount() {
        double sum = 0;
        for (Contribution c : contributions) {
            sum += c.getAmount();
        }
        return sum;
    }

    public List<Contribution> getContributionsOn(LocalDate date) {
        ArrayList<Contribution> result = new ArrayList<>();
        if (date == null) {
            return result;
        }
        for (Contribution c : contributions) {
            if (c.getDate().equals(date)) {
                result.add(c);
            }
        }
        return result;
    }

    public double getTotalOn(LocalDate date) {
        double sum = 0;
        for (Contribution c : getContributionsOn(date)) {
            sum += c.getAmount();
        }
        return sum;
    }

    public boolean isGoalReached() {
        return getCurrentAmount() >= targetAmount;
    }

    /** Replaces every contribution made by oldName with newName (used when a user changes username). */
    public void renameMember(String oldName, String newName) {
        if (oldName == null || newName == null || newName.trim().isEmpty()) {
            return;
        }
        String clean = newName.trim();
        if (creator.equalsIgnoreCase(oldName.trim())) {
            creator = clean;
        }
        for (int i = 0; i < contributions.size(); i++) {
            Contribution c = contributions.get(i);
            if (c.getMember().equalsIgnoreCase(oldName.trim())) {
                contributions.set(i, new Contribution(clean, c.getAmount(), c.getDate(), c.getNote(), c.getTimestamp()));
            }
        }
    }

    // --- ABSTRACTION: every kind of savings must answer these; the UI/backend never checks the type ---

    /** The name shown for this savings (group name / personal savings name). */
    public abstract String getDisplayName();

    /** "Group" or "Personal". */
    public abstract String getTypeLabel();

    /** Can this user see / use this savings? (group: creator or member, personal: only the owner) */
    public abstract boolean isOwnedBy(String username);

    /** Who can put money in or take money out: the group members, or only the owner. */
    public abstract List<String> getParticipants();

    /** Text used in dropdowns. Same as the display name; PersonalSavings overrides it to add a tag. */
    public String getChoiceLabel() {
        return getDisplayName();
    }

    public boolean isPersonal() {
        return false;
    }
}