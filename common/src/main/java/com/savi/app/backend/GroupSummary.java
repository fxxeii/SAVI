package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Backend of SummaryPage (Group Savings Summary).
 *
 *  SummaryPage part                         -> GroupSummary method
 *  ---------------------------------------------------------------------------
 *  purple card: group name + arrow          -> getGroupTitle(), getGroupNames(), selectGroup()
 *  purple card: Total Pool, Contributors    -> getTotalPoolText(), getContributorsText()
 *  donut chart + legend                     -> hasShares(), getShareValues(), getMembers(),
 *                                              getGoalLabel(), getGoalPercent(), getStatusText()
 *  Member Summary table                     -> getMembers()  (name, amount, share, "Paid Dec 12")
 *  monthly chart (W / M / Y)                -> setChartMode(), getChartTitle(), getChartSubtitle(),
 *                                              getChartLabels(), getChartValues(), getChartTip()
 *  chart footer                             -> isTargetAchieved(), getFooterText()
 *
 * A group "has shares" when its members have put money in (contributions that add up to more
 * than 0). When it has none, the page draws the charts in gray and shows NO_SHARES_MESSAGE.
 */
public class GroupSummary implements ChartData {
    public static final String NO_SHARES_MESSAGE = "No shares has been made for this group.";

    // chart modes (the W / M / Y toggles)
    public static final int WEEK = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;

    private static final int POINTS = 6;   // how many points the chart has
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    // ---------------------------------------------------------------- ENCAPSULATION
    private User user;
    private GroupSavings group;        // the group being shown (null if there are no groups)
    private List<GroupSavings> allGroups = new ArrayList<GroupSavings>();   // user-scoped groups, for the popup
    private int chartMode = MONTH;

    public GroupSummary(User user, List<GroupSavings> allGroups) {
        this.user = user;
        this.allGroups = new ArrayList<GroupSavings>();
        if (allGroups != null) {
            String currentUsername = (user != null) ? user.getCurrentName() : "";
            for (GroupSavings g : allGroups) {
                if (g != null && (currentUsername.isEmpty()
                        || g.getCreator().equalsIgnoreCase(currentUsername)
                        || g.hasMember(currentUsername))) {
                    this.allGroups.add(g);
                }
            }
        }
        this.group = findNewestGroup(this.allGroups);
    }

    // ---------------------------------------------------------------- GROUP SELECTION

    public List<String> getGroupNames() {
        List<String> names = new ArrayList<String>();
        for (GroupSavings g : allGroups) {
            names.add(g.getGroupName());
        }
        return names;
    }

    /** Switches to another group. Returns false if there is no such group. */
    public boolean selectGroup(String name) {
        if (name == null) {
            return false;
        }
        for (GroupSavings g : allGroups) {
            if (g.getGroupName().equalsIgnoreCase(name.trim())) {
                group = g;
                return true;
            }
        }
        return false;
    }

    public boolean hasGroup() {
        return group != null;
    }

    // ---------------------------------------------------------------- PURPLE POOL CARD

    /** "DECEMBER VACATION" */
    public String getGroupTitle() {
        return group == null ? "NO GROUP YET" : group.getGroupName().toUpperCase();
    }

    /** "P12,450.00" */
    public String getTotalPoolText() {
        return Home.pesoFormat(group == null ? 0 : group.getCurrentAmount());
    }

    /** "4 Contributors": members who have put money in. */
    public String getContributorsText() {
        int count = 0;
        for (MemberShare m : getMembers()) {
            if (m.amount > 0) {
                count++;
            }
        }
        return count + (count == 1 ? " Contributor" : " Contributors");
    }

    // ---------------------------------------------------------------- DONUT + MEMBER SUMMARY

    /** One row of the legend / Member Summary table. Biggest share first. */
    public List<MemberShare> getMembers() {
        List<MemberShare> rows = new ArrayList<MemberShare>();
        if (group == null) {
            return rows;
        }

        String me = user == null ? null : user.getCurrentName();
        List<String> members = group.getMembers();
        int n = members.size();
        double[] totals = new double[n];
        LocalDate[] lastPaid = new LocalDate[n];

        for (Contribution c : group.getContributions()) {
            for (int i = 0; i < n; i++) {
                if (members.get(i).equalsIgnoreCase(c.getMember())) {
                    totals[i] += c.getAmount();
                    if (lastPaid[i] == null || compareDates(c.getDate(), lastPaid[i]) > 0) {
                        lastPaid[i] = c.getDate();
                    }
                    break;
                }
            }
        }

        double sum = 0;
        for (int i = 0; i < n; i++) {
            if (totals[i] < 0) {
                totals[i] = 0;     // a member who sent everything away has no share
            }
            sum += totals[i];
        }

        for (int i = 0; i < n; i++) {
            boolean you = me != null && members.get(i).equalsIgnoreCase(me);
            double percent = sum > 0 ? totals[i] / sum * 100 : 0;
            rows.add(new MemberShare(members.get(i), you, totals[i], percent, lastPaid[i]));
        }

        // biggest share first; members with the same amount keep their order (stable sort)
        Collections.sort(rows, (a, b) -> a.amount < b.amount ? 1 : (a.amount > b.amount ? -1 : 0));
        return rows;
    }

    /** True when the members have put money in. False -> draw everything gray + the message. */
    public boolean hasShares() {
        for (MemberShare m : getMembers()) {
            if (m.amount > 0) {
                return true;
            }
        }
        return false;
    }

    /** Share of each member in percent (same order as getMembers()), for the donut slices. */
    public float[] getShareValues() {
        List<MemberShare> rows = getMembers();
        float[] values = new float[rows.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (float) rows.get(i).percent;
        }
        return values;
    }

    /** "GOAL MET" once the target is reached, otherwise "GOAL PROGRESS". */
    public String getGoalLabel() {
        return getGoalPercent() >= 100 ? "GOAL MET" : "GOAL PROGRESS";
    }

    /** 0 to 100: how much of the target amount is saved. */
    public int getGoalPercent() {
        if (group == null || group.getTargetAmount() <= 0) {
            return 0;
        }
        int p = (int) (group.getCurrentAmount() / group.getTargetAmount() * 100);
        return Math.max(0, Math.min(100, p));
    }

    /** "On Schedule" when no member has missed contributions, else "Member/s Behind Schedule". */
    public String getStatusText() {
        if (group == null) {
            return "On Schedule";
        }
        for (String member : group.getMembers()) {
            if (group.getTotalMissed(member) > 0) {
                return "Member/s Behind Schedule";
            }
        }
        return "On Schedule";
    }

    // ---------------------------------------------------------------- CHART (W / M / Y)

    public void setChartMode(int mode) {
        if (mode != WEEK && mode != MONTH && mode != YEAR) {
            throw new IllegalArgumentException("Unknown chart mode");
        }
        this.chartMode = mode;
    }

    public int getChartMode() {
        return chartMode;
    }

    public String getChartTitle() {
        if (chartMode == WEEK) {
            return "WEEKLY CHART";
        }
        return chartMode == YEAR ? "YEARLY CHART" : "MONTHLY CHART";
    }

    /** "Growth from Jul to Dec" */
    public String getChartSubtitle() {
        String[] labels = getChartLabels();
        if (chartMode == WEEK) {
            return "Growth over the last " + POINTS + " weeks";
        }
        return "Growth from " + labels[0] + " to " + labels[POINTS - 1];
    }

    /** The labels under the chart: "Jul" ... "Dec" (or week starts, or years). */
    public String[] getChartLabels() {
        String[] labels = new String[POINTS];
        long now = periodIndex(LocalDate.now());
        for (int k = 0; k < POINTS; k++) {
            labels[k] = periodLabel(now - (POINTS - 1) + k);
        }
        return labels;
    }

    /** Height of each chart point, 0.0 to 1.0 (the biggest total is 1.0). All 0 with no shares. */
    public float[] getChartValues() {
        float[] values = new float[POINTS];
        if (!hasShares()) {
            return values;
        }

        double[] totals = cumulative();
        double max = 0;
        for (int k = 0; k < POINTS; k++) {
            if (totals[k] > max) {
                max = totals[k];
            }
        }
        if (max <= 0) {
            return values;
        }
        for (int k = 0; k < POINTS; k++) {
            values[k] = (float) (Math.max(0, totals[k]) / max);
        }
        return values;
    }

    /** Message drawn on the gray chart when there are no shares. */
    public String getEmptyMessage() {
        return NO_SHARES_MESSAGE;
    }

    /** "P12.45k" for the bubble above the last point. */
    public String getChartTip() {
        double v = group == null ? 0 : group.getCurrentAmount();
        if (v >= 1000000) {
            return compact(v / 1000000.0, "M");
        }
        if (v >= 1000) {
            return compact(v / 1000.0, "k");
        }
        return Home.pesoShort(v);
    }

    // ---------------------------------------------------------------- CHART FOOTER

    public boolean isTargetAchieved() {
        return group != null && group.getTargetAmount() > 0
                && group.getCurrentAmount() >= group.getTargetAmount();
    }

    /** "Target Achieved" or "P40 of P5,000 saved". */
    public String getFooterText() {
        if (group == null) {
            return "No group selected";
        }
        if (isTargetAchieved()) {
            return "Target Achieved";
        }
        return Home.pesoShort(group.getCurrentAmount()) + " of "
                + Home.pesoShort(group.getTargetAmount()) + " saved";
    }

    // ---------------------------------------------------------------- helpers

    private static GroupSavings findNewestGroup(List<GroupSavings> groups) {
        if (groups == null || groups.isEmpty()) {
            return null;
        }
        GroupSavings newest = groups.get(0);
        long newestTs = -1;
        for (GroupSavings g : groups) {
            for (Contribution c : g.getContributions()) {
                if (c.getTimestamp() > newestTs) {
                    newestTs = c.getTimestamp();
                    newest = g;
                }
            }
        }
        return newest;
    }

    /** Pool total at the end of each of the last POINTS periods (money only ever adds up). */
    private double[] cumulative() {
        double[] totals = new double[POINTS];
        if (group == null) {
            return totals;
        }
        long now = periodIndex(LocalDate.now());
        for (Contribution c : group.getContributions()) {
            long idx = periodIndex(c.getDate());
            for (int k = 0; k < POINTS; k++) {
                if (idx <= now - (POINTS - 1) + k) {
                    totals[k] += c.getAmount();
                }
            }
        }
        return totals;
    }

    /** Which week / month / year a date belongs to, as one counting number. */
    private long periodIndex(LocalDate date) {
        int[] ymd = ymd(date);
        if (chartMode == YEAR) {
            return ymd[0];
        }
        if (chartMode == MONTH) {
            return ymd[0] * 12L + (ymd[1] - 1);
        }
        return (epochDay(date) + 4) / 7;      // weeks start on Sunday (1970-01-01 was a Thursday)
    }

    private String periodLabel(long period) {
        if (chartMode == YEAR) {
            return String.valueOf(period);
        }
        if (chartMode == MONTH) {
            return MONTHS[(int) (period % 12)];
        }
        int[] start = civil(period * 7 - 4);   // the Sunday that starts this week
        return MONTHS[start[1] - 1] + " " + start[2];
    }

    private String compact(double value, String suffix) {
        long hundredths = Math.round(value * 100);
        long whole = hundredths / 100;
        long dec = hundredths % 100;
        String s = "P" + whole;
        if (dec != 0) {
            if (dec < 10) {
                s += ".0" + dec;
            } else if (dec % 10 == 0) {
                s += "." + (dec / 10);
            } else {
                s += "." + dec;
            }
        }
        return s + suffix;
    }

    private static int compareDates(LocalDate a, LocalDate b) {
        return a.toString().compareTo(b.toString());
    }

    private static int[] ymd(LocalDate date) {
        String s = date.toString();
        return new int[]{Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(5, 7)),
                Integer.parseInt(s.substring(8, 10))};
    }

    /** Days since 1970-01-01. */
    private static long epochDay(LocalDate date) {
        int[] t = ymd(date);
        long y = t[0];
        long m = t[1];
        long d = t[2];
        if (m <= 2) {
            y--;
        }
        long era = (y >= 0 ? y : y - 399) / 400;
        long yoe = y - era * 400;
        long doy = (153 * (m + (m > 2 ? -3 : 9)) + 2) / 5 + d - 1;
        long doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
        return era * 146097 + doe - 719468;
    }

    /** The reverse: days since 1970-01-01 -> {year, month, day}. */
    private static int[] civil(long days) {
        long z = days + 719468;
        long era = (z >= 0 ? z : z - 146096) / 146097;
        long doe = z - era * 146097;
        long yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365;
        long y = yoe + era * 400;
        long doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
        long mp = (5 * doy + 2) / 153;
        long d = doy - (153 * mp + 2) / 5 + 1;
        long m = mp < 10 ? mp + 3 : mp - 9;
        if (m <= 2) {
            y++;
        }
        return new int[]{(int) y, (int) m, (int) d};
    }

    /** "Dec 08" */
    private static String shortDate(LocalDate date) {
        int[] t = ymd(date);
        return MONTHS[t[1] - 1] + " " + (t[2] < 10 ? "0" : "") + t[2];
    }

    // ---------------------------------------------------------------- one row of the legend / table
    public static class MemberShare {
        private final String name;
        private final boolean you;
        private final double amount;
        private final double percent;
        private final LocalDate lastPaid;

        private MemberShare(String name, boolean you, double amount, double percent, LocalDate lastPaid) {
            this.name = name;
            this.you = you;
            this.amount = amount;
            this.percent = percent;
            this.lastPaid = lastPaid;
        }

        public boolean isYou() {
            return you;
        }

        /** "Faye" or "Alex (You)" */
        public String getDisplayName() {
            return you ? name + " (You)" : name;
        }

        /** "F" (the table shows "You" for the logged-in user instead) */
        public String getInitial() {
            return name.isEmpty() ? "" : name.substring(0, 1).toUpperCase();
        }

        /** "P2,490" */
        public String getAmountText() {
            return Home.pesoShort(amount);
        }

        /** 20 */
        public int getPercent() {
            return (int) Math.round(percent);
        }

        /** "20%" */
        public String getPercentText() {
            return getPercent() + "%";
        }

        /** "Paid Dec 12" or "No payment yet" */
        public String getPaidText() {
            return lastPaid == null ? "No payment yet" : "Paid " + shortDate(lastPaid);
        }

        /** "Alex (You) \u2022 40%" for the legend under the donut */
        public String getLegendText() {
            return getDisplayName() + " \u2022 " + getPercentText();
        }
    }
}