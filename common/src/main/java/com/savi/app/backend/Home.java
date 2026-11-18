//package com.savi.app.backend;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class Home {
//    //Don't remove Comments yet para madali aralin
//    private static final double DEFAULT_WEEKLY_TARGET = 5000;   // same value the HomePage shows
//
//    // ---------------------------------------------------------------- ENCAPSULATION
//    // everything is private; it only changes through validated setters
//    private User user;                          // gives us the logged-in name
//    private ArrayList<Savings> allSavings;      // every savings object (group or personal)
//    private LocalDate weekStart;                // first day (Sunday) of the week on the calendar
//    private double weeklySaved;
//    private double weeklyTarget = DEFAULT_WEEKLY_TARGET;
//    private double weeklyProgress;              // always between 0.0 and 1.0
//
//    public Home(User user, ArrayList<Savings> allSavings, LocalDate weekStart) {
//        if (user == null || user.getCurrentName() == null) {
//            throw new IllegalStateException("Nobody is logged in");
//        }
//        if (allSavings == null) {
//            throw new IllegalArgumentException("Savings list is required");
//        }
//        this.user = user;
//        this.allSavings = allSavings;
//        setWeekStart(weekStart);
//    }
//
//    public void setWeekStart(LocalDate weekStart) {
//        if (weekStart == null) {
//            throw new IllegalArgumentException("Week start is required");
//        }
//        this.weekStart = weekStart;
//        refresh();
//    }
//
//    public void setWeeklyTarget(double target) {
//        if (target < 0) {
//            throw new IllegalArgumentException("Target cannot be negative");
//        }
//        this.weeklyTarget = target;
//        refresh();
//    }
//
//    /** Recomputes the weekly numbers. Call it again after a new contribution is added. */
//    public void refresh() {
//        String me = user.getCurrentName();
//        double total = 0;
//
//        for (GroupSavings g : getGroups()) {
//            for (int i = 0; i < 7; i++) {
//                // the group looks up its own records for that day
//                for (Contribution c : g.getContributionsOn(weekStart.plusDays(i))) {
//                    if (c.getMember().equalsIgnoreCase(me)) {
//                        total += c.getAmount();
//                    }
//                }
//            }
//        }
//
//        weeklySaved = total;
//        weeklyProgress = (weeklyTarget <= 0) ? 0 : Math.min(1.0, weeklySaved / weeklyTarget);
//    }
//
//    // ---------------------------------------------------------------- THIS WEEK / AMOUNT SAVED
//    public double getWeeklySaved() {
//        return weeklySaved;
//    }
//
//    public double getWeeklyTarget() {
//        return weeklyTarget;
//    }
//
//    /** 0.0 to 1.0, used by the progress bar. Multiply by 100 for the percentage. */
//    public double getWeeklyProgress() {
//        return weeklyProgress;
//    }
//
//    // ---------------------------------------------------------------- GROUP OVERVIEW CARD
//
//    /** The groups the logged-in user is a member of. */
//    public List<GroupSavings> getGroups() {
//        String me = user.getCurrentName();
//        List<GroupSavings> groups = new ArrayList<GroupSavings>();
//
//        for (Savings s : allSavings) {
//            // POLYMORPHISM: the list holds Savings; only GroupSavings are wanted here
//            if (s instanceof GroupSavings) {
//                GroupSavings g = (GroupSavings) s;
//                if (isMember(g, me)) {
//                    groups.add(g);
//                }
//            }
//        }
//        return groups;   // a copy, so callers can't change Home's data
//    }
//
//    /** The group shown on the home card: the one with the newest contribution. */
//    public GroupSavings getFeaturedGroup() {
//        GroupSavings featured = null;
//        LocalDate newest = null;
//
//        for (GroupSavings g : getGroups()) {
//            for (Contribution c : g.getContributions()) {
//                if (newest == null || c.getDate().isAfter(newest)) {
//                    newest = c.getDate();
//                    featured = g;
//                }
//            }
//        }
//
//        if (featured == null) {
//            List<GroupSavings> groups = getGroups();
//            featured = groups.isEmpty() ? null : groups.get(0);   // no contributions yet
//        }
//        return featured;   // null when the user has no groups: HomePage must handle this
//    }
//
//    /** What is left to reach the group's target (never below 0). */
//    public double getRemainingBalance(GroupSavings g) {
//        return Math.max(0, g.getTargetAmount() - g.getCurrentAmount());
//    }
//
//    // ---------------------------------------------------------------- RECENT CONTRIBUTIONS
//
//    /** Records of the user's groups, newest first. limit <= 0 means no limit (See All page). */
//    public List<RecentContribution> getRecentContributions(int limit) {
//        List<RecentContribution> all = new ArrayList<RecentContribution>();
//        for (GroupSavings g : getGroups()) {
//            for (Contribution c : g.getContributions()) {
//                all.add(new RecentContribution(g.getGroupName(), c));
//            }
//        }
//
//        Collections.reverse(all);   // so records added later come first on the same date
//        Collections.sort(all, (a, b) -> b.getDate().compareTo(a.getDate()));
//
//        if (limit > 0 && all.size() > limit) {
//            return new ArrayList<RecentContribution>(all.subList(0, limit));
//        }
//        return all;
//    }
//
//    // OVERLOADING: no limit given -> return everything
//    public List<RecentContribution> getRecentContributions() {
//        return getRecentContributions(0);
//    }
//
//    // ---------------------------------------------------------------- helpers
//
//    private boolean isMember(GroupSavings g, String name) {
//        for (String m : g.getMembers()) {
//            if (m.equalsIgnoreCase(name)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /** 4256.5 -> "P4,256.50". (Codename One has no String.format or DecimalFormat.) */
//    public static String pesoFormat(double amount) {
//        long cents = Math.round(Math.abs(amount) * 100);
//        long whole = cents / 100;
//        long dec = cents % 100;
//
//        String w = String.valueOf(whole);
//        String withCommas = "";
//        for (int i = 0; i < w.length(); i++) {
//            if (i > 0 && (w.length() - i) % 3 == 0) {
//                withCommas += ",";
//            }
//            withCommas += w.charAt(i);
//        }
//        return (amount < 0 ? "-P" : "P") + withCommas + "." + (dec < 10 ? "0" : "") + dec;
//    }
//
//    // ---------------------------------------------------------------- one row of the Recent Contributions card
//    // Contribution does not know which group it belongs to, so this wraps the two together
//    // (composition) and hides the formatting details from the HomePage.
//    public static class RecentContribution {
//        private final String groupName;
//        private final Contribution contribution;
//
//        private RecentContribution(String groupName, Contribution contribution) {
//            this.groupName = groupName;
//            this.contribution = contribution;
//        }
//
//        public String getGroupName(){
//            return groupName;
//        }
//
//        public String getMember(){
//            return contribution.getMember();
//        }
//
//        public double getAmount(){
//            return contribution.getAmount();
//        }
//        public String getNote(){
//            return contribution.getNote() == null ? "" : contribution.getNote();
//        }
//        public LocalDate getDate(){
//            return contribution.getDate();
//        }
//
//        /** "02" for the date badge */
//        public String getDayNumber() {
//            int d = contribution.getDate().getDayOfMonth();
//            return (d < 10 ? "0" : "") + d;
//        }
//
//        /** "Wednesday" for the date badge */
//        public String getWeekday() {
//            String w = contribution.getDate().getDayOfWeek().toString();
//            return w.charAt(0) + w.substring(1).toLowerCase();
//        }
//    }
//}