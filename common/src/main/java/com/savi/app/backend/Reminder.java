package com.savi.app.backend;
import java.time.LocalDate;

public class Reminder {
    String title;
    String groupName;
    LocalDate date;
    String time;
    double amount;
    boolean completed;
    Reminder next;
}