package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.io.Log;
import java.io.IOException;
import com.savi.app.SAVI;

public class MainPage {

     public void show(){

          Form mainForm = new Form(new BorderLayout());;
          mainForm.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
          mainForm.getAllStyles().setBgTransparency(255);
          mainForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
          mainForm.getContentPane().getAllStyles().setBgTransparency(255);

          Container content = new Container(BoxLayout.yCenter());
          content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
          content.getAllStyles().setBgTransparency(255);
          /*
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
          */
          Image img = null;
          try {
               Image logoimg = Image.createImage("/Logo_main.png");
               int deviceWidth = Display.getInstance().getDisplayWidth();
               int targetWidth = (int)(deviceWidth * 0.8f);
               img = logoimg.scaledWidth(targetWidth);
          } catch (IOException err) {
               Log.e(err);
          }

          Label logo = new Label(img);
          Label title = SAVI.text("Savi", SAVI.bold(6f), SAVI.BLACK);
          Label subtitle = SAVI.text("MONEY. TRACKER. ALL IN ONE PLACE.", SAVI.regular(2.2f), SAVI.SLATE_GRAY);

          logo.setAlignment(Component.CENTER);
          title.setAlignment(Component.CENTER);
          subtitle.setAlignment(Component.CENTER);

          /*
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
          */
          content.add(logo);
          content.add(title);
          content.add(subtitle);

          Button btn = new Button("Get Started");

          Image btnimg = null;
          try {
               Image btni = Image.createImage("/arrow_NE.png");
               int deviceWidth = Display.getInstance().getDisplayWidth();
               int targetWidth = (int)(deviceWidth * 1f);
               btnimg = btni.scaledWidth(targetWidth);

               btn.setIcon(btni);
               //btnimg.setTextPosition(Component.LEFT);

               int spaceGap = Display.getInstance().convertToPixels(1f);
               btn.setGap(spaceGap);
          } catch (IOException err) {
               Log.e(err);
          }
          //base
          btn.getAllStyles().setFgColor(SAVI.WHITE);
          btn.getAllStyles().setBgTransparency(255);
          btn.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
          btn.getAllStyles().setPadding(3, 3, 5, 5);
          btn.getAllStyles().setFont(SAVI.regular(2.5f));

          btn.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
          btn.getAllStyles().setMargin(0, 5, 5, 5);

          //normal state gradient
          Style normal = btn.getUnselectedStyle();
          normal.setBgImage(null);
          normal.setBorder(Border.createEmpty());
          normal.setBgTransparency(255);
          normal.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
          normal.setBackgroundGradientStartColor(SAVI.DARK_DARK_PURPLE);
          normal.setBackgroundGradientEndColor(SAVI.DARK_PURPLE);
          normal.setFgColor(SAVI.WHITE);

          //hover/selected state gradient
          Style hover = btn.getSelectedStyle();
          hover.setBgImage(null);
          hover.setBorder(Border.createEmpty());
          hover.setFgColor(SAVI.WHITE);
          hover.setBgTransparency(255);
          hover.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
          hover.setBackgroundGradientStartColor(SAVI.DARK_PURPLE);
          hover.setBackgroundGradientEndColor(SAVI.BLUISH_PURPLE);

          // pressed state solid
          Style pressed = btn.getPressedStyle();
          pressed.setBgImage(null);
          pressed.setBorder(Border.createEmpty());
          pressed.setBgTransparency(255);
          pressed.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
          pressed.setBackgroundGradientStartColor(SAVI.PALE_PURPLE);
          pressed.setBackgroundGradientEndColor(SAVI.PASTEL_LAVENDER);
          pressed.setFgColor(SAVI.DARK_PURPLE);

          btn.getAllStyles().setBorder(Border.createRoundBorder(140, SAVI.BORDER_COLOR));

          mainForm.add(BorderLayout.CENTER, content);
          mainForm.add(BorderLayout.SOUTH, btn);

          btn.addActionListener(e -> { LogInPage login = new LogInPage(); login.show();});

          mainForm.show();
     }
}
