package com.savi.app.backend;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;
import com.savi.app.SAVI;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Backend of PeoplePage and ViewAllTransacPage.
 *
 * Reads the group data from Groups_Records.csv:
 *   G,<creator>,<totalTarget>,<groupName>,<expectedPerMember>,<createdDate>[,more...]
 *   M,<groupName>,<memberName>
 *   C,<groupName>,<memberName>,<amount>,<date>,<note>[,timestamp]
 */
public class PeopleBackend {

    private static final String GROUPS_CSV = "Groups_Records.csv";

    private static String selectedGroupName = null;

    private static final String[] MONTHS = {
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };

    public static class Member {
        public final String name;
        public final String role;
        public final String handle;
        public final String group;
        public final boolean active;
        public final double totalPaid;
        public final double missed;
        public final double goal;

        public Member(String name, String role, String handle, String group, boolean active,
                      double totalPaid, double missed, double goal) {
            this.name = name;
            this.role = role;
            this.handle = handle;
            this.group = group;
            this.active = active;
            this.totalPaid = totalPaid;
            this.missed = missed;
            this.goal = goal;
        }
    }

    public static class Txn {
        public final String type;
        public final String title;
        public final String date;
        public final String month;
        public final double amount;
        public final boolean settled;

        public Txn(String type, String title, String date, String month, double amount, boolean settled) {
            this.type = type;
            this.title = title;
            this.date = date;
            this.month = month;
            this.amount = amount;
            this.settled = settled;
        }
    }

    private static class RawGroup {
        String creator = "";
        String name = "";
        double totalTarget = 0;
        double expectedPerMember = 0;
        LocalDate createdDate = LocalDate.now();
        List<String> members = new ArrayList<>();
        List<RawContribution> contributions = new ArrayList<>();
    }

    private static class RawContribution {
        String member;
        double amount;
        LocalDate date;
        String note;

        RawContribution(String member, double amount, LocalDate date, String note) {
            this.member = member;
            this.amount = amount;
            this.date = date;
            this.note = note;
        }
    }

    public static void setSelectedGroup(String groupName) {
        selectedGroupName = (groupName == null || groupName.trim().isEmpty()) ? null : groupName.trim();
    }

    public static String[] getUserGroupNames() {
        String me = currentUserName();
        if (me.isEmpty()) {
            return new String[0];
        }
        List<String> names = new ArrayList<>();
        List<RawGroup> allRaw = loadAllFromCsv();

        if (SAVI.groupList != null) {
            SAVI.groupList.load();
            ArrayList<GroupSavings> groups = SAVI.groupList.getGroups();
            if (groups != null) {
                for (int i = 0; i < groups.size(); i++) {
                    GroupSavings g = groups.get(i);
                    if (g == null) continue;
                    RawGroup matchingRaw = findRawGroup(allRaw, g.getGroupName());
                    if (belongsToCurrentUser(matchingRaw, g)) {
                        String n = g.getGroupName().trim();
                        if (!containsIgnoreCase(names, n)) {
                            names.add(n);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < allRaw.size(); i++) {
            RawGroup rg = allRaw.get(i);
            if (belongsToCurrentUser(rg, null) && rg.name != null && !rg.name.trim().isEmpty()) {
                String n = rg.name.trim();
                if (!containsIgnoreCase(names, n)) {
                    names.add(n);
                }
            }
        }

        return names.toArray(new String[names.size()]);
    }

    private static boolean containsIgnoreCase(List<String> list, String target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private static String currentUserName() {
        if (SAVI.user != null && SAVI.user.getCurrentName() != null) {
            String n = SAVI.user.getCurrentName().trim();
            if (!n.isEmpty()) {
                return n;
            }
        }
        String memberName = Profile.getInstance().getMemberName();
        return memberName == null ? "" : memberName.trim();
    }

    private static boolean belongsToCurrentUser(RawGroup rg, GroupSavings gs) {
        String me = currentUserName();
        if (me.isEmpty()) {
            return false;
        }
        if (rg != null) {
            if (rg.creator != null && rg.creator.trim().equalsIgnoreCase(me)) {
                return true;
            }
            for (int i = 0; i < rg.members.size(); i++) {
                if (rg.members.get(i) != null && rg.members.get(i).trim().equalsIgnoreCase(me)) {
                    return true;
                }
            }
        }
        if (gs != null && gs.hasMember(me)) {
            return true;
        }
        return false;
    }

    private static GroupSavings currentGroup() {
        String me = currentUserName();
        if (me.isEmpty()) {
            return null;
        }
        List<RawGroup> allRaw = loadAllFromCsv();
        if (SAVI.groupList != null) {
            SAVI.groupList.load();
            ArrayList<GroupSavings> groups = SAVI.groupList.getGroups();
            if (groups != null && !groups.isEmpty()) {
                if (selectedGroupName != null) {
                    for (int i = 0; i < groups.size(); i++) {
                        GroupSavings g = groups.get(i);
                        if (g != null && selectedGroupName.equalsIgnoreCase(g.getGroupName())) {
                            RawGroup matchingRaw = findRawGroup(allRaw, g.getGroupName());
                            if (belongsToCurrentUser(matchingRaw, g)) {
                                return g;
                            }
                        }
                    }
                }
                for (int i = groups.size() - 1; i >= 0; i--) {
                    GroupSavings g = groups.get(i);
                    if (g == null) {
                        continue;
                    }
                    RawGroup matchingRaw = findRawGroup(allRaw, g.getGroupName());
                    if (belongsToCurrentUser(matchingRaw, g)) {
                        selectedGroupName = g.getGroupName();
                        return g;
                    }
                }
            }
        }
        return null;
    }

    private static RawGroup currentRawGroup() {
        String me = currentUserName();
        if (me.isEmpty()) {
            return null;
        }
        List<RawGroup> all = loadAllFromCsv();
        if (selectedGroupName != null) {
            for (int i = 0; i < all.size(); i++) {
                RawGroup g = all.get(i);
                if (g.name != null && selectedGroupName.equalsIgnoreCase(g.name) && belongsToCurrentUser(g, null)) {
                    return g;
                }
            }
        }
        for (int i = all.size() - 1; i >= 0; i--) {
            RawGroup g = all.get(i);
            if (belongsToCurrentUser(g, null)) {
                selectedGroupName = g.name;
                return g;
            }
        }
        return null;
    }

    private static RawGroup findRawGroup(List<RawGroup> all, String groupName) {
        if (groupName == null) {
            return null;
        }
        for (int i = 0; i < all.size(); i++) {
            RawGroup rg = all.get(i);
            if (rg.name != null && rg.name.trim().equalsIgnoreCase(groupName.trim())) {
                return rg;
            }
        }
        return null;
    }

    public static String getGroupName() {
        GroupSavings g = currentGroup();
        if (g != null && g.getGroupName() != null && !g.getGroupName().trim().isEmpty()) {
            return g.getGroupName();
        }
        RawGroup rg = currentRawGroup();
        if (rg != null && rg.name != null && !rg.name.trim().isEmpty()) {
            return rg.name;
        }
        return "No Group Yet";
    }

    public static double getGroupPool() {
        GroupSavings g = currentGroup();
        if (g != null) {
            return g.getCurrentAmount();
        }
        RawGroup rg = currentRawGroup();
        if (rg != null) {
            double sum = 0;
            for (int i = 0; i < rg.contributions.size(); i++) {
                sum += rg.contributions.get(i).amount;
            }
            return sum;
        }
        return 0.0;
    }

    public static Member[] getMembers() {
        GroupSavings g = currentGroup();
        RawGroup rg = currentRawGroup();

        if (g != null) {
            List<String> memberNames = g.getMembers();
            List<Contribution> allContribs = g.getContributions();
            double expected = g.getExpectedPerMember();
            double goal = expected > 0 ? expected : g.getTargetAmount();
            String creator = rg != null ? rg.creator : "";

            Member[] result = new Member[memberNames.size()];
            for (int i = 0; i < memberNames.size(); i++) {
                String name = memberNames.get(i);
                double paid = 0.0;
                for (int j = 0; j < allContribs.size(); j++) {
                    Contribution c = allContribs.get(j);
                    if (c.getMember() != null && c.getMember().equalsIgnoreCase(name)) {
                        paid += c.getAmount();
                    }
                }
                double missed = Math.max(0.0, expected - paid);
                boolean isCreator = !creator.isEmpty() && creator.equalsIgnoreCase(name);
                String role = (isCreator || i == 0) ? "Admin" : "Member";
                result[i] = new Member(
                        name,
                        role,
                        toHandle(name),
                        g.getGroupName(),
                        paid > 0,
                        paid,
                        missed,
                        goal > 0 ? goal : 1.0
                );
            }
            return result;
        }

        if (rg == null) {
            return new Member[0];
        }
        double goal = rg.expectedPerMember > 0 ? rg.expectedPerMember : rg.totalTarget;
        Member[] result = new Member[rg.members.size()];
        for (int i = 0; i < rg.members.size(); i++) {
            String name = rg.members.get(i);
            double paid = 0;
            for (int j = 0; j < rg.contributions.size(); j++) {
                RawContribution c = rg.contributions.get(j);
                if (c.member != null && c.member.equalsIgnoreCase(name)) {
                    paid += c.amount;
                }
            }
            double missed = Math.max(0.0, rg.expectedPerMember - paid);
            boolean isCreator = !rg.creator.isEmpty() && rg.creator.equalsIgnoreCase(name);
            String role = (isCreator || i == 0) ? "Admin" : "Member";
            result[i] = new Member(
                    name,
                    role,
                    toHandle(name),
                    rg.name,
                    paid > 0,
                    paid,
                    missed,
                    goal > 0 ? goal : 1.0
            );
        }
        return result;
    }

    public static Txn[] getTransactions(Member m) {
        if (m == null || m.name == null) {
            return new Txn[0];
        }
        String memberName = m.name.trim();
        List<Txn> list = new ArrayList<>();

        GroupSavings g = currentGroup();
        if (g != null) {
            List<Contribution> allContribs = g.getContributions();
            for (int i = allContribs.size() - 1; i >= 0; i--) {
                Contribution c = allContribs.get(i);
                if (c.getMember() != null && c.getMember().equalsIgnoreCase(memberName)) {
                    String title = (c.getNote() == null || c.getNote().trim().isEmpty())
                            ? "Group Deposit" : c.getNote().trim();
                    LocalDate d = c.getDate() != null ? c.getDate() : LocalDate.now();
                    list.add(new Txn(
                            "DEPOSIT",
                            title,
                            formatDate(d),
                            formatMonth(d),
                            c.getAmount(),
                            true
                    ));
                }
            }
        } else {
            RawGroup rg = currentRawGroup();
            if (rg != null) {
                for (int i = rg.contributions.size() - 1; i >= 0; i--) {
                    RawContribution c = rg.contributions.get(i);
                    if (c.member != null && c.member.equalsIgnoreCase(memberName)) {
                        String title = (c.note == null || c.note.trim().isEmpty())
                                ? "Group Deposit" : c.note.trim();
                        LocalDate d = c.date != null ? c.date : LocalDate.now();
                        list.add(new Txn(
                                "DEPOSIT",
                                title,
                                formatDate(d),
                                formatMonth(d),
                                c.amount,
                                true
                        ));
                    }
                }
            }
        }

        if (m.missed > 0) {
            LocalDate today = LocalDate.now();
            list.add(new Txn(
                    "MISSED",
                    "Remaining Share",
                    formatDate(today),
                    formatMonth(today),
                    m.missed,
                    false
            ));
        }

        return list.toArray(new Txn[list.size()]);
    }

    private static String toHandle(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "member";
        }
        return name.trim().toLowerCase().replace(' ', '.');
    }

    private static String formatDate(LocalDate d) {
        if (d == null) {
            return "";
        }
        String m = MONTHS[d.getMonthValue() - 1];
        String shortMonth = m.charAt(0) + m.substring(1, 3).toLowerCase();
        return shortMonth + " " + d.getDayOfMonth() + ", " + d.getYear();
    }

    private static String formatMonth(LocalDate d) {
        if (d == null) {
            return "";
        }
        return MONTHS[d.getMonthValue() - 1] + " " + d.getYear();
    }

    private static List<RawGroup> loadAllFromCsv() {
        Map<String, RawGroup> map = new LinkedHashMap<>();
        String text = readCsvText();
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        if (text.charAt(0) == '\uFEFF') {
            text = text.substring(1);
        }
        String[] lines = Util.split(text, "\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].replace("\r", "").trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] p = splitLine(line);
            String type = p[0];
            if ("G".equals(type) && p.length >= 5) {
                String gName = p[3];
                if (!map.containsKey(gName)) {
                    RawGroup rg = new RawGroup();
                    rg.creator = p[1];
                    rg.totalTarget = parseDoubleSafe(p[2]);
                    rg.name = gName;
                    rg.expectedPerMember = parseDoubleSafe(p[4]);
                    rg.createdDate = p.length >= 6 && !p[5].isEmpty() ? parseDateSafe(p[5]) : LocalDate.now();
                    map.put(gName, rg);
                }
            } else if ("M".equals(type) && p.length >= 3) {
                RawGroup rg = map.get(p[1]);
                if (rg != null && !rg.members.contains(p[2])) {
                    rg.members.add(p[2]);
                }
            } else if ("C".equals(type) && p.length >= 5) {
                RawGroup rg = map.get(p[1]);
                if (rg != null) {
                    String member = p[2];
                    double amt = parseDoubleSafe(p[3]);
                    LocalDate date = parseDateSafe(p[4]);
                    String note = p.length >= 6 && !p[5].isEmpty() ? p[5] : "Group Deposit";
                    rg.contributions.add(new RawContribution(member, amt, date, note));
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    private static String readCsvText() {
        try {
            FileSystemStorage fs = FileSystemStorage.getInstance();
            String path = fs.getAppHomePath() + GROUPS_CSV;
            if (fs.exists(path)) {
                try (InputStream in = fs.openInputStream(path)) {
                    String text = Util.readToString(in, "UTF-8");
                    if (text != null && !text.trim().isEmpty()) {
                        return text;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        try {
            if (Storage.getInstance().exists(GROUPS_CSV)) {
                try (InputStream in = Storage.getInstance().createInputStream(GROUPS_CSV)) {
                    String text = Util.readToString(in, "UTF-8");
                    if (text != null && !text.trim().isEmpty()) {
                        return text;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        try {
            InputStream in = Display.getInstance().getResourceAsStream(PeopleBackend.class, "/" + GROUPS_CSV);
            if (in != null) {
                try (InputStream stream = in) {
                    String text = Util.readToString(stream, "UTF-8");
                    if (text != null) {
                        return text;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private static String[] splitLine(String line) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ',') {
                parts.add(line.substring(start, i).trim());
                start = i + 1;
            }
        }
        parts.add(line.substring(start).trim());
        return parts.toArray(new String[parts.size()]);
    }

    private static double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static LocalDate parseDateSafe(String s) {
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}