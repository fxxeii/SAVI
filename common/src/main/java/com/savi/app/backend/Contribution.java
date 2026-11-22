package com.savi.app.backend;

import java.time.LocalDate;

public class Contribution {
    private final String member;
    private final double amount;
    private final LocalDate date;
    private final String note;
    private final long timestamp;

    public Contribution(String member, double amount, LocalDate date, String note) {
        this(member, amount, date, note, System.currentTimeMillis());
    }

    public Contribution(String member, double amount, LocalDate date, String note, long timestamp) {
        if (member == null || member.trim().isEmpty()) {
            throw new IllegalArgumentException("Member is required.");
        }
        if (amount == 0) {
            throw new IllegalArgumentException("Amount cannot be zero.");
        }
        this.member = member.trim();
        this.amount = amount;
        this.date = date != null ? date : LocalDate.now();
        this.note = note != null ? note.trim() : "";
        this.timestamp = timestamp > 0 ? timestamp : System.currentTimeMillis();
    }

    public String getMember() { return member; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }
}