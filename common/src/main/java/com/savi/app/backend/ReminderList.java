package com.savi.app.backend;

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

// REMINDER LIST - the backend of the Reminder page.
// DATA STRUCTURE: a singly linked list of Reminder nodes (head -> next -> next -> null).
// FILE HANDLING: every change is saved to Reminders_Records.csv, and load() reads it back at startup.
// ENCAPSULATION: head is private, so the pages can only use the methods below.
public class ReminderList {
    private Reminder head;                                   // first node of the list
    private final String fileRec = "Reminders_Records.csv";

    // create a node, link it at the end of the list, then save
    public void addReminder(String title, String groupName, LocalDate date, String time, double amount) {
        Reminder node = new Reminder(title, groupName, date, time, amount);
        insertNode(node);
        saveAll();
    }

    // INSERT AT TAIL: if the list is empty the node becomes the head,
    // otherwise walk to the last node and link the new one after it
    private void insertNode(Reminder node) {
        if (head == null) {
            head = node;
        } else {
            Reminder temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
            }
            temp.setNext(node);
        }
    }

    // TRAVERSE the list until we find the same node, mark it done, and save
    public void markCompleted(Reminder target) {
        Reminder temp = head;
        while (temp != null) {
            if (temp == target) {
                temp.markCompleted();
                saveAll();
                return;
            }
            temp = temp.getNext();
        }
    }

    // TRAVERSAL: collect the reminders that are not completed (the Upcoming tab)
    public ArrayList<Reminder> getUpcoming() {
        ArrayList<Reminder> list = new ArrayList<>();
        Reminder temp = head;
        while (temp != null) {
            if (!temp.isCompleted()) {
                list.add(temp);
            }
            temp = temp.getNext();
        }
        return list;
    }

    // TRAVERSAL: collect the reminders that are completed (the Completed tab)
    public ArrayList<Reminder> getCompleted() {
        ArrayList<Reminder> list = new ArrayList<>();
        Reminder temp = head;
        while (temp != null) {
            if (temp.isCompleted()) {
                list.add(temp);
            }
            temp = temp.getNext();
        }
        return list;
    }

    // numbers shown in the tab badges, e.g. "Upcomings (3)"
    public int countUpcoming() {
        return getUpcoming().size();
    }

    public int countCompleted() {
        return getCompleted().size();
    }

    // ---------------------------------------------------------------- FILE HANDLING (CSV)
    // Format per line: title,groupName,yyyy-MM-dd,time,amount,completed

    // Rewrites the whole file from the linked list (Storage cannot append).
    private void saveAll() {
        StringBuilder sb = new StringBuilder();
        Reminder temp = head;
        while (temp != null) {
            // encode() protects commas inside text so they do not break the CSV
            sb.append(encode(temp.getTitle())).append(",")
                    .append(encode(temp.getGroupName())).append(",")
                    .append(temp.getDate().toString()).append(",")
                    .append(encode(temp.getTime())).append(",")
                    .append(temp.getAmount()).append(",")
                    .append(temp.isCompleted()).append("\n");
            temp = temp.getNext();
        }
        try {
            // Codename One has no FileWriter, so the text is saved with Storage
            OutputStream out = Storage.getInstance().createOutputStream(fileRec);
            out.write(sb.toString().getBytes("UTF-8"));
            out.close();
            // copy these lines into src/main/resources/Reminders_Records.csv to share them
            System.out.println("REMINDERS CSV NOW:\n" + sb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Called once at startup: rebuilds the linked list from the saved text.
    public void load() {
        head = null;

        loadText(readRecords());   // shared file from resources
        loadText(readFile());      // this device's saved file
    }

    // split the text into lines, turn each line into a Reminder, and link it into the list
    private void loadText(String text) {
        int start = 0;
        while (start < text.length()) {
            int end = text.indexOf('\n', start);
            if (end == -1) end = text.length();
            String line = text.substring(start, end);
            start = end + 1;
            if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);

            Reminder r = parseLine(line);
            if (r == null) continue;          // damaged or blank line: skip it

            Reminder same = findSame(r);
            if (same == null) {
                insertNode(r);
            } else if (r.isCompleted()) {
                same.markCompleted();   // "completed" wins over "upcoming"
            }
        }
    }

    // reminders have no unique name, so compare their details to avoid duplicates
    // (the same reminder can be in both the shared file and the device file)
    private Reminder findSame(Reminder r) {
        Reminder temp = head;
        while (temp != null) {
            if (temp.getTitle().equals(r.getTitle())
                    && temp.getGroupName().equals(r.getGroupName())
                    && temp.getDate().equals(r.getDate())
                    && temp.getTime().equals(r.getTime())) {
                return temp;
            }
            temp = temp.getNext();
        }
        return null;
    }

    // reads this device's saved file (empty text if nothing was saved yet)
    private String readFile() {
        try {
            if (!Storage.getInstance().exists(fileRec)) {
                return "";
            }
            InputStream in = Storage.getInstance().createInputStream(fileRec);
            String text = Util.readToString(in, "UTF-8");
            return text == null ? "" : text;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    // reads the shared starter file packaged inside the app (src/main/resources)
    private String readRecords() {
        try {
            InputStream in = Display.getInstance().getResourceAsStream(ReminderList.class, "/" + fileRec);
            if (in == null) {
                return "";
            }
            String text = Util.readToString(in, "UTF-8");
            return text == null ? "" : text;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    // one CSV line -> one Reminder. The commas are found by position (c1..c5)
    // and the text between them is cut out with substring().
    private Reminder parseLine(String line) {
        int c1 = line.indexOf(',');
        int c2 = line.indexOf(',', c1 + 1);
        int c3 = line.indexOf(',', c2 + 1);
        int c4 = line.indexOf(',', c3 + 1);
        int c5 = line.indexOf(',', c4 + 1);
        if (c1 < 0 || c2 < 0 || c3 < 0 || c4 < 0 || c5 < 0) return null;

        try {
            String title = decode(line.substring(0, c1));
            String group = decode(line.substring(c1 + 1, c2));
            LocalDate date = LocalDate.parse(line.substring(c2 + 1, c3));
            String time = decode(line.substring(c3 + 1, c4));
            double amount = Double.parseDouble(line.substring(c4 + 1, c5));
            boolean done = "true".equalsIgnoreCase(line.substring(c5 + 1).trim());

            Reminder r = new Reminder(title, group, date, time, amount);
            if (done) r.markCompleted();
            return r;
        } catch (Exception e) {
            return null;
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