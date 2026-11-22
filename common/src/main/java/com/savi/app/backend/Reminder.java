package com.savi.app.backend;

import java.time.LocalDate;

// REMINDER - one reminder record, and also ONE NODE of the linked list in ReminderList.
// ENCAPSULATION: every field is private. Other classes can only read them through getters,
// and the only way to change "completed" is markCompleted().
public class Reminder {
    private String title;
    private String groupName;      // which savings group the reminder belongs to
    private LocalDate date;
    private String time;           // stored as text, e.g. "10:00 AM"
    private double amount;         // amount to collect
    private boolean completed;     // false = Upcoming tab, true = Completed tab
    private Reminder next;         // DATA STRUCTURE: pointer to the next node (singly linked list)

    public Reminder(String title, String groupName, LocalDate date, String time, double amount) {
        this(title, groupName, date, time, amount, false);   // every new reminder starts as upcoming
    }

    // used when loading from the file (the record may already be completed)
    public Reminder(String title, String groupName, LocalDate date, String time, double amount, boolean completed) {
        this.title = title;
        this.groupName = groupName;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.completed = completed;
        this.next = null;          // a new node points to nothing until it is linked
    }

    // getters (read-only access)
    public String getTitle() { return title; }
    public String getGroupName() { return groupName; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public double getAmount() { return amount; }
    public boolean isCompleted() { return completed; }

    // the only way to change the status
    public void markCompleted() { completed = true; }

    // used by ReminderList to link the nodes
    public Reminder getNext() { return next; }
    public void setNext(Reminder next) { this.next = next; }
}