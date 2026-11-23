package com.savi.app.backend;

import com.savi.app.SAVI;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home {
    // ---------------------------------------------------------------- ENCAPSULATION
    private User user;                                      // gives us the logged-in name
    private List<? extends Savings> allSavings;  // every savings object (group or personal)
    private LocalDate weekStart;                // first day (Sunday) of the week on the calendar
    private double weeklySaved;

    private String weeklyChoice = "";           // what the weekly card shows ("" = the default choice)

    /** Every savings the logged-in user can see: all groups + the user's own personal savings.
     *  Pass this to new Home(...) so the home page and the Recent Contributions page show both. */
    public static List<Savings> allSavings() {
        List<Savings> all = new ArrayList<Savings>();
        String me = (SAVI.user != null && SAVI.user.getCurrentName() != null)
                ? SAVI.user.getCurrentName().trim()
                : Profile.getInstance().getMemberName();
        if (me != null && !me.isEmpty()) {
            if (SAVI.groupList != null) {
                all.addAll(SAVI.groupList.getForUser(me));
            }
            if (SAVI.personalList != null) {
                all.addAll(SAVI.personalList.getForUser(me));
            }
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

    /** Recomputes the weekly numbers. Call it again after a new contribution is added. */
    public void refresh() {
        String me = currentName();             // null if nobody is logged in
        double saved = 0;

        if (me != null) {
            Savings chosen = findChoice(getWeeklyChoice());
            if (chosen != null) {
                saved = weekTotal(chosen);
            }
        }

        weeklySaved = saved;
    }

    /** Everything saved into one savings during the week shown on the calendar.
     *  Every member counts, not just the logged-in user. */
    private double weekTotal(Savings savings) {
        double total = 0;
        for (int i = 0; i < 7; i++) {
            for (Contribution c : savings.getContributionsOn(weekStart.plusDays(i))) {
                total += c.getAmount();
            }
        }
        return total;
    }

    // ---------------------------------------------------------------- WEEKLY CARD CHOICE

    /** The dropdown texts: each group, then each personal savings. */
    public List<String> getWeeklyChoices() {
        List<String> choices = new ArrayList<String>();
        for (Savings s : getVisibleSavings()) {
            choices.add(s.getChoiceLabel());      // each class decides its own label
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

    private Savings findChoice(String choice) {
        for (Savings s : getVisibleSavings()) {
            if (s.getChoiceLabel().equalsIgnoreCase(choice)) {
                return s;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- THIS WEEK (calendar strip)

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

    /** "NOVEMBER", shown at the top right of the week card (this month). */
    public static String getMonthName() {
        return getMonthName(LocalDate.now());
    }

    /** The month name of any date, so a card showing another week can show that week's month. */
    public static String getMonthName(LocalDate date) {
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY",
                "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        return months[monthOf(date) - 1];
    }

    // ---------------------------------------------------------------- AMOUNT SAVED CARD

    public double getWeeklySaved() {
        return weeklySaved;
    }

    // ---------------------------------------------------------------- GROUP OVERVIEW CARD

    /** Every savings (group + personal) the logged-in user can use, in the order they were given. */
    public List<Savings> getVisibleSavings() {
        String me = currentName();
        List<Savings> visible = new ArrayList<Savings>();
        if (me == null) {
            return visible;
        }
        for (Savings s : allSavings) {
            if (s.isOwnedBy(me)) {          // POLYMORPHISM: each kind decides who owns it
                visible.add(s);
            }
        }
        return visible;   // a new list, so callers can't change Home's data
    }

    /** The groups the logged-in user is a member of (typed list, because the group card needs GroupSavings). */
    public List<GroupSavings> getGroups() {
        List<GroupSavings> groups = new ArrayList<GroupSavings>();
        for (Savings s : getVisibleSavings()) {
            if (s instanceof GroupSavings) {
                groups.add((GroupSavings) s);
            }
        }
        return groups;
    }

    /** The personal savings that belong to the logged-in user. */
    public List<PersonalSavings> getPersonals() {
        List<PersonalSavings> personals = new ArrayList<PersonalSavings>();
        for (Savings s : getVisibleSavings()) {
            if (s instanceof PersonalSavings) {
                personals.add((PersonalSavings) s);
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

    // ---------------------------------------------------------------- SAVINGS OVERVIEW CARDS
    public static final int MAX_OVERVIEWS = 2;     // the home page never shows more cards than this

    /** How many savings the user has (groups + personal): the "2 Active" label. */
    public int getSavingsCount() {
        return getGroups().size() + getPersonals().size();
    }

    /** At most MAX_OVERVIEWS savings (groups or personal): the ones that received the latest contributions. */
    public List<OverviewItem> getOverviews() {
        List<OverviewItem> items = new ArrayList<OverviewItem>();
        for (Savings s : getVisibleSavings()) {
            items.add(new OverviewItem(s));
        }

        Collections.sort(items, (a, b) -> {
            int byDate = b.latest.compareTo(a.latest);
            if (byDate != 0) {
                return byDate;
            }
            return a.latestTime < b.latestTime ? 1 : (a.latestTime > b.latestTime ? -1 : 0);
        });

        if (items.size() > MAX_OVERVIEWS) {
            return new ArrayList<OverviewItem>(items.subList(0, MAX_OVERVIEWS));
        }
        return items;
    }

    public static class OverviewItem {
        private final Savings savings;
        private final String latest;
        private final long latestTime;

        private OverviewItem(Savings savings) {
            this.savings = savings;

            String newest = "";
            long newestTime = 0;
            for (Contribution c : savings.getContributions()) {
                String d = c.getDate().toString();
                int cmp = d.compareTo(newest);
                if (cmp > 0) {
                    newest = d;
                    newestTime = c.getTimestamp();
                } else if (cmp == 0 && c.getTimestamp() > newestTime) {
                    newestTime = c.getTimestamp();
                }
            }
            this.latest = newest;
            this.latestTime = newestTime;
        }

        public String getName() {
            return savings.getDisplayName();
        }

        public boolean isPersonal() {
            return savings.isPersonal();
        }

        /** "Group" or "Personal" (top right of the card). */
        public String getTypeLabel() {
            return savings.getTypeLabel();
        }

        public double getRemaining() {
            return Math.max(0, savings.getTargetAmount() - savings.getCurrentAmount());
        }

        /** "P12,450.00" */
        public String getTotalSavedText() {
            return pesoFormat(savings.getCurrentAmount());
        }

        /** "P350.00" */
        public String getRemainingText() {
            return pesoFormat(getRemaining());
        }

        /** "All Paid" when nothing is left, "Due Soon" when 10% or less of the target is left, else "". */
        public String getStatus() {
            if (Math.round(getRemaining() * 100) <= 0) {
                return "All Paid";
            }
            if (getRemaining() <= savings.getTargetAmount() * 0.10) {
                return "Due Soon";
            }
            return "";
        }
    }

    // ---------------------------------------------------------------- RECENT CONTRIBUTIONS

    /** Records of the user's groups AND personal savings, newest first. */
    public List<RecentContribution> getRecentContributions(int limit) {
        List<RecentContribution> all = new ArrayList<RecentContribution>();
        for (Savings s : getVisibleSavings()) {
            for (Contribution c : s.getContributions()) {
                all.add(new RecentContribution(s.getChoiceLabel(), c, s.isPersonal()));
            }
        }

        Collections.reverse(all);
        Collections.sort(all, (a, b) -> {
            int byDate = compareDates(b.getDate(), a.getDate());
            if (byDate != 0) {
                return byDate;
            }
            long ta = a.getTimestamp();
            long tb = b.getTimestamp();
            return ta < tb ? 1 : (ta > tb ? -1 : 0);
        });

        if (limit > 0 && all.size() > limit) {
            return new ArrayList<RecentContribution>(all.subList(0, limit));
        }
        return all;
    }

    public List<RecentContribution> getRecentContributions() {
        return getRecentContributions(0);
    }

    // ---------------------------------------------------------------- date helpers

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

    /** 4256.5 -> "P4,256.50". */
    public static String pesoFormat(double amount) {
        long cents = Math.round(Math.abs(amount) * 100);
        long whole = cents / 100;
        long dec = cents % 100;

        return (amount < 0 ? "-P" : "P") + addCommas(whole) + "." + (dec < 10 ? "0" : "") + dec;
    }

    /** 12000 -> "P12,000", 4256.5 -> "P4,256.50". */
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
    public static class RecentContribution {
        private final String groupName;
        private final Contribution contribution;
        private final boolean personal;

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

        public long getTimestamp() {
            return contribution.getTimestamp();
        }

        public String getNoteText() {
            return "Note: " + getNote();
        }

        public String getDayNumber() {
            int d = dayOfMonth(contribution.getDate());
            return (d < 10 ? "0" : "") + d;
        }

        public String getWeekday() {
            String[] names = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            return names[dayOfWeekIndex(contribution.getDate())];
        }
    }
}