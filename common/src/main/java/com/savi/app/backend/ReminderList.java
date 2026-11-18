package com.savi.app.backend;

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReminderList {
    private Reminder head;
    private final String fileRec = "Reminders_Records.csv";

    public void addReminder(String title, String groupName, LocalDate date, String time, double amount) {
        Reminder node = new Reminder(title, groupName, date, time, amount);
        insertNode(node);
        saveAll();
    }

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

    public int countUpcoming() {
        return getUpcoming().size();
    }

    public int countCompleted() {
        return getCompleted().size();
    }

   // Format per line: title,groupName,yyyy-MM-dd,time,amount,completed
    private void saveAll() {
        StringBuilder sb = new StringBuilder();
        Reminder temp = head;
        while (temp != null) {
            sb.append(encode(temp.getTitle())).append(",")
                    .append(encode(temp.getGroupName())).append(",")
                    .append(temp.getDate().toString()).append(",")
                    .append(encode(temp.getTime())).append(",")
                    .append(temp.getAmount()).append(",")
                    .append(temp.isCompleted()).append("\n");
            temp = temp.getNext();
        }
        try {
            OutputStream out = Storage.getInstance().createOutputStream(fileRec);
            out.write(sb.toString().getBytes("UTF-8"));
            out.close();
            // copy these lines into src/main/resources/Reminders_Records.csv to share them
            System.out.println("REMINDERS CSV NOW:\n" + sb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
        head = null;

        loadText(readRecords());   // shared file from resources
        loadText(readFile());      // this device's saved file
    }

    private void loadText(String text) {
        int start = 0;
        while (start < text.length()) {
            int end = text.indexOf('\n', start);
            if (end == -1) end = text.length();
            String line = text.substring(start, end);
            start = end + 1;
            if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);

            Reminder r = parseLine(line);
            if (r == null) continue;

            Reminder same = findSame(r);
            if (same == null) {
                insertNode(r);
            } else if (r.isCompleted()) {
                same.markCompleted();   // "completed" wins over "upcoming"
            }
        }
    }

    // reminders have no unique name, so compare their details to avoid duplicates
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