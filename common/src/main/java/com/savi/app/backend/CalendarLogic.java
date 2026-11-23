package com.savi.app.backend;

// CALENDAR LOGIC - the backend of the Calendar page.
// ABSTRACTION: the page only asks questions ("what is the day text?", "which trackers saved on this date?")
// and this class does the thinking. It has no screen code and no file code.

import com.codename1.l10n.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarLogic {

    // The calendar component gives a java.util.Date, but our savings data uses LocalDate.
    // Formatting the Date as "yyyy-MM-dd" text and parsing it backdrops the time part.
    public LocalDate toLocalDate(Date date) {
        return LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
    }

    // "Today", "Yesterday", "15 days ago", ...
    // Both dates are turned into midnight first, so the difference is always a whole number of days.
    public String getDayText(LocalDate selected) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date selectedDate = f.parse(selected.toString());
            Date todayDate = f.parse(LocalDate.now().toString());
            // 86400000 = milliseconds in one day. Positive diff = the selected date is in the past.
            long diff = Math.round((todayDate.getTime() - selectedDate.getTime()) / 86400000.0);

            if (diff == 0) return "Today";
            if (diff == 1) return "Yesterday";
            if (diff == -1) return "Tomorrow";
            if (diff > 1) return diff + " days ago";
            return "In " + (-diff) + " days";
        } catch (Exception ex) {
            return "";
        }
    }

    // trackers (both GroupSavings and PersonalSavings) that have at least one contribution on this date
    // getContributionsOn(...) is inherited from Savings (INHERITANCE & POLYMORPHISM)
    public ArrayList<Savings> getTrackersWithRecords(List<? extends Savings> trackers, LocalDate date) {
        ArrayList<Savings> result = new ArrayList<>();
        if (trackers == null || date == null) {
            return result;
        }
        for (Savings s : trackers) {
            if (!s.getContributionsOn(date).isEmpty()) {
                result.add(s);
            }
        }
        return result;
    }

    // "Saved" if something was contributed on this date, otherwise "Pending"
    public String getStatus(Savings tracker, LocalDate date) {
        if (tracker.getTotalOn(date) > 0) {
            return "Saved";
        }
        return "Pending";
    }

    // all notes found on that date (each contributor's note on its own line)
    public ArrayList<String> getNotesOn(Savings tracker, LocalDate date) {
        ArrayList<String> notes = new ArrayList<>();
        for (Contribution c : tracker.getContributionsOn(date)) {
            if (c.getNote() != null && !c.getNote().trim().isEmpty()) {
                if (tracker.isPersonal()) {
                    notes.add(c.getNote().trim());
                } else {
                    notes.add(c.getMember() + ": " + c.getNote().trim());
                }
            }
        }
        return notes;
    }
}