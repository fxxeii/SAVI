package com.savi.app;

import com.codename1.system.Lifecycle;
import com.savi.app.frontend.CalendarPage;

public class SAVI extends Lifecycle {

    @Override
    public void runApp() {
        new CalendarPage().show();
    }
}