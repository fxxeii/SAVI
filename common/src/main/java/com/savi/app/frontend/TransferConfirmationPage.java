package com.savi.app.frontend;

import com.codename1.components.SpanLabel;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;

public class TransferConfirmationPage {

    int darkPurple = 0x7C3AED;
    int pageBg = 0xF8FAFC;
    int white = 0xFFFFFF;

    void styles(Container cont, int bgColor, float padLR, float padTB, float marLR, float marT, float marB) {
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
                shadowColor(SAVI.SLATE_GRAY));
        contS.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        contS.setPadding(padTB, padTB, padLR, padLR);
        contS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        contS.setMargin(marT, marB, marLR, marLR);
    }

    // 1234.5 -> "1,234.50"  (Codename One has no String.format)
    private String money(float amnt) {
        long cents = Math.round(amnt * 100.0);
        long whole = cents / 100;
        long dec = cents % 100;

        String w = String.valueOf(whole);
        String withCommas = "";
        for (int i = 0; i < w.length(); i++) {
            if (i > 0 && (w.length() - i) % 3 == 0) {
                withCommas += ",";
            }
            withCommas += w.charAt(i);
        }
        return withCommas + "." + (dec < 10 ? "0" : "") + dec;
    }

    // SpanLabel wraps long text onto several lines, a plain Label would cut it off
    public SpanLabel transferInfo(float amnt, String sender, String groupname1,
                                  String recipient, String groupname2) {
        String info = "P" + money(amnt) + " successfully transferred from " + sender
                + " of " + groupname1 + " to " + recipient + " of " + groupname2;

        SpanLabel transferInfo = new SpanLabel(info);
        transferInfo.setTextUIID("Label");
        Style ts = transferInfo.getTextAllStyles();
        ts.setFont(SAVI.regular(2f));
        ts.setFgColor(SAVI.GRANITE_GRAY);
        ts.setAlignment(Component.CENTER);
        ts.setMargin(0, 0, 0, 0);
        ts.setPadding(0, 0, 0, 0);

        transferInfo.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        transferInfo.getAllStyles().setMargin(1, 0, 4, 4);
        return transferInfo;
    }

    private Container header() {
        Label title = SAVI.text("Transfer Funds", SAVI.bold(2.5f), white);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container pill = new Container(new BorderLayout());
        styles(pill, darkPurple, 1.5f, 1.5f, 2f, 1f, 1f);   // same box as the THIS WEEK pill
        pill.add(BorderLayout.CENTER, title);
        return pill;
    }

    private Label icon() {
        Label pic = new Label();
        pic.getAllStyles().setAlignment(Component.CENTER);
        try {
            Image img = Image.createImage("/Transfer_successful icon.png");
            pic.setIcon(img.scaledWidth(Display.getInstance().convertToPixels(45f)));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return pic;
    }

//    // shown with sample data, so you can preview the page without the backend
//    public void show() {
//        show(0f, "*name*", "*group name*", "*name*", "*group name*");
//    }

    public void show(float amnt, String sender, String groupname1,
                     String recipient, String groupname2) {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(pageBg);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Label transferSuccessful = SAVI.text("Fund transfer successful", SAVI.bold(3f), SAVI.BLACK);
        transferSuccessful.getAllStyles().setAlignment(Component.CENTER);
        transferSuccessful.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        transferSuccessful.getAllStyles().setMargin(3, 0, 0, 0);

        Container content = new Container(BoxLayout.y());
        content.add(icon());
        content.add(transferSuccessful);
        content.add(transferInfo(amnt, sender, groupname1, recipient, groupname2));

        Button backHome = new Button("Back to Home Page");
        Style bs = backHome.getAllStyles();
        bs.setFgColor(white);
        bs.setFont(SAVI.semiBold(2f));
        bs.setAlignment(Component.CENTER);
        bs.setBgTransparency(255);
        bs.setBgColor(darkPurple);
        bs.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(darkPurple));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(1.5f, 1.5f, 1, 1);
        bs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        bs.setMargin(1, 3, 3, 3);
        backHome.addActionListener(e -> new HomePage().show());

        base.add(BorderLayout.NORTH, header());
        base.add(BorderLayout.CENTER, BorderLayout.centerAbsolute(content));
        base.add(BorderLayout.SOUTH, backHome);
        base.show();
    }
}