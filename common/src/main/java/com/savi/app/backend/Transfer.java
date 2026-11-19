package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.savi.app.SAVI;

/**
 * Backend of TransferPage (Transfer Funds).
 *
 * Money can move between ANY two savings of the logged-in user:
 *   group -> group, group -> personal, personal -> group, personal -> personal.
 * The dropdowns list every savings as "Name (Group)" or "Name (Personal)".
 *
 * A transfer is saved as two contribution records (through GroupList / PersonalList, so they
 * are written to the CSV files too):
 *   sending side   : a NEGATIVE contribution  ("Transferred to ...")
 *   receiving side : a POSITIVE contribution  ("Transferred from ...")
 */
public class Transfer {
    public static final String SUCCESS = "success";

    private static final String GROUP_TAG = " (Group)";
    private static final String PERSONAL_TAG = " (Personal)";

    // ---------------------------------------------------------------- ENCAPSULATION
    private User user;
    private Home home;                    // gives us the groups of the logged-in user
    private String fromSavings = "";      // the dropdown text, e.g. "Gala sa December (Group)"
    private String fromMember = "";
    private String toSavings = "";
    private String toMember = "";
    private String amountText = "";

    public Transfer(User user, List<? extends Savings> allSavings) {
        this.user = user;
        // Home does the "which groups is the logged-in user in" work, so it is reused here
        this.home = new Home(user, allSavings);
    }

    // ---------------------------------------------------------------- ONE SAVINGS (group OR personal)
    // The page only talks about "savings"; this small class hides which kind it is, so
    // validate() and confirm() have almost no group/personal special cases.
    private static class Account {
        private final GroupSavings group;          // set when it is a group savings
        private final PersonalSavings personal;    // set when it is a personal savings

        Account(GroupSavings group, PersonalSavings personal) {
            this.group = group;
            this.personal = personal;
        }

        boolean isPersonal() {
            return personal != null;
        }

        /** "Gala sa December" */
        String name() {
            return group != null ? group.getGroupName() : personal.getSavingsName();
        }

        /** "Gala sa December (Group)" / "Emergency Fund (Personal)" */
        String label() {
            return name() + (group != null ? GROUP_TAG : PERSONAL_TAG);
        }

        /** Group: its members. Personal: only the owner. */
        List<String> members() {
            List<String> names = new ArrayList<String>();
            if (group != null) {
                names.addAll(group.getMembers());
            } else {
                names.add(personal.getOwner());
            }
            return names;
        }

        /** The member's name exactly as stored, or null if not a member of this savings. */
        String memberOf(String name) {
            for (String m : members()) {
                if (m.equalsIgnoreCase(name)) {
                    return m;
                }
            }
            return null;
        }

        /** What this member has put in (transfers included), never below 0. */
        double balance(String member) {
            double total = 0;
            List<Contribution> all = group != null ? group.getContributions() : personal.getContributions();
            for (Contribution c : all) {
                if (c.getMember().equalsIgnoreCase(member)) {
                    total += c.getAmount();
                }
            }
            return Math.max(0, total);
        }

        /** Records the contribution AND saves it to the CSV. */
        boolean add(String member, double amount, LocalDate date, String note) {
            if (group != null) {
                return SAVI.groupList.addContribution(group.getGroupName(), member, amount, date, note);
            }
            return SAVI.personalList.addContribution(personal.getSavingsName(), personal.getOwner(),
                    amount, date, note);
        }

        boolean sameAs(Account other) {
            return group != null ? group == other.group : personal == other.personal;
        }
    }

    // ---------------------------------------------------------------- FINDING SAVINGS

    /** Every savings the logged-in user can use: their groups, then their personal savings. */
    private List<Account> allAccounts() {
        List<Account> list = new ArrayList<Account>();
        for (GroupSavings g : home.getGroups()) {
            list.add(new Account(g, null));
        }
        String me = user == null ? null : user.getCurrentName();
        if (me != null) {
            for (PersonalSavings p : SAVI.personalList.getForOwner(me)) {
                list.add(new Account(null, p));
            }
        }
        return list;
    }

    private Account find(String label) {
        if (label == null || label.isEmpty()) {
            return null;
        }
        for (Account a : allAccounts()) {
            if (a.label().equalsIgnoreCase(label)) {
                return a;
            }
        }
        for (Account a : allAccounts()) {     // the text had no "(Group)" / "(Personal)" tag
            if (a.name().equalsIgnoreCase(label)) {
                return a;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- DROPDOWN CONTENTS

    /** For both "Select savings" dropdowns: all groups and all personal savings. */
    public List<String> getSavingsNames() {
        List<String> names = new ArrayList<String>();
        for (Account a : allAccounts()) {
            names.add(a.label());
        }
        return names;
    }

    /** For the "Select member" dropdown: the group's members, or just the owner of a personal savings. */
    public List<String> getMemberNames(String savings) {
        Account a = find(clean(savings));
        return a == null ? new ArrayList<String>() : a.members();
    }

    /** True for a personal savings: the page can skip the member choice (the owner is the only one). */
    public boolean isPersonal(String savings) {
        Account a = find(clean(savings));
        return a != null && a.isPersonal();
    }

    // ---------------------------------------------------------------- SETTERS
    // Choosing another savings clears the member, because the member list belongs to that savings.
    // For a personal savings the owner is picked automatically.

    public void setFromSavings(String savings) {
        this.fromSavings = clean(savings);
        this.fromMember = onlyMember(this.fromSavings);
    }

    public void setFromMember(String memberName) {
        this.fromMember = clean(memberName);
    }

    public void setToSavings(String savings) {
        this.toSavings = clean(savings);
        this.toMember = onlyMember(this.toSavings);
    }

    public void setToMember(String memberName) {
        this.toMember = clean(memberName);
    }

    /** The raw text of the Amount box ("250", "1,250.50" and "P250" are all accepted). */
    public void setAmount(String text) {
        this.amountText = text == null ? "" : text.trim();
    }

    // ---------------------------------------------------------------- GETTERS (confirmation page)

    /** The dropdown text, e.g. "Gala sa December (Group)". */
    public String getFromSavings() {
        return fromSavings;
    }

    public String getFromMember() {
        return fromMember;
    }

    public String getToSavings() {
        return toSavings;
    }

    public String getToMember() {
        return toMember;
    }

    /** The savings name without the tag, e.g. "Gala sa December" (for the confirmation text). */
    public String getFromName() {
        Account a = find(fromSavings);
        return a == null ? fromSavings : a.name();
    }

    public String getToName() {
        Account a = find(toSavings);
        return a == null ? toSavings : a.name();
    }

    /** The amount as a number, rounded to centavos. 0 if the text is not a valid amount. */
    public double getAmount() {
        double value = parseAmount(amountText);
        return value < 0 ? 0 : value;
    }

    // ---------------------------------------------------------------- VALIDATE / CONFIRM

    /** Returns "success" or the message to show to the user. Changes nothing. */
    public String validate() {
        if (fromSavings.isEmpty()) {
            return "Please choose the savings where the funds will come from.";
        }
        if (fromMember.isEmpty()) {
            return "Please choose the member who will send the funds.";
        }
        if (toSavings.isEmpty()) {
            return "Please choose the savings that will receive the funds.";
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

        Account from = find(fromSavings);
        Account to = find(toSavings);
        if (from == null || to == null) {
            return "Savings not found.";
        }

        String fm = from.memberOf(fromMember);
        String tm = to.memberOf(toMember);
        if (fm == null) {
            return fromMember + " is not a member of " + from.name() + ".";
        }
        if (tm == null) {
            return toMember + " is not a member of " + to.name() + ".";
        }

        if (from.sameAs(to) && fm.equalsIgnoreCase(tm)) {
            return "You can't transfer to the same savings member.";
        }

        // a personal savings belongs to one person: it may only exchange funds with that
        // person's own share in a group (nobody can move other members' money into it)
        if (from.isPersonal() != to.isPersonal()) {
            String groupSide = from.isPersonal() ? tm : fm;
            String owner = from.isPersonal() ? fm : tm;
            if (!groupSide.equalsIgnoreCase(owner)) {
                return "Personal savings can only exchange funds with your own share in a group.";
            }
        }

        double available = from.balance(fm);
        if (Math.round(amount * 100) > Math.round(available * 100)) {
            return fm + " only has " + Home.pesoShort(available) + " in " + from.name() + ".";
        }
        return SUCCESS;
    }

    /** Moves the money. Returns "success" or the same messages as validate(). */
    public String confirm() {
        String result = validate();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        Account from = find(fromSavings);
        Account to = find(toSavings);
        String fm = from.memberOf(fromMember);
        String tm = to.memberOf(toMember);
        double amount = parseAmount(amountText);
        LocalDate today = LocalDate.now();

        if (!from.add(fm, -amount, today, "Transferred to " + tm + " (" + to.name() + ")")) {
            return "The transfer could not be recorded.";
        }
        if (!to.add(tm, amount, today, "Transferred from " + fm + " (" + from.name() + ")")) {
            from.add(fm, amount, today, "Transfer reversed");   // put the money back
            return "The transfer could not be recorded.";
        }

        home.refresh();   // weekly numbers change when the logged-in user is part of the transfer
        return SUCCESS;
    }

    // ---------------------------------------------------------------- helpers

    /** For a personal savings: its owner (the only member). Otherwise "" (nothing chosen yet). */
    private String onlyMember(String savings) {
        Account a = find(savings);
        return (a != null && a.isPersonal()) ? a.members().get(0) : "";
    }

    /** The dropdown placeholders count as "nothing chosen". */
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        String v = value.trim();
        if (v.equalsIgnoreCase("Select savings") || v.equalsIgnoreCase("Select group")
                || v.equalsIgnoreCase("Select member")) {
            return "";
        }
        return v;
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