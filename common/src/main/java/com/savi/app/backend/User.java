package com.savi.app.backend;
//import java.util.Scanner;
//user infos, log in, sign up, sign out, update profile, add/del profile photo

class Node {
    String name;
    String pass;
    String email;
    Node pntnext;
}
public class User {
    //Scanner s = new Scanner(System.in);

    Node head;

    void signup(String n, String e, String p){
        Node newNode = new Node();
        newNode.name = n;
        newNode.email = e;
        newNode.pass = p;

        if (head==null){
            head = newNode;
        }
        else{
            Node temp = head;
            
            while(temp.pntnext != null){
                temp = temp.pntnext;
            }
            temp.pntnext = newNode;
        }
    }

    void deluser(String n){
        Node p = head;
        
        if(exist(n)){
            if(n.equalsIgnoreCase(p.name)){
                head = p.pntnext;
            }
            else{
                Node q = head;
                while(q.pntnext != null && !q.pntnext.name.equalsIgnoreCase(n)){
                    q = q.pntnext;
                }
                if(q.pntnext != null){
                    q.pntnext = q.pntnext.pntnext;
                }
            } //deleted
        }//not found
    }

    private boolean exist(String n) {
        Node p = head;
        while(p!=null){
            if(n.equalsIgnoreCase(p.name)){
                return true;
            }
            p = p.pntnext;
        }
        return false;
    }
}
