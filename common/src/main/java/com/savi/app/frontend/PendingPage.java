
package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.savi.app.SAVI;

public class PendingPage {

    public void show() {

        Form pendingForm = new Form(new BorderLayout());
        pendingForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        pendingForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPadding(3, 3, 3, 3);

        // BACK BUTTON
        com.codename1.ui.Button back = new com.codename1.ui.Button();
        Style s = back.getAllStyles();
        s.setFgColor(SAVI.BLACK);
        s.setBgTransparency(0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1, 1, 1, 1);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 5);

        back.addActionListener(e -> {new HomePage().show();});

        // HEADER
        Container BackAndLabel = new Container(new BorderLayout());

        Label title = new Label("Pending");
        title.getAllStyles().setFgColor(SAVI.WHITE);
        title.getAllStyles().setFont(SAVI.semiBold(3));
        title.getAllStyles().setAlignment(Component.CENTER);

        Container titleBar = new Container(new BorderLayout());
        titleBar.add(BorderLayout.CENTER, title);

        titleBar.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        titleBar.getAllStyles().setBgTransparency(255);
        titleBar.getAllStyles().setPadding(1, 1, 1, 1);
        titleBar.getAllStyles().setMargin(1, 1, 1, 1);
        titleBar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        BackAndLabel.add(BorderLayout.WEST, back);
        BackAndLabel.add(BorderLayout.CENTER, titleBar);

        content.add(BackAndLabel);

        // SPACE
        Container space1 = new Container();
        space1.setPreferredH(24);
        content.add(space1);

        // PENDING SUMMARY CARD
        Label groupBullet = new Label("●");
        groupBullet.getAllStyles().setFgColor(SAVI.DARK_PURPLE);
        groupBullet.getAllStyles().setFont(SAVI.regular(3));

        Label groupName = new Label("December Vacation");
        groupName.getAllStyles().setFgColor(SAVI.NAVY);
        groupName.getAllStyles().setFont(SAVI.semiBold(3));
        groupName.getAllStyles().setMargin(0, 0, 2, 0);

        Container groupHeader = new Container(new BorderLayout());
        groupHeader.add(BorderLayout.WEST, groupBullet);
        groupHeader.add(BorderLayout.CENTER, groupName);

        Label amountTitle = SAVI.text("TOTAL PENDING AMOUNT", SAVI.semiBold(2), SAVI.AMBER);
        Label pendingAmount = SAVI.text("₱140.00", SAVI.bold(3.5f), SAVI.AMBER);
        Container amountBox = BoxLayout.encloseY(amountTitle, pendingAmount);

        Label status = SAVI.text("Due Soon", SAVI.semiBold(2), SAVI.AMBER);

        status.getAllStyles().setBgColor(SAVI.YELLOW_TAG);
        status.getAllStyles().setBgTransparency(255);
        status.getAllStyles().setPadding(1, 1, 2, 2);
        status.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        Container tagBox = new Container(new BorderLayout());
        tagBox.add(BorderLayout.NORTH, status);

        Container amountSection = new Container(new BorderLayout());
        amountSection.add(BorderLayout.WEST, amountBox);
        amountSection.add(BorderLayout.EAST, tagBox);

        amountSection.getAllStyles().setBgColor(SAVI.PALE_YELLOW);
        amountSection.getAllStyles().setBgTransparency(255);
        amountSection.getAllStyles().setPadding(2, 2, 2, 2);
        amountSection.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2).strokeColor(SAVI.PASTEL_YELLOW));

        Container pendingCard = new Container(BoxLayout.y());
        pendingCard.add(groupHeader);
        pendingCard.add(amountSection);

        pendingCard.getAllStyles().setBgColor(SAVI.WHITE);
        pendingCard.getAllStyles().setBgTransparency(255);
        pendingCard.getAllStyles().setPadding(3, 3, 3, 3);
        pendingCard.getAllStyles().setMargin(0, 3, 0, 0);
        pendingCard.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(3).strokeColor(SAVI.BORDER_COLOR));

        content.add(pendingCard);

        // MEMBER CARDS (2 PER ROW)
        Container memberGrid = new Container(new GridLayout(2, 2));
        memberGrid.add(createMemberCard("Air", "@rei", "₱35.00", SAVI.DARK_PURPLE, SAVI.NAVY, SAVI.BLUE_GRAY, SAVI.BORDER_COLOR, SAVI.WHITE, SAVI.RED, SAVI.PALE_RED));
        memberGrid.add(createMemberCard("Faye", "@faye", "₱35.00", SAVI.DARK_PURPLE, SAVI.NAVY, SAVI.BLUE_GRAY, SAVI.BORDER_COLOR, SAVI.WHITE, SAVI.RED, SAVI.PALE_RED));
        memberGrid.add(createMemberCard("Shy", "@zhv", "₱35.00", SAVI.DARK_PURPLE, SAVI.NAVY, SAVI.BLUE_GRAY, SAVI.BORDER_COLOR, SAVI.WHITE, SAVI.RED, SAVI.PALE_RED));
        memberGrid.add(createMemberCard("Sam", "@sam", "₱35.00", SAVI.DARK_PURPLE, SAVI.NAVY, SAVI.BLUE_GRAY, SAVI.BORDER_COLOR, SAVI.WHITE, SAVI.RED, SAVI.PALE_RED));
        content.add(memberGrid);

        // BOTTOM: REMIND PEOPLE + SET REMINDER
        Button remindButton = new Button("Remind People");
        remindButton.getAllStyles().setFgColor(SAVI.WHITE);
        remindButton.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        remindButton.getAllStyles().setBgTransparency(255);
        remindButton.getAllStyles().setFont(SAVI.semiBold(3));
        remindButton.getAllStyles().setAlignment(Component.CENTER);
        remindButton.getAllStyles().setPadding(2, 2, 3, 3);
        remindButton.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(6));

        Button setReminder = new Button("Set reminder");
        setReminder.getAllStyles().setFgColor(SAVI.DARK_PURPLE);
        setReminder.getAllStyles().setBgTransparency(0);
        setReminder.getAllStyles().setBorder(Border.createEmpty());
        setReminder.getAllStyles().setFont(SAVI.regular(2));
        setReminder.getAllStyles().setAlignment(Component.CENTER);

        setReminder.addActionListener(e -> {
            // new ReminderPage().show();
        });

        Container bottomBar = new Container(BoxLayout.y());
        bottomBar.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        bottomBar.getAllStyles().setBgTransparency(255);
        bottomBar.getAllStyles().setPadding(2, 2, 3, 3);
        bottomBar.add(remindButton);
        bottomBar.add(setReminder);

        // FORM LAYOUT
        pendingForm.add(BorderLayout.CENTER, content);
        pendingForm.add(BorderLayout.SOUTH, bottomBar);
        pendingForm.show();
    }

    // MEMBER CARD METHOD
    private Container createMemberCard(String name, String username, String missed, int darkPurple, int navy, int blueGray, int borderColor, int white, int red, int paleRed) {
        Label avatar = new Label();

        avatar.getAllStyles().setFgColor(white);
        avatar.getAllStyles().setBgColor(darkPurple);
        avatar.getAllStyles().setBgTransparency(255);
        avatar.getAllStyles().setPadding(2, 2, 2, 2);
        avatar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2));
        avatar.setMaterialIcon(FontImage.MATERIAL_PERSON, 4);

        Label nameLabel = SAVI.text(name, SAVI.semiBold(3), navy);
        Label userLabel = SAVI.text(username, SAVI.regular(2), blueGray);

        Container nameBox = BoxLayout.encloseY(nameLabel, userLabel);

        Container top = new Container(new BorderLayout());
        top.add(BorderLayout.WEST, avatar);
        top.add(BorderLayout.CENTER, nameBox);

        // THIN DIVIDER
        Container line = new Container();
        line.setPreferredH(2);
        line.getAllStyles().setBgColor(borderColor);
        line.getAllStyles().setBgTransparency(255);
        line.getAllStyles().setMargin(2, 2, 0, 0);

        // MISSED CONTRIBUTION BOX
        Label missedTitle = SAVI.text(
                "Missed Contribution", SAVI.regular(2), red
        );

        Label missedAmount = SAVI.text(
                missed, SAVI.bold(3), red
        );

        Container missedBox = new Container(BoxLayout.y());
        missedBox.add(missedTitle);
        missedBox.add(missedAmount);

        missedBox.getAllStyles().setBgColor(paleRed);
        missedBox.getAllStyles().setBgTransparency(255);
        missedBox.getAllStyles().setPadding(2, 2, 2, 2);
        missedBox.getAllStyles().setBorder(
                RoundRectBorder.create().cornerRadius(2)
        );

        // MEMBER CARD
        Container card = new Container(BoxLayout.y());
        card.add(top);
        card.add(line);
        card.add(missedBox);

        card.getAllStyles().setBgColor(white);
        card.getAllStyles().setBgTransparency(255);
        card.getAllStyles().setPadding(2, 2, 2, 2);
        card.getAllStyles().setMargin(1, 1, 1, 1);
        card.getAllStyles().setBorder(
                RoundRectBorder.create().cornerRadius(3)
                        .strokeColor(borderColor)
        );

        return card;
    }
}
