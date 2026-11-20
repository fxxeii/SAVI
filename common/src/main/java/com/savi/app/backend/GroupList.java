package com.savi.app.backend;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;
import com.savi.app.SAVI;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class GroupList {
    private final ArrayList<GroupSavings> groups = new ArrayList<>();
    private final String fileRec = "Groups_Records.txt";

    public ArrayList<GroupSavings> getGroups() {
        return new ArrayList<>(groups);
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

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
        if (expectedPerMember < 0 || Double.isNaN(expectedPerMember) || Double.isInfinite(expectedPerMember)) {
            return "Invalid expected contribution.";
        }
        if (findGroup(groupName) != null) {
            return "Group already exists.";
        }

        GroupSavings created = new GroupSavings(
                savingsName.trim(),
                target,
                groupName.trim(),
                expectedPerMember
        );

        String currentUser = (SAVI.user != null) ? SAVI.user.getCurrentName() : null;
        if (currentUser != null && !currentUser.trim().isEmpty()) {
            created.addMember(currentUser.trim());
        }

        groups.add(created);
        saveAll();
        return "success";
    }

    public String addGroup(String savingsName, double target, String groupName,
                           double expectedPerMember, double weeklyTarget) {
        if (weeklyTarget < 0 || Double.isNaN(weeklyTarget) || Double.isInfinite(weeklyTarget)) {
            return "Invalid weekly target.";
        }
        String result = addGroup(savingsName, target, groupName, expectedPerMember);
        if (result.equals("success")) {
            GroupSavings g = findGroup(groupName);
            if (g != null) {
                g.setWeeklyTarget(weeklyTarget);
                saveAll();
            }
        }
        return result;
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

    public boolean addContribution(String groupName, String member, double amount, LocalDate date, String note) {
        GroupSavings g = findGroup(groupName);
        if (g == null) {
            return false;
        }
        if (!g.hasMember(member)) {
            return false;
        }
        if (amount == 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            return false;
        }
        if (date == null) {
            return false;
        }
        g.addContribution(member.trim(), amount, date, note);
        saveAll();
        return true;
    }

    public void clearLocalFile() {
        try {
            String path = txtPath();
            if (FileSystemStorage.getInstance().exists(path)) {
                FileSystemStorage.getInstance().delete(path);
            }
        } catch (Exception ignored) {
        }
        Storage.getInstance().deleteStorageFile(fileRec);
        groups.clear();
    }

    private String txtPath() {
        return FileSystemStorage.getInstance().getAppHomePath() + fileRec;
    }

    private boolean saveAll() {
        StringBuilder sb = new StringBuilder();
        String owner = (SAVI.user != null && SAVI.user.getCurrentName() != null)
                ? SAVI.user.getCurrentName() : "";

        for (GroupSavings g : groups) {
            sb.append("G,").append(encode(owner)).append(",")
                    .append(g.getTargetAmount()).append(",")
                    .append(encode(g.getGroupName())).append(",")
                    .append(g.getExpectedPerMember()).append(",")
                    .append(g.getCreatedDate()).append(",")
                    .append(g.getWeeklyTarget()).append(",")
                    .append(encode(g.getSavingsName())).append("\n");

            for (String m : g.getMembers()) {
                sb.append("M,").append(encode(g.getGroupName())).append(",")
                        .append(encode(m)).append("\n");
            }

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

    public void load() {
        groups.clear();
        String text = readFile();
        if (text == null || text.trim().isEmpty()) {
            text = readRecords();
        }
        loadText(text == null ? "" : text);
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
                double target = Double.parseDouble(p.get(2));
                String groupName = decode(p.get(3));
                double expected = Double.parseDouble(p.get(4));
                String savingsName = p.size() >= 8 && !decode(p.get(7)).trim().isEmpty()
                        ? decode(p.get(7))
                        : groupName;

                LocalDate createdDate;
                if (p.size() >= 6 && !p.get(5).trim().isEmpty()) {
                    createdDate = LocalDate.parse(p.get(5).trim());
                } else {
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

                    if (p.size() >= 7 && !p.get(6).trim().isEmpty()) {
                        try {
                            findGroup(groupName).setWeeklyTarget(Double.parseDouble(p.get(6).trim()));
                        } catch (Exception ignored) {
                        }
                    }
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
                        && amount != 0
                        && !Double.isInfinite(amount)
                        && !Double.isNaN(amount)) {

                    g.addContribution(member, amount, date, note);

                    long recordedAt = 0;
                    if (p.size() >= 7) {
                        try {
                            recordedAt = Long.parseLong(p.get(6).trim());
                        } catch (NumberFormatException e) {
                            recordedAt = 0;
                        }
                    }
                    Contribution added = null;
                    for (Contribution c : g.getContributions()) {
                        added = c;
                    }
                    if (added != null) {
                        added.setTimestamp(recordedAt);
                    }
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
            String path = txtPath();
            if (FileSystemStorage.getInstance().exists(path)) {
                try (InputStream in = FileSystemStorage.getInstance().openInputStream(path)) {
                    String text = Util.readToString(in, "UTF-8");
                    if (text != null) {
                        return text;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (!Storage.getInstance().exists(fileRec)) {
                return "";
            }
            try (InputStream in = Storage.getInstance().createInputStream(fileRec)) {
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
            InputStream resource = Display.getInstance().getResourceAsStream(
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
        boolean fsOk = false;
        try (OutputStream out = FileSystemStorage.getInstance().openOutputStream(txtPath())) {
            out.write(text.getBytes("UTF-8"));
            fsOk = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try (OutputStream out = Storage.getInstance().createOutputStream(fileRec)) {
            out.write(text.getBytes("UTF-8"));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return fsOk;
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