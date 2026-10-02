package com.savi.app;

import com.codename1.system.Lifecycle;

public class SAVI extends Lifecycle {

    @Override
    public void runApp() {
        new CalendarPage().show();
    }
}