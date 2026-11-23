package com.savi.app.frontend;

import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.ui.spinner.Picker;
import com.codename1.util.SuccessCallback;
import com.savi.app.SAVI;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.Home;
import com.savi.app.backend.PersonalSavings;
import com.savi.app.backend.Profile;
import com.savi.app.backend.Savings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// PENDING PAGE (frontend)
// Styled like SummaryPage:
//   1. Top purple pill header switches between "Personal Savings Pending" and "Group Savings Pending"
//   2. Second purple card has the tracker Picker + chevron on top, "Total Pending" on the left,
//      and status ("Due Soon" / "All Settled") on the right
//   3. Below that: member/user missed-contribution cards + "Remind People" / "Set reminder"
public class PendingPage {

    private boolean personalMode = false;
    private int selectedIndex = 0;

    public void show() {
        show(false, 0);
    }

    public void show(boolean isPersonalMode, int startIndex) {
        this.personalMode = isPersonalMode;
        this.selectedIndex = Math.max(0, startIndex);

        Form pendingForm = new Form("", new BorderLayout());
        pendingForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        pendingForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container t = pendingForm.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        String currentUser = activeUsername();

        // Pull only the active mode's trackers for the logged-in user
        final ArrayList<Savings> trackers = new ArrayList<>();
        if (personalMode) {
            trackers.addAll(SAVI.personalList.getForUser(currentUser));
        } else {
            trackers.addAll(SAVI.groupList.getForUser(currentUser));
        }
        if (selectedIndex >= trackers.size()) {
            selectedIndex = 0;
        }

        // Compute pending items for the selected tracker
        final ArrayList<String> pendingNames = new ArrayList<>();
        final ArrayList<Double> pendingAmounts = new ArrayList<>();
        double totalPending = 0;

        Savings activeTracker = trackers.isEmpty() ? null : trackers.get(selectedIndex);
        if (activeTracker instanceof GroupSavings) {
            GroupSavings g = (GroupSavings) activeTracker;
            for (String m : g.getMembers()) {
                double missed = g.getTotalMissed(m);
                if (missed > 0) {
                    pendingNames.add(m);
                    pendingAmounts.add(missed);
                    totalPending += missed;
                }
            }
        } else if (activeTracker instanceof PersonalSavings) {
            PersonalSavings p = (PersonalSavings) activeTracker;
            double missed = p.getTotalMissed();
            if (missed > 0) {
                pendingNames.add(currentUser);
                pendingAmounts.add(missed);
                totalPending += missed;
            }
        }

        // --- 1. TOP PURPLE PILL SWITCHER (matches SummaryPage.summaryHeader) ---
        Container topHeader = pendingHeader(personalMode);

        // --- 2. SECOND PURPLE CARD (matches SummaryPage.poolCardFor) ---
        String activeTitle = activeTracker == null
                ? (personalMode ? "NO PERSONAL SAVINGS" : "NO GROUP YET")
                : activeTracker.getDisplayName().toUpperCase();

        String[] pickerChoices = pickerItems(trackers, activeTitle);

        Label statusHeader = txt("Status", SAVI.regular(2f), SAVI.WHITE);
        statusHeader.getAllStyles().setAlignment(Component.RIGHT);
        Label statusValue = txt(totalPending > 0 ? "Due Soon" : "All Settled", SAVI.semiBold(2.3f), SAVI.WHITE);

        Container rightCol = new Container(BoxLayout.y());
        rightCol.add(statusHeader);
        rightCol.add(FlowLayout.encloseRight(statusValue));

        Container summaryCard = purplePendingCard(
                activeTitle,
                pickerChoices,
                chosen -> {
                    for (int i = 0; i < trackers.size(); i++) {
                        if (trackers.get(i).getDisplayName().equalsIgnoreCase(chosen)) {
                            new PendingPage().show(personalMode, i);
                            return;
                        }
                    }
                },
                Home.pesoFormat(totalPending),
                rightCol
        );

        // --- 3. MEMBER / PERSONAL PENDING CARDS ---
        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        content.getAllStyles().setPadding(0, 2f, 0, 0);

        content.add(summaryCard);

        if (trackers.isEmpty()) {
            Label empty = SAVI.text(
                    personalMode ? "No personal savings created yet." : "No group savings created yet.",
                    SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
            empty.getAllStyles().setAlignment(Component.CENTER);
            empty.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
            empty.getAllStyles().setPadding(4f, 4f, 2f, 2f);
            content.add(empty);
        } else if (pendingNames.isEmpty()) {
            Label allPaid = SAVI.text(
                    personalMode ? "You are up to date on your personal savings!"
                            : "Everyone is up to date on contributions!",
                    SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
            allPaid.getAllStyles().setAlignment(Component.CENTER);
            allPaid.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
            allPaid.getAllStyles().setPadding(4f, 4f, 2f, 2f);
            content.add(allPaid);
        } else {
            int rows = (pendingNames.size() + 1) / 2;
            Container memberGrid = new Container(new GridLayout(rows, 2));
            memberGrid.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
            memberGrid.getAllStyles().setMargin(0.6f, 1f, 1.2f, 1.2f);

            for (int i = 0; i < pendingNames.size(); i++) {
                String member = pendingNames.get(i);
                String displayName = personalMode ? "You" : member;
                String handle = "@" + member.toLowerCase();
                String missedStr = Home.pesoFormat(pendingAmounts.get(i));
                memberGrid.add(createMemberCard(displayName, handle, missedStr));
            }
            content.add(memberGrid);
        }

        // --- 4. BOTTOM: REMIND PEOPLE + SET REMINDER ---
        final Savings finalTracker = activeTracker;
        Button remindButton = new Button(personalMode ? "Remind Me" : "Remind People");
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
            if (pendingNames.isEmpty() || finalTracker == null) {
                Dialog.show("All Clear", "No pending contributions to remind.", "OK", null);
                return;
            }

            LocalDate today = LocalDate.now();
            String nowTime = new SimpleDateFormat("hh:mm a").format(new Date());
            String trackerLabel = "[" + finalTracker.getTypeLabel() + "] " + finalTracker.getDisplayName();

            for (int i = 0; i < pendingNames.size(); i++) {
                String member = pendingNames.get(i);
                double missedAmt = pendingAmounts.get(i);
                String reminderTitle = personalMode
                        ? "Personal deposit: " + finalTracker.getDisplayName() + " (missed target)"
                        : "Follow up: " + member + " (missed contribution)";
                SAVI.reminders.addReminder(currentUser, reminderTitle, trackerLabel, today, nowTime, missedAmt);
            }

            Dialog.show("Reminder Added",
                    "Added " + pendingNames.size() + " pending reminder(s) for " + finalTracker.getDisplayName() + " to your Reminders.",
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
        setReminder.addActionListener(e -> new ReminderPage().show());

        Container bottomBar = new Container(BoxLayout.y());
        bottomBar.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        bottomBar.getAllStyles().setBgTransparency(255);
        bottomBar.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bottomBar.getAllStyles().setPadding(1f, 1.5f, 1f, 1f);
        bottomBar.add(remindButton);
        bottomBar.add(setReminder);

        pendingForm.add(BorderLayout.NORTH, topHeader);
        pendingForm.add(BorderLayout.CENTER, content);
        pendingForm.add(BorderLayout.SOUTH, bottomBar);
        pendingForm.show();
    }

    // TOP DROPDOWN PILL: switches between Personal Savings Pending and Group Savings Pending
    private Container pendingHeader(boolean isPersonal) {
        final String[] modes = {"Personal Savings Pending", "Group Savings Pending"};
        String current = isPersonal ? modes[0] : modes[1];

        Button back = new Button();
        Style bs = back.getAllStyles();
        bs.setFgColor(SAVI.DARK_PURPLE);
        bs.setBgTransparency(0);
        bs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(0.6f, 0.6f, 0.6f, 0.6f);
        bs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 3.5f);
        back.addActionListener(e -> new HomePage().show());

        Picker modePicker = titlePicker(modes, current, SAVI.bold(2.2f), chosen -> {
            new PendingPage().show(chosen.equals(modes[0]), 0);
        });
        modePicker.getAllStyles().setAlignment(Component.CENTER);

        Container titleArea = new Container(new BorderLayout());
        titleArea.add(BorderLayout.CENTER, modePicker);
        titleArea.add(BorderLayout.EAST, chevron(SAVI.DARK_PURPLE));
        titleArea.setLeadComponent(modePicker);

        Container pill = new Container(new BorderLayout());
        Style ps = pill.getAllStyles();
        ps.setBgColor(SAVI.DARK_PURPLE);
        ps.setBgTransparency(255);
        ps.setBorder(RoundRectBorder.create().cornerRadius(4f).shadowOpacity(50).shadowBlur(10)
                .shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(SAVI.SLATE_GRAY));
        ps.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ps.setPadding(1.5f, 1.5f, 1.5f, 1.5f);
        ps.setMarginUnit(Style.UNIT_TYPE_DIPS);
        ps.setMargin(1f, 0.4f, 2f, 2f);
        pill.add(BorderLayout.WEST, back);
        pill.add(BorderLayout.CENTER, titleArea);

        Container wrap = new Container(new BorderLayout());
        wrap.add(BorderLayout.CENTER, pill);
        return wrap;
    }

    // SECOND PURPLE CARD: matches SummaryPage.poolCardFor
    private Container purplePendingCard(String title, String[] items, SuccessCallback<String> onPick,
                                        String totalPendingText, Component right) {
        Container card = new Container(new BorderLayout());
        Style contS = card.getAllStyles();
        contS.setBgTransparency(255);
        contS.setBgColor(SAVI.DARK_PURPLE);
        contS.setBorder(RoundRectBorder.create()
                .cornerRadius(4)
                .shadowOpacity(50)
                .shadowBlur(10)
                .shadowSpread(1f)
                .shadowX(0.5f)
                .shadowY(0.3f)
                .shadowColor(SAVI.SLATE_GRAY));
        contS.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        contS.setPadding(4f, 4f, 3f, 3f);
        contS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        contS.setMargin(0.5f, 0.5f, 2f, 2f);

        Picker namePicker = titlePicker(items, title, SAVI.bold(2.5f), onPick);

        Container topRow = new Container(new BorderLayout());
        topRow.add(BorderLayout.WEST, namePicker);
        topRow.add(BorderLayout.EAST, chevron(SAVI.DARK_PURPLE));
        topRow.setLeadComponent(namePicker);

        Container total = new Container(BoxLayout.y());
        total.add(txt("Total Pending", SAVI.regular(2f), SAVI.WHITE));
        total.add(txt(totalPendingText, SAVI.bold(2.6f), SAVI.WHITE));

        Container bottomRow = new Container(new BorderLayout());
        bottomRow.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        bottomRow.getAllStyles().setMargin(1.5f, 0, 0, 0);
        bottomRow.add(BorderLayout.WEST, total);
        bottomRow.add(BorderLayout.EAST, right);

        card.add(BorderLayout.NORTH, topRow);
        card.add(BorderLayout.SOUTH, bottomRow);
        return card;
    }

    private Label chevron(int iconColor) {
        Label chev = new Label();
        Style cs = chev.getAllStyles();
        cs.setFgColor(iconColor);
        cs.setBgTransparency(0);
        cs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        cs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        cs.setPadding(0.3f, 0.3f, 0.3f, 0.3f);
        cs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(chev, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 3f);
        return chev;
    }

    private Picker titlePicker(String[] items, final String current, Font font,
                               final SuccessCallback<String> onPick) {
        final Picker p = new Picker();
        p.setUIID("Label");
        p.setType(Display.PICKER_TYPE_STRINGS);
        p.setStrings(items);
        p.setSelectedString(current);
        Style st = p.getAllStyles();
        st.setFont(font);
        st.setFgColor(SAVI.WHITE);
        st.setBgTransparency(0);
        st.setBorder(Border.createEmpty());
        st.setMargin(0, 0, 0, 0);
        st.setPadding(0, 0, 0, 0);
        p.addActionListener(e -> {
            String chosen = p.getSelectedString();
            if (chosen != null && !chosen.equalsIgnoreCase(current)) {
                onPick.onSucess(chosen);
            }
        });
        return p;
    }

    private String[] pickerItems(List<Savings> trackers, String fallbackTitle) {
        if (trackers.isEmpty()) {
            return new String[]{fallbackTitle};
        }
        String[] items = new String[trackers.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = trackers.get(i).getDisplayName().toUpperCase();
        }
        return items;
    }

    private Label txt(String s, Font f, int color) {
        Label l = SAVI.text(s, f, color);
        Style st = l.getAllStyles();
        st.setMargin(0, 0, 0, 0);
        st.setPadding(0, 0, 0, 0);
        return l;
    }

    private static String activeUsername() {
        if (SAVI.user != null && SAVI.user.getCurrentName() != null
                && !SAVI.user.getCurrentName().trim().isEmpty()) {
            return SAVI.user.getCurrentName().trim();
        }
        return Profile.getInstance().getMemberName();
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