package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.l10n.SimpleDateFormat;
import com.savi.app.SAVI;
import com.savi.app.backend.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarPage {

    public void show() {

        CalendarLogic logic = new CalendarLogic(); //backend

        Form calendarForm = new Form("", new BorderLayout());
        calendarForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        calendarForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);
        content.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        content.getAllStyles().setPadding(0.5f, 2f, 0, 0);

        MonthCalendar cld = new MonthCalendar();

        // date labels [Today     (the date)]
        Label dayTextLabel = SAVI.text("", SAVI.bold(2.3f), SAVI.WHITE);
        Label formattedDateLabel = SAVI.text("", SAVI.regular(2f), SAVI.WHITE);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        // show the starting date immediately
        LocalDate initialDate = logic.toLocalDate(cld.getDate());
        dayTextLabel.setText(logic.getDayText(initialDate));
        formattedDateLabel.setText(formatter.format(cld.getDate()));

        // date bar
        Container dateBar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        dateBar.add(BorderLayout.WEST, dayTextLabel);
        dateBar.add(BorderLayout.EAST, formattedDateLabel);
        HomePage.boxStyles(dateBar, SAVI.DARK_PURPLE, 1.5f, 2f);
        dateBar.getAllStyles().setMarginTop(1.2f);
        dateBar.getAllStyles().setMarginBottom(1f);

        content.add(cld);
        content.add(dateBar);

        // Date-dependent records container
        Container recordsContainer = new Container(BoxLayout.y());

        Label emptyMessage = SAVI.text("No savings recorded for this date.", SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
        emptyMessage.getAllStyles().setAlignment(Component.CENTER);
        emptyMessage.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        emptyMessage.getAllStyles().setPadding(4f, 4f, 2f, 2f);

        content.add(recordsContainer);

        // load the cards for the starting date
        showRecords(recordsContainer, emptyMessage, initialDate, logic);

        // runs when a day is selected
        cld.addDayActionListener(e -> {
            LocalDate selected = logic.toLocalDate(cld.getDate());
            dayTextLabel.setText(logic.getDayText(selected));
            formattedDateLabel.setText(formatter.format(cld.getDate()));
            showRecords(recordsContainer, emptyMessage, selected, logic);
        });

        calendarForm.add(BorderLayout.NORTH, SAVI.buildHeader("Savings Calendar", () -> new HomePage().show()));
        calendarForm.add(BorderLayout.CENTER, content);
        calendarForm.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_CALENDAR));
        calendarForm.show();
    }

    // rebuilds the cards for the selected date
    private void showRecords(Container recordsContainer, Label emptyMessage, LocalDate date, CalendarLogic logic) {
        recordsContainer.removeAll();

        ArrayList<GroupSavings> found = logic.getGroupsWithRecords(SAVI.groupList.getGroups(), date);

        if (found.isEmpty()) {
            recordsContainer.add(emptyMessage);
        } else {
            for (GroupSavings g : found) {
                recordsContainer.add(createTrackerCard(g, date, logic));
            }
        }
        recordsContainer.revalidate();
    }

    // one card per group (same design as before, but with real data)
    private Container createTrackerCard(GroupSavings g, LocalDate date, CalendarLogic logic) {
        ArrayList<Contribution> list = g.getContributionsOn(date);
        int count = list.size();

        Container trackerCard = new Container(BoxLayout.y());
        HomePage.boxStyles(trackerCard, SAVI.WHITE, 2f, 2f);

        // HEADER: group name on the left, status on the right
        Container trackerHeader = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        Label groupName = SAVI.text(g.getGroupName(), SAVI.bold(2.3f), SAVI.DARK_KNIGHT);

        String statusStr = logic.getStatus(g, date);
        boolean isSaved = "Saved".equalsIgnoreCase(statusStr);
        Label status = SAVI.text(statusStr, SAVI.bold(1.7f), isSaved ? SAVI.GREEN : SAVI.AMBER);
        Style ss = status.getAllStyles();
        ss.setBgColor(isSaved ? SAVI.PALE_GREEN : SAVI.YELLOW_TAG);
        ss.setBgTransparency(255);
        ss.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ss.setPadding(0.6f, 0.6f, 1.5f, 1.5f);
        ss.setBorder(RoundRectBorder.create().cornerRadius(2f));

        trackerHeader.add(BorderLayout.WEST, groupName);
        trackerHeader.add(BorderLayout.EAST, status);

        // AMOUNT SAVED
        Label amountSaved = SAVI.text("Amount Saved: " + Home.pesoFormat(g.getTotalOn(date)), SAVI.bold(2.2f), SAVI.GREEN);
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

        // CONTRIBUTIONS LIST: one row per contribution
        Container contributionsList = new Container(BoxLayout.y());
        for (Contribution c : list) {
            contributionsList.add(createContributionRow(c.getMember(), "+" + Home.pesoFormat(c.getAmount())));
        }
        contributionsList.setHidden(true);

        // EXPAND / COLLAPSE
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

        // NOTE SECTION (only if there is a note)
        String note = logic.getNoteOn(g, date);
        if (!note.isEmpty()) {
            Label noteTitle = SAVI.text("Note", SAVI.semiBold(1.7f), SAVI.SLATE_GRAY);
            noteTitle.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
            noteTitle.getAllStyles().setMargin(1f, 0, 0, 0);
            Label noteText = SAVI.text(note, SAVI.regular(1.8f), SAVI.SLATE_GRAY);
            trackerCard.add(noteTitle);
            trackerCard.add(noteText);
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
}