package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// PERSONAL LIST (manages PersonalSavings + saves to Personal_Records.txt via Codename One Storage)
// File format:
//   P,creator,targetAmount,savingsName[,dailyTarget,createdDate,createdTimestamp]
//   C,creator,savingsName,amount,date,note[,timestamp]
public class PersonalList implements SavingsStore {

    private final ArrayList<PersonalSavings> list = new ArrayList<>();
    private final String fileRec = "Personal_Records.txt";

    // CREATE (Overloaded for backward compatibility)
    public String addPersonalSavings(String creator, double targetAmount, String savingsName) {
        return addPersonalSavings(creator, targetAmount, savingsName, 0, LocalDate.now());
    }

    public String addPersonalSavings(String creator, double targetAmount, String savingsName,
                                     double dailyTarget, LocalDate createdDate) {
        if (creator == null || creator.trim().isEmpty()) {
            return "Creator name is required.";
        }
        if (savingsName == null || savingsName.trim().isEmpty()) {
            return "Savings name is required.";
        }
        if (targetAmount <= 0) {
            return "Target amount must be greater than 0.";
        }
        if (dailyTarget < 0) {
            return "Daily target amount cannot be negative.";
        }
        if (findByCreatorAndName(creator, savingsName) != null) {
            return "You already have a personal saving with that name.";
        }

        list.add(new PersonalSavings(creator.trim(), targetAmount, savingsName.trim(),
                dailyTarget, createdDate, System.currentTimeMillis()));
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
        for (PersonalSavings p : list) {
            if (p.isOwnedBy(username)) {
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
    /** member = the owner of the savings (a personal savings has only one member). */
    @Override
    public boolean addContribution(String savingsName, String member, double amount, LocalDate date, String note) {
        PersonalSavings p = findByCreatorAndName(member, savingsName);
        if (p == null || amount == 0) {
            return false;
        }
        try {
            p.addContribution(p.getCreator(), amount, date, note);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return saveAll();
    }

    /** A user changed their username: their personal savings follow. */
    public boolean renameOwner(String oldName, String newName) {
        for (PersonalSavings p : list) {
            p.renameMember(oldName, newName);
        }
        return saveAll();
    }

    @Override
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
                    .append(p.getDailyTarget()).append(",")
                    .append(p.getCreatedDate() != null ? p.getCreatedDate().toString() : LocalDate.now().toString()).append(",")
                    .append(p.getCreatedTimestamp()).append("\n");

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
        for (String line : RecordStorage.lines(text)) {
            parseLine(line);
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
                double dailyTarget = parts.size() >= 5 ? Double.parseDouble(parts.get(4)) : 0.0;
                LocalDate createdDate = (parts.size() >= 6 && !parts.get(5).trim().isEmpty())
                        ? RecordStorage.parseDate(parts.get(5))
                        : LocalDate.now();
                long createdTimestamp = 0L;
                if (parts.size() >= 7) {
                    try {
                        createdTimestamp = Long.parseLong(parts.get(6).trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (findByCreatorAndName(creator, name) == null) {
                    list.add(new PersonalSavings(creator, target, name, dailyTarget, createdDate, createdTimestamp));
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
                    LocalDate date = RecordStorage.parseDate(parts.get(4));
                    String note = RecordStorage.decode(parts.get(5));
                    long timestamp = parts.size() >= 7 ? Long.parseLong(parts.get(6).trim()) : 0L;
                    p.addContribution(p.getCreator(), amount, date, note, timestamp);
                } catch (Exception ignored) {
                }
            }
        }
    }
}