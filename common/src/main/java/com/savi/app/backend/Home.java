package com.savi.app.backend;

import com.savi.app.SAVI;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home {
    // ---------------------------------------------------------------- ENCAPSULATION
    private static final double DEFAULT_WEEKLY_TARGET = 5000;   // same value HomePage shows

    private User user = SAVI.user;                          // gives us the logged-in name
    private List<? extends Savings> allSavings;  // every savings object (group or personal)
    private LocalDate weekStart;                // first day (Sunday) of the week on the calendar
    private double weeklySaved;
    private double weeklyTarget;               // taken from the savings chosen on the weekly card (0 = not set)
    private double weeklyProgress;              // always between 0.0 and 1.0

    public static final String ALL_GROUPS = "All Savings";   // choice that adds up every savings' weekly target (groups + personal)
    private static final String PERSONAL_TAG = " (Personal)";
    private String weeklyChoice = "";           // what the weekly card shows ("" = the default choice)

    /** Every savings the logged-in user can see: all groups + the user's own personal savings.
     *  Pass this to new Home(...) so the home page and the Recent Contributions page show both. */
    public static List<Savings> allSavings() {
        List<Savings> all = new ArrayList<Savings>();
        all.addAll(SAVI.groupList.getGroups());
        String me = SAVI.user == null ? null : SAVI.user.getCurrentName();
        if (me != null) {
            all.addAll(SAVI.personalList.getForOwner(me));
        }
        return all;
    }

    /** Uses the week that today falls in. */
    public Home(User user, List<? extends Savings> allSavings) {
        this(user, allSavings, startOfWeek(LocalDate.now()));
    }

    public Home(User user, List<? extends Savings> allSavings, LocalDate weekStart) {
        // user may be null / logged out: the page then simply shows empty values
        if (allSavings == null) {
            throw new IllegalArgumentException("Savings list is required");
        }
        this.user = user;
        this.allSavings = allSavings;
        setWeekStart(weekStart);
    }

    // ---------------------------------------------------------------- SETTERS (validated)

    public void setWeekStart(LocalDate weekStart) {
        if (weekStart == null) {
            throw new IllegalArgumentException("Week start is required");
        }
        this.weekStart = weekStart;
        refresh();
    }

    public void setWeeklyTarget(double target) {
        if (target < 0) {
            throw new IllegalArgumentException("Target cannot be negative");
        }
        this.weeklyTarget = target;
        refresh();
    }

    /** Recomputes the weekly numbers. Call it again after a new contribution is added. */
    public void refresh() {
        String me = currentName();             // null if nobody is logged in
        double saved = 0;
        double target = 0;

        if (me != null) {
            String choice = getWeeklyChoice();
            GroupSavings g = findGroupChoice(choice);
            PersonalSavings p = g == null ? findPersonalChoice(choice) : null;

            if (choice.equals(ALL_GROUPS)) {
                // every weekly target added up (groups AND personal), and what this person put into all of them
                for (GroupSavings each : getGroups()) {
                    saved += weekTotal(each, me);
                    target += each.getWeeklyTarget();
                }
                for (PersonalSavings each : getPersonals()) {
                    saved += weekTotal(each, me);
                    target += each.getWeeklyTarget();
                }
            } else if (g != null) {
                saved = weekTotal(g, me);
                target = g.getWeeklyTarget();
            } else if (p != null) {
                saved = weekTotal(p, me);
                target = p.getWeeklyTarget();
            }
        }

        weeklySaved = saved;
        weeklyTarget = target;
        weeklyProgress = (weeklyTarget <= 0) ? 0 : Math.max(0, Math.min(1.0, weeklySaved / weeklyTarget));
    }

    /** What this person put into one savings during the week shown on the calendar. */
    private double weekTotal(Savings savings, String me) {
        double total = 0;
        for (int i = 0; i < 7; i++) {
            // the savings looks up its own records for that day
            for (Contribution c : savings.getContributionsOn(weekStart.plusDays(i))) {
                if (c.getMember().equalsIgnoreCase(me)) {
                    total += c.getAmount();
                }
            }
        }
        return total;
    }

    // ---------------------------------------------------------------- WEEKLY CARD CHOICE
    // The weekly card shows ONE of: "All Groups", a group, or a personal savings.

    /** The dropdown texts: "All Savings" (when there is any savings), each group, each personal savings. */
    public List<String> getWeeklyChoices() {
        List<String> choices = new ArrayList<String>();
        List<GroupSavings> groups = getGroups();
        if (!groups.isEmpty() || !getPersonals().isEmpty()) {
            choices.add(ALL_GROUPS);
        }
        for (GroupSavings g : groups) {
            choices.add(g.getGroupName());
        }
        for (PersonalSavings p : getPersonals()) {
            choices.add(p.getSavingsName() + PERSONAL_TAG);
        }
        return choices;
    }

    /** What the weekly card shows now: the picked one, else the first choice, else "" (no savings). */
    public String getWeeklyChoice() {
        String picked = matchChoice(weeklyChoice);
        if (picked != null) {
            return picked;
        }
        List<String> choices = getWeeklyChoices();
        return choices.isEmpty() ? "" : choices.get(0);
    }

    /** Picks what the weekly card shows (capitals don't matter). False if there is no such choice. */
    public boolean setWeeklyChoice(String choice) {
        String picked = matchChoice(choice);
        if (picked == null) {
            return false;
        }
        this.weeklyChoice = picked;
        refresh();
        return true;
    }

    /** "Weekly Target: P5,000", or "No weekly target set" when nothing was set. */
    public String getWeeklyTargetLabel() {
        return weeklyTarget > 0 ? "Weekly Target: " + pesoShort(weeklyTarget) : "No weekly target set";
    }

    // the official text of a choice, or null if it is not one of the choices
    private String matchChoice(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (String c : getWeeklyChoices()) {
            if (c.equalsIgnoreCase(text.trim())) {
                return c;
            }
        }
        return null;
    }

    private GroupSavings findGroupChoice(String choice) {
        for (GroupSavings g : getGroups()) {
            if (g.getGroupName().equalsIgnoreCase(choice)) {
                return g;
            }
        }
        return null;
    }

    private PersonalSavings findPersonalChoice(String choice) {
        for (PersonalSavings p : getPersonals()) {
            if ((p.getSavingsName() + PERSONAL_TAG).equalsIgnoreCase(choice)) {
                return p;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- THIS WEEK (calendar strip)
    // static on purpose: the calendar strip needs no logged-in user or savings data

    /** The Sunday of the week the given date falls in. */
    public static LocalDate startOfWeek(LocalDate date) {
        return date.minusDays(dayOfWeekIndex(date));   // Sunday = 0 days back
    }

    /** The 7 days (Sunday to Saturday) of the week that contains the given date. */
    public static List<LocalDate> getWeekDays(LocalDate anyDateInWeek) {
        LocalDate start = startOfWeek(anyDateInWeek);
        List<LocalDate> days = new ArrayList<LocalDate>();
        for (int i = 0; i < 7; i++) {
            days.add(start.plusDays(i));
        }
        return days;
    }

    /** The 7 days of the week this Home object is showing. */
    public List<LocalDate> getWeekDays() {
        return getWeekDays(weekStart);
    }

    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    /** "S", "M", "T", ... for the small letter under the day number. */
    public static String getDayLetter(LocalDate date) {
        String[] letters = {"S", "M", "T", "W", "T", "F", "S"};
        return letters[dayOfWeekIndex(date)];
    }

    /** "02" style day number for the day cell. */
    public static String getDayNumber(LocalDate date) {
        return String.valueOf(dayOfMonth(date));
    }

    /** "NOVEMBER", shown at the top right of the week card. */
    public static String getMonthName() {
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY",
                "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        return months[monthOf(LocalDate.now()) - 1];
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    // ---------------------------------------------------------------- AMOUNT SAVED CARD

    public double getWeeklySaved() {
        return weeklySaved;
    }

    public double getWeeklyTarget() {
        return weeklyTarget;
    }

    /** 0.0 to 1.0, used by the progress bar. */
    public double getWeeklyProgress() {
        return weeklyProgress;
    }

    /** 0 to 100, the "85%" label beside the weekly target. */
    public int getWeeklyPercent() {
        return (int) (weeklyProgress * 100);
    }

    // ---------------------------------------------------------------- GROUP OVERVIEW CARD

    /** The groups the logged-in user is a member of. */
    public List<GroupSavings> getGroups() {
        String me = currentName();
        List<GroupSavings> groups = new ArrayList<GroupSavings>();
        if (me == null) {
            return groups;
        }

        for (Savings s : allSavings) {
            // POLYMORPHISM: the list holds Savings; only GroupSavings are wanted here
            if (s instanceof GroupSavings) {
                GroupSavings g = (GroupSavings) s;
                if (isMember(g, me)) {
                    groups.add(g);
                }
            }
        }
        return groups;   // a new list, so callers can't change Home's data
    }

    /** The personal savings that belong to the logged-in user. */
    public List<PersonalSavings> getPersonals() {
        String me = currentName();
        List<PersonalSavings> personals = new ArrayList<PersonalSavings>();
        if (me == null) {
            return personals;
        }
        for (Savings s : allSavings) {
            if (s instanceof PersonalSavings) {
                PersonalSavings p = (PersonalSavings) s;
                if (p.getOwner() != null && p.getOwner().equalsIgnoreCase(me)) {
                    personals.add(p);
                }
            }
        }
        return personals;
    }

    /** The group shown on the home card: the one with the newest contribution. */
    public GroupSavings getFeaturedGroup() {
        GroupSavings featured = null;
        LocalDate newest = null;
        List<GroupSavings> groups = getGroups();

        for (GroupSavings g : groups) {
            for (Contribution c : g.getContributions()) {
                if (newest == null || isAfter(c.getDate(), newest)) {
                    newest = c.getDate();
                    featured = g;
                }
            }
        }

        if (featured == null && !groups.isEmpty()) {
            featured = groups.get(0);   // no contributions yet
        }
        return featured;   // null when the user has no groups: HomePage must handle this
    }

    /** TOTAL SAVED of a group. */
    public double getTotalSaved(GroupSavings g) {
        return g.getCurrentAmount();
    }

    /** What is left to reach the group's target (never below 0). */
    public double getRemainingBalance(GroupSavings g) {
        return Math.max(0, g.getTargetAmount() - g.getCurrentAmount());
    }

    // ---------------------------------------------------------------- RECENT CONTRIBUTIONS

    /** Records of the user's groups AND personal savings, newest first.
     *  limit <= 0 means no limit (See All page). */
    public List<RecentContribution> getRecentContributions(int limit) {
        List<RecentContribution> all = new ArrayList<RecentContribution>();
        for (GroupSavings g : getGroups()) {
            for (Contribution c : g.getContributions()) {
                all.add(new RecentContribution(g.getGroupName(), c, false));
            }
        }
        for (PersonalSavings p : getPersonals()) {
            for (Contribution c : p.getContributions()) {
                all.add(new RecentContribution(p.getSavingsName() + " (Personal)", c, true));
            }
        }

        Collections.reverse(all);   // so records added later come first on the same date
        // newest date first; on the same date, the one recorded last comes first.
        // (records without a saved time are 0, so they keep the order above)
        Collections.sort(all, (a, b) -> {
            int byDate = compareDates(b.getDate(), a.getDate());
            if (byDate != 0) {
                return byDate;
            }
            long ta = a.getTimestamp();
            long tb = b.getTimestamp();
            return ta < tb ? 1 : (ta > tb ? -1 : 0);
        });   // stable sort

        if (limit > 0 && all.size() > limit) {
            return new ArrayList<RecentContribution>(all.subList(0, limit));
        }
        return all;
    }

    // OVERLOADING: no limit given -> return everything
    public List<RecentContribution> getRecentContributions() {
        return getRecentContributions(0);
    }

    /** The single row shown on the home card, or null if there are no contributions yet. */
    public RecentContribution getLatestContribution() {
        List<RecentContribution> latest = getRecentContributions(1);
        return latest.isEmpty() ? null : latest.get(0);
    }

    // ---------------------------------------------------------------- helpers

    // ---------------------------------------------------------------- date helpers
    // Codename One's java.time only has a few methods (getDayOfWeek, getMonth, getDayOfMonth,
    // DayOfWeek and Month are NOT allowed), so these read the ISO text "2026-11-18" instead.

    /** ISO dates ("2026-11-18") sort correctly as plain text, so no LocalDate.compareTo/isAfter needed. */
    private static int compareDates(LocalDate a, LocalDate b) {
        return a.toString().compareTo(b.toString());
    }

    private static boolean isAfter(LocalDate a, LocalDate b) {
        return compareDates(a, b) > 0;
    }

    private static int monthOf(LocalDate date) {
        return Integer.parseInt(date.toString().substring(5, 7));
    }

    private static int dayOfMonth(LocalDate date) {
        return Integer.parseInt(date.toString().substring(8, 10));
    }

    /** 0 = Sunday, 1 = Monday ... 6 = Saturday (Sakamoto's algorithm). */
    private static int dayOfWeekIndex(LocalDate date) {
        String s = date.toString();
        int y = Integer.parseInt(s.substring(0, 4));
        int m = Integer.parseInt(s.substring(5, 7));
        int d = Integer.parseInt(s.substring(8, 10));
        int[] t = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
        if (m < 3) {
            y--;
        }
        return (y + y / 4 - y / 100 + y / 400 + t[m - 1] + d) % 7;
    }

    private String currentName() {
        return user == null ? null : user.getCurrentName();
    }

    private boolean isMember(GroupSavings g, String name) {
        for (String m : g.getMembers()) {
            if (m.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** 4256.5 -> "P4,256.50". (Codename One has no String.format or DecimalFormat.) */
    public static String pesoFormat(double amount) {
        long cents = Math.round(Math.abs(amount) * 100);
        long whole = cents / 100;
        long dec = cents % 100;

        return (amount < 0 ? "-P" : "P") + addCommas(whole) + "." + (dec < 10 ? "0" : "") + dec;
    }

    /** 12000 -> "P12,000", 4256.5 -> "P4,256.50". Drops ".00" like the design does. */
    public static String pesoShort(double amount) {
        long cents = Math.round(Math.abs(amount) * 100);
        if (cents % 100 == 0) {
            return (amount < 0 ? "-P" : "P") + addCommas(cents / 100);
        }
        return pesoFormat(amount);
    }

    private static String addCommas(long number) {
        String w = String.valueOf(number);
        String withCommas = "";
        for (int i = 0; i < w.length(); i++) {
            if (i > 0 && (w.length() - i) % 3 == 0) {
                withCommas += ",";
            }
            withCommas += w.charAt(i);
        }
        return withCommas;
    }

    // ---------------------------------------------------------------- one row of the Recent Contributions card
    // Contribution does not know which group it belongs to, so this wraps the two together
    // (composition) and hides the formatting details from the HomePage.
    public static class RecentContribution {
        private final String groupName;
        private final Contribution contribution;
        private final boolean personal;     // a personal saving: the member line says "You"

        private RecentContribution(String groupName, Contribution contribution, boolean personal) {
            this.groupName = groupName;
            this.contribution = contribution;
            this.personal = personal;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getMember() {
            return personal ? "You" : contribution.getMember();
        }

        public double getAmount() {
            return contribution.getAmount();
        }

        public String getNote() {
            return contribution.getNote() == null ? "" : contribution.getNote();
        }

        public LocalDate getDate() {
            return contribution.getDate();
        }

        /** When it was recorded: used to order contributions made on the same day. */
        public long getTimestamp() {
            return contribution.getTimestamp();
        }

        /** "Maria contributed P24" for the middle line of the card */
        public String getSummary() {
            return getMember() + " contributed " + pesoShort(getAmount());
        }

        /** "Note: Birthday fund" for the last line of the card */
        public String getNoteText() {
            return "Note: " + getNote();
        }

        /** "02" for the date badge */
        public String getDayNumber() {
            int d = dayOfMonth(contribution.getDate());
            return (d < 10 ? "0" : "") + d;
        }

        /** "Wednesday" for the date badge */
        public String getWeekday() {
            String[] names = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            return names[dayOfWeekIndex(contribution.getDate())];
        }
    }
}