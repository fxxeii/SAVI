package com.savi.app.frontend;

import com.codename1.ui.*; // for form, button, dialog, front image
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import java.util.Date;
import com.codename1.l10n.SimpleDateFormat;

public class CalendarPage {

    public void show() {

        // COLORS
        int darkDarkPurple = 0x30205E;
        int darkPurple = 0x7C3AED;
        int palePurple = 0xF4EFFE;
        int navy = 0x29364D;
        int gray = 0x8491A7;
        int green = 0x00A878;
        int paleGreen = 0xE6FAF2;
        int borderColor = 0xEDF0F5;
        int white = 0xFFFFFF;

        Form calendarForm = new Form(new BorderLayout()); // ginagawa yung screen
        calendarForm.getAllStyles().setBgColor(palePurple);
        calendarForm.getAllStyles().setBgTransparency(255);

        // for back button
        calendarForm.getToolbar().addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK, e -> {
            // dito ilalagay yung code para bumalik sa previous screen
            // should look like: previousForm.showBack();
        });

        Container content = new Container(BoxLayout.y()); // creates a container para paglagyan ng iba pang content sa center. main content
        content.getAllStyles().setBgColor(palePurple);
        content.getAllStyles().setBgTransparency(255);

        Calendar cld = new Calendar(); // creates a calendar
        // calendar style
        cld.getAllStyles().setBgColor(darkDarkPurple);
        cld.getAllStyles().setBgTransparency(255);
        cld.getAllStyles().setPadding(3, 3, 3, 3);
        cld.getAllStyles().setMargin(2, 2, 2, 2);

        // date labels [Today     (the date)]
        Label dayTextLabel = new Label();
        Label formattedDateLabel = new Label();

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy"); // for format ng date

        // display todays date immediately after opening the app
        Date currentDate = new Date();
        String dayText = getDayText(currentDate);
        String formattedDate = formatter.format(currentDate);
        dayTextLabel.setText(dayText);
        formattedDateLabel.setText(formattedDate);

        // selected day style
        dayTextLabel.getAllStyles().setFgColor(white);
        dayTextLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        //formatted day style
        formattedDateLabel.getAllStyles().setFgColor(white);
        formattedDateLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        // date bar
        Container dateBar = new Container(new BorderLayout());
        dateBar.add(BorderLayout.WEST, dayTextLabel);
        dateBar.add(BorderLayout.EAST, formattedDateLabel);

        // date bar style
        dateBar.getAllStyles().setBgColor(darkPurple);
        dateBar.getAllStyles().setBgTransparency(255);
        dateBar.getAllStyles().setPadding(1, 1, 1, 1);
        dateBar.getAllStyles().setMargin(1, 1, 1, 1);
        dateBar.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        content.add(cld); // put the calendar inside the content

        // SPACE BETWEEN CALENDAR AND DATE BAR
        Container space1 = new Container();
        space1.setPreferredH(24);
        content.add(space1);

        content.add(dateBar); // adds date bar inside the content

        // SPACE BETWEEN DATE BAR AND TRACKER CARD
        Container space2 = new Container();
        space2.setPreferredH(24);
        content.add(space2);

        Container trackerCard = new Container(BoxLayout.y()); // isang malaking container na paglalagyan ng contribution tracker

        // HEADER: group name on the left, status on the right
        Container trackerHeader = new Container(new BorderLayout());

        Label groupName = new Label("December Vacation");
        Label status = new Label("Saved");

        trackerHeader.add(BorderLayout.WEST, groupName);
        trackerHeader.add(BorderLayout.EAST, status);

        // AMOUNT SAVED
        Label amountSaved = new Label("Amount Saved: ₱40.00");

        // CONTRIBUTIONS BUTTON for drop down
        Button viewContributions = new Button("View contributions (4)");
        viewContributions.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);

        // CONTRIBUTIONS LIST yung history or list
        Container contributionsList = new Container(BoxLayout.y());

        Container row1 = new Container(new BorderLayout());
        Label name1 = new Label("Airesse");
        Label amount1 = new Label("+₱10.00");
        row1.add(BorderLayout.WEST, name1);
        row1.add(BorderLayout.EAST, amount1);

        Container row2 = new Container(new BorderLayout());
        Label name2 = new Label("Faye");
        Label amount2 = new Label("+₱10.00");
        row2.add(BorderLayout.WEST, name2);
        row2.add(BorderLayout.EAST, amount2);

        Container row3 = new Container(new BorderLayout());
        Label name3 = new Label("Sam");
        Label amount3 = new Label("+₱10.00");
        row3.add(BorderLayout.WEST, name3);
        row3.add(BorderLayout.EAST, amount3);

        Container row4 = new Container(new BorderLayout());
        Label name4 = new Label("Shy");
        Label amount4 = new Label("+₱10.00");
        row4.add(BorderLayout.WEST, name4);
        row4.add(BorderLayout.EAST, amount4);

        contributionsList.add(row1);
        contributionsList.add(row2);
        contributionsList.add(row3);
        contributionsList.add(row4);

        contributionsList.setHidden(true); // dapat mag-open ng page naka-hide pa yung contributions list

        // EXPAND / COLLAPSE
        viewContributions.addActionListener(e -> {
            boolean isHidden = contributionsList.isHidden();

            contributionsList.setHidden(!isHidden);

            if (isHidden) {
                viewContributions.setText("Hide contributions (4)");
                viewContributions.setMaterialIcon(
                        FontImage.MATERIAL_ARROW_DROP_UP
                );
            } else {
                viewContributions.setText("View contributions (4)");
                viewContributions.setMaterialIcon(
                        FontImage.MATERIAL_ARROW_DROP_DOWN
                );
            }

            trackerCard.revalidate();
        });

        // NOTE SECTION
        Label noteTitle = new Label("Note");
        Label noteText = new Label("Completed daily round");

        // ADD COMPONENTS IN ORDER
        trackerCard.add(trackerHeader);
        trackerCard.add(amountSaved);
        trackerCard.add(viewContributions);
        trackerCard.add(contributionsList);
        trackerCard.add(noteTitle);
        trackerCard.add(noteText);

        // TRACKER CARD
        trackerCard.getAllStyles().setBgColor(white);
        trackerCard.getAllStyles().setBgTransparency(255); // 255 means show its background color fully. 0 is full transparent, 255 is full opaque
        trackerCard.getAllStyles().setPadding(3, 3, 3, 3); // padding is the space inside the container
        trackerCard.getAllStyles().setMargin(2, 2, 2, 2); // margin is the space outside the container
        // .create() creates a rounded-rectangular border
        trackerCard.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(3).strokeColor(borderColor));

        // GROUP NAME
        groupName.getAllStyles().setFgColor(navy); // fg color is for text or icon color
        // Font.createSystemFont() — changing the text style
        groupName.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        // SAVED BADGE
        status.getAllStyles().setFgColor(green);
        status.getAllStyles().setBgColor(paleGreen);
        status.getAllStyles().setBgTransparency(255);
        status.getAllStyles().setPadding(1, 1, 1, 1);
        status.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));
        status.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));

        // AMOUNT
        amountSaved.getAllStyles().setFgColor(green);
        amountSaved.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        // VIEW/HIDE CONTRIBUTIONS BUTTON
        viewContributions.getAllStyles().setFgColor(darkPurple);
        viewContributions.getAllStyles().setBgColor(palePurple);
        viewContributions.getAllStyles().setBgTransparency(255);
        viewContributions.getAllStyles().setPadding(2, 2, 2, 2);
        viewContributions.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(1));

        // CONTRIBUTION ROWS
        row1.getAllStyles().setPadding(1, 1, 2, 2);
        row2.getAllStyles().setPadding(1, 1, 2, 2);
        row3.getAllStyles().setPadding(1, 1, 2, 2);
        row4.getAllStyles().setPadding(1, 1, 2, 2);

        Font rowFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        name1.getAllStyles().setFont(rowFont);
        name2.getAllStyles().setFont(rowFont);
        name3.getAllStyles().setFont(rowFont);
        name4.getAllStyles().setFont(rowFont);

        amount1.getAllStyles().setFont(rowFont);
        amount1.getAllStyles().setFgColor(green);
        amount2.getAllStyles().setFont(rowFont);
        amount2.getAllStyles().setFgColor(green);
        amount3.getAllStyles().setFont(rowFont);
        amount3.getAllStyles().setFgColor(green);
        amount4.getAllStyles().setFont(rowFont);
        amount4.getAllStyles().setFgColor(green);

        // NOTE
        noteTitle.getAllStyles().setFgColor(gray);
        noteTitle.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        noteText.getAllStyles().setFgColor(gray);
        noteText.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));

        Container recordsContainer = new Container(BoxLayout.y()); // container for date-dependent content

        Label emptyMessage = new Label("No savings recorded for this date.");
        emptyMessage.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
        emptyMessage.getAllStyles().setFgColor(gray);
        emptyMessage.getAllStyles().setAlignment(Component.CENTER);
        emptyMessage.getAllStyles().setPadding(5, 5, 3, 3);

        content.add(recordsContainer); // then put the container inide the main content

        Date initialDate = cld.getDate();

        SimpleDateFormat dateKeyFormat = new SimpleDateFormat("yyyy-MM-dd");
        String initialDateKey = dateKeyFormat.format(initialDate);

        if (initialDateKey.equals("2026-10-02")) {
            recordsContainer.add(trackerCard);
        } else {
            recordsContainer.add(emptyMessage);
        }

        // runs when a day is selected
        cld.addDayActionListener((e) -> {
            Date selectedDate = cld.getDate(); // gets the date currently selected by the user
            //updates date bar
            String selectedDayText = getDayText(selectedDate); // get the current date
            String selectedFormattedDate = formatter.format(selectedDate); //get the formatted date
            dayTextLabel.setText(selectedDayText);
            formattedDateLabel.setText(selectedFormattedDate);
            // checks if the selected date has records
            SimpleDateFormat dateKeyFormat1 = new SimpleDateFormat("yyyy-MM-dd");

            String selectedDateKey = dateKeyFormat1.format(selectedDate);

            if (selectedDateKey.equals("2026-10-02")) {
                // OCTOBER 2: SHOW TRACKER CARD
                recordsContainer.removeAll();
                recordsContainer.add(trackerCard);
            } else {
                // OTHER DATES: SHOW EMPTY STATE
                recordsContainer.removeAll();
                recordsContainer.add(emptyMessage);
            }

            recordsContainer.revalidate();
        });

        calendarForm.add(BorderLayout.CENTER, content); //put the content inside the form and place it in the center

        calendarForm.show();
    }

    //determines if the selected date is today or other days
    private String getDayText(Date selected) {
        Date today = new Date();
        long difference = (today.getTime() - selected.getTime()) / (1000l * 60 * 60 * 24); // calculates the difference using milliseconds and converts it back to day

        if (difference == 0){
            return "Today";
        }
        if (difference == 1){
            return "Yesterday";
        }
        if (difference == -1){
            return "Tomorrow";
        }
        if (difference > 1){
            return difference + " days ago";
        }

        return "In " + (-difference) + " days";
    }

}
