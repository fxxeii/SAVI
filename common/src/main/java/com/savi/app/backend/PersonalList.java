package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// PERSONAL LIST (manages PersonalSavings + saves to Personal_Records.txt via Codename One Storage)
// File format:
//   P,creator,targetAmount,savingsName,weeklyTarget,targetDate
//   C,creator,savingsName,amount,date,note[,timestamp]
public class PersonalList {

    private final ArrayList<PersonalSavings> list = new ArrayList<>();
    private final String fileRec = "Personal_Records.txt";

    // CREATE
    public String addPersonalSavings(String creator, double targetAmount, String savingsName) {
        return addPersonalSavings(creator, targetAmount, savingsName, 0);
    }

    public String addPersonalSavings(String creator, double targetAmount, String savingsName, double weeklyTarget) {
        return addPersonalSavings(creator, targetAmount, savingsName, weeklyTarget, "");
    }

    public String addPersonalSavings(String creator, double targetAmount, String savingsName,
                                     double weeklyTarget, String targetDate) {
        if (creator == null || creator.trim().isEmpty()) {
            return "Creator name is required.";
        }
        if (savingsName == null || savingsName.trim().isEmpty()) {
            return "Savings name is required.";
        }
        if (targetAmount <= 0) {
            return "Target amount must be greater than 0.";
        }
        if (findByCreatorAndName(creator, savingsName) != null) {
            return "You already have a personal saving with that name.";
        }

        list.add(new PersonalSavings(creator.trim(), targetAmount, savingsName.trim(), weeklyTarget, targetDate));
        if (!saveAll()) {
            return "Could not save personal savings.";
        }
        return "success";
    }

    // READ
    public List<PersonalSavings> getAll() {
        return new ArrayList<>(list);
    }

    public List<PersonalSavings> getForUser(String username) {
        ArrayList<PersonalSavings> out = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            return out;
        }
        String u = username.trim();
        for (PersonalSavings p : list) {
            if (p.getCreator().equalsIgnoreCase(u)) {
                out.add(p);
            }
        }
        return out;
    }

    public PersonalSavings findByCreatorAndName(String creator, String savingsName) {
        if (creator == null || savingsName == null) {
            return null;
        }
        String c = creator.trim();
        String n = savingsName.trim();
        for (PersonalSavings p : list) {
            if (p.getCreator().equalsIgnoreCase(c) && p.getSavingsName().equalsIgnoreCase(n)) {
                return p;
            }
        }
        return null;
    }

    // UPDATE
    public String recordDeposit(String creator, String savingsName, double amount, LocalDate date, String note) {
        PersonalSavings p = findByCreatorAndName(creator, savingsName);
        if (p == null) {
            return "Personal savings goal not found.";
        }
        if (amount <= 0) {
            return "Amount must be greater than 0.";
        }
        try {
            p.addContribution(p.getCreator(), amount, date, note);
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
        saveAll();
        return "success";
    }

    public boolean saveChanges() {
        return saveAll();
    }

    // DELETE
    public boolean deletePersonalSavings(String creator, String savingsName) {
        PersonalSavings p = findByCreatorAndName(creator, savingsName);
        if (p == null) {
            return false;
        }
        list.remove(p);
        saveAll();
        return true;
    }

    // PERSISTENCE (Storage)
    private boolean saveAll() {
        StringBuilder sb = new StringBuilder();
        for (PersonalSavings p : list) {
            sb.append("P,").append(RecordStorage.encode(p.getCreator())).append(",")
                    .append(p.getTargetAmount()).append(",")
                    .append(RecordStorage.encode(p.getSavingsName())).append(",")
                    .append(p.getWeeklyTarget()).append(",")
                    .append(RecordStorage.encode(p.getTargetDate())).append("\n");

            for (Contribution c : p.getContributions()) {
                sb.append("C,").append(RecordStorage.encode(p.getCreator())).append(",")
                        .append(RecordStorage.encode(p.getSavingsName())).append(",")
                        .append(c.getAmount()).append(",")
                        .append(c.getDate().toString()).append(",")
                        .append(RecordStorage.encode(c.getNote())).append(",")
                        .append(c.getTimestamp()).append("\n");
            }
        }
        return RecordStorage.writeLocal(fileRec, sb.toString());
    }

    public void load() {
        list.clear();
        loadText(RecordStorage.loadOrSeed(PersonalList.class, fileRec));
    }

    private void loadText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        int lineStart = 0;
        for (int i = 0; i <= text.length(); i++) {
            if (i == text.length() || text.charAt(i) == '\n') {
                String line = text.substring(lineStart, i).trim();
                lineStart = i + 1;
                if (!line.isEmpty()) {
                    parseLine(line);
                }
            }
        }
    }

    private void parseLine(String line) {
        ArrayList<String> parts = RecordStorage.splitLine(line);
        if (parts.isEmpty()) {
            return;
        }
        String type = parts.get(0);

        if (type.equals("P") && parts.size() >= 4) {
            try {
                String creator = RecordStorage.decode(parts.get(1));
                double target = Double.parseDouble(parts.get(2));
                String name = RecordStorage.decode(parts.get(3));
                double weeklyTarget = parts.size() >= 5 ? Double.parseDouble(parts.get(4)) : 0;
                String targetDate = parts.size() >= 6 ? RecordStorage.decode(parts.get(5)) : "";
                if (findByCreatorAndName(creator, name) == null) {
                    list.add(new PersonalSavings(creator, target, name, weeklyTarget, targetDate));
                }
            } catch (Exception ignored) {
            }
        } else if (type.equals("C") && parts.size() >= 6) {
            PersonalSavings p = findByCreatorAndName(
                    RecordStorage.decode(parts.get(1)),
                    RecordStorage.decode(parts.get(2))
            );
            if (p != null) {
                try {
                    double amount = Double.parseDouble(parts.get(3));
                    LocalDate date = parseDate(parts.get(4));
                    String note = RecordStorage.decode(parts.get(5));
                    long timestamp = parts.size() >= 7 ? Long.parseLong(parts.get(6).trim()) : 0L;
                    p.addContribution(p.getCreator(), amount, date, note, timestamp);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.length() < 10) {
            return LocalDate.now();
        }
        try {
            int y = Integer.parseInt(s.substring(0, 4));
            int m = Integer.parseInt(s.substring(5, 7));
            int d = Integer.parseInt(s.substring(8, 10));
            return LocalDate.of(y, m, d);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}