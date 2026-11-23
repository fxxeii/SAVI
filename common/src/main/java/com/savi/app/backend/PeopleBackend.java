package com.savi.app.backend;

import com.savi.app.SAVI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** People-page service. All group, personal, member and transaction data comes from GroupList & PersonalList. */
public class PeopleBackend {
    private static boolean personalMode = false;
    private static String selectedGroupName;
    private static String selectedPersonalName;
    private static final String[] MONTHS = {
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };

    public static class Member {
        public final String name, role, handle, group;
        public final boolean active, personal;
        public final double totalPaid, missed, goal;
        public Member(String name, String role, String handle, String group, boolean active,
                      double totalPaid, double missed, double goal) {
            this(name, role, handle, group, active, false, totalPaid, missed, goal);
        }
        public Member(String name, String role, String handle, String group, boolean active, boolean personal,
                      double totalPaid, double missed, double goal) {
            this.name = name; this.role = role; this.handle = handle; this.group = group;
            this.active = active; this.personal = personal;
            this.totalPaid = totalPaid; this.missed = missed; this.goal = goal;
        }
    }

    public static class Txn {
        public final String type, title, date, month;
        public final double amount;
        public final boolean settled;
        public Txn(String type, String title, String date, String month, double amount, boolean settled) {
            this.type = type; this.title = title; this.date = date; this.month = month;
            this.amount = amount; this.settled = settled;
        }
    }

    public static boolean isPersonalMode() {
        return personalMode;
    }

    public static void setPersonalMode(boolean personal) {
        personalMode = personal;
    }

    public static void setSelectedGroup(String groupName) {
        selectedGroupName = groupName == null || groupName.trim().isEmpty() ? null : groupName.trim();
    }

    public static void setSelectedPersonal(String savingsName) {
        selectedPersonalName = savingsName == null || savingsName.trim().isEmpty() ? null : savingsName.trim();
    }

    private static String currentUserName() {
        if (SAVI.user != null && SAVI.user.getCurrentName() != null
                && !SAVI.user.getCurrentName().trim().isEmpty()) {
            return SAVI.user.getCurrentName().trim();
        }
        return Profile.getInstance().getMemberName();
    }

    private static List<GroupSavings> userGroups() {
        if (SAVI.groupList == null) return new ArrayList<>();
        return SAVI.groupList.getForUser(currentUserName());
    }

    private static List<PersonalSavings> userPersonals() {
        if (SAVI.personalList == null) return new ArrayList<>();
        return SAVI.personalList.getForUser(currentUserName());
    }

    public static String[] getUserGroupNames() {
        List<GroupSavings> groups = userGroups();
        String[] names = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) names[i] = groups.get(i).getGroupName();
        return names;
    }

    public static String[] getTrackerNames() {
        if (personalMode) {
            List<PersonalSavings> personals = userPersonals();
            String[] names = new String[personals.size()];
            for (int i = 0; i < personals.size(); i++) names[i] = personals.get(i).getSavingsName();
            return names;
        }
        return getUserGroupNames();
    }

    private static GroupSavings currentGroup() {
        List<GroupSavings> groups = userGroups();
        if (groups.isEmpty()) return null;
        if (selectedGroupName != null) {
            for (GroupSavings group : groups) {
                if (group.getGroupName().equalsIgnoreCase(selectedGroupName)) return group;
            }
        }
        GroupSavings latest = groups.get(groups.size() - 1);
        selectedGroupName = latest.getGroupName();
        return latest;
    }

    private static PersonalSavings currentPersonal() {
        List<PersonalSavings> personals = userPersonals();
        if (personals.isEmpty()) return null;
        if (selectedPersonalName != null) {
            for (PersonalSavings p : personals) {
                if (p.getSavingsName().equalsIgnoreCase(selectedPersonalName)) return p;
            }
        }
        PersonalSavings latest = personals.get(personals.size() - 1);
        selectedPersonalName = latest.getSavingsName();
        return latest;
    }

    public static String getGroupName() {
        if (personalMode) {
            PersonalSavings p = currentPersonal();
            return p == null ? "No Personal Savings" : p.getSavingsName();
        }
        GroupSavings group = currentGroup();
        return group == null ? "No Group Yet" : group.getGroupName();
    }

    public static double getGroupPool() {
        if (personalMode) {
            PersonalSavings p = currentPersonal();
            return p == null ? 0.0 : p.getCurrentAmount();
        }
        GroupSavings group = currentGroup();
        return group == null ? 0.0 : group.getCurrentAmount();
    }

    public static double getTargetAmount() {
        if (personalMode) {
            PersonalSavings p = currentPersonal();
            return p == null ? 0.0 : p.getTargetAmount();
        }
        GroupSavings group = currentGroup();
        return group == null ? 0.0 : group.getTargetAmount();
    }

    public static Member[] getMembers() {
        if (personalMode) {
            PersonalSavings p = currentPersonal();
            if (p == null) return new Member[0];
            String me = currentUserName();
            double paid = Math.max(0.0, p.getCurrentAmount());
            double missed = p.getTotalMissed();
            double goal = p.getTargetAmount() > 0 ? p.getTargetAmount() : 1.0;
            return new Member[]{
                    new Member("You", "Owner", toHandle(me), p.getSavingsName(), paid > 0, true, paid, missed, goal)
            };
        }

        GroupSavings group = currentGroup();
        if (group == null) return new Member[0];
        List<String> names = group.getMembers();
        List<Contribution> contributions = group.getContributions();
        Member[] result = new Member[names.size()];
        double expected = group.getExpectedPerMember();
        double goal = expected > 0 ? expected : group.getTargetAmount();

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            double paid = 0;
            for (Contribution contribution : contributions) {
                if (contribution.getMember().equalsIgnoreCase(name)) {
                    paid += contribution.getAmount();
                }
            }
            boolean admin = group.getCreator().equalsIgnoreCase(name);
            double totalMissed = group.getTotalMissed(name);

            result[i] = new Member(
                    name,
                    admin ? "Admin" : "Member",
                    toHandle(name),
                    group.getGroupName(),
                    paid > 0,
                    false,
                    paid,
                    totalMissed,
                    goal > 0 ? goal : 1.0
            );
        }
        return result;
    }

    public static Txn[] getTransactions(Member member) {
        if (member == null || member.name == null) return new Txn[0];

        if (member.personal || personalMode) {
            PersonalSavings p = currentPersonal();
            if (p == null) return new Txn[0];
            List<Txn> result = new ArrayList<>();
            List<Contribution> contributions = p.getContributions();
            for (int i = contributions.size() - 1; i >= 0; i--) {
                Contribution contribution = contributions.get(i);
                LocalDate date = contribution.getDate();
                String title = contribution.getNote() == null || contribution.getNote().trim().isEmpty()
                        ? "Personal Deposit" : contribution.getNote().trim();
                result.add(new Txn(contribution.getAmount() < 0 ? "WITHDRAWAL" : "DEPOSIT", title,
                        formatDate(date), formatMonth(date), contribution.getAmount(), true));
            }
            if (member.missed > 0) {
                LocalDate today = LocalDate.now();
                result.add(new Txn("MISSED", "Remaining Target", formatDate(today), formatMonth(today),
                        member.missed, false));
            }
            return result.toArray(new Txn[result.size()]);
        }

        GroupSavings group = currentGroup();
        if (group == null) return new Txn[0];
        List<Txn> result = new ArrayList<>();
        List<Contribution> contributions = group.getContributions();
        for (int i = contributions.size() - 1; i >= 0; i--) {
            Contribution contribution = contributions.get(i);
            if (!contribution.getMember().equalsIgnoreCase(member.name)) continue;
            LocalDate date = contribution.getDate();
            String title = contribution.getNote() == null || contribution.getNote().trim().isEmpty()
                    ? "Group Deposit" : contribution.getNote().trim();
            result.add(new Txn(contribution.getAmount() < 0 ? "TRANSFER" : "DEPOSIT", title,
                    formatDate(date), formatMonth(date), contribution.getAmount(), true));
        }
        if (member.missed > 0) {
            LocalDate today = LocalDate.now();
            result.add(new Txn("MISSED", "Remaining Share", formatDate(today), formatMonth(today),
                    member.missed, false));
        }
        return result.toArray(new Txn[result.size()]);
    }

    private static String toHandle(String name) {
        return name == null || name.trim().isEmpty() ? "member" : name.trim().toLowerCase().replace(' ', '.');
    }

    private static String formatDate(LocalDate date) {
        if (date == null) return "";
        String month = MONTHS[date.getMonthValue() - 1];
        return month.substring(0, 1) + month.substring(1, 3).toLowerCase()
                + " " + date.getDayOfMonth() + ", " + date.getYear();
    }

    private static String formatMonth(LocalDate date) {
        return date == null ? "" : MONTHS[date.getMonthValue() - 1] + " " + date.getYear();
    }
}