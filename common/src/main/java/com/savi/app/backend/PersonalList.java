package com.savi.app.backend;

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class PersonalList {
    private final ArrayList<PersonalSavings> list = new ArrayList<>();
    private final String fileRec = "Personal_Records.csv";

    public ArrayList<PersonalSavings> getAll() {
        return new ArrayList<>(list);
    }

    public ArrayList<PersonalSavings> getForOwner(String ownerName) {
        ArrayList<PersonalSavings> result = new ArrayList<>();
        if (ownerName == null) return result;
        for (PersonalSavings p : list) {
            if (p.getOwner() != null && p.getOwner().equalsIgnoreCase(ownerName.trim())) {
                result.add(p);
            }
        }
        return result;
    }

    public PersonalSavings findSavings(String savingsName, String ownerName) {
        if (savingsName == null) return null;
        for (PersonalSavings p : list) {
            boolean nameMatch = p.getSavingsName().equalsIgnoreCase(savingsName.trim());
            boolean ownerMatch = ownerName == null || (p.getOwner() != null && p.getOwner().equalsIgnoreCase(ownerName.trim()));
            if (nameMatch && ownerMatch) {
                return p;
            }
        }
        return null;
    }

    public String addPersonalSavings(String savingsName, double targetAmount, String ownerName) {
        if (savingsName == null || savingsName.trim().isEmpty()) {
            return "Please enter a savings goal name.";
        }
        if (targetAmount <= 0 || Double.isNaN(targetAmount) || Double.isInfinite(targetAmount)) {
            return "Target amount must be greater than 0.";
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            return "Please log in first.";
        }
        if (findSavings(savingsName, ownerName) != null) {
            return "You already have a personal savings goal with that name.";
        }

        list.add(new PersonalSavings(savingsName.trim(), targetAmount, ownerName.trim()));
        saveAll();
        return "success";
    }

    public boolean addContribution(String savingsName, String ownerName, double amount, LocalDate date, String note) {
        PersonalSavings p = findSavings(savingsName, ownerName);
        if (p == null) return false;
        p.addContribution(ownerName, amount, date, note);
        saveAll();
        return true;
    }

    // ---------------------------------------------------------------- FILE HANDLING (CSV)
    // P,savingsName,targetAmount,ownerName
    // C,savingsName,ownerName,amount,date,note
    private void saveAll() {
        StringBuilder sb = new StringBuilder();
        for (PersonalSavings p : list) {
            sb.append("P,").append(encode(p.getSavingsName())).append(",")
                    .append(p.getTargetAmount()).append(",")
                    .append(encode(p.getOwner())).append("\n");

            for (Contribution c : p.getContributions()) {
                sb.append("C,").append(encode(p.getSavingsName())).append(",")
                        .append(encode(p.getOwner())).append(",")
                        .append(c.getAmount()).append(",")
                        .append(c.getDate().toString()).append(",")
                        .append(encode(c.getNote())).append("\n");
            }
        }
        writeFile(sb.toString());
    }

    public void load() {
        list.clear();
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
            if (type.equals("P") && p.size() >= 4) {
                String savingsName = decode(p.get(1));
                double target = Double.parseDouble(p.get(2));
                String owner = decode(p.get(3));
                if (findSavings(savingsName, owner) == null) {
                    list.add(new PersonalSavings(savingsName, target, owner));
                }
            } else if (type.equals("C") && p.size() >= 6) {
                String savingsName = decode(p.get(1));
                String owner = decode(p.get(2));
                PersonalSavings ps = findSavings(savingsName, owner);
                if (ps != null) {
                    ps.addContribution(owner,
                            Double.parseDouble(p.get(3)),
                            LocalDate.parse(p.get(4)),
                            decode(p.get(5)));
                }
            }
        } catch (Exception ex) {
            // skip damaged line
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
            if (!Storage.getInstance().exists(fileRec)) return "";
            InputStream in = Storage.getInstance().createInputStream(fileRec);
            String text = Util.readToString(in, "UTF-8");
            return text == null ? "" : text;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private String readRecords() {
        try {
            InputStream in = Display.getInstance().getResourceAsStream(PersonalList.class, "/" + fileRec);
            if (in == null) return "";
            String text = Util.readToString(in, "UTF-8");
            return text == null ? "" : text;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private void writeFile(String text) {
        try {
            OutputStream out = Storage.getInstance().createOutputStream(fileRec);
            out.write(text.getBytes("UTF-8"));
            out.close();
            System.out.println("PERSONAL CSV NOW:\n" + text);
        } catch (Exception ex) {
            ex.printStackTrace();
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