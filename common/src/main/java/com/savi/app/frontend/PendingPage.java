package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.savi.app.SAVI;

public class PendingPage {

    public void show() {

        int darkPurple = 0x7C3AED;
        int palePurple = 0xF4EFFE;
        int navy = 0x29364D;
        int gray = 0x8491A7;
        int borderColor = 0xEDF0F5;
        int white = 0xFFFFFF;
        int pastelYellow = 0xF5E2A8;
        int paleYellow = 0xFFFCF2;
        int yellowTag = 0xFEF3C7;
        int amber = 0xB45309;
        int red = 0xE11D48;
        int paleRed = 0xFFF1F2;

        Form pendingForm = new Form(new BorderLayout());
        pendingForm.getContentPane().getAllStyles().setBgColor(palePurple);
        pendingForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(palePurple);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPadding(3, 3, 3, 3);

        // for back button
        pendingForm.getToolbar().addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK, e -> {
            // dito ilalagay yung code para bumalik sa previous screen
            // should look like: previousForm.showBack();
        });

        // HEADER
        Label title = new Label("Pending");
        title.getAllStyles().setFgColor(white);
        title.getAllStyles().setFont(SAVI.semiBold(3));
        title.getAllStyles().setAlignment(Component.CENTER);

        Container titleBar = new Container(new BorderLayout());
        titleBar.add(BorderLayout.CENTER, title);

        // title bar style
        titleBar.getAllStyles().setBgColor(darkPurple);
        titleBar.getAllStyles().setBgTransparency(255);
        titleBar.getAllStyles().setPadding(1, 1, 1, 1);
        titleBar.getAllStyles().setMargin(1, 1, 1, 1);
        titleBar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        content.add(titleBar);

        // SPACE
        Container space1 = new Container();
        space1.setPreferredH(24);
        content.add(space1);

        // PENDING SUMMARY CARD
        Label groupBullet = new Label("●");
        groupBullet.getAllStyles().setFgColor(darkPurple);
        groupBullet.getAllStyles().setFont(SAVI.regular(3));

        Label groupName = new Label("December Vacation");
        groupName.getAllStyles().setFgColor(navy);
        groupName.getAllStyles().setFont(SAVI.semiBold(3));
        groupName.getAllStyles().setMargin(0, 0, 2, 0);

        Container groupHeader = new Container(new BorderLayout());
        groupHeader.add(BorderLayout.WEST, groupBullet);
        groupHeader.add(BorderLayout.CENTER, groupName);

        Label amountTitle = SAVI.text("TOTAL PENDING AMOUNT", SAVI.semiBold(2), amber);
        Label pendingAmount = SAVI.text("₱140.00", SAVI.bold(3.5f), amber);
        Container amountBox = BoxLayout.encloseY(amountTitle, pendingAmount);

        Label status = SAVI.text("Due Soon", SAVI.semiBold(2), amber);
        status.getAllStyles().setBgColor(yellowTag);
        status.getAllStyles().setBgTransparency(255);
        status.getAllStyles().setPadding(1, 1, 2, 2);
        status.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        Container tagBox = new Container(new BorderLayout());
        tagBox.add(BorderLayout.NORTH, status);

        Container amountSection = new Container(new BorderLayout());
        amountSection.add(BorderLayout.WEST, amountBox);
        amountSection.add(BorderLayout.EAST, tagBox);
        amountSection.getAllStyles().setBgColor(paleYellow);
        amountSection.getAllStyles().setBgTransparency(255);
        amountSection.getAllStyles().setPadding(2, 2, 2, 2);
        amountSection.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2).strokeColor(pastelYellow));

        Container pendingCard = new Container(BoxLayout.y());
        pendingCard.add(groupHeader);
        pendingCard.add(amountSection);
        pendingCard.getAllStyles().setBgColor(white);
        pendingCard.getAllStyles().setBgTransparency(255);
        pendingCard.getAllStyles().setPadding(3, 3, 3, 3);
        pendingCard.getAllStyles().setMargin(0, 3, 0, 0);
        pendingCard.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(3).strokeColor(borderColor));
        content.add(pendingCard);

        // MEMBER CARDS (2 per row)
        Container memberGrid = new Container(new GridLayout(2, 2));
        memberGrid.add(createMemberCard("Air", "@rei", "₱35.00", darkPurple, navy, gray, borderColor, white, red, paleRed));
        memberGrid.add(createMemberCard("Faye", "@faye", "₱35.00", darkPurple, navy, gray, borderColor, white, red, paleRed));
        memberGrid.add(createMemberCard("Shy", "@zhv", "₱35.00", darkPurple, navy, gray, borderColor, white, red, paleRed));
        memberGrid.add(createMemberCard("Sam", "@sam", "₱35.00", darkPurple, navy, gray, borderColor, white, red, paleRed));
        content.add(memberGrid);

        // BOTTOM: Remind People + Set reminder
        Button remindButton = new Button("Remind People");
        remindButton.getAllStyles().setFgColor(white);
        remindButton.getAllStyles().setBgColor(darkPurple);
        remindButton.getAllStyles().setBgTransparency(255);
        remindButton.getAllStyles().setFont(SAVI.semiBold(3));
        remindButton.getAllStyles().setAlignment(Component.CENTER);
        remindButton.getAllStyles().setPadding(2, 2, 3, 3);
        remindButton.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(6));

        Button setReminder = new Button("Set reminder");
        setReminder.getAllStyles().setFgColor(darkPurple);
        setReminder.getAllStyles().setBgTransparency(0);
        setReminder.getAllStyles().setBorder(Border.createEmpty());
        setReminder.getAllStyles().setFont(SAVI.regular(2));
        setReminder.getAllStyles().setAlignment(Component.CENTER);
        setReminder.addActionListener(e -> {
            // dito ilalagay yung para sa ReminderPage
        });

        Container bottomBar = new Container(BoxLayout.y());
        bottomBar.getAllStyles().setBgColor(palePurple);
        bottomBar.getAllStyles().setBgTransparency(255);
        bottomBar.getAllStyles().setPadding(2, 2, 3, 3);
        bottomBar.add(remindButton);
        bottomBar.add(setReminder);

        pendingForm.add(BorderLayout.CENTER, content);
        pendingForm.add(BorderLayout.SOUTH, bottomBar);
        pendingForm.show();
    }

    private Container createMemberCard(String name, String username, String missed,
                                       int purple, int navy, int gray, int borderColor,
                                       int white, int red, int paleRed) {

        // avatar (style first, then icon, so the icon takes the color)
        Label avatar = new Label();
        avatar.getAllStyles().setFgColor(white);
        avatar.getAllStyles().setBgColor(purple);
        avatar.getAllStyles().setBgTransparency(255);
        avatar.getAllStyles().setPadding(2, 2, 2, 2);
        avatar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2));
        avatar.setMaterialIcon(FontImage.MATERIAL_PERSON, 4);

        Label nameLabel = SAVI.text(name, SAVI.semiBold(3), navy);
        Label userLabel = SAVI.text(username, SAVI.regular(2), gray);
        Container nameBox = BoxLayout.encloseY(nameLabel, userLabel);

        Container top = new Container(new BorderLayout());
        top.add(BorderLayout.WEST, avatar);
        top.add(BorderLayout.CENTER, nameBox);

        // thin divider
        Container line = new Container();
        line.setPreferredH(2);
        line.getAllStyles().setBgColor(borderColor);
        line.getAllStyles().setBgTransparency(255);
        line.getAllStyles().setMargin(2, 2, 0, 0);

        // missed contribution box
        Label missedTitle = SAVI.text("Missed Contribution", SAVI.regular(2), red);
        Label missedAmount = SAVI.text(missed, SAVI.bold(3), red);
        Container missedBox = new Container(BoxLayout.y());
        missedBox.add(missedTitle);
        missedBox.add(missedAmount);
        missedBox.getAllStyles().setBgColor(paleRed);
        missedBox.getAllStyles().setBgTransparency(255);
        missedBox.getAllStyles().setPadding(2, 2, 2, 2);
        missedBox.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2));

        Container card = new Container(BoxLayout.y());
        card.add(top);
        card.add(line);
        card.add(missedBox);
        card.getAllStyles().setBgColor(white);
        card.getAllStyles().setBgTransparency(255);
        card.getAllStyles().setPadding(2, 2, 2, 2);
        card.getAllStyles().setMargin(1, 1, 1, 1);
        card.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(3).strokeColor(borderColor));
        return card;
    }
}