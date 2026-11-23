package com.savi.app.backend;
//user infos, log in, sign up, sign out

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;

class Node {
    String name;
    String pass;
    Node pntnext;

    Node(String n, String p) {
        name = n;
        pass = p;
        pntnext = null;
    }
}
public class User {
    Node head;
    Node currentUser;
    String fileRec = "Users_Records.txt";

    public String signup(String n, String p, String cp) {
        if (!fieldsComplete(n, p, cp)) { //is it empty
            return "Please fill in all fields.";
        }

        n = n.trim(); //remove spaces

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

        Node newNode = new Node(n.trim(), p); //stores the new user's info
        //add node
        if (head == null) { //if empty new node becomes the first
            head = newNode;
        } else {
            Node temp = head; //temp starts at the first node

            while (temp.pntnext != null) { //loop until it reaches last
                temp = temp.pntnext;
            }
            temp.pntnext = newNode; //connects last node to new user
        }

        save(n, p); //save
        return "success";
    }

    public String login(String n, String p) {
        if (!validUsername(n)) { //is it empty
            return "Please enter your username.";
        }
        if (!validLoginPassword(p)) { //is it empty
            return "Please enter your password.";
        }

        Node temp = head; //search for the head

        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n) && temp.pass.equals(p)) { //compare pass and usn
                currentUser = temp; // if success it becomes the current user, remember which ac is logged in
                return "success";
            }
            temp = temp.pntnext;
        }
        return "Incorrect username or password.";
    }

    public void logout() {
        currentUser = null;
    } //clear current user

    public String resetPass(String n, String p, String cp) {
        if (n == null || n.trim().isEmpty()
                || p == null || p.isEmpty()
                || cp == null || cp.isEmpty()) {
            return "Please fill in all fields.";
        } // is it empty

        if (!validPassword(p)) {
            return "Password must be at least 5 characters.";
        }

        if (!matchingPassword(p, cp)) {
            return "Passwords do not match.";
        }

        Node temp = head;

        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n.trim())) { //search name in the ll
                temp.pass = p; //replace old pass to the new one
                saveAll(); //update recs
                return "success";
            }
            temp = temp.pntnext;
        }
        return "Username not found.";
    }

    private boolean exist(String n) {
        Node temp = head;

        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n)) { //search through the ll if usn already existed
                return true;
            }
            temp = temp.pntnext;
        }
        return false;
    }

    public String getCurrentName() {
        if (currentUser == null) { //for the frontend to access the current logged-in user
            return null;
        }
        return currentUser.name;
    }

    private boolean validName(String n) {
        return n != null && n.trim().length() >= 3; //cant be empty, must be 3 letter above
    }

    private boolean validPassword(String p) {
        return p != null && p.length() >= 5; //cant be empty, must be 5 characs above
    }

    private boolean matchingPassword(String p, String cp) {
        return p != null && p.equals(cp);
    }

    private boolean fieldsComplete(String n, String p, String cp) {
        return n != null && !n.trim().isEmpty() //no space, not empty
                && p != null && !p.isEmpty()
                && cp != null && !cp.isEmpty();
    }

    private boolean validUsername(String n) {
        return n != null && !n.trim().isEmpty();
    }

    private boolean validLoginPassword(String p) {
        return p != null && !p.isEmpty();
    }

    // ---------------------------------------------------------------- FILE HANDLING (TXT)
    // Codename One does not allow FileWriter/FileReader, so the TXT text is kept with Storage.
    // File format, one account per line:  name,password

    /** Adds ONE new account to the end of the file (used by signup). */
    private void save(String name, String pass) {
        // Storage cannot append, so read the old text and write it back with the new line added
        writeFile(readFile() + toLine(name, pass));
        Log.p("SAVED: " + name);
    }

    /** Rewrites the whole file from the linked list (used after a delete or an update). */
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
                continue;   // blank or damaged line: skip it
            }
            if (exist(node.name)) {
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

    // a comma or new line typed into a field would break the TXT, so those characters are
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
            try (InputStream in = Storage.getInstance().createInputStream(fileRec)) {
                return Util.readToString(in, "UTF-8");
            }
        } catch (Exception ex) {
            Log.e(ex);
            return "";
        }
    }

    private String readRecords() {
        try {
            InputStream resource = Display.getInstance().getResourceAsStream(User.class, "/" + fileRec);
            if (resource == null) {
                return "";
            }
            try (InputStream in = resource) {
                return Util.readToString(in, "UTF-8");
            }
        } catch (Exception ex) {
            Log.e(ex);
            return "";
        }
    }

    private void writeFile(String text) {
        try (OutputStream out = Storage.getInstance().createOutputStream(fileRec)) {
            out.write(text.getBytes("UTF-8"));
        } catch (Exception ex) {
            Log.e(ex);
        }
    }
}