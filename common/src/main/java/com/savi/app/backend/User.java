package com.savi.app.backend;
//user infos, log in, sign up, sign out, update profile, add/del profile photo

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
}

