package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Transfer {
    public static final String SUCCESS = "success";

    // ---------------------------------------------------------------- ENCAPSULATION
    private Home home;                    // gives us the groups of the logged-in user
    private String fromGroup = "";
    private String fromMember = "";
    private String toGroup = "";
    private String toMember = "";
    private String amountText = "";

    public Transfer(User user, List<? extends Savings> allSavings) {
        // Home does the "which groups is the logged-in user in" work, so it is reused here
        this.home = new Home(user, allSavings);
    }

    // ---------------------------------------------------------------- DROPDOWN CONTENTS

    /** Names for the "Select group" dropdowns (the groups of the logged-in user). */
    public List<String> getGroupNames() {
        List<String> names = new ArrayList<String>();
        for (GroupSavings g : home.getGroups()) {
            names.add(g.getGroupName());
        }
        return names;
    }

    /** Names for the "Select member" dropdown of the chosen group (empty if no such group). */
    public List<String> getMemberNames(String groupName) {
        List<String> names = new ArrayList<String>();
        GroupSavings g = findGroup(clean(groupName));
        if (g != null) {
            names.addAll(g.getMembers());   // a copy, so the group's own list can't be changed
        }
        return names;
    }

    /** What a member has put into a group so far (transfers in/out included, never below 0). */
    public double getBalance(String groupName, String memberName) {
        GroupSavings g = findGroup(clean(groupName));
        if (g == null) {
            return 0;
        }
        double total = 0;
        for (Contribution c : g.getContributions()) {
            if (c.getMember().equalsIgnoreCase(clean(memberName))) {
                total += c.getAmount();
            }
        }
        return Math.max(0, total);
    }

    // ---------------------------------------------------------------- SETTERS
    // Choosing another group clears the member, because the member list belongs to that group.

    public void setFromGroup(String groupName) {
        this.fromGroup = clean(groupName);
        this.fromMember = "";
    }

    public void setFromMember(String memberName) {
        this.fromMember = clean(memberName);
    }

    public void setToGroup(String groupName) {
        this.toGroup = clean(groupName);
        this.toMember = "";
    }

    public void setToMember(String memberName) {
        this.toMember = clean(memberName);
    }

    /** The raw text of the Amount box ("250", "1,250.50" and "P250" are all accepted). */
    public void setAmount(String text) {
        this.amountText = text == null ? "" : text.trim();
    }

    /** Empties every choice (call it when the page is opened again). */
    public void clear() {
        fromGroup = "";
        fromMember = "";
        toGroup = "";
        toMember = "";
        amountText = "";
    }

    // ---------------------------------------------------------------- GETTERS (confirmation page)

    public String getFromGroup() {
        return fromGroup;
    }

    public String getFromMember() {
        return fromMember;
    }

    public String getToGroup() {
        return toGroup;
    }

    public String getToMember() {
        return toMember;
    }

    /** The amount as a number, rounded to centavos. 0 if the text is not a valid amount. */
    public double getAmount() {
        double value = parseAmount(amountText);
        return value < 0 ? 0 : value;
    }

    /** "P250" or "P1,250.50" for the confirmation page. */
    public String getAmountText() {
        return Home.pesoShort(getAmount());
    }

    // ---------------------------------------------------------------- VALIDATE / CONFIRM

    /** Returns "success" or the message to show to the user. Changes nothing. */
    public String validate() {
        if (fromGroup.isEmpty()) {
            return "Please choose a group where the funds will come from.";
        }
        if (fromMember.isEmpty()) {
            return "Please choose the member who will send the funds.";
        }
        if (toGroup.isEmpty()) {
            return "Please choose the group that will receive the funds.";
        }
        if (toMember.isEmpty()) {
            return "Please choose the member who will receive the funds.";
        }
        if (amountText.isEmpty()) {
            return "Please enter an amount.";
        }

        double amount = parseAmount(amountText);
        if (amount < 0) {
            return "Amount must be a number.";
        }
        if (amount <= 0) {
            return "Amount must be greater than 0.";
        }

        GroupSavings from = findGroup(fromGroup);
        GroupSavings to = findGroup(toGroup);
        if (from == null || to == null) {
            return "Group not found.";
        }

        String fm = memberOf(from, fromMember);
        String tm = memberOf(to, toMember);
        if (fm == null) {
            return fromMember + " is not a member of " + from.getGroupName() + ".";
        }
        if (tm == null) {
            return toMember + " is not a member of " + to.getGroupName() + ".";
        }

        if (from == to && fm.equalsIgnoreCase(tm)) {
            return "You can't transfer to the same member in the same group.";
        }

        double available = getBalance(fromGroup, fm);
        if (Math.round(amount * 100) > Math.round(available * 100)) {
            return fm + " only has " + Home.pesoShort(available) + " in " + from.getGroupName() + ".";
        }
        return SUCCESS;
    }

    public String confirm() {
        String result = validate();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        GroupSavings from = findGroup(fromGroup);
        GroupSavings to = findGroup(toGroup);
        String fm = memberOf(from, fromMember);
        String tm = memberOf(to, toMember);
        double amount = parseAmount(amountText);
        LocalDate today = LocalDate.now();

        from.addContribution(fm, -amount, today, "Transferred to " + tm + " (" + to.getGroupName() + ")");
        to.addContribution(tm, amount, today, "Transferred from " + fm + " (" + from.getGroupName() + ")");

        home.refresh();   // weekly numbers change when the logged-in user is part of the transfer
        return SUCCESS;
    }

    // ---------------------------------------------------------------- helpers

    /** The dropdown placeholders count as "nothing chosen". */
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        String v = value.trim();
        if (v.equalsIgnoreCase("Select group") || v.equalsIgnoreCase("Select member")) {
            return "";
        }
        return v;
    }

    private GroupSavings findGroup(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (GroupSavings g : home.getGroups()) {
            if (g.getGroupName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }

    /** The member's name exactly as the group stores it, or null if not in the group. */
    private String memberOf(GroupSavings g, String name) {
        for (String m : g.getMembers()) {
            if (m.equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    /** "1,250.50" / "P250" -> 1250.5 / 250, rounded to centavos. Returns -1 if not a number. */
    private double parseAmount(String text) {
        if (text == null) {
            return -1;
        }
        String t = text.trim();
        if (t.startsWith("P") || t.startsWith("p") || t.startsWith("\u20B1")) {
            t = t.substring(1).trim();
        }

        String digits = "";
        for (int i = 0; i < t.length(); i++) {
            if (t.charAt(i) != ',') {
                digits += t.charAt(i);
            }
        }
        if (digits.isEmpty()) {
            return -1;
        }

        try {
            double value = Double.parseDouble(digits);
            if (value != value || value > 1000000000.0) {   // NaN or unrealistically large
                return -1;
            }
            return Math.round(value * 100) / 100.0;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}