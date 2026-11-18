package com.savi.app.backend;

import java.util.Calendar;
import java.util.Date;

/**
 * Small date toolbox built only on java.util.Date / Calendar, because Codename One
 * does not support java.time (LocalDate, DayOfWeek, ...).
 */
public class DateHelper {

    private static final long DAY_MS = 86400000L;
    private static final String[] DAY_NAMES = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    private DateHelper() { }   // only static helpers, nobody should create one

    /** The date moved by a number of days (negative goes back in time). */
    public static Date addDays(Date d, int days) {
        return new Date(d.getTime() + days * DAY_MS);
    }

    /** true when both dates fall on the same calendar day (the time of day is ignored). */
    public static boolean sameDay(Date a, Date b) {
        Calendar ca = Calendar.getInstance();
        Calendar cb = Calendar.getInstance();
        ca.setTime(a);
        cb.setTime(b);
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR)
                && ca.get(Calendar.MONTH) == cb.get(Calendar.MONTH)
                && ca.get(Calendar.DAY_OF_MONTH) == cb.get(Calendar.DAY_OF_MONTH);
    }

    /** "02" for the date badge */
    public static String dayNumber(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return (day < 10 ? "0" : "") + day;
    }

    /** "Wednesday" for the date badge */
    public static String weekdayName(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return DAY_NAMES[c.get(Calendar.DAY_OF_WEEK) - 1];
    }
}