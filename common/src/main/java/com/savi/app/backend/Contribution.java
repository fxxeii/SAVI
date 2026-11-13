package com.savi.app.backend;

import java.time.LocalDate;

public class Contribution {

    private String member;
    private double amount;
    private LocalDate date;
    private String note;

    public Contribution(String member, double amount, LocalDate date, String note) {
        this.member = member;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public String getMember() { return member; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
}