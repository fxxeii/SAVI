package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend of PersonalSummaryPage (Personal Savings GroupSummary).
 *
 *  PersonalSummaryPage part                 -> PersonalSummary method
 *  ---------------------------------------------------------------------------
 *  purple card: name + arrow                -> getTitle(), getNames(), select()
 *  purple card: Total Pool, Target Amount   -> getTotalPoolText(), getTargetText()
 *  monthly chart (W / M / Y) + footer       -> the ChartData methods
 */
public class PersonalSummary implements ChartData {
    public static final String NO_SAVINGS_MESSAGE = "Nothing has been saved here yet.";

    private static final int POINTS = 6;
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    // ------------------------------------------------------------ ADAPT
    // The ONLY 4 lines that touch your personal savings class. If PersonalSavings uses other
    // method names, change them here and nothing else needs to change.
    private static String nameOf(PersonalSavings p) { return p.getSavingsName(); }
    private static double savedOf(PersonalSavings p) { return p.getCurrentAmount(); }
    private static double targetOf(PersonalSavings p) { return p.getTargetAmount(); }
    private static Iterable<Contribution> contributionsOf(PersonalSavings p) { return p.getContributions(); }
    // -----------------------------------------------------------------

    private final List<PersonalSavings> all = new ArrayList<PersonalSavings>();
    private PersonalSavings current;
    private int chartMode = GroupSummary.MONTH;

    public PersonalSummary(List<PersonalSavings> personalSavings) {
        all.addAll(personalSavings);
        if (!all.isEmpty()) {
            current = all.get(0);
        }
    }

    // ---------------------------------------------------------------- SELECTION

    public List<String> getNames() {
        List<String> names = new ArrayList<String>();
        for (PersonalSavings p : all) {
            names.add(nameOf(p));
        }
        return names;
    }

    /** Switches to another personal saving. Returns false if there is no such one. */
    public boolean select(String name) {
        if (name == null) {
            return false;
        }
        for (PersonalSavings p : all) {
            if (nameOf(p).equalsIgnoreCase(name.trim())) {
                current = p;
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- PURPLE CARD

    /** "LAPTOP" */
    public String getTitle() {
        return current == null ? "NO SAVINGS YET" : nameOf(current).toUpperCase();
    }

    /** "P12,450.00" */
    public String getTotalPoolText() {
        return Home.pesoFormat(current == null ? 0 : savedOf(current));
    }

    /** "P15,450.00" */
    public String getTargetText() {
        return Home.pesoFormat(current == null ? 0 : targetOf(current));
    }

    // ---------------------------------------------------------------- CHART (W / M / Y)

    @Override
    public void setChartMode(int mode) {
        if (mode != GroupSummary.WEEK && mode != GroupSummary.MONTH && mode != GroupSummary.YEAR) {
            throw new IllegalArgumentException("Unknown chart mode");
        }
        this.chartMode = mode;
    }

    @Override
    public int getChartMode() {
        return chartMode;
    }

    @Override
    public String getChartTitle() {
        if (chartMode == GroupSummary.WEEK) {
            return "WEEKLY CHART";
        }
        return chartMode == GroupSummary.YEAR ? "YEARLY CHART" : "MONTHLY CHART";
    }

    @Override
    public String getChartSubtitle() {
        String[] labels = getChartLabels();
        if (chartMode == GroupSummary.WEEK) {
            return "Growth over the last " + POINTS + " weeks";
        }
        return "Growth from " + labels[0] + " to " + labels[POINTS - 1];
    }

    @Override
    public String[] getChartLabels() {
        String[] labels = new String[POINTS];
        long now = periodIndex(LocalDate.now());
        for (int k = 0; k < POINTS; k++) {
            labels[k] = periodLabel(now - (POINTS - 1) + k);
        }
        return labels;
    }

    /** True when something has been saved. False -> the page draws the chart in gray. */
    @Override
    public boolean hasShares() {
        return current != null && savedOf(current) > 0;
    }

    /** Height of each chart point, 0.0 to 1.0 (the biggest total is 1.0). All 0 when empty. */
    @Override
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

    /** "P12.45k" for the bubble above the last point. */
    @Override
    public String getChartTip() {
        double v = current == null ? 0 : savedOf(current);
        if (v >= 1000000) {
            return compact(v / 1000000.0, "M");
        }
        if (v >= 1000) {
            return compact(v / 1000.0, "k");
        }
        return Home.pesoShort(v);
    }

    @Override
    public String getEmptyMessage() {
        return NO_SAVINGS_MESSAGE;
    }

    // ---------------------------------------------------------------- CHART FOOTER

    @Override
    public boolean isTargetAchieved() {
        return current != null && targetOf(current) > 0 && savedOf(current) >= targetOf(current);
    }

    /** "Laptop Target Achieved" or "P40 of P5,000 saved". */
    @Override
    public String getFooterText() {
        if (current == null) {
            return "No savings selected";
        }
        if (isTargetAchieved()) {
            return nameOf(current) + " Target Achieved";
        }
        return Home.pesoShort(savedOf(current)) + " of " + Home.pesoShort(targetOf(current)) + " saved";
    }

    // ---------------------------------------------------------------- helpers
    // (same date math as GroupSummary: Codename One's java.time is small, so dates are read from text)

    private double[] cumulative() {
        double[] totals = new double[POINTS];
        if (current == null) {
            return totals;
        }
        long now = periodIndex(LocalDate.now());
        for (Contribution c : contributionsOf(current)) {
            long idx = periodIndex(c.getDate());
            for (int k = 0; k < POINTS; k++) {
                if (idx <= now - (POINTS - 1) + k) {
                    totals[k] += c.getAmount();
                }
            }
        }
        return totals;
    }

    private long periodIndex(LocalDate date) {
        int[] ymd = ymd(date);
        if (chartMode == GroupSummary.YEAR) {
            return ymd[0];
        }
        if (chartMode == GroupSummary.MONTH) {
            return ymd[0] * 12L + (ymd[1] - 1);
        }
        return (epochDay(date) + 4) / 7;      // weeks start on Sunday
    }

    private String periodLabel(long period) {
        if (chartMode == GroupSummary.YEAR) {
            return String.valueOf(period);
        }
        if (chartMode == GroupSummary.MONTH) {
            return MONTHS[(int) (period % 12)];
        }
        int[] start = civil(period * 7 - 4);
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

    private static int[] ymd(LocalDate date) {
        String s = date.toString();
        return new int[]{Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(5, 7)),
                Integer.parseInt(s.substring(8, 10))};
    }

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
}