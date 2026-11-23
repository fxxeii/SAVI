package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;

// REMINDER LIST (backend linked list + file storage)
// File format (Reminders_Records.txt):
//   title,groupName,yyyy-MM-dd,time,amount,completed,owner
public class ReminderList {

    private Reminder head;
    private final String fileRec = "Reminders_Records.txt";

    // CREATE
    public void addReminder(String title, String groupName, LocalDate date, String time, double amount) {
        addReminder("", title, groupName, date, time, amount);
    }

    public void addReminder(String owner, String title, String groupName, LocalDate date, String time, double amount) {
        Reminder node = new Reminder(owner, title, groupName, date, time, amount, false);
        appendNode(node);
        saveAll();
    }

    // READ (filtered by logged-in user; auto-claims legacy 6-column records with no owner yet)
    public ArrayList<Reminder> getUpcoming(String username) {
        claimLegacyRemindersIfNeeded(username);
        ArrayList<Reminder> result = new ArrayList<>();
        Reminder curr = head;
        while (curr != null) {
            if (!curr.isCompleted() && curr.isOwnedBy(username)) {
                result.add(curr);
            }
            curr = curr.getNext();
        }
        return result;
    }

    public ArrayList<Reminder> getCompleted(String username) {
        claimLegacyRemindersIfNeeded(username);
        ArrayList<Reminder> result = new ArrayList<>();
        Reminder curr = head;
        while (curr != null) {
            if (curr.isCompleted() && curr.isOwnedBy(username)) {
                result.add(curr);
            }
            curr = curr.getNext();
        }
        return result;
    }

    public int countUpcoming(String username) {
        claimLegacyRemindersIfNeeded(username);
        int count = 0;
        Reminder curr = head;
        while (curr != null) {
            if (!curr.isCompleted() && curr.isOwnedBy(username)) {
                count++;
            }
            curr = curr.getNext();
        }
        return count;
    }

    public int countCompleted(String username) {
        claimLegacyRemindersIfNeeded(username);
        int count = 0;
        Reminder curr = head;
        while (curr != null) {
            if (curr.isCompleted() && curr.isOwnedBy(username)) {
                count++;
            }
            curr = curr.getNext();
        }
        return count;
    }

    // If Reminders_Records.txt still has older 6-column rows without an owner, assign them to the active user
    // and rewrite the TXT file as 7-column rows so they stay private to that account.
    private void claimLegacyRemindersIfNeeded(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        boolean upgraded = false;
        Reminder curr = head;
        while (curr != null) {
            if (curr.getOwner() == null || curr.getOwner().trim().isEmpty()) {
                curr.setOwner(username.trim());
                upgraded = true;
            }
            curr = curr.getNext();
        }
        if (upgraded) {
            saveAll();
        }
    }

    // UPDATE
    public void markCompleted(Reminder target) {
        if (target == null) {
            return;
        }
        Reminder curr = head;
        while (curr != null) {
            if (curr == target) {
                curr.markCompleted();   // the only way to change the status (Reminder owns it)
                saveAll();
                return;
            }
            curr = curr.getNext();
        }
    }

    public void toggleCompleted(Reminder target) {
        if (target == null) {
            return;
        }
        Reminder curr = head;
        while (curr != null) {
            if (curr == target) {
                curr.toggleCompleted(); // switches between Upcoming and Completed and saves to file
                saveAll();
                return;
            }
            curr = curr.getNext();
        }
    }

    /** A user changed their username: their reminders follow. */
    public void renameOwner(String oldName, String newName) {
        if (oldName == null || newName == null || newName.trim().isEmpty()) {
            return;
        }
        Reminder curr = head;
        while (curr != null) {
            if (curr.isOwnedBy(oldName)) {
                curr.setOwner(newName);
            }
            curr = curr.getNext();
        }
        saveAll();
    }

    // DELETE
    public void deleteReminder(Reminder target) {
        if (target == null || head == null) {
            return;
        }
        if (head == target) {
            head = head.getNext();
            saveAll();
            return;
        }
        Reminder curr = head;
        while (curr.getNext() != null) {
            if (curr.getNext() == target) {
                curr.setNext(curr.getNext().getNext());
                saveAll();
                return;
            }
            curr = curr.getNext();
        }
    }

    // LINKED LIST HELPER
    private void appendNode(Reminder node) {
        if (head == null) {
            head = node;
            return;
        }
        Reminder curr = head;
        while (curr.getNext() != null) {
            curr = curr.getNext();
        }
        curr.setNext(node);
    }

    // PERSISTENCE (Storage)
    private void saveAll() {
        StringBuilder sb = new StringBuilder();
        Reminder curr = head;
        while (curr != null) {
            sb.append(RecordStorage.encode(curr.getTitle())).append(",")
                    .append(RecordStorage.encode(curr.getGroupName())).append(",")
                    .append(curr.getDate() != null ? curr.getDate().toString() : LocalDate.now().toString()).append(",")
                    .append(RecordStorage.encode(curr.getTime())).append(",")
                    .append(curr.getAmount()).append(",")
                    .append(curr.isCompleted()).append(",")
                    .append(RecordStorage.encode(curr.getOwner())).append("\n");
            curr = curr.getNext();
        }
        RecordStorage.writeLocal(fileRec, sb.toString());
    }

    public void load() {
        head = null;
        loadText(RecordStorage.loadOrSeed(ReminderList.class, fileRec));
    }

    private void loadText(String text) {
        for (String line : RecordStorage.lines(text)) {
            parseLine(line);
        }
    }

    private void parseLine(String line) {
        ArrayList<String> parts = RecordStorage.splitLine(line);
        if (parts.size() < 6) {
            return;
        }
        try {
            String title = RecordStorage.decode(parts.get(0));
            String groupName = RecordStorage.decode(parts.get(1));
            LocalDate date = RecordStorage.parseDate(parts.get(2));
            String time = RecordStorage.decode(parts.get(3));
            double amount = Double.parseDouble(parts.get(4));
            boolean completed = Boolean.parseBoolean(parts.get(5));
            String owner = parts.size() >= 7 ? RecordStorage.decode(parts.get(6)) : "";
            appendNode(new Reminder(owner, title, groupName, date, time, amount, completed));
        } catch (Exception ignored) {
        }
    }
}