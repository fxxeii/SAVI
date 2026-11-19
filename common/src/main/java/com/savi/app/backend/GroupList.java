package com.savi.app.backend;
// this class manages the collection of group savings and handles saving/loading

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

// GROUP LIST - the "manager" of all GroupSavings objects.
// Every page reads and changes groups through this class (SAVI.groupList), and every change is
// saved to Groups_Records.csv right away. It works the same way as ReminderList.
// ENCAPSULATION: the list is private and getGroups() returns a COPY, so nobody can change a group
// without going through the methods below, which validate the data and then save it.
public class GroupList {
    private final ArrayList<GroupSavings> groups = new ArrayList<>();
    private final String fileRec = "Groups_Records.csv";

    // returns a copy, so the real list can only be changed by addGroup / addMember / addContribution
    public ArrayList<GroupSavings> getGroups() {
        return new ArrayList<>(groups);
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

    // group names are treated as unique
    // searching is not case sensitive, so "december vacation" finds "December Vacation"
    public GroupSavings findGroup(String groupName) {

        if (groupName == null || groupName.trim().isEmpty()) {
            return null;
        }

        for (GroupSavings g : groups) {
            if (g.getGroupName().equalsIgnoreCase(groupName.trim())) {
                return g;
            }
        }

        return null;
    }

    // Creates a new group. It returns "success" or an error message, so the page can show the message.
    public String addGroup(String savingsName, double target, String groupName, double expectedPerMember) {

        // VALIDATION: check everything before creating the object
        if (savingsName == null || savingsName.trim().isEmpty()) {
            return "Savings name is required.";
        }

        if (groupName == null || groupName.trim().isEmpty()) {
            return "Group name is required.";
        }

        if (target <= 0 || Double.isNaN(target) || Double.isInfinite(target)) {
            return "Target amount must be greater than zero.";
        }

        if (expectedPerMember < 0 ||
                Double.isNaN(expectedPerMember) ||
                Double.isInfinite(expectedPerMember)) {
            return "Invalid expected contribution.";
        }

        if (findGroup(groupName) != null) {
            return "Group already exists.";
        }

        groups.add(new GroupSavings(
                savingsName.trim(),
                target,
                groupName.trim(),
                expectedPerMember
        ));

        saveAll();   // every change is saved to the CSV right away

        return "success";
    }

    // same as addGroup, plus the weekly target (0 = no weekly target)
    // POLYMORPHISM (compile-time): addGroup is overloaded. Same name, different parameters.
    public String addGroup(String savingsName, double target, String groupName,
                           double expectedPerMember, double weeklyTarget) {

        if (weeklyTarget < 0 || Double.isNaN(weeklyTarget) || Double.isInfinite(weeklyTarget)) {
            return "Invalid weekly target.";
        }

        // reuse the first version, then set the weekly target on the new group
        String result = addGroup(savingsName, target, groupName, expectedPerMember);

        if (result.equals("success")) {
            findGroup(groupName).setWeeklyTarget(weeklyTarget);
            saveAll();
        }

        return result;
    }

    // adds a member to a group and saves; returns false if the group is missing or the member already exists
    public boolean addMember(String groupName, String member) {

        GroupSavings g = findGroup(groupName);

        if (g == null || member == null || member.trim().isEmpty()) {
            return false;
        }

        if (g.hasMember(member)) {
            return false;
        }

        g.addMember(member);
        saveAll();

        return true;
    }

    // use this for every new or transferred contribution, so it gets saved
    // (calling g.addContribution directly would change the data in memory but NOT save it)
    public boolean addContribution(String groupName, String member, double amount, LocalDate date, String note) {

        GroupSavings g = findGroup(groupName);

        if (g == null) {
            return false;
        }

        // only members of the group can contribute
        if (!g.hasMember(member)) {
            return false;
        }

        if (amount == 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {   // negative = money moved out (transfer)
            return false;
        }

        if (date == null) {
            return false;
        }

        g.addContribution(member.trim(), amount, date, note);
        saveAll();

        return true;
    }

    // TEMPORARY: deletes this device's saved file and empties the list
    public void clearLocalFile() {
        Storage.getInstance().deleteStorageFile(fileRec);
        groups.clear();
    }

    // ---------------------------------------------------------------- FILE HANDLING (CSV)
    // One file holds three kinds of lines, told apart by the first letter:
    // G,savingsName,targetAmount,groupName,expectedPerMember,createdDate,weeklyTarget
    // M,groupName,memberName
    // C,groupName,member,amount,date,note,timestamp
    // Rewrites the whole file from the groups in memory (Storage cannot append).
    private boolean saveAll() {
        StringBuilder sb = new StringBuilder();

        for (GroupSavings g : groups) {
            // one G line for the group itself
            sb.append("G,").append(encode(g.getSavingsName())).append(",")
                    .append(g.getTargetAmount()).append(",")
                    .append(encode(g.getGroupName())).append(",")
                    .append(g.getExpectedPerMember()).append(",")
                    .append(g.getCreatedDate()).append(",")
                    .append(g.getWeeklyTarget()).append("\n");

            // one M line for each member
            for (String m : g.getMembers()) {
                sb.append("M,").append(encode(g.getGroupName())).append(",")
                        .append(encode(m)).append("\n");
            }

            // one C line for each contribution
            for (Contribution c : g.getContributions()) {
                sb.append("C,").append(encode(g.getGroupName())).append(",")
                        .append(encode(c.getMember())).append(",")
                        .append(c.getAmount()).append(",")
                        .append(c.getDate().toString()).append(",")
                        .append(encode(c.getNote())).append(",")
                        .append(c.getTimestamp()).append("\n");
            }
        }
        return writeFile(sb.toString());
    }

    // this device's file wins; if there is none yet, start from the shared file in resources
    // Called once at startup from SAVI.runApp()
    public void load() {
        groups.clear();
        String text = Storage.getInstance().exists(fileRec) ? readFile() : readRecords();
        loadText(text);
    }

    // split the text into lines and send each line to parseLine
    private void loadText(String text) {
        int start = 0;
        while (start < text.length()) {
            int end = text.indexOf('\n', start);
            if (end == -1) end = text.length();
            String line = text.substring(start, end);
            start = end + 1;
            if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
            if (line.length() == 0) continue;

            parseLine(line);
        }
    }

    // One line -> one object. The first letter decides what the line is.
    // Order matters: a G line creates the group first, so the M and C lines after it can find it.
    private void parseLine(String line) {
        ArrayList<String> p = splitLine(line);
        try {
            String type = p.get(0);

            if (type.equals("G") && p.size() >= 5) {
                String savingsName = decode(p.get(1));
                double target = Double.parseDouble(p.get(2));
                String groupName = decode(p.get(3));
                double expected = Double.parseDouble(p.get(4));

                LocalDate createdDate;

                if (p.size() >= 6) {
                    createdDate = LocalDate.parse(p.get(5));
                } else {
                    // Compatibility with older CSV records
                    createdDate = LocalDate.now();
                }

                if (findGroup(groupName) == null) {
                    groups.add(new GroupSavings(
                            savingsName,
                            target,
                            groupName,
                            expected,
                            createdDate
                    ));

                    // optional 7th column: the weekly target (older records don't have it: 0)
                    if (p.size() >= 7) {
                        try {
                            findGroup(groupName).setWeeklyTarget(Double.parseDouble(p.get(6)));
                        } catch (Exception ignored) {
                            // keep 0
                        }
                    }
                }

            } else if (type.equals("M") && p.size() >= 3) {

                // M line: add the member to its group
                GroupSavings g = findGroup(decode(p.get(1)));

                if (g != null) {
                    g.addMember(decode(p.get(2)));
                }

            } else if (type.equals("C") && p.size() >= 6) {

                // C line: add the contribution to its group (only for existing members)
                GroupSavings g = findGroup(decode(p.get(1)));

                String member = decode(p.get(2));
                double amount = Double.parseDouble(p.get(3));
                LocalDate date = LocalDate.parse(p.get(4));
                String note = decode(p.get(5));

                if (g != null && g.hasMember(member)
                        && amount != 0
                        && !Double.isInfinite(amount)
                        && !Double.isNaN(amount)) {

                    g.addContribution(member, amount, date, note);

                    // keep the exact time it was recorded (older records have no time: 0)
                    long recordedAt = 0;
                    if (p.size() >= 7) {
                        try {
                            recordedAt = Long.parseLong(p.get(6));
                        } catch (NumberFormatException e) {
                            recordedAt = 0;
                        }
                    }
                    // the contribution we just added is the last one in the list
                    Contribution added = null;
                    for (Contribution c : g.getContributions()) {
                        added = c;
                    }
                    if (added != null) {
                        added.setTimestamp(recordedAt);   // package-private setter in Contribution
                    }
                }
            }
        } catch (Exception ex) {
            // a damaged line is reported and skipped, it does not crash the app
            System.out.println("Invalid group record: " + line);
            ex.printStackTrace();
        }
    }

    // cuts one line into its parts at every comma
    private ArrayList<String> splitLine(String line) {
        ArrayList<String> parts = new ArrayList<>();
        int start = 0;
        while (true) {
            int comma = line.indexOf(',', start);
            if (comma == -1) {
                parts.add(line.substring(start));
                break;
            }
            parts.add(line.substring(start, comma));
            start = comma + 1;
        }
        return parts;
    }

    // reads this device's saved file (empty text if nothing was saved yet)
    private String readFile() {
        try {
            if (!Storage.getInstance().exists(fileRec)) {
                return "";
            }

            // try-with-resources closes the stream automatically
            try (InputStream in =
                         Storage.getInstance().createInputStream(fileRec)) {

                String text = Util.readToString(in, "UTF-8");
                return text == null ? "" : text;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    // reads the shared starter file packaged inside the app (src/main/resources)
    private String readRecords() {
        try {
            InputStream resource =
                    Display.getInstance().getResourceAsStream(
                            GroupList.class, "/" + fileRec
                    );

            if (resource == null) {
                return "";
            }

            try (InputStream in = resource) {
                String text = Util.readToString(in, "UTF-8");
                return text == null ? "" : text;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    // writes the text with Storage; returns true if it worked
    private boolean writeFile(String text) {
        try (OutputStream out = Storage.getInstance().createOutputStream(fileRec)) {

            out.write(text.getBytes("UTF-8"));

            // copy these lines into src/main/resources/Groups_Records.csv to share them
            System.out.println("GROUPS CSV NOW:\n" + text);

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // A comma or new line typed into a field would break the CSV, so those characters
    // are stored as %2C / %0A / %0D (and "%" itself as %25), then turned back by decode().
    private String encode(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '%') out.append("%25");
            else if (c == ',') out.append("%2C");
            else if (c == '\n') out.append("%0A");
            else if (c == '\r') out.append("%0D");
            else out.append(c);
        }
        return out.toString();
    }

    private String decode(String s) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '%' && i + 3 <= s.length()) {
                String code = s.substring(i, i + 3);
                if (code.equals("%25")) { out.append('%'); i += 3; continue; }
                if (code.equals("%2C")) { out.append(','); i += 3; continue; }
                if (code.equals("%0A")) { out.append('\n'); i += 3; continue; }
                if (code.equals("%0D")) { out.append('\r'); i += 3; continue; }
            }
            out.append(c);
            i++;
        }
        return out.toString();
    }
}