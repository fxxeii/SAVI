package com.savi.app.frontend;

import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.Style;

import java.awt.*;

public class TransferPage {

    int darkDarkPurple = 0x30205E;
    int darkPurple = 0x7C3AED;
    int palePurple = 0xF4EFFE;
    int pastelLavander = 0xD8B4FE;
    int paleLavander = 0xE9D5FF;
    int bluishPurple = 0x4F46E5;
    int borderColor = 0xEDF0F5;
    int white = 0xFFFFFF;
    int black = 0x000000;
    int slategray = 0x64748B;
    int navy = 0x29364D;
    int gray = 0x8491A7;
    int green = 0x00A878;
    int paleGreen = 0xE6FAF2;

    public void show(){
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(palePurple);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        com.codename1.ui.Button back = new com.codename1.ui.Button();
        Style s = back.getAllStyles();
        s.setFgColor(0x2d3a4f);
        s.setBgTransparency(0);
        int pad = Display.getInstance().convertToPixels(3);
        s.setPadding(pad, pad, pad, pad);
        s.setBorder(RoundBorder.create()
                .color(white)
                .strokeColor(gray)
                .strokeOpacity(255)
                .stroke(1, false)
                .shadowOpacity(0));
        s.setBgTransparency(0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(2, 2, 2, 2);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 5);

        back.addActionListener(e -> {new HomePage().show();});

        // put it at the top-left
        Container top = BorderLayout.west(back);
        Style ts = top.getAllStyles();
        ts.setMarginUnit(Style.UNIT_TYPE_DIPS);   // or UNIT_TYPE_PIXELS / UNIT_TYPE_SCREEN_PERCENTAGE
        ts.setMargin(2, 0, 2, 0);                 // top, bottom, left, right (in dips)
        base.add(BorderLayout.NORTH, top);

        com.codename1.ui.Container mainContents = new Container(BoxLayout.y());
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}
