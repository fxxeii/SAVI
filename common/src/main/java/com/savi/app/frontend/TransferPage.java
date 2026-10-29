package com.savi.app.frontend;

import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.spinner.Picker;
import com.savi.app.SAVI;
import com.codename1.ui.Component;

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

    void styles(Container cont, int bgColor, float padLR, float padTB, float marLR, float marT, float marB){
        Style contS = cont.getAllStyles();
        contS.setBgTransparency(255);
        contS.setBgColor(bgColor);
        contS.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(slategray));
        contS.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        contS.setPadding(padTB, padTB, padLR, padLR);
        contS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        contS.setMargin(marT, marB, marLR,marLR);
    }

    private Container dropdown(String placeholder, String... options) {
        // 1. the picker itself (placeholder is the first entry)
        String[] items = new String[options.length + 1];
        items[0] = placeholder;
        System.arraycopy(options, 0, items, 1, options.length);

        Picker picker = new Picker();
        picker.setType(Display.PICKER_TYPE_STRINGS);
        picker.setStrings(items);
        picker.setSelectedStringIndex(0);

        Style ps = picker.getAllStyles();
        ps.setBgTransparency(0);
        ps.setBorder(Border.createEmpty());      // remove the default picker border
        ps.setAlignment(Component.LEFT);
        ps.setFgColor(0x1f2937);

        // 2. chevron on the right
        Label chevron = new Label();
        chevron.getAllStyles().setFgColor(0x6b7280);
        FontImage.setMaterialIcon(chevron, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 4);

        Container bar = new Container(new BorderLayout());
        bar.add(BorderLayout.CENTER, picker);
        bar.add(BorderLayout.EAST, chevron);
        bar.setLeadComponent(picker);

        styles(bar, white, 0f, 0f, 1, 0.5f, 0.5f);
        Style bs = bar.getAllStyles();
        bs.setBorder(RoundRectBorder.create().stroke(1, false).strokeColor(gray));

        picker.addActionListener(e -> {
            String chosen = picker.getSelectedString();
        });

        return bar;
    }

    public void show(){
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(palePurple);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        com.codename1.ui.Button back = new com.codename1.ui.Button();
        Style s = back.getAllStyles();
        s.setFgColor(black);
        s.setBgTransparency(0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1, 1, 1, 1);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 5);

        back.addActionListener(e -> {new HomePage().show();});

        // put it at the top-left
        Container top = BorderLayout.west(back);
        Style ts = top.getAllStyles();
        ts.setMarginUnit(Style.UNIT_TYPE_DIPS);   // or UNIT_TYPE_PIXELS / UNIT_TYPE_SCREEN_PERCENTAGE
        ts.setMargin(2, 1, 2, 0);                 // top, bottom, left, right (in dips)
        //base.add(BorderLayout.NORTH, top);

        Container titleContainer = new Container(new BorderLayout());
        styles(titleContainer, darkPurple, 1f, 0f, 2f, 2, 1);

        Label TransferFunds = SAVI.text("Transfer Funds", SAVI.semiBold(2.5f), white);
        titleContainer.add(BorderLayout.WEST, TransferFunds);

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.WEST, top);
        header.add(BorderLayout.CENTER, titleContainer);

        Container transFromCont = new Container(new BorderLayout());
        styles(transFromCont, white, 1f, 1f, 2f, 1, 1);

        Label transFrom = SAVI.text("Transfer from", SAVI.semiBold(3f), black);
        transFromCont.add(BorderLayout.NORTH, transFrom);

        Container fromCard = new Container(BoxLayout.y());
        fromCard.add(dropdown("Select group", "December Vacation", "Emergency Funds", "Personal Savings"));
        fromCard.add(dropdown("Select member", "Faye", "Sam", "Air", "Aron", "Andrei"));
        transFromCont.add(BorderLayout.CENTER, fromCard);

        com.codename1.ui.Container mainContents = new Container(BoxLayout.y());
        mainContents.isScrollableY();
        base.add(BorderLayout.CENTER, mainContents);
        base.add(BorderLayout.NORTH, header);
        mainContents.add(transFromCont);
        base.show();
    }
}
