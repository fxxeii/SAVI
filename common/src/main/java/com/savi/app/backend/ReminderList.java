package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;

// REMINDER LIST (backend linked list + file storage)
// File format (Reminders_Records.txt):
//   title,groupName,yyyy-MM-dd,time,amount,completed
public class ReminderList {

    private Reminder head;
    private final String fileRec = "Reminders_Records.txt";

    // CREATE
    public void addReminder(String title, String groupName, LocalDate date, String time, double amount) {
        Reminder node = new Reminder(title, groupName, date, time, amount, false);
        appendNode(node);
        saveAll();
    }

    // READ
    public ArrayList<Reminder> getUpcoming() {
        ArrayList<Reminder> result = new ArrayList<>();
        Reminder curr = head;
        while (curr != null) {
            if (!curr.isCompleted()) {
                result.add(curr);
            }
            curr = curr.getNext();
        }
        return result;
    }

    public ArrayList<Reminder> getCompleted() {
        ArrayList<Reminder> result = new ArrayList<>();
        Reminder curr = head;
        while (curr != null) {
            if (curr.isCompleted()) {
                result.add(curr);
            }
            curr = curr.getNext();
        }
        return result;
    }

    public int countUpcoming() {
        int count = 0;
        Reminder curr = head;
        while (curr != null) {
            if (!curr.isCompleted()) {
                count++;
            }
            curr = curr.getNext();
        }
        return count;
    }

    public int countCompleted() {
        int count = 0;
        Reminder curr = head;
        while (curr != null) {
            if (curr.isCompleted()) {
                count++;
            }
            curr = curr.getNext();
        }
        return count;
    }

    // UPDATE
    public void markCompleted(Reminder target) {
        if (target == null) {
            return;
        }
        Reminder curr = head;
        while (curr != null) {
            if (curr == target) {
                curr.setCompleted(true);
                saveAll();
                return;
            }
            curr = curr.getNext();
        }
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
                    .append(curr.isCompleted()).append("\n");
            curr = curr.getNext();
        }
        RecordStorage.writeLocal(fileRec, sb.toString());
    }

    public void load() {
        head = null;
        loadText(RecordStorage.loadOrSeed(ReminderList.class, fileRec));
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
        if (parts.size() < 6) {
            return;
        }
        try {
            String title = RecordStorage.decode(parts.get(0));
            String groupName = RecordStorage.decode(parts.get(1));
            LocalDate date = parseDate(parts.get(2));
            String time = RecordStorage.decode(parts.get(3));
            double amount = Double.parseDouble(parts.get(4));
            boolean completed = Boolean.parseBoolean(parts.get(5));
            appendNode(new Reminder(title, groupName, date, time, amount, completed));
        } catch (Exception ignored) {
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