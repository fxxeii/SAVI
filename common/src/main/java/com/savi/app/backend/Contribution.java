package com.savi.app.backend;

import java.time.LocalDate;

// CONTRIBUTION - one deposit (or one transfer out) of one member on one date.
// This is the basic record that the Calendar and Pending pages are built from.
// ENCAPSULATION: all fields are private and final, so a contribution cannot be changed
// after it is created, and the constructor refuses invalid data.
public class Contribution {

    private final String member;
    private final double amount;
    private final LocalDate date;
    private final String note;
    private long timestamp = System.currentTimeMillis();   // exact time it was recorded

    public Contribution(String member, double amount, LocalDate date, String note) {

        // VALIDATION: bad data is rejected here, so no other class has to re-check it
        if (member == null || member.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required.");
        }

        // amount cannot be 0, but it can be negative: a transfer out is stored as a negative contribution
        if (amount == 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {   // negative = money moved out (transfer)
            throw new IllegalArgumentException("Amount must be positive.");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }

        this.member = member.trim();
        this.amount = amount;
        this.date = date;
        this.note = note == null ? "" : note;   // no note is stored as an empty text, never null
    }

    // getters (read-only access)
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

    /** When the contribution was recorded (milliseconds). 0 for old records saved without a time. */
    public long getTimestamp() {
        return timestamp;
    }

    // package-private (no "public"): only classes in this package, like GroupList / PersonalList,
    // can restore the saved time when loading the TXT
    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}