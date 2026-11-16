package com.savi.app.backend;

import com.codename1.l10n.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class CalendarLogic {

    // converts the Calendar component's Date into a LocalDate
    public LocalDate toLocalDate(Date date) {
        return LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
    }

    // "Today", "Yesterday", "15 days ago", ...
    public String getDayText(LocalDate selected) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date selectedDate = f.parse(selected.toString());
            Date todayDate = f.parse(LocalDate.now().toString());
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

    // groups that have at least one contribution on this date
    public ArrayList<GroupSavings> getGroupsWithRecords(ArrayList<GroupSavings> groups, LocalDate date) {
        ArrayList<GroupSavings> result = new ArrayList<>();
        for (GroupSavings g : groups) {
            if (!g.getContributionsOn(date).isEmpty()) {
                result.add(g);
            }
        }
        return result;
    }

    // "Saved" if something was contributed on this date, otherwise "Pending"
    public String getStatus(GroupSavings group, LocalDate date) {
        if (group.getTotalOn(date) > 0) {
            return "Saved";
        }
        return "Pending";
    }

    // the note shown at the bottom of the card (first note found on that date)
    public String getNoteOn(GroupSavings group, LocalDate date) {
        for (Contribution c : group.getContributionsOn(date)) {
            if (c.getNote() != null && !c.getNote().isEmpty()) {
                return c.getNote();
            }
        }
        return "";
    }

    // 30.0 -> "₱30.00" (String.format isn't available in Codename One)
    public String formatAmount(double amount) {
        long cents = Math.round(amount * 100);
        long pesos = cents / 100;
        long rem = cents % 100;
        return "₱" + pesos + "." + (rem < 10 ? "0" : "") + rem;
    }
}