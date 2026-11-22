package com.savi.app.backend;
// this class represents one actual group savings record.
// this stores the group's information, members, and contributions, and performs group-related calculations.

import java.time.LocalDate;
import java.util.ArrayList;

// INHERITANCE: GroupSavings "is a" Savings. It reuses the parent's name, target, contributions,
// addContribution(), getTotalOn(), ... and only adds what is special about a GROUP:
// members, the expected amount per member, and the missed-amount calculation.
public class GroupSavings extends Savings {

    private String groupName;
    private String creator;
    private String targetDate = "TBD";
    private final ArrayList<String> members = new ArrayList<>();
    private double expectedPerMember;   // how much each member should save per day
    private final LocalDate createdDate;      // counting of missed days starts here
    private double weeklyTarget;   // goal for each week (0 = not set)

    // implements constructor overloading
    // POLYMORPHISM (compile-time): two constructors with the same name but different parameters.
    // This one has no date, so it uses today as the creation date.
    public GroupSavings(String savingsName, double targetAmount,
                        String groupName, double expectedPerMember) {

        this(savingsName, targetAmount, groupName,
                expectedPerMember, LocalDate.now());
    }

    // This one takes the creation date (used when loading a saved group from the TXT)
    public GroupSavings(String savingsName, double targetAmount,
                        String groupName, double expectedPerMember,
                        LocalDate createdDate) {

        super(savingsName, targetAmount);   // the parent class stores the name and target

        // VALIDATION: refuse invalid data when the object is created
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required.");
        }

        if (expectedPerMember < 0 ||
                Double.isNaN(expectedPerMember) ||
                Double.isInfinite(expectedPerMember)) {
            throw new IllegalArgumentException("Invalid expected contribution.");
        }

        if (createdDate == null) {
            throw new IllegalArgumentException("Creation date is required.");
        }

        this.groupName = groupName.trim();
        this.expectedPerMember = expectedPerMember;
        this.createdDate = createdDate;
    }

    // adds a member, ignoring blank names and names that already exist (not case sensitive)
    public void addMember(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String cleanName = name.trim();

        for (String m : members) {
            if (m.equalsIgnoreCase(cleanName)) {
                return;
            }
        }

        members.add(cleanName);
    }

    public void renameMember(String oldName, String newName) {
        if (oldName == null || newName == null || newName.trim().isEmpty()) return;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equalsIgnoreCase(oldName.trim())) {
                members.set(i, newName.trim());
            }
        }
        if (creator != null && creator.equalsIgnoreCase(oldName.trim())) creator = newName.trim();
        ArrayList<Contribution> renamed = new ArrayList<>();
        for (Contribution contribution : contributions) {
            String memberName = contribution.getMember().equalsIgnoreCase(oldName.trim())
                    ? newName.trim() : contribution.getMember();
            Contribution copy = new Contribution(memberName, contribution.getAmount(),
                    contribution.getDate(), contribution.getNote());
            copy.setTimestamp(contribution.getTimestamp());
            renamed.add(copy);
        }
        contributions.clear();
        contributions.addAll(renamed);
    }

    public boolean hasMember(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        for (String m : members) {
            if (m.equalsIgnoreCase(name.trim())) {
                return true;
            }
        }

        return false;
    }

    // ENCAPSULATION: returns a copy, so outside code cannot add members behind our back
    public ArrayList<String> getMembers() {
        return new ArrayList<>(members);
    }

    public double getExpectedPerMember() {
        return expectedPerMember;
    }

    public void setExpectedPerMember(double expectedPerMember) {
        if (expectedPerMember < 0 || Double.isNaN(expectedPerMember) || Double.isInfinite(expectedPerMember)) {
            throw new IllegalArgumentException("Invalid expected contribution.");
        }
        this.expectedPerMember = expectedPerMember;
    }

    public String getCreator() {
        return creator == null ? "" : creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? "" : creator.trim();
    }

    public String getTargetDate() {
        return targetDate == null || targetDate.trim().isEmpty() ? "TBD" : targetDate;
    }

    public void setTargetDate(String targetDate) {
        String value = targetDate == null ? "" : targetDate.trim();
        if (!value.isEmpty() && !"TBD".equalsIgnoreCase(value)) {
            try {
                LocalDate.parse(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Target date must use YYYY-MM-DD or TBD.");
            }
        }
        this.targetDate = value.isEmpty() ? "TBD" : value;
    }

    public void setGroupName(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required.");
        }
        this.groupName = groupName.trim();
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public double getWeeklyTarget() {
        return weeklyTarget;
    }

    public void setWeeklyTarget(double weeklyTarget) {
        if (weeklyTarget < 0 || Double.isNaN(weeklyTarget) || Double.isInfinite(weeklyTarget)) {
            throw new IllegalArgumentException("Invalid weekly target.");
        }
        this.weeklyTarget = weeklyTarget;
    }

    // Per-day helper: how much a member still owes on ONE specific date
    // (expected amount minus what they paid that day, never below 0).
    // getTotalMissed() below uses a running total instead of this method.
    public double getMissed(String member, LocalDate date) {
        double contributed = 0;

        for (Contribution c : contributions) {
            if (c.getMember().equalsIgnoreCase(member) && c.getDate().equals(date)) {
                contributed += c.getAmount();
            }
        }

        return Math.max(0, expectedPerMember - contributed);
    }

    // USED BY THE PENDING PAGE: the total a member still owes up to today.
    // total owed = (number of days since createdDate, including today) x expectedPerMember
    // total missed = total owed - everything the member paid, never below 0
    public double getTotalMissed(String member) {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        // Bilangin kung ilang araw na mula createdDate hanggang today (inclusive)
        // (count the days from createdDate to today; "yyyy-MM-dd" text compares in date order)
        int daysCount = 0;
        LocalDate date = createdDate;
        while (date.toString().compareTo(todayStr) <= 0) {
            daysCount++;
            date = date.plusDays(1);
        }

        double totalExpected = daysCount * expectedPerMember;

        // Kunin yung overall na naibayad ng member (yung sobrang bayad sa isang araw ay automatic na ibabawas sa past missed days)
        // (add up everything the member paid; extra payment on one day automatically covers earlier missed days.
        // Only positive amounts count, so a transfer out is not counted as paid.)
        double totalContributed = 0;
        for (Contribution c : contributions) {
            if (c.getMember().equalsIgnoreCase(member) && c.getAmount() > 0) {
                totalContributed += c.getAmount();
            }
        }

        return Math.max(0, totalExpected - totalContributed);
    }

    public String getGroupName() {
        return groupName;
    }

    // POLYMORPHISM (runtime): this overrides the abstract displayInfo() of Savings.
    // PersonalSavings has its own version, so the same call behaves differently per type.
    @Override
    public void displayInfo() {
        System.out.println("Type: Group Savings");
        System.out.println("Group Name: " + groupName);
        System.out.println("Savings Name: " + savingsName);
        System.out.println("Target Amount: " + targetAmount);
        System.out.println("Current Amount: " + currentAmount);
    }
}