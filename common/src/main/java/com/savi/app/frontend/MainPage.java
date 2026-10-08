package com.savi.app.frontend;

import com.codename1.ui.*; // for form, button, dialog
import com.codename1.ui.layouts.*; //boxlayout, border layout, gridlayout
import com.codename1.ui.events.*; //action listener
import com.codename1.ui.plaf.*;

public class MainPage {
   public void show(){

        int darkDarkPurple = 0x30205E;
        int darkPurple = 0x7C3AED;
        int palePurple = 0xF4EFFE;
        int navy = 0x29364D;
        int gray = 0x8491A7;
        int green = 0x00A878;
        int paleGreen = 0xE6FAF2;
        int borderColor = 0xEDF0F5;
        int white = 0xFFFFFF;

        Form mainForm = new Form(new BorderLayout());
        mainForm.getAllStyles().setBgColor(palePurple);
        mainForm.getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.yCenter());
        content.getAllStyles().setBgColor(palePurple);
        content.getAllStyles().setBgTransparency(255);

        Label logo = new Label("LOGO"); //ito muna
        Label title = new Label("Savi");
        title.setUIID("Title"); //style it later, ito muna
        Label subtitle = new Label("Money. Tracker. All In One Place.");

        logo.setAlignment(Component.CENTER);
        logo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));

        title.setAlignment(Component.CENTER);
        title.getAllStyles().setFgColor(darkPurple);
        title.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));

        subtitle.setAlignment(Component.CENTER);
        subtitle.getAllStyles().setFgColor(darkPurple);
        subtitle.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));

        content.add(logo);
        content.add(title);
        content.add(subtitle);

        mainForm.add(BorderLayout.CENTER, content);

        Button btn = new Button("Get Started");

        btn.getAllStyles().setBgColor(darkPurple);
        btn.getAllStyles().setFgColor(white);
        btn.getAllStyles().setBgTransparency(255);
        btn.getAllStyles().setBorder(null);
        btn.getAllStyles().setPadding(2, 2,1,1);
        btn.getAllStyles().setBorder(Border.createRoundBorder(150, darkPurple));
        btn.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));

        mainForm.add(BorderLayout.SOUTH, btn);

        mainForm.show();
    }
}
