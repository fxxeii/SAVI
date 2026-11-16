package com.savi.app.backend;

import com.savi.app.SAVI;
import java.time.LocalDate;

public class SampleData {

    public static void load() {
        GroupSavings g = new GroupSavings("December Vacation", 5000, "December Vacation", 35);

        g.addMember("Air");
        g.addMember("Faye");
        g.addMember("Shy");
        g.addMember("Sam");

        g.addContribution("Air", 10, LocalDate.now(), "Completed daily round");
        g.addContribution("Faye", 10, LocalDate.now(), "Completed daily round");
        g.addContribution("Shy", 10, LocalDate.now(), "Completed daily round");
        g.addContribution("Sam", 10, LocalDate.now().minusDays(1), "Paid late");

        SAVI.groups.add(g);
    }
}