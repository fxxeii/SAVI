package com.savi.app.backend;

import java.time.LocalDate;

public class Contribution {

    private final String member;
    private final double amount;
    private final LocalDate date;
    private final String note;

    public Contribution(String member, double amount, LocalDate date, String note) {

        if (member == null || member.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required.");
        }

        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }

        this.member = member.trim();
        this.amount = amount;
        this.date = date;
        this.note = note == null ? "" : note;
    }

    public String getMember() {
        return member;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}
