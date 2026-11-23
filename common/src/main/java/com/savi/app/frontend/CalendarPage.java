package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.l10n.SimpleDateFormat;
import com.savi.app.SAVI;
import com.savi.app.backend.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// CALENDAR PAGE (frontend)
// FLOW: open the page -> show today's records -> the user taps a date -> the page asks the backend
// (CalendarLogic + the trackers) -> the cards for that date are rebuilt.
// This page only READS data. It never changes or saves anything.
public class CalendarPage {

    public void show() {

        CalendarLogic logic = new CalendarLogic(); //backend: the page asks it questions about dates and trackers

        Form calendarForm = new Form("", new BorderLayout());
        calendarForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        calendarForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container t = calendarForm.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        content.getAllStyles().setPadding(0.5f, 2f, 0, 0);

        MonthCalendar cld = new MonthCalendar();   // our own month calendar (see MonthCalendar.java)

        // date labels [Today     (the date)]
        Label dayTextLabel = SAVI.text("", SAVI.bold(2.3f), SAVI.WHITE);
        Label formattedDateLabel = SAVI.text("", SAVI.regular(2f), SAVI.WHITE);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        // show the starting date immediately
        LocalDate initialDate = logic.toLocalDate(cld.getDate());
        dayTextLabel.setText(logic.getDayText(initialDate));          // "Today"
        formattedDateLabel.setText(formatter.format(cld.getDate()));  // "Nov 20, 2026"

        // date bar
        Container dateBar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        dateBar.add(BorderLayout.WEST, dayTextLabel);
        dateBar.add(BorderLayout.EAST, formattedDateLabel);
        HomePage.boxStyles(dateBar, SAVI.DARK_PURPLE, 1.5f, 2f);
        dateBar.getAllStyles().setMarginTop(1.2f);
        dateBar.getAllStyles().setMarginBottom(1f);

        content.add(cld);
        content.add(dateBar);

        // Date-dependent records container: its content is rebuilt every time a date is selected
        Container recordsContainer = new Container(BoxLayout.y());

        // shown when the selected date has no records
        Label emptyMessage = SAVI.text("No savings recorded for this date.", SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
        emptyMessage.getAllStyles().setAlignment(Component.CENTER);
        emptyMessage.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        emptyMessage.getAllStyles().setPadding(4f, 4f, 2f, 2f);

        content.add(recordsContainer);

        // load the cards for the starting date
        showRecords(recordsContainer, emptyMessage, initialDate, logic);

        // runs when a day is selected: update the date bar, then rebuild the cards
        cld.addDayActionListener(e -> {
            LocalDate selected = logic.toLocalDate(cld.getDate());
            dayTextLabel.setText(logic.getDayText(selected));
            formattedDateLabel.setText(formatter.format(cld.getDate()));
            showRecords(recordsContainer, emptyMessage, selected, logic);
            content.revalidate();
        });

        // layout: shared header on top, scrolling content in the middle, shared bottom nav at the bottom
        calendarForm.add(BorderLayout.NORTH, SAVI.buildHeader("Savings Calendar", () -> new HomePage().show()));
        calendarForm.add(BorderLayout.CENTER, content);
        calendarForm.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_CALENDAR));
        calendarForm.show();
    }

    // rebuilds the cards for the selected date
    private void showRecords(Container recordsContainer, Label emptyMessage, LocalDate date, CalendarLogic logic) {
        recordsContainer.removeAll();

        String currentUser = activeUsername();
        ArrayList<Savings> allUserTrackers = new ArrayList<>();
        allUserTrackers.addAll(SAVI.groupList.getForUser(currentUser));
        allUserTrackers.addAll(SAVI.personalList.getForUser(currentUser));

        // ask the backend: which group or personal trackers have contributions on this date?
        ArrayList<Savings> found = logic.getTrackersWithRecords(allUserTrackers, date);

        if (found.isEmpty()) {
            recordsContainer.add(emptyMessage);
        } else {
            for (Savings s : found) {
                recordsContainer.add(createTrackerCard(s, date, logic));   // one card per tracker
            }
        }
        recordsContainer.revalidate();
    }

    // one card per tracker (works polymorphically for both GroupSavings and PersonalSavings)
    private Container createTrackerCard(Savings s, LocalDate date, CalendarLogic logic) {
        List<Contribution> list = s.getContributionsOn(date);   // inherited from Savings
        int count = list.size();

        Container trackerCard = new Container(BoxLayout.y());
        HomePage.boxStyles(trackerCard, SAVI.WHITE, 2f, 2f);

        // HEADER: tracker name (+ type tag) on the left, status on the right
        Container trackerHeader = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        String title = "[" + s.getTypeLabel() + "] " + s.getDisplayName();
        Label trackerName = SAVI.text(title, SAVI.bold(2.3f), SAVI.DARK_KNIGHT);

        // status badge: green for "Saved", yellow for anything else
        String statusStr = logic.getStatus(s, date);
        boolean isSaved = "Saved".equalsIgnoreCase(statusStr);
        Label status = SAVI.text(statusStr, SAVI.bold(1.7f), isSaved ? SAVI.GREEN : SAVI.AMBER);
        Style ss = status.getAllStyles();
        ss.setBgColor(isSaved ? SAVI.PALE_GREEN : SAVI.YELLOW_TAG);
        ss.setBgTransparency(255);
        ss.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ss.setPadding(0.6f, 0.6f, 1.5f, 1.5f);
        ss.setBorder(RoundRectBorder.create().cornerRadius(2f));

        trackerHeader.add(BorderLayout.WEST, trackerName);
        trackerHeader.add(BorderLayout.EAST, status);

        // AMOUNT SAVED: the total of this date's contributions (getTotalOn is from Savings)
        Label amountSaved = SAVI.text("Amount Saved: " + Home.pesoFormat(s.getTotalOn(date)), SAVI.bold(2.2f), SAVI.GREEN);
        amountSaved.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        amountSaved.getAllStyles().setMargin(0.8f, 1f, 0, 0);

        // CONTRIBUTIONS BUTTON for drop down
        Button viewContributions = new Button("View contributions (" + count + ")");
        viewContributions.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);
        Style vs = viewContributions.getAllStyles();
        vs.setFont(SAVI.semiBold(1.9f));
        vs.setFgColor(SAVI.DARK_PURPLE);
        vs.setBgColor(SAVI.PALE_PURPLE);
        vs.setBgTransparency(255);
        vs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        vs.setPadding(1.2f, 1.2f, 1.5f, 1.5f);
        vs.setBorder(RoundRectBorder.create().cornerRadius(2.5f));

        // CONTRIBUTIONS LIST: one row per contribution (hidden until the button is tapped)
        Container contributionsList = new Container(BoxLayout.y());
        for (Contribution c : list) {
            String amountPrefix = c.getAmount() >= 0 ? "+" : "";
            contributionsList.add(createContributionRow(c.getMember(), amountPrefix + Home.pesoFormat(c.getAmount())));
        }
        contributionsList.setHidden(true);

        // EXPAND / COLLAPSE: show or hide the list and swap the button text and arrow
        viewContributions.addActionListener(e -> {
            boolean isHidden = contributionsList.isHidden();
            contributionsList.setHidden(!isHidden);

            if (isHidden) {
                viewContributions.setText("Hide contributions (" + count + ")");
                viewContributions.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_UP);
            } else {
                viewContributions.setText("View contributions (" + count + ")");
                viewContributions.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);
            }
            trackerCard.revalidate();
        });

        // ADD COMPONENTS IN ORDER
        trackerCard.add(trackerHeader);
        trackerCard.add(amountSaved);
        trackerCard.add(viewContributions);
        trackerCard.add(contributionsList);

        // NOTES SECTION (lists every contributor's note on this date)
        ArrayList<String> notes = logic.getNotesOn(s, date);
        if (!notes.isEmpty()) {
            Label noteTitle = SAVI.text(notes.size() > 1 ? "Notes" : "Note", SAVI.semiBold(1.7f), SAVI.SLATE_GRAY);
            noteTitle.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
            noteTitle.getAllStyles().setMargin(1f, 0.3f, 0, 0);
            trackerCard.add(noteTitle);

            for (String noteEntry : notes) {
                Label noteText = SAVI.text("• " + noteEntry, SAVI.regular(1.8f), SAVI.SLATE_GRAY);
                noteText.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
                noteText.getAllStyles().setMargin(0.2f, 0.2f, 0, 0);
                trackerCard.add(noteText);
            }
        }

        return trackerCard;
    }

    // one "name .... +₱10.00" row
    private Container createContributionRow(String name, String amount) {
        Label nameLabel = SAVI.text(name, SAVI.regular(1.9f), SAVI.DARK_KNIGHT);
        Label amountLabel = SAVI.text(amount, SAVI.semiBold(1.9f), SAVI.GREEN);

        Container row = new Container(new BorderLayout());
        row.add(BorderLayout.WEST, nameLabel);
        row.add(BorderLayout.EAST, amountLabel);
        row.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        row.getAllStyles().setPadding(0.8f, 0.8f, 1.5f, 1.5f);
        return row;
    }

    private static String activeUsername() {
        if (SAVI.user != null && SAVI.user.getCurrentName() != null
                && !SAVI.user.getCurrentName().trim().isEmpty()) {
            return SAVI.user.getCurrentName().trim();
        }
        return Profile.getInstance().getMemberName();
    }
}