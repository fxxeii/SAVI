package com.savi.app.backend;

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;

public class UserProfile {
    private String id;
    private String greetingName;
    private String username;
    private String handle;
    private double personalSaved;
    private boolean notificationsEnabled;
    private boolean appLockEnabled;

    // ---------------------------------------------------------------- USER.JAVA LINKED LIST & CSV
    Node head;
    Node currentUser;
    String fileRec = "Users_Records.csv";

    public UserProfile() {
        load();
    }

    public UserProfile(String id, String greetingName, String username, String handle,
                       double personalSaved, boolean notificationsEnabled, boolean appLockEnabled) {
        this.id = id;
        this.greetingName = greetingName;
        this.username = username;
        this.handle = handle;
        this.personalSaved = personalSaved;
        this.notificationsEnabled = notificationsEnabled;
        this.appLockEnabled = appLockEnabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGreetingName() {
        return (greetingName == null || greetingName.trim().isEmpty()) ? "User" : greetingName.trim();
    }

    public void setGreetingName(String greetingName) {
        this.greetingName = greetingName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public double getPersonalSaved() {
        return personalSaved;
    }

    public void setPersonalSaved(double personalSaved) {
        this.personalSaved = personalSaved;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isAppLockEnabled() {
        return appLockEnabled;
    }

    public void setAppLockEnabled(boolean appLockEnabled) {
        this.appLockEnabled = appLockEnabled;
    }

    // ---------------------------------------------------------------- EXACT METHODS FROM User.java

    public String signup(String n, String p, String cp) {
        if (!fieldsComplete(n, p, cp)) {
            return "Please fill in all fields.";
        }

        n = n.trim();

        if (!validName(n)) {
            return "Username must be at least 3 characters.";
        }
        if (!validPassword(p)) {
            return "Password must be at least 5 characters.";
        }
        if (!matchingPassword(p, cp)) {
            return "Passwords do not match.";
        }
        if (exist(n)) {
            return "Username already exists.";
        }

        Node newNode = new Node(n.trim(), p);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.pntnext != null) {
                temp = temp.pntnext;
            }
            temp.pntnext = newNode;
        }

        save(n, p);
        return "success";
    }

    public String login(String n, String p) {
        if (!validUsername(n)) {
            return "Please enter your username.";
        }
        if (!validLoginPassword(p)) {
            return "Please enter your password.";
        }

        Node temp = head;
        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n) && temp.pass.equals(p)) {
                currentUser = temp;
                this.username = temp.name;
                this.greetingName = temp.name;
                this.id = "acc_" + temp.name.toLowerCase();
                this.handle = "@" + temp.name.toLowerCase().replace(' ', '.');
                return "success";
            }
            temp = temp.pntnext;
        }
        return "Incorrect username or password.";
    }

    public void logout() {
        currentUser = null;
    }

    public String resetPass(String n, String p, String cp) {
        if (n == null || n.trim().isEmpty()
                || p == null || p.isEmpty()
                || cp == null || cp.isEmpty()) {
            return "Please fill in all fields.";
        }

        if (!validPassword(p)) {
            return "Password must be at least 5 characters.";
        }

        if (!matchingPassword(p, cp)) {
            return "Passwords do not match.";
        }

        Node temp = head;
        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n.trim())) {
                temp.pass = p;
                saveAll();
                return "success";
            }
            temp = temp.pntnext;
        }
        return "Username not found.";
    }

    private boolean exist(String n) {
        Node temp = head;
        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n)) {
                return true;
            }
            temp = temp.pntnext;
        }
        return false;
    }

    public String getCurrentName() {
        if (currentUser != null) {
            return currentUser.name;
        }
        return username;
    }

    private boolean validName(String n) {
        return n != null && n.trim().length() >= 3;
    }

    private boolean validPassword(String p) {
        return p != null && p.length() >= 5;
    }

    private boolean matchingPassword(String p, String cp) {
        return p != null && p.equals(cp);
    }

    private boolean fieldsComplete(String n, String p, String cp) {
        return n != null && !n.trim().isEmpty()
                && p != null && !p.isEmpty()
                && cp != null && !cp.isEmpty();
    }

    private boolean validUsername(String n) {
        return n != null && !n.trim().isEmpty();
    }

    private boolean validLoginPassword(String p) {
        return p != null && !p.isEmpty();
    }

    // ---------------------------------------------------------------- FILE HANDLING (Users_Records.csv)

    private void save(String name, String pass) {
        writeFile(readFile() + toLine(name, pass));
        System.out.println("SAVED: " + name);
    }

    private void saveAll() {
        StringBuilder text = new StringBuilder();
        Node temp = head;
        while (temp != null) {
            text.append(toLine(temp.name, temp.pass));
            temp = temp.pntnext;
        }
        writeFile(text.toString());
    }

    public void load() {
        head = null;
        currentUser = null;

        String local = readFile();
        String shared = readRecords();

        loadText(local);
        loadText(shared);
    }

    private void loadText(String text) {
        int start = 0;

        while (start < text.length()) {
            int end = text.indexOf('\n', start);
            if (end == -1) {
                end = text.length();
            }
            String line = text.substring(start, end);
            start = end + 1;

            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }

            Node node = toNode(line);
            if (node == null) {
                continue;
            }
            if (exist(node.name)) {
                continue;
            }

            if (head == null) {
                head = node;
            } else {
                Node tail = head;
                while (tail.pntnext != null) {
                    tail = tail.pntnext;
                }
                tail.pntnext = node;
            }
        }
    }

    private String toLine(String name, String pass) {
        return encode(name) + "," + encode(pass) + "\n";
    }

    private Node toNode(String line) {
        int c1 = line.indexOf(',');
        if (c1 < 0) {
            return null;
        }
        return new Node(
                decode(line.substring(0, c1)),
                decode(line.substring(c1 + 1))
        );
    }

    private String encode(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '%') {
                out.append("%25");
            } else if (c == ',') {
                out.append("%2C");
            } else if (c == '\n') {
                out.append("%0A");
            } else if (c == '\r') {
                out.append("%0D");
            } else {
                out.append(c);
            }
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
                if (code.equals("%25")) {
                    out.append('%');
                    i += 3;
                    continue;
                } else if (code.equals("%2C")) {
                    out.append(',');
                    i += 3;
                    continue;
                } else if (code.equals("%0A")) {
                    out.append('\n');
                    i += 3;
                    continue;
                } else if (code.equals("%0D")) {
                    out.append('\r');
                    i += 3;
                    continue;
                }
            }
            out.append(c);
            i++;
        }
        return out.toString();
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
            InputStream in = Display.getInstance().getResourceAsStream(UserProfile.class, "/" + fileRec);
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

    private void writeFile(String text) {
        try {
            OutputStream out = Storage.getInstance().createOutputStream(fileRec);
            out.write(text.getBytes("UTF-8"));
            out.close();
            System.out.println("USERS CSV NOW:\n" + text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}