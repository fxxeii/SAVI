package com.savi.app.backend;

import java.time.LocalDate;

/**
 * ABSTRACTION: what Transfer / the Add menu need from ANY list of savings (GroupList, PersonalList).
 * They talk to this interface, so they never need to know which kind of list they are saving to.
 */
public interface SavingsStore {
    /** Adds a contribution (a negative amount is a transfer out) and saves it to the file. */
    boolean addContribution(String savingsName, String member, double amount, LocalDate date, String note);

    /** Writes every record to the file again. */
    boolean saveChanges();
}
