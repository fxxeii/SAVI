package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;

public class TransferConfirmationPage {

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

    public void transferInfo(float amnt, String sender, String groupname1, String recipient, String groupname2){
        String info = "P" + amnt + " successfully transferred from " + sender + "of" + groupname1 + "to" + recipient + "of" + groupname2;
        Label transferInfo = SAVI.text(info, SAVI.regular(1.5f), SAVI.GRANITE_GRAY);
    }

    public void show(){
        Form base = new Form("", new BorderLayout());

        Container transferIcon = new Container();

        try {
            Image img = Image.createImage("/Transfer_successful icon.png");
            Label pic = new Label(img);
            transferIcon.add(pic);

            int w = Display.getInstance().convertToPixels(20f);
            Image small = img.scaledWidth(w);
            Image exact = img.scaled(w, w);
            Image filled = img.fill(w, w);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }

        Label transferSuccessful = SAVI.text("Fund Transfer Successful.", SAVI.bold(3f), SAVI.BLACK);
//        transferInfo();
    }
}
