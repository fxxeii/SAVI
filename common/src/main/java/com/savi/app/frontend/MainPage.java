package com.savi.app.frontend;

import com.codename1.ui.*; // for form, button, dialog
import com.codename1.ui.layouts.*; //boxlayout, border layout, gridlayout
import com.codename1.ui.plaf.*;
import com.codename1.io.Log;
import com.codename1.ui.Form;
import java.io.IOException;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;


public class MainPage {
   public void show(){

        int darkDarkPurple = 0x30205E;
        int darkPurple = 0x7C3AED;
        int palePurple = 0xF4EFFE;
        int pastelLavander = 0xD8B4FE;
        int paleLavander = 0xE9D5FF;
        int gray = 0x8491A7;
        int borderColor = 0xEDF0F5;
        int white = 0xFFFFFF;
        int black = 0x000000;
        int slategray = 0x64748B;

        Form mainForm = new Form(new BorderLayout());;
        mainForm.getAllStyles().setBgColor(palePurple);
        mainForm.getAllStyles().setBgTransparency(255);
        mainForm.getContentPane().getAllStyles().setBgColor(palePurple);
        mainForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.yCenter());
        content.getAllStyles().setBgColor(palePurple);
        content.getAllStyles().setBgTransparency(255);

        Container overlap = new Container(new LayeredLayout());
        int outer = Display.getInstance().convertToPixels(45f);
        int inner = Display.getInstance().convertToPixels(35f);

        Container outerc = new Container();
        outerc.getAllStyles().setBgTransparency(0);
        outerc.getAllStyles().setBorder(Border.createRoundBorder(outer, outer, paleLavander));
        outerc.setPreferredW(outer);
        outerc.setPreferredH(outer);

        Container innerc = new Container();
        innerc.getAllStyles().setBgTransparency(0);
        innerc.getAllStyles().setBorder(Border.createRoundBorder(inner, inner, pastelLavander));
        innerc.setPreferredW(inner);
        innerc.setPreferredH(inner);
        innerc.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        innerc.getAllStyles().setPadding(5, 0, 0, 0);

        Image img = null;
        try {
             Image logoimg = Image.createImage("/Logo.png");
             int deviceWidth = Display.getInstance().getDisplayWidth();
             int targetWidth = (int)(deviceWidth * 0.6f);//0.85
             img = logoimg.scaledWidth(targetWidth);
        } catch (IOException err) {
             Log.e(err);
        }

        Label logo = new Label(img);
        Label title = SAVI.text("Savi", SAVI.bold(6f), black);
        Label subtitle = SAVI.text("MONEY. TRACKER. ALL IN ONE PLACE.", SAVI.regular(2.2f), slategray);

        logo.setAlignment(Component.CENTER);
        title.setAlignment(Component.CENTER);
        subtitle.setAlignment(Component.CENTER);

        overlap.setPreferredW(outer);
        overlap.setPreferredH(outer);
        overlap.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        overlap.getAllStyles().setPadding(2, 0, 0, 0);

        overlap.add(outerc);

        Container innerLayer = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        innerLayer.add(innerc);
        overlap.add(innerLayer);

        Container logoLayer = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        logoLayer.add(logo);
        overlap.add(logoLayer);

        Container center = new Container(new FlowLayout(Component.CENTER));
        center.add(overlap);

        content.add(center);
        content.add(title);
        content.add(subtitle);

        Button btn = new Button("Get Started");
        //base
        btn.getAllStyles().setFgColor(white);
        btn.getAllStyles().setBgColor(darkPurple);
        btn.getAllStyles().setBgTransparency(255);
        btn.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        btn.getAllStyles().setPadding(3, 3, 5, 5);
        btn.getAllStyles().setFont(SAVI.regular(2.5f));

        //normal state gradient
        Style normal = btn.getUnselectedStyle();
        normal.setBgImage(null);
        normal.setBorder(Border.createEmpty());
        normal.setBgTransparency(255);
        normal.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL );
        normal.setBackgroundGradientStartColor(darkPurple);
        normal.setBackgroundGradientEndColor(pastelLavander);
        normal.setFgColor(white);

        //hover/selected state gradient
        Style hover = btn.getSelectedStyle();
        hover.setBgImage(null);
        hover.setBorder(Border.createEmpty());
        hover.setFgColor(white);
        hover.setBgTransparency(255);
        hover.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        hover.setBackgroundGradientStartColor(pastelLavander);
        hover.setBackgroundGradientEndColor(darkPurple);

        // pressed state solid
        Style pressed = btn.getPressedStyle();
        pressed.setBgImage(null);
        pressed.setBorder(Border.createEmpty());
        pressed.setBgTransparency(255);
        pressed.setBgColor(darkPurple);
        pressed.setFgColor(white);
/*
        RoundRectBorder roundedBorder = RoundRectBorder.create()
                .cornerRadius(4.5f)
                .shadowOpacity(50)
                .shadowBlur(10)
                .shadowSpread(3)
                .shadowX(1)
                .shadowY(1)
                .shadowColor(darkDarkPurple);

        btn.getUnselectedStyle().setBorder(roundedBorder);
        btn.getSelectedStyle().setBorder(roundedBorder);
        btn.getPressedStyle().setBorder(roundedBorder);
*/
        mainForm.add(BorderLayout.CENTER, content);
        mainForm.add(BorderLayout.SOUTH, btn);

        mainForm.show();
    }
}
