package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.ui.spinner.Picker;
import com.savi.app.SAVI;
import java.time.LocalDate;
import com.codename1.l10n.SimpleDateFormat;
import java.util.Date;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.Home;

import java.util.ArrayList;

// PENDING PAGE (frontend)
// FLOW: open the page -> pick a group from SAVI.groupList -> ask GroupSavings how much each member missed ->
// show a card for every member who still owes money -> "Remind People" creates one reminder
// per pending member (saved by ReminderList) and opens the Reminder page.
public class PendingPage {

    private int selectedGroupIndex = 0;

    public void show() {

        Form pendingForm = new Form("", new BorderLayout());
        pendingForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        pendingForm.getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        content.getAllStyles().setPadding(0.5f, 2f, 0, 0);

        // Pull all groups dynamically from SAVI.groupList.getGroups()
        ArrayList<GroupSavings> allGroups = SAVI.groupList.getGroups();
        if (selectedGroupIndex >= allGroups.size()) {
            selectedGroupIndex = 0;
        }

        ArrayList<String> pendingNames = new ArrayList<>();      // members who still owe money
        ArrayList<Double> pendingAmounts = new ArrayList<>();    // how much each of them owes (same order)
        String[] activeTitleHolder = new String[]{ allGroups.isEmpty() ? "" : allGroups.get(selectedGroupIndex).getGroupName() };

        // --- STATIC TOP HEADER INSIDE SUMMARY CARD (same native Picker wheel + chevron as HomePage) ---
        Label groupBullet = SAVI.text("● ", SAVI.bold(2.2f), SAVI.DARK_PURPLE);
        Container groupHeader = new Container(new BorderLayout());
        groupHeader.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        groupHeader.getAllStyles().setMargin(0, 1.2f, 0, 0);
        groupHeader.add(BorderLayout.WEST, groupBullet);

        Picker groupPicker = new Picker();
        if (!allGroups.isEmpty()) {
            String[] groupNames = new String[allGroups.size()];
            for (int i = 0; i < allGroups.size(); i++) {
                groupNames[i] = allGroups.get(i).getGroupName();
            }
            groupPicker.setType(Display.PICKER_TYPE_STRINGS);
            groupPicker.setStrings(groupNames);
            groupPicker.setSelectedString(groupNames[selectedGroupIndex]);

            Style gps = groupPicker.getAllStyles();
            gps.setFont(SAVI.bold(2.4f));
            gps.setFgColor(SAVI.DARK_KNIGHT);
            gps.setBgTransparency(0);
            gps.setBorder(Border.createEmpty());
            gps.setPaddingUnit(Style.UNIT_TYPE_DIPS);
            gps.setPadding(0.6f, 0.6f, 0, 0);
            gps.setMargin(0, 0, 0, 0);
            gps.setAlignment(Component.LEFT);

            Label arrow = new Label();
            arrow.getAllStyles().setFgColor(SAVI.DARK_KNIGHT);
            arrow.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
            arrow.getAllStyles().setPadding(0.6f, 0.6f, 1f, 0.5f);
            arrow.getAllStyles().setMargin(0, 0, 0, 0);
            FontImage.setMaterialIcon(arrow, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 4f);

            groupHeader.add(BorderLayout.CENTER, groupPicker);
            groupHeader.add(BorderLayout.EAST, arrow);

            // Makes the entire top bar (text, empty space, AND the chevron arrow) trigger the Picker wheel
            groupHeader.setLeadComponent(groupPicker);
        } else {
            Label emptyTitle = SAVI.text("No Active Groups", SAVI.bold(2.5f), SAVI.SLATE_GRAY);
            groupHeader.add(BorderLayout.CENTER, emptyTitle);
        }

        // Dynamic containers below the picker so the Picker itself is never removed/re-added
        Container amountHolder = new Container(new BorderLayout());
        Container pendingCard = new Container(BoxLayout.y());
        pendingCard.add(groupHeader);
        pendingCard.add(amountHolder);
        HomePage.boxStyles(pendingCard, SAVI.WHITE, 2f, 2f);
        pendingCard.getAllStyles().setMarginBottom(1.2f);

        Container memberSection = new Container(BoxLayout.y());

        Runnable refreshGroup = () -> {
            amountHolder.removeAll();
            memberSection.removeAll();
            pendingNames.clear();
            pendingAmounts.clear();

            GroupSavings activeGroup = allGroups.isEmpty() ? null : allGroups.get(selectedGroupIndex);
            activeTitleHolder[0] = activeGroup != null ? activeGroup.getGroupName() : "";
            double totalPending = 0;

            // BACKEND CALCULATION: for every member, ask GroupSavings how much they missed.
            // Only members with a missed amount above 0 are kept, and the amounts are added up.
            if (activeGroup != null) {
                for (String m : activeGroup.getMembers()) {
                    double missed = activeGroup.getTotalMissed(m);
                    if (missed > 0) {
                        pendingNames.add(m);
                        pendingAmounts.add(missed);
                        totalPending += missed;
                    }
                }
            }

            Label amountTitle = SAVI.text("TOTAL PENDING AMOUNT", SAVI.bold(1.7f), SAVI.AMBER);
            Label pendingAmount = SAVI.text(Home.pesoFormat(totalPending), SAVI.bold(3.2f), SAVI.AMBER);
            Container amountBox = BoxLayout.encloseY(amountTitle, pendingAmount);

            // the tag and colors change depending on whether anyone still owes money
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

            amountHolder.add(BorderLayout.CENTER, amountSection);

            // MEMBER CARDS (2 PER ROW)
            if (allGroups.isEmpty()) {
                Label noGroups = SAVI.text("No group savings created yet.", SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
                noGroups.getAllStyles().setAlignment(Component.CENTER);
                noGroups.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
                noGroups.getAllStyles().setPadding(4f, 4f, 2f, 2f);
                memberSection.add(noGroups);
            } else if (pendingNames.isEmpty()) {
                // nobody owes anything
                Label allPaid = SAVI.text("Everyone is up to date on contributions!", SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
                allPaid.getAllStyles().setAlignment(Component.CENTER);
                allPaid.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
                allPaid.getAllStyles().setPadding(4f, 4f, 2f, 2f);
                memberSection.add(allPaid);
            } else {
                int rows = (pendingNames.size() + 1) / 2;   // 2 cards per row, so round the row count up
                Container memberGrid = new Container(new GridLayout(rows, 2));
                memberGrid.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
                memberGrid.getAllStyles().setMargin(0, 1f, 1.2f, 1.2f);

                for (int i = 0; i < pendingNames.size(); i++) {
                    String member = pendingNames.get(i);
                    String handle = "@" + member.toLowerCase();
                    String missedStr = Home.pesoFormat(pendingAmounts.get(i));
                    memberGrid.add(createMemberCard(member, handle, missedStr));
                }
                memberSection.add(memberGrid);
            }

            pendingCard.revalidate();
            memberSection.revalidate();
            content.revalidate();
        };

        if (!allGroups.isEmpty()) {
            groupPicker.addActionListener(e -> {
                String picked = groupPicker.getSelectedString();
                if (picked != null) {
                    for (int i = 0; i < allGroups.size(); i++) {
                        if (allGroups.get(i).getGroupName().equalsIgnoreCase(picked)) {
                            selectedGroupIndex = i;
                            break;
                        }
                    }
                    refreshGroup.run();
                }
            });
        }

        refreshGroup.run();
        content.add(pendingCard);
        content.add(memberSection);

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

        // REMIND PEOPLE: this is where the Pending page connects to the Reminder page
        remindButton.addActionListener(e -> {
            if (pendingNames.isEmpty()) {
                Dialog.show("All Clear", "No pending members to remind.", "OK", null);
                return;
            }

            LocalDate today = LocalDate.now();
            String nowTime = new SimpleDateFormat("hh:mm a").format(new Date());
            String chosenGroup = activeTitleHolder[0];

            // Gumawa ng automatic reminder entry sa SAVI.reminders (at automatic na mase-save sa Reminders_Records.csv)
            // (one reminder per pending member; addReminder links a new node in the list and saves the CSV)
            for (int i = 0; i < pendingNames.size(); i++) {
                String member = pendingNames.get(i);
                double missedAmt = pendingAmounts.get(i);
                String reminderTitle = "Follow up: " + member + " (missed contribution)";
                SAVI.reminders.addReminder(reminderTitle, chosenGroup, today, nowTime, missedAmt);
            }

            Dialog.show("Reminder Added",
                    "Added " + pendingNames.size() + " pending reminder(s) for " + chosenGroup + " to your Reminders.",
                    "OK", null);
            new ReminderPage().show();
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

        // "Set reminder" just opens the Reminder page so the user can add one manually
        setReminder.addActionListener(e -> new ReminderPage().show());

        Container bottomBar = new Container(BoxLayout.y());
        bottomBar.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        bottomBar.getAllStyles().setBgTransparency(255);
        bottomBar.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bottomBar.getAllStyles().setPadding(1f, 1.5f, 1f, 1f);
        bottomBar.add(remindButton);
        bottomBar.add(setReminder);

        // FORM LAYOUT: shared header on top, content in the middle, buttons at the bottom
        pendingForm.add(BorderLayout.NORTH, SAVI.buildHeader("Pending", () -> new HomePage().show()));
        pendingForm.add(BorderLayout.CENTER, content);
        pendingForm.add(BorderLayout.SOUTH, bottomBar);
        pendingForm.show();
    }

    // MEMBER CARD METHOD: builds one card (avatar, name, @handle, missed amount) for one member
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