package com.savi.app.backend;
//user infos, log in, sign up, sign out


import java.util.ArrayList;
import java.util.List;

public class User {

    // ENCAPSULATION: the linked-list node is private to User. Nobody else can touch the nodes or the head.
    private static class Node {
        private String name;
        private String pass;
        private String email;
        private Node pntnext;

        private Node(String n, String e, String p) {
            name = n;
            email = e;
            pass = p;
            pntnext = null;
        }
    }

    private Node head;
    private Node currentUser;
    private final String fileRec = "Users_Records.txt";

    public String signup(String n, String e, String p, String cp) {
        if (!fieldsComplete(n, e, p, cp)) { //is it empty
            return "Please fill in all fields.";
        }

        n = n.trim(); //remove spaces
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
        if (emailExists(e)) {
            return "Email already registered.";
        }
        if (exist(n)) {
            return "Username already exists.";
        }

        Node newNode = new Node(n, e, p); //stores the new user's info (n and e are already trimmed)
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

        save(n, e, p); //save
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
            if (temp.name.equalsIgnoreCase(n.trim()) && temp.pass.equals(p)) { //compare pass and usn
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

    /** Changes a username in the user list + file. Returns "success" or the message to show. */
    public String rename(String oldName, String newName) {
        if (!validName(newName)) {
            return "Username must be at least 3 characters.";
        }
        String clean = newName.trim();
        if (!clean.equalsIgnoreCase(oldName) && exist(clean)) {
            return "Username already exists.";
        }
        for (Node temp = head; temp != null; temp = temp.pntnext) {
            if (temp.name.equalsIgnoreCase(oldName)) {
                temp.name = clean;
                saveAll();
                return "success";
            }
        }
        return "Username not found.";
    }

    /** Every username, in order. Other classes read the accounts through this instead of walking the nodes. */
    public List<String> getUsernames() {
        List<String> names = new ArrayList<String>();
        for (Node temp = head; temp != null; temp = temp.pntnext) {
            names.add(temp.name);
        }
        return names;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
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

    public boolean emailExists(String e) {
        if (e == null) {
            return false;
        }
        Node temp = head;

        while (temp != null) {
            if (temp.email != null && temp.email.equalsIgnoreCase(e.trim())) { //search through the ll if email already existed
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


    public String getCurrentEmail() {
        if (currentUser == null) {
            return null;
        }
        return currentUser.email;
    }

    private boolean validName(String n) {
        return n != null && n.trim().length() >= 3; //cant be empty, must be 3 letter above
    }

    public boolean validEmail(String e) {
        if (e == null) {
            return false;
        }

        int at = e.indexOf('@'); //finds the index of @
        int dot = e.lastIndexOf('.'); //finds the index of .
        //@ must not be in beginning, the . must be at least 2 position after @, . cannot be last, no spaces
        return at > 0 && dot > at + 1 && dot < e.length() - 1 && e.indexOf(' ') == -1;
    }

    private boolean validPassword(String p) {
        return p != null && p.length() >= 5; //cant be empty, must be 5 characs above
    }

    private boolean matchingPassword(String p, String cp) {
        return p != null && p.equals(cp);
    }

    private boolean fieldsComplete(String n, String e, String p, String cp) {
        return n != null && !n.trim().isEmpty() //no space, not empty
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

    // ---------------------------------------------------------------- FILE HANDLING (TXT)
    // All reading/writing/encoding goes through RecordStorage (the same code the other lists use).
    // File format, one account per line:  name, email, password

    /** Adds ONE new account to the end of the file (used by signup). */
    private void save(String name, String email, String pass) {
        // Storage cannot append, so read the old text and write it back with the new line added
        RecordStorage.writeLocal(fileRec, RecordStorage.readLocal(fileRec) + toLine(name, email, pass));
    }

    /** Rewrites the whole file from the linked list (used after an update). */
    private void saveAll() {
        StringBuilder text = new StringBuilder();
        Node temp = head;

        while (temp != null) {
            text.append(toLine(temp.name, temp.email, temp.pass));
            temp = temp.pntnext;
        }
        RecordStorage.writeLocal(fileRec, text.toString());
    }

    public void load() {
        head = null;
        currentUser = null;

        loadText(RecordStorage.readLocal(fileRec));
        loadText(RecordStorage.readResource(User.class, fileRec));   // accounts bundled with the app
    }

    private void loadText(String text) {
        for (String line : RecordStorage.lines(text)) {
            Node node = toNode(line);
            if (node == null) {
                continue;   // damaged line: skip it
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
        return RecordStorage.encode(name) + "," + RecordStorage.encode(email) + "," + RecordStorage.encode(pass) + "\n";
    }

    private Node toNode(String line) {
        int c1 = line.indexOf(',');
        int c2 = line.indexOf(',', c1 + 1);

        if (c1 < 0 || c2 < 0) {
            return null;
        }

        return new Node(RecordStorage.decode(line.substring(0, c1)),
                RecordStorage.decode(line.substring(c1 + 1, c2)),
                RecordStorage.decode(line.substring(c2 + 1)));
    }
}
