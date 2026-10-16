package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.savi.app.SAVI;

public class PendingPage {

    public void show() {

        int darkDarkPurple = 0x30205E;
        int darkPurple = 0x7C3AED;
        int palePurple = 0xF4EFFE;
        int navy = 0x29364D;
        int pastelYellow = 0xF5E2A8;
        int borderColor = 0xEDF0F5;
        int white = 0xFFFFFF;
        int paleYellow = 0xFFFCF2;

        Form pendingForm = new Form(new BorderLayout());
        pendingForm.getContentPane().setScrollVisible(true);
        pendingForm.getContentPane().getAllStyles().setBgColor(palePurple);
        pendingForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(palePurple);
        content.getAllStyles().setBgTransparency(255);

        content.getAllStyles().setPadding(3, 3, 3, 3);
        content.getAllStyles().setMargin(0, 0, 0, 0);

        pendingForm.getToolbar().addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK, e -> {
                    // Dito ilalagay ang back navigation later.
        });

        Label title = new Label("Pending");

        Container titleBar = new Container(new BorderLayout());
        titleBar.add(BorderLayout.CENTER, title);

        titleBar.getAllStyles().setBgColor(darkPurple);
        titleBar.getAllStyles().setBgTransparency(255);
        titleBar.getAllStyles().setPadding(1, 1, 1, 1);
        titleBar.getAllStyles().setMargin(0, 0, 10, 10);
        titleBar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        title.getAllStyles().setFgColor(white);
        title.getAllStyles().setFont(SAVI.semiBold(3));
        title.getAllStyles().setAlignment(Component.CENTER);

        content.add(titleBar);

        // SPACE BETWEEN TITLE BAR AND PENDING CARD
        Container space1 = new Container();
        space1.setPreferredH(24);
        content.add(space1);

        // PENDING SUMMARY CARD
        Container pendingCard = new Container(BoxLayout.y());

        // GROUP NAME
        Label groupName = new Label("December Vacation");
        Label groupBullet = new Label("●");
        groupBullet.getAllStyles().setFgColor(darkPurple);
        groupBullet.getAllStyles().setFont(SAVI.regular(3));

        // PENDING AMOUNT
        Label pendingAmount = new Label("₱7,000.00");

        // STATUS
        Label status = new Label("Due Soon");

        // AMOUNT AND STATUS SECTION
        Container amountSection = new Container(new BorderLayout());
        // Add the amount on the left
        amountSection.add(BorderLayout.WEST, pendingAmount);
        // Add the status on the right
        amountSection.add(BorderLayout.EAST, status);

        amountSection.getAllStyles().setBgColor(paleYellow);
        amountSection.getAllStyles().setBgTransparency(255);
        amountSection.getAllStyles().setPadding(2, 2, 2, 2);
        amountSection.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2).strokeColor(pastelYellow));

        Container groupHeader = new Container(new BorderLayout());
        groupHeader.add(BorderLayout.WEST, groupBullet);
        groupHeader.add(BorderLayout.CENTER, groupName);

        groupHeader.getAllStyles().setPadding(0, 1, 0, 0);
        groupName.getAllStyles().setMargin(0, 0, 2, 0);

        pendingCard.add(groupHeader);
        pendingCard.add(amountSection);

        // PENDING CARD STYLE
        pendingCard.getAllStyles().setBgColor(white);
        pendingCard.getAllStyles().setBgTransparency(255);
        pendingCard.getAllStyles().setPadding(3, 3, 3, 3);
        pendingCard.getAllStyles().setMargin(0, 0, 10, 10);
        pendingCard.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(3).strokeColor(borderColor));

        // GROUP NAME STYLE
        groupName.getAllStyles().setFgColor(navy);
        groupName.getAllStyles().setFont(SAVI.semiBold(3));

        // PENDING AMOUNT STYLE
        pendingAmount.getAllStyles().setFgColor(darkDarkPurple);
        pendingAmount.getAllStyles().setFont(SAVI.bold(3));

        // STATUS STYLE
        status.getAllStyles().setFgColor(darkPurple);
        status.getAllStyles().setBgColor(palePurple);
        status.getAllStyles().setBgTransparency(255);
        status.getAllStyles().setPadding(1, 1, 1, 1);
        status.getAllStyles().setMargin(5, 5, 5, 5);
        status.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));
        status.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));

        content.add(pendingCard);

        pendingForm.add(BorderLayout.CENTER, content);

        pendingForm.show();
    }
}

