package com.savi.app.backend;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Util;
import com.codename1.ui.Display;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Reads Groups_Records.csv and gives the People page and the View All Transactions page their data.
 *
 * CSV format (no header row):
 *   G,owner,goal,groupName,dailyAmount,dateCreated
 *   M,groupName,memberName
 *   C,groupName,memberName,amount,date,note
 */
public class PeopleBackend {

    static final String FILE_NAME = "Groups_Records.csv";

    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    // ---------- models used by the pages ----------

    public static class Member {
        public final String name, handle, role, group;
        public final boolean active;
        public final double totalPaid, missed, goal;

        public Member(String name, String handle, String role, String group, boolean active,
                      double totalPaid, double missed, double goal) {
            this.name = name;
            this.handle = handle;
            this.role = role;
            this.group = group;
            this.active = active;
            this.totalPaid = totalPaid;
            this.missed = missed;
            this.goal = goal;
        }
    }

    public static class Txn {
        public final String title, date, month, type; // type: DEPOSIT, MISSED, ADJUSTMENT
        public final double amount;
        public final boolean settled;

        public Txn(String title, String date, String month, String type, double amount, boolean settled) {
            this.title = title;
            this.date = date;
            this.month = month;
            this.type = type;
            this.amount = amount;
            this.settled = settled;
        }
    }

    // ---------- raw records from the csv ----------

    private static class Group {
        String owner, name, created;
        double goal, daily;
    }

    private static class Contribution {
        String member, date, note;
        double amount;
    }

    private static class Records {
        Group group;
        List<String> members = new ArrayList<String>();
        List<Contribution> contributions = new ArrayList<Contribution>();
    }

    // ---------- public api ----------

    public static String getGroupName() {
        Records r = load();
        return r.group == null ? "No Group" : r.group.name;
    }

    // total money collected so far
    public static double getGroupPool() {
        Records r = load();
        double sum = 0;
        for (Contribution c : r.contributions) {
            sum += c.amount;
        }
        return sum;
    }

    public static Member[] getMembers() {
        Records r = load();
        if (r.group == null) {
            return new Member[0];
        }
        int days = daysSince(r.group.created);
        double expected = r.group.daily * days;
        double goalPerMember = r.members.size() == 0 ? r.group.goal : r.group.goal / r.members.size();

        Member[] out = new Member[r.members.size()];
        for (int i = 0; i < out.length; i++) {
            String name = r.members.get(i);
            double paid = paidBy(r, name);
            double missed = Math.max(0, expected - paid);
            out[i] = new Member(capitalize(name), name.toLowerCase(), "Member", r.group.name, true,
                    paid, missed, goalPerMember);
        }
        return out;
    }

    // newest first: contributions from the csv, plus a missed entry when the member is behind
    public static Txn[] getTransactions(Member m) {
        Records r = load();
        List<Contribution> mine = new ArrayList<Contribution>();
        for (Contribution c : r.contributions) {
            if (c.member.equalsIgnoreCase(m.name)) {
                mine.add(c);
            }
        }
        // sort by date, newest first (iso dates sort correctly as text)
        for (int i = 1; i < mine.size(); i++) {
            Contribution key = mine.get(i);
            int j = i - 1;
            while (j >= 0 && mine.get(j).date.compareTo(key.date) < 0) {
                mine.set(j + 1, mine.get(j));
                j--;
            }
            mine.set(j + 1, key);
        }

        List<Txn> out = new ArrayList<Txn>();
        if (m.missed > 0) {
            String today = todayIso();
            out.add(new Txn("Missed Contribution", displayDate(today), monthLabel(today), "MISSED", m.missed, false));
        }
        for (Contribution c : mine) {
            String title = c.note != null && c.note.length() > 0 ? c.note : "Contribution";
            out.add(new Txn(title, displayDate(c.date), monthLabel(c.date), "DEPOSIT", c.amount, true));
        }
        return out.toArray(new Txn[out.size()]);
    }

    // ---------- csv loading ----------

    private static double paidBy(Records r, String member) {
        double sum = 0;
        for (Contribution c : r.contributions) {
            if (c.member.equalsIgnoreCase(member)) {
                sum += c.amount;
            }
        }
        return sum;
    }

    // looks in the app's file storage first (in case the app writes new records), then the bundled copy
    private static String readFile() {
        try {
            String path = FileSystemStorage.getInstance().getAppHomePath() + FILE_NAME;
            if (FileSystemStorage.getInstance().exists(path)) {
                InputStream in = FileSystemStorage.getInstance().openInputStream(path);
                return Util.readToString(in, "UTF-8");
            }
        } catch (Exception e) {
            // fall through to the bundled file
        }
        try {
            InputStream in = Display.getInstance().getResourceAsStream(PeopleBackend.class, "/" + FILE_NAME);
            if (in != null) {
                return Util.readToString(in, "UTF-8");
            }
        } catch (Exception e) {
            // no file found
        }
        return null;
    }

    private static Records load() {
        Records r = new Records();
        String text = readFile();
        if (text == null) {
            return r;
        }
        String[] lines = Util.split(text, "\n");

        // pass 1: the group (first G row is the current group)
        for (String line : lines) {
            List<String> f = splitCsv(line);
            if (f.size() >= 6 && f.get(0).equals("G")) {
                Group g = new Group();
                g.owner = f.get(1);
                g.goal = num(f.get(2));
                g.name = f.get(3);
                g.daily = num(f.get(4));
                g.created = f.get(5);
                r.group = g;
                break;
            }
        }
        if (r.group == null) {
            return r;
        }

        // pass 2: members and contributions of that group
        for (String line : lines) {
            List<String> f = splitCsv(line);
            if (f.size() < 3 || !f.get(1).equalsIgnoreCase(r.group.name)) {
                continue;
            }
            String type = f.get(0);
            if (type.equals("M")) {
                r.members.add(f.get(2));
            } else if (type.equals("C") && f.size() >= 5) {
                Contribution c = new Contribution();
                c.member = f.get(2);
                c.amount = num(f.get(3));
                c.date = f.get(4);
                c.note = f.size() >= 6 ? f.get(5) : "";
                r.contributions.add(c);
            }
        }
        return r;
    }

    // keeps empty fields (like the trailing one in C rows)
    private static List<String> splitCsv(String line) {
        List<String> out = new ArrayList<String>();
        line = line.trim();
        if (line.length() == 0) {
            return out;
        }
        int start = 0;
        for (int i = 0; i <= line.length(); i++) {
            if (i == line.length() || line.charAt(i) == ',') {
                out.add(line.substring(start, i).trim());
                start = i + 1;
            }
        }
        return out;
    }

    private static double num(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String capitalize(String s) {
        return s.length() == 0 ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    // ---------- dates ----------

    private static int[] ymd(String iso) {
        String[] p = Util.split(iso, "-");
        return new int[]{(int) num(p[0]), (int) num(p[1]), (int) num(p[2])};
    }

    private static String displayDate(String iso) {
        int[] d = ymd(iso);
        return MONTHS[d[1] - 1].substring(0, 3) + " " + d[2] + ", " + d[0];
    }

    private static String monthLabel(String iso) {
        int[] d = ymd(iso);
        return MONTHS[d[1] - 1].toUpperCase() + " " + d[0];
    }

    private static String todayIso() {
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return c.get(Calendar.YEAR) + "-" + (m < 10 ? "0" : "") + m + "-" + (d < 10 ? "0" : "") + d;
    }

    // whole days passed since the given date (0 if today or in the future)
    private static int daysSince(String iso) {
        int[] d = ymd(iso);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, d[0]);
        c.set(Calendar.MONTH, d[1] - 1);
        c.set(Calendar.DAY_OF_MONTH, d[2]);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long diff = new Date().getTime() - c.getTime().getTime();
        return diff <= 0 ? 0 : (int) (diff / 86400000L);
    }
}