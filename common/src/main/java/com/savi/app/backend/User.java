package com.savi.app.backend;
//import java.util.Scanner;
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

    public boolean signup(String n, String e, String p) {
        if (exist(n) || emailExists(e)) {
            return false;
        }

        Node newNode = new Node(n, e, p);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;

            while (temp.pntnext != null) {
                temp = temp.pntnext;
            }
            temp.pntnext = newNode;
        }
        return true;
    }

    public boolean login(String n, String p) {
        Node temp = head;

        while (temp != null) {
            if (temp.name.equalsIgnoreCase(n)
                    && temp.pass.equals(p)) {
                currentUser = temp;
                return true;
            }
            temp = temp.pntnext;
        }
        return false;
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
        Node p = head;
        Node q = null;

        while (p != null) {
            if (p.name.equalsIgnoreCase(n)) {
                if (q == null) {
                    head = p.pntnext;
                } else {
                    q.pntnext = p.pntnext;
                }
                if (currentUser == p) {
                    currentUser = null;
                }
                return true;
            }
            q = p;
            p = p.pntnext;
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
}

