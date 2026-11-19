package com.savi.app.backend;
// this class manages the collection of group savings and handles saving/loading

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class GroupList {
    private final ArrayList<GroupSavings> groups = new ArrayList<>();
    private final String fileRec = "Groups_Records.csv";

    public ArrayList<GroupSavings> getGroups() {
        return new ArrayList<>(groups);
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

    // group names are treated as unique
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

    public String addGroup(String savingsName, double target, String groupName, double expectedPerMember) {

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

        saveAll();

        return "success";
    }

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
    public boolean addContribution(String groupName, String member, double amount, LocalDate date, String note) {

        GroupSavings g = findGroup(groupName);

        if (g == null) {
            return false;
        }

        if (!g.hasMember(member)) {
            return false;
        }

        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
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
    // G,savingsName,targetAmount,groupName,expectedPerMember
    // M,groupName,memberName
    // C,groupName,member,amount,date,note
    private boolean saveAll() {
        StringBuilder sb = new StringBuilder();

        for (GroupSavings g : groups) {
            sb.append("G,").append(encode(g.getSavingsName())).append(",")
                    .append(g.getTargetAmount()).append(",")
                    .append(encode(g.getGroupName())).append(",")
                    .append(g.getExpectedPerMember()).append("\n");

            for (String m : g.getMembers()) {
                sb.append("M,").append(encode(g.getGroupName())).append(",")
                        .append(encode(m)).append("\n");
            }

            for (Contribution c : g.getContributions()) {
                sb.append("C,").append(encode(g.getGroupName())).append(",")
                        .append(encode(c.getMember())).append(",")
                        .append(c.getAmount()).append(",")
                        .append(c.getDate().toString()).append(",")
                        .append(encode(c.getNote())).append("\n");
            }
        }
        return writeFile(sb.toString());
    }

    // this device's file wins; if there is none yet, start from the shared file in resources
    public void load() {
        groups.clear();
        String text = Storage.getInstance().exists(fileRec) ? readFile() : readRecords();
        loadText(text);
    }

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

    private void parseLine(String line) {
        ArrayList<String> p = splitLine(line);
        try {
            String type = p.get(0);

            if (type.equals("G") && p.size() >= 5) {
                String savingsName = decode(p.get(1));
                double target = Double.parseDouble(p.get(2));
                String groupName = decode(p.get(3));
                double expected = Double.parseDouble(p.get(4));
                if (findGroup(groupName) == null) {
                    groups.add(new GroupSavings(savingsName, target, groupName, expected));
                }

            } else if (type.equals("M") && p.size() >= 3) {

                GroupSavings g = findGroup(decode(p.get(1)));

                if (g != null) {
                    g.addMember(decode(p.get(2)));
                }

            } else if (type.equals("C") && p.size() >= 6) {

                GroupSavings g = findGroup(decode(p.get(1)));

                String member = decode(p.get(2));
                double amount = Double.parseDouble(p.get(3));
                LocalDate date = LocalDate.parse(p.get(4));
                String note = decode(p.get(5));

                if (g != null && g.hasMember(member)
                        && amount > 0
                        && !Double.isInfinite(amount)
                        && !Double.isNaN(amount)) {

                    g.addContribution(member, amount, date, note);
                }
            }
        } catch (Exception ex) {
            System.out.println("Invalid group record: " + line);
            ex.printStackTrace();
        }
    }

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

    private String readFile() {
        try {
            if (!Storage.getInstance().exists(fileRec)) {
                return "";
            }

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

    private boolean writeFile(String text) {
        try (OutputStream out = Storage.getInstance().createOutputStream(fileRec)) {

            out.write(text.getBytes("UTF-8"));

            System.out.println("GROUPS CSV NOW:\n" + text);

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

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