package com.savi.app.backend;
//user infos, log in, sign up, sign out, update profile

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;

class Node {
    String name;
    String pass;
    String email;
    Node pntnext;

    Node(String n, String e, String p) {
        name = n;
        email = e;
        pass = p;
        pntnext = null;
    }
}
public class User {
    Node head;
    Node currentUser;
    String fileRec = "Users_Records.csv";

    public String signup(String n, String e, String p, String cp) {
        if (!fieldsComplete(n, e, p, cp)) {
            return "Please fill in all fields.";
        }

        n = n.trim();
        e = e.trim();

        if (!validName(n)) {
            return "Username must be at least 3 characters.";
        }
        if (!validEmail(e)) {
            return "Please enter a valid email address.";
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
        if (emailExists(e)) {
            return "Email already registered.";
        }

        Node newNode = new Node(n.trim(), e.trim(), p);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;

            while (temp.pntnext != null) {
                temp = temp.pntnext;
            }
            temp.pntnext = newNode;
        }

        save(n, e, p);
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

    private boolean emailExists(String e) {
        Node temp = head;

        while (temp != null) {
            if (temp.email.equalsIgnoreCase(e)) {
                return true;
            }
            temp = temp.pntnext;
        }
        return false;
    }

    public boolean deluser(String n) {
        Node temp = head;
        Node q = null;

        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n)) {
                if (q == null) {
                    head = temp.pntnext;
                } else {
                    q.pntnext = temp.pntnext;
                }
                if (currentUser == temp) {
                    currentUser = null;
                }
                saveAll();      // a line in the middle of the file is gone, so rewrite the file
                return true;
            }
            q = temp;
            temp = temp.pntnext;
        }
        return false;
    }

    public String getCurrentName() {
        if (currentUser == null) {
            return null;
        }
        return currentUser.name;
    }

    public String getCurrentEmail() {
        if (currentUser == null) {
            return null;
        }
        return currentUser.email;
    }


    private boolean validName(String n) {
        return n != null && n.trim().length() >= 3;
    }

    private boolean validEmail(String e) {
        if (e == null) {
            return false;
        }

        int at = e.indexOf('@');
        int dot = e.lastIndexOf('.');

        return at > 0
                && dot > at + 1
                && dot < e.length() - 1
                && e.indexOf(' ') == -1;
    }

    private boolean validPassword(String p) {
        return p != null && p.length() >= 5;
    }

    private boolean matchingPassword(String p, String cp) {
        return p != null && p.equals(cp);
    }

    private boolean fieldsComplete(String n, String e, String p, String cp) {
        return n != null && !n.trim().isEmpty()
                && e != null && !e.trim().isEmpty()
                && p != null && !p.isEmpty()
                && cp != null && !cp.isEmpty();
    }

    private boolean validUsername(String n) {
        return n != null && !n.trim().isEmpty();
    }

    private boolean validLoginPassword(String p) {
        return p != null && !p.isEmpty();
    }

    // ---------------------------------------------------------------- FILE HANDLING (CSV)
    // Codename One does not allow FileWriter/FileReader, so the CSV text is kept with Storage.
    // File format, one account per line:  name,email,password

    /** Adds ONE new account to the end of the file (used by signup). */
    private void save(String name, String email, String pass) {
        // Storage cannot append, so read the old text and write it back with the new line added
        writeFile(readFile() + toLine(name, email, pass));
        System.out.println("SAVED: " + name);
    }

    /** Rewrites the whole file from the linked list (used after a delete or an update). */
    private void saveAll() {
        StringBuilder text = new StringBuilder();
        Node temp = head;

        while (temp != null) {
            text.append(toLine(temp.name, temp.email, temp.pass));
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
                continue;   // blank or damaged line: skip it
            }
            if (exist(node.name) || emailExists(node.email)) {
                continue;   // already loaded (the same account is in both files)
            }

            // added straight to the list (NOT through signup, so nothing is saved twice)
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

    private String toLine(String name, String email, String pass) {
        return encode(name) + "," + encode(email) + "," + encode(pass) + "\n";
    }

    private Node toNode(String line) {
        int c1 = line.indexOf(',');
        int c2 = line.indexOf(',', c1 + 1);
        if (c1 < 0 || c2 < 0) {
            return null;
        }
        return new Node(decode(line.substring(0, c1)),
                decode(line.substring(c1 + 1, c2)),
                decode(line.substring(c2 + 1)));
    }

    // a comma or new line typed into a field would break the CSV, so those characters are
    // stored as %2C / %0A / %0D (and "%" itself as %25), then turned back when loading
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
                return "";      // first run: nothing saved yet
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
            InputStream in = Display.getInstance().getResourceAsStream(User.class, "/" + fileRec);
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
            // temporary: copy these lines into src/main/resources/Users_Records.csv to share them
            System.out.println("USERS CSV NOW:\n" + text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}