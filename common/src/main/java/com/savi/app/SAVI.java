package com.savi.app;

import com.codename1.system.Lifecycle;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Label;
import com.savi.app.frontend.*;

public class SAVI extends Lifecycle {
    public static Font regularFont;
    public static Font boldFont;
    public static Font semiBoldFont;

    public static Font bold(float mm){
        if(boldFont==null){
            boldFont = Font.createTrueTypeFont("GoogleSans-Bold", "GoogleSans-Bold.ttf");
        }
        return boldFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_BOLD);
    }

    public static Font regular(float mm){
        if(regularFont==null){
            regularFont = Font.createTrueTypeFont("GoogleSans-Regular", "GoogleSans-Regular.ttf");
        }
        return regularFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_PLAIN);
    }

    public static Font semiBold(float mm){
        if(semiBoldFont==null){
            semiBoldFont = Font.createTrueTypeFont("GoogleSans-SemiBold", "GoogleSans-SemiBold.ttf");
        }
        return semiBoldFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_BOLD);
    }

    public static Label text(String str, Font font, int color) {
        Label lbl = new Label(str);
        lbl.getAllStyles().setFont(font);
        lbl.getAllStyles().setFgColor(color);
        return lbl;
    }
    @Override
    public void runApp() {
        new HomePage().show();
        new CalendarPage().show();
        new SignUpPage().show();
        new PendingPage().show();
        new LogInPage().show();
        new MainPage().show();
    }
}