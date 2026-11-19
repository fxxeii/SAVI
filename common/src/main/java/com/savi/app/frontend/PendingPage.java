package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.savi.app.SAVI;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.Home;

import java.util.ArrayList;

public class PendingPage {

    public void show() {

        Form pendingForm = new Form("", new BorderLayout());
        pendingForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        pendingForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        content.getAllStyles().setPadding(0.5f, 2f, 0, 0);

        // Pull active group from SAVI.groupList.getGroups() (or fallback preview if groups haven't been loaded yet)
        GroupSavings activeGroup = SAVI.groupList.getGroups().isEmpty() ? null : SAVI.groupList.getGroups().get(0);

        String titleStr = activeGroup != null ? activeGroup.getGroupName() : "December Vacation";
        ArrayList<String> pendingNames = new ArrayList<>();
        ArrayList<Double> pendingAmounts = new ArrayList<>();
        double totalPending = 0;

        if (activeGroup != null) {
            for (String m : activeGroup.getMembers()) {
                double missed = activeGroup.getTotalMissed(m);
                if (missed > 0) {
                    pendingNames.add(m);
                    pendingAmounts.add(missed);
                    totalPending += missed;
                }
            }
        } else {
            // Sample preview if SAVI.groupList.getGroups() is empty
            String[] sampleNames = {"Air", "Faye", "Shy", "Sam"};
            for (String n : sampleNames) {
                pendingNames.add(n);
                pendingAmounts.add(35.0);
                totalPending += 35.0;
            }
        }

        // PENDING SUMMARY CARD
        Label groupBullet = SAVI.text("● ", SAVI.bold(2.2f), SAVI.DARK_PURPLE);
        Label groupName = SAVI.text(titleStr, SAVI.bold(2.5f), SAVI.DARK_KNIGHT);

        Container groupHeader = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        groupHeader.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        groupHeader.getAllStyles().setMargin(0, 1.2f, 0, 0);
        groupHeader.add(groupBullet).add(groupName);

        Label amountTitle = SAVI.text("TOTAL PENDING AMOUNT", SAVI.bold(1.7f), SAVI.AMBER);
        Label pendingAmount = SAVI.text(Home.pesoFormat(totalPending), SAVI.bold(3.2f), SAVI.AMBER);
        Container amountBox = BoxLayout.encloseY(amountTitle, pendingAmount);

        Label status = SAVI.text(totalPending > 0 ? "Due Soon" : "All Settled", SAVI.bold(1.7f), totalPending > 0 ? SAVI.AMBER : SAVI.GREEN);
        Style ss = status.getAllStyles();
        ss.setBgColor(totalPending > 0 ? SAVI.YELLOW_TAG : SAVI.PALE_GREEN);
        ss.setBgTransparency(255);
        ss.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ss.setPadding(0.6f, 0.6f, 1.5f, 1.5f);
        ss.setBorder(RoundRectBorder.create().cornerRadius(2f));

        Container tagBox = new Container(new BorderLayout());
        tagBox.add(BorderLayout.NORTH, status);

        Container amountSection = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        amountSection.add(BorderLayout.WEST, amountBox);
        amountSection.add(BorderLayout.EAST, tagBox);
        Style as = amountSection.getAllStyles();
        as.setBgColor(totalPending > 0 ? SAVI.PALE_YELLOW : SAVI.PALE_GREEN);
        as.setBgTransparency(255);
        as.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        as.setPadding(1.5f, 1.5f, 2f, 2f);
        as.setBorder(RoundRectBorder.create().cornerRadius(3f).strokeColor(totalPending > 0 ? SAVI.PASTEL_YELLOW : SAVI.PALE_GREEN).strokeOpacity(255).stroke(0.2f, true));

        Container pendingCard = new Container(BoxLayout.y());
        pendingCard.add(groupHeader);
        pendingCard.add(amountSection);
        HomePage.boxStyles(pendingCard, SAVI.WHITE, 2f, 2f);
        pendingCard.getAllStyles().setMarginBottom(1.2f);

        content.add(pendingCard);

        // MEMBER CARDS (2 PER ROW)
        if (pendingNames.isEmpty()) {
            Label allPaid = SAVI.text("Everyone is up to date on contributions!", SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
            allPaid.getAllStyles().setAlignment(Component.CENTER);
            allPaid.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
            allPaid.getAllStyles().setPadding(4f, 4f, 2f, 2f);
            content.add(allPaid);
        } else {
            int rows = (pendingNames.size() + 1) / 2;
            Container memberGrid = new Container(new GridLayout(rows, 2));
            memberGrid.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
            memberGrid.getAllStyles().setMargin(0, 1f, 1.2f, 1.2f);

            for (int i = 0; i < pendingNames.size(); i++) {
                String member = pendingNames.get(i);
                String handle = "@" + member.toLowerCase();
                String missedStr = Home.pesoFormat(pendingAmounts.get(i));
                memberGrid.add(createMemberCard(member, handle, missedStr));
            }
            content.add(memberGrid);
        }

        // BOTTOM: REMIND PEOPLE + SET REMINDER
        Button remindButton = new Button("Remind People");
        Style rs = remindButton.getAllStyles();
        rs.setFgColor(SAVI.WHITE);
        rs.setBgColor(SAVI.DARK_PURPLE);
        rs.setBgTransparency(255);
        rs.setFont(SAVI.semiBold(2.2f));
        rs.setAlignment(Component.CENTER);
        rs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        rs.setPadding(1.6f, 1.6f, 2f, 2f);
        rs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        rs.setMargin(0, 0.8f, 2f, 2f);
        rs.setBorder(RoundRectBorder.create().cornerRadius(4f).shadowOpacity(50).shadowBlur(10).shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(SAVI.BRIGHT_VIOLET));

        remindButton.addActionListener(e -> {
            if (pendingNames.isEmpty()) {
                Dialog.show("All Clear", "No pending members to remind.", "OK", null);
            } else {
                Dialog.show("Reminder Sent",
                        "Sent payment reminder to " + pendingNames.size() + " member(s) in " + titleStr + ".",
                        "OK", null);
            }
        });

        Button setReminder = new Button("Set reminder");
        Style srs = setReminder.getAllStyles();
        srs.setFgColor(SAVI.DARK_PURPLE);
        srs.setBgTransparency(0);
        srs.setBorder(Border.createEmpty());
        srs.setFont(SAVI.semiBold(1.9f));
        srs.setAlignment(Component.CENTER);
        srs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        srs.setPadding(0.5f, 0.5f, 1f, 1f);

        setReminder.addActionListener(e -> new ReminderPage().show());

        Container bottomBar = new Container(BoxLayout.y());
        bottomBar.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        bottomBar.getAllStyles().setBgTransparency(255);
        bottomBar.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bottomBar.getAllStyles().setPadding(1f, 1.5f, 1f, 1f);
        bottomBar.add(remindButton);
        bottomBar.add(setReminder);

        // FORM LAYOUT
        pendingForm.add(BorderLayout.NORTH, SAVI.buildHeader("Pending", () -> new HomePage().show()));
        pendingForm.add(BorderLayout.CENTER, content);
        pendingForm.add(BorderLayout.SOUTH, bottomBar);
        pendingForm.show();
    }

    // MEMBER CARD METHOD
    private Container createMemberCard(String name, String username, String missed) {
        Label avatar = new Label();
        Style avs = avatar.getAllStyles();
        avs.setFgColor(SAVI.WHITE);
        avs.setBgColor(SAVI.DARK_PURPLE);
        avs.setBgTransparency(255);
        avs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        avs.setPadding(1.5f, 1.5f, 1.5f, 1.5f);
        avs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        avs.setMargin(0, 0, 0, 1.2f);
        avs.setBorder(RoundRectBorder.create().cornerRadius(2.5f));
        avatar.setMaterialIcon(FontImage.MATERIAL_PERSON, 3.5f);

        Label nameLabel = SAVI.text(name, SAVI.bold(2.2f), SAVI.DARK_KNIGHT);
        Label userLabel = SAVI.text(username, SAVI.regular(1.7f), SAVI.SLATE_GRAY);
        nameLabel.getAllStyles().setMargin(0, 0, 0, 0);
        userLabel.getAllStyles().setMargin(0, 0, 0, 0);

        Container nameBox = BoxLayout.encloseY(nameLabel, userLabel);

        Container top = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        top.add(BorderLayout.WEST, avatar);
        top.add(BorderLayout.CENTER, nameBox);

        // Thin divider
        Container line = new Container();
        line.setPreferredH(Display.getInstance().convertToPixels(0.2f));
        Style ls = line.getAllStyles();
        ls.setBgColor(SAVI.BORDER_COLOR);
        ls.setBgTransparency(255);
        ls.setMarginUnit(Style.UNIT_TYPE_DIPS);
        ls.setMargin(1.2f, 1.2f, 0, 0);

        // Missed contribution pill box
        Label missedTitle = SAVI.text("Missed Contribution", SAVI.regular(1.6f), SAVI.RED);
        Label missedAmount = SAVI.text(missed, SAVI.bold(2.2f), SAVI.RED);
        missedTitle.getAllStyles().setMargin(0, 0, 0, 0);
        missedAmount.getAllStyles().setMargin(0, 0, 0, 0);

        Container missedBox = new Container(BoxLayout.y());
        missedBox.add(missedTitle);
        missedBox.add(missedAmount);
        Style ms = missedBox.getAllStyles();
        ms.setBgColor(SAVI.PALE_RED);
        ms.setBgTransparency(255);
        ms.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ms.setPadding(1.2f, 1.2f, 1.5f, 1.5f);
        ms.setBorder(RoundRectBorder.create().cornerRadius(2.5f));

        Container card = new Container(BoxLayout.y());
        card.add(top);
        card.add(line);
        card.add(missedBox);

        Style cs = card.getAllStyles();
        cs.setBgColor(SAVI.WHITE);
        cs.setBgTransparency(255);
        cs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        cs.setPadding(1.8f, 1.8f, 1.8f, 1.8f);
        cs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        cs.setMargin(0.8f, 0.8f, 0.8f, 0.8f);
        cs.setBorder(RoundRectBorder.create().cornerRadius(4f).shadowOpacity(45).shadowBlur(8).shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(SAVI.BRIGHT_VIOLET));

        return card;
    }
}