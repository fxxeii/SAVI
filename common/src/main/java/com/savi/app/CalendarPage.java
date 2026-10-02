package com.savi.app;

import static com.codename1.ui.CN.*;
import com.codename1.ui.*; // for form, button, dialog, front image
import com.codename1.ui.layouts.*;
import com.codename1.io.*;
import com.codename1.ui.plaf.*;
import java.util.Date;
import com.codename1.l10n.SimpleDateFormat;

public class CalendarPage {

    public void show() {

        Form calendarForm = new Form(new BorderLayout()); // ginagawa yung screen

        // for back button
        calendarForm.getToolbar().addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK, e -> {
            // dito ilalagay yung code para bumalik sa previous screen
            // should look like: previousForm.showBack();
        });

        Container content = new Container(BoxLayout.y()); // creates a container para paglagyan ng iba pang content sa center. main content

        Calendar cld = new Calendar(); // creates a calendar

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

        // date bar
        Container dateBar = new Container(new BorderLayout());
        dateBar.add(BorderLayout.WEST, dayTextLabel);
        dateBar.add(BorderLayout.EAST, formattedDateLabel);

        // runs when a day is selected
        cld.addDayActionListener((e) -> {
            Date selectedDate = cld.getDate(); // gets the date currently selected by the user
            String selectedDayText = getDayText(selectedDate); // get the current date
            String selectedFormattedDate = formatter.format(selectedDate); //get the formatted date
            dayTextLabel.setText(selectedDayText);
            formattedDateLabel.setText(selectedFormattedDate);
        });

        content.add(cld); // put the calendar inside the content
        content.add(dateBar); // adds date bar inside the content


        Container trackerCard = new Container(BoxLayout.y());

        // HEADER: group name on the left, status on the right
        Container trackerHeader = new Container(new BorderLayout());

        Container trackerInfo = new Container(BoxLayout.y());

        Label groupName = new Label("December Vacation");
        Label status = new Label("Saved");

        trackerHeader.add(BorderLayout.WEST, groupName);
        trackerHeader.add(BorderLayout.EAST, status);

        // AMOUNT SAVED
        Label amountSaved = new Label("Amount Saved: ₱30.00");

        // CONTRIBUTIONS BUTTON
        Button viewContributions = new Button("View contributions (3)");
        viewContributions.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);

        // CONTRIBUTIONS LIST
        Container contributionsList = new Container(BoxLayout.y());

        Container row1 = new Container(new BorderLayout());
        row1.add(BorderLayout.WEST, new Label("Airesse"));
        row1.add(BorderLayout.EAST, new Label("+₱10.00"));

        Container row2 = new Container(new BorderLayout());
        row2.add(BorderLayout.WEST, new Label("Faye"));
        row2.add(BorderLayout.EAST, new Label("+₱10.00"));

        Container row3 = new Container(new BorderLayout());
        row3.add(BorderLayout.WEST, new Label("Sam"));
        row3.add(BorderLayout.EAST, new Label("+₱10.00"));

        Container row4 = new Container(new BorderLayout());
        row4.add(BorderLayout.WEST, new Label("Shy"));
        row4.add(BorderLayout.EAST, new Label("+₱10.00"));

        contributionsList.add(row1);
        contributionsList.add(row2);
        contributionsList.add(row3);
        contributionsList.add(row4);

        contributionsList.setHidden(true);

        // EXPAND / COLLAPSE
        viewContributions.addActionListener(e -> {
            boolean isHidden = contributionsList.isHidden();

            contributionsList.setHidden(!isHidden);

            if (isHidden) {
                viewContributions.setText("Hide contributions (3)");
                viewContributions.setMaterialIcon(
                        FontImage.MATERIAL_ARROW_DROP_UP
                );
            } else {
                viewContributions.setText("View contributions (3)");
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

        content.add(trackerCard);

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

    /*
    @Override
    public void runApp() {
        Form hi = new Form("SAVI", BoxLayout.y()); // form is a screen

        Button helloButton = new Button("Get Started"); // button is, well, button

        hi.add(helloButton); // putting the button inside the screen

        helloButton.addActionListener(e -> hello()); // when the user interacts with this button, call hello(). the e represents the event

        hi.getToolbar().addMaterialCommandToSideMenu("MENU", FontImage.MATERIAL_CHECK, 4, e -> hello()); // addMaterialCommandToSideMenu means add something to the side menu

        hi.show(); // display the fom (screen)
    }

    private void hello() {
    Dialog.show("Hello, User", "Welcome to SAVI", "Thanks!", "Exit"); // dialog is for popup
    }
    */

    /*
    Button dateButton = new Button("September 2026"); // gagawa ng label for date
    calendarForm.add(BorderLayout.NORTH, dateButton); // ilalagay sa screen tapos i-p-place sa top

    Button backButton = new Button("<-- Back");
    calendarForm.add(BorderLayout.NORTH, backButton);
    */

    /*
    Button backButton = new Button("<-- Back"); // gagawa ng button for back
    calendarForm.add(BorderLayout.NORTH, backButton); // place sa top
    */
