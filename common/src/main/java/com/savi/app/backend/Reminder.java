package com.savi.app.backend;

import java.time.LocalDate;

public class Reminder {
    private String title;
    private String groupName;
    private LocalDate date;
    private String time;
    private double amount;
    private boolean completed;
    private Reminder next;

    public Reminder(String title, String groupName, LocalDate date, String time, double amount) {
        this.title = title;
        this.groupName = groupName;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.completed = false;
        this.next = null;
    }

    public String getTitle() { return title; }
    public String getGroupName() { return groupName; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public double getAmount() { return amount; }
    public boolean isCompleted() { return completed; }
    public void markCompleted() { completed = true; }
    public Reminder getNext() { return next; }
    public void setNext(Reminder next) { this.next = next; }
}