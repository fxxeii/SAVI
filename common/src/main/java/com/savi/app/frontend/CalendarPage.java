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

        Form calendarForm = new Form(new BorderLayout());
        calendarForm.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        calendarForm.getAllStyles().setBgTransparency(255);

        // for back button
        calendarForm.getToolbar().addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK, e -> {new HomePage().show();});

        Container content = new Container(BoxLayout.y());
        content.setScrollableY(true);
        content.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        content.getAllStyles().setBgTransparency(255);

        MonthCalendar cld = new MonthCalendar();

        // date labels [Today     (the date)]
        Label dayTextLabel = new Label();
        Label formattedDateLabel = new Label();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        // show the starting date immediately
        LocalDate initialDate = logic.toLocalDate(cld.getDate());
        dayTextLabel.setText(logic.getDayText(initialDate));
        formattedDateLabel.setText(formatter.format(cld.getDate()));

        dayTextLabel.getAllStyles().setFgColor(SAVI.WHITE);
        dayTextLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        formattedDateLabel.getAllStyles().setFgColor(SAVI.WHITE);
        formattedDateLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        // date bar
        Container dateBar = new Container(new BorderLayout());
        dateBar.add(BorderLayout.WEST, dayTextLabel);
        dateBar.add(BorderLayout.EAST, formattedDateLabel);
        dateBar.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        dateBar.getAllStyles().setBgTransparency(255);
        dateBar.getAllStyles().setPadding(1, 1, 1, 1);
        dateBar.getAllStyles().setMargin(1, 1, 1, 1);
        dateBar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        content.add(cld);

        Container space1 = new Container();
        space1.setPreferredH(24);
        content.add(space1);

        content.add(dateBar);

        Container space2 = new Container();
        space2.setPreferredH(24);
        content.add(space2);

        // container for the date-dependent cards
        Container recordsContainer = new Container(BoxLayout.y());

        Label emptyMessage = new Label("No savings recorded for this date.");
        emptyMessage.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
        emptyMessage.getAllStyles().setFgColor(SAVI.BLUE_GRAY);
        emptyMessage.getAllStyles().setAlignment(Component.CENTER);
        emptyMessage.getAllStyles().setPadding(5, 5, 3, 3);

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

        calendarForm.add(BorderLayout.CENTER, content);
        calendarForm.show();
    }

    // rebuilds the cards for the selected date
    private void showRecords(Container recordsContainer, Label emptyMessage, LocalDate date, CalendarLogic logic) {
        recordsContainer.removeAll();

        ArrayList<GroupSavings> found = logic.getGroupsWithRecords(SAVI.groups, date);

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

        // HEADER: group name on the left, status on the right
        Container trackerHeader = new Container(new BorderLayout());
        Label groupName = new Label(g.getGroupName());
        Label status = new Label(logic.getStatus(g, date));
        trackerHeader.add(BorderLayout.WEST, groupName);
        trackerHeader.add(BorderLayout.EAST, status);

        // AMOUNT SAVED
        Label amountSaved = new Label("Amount Saved: " + logic.formatAmount(g.getTotalOn(date)));

        // CONTRIBUTIONS BUTTON for drop down
        Button viewContributions = new Button("View contributions (" + count + ")");
        viewContributions.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);

        // CONTRIBUTIONS LIST: one row per contribution
        Container contributionsList = new Container(BoxLayout.y());
        for (Contribution c : list) {
            contributionsList.add(createContributionRow(c.getMember(), "+" + logic.formatAmount(c.getAmount())));
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
            Label noteTitle = new Label("Note");
            Label noteText = new Label(note);
            noteTitle.getAllStyles().setFgColor(SAVI.BLUE_GRAY);
            noteTitle.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            noteText.getAllStyles().setFgColor(SAVI.BLUE_GRAY);
            noteText.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));
            trackerCard.add(noteTitle);
            trackerCard.add(noteText);
        }

        // TRACKER CARD STYLE
        trackerCard.getAllStyles().setBgColor(SAVI.WHITE);
        trackerCard.getAllStyles().setBgTransparency(255);
        trackerCard.getAllStyles().setPadding(3, 3, 3, 3);
        trackerCard.getAllStyles().setMargin(2, 2, 2, 2);
        trackerCard.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(3).strokeColor(SAVI.BORDER_COLOR));

        // GROUP NAME
        groupName.getAllStyles().setFgColor(SAVI.NAVY);
        groupName.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        // STATUS BADGE (green for now; a yellow "Pending" style comes later)
        status.getAllStyles().setFgColor(SAVI.GREEN);
        status.getAllStyles().setBgColor(SAVI.PALE_GREEN);
        status.getAllStyles().setBgTransparency(255);
        status.getAllStyles().setPadding(1, 1, 1, 1);
        status.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));
        status.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));

        // AMOUNT
        amountSaved.getAllStyles().setFgColor(SAVI.GREEN);
        amountSaved.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        // VIEW/HIDE CONTRIBUTIONS BUTTON
        viewContributions.getAllStyles().setFgColor(SAVI.DARK_PURPLE);
        viewContributions.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        viewContributions.getAllStyles().setBgTransparency(255);
        viewContributions.getAllStyles().setPadding(2, 2, 2, 2);
        viewContributions.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        return trackerCard;
    }

    // one "name .... +₱10.00" row
    private Container createContributionRow(String name, String amount) {
        Font rowFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        Label nameLabel = new Label(name);
        Label amountLabel = new Label(amount);
        nameLabel.getAllStyles().setFont(rowFont);
        amountLabel.getAllStyles().setFont(rowFont);
        amountLabel.getAllStyles().setFgColor(SAVI.GREEN);

        Container row = new Container(new BorderLayout());
        row.add(BorderLayout.WEST, nameLabel);
        row.add(BorderLayout.EAST, amountLabel);
        row.getAllStyles().setPadding(1, 1, 2, 2);
        return row;
    }
}