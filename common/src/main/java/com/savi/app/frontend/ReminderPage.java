package com.savi.app.frontend;

import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.ui.spinner.Picker;
import com.savi.app.SAVI;
import com.savi.app.backend.Home;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.PersonalSavings;
import com.savi.app.backend.Profile;
import com.savi.app.backend.Reminder;
import com.savi.app.backend.Savings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// REMINDER PAGE (frontend)
// FLOW: open the page -> read the logged-in user's reminders from SAVI.reminders (the linked list in ReminderList) ->
// show them in two tabs (Upcomings / Completed), grouped by savings tracker (Group or Personal) ->
// tapping the check circle toggles a reminder between Upcoming and Completed -> "+ Add New Reminder" opens a form
// that adds a new one. Every change is saved to the TXT file by ReminderList, not by this page.
public class ReminderPage {

    private boolean showCompleted = false;   // which tab is open: false = Upcomings, true = Completed

    public void show() {
        Form reminderForm = new Form("", new BorderLayout());
        reminderForm.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        reminderForm.getContentPane().getAllStyles().setBgTransparency(255);

        Container t = reminderForm.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        Container body = new Container(BoxLayout.y());
        body.setScrollableY(true);
        body.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        body.getAllStyles().setBgTransparency(255);
        body.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        body.getAllStyles().setPadding(0.5f, 2f, 0, 0);

        // Segmented Tab Bar: [ Upcomings (0) | Completed (0) ]
        Container tabWrapper = new Container(new GridLayout(1, 2));
        Style tws = tabWrapper.getAllStyles();
        tws.setBgColor(SAVI.WHITE);
        tws.setBgTransparency(255);
        tws.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        tws.setPadding(0.6f, 0.6f, 0.6f, 0.6f);
        tws.setMarginUnit(Style.UNIT_TYPE_DIPS);
        tws.setMargin(0.5f, 1.2f, 2f, 2f);
        tws.setBorder(RoundRectBorder.create()
                .cornerRadius(4f)
                .shadowOpacity(35)
                .shadowBlur(8)
                .shadowSpread(1f)
                .shadowX(0.5f)
                .shadowY(0.3f)
                .shadowColor(SAVI.SLATE_GRAY));

        Container listContainer = new Container(BoxLayout.y());

        // refreshUI redraws the tab bar and the list. It runs when the page opens and again after
        // every change (switching tabs, completing/unchecking a reminder, adding a reminder).
        // "this.run()" inside it means the code calls itself to redraw.
        Runnable refreshUI = new Runnable() {
            @Override
            public void run() {
                String currentUser = activeUsername();

                tabWrapper.removeAll();
                // the numbers in the badges come from the linked list for the logged-in user
                int upCount = SAVI.reminders.countUpcoming(currentUser);
                int doneCount = SAVI.reminders.countCompleted(currentUser);

                tabWrapper.add(buildTab("Upcomings", upCount, !showCompleted, () -> {
                    showCompleted = false;
                    this.run();
                }));
                tabWrapper.add(buildTab("Completed", doneCount, showCompleted, () -> {
                    showCompleted = true;
                    this.run();
                }));

                listContainer.removeAll();
                // ask the backend for the logged-in user's reminders of the open tab
                ArrayList<Reminder> items = showCompleted
                        ? SAVI.reminders.getCompleted(currentUser)
                        : SAVI.reminders.getUpcoming(currentUser);

                if (items.isEmpty()) {
                    Label empty = SAVI.text(
                            showCompleted ? "No completed reminders yet." : "No upcoming reminders yet.",
                            SAVI.semiBold(2.1f), SAVI.SLATE_GRAY);
                    empty.getAllStyles().setAlignment(Component.CENTER);
                    empty.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
                    empty.getAllStyles().setPadding(4f, 4f, 2f, 2f);
                    listContainer.add(empty);
                } else {
                    // GROUPING: for each reminder, if its tracker has no header yet, add the header
                    // and then every reminder of that tracker under it
                    ArrayList<String> shownGroups = new ArrayList<>();
                    for (Reminder r : items) {
                        String grp = (r.getGroupName() == null || r.getGroupName().trim().isEmpty())
                                ? "General" : r.getGroupName().trim();
                        if (!shownGroups.contains(grp)) {
                            shownGroups.add(grp);
                            listContainer.add(buildGroupHeader(grp));
                            for (Reminder item : items) {
                                String itemGrp = (item.getGroupName() == null || item.getGroupName().trim().isEmpty())
                                        ? "General" : item.getGroupName().trim();
                                if (itemGrp.equals(grp)) {
                                    listContainer.add(buildReminderCard(item, this));
                                }
                            }
                        }
                    }
                }
                tabWrapper.revalidate();
                listContainer.revalidate();
            }
        };

        refreshUI.run();   // draw the page for the first time

        body.add(tabWrapper);
        body.add(listContainer);

        // Bottom "+ Add New Reminder" Pill Button
        Button addBtn = new Button("+  Add New Reminder");
        Style abs = addBtn.getAllStyles();
        abs.setFgColor(SAVI.WHITE);
        abs.setBgColor(SAVI.DARK_PURPLE);
        abs.setBgTransparency(255);
        abs.setFont(SAVI.semiBold(2.2f));
        abs.setAlignment(Component.CENTER);
        abs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        abs.setPadding(1.6f, 1.6f, 3f, 3f);
        abs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        abs.setMargin(0.5f, 1.5f, 4f, 4f);
        abs.setBorder(RoundRectBorder.create()
                .cornerRadius(4f)
                .shadowOpacity(50)
                .shadowBlur(10)
                .shadowSpread(1f)
                .shadowX(0.5f)
                .shadowY(0.3f)
                .shadowColor(SAVI.BRIGHT_VIOLET));

        // opens the form; refreshUI is passed in so the list redraws after a reminder is added
        addBtn.addActionListener(e -> showAddReminderDialog(refreshUI));

        Container bottomBar = new Container(new BorderLayout());
        bottomBar.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        bottomBar.getAllStyles().setBgTransparency(255);
        bottomBar.add(BorderLayout.CENTER, addBtn);

        reminderForm.add(BorderLayout.NORTH, SAVI.buildHeader("Reminders", () -> new HomePage().show()));
        reminderForm.add(BorderLayout.CENTER, body);
        reminderForm.add(BorderLayout.SOUTH, bottomBar);
        reminderForm.show();
    }

    // One tab of the tab bar. The active tab is purple, the other is white.
    // The invisible button "hit" makes the whole pill tappable.
    private Container buildTab(String label, int count, boolean active, Runnable onClick) {
        Label textLbl = SAVI.text(label, active ? SAVI.bold(2f) : SAVI.semiBold(2f),
                active ? SAVI.WHITE : SAVI.SLATE_GRAY);
        textLbl.getAllStyles().setMargin(0, 0, 0, 0);

        Label countBadge = SAVI.text("(" + count + ")", SAVI.bold(1.7f),
                active ? SAVI.WHITE : SAVI.SLATE_GRAY);
        Style cbs = countBadge.getAllStyles();
        cbs.setBgColor(active ? SAVI.BRIGHT_VIOLET : SAVI.POWDER_WHITE);
        cbs.setBgTransparency(255);
        cbs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        cbs.setPadding(0.3f, 0.3f, 1f, 1f);
        cbs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        cbs.setMargin(0, 0, 0.8f, 0);
        cbs.setBorder(RoundRectBorder.create().cornerRadius(2f));

        Button hit = new Button();
        hit.setUIID("Label");
        hit.addActionListener(e -> onClick.run());

        Container inner = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        inner.add(textLbl).add(countBadge).add(hit);

        Container pill = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        Style ps = pill.getAllStyles();
        ps.setBgColor(active ? SAVI.DARK_PURPLE : SAVI.WHITE);
        ps.setBgTransparency(255);
        ps.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ps.setPadding(1.2f, 1.2f, 1f, 1f);
        ps.setBorder(RoundRectBorder.create().cornerRadius(3.5f));
        pill.add(BorderLayout.CENTER, inner);
        pill.setLeadComponent(hit);   // tapping anywhere on the pill counts as tapping "hit"
        return pill;
    }

    // the "● [Group] December Vacation" bar that starts each group of reminders
    private Container buildGroupHeader(String groupTitle) {
        Label dot = SAVI.text("● ", SAVI.bold(1.8f), SAVI.DARK_PURPLE);
        Label name = SAVI.text(groupTitle, SAVI.semiBold(1.9f), SAVI.DARK_PURPLE);
        dot.getAllStyles().setMargin(0, 0, 0, 0);
        name.getAllStyles().setMargin(0, 0, 0, 0);

        Container bar = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        Style bs = bar.getAllStyles();
        bs.setBgColor(0xEFEAFE);
        bs.setBgTransparency(255);
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(1f, 1f, 1.8f, 1.8f);
        bs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        bs.setMargin(0.4f, 0.8f, 2f, 2f);
        bs.setBorder(RoundRectBorder.create()
                .cornerRadius(2.5f)
                .strokeColor(0xDDD3FB)
                .strokeOpacity(255)
                .stroke(0.2f, true));
        bar.add(dot).add(name);
        return bar;
    }

    // One reminder card: [check circle] [title + date/time] [status tag].
    // "onToggle" is refreshUI, so the page redraws after the reminder is checked or unchecked.
    private Container buildReminderCard(Reminder r, Runnable onToggle) {
        Container card = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        HomePage.boxStyles(card, SAVI.WHITE, 1.6f, 1.8f);
        card.getAllStyles().setMarginBottom(0.8f);

        // Left: Circular check icon button (tap to check or uncheck)
        Button checkBtn = new Button();
        checkBtn.setUIID("Label");
        Style cbs = checkBtn.getAllStyles();
        cbs.setFgColor(r.isCompleted() ? SAVI.WHITE : SAVI.DARK_PURPLE);
        cbs.setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(checkBtn,
                r.isCompleted() ? FontImage.MATERIAL_CHECK : FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED, 3.2f);
        checkBtn.addActionListener(e -> {
            SAVI.reminders.toggleCompleted(r);   // backend: find the node, toggle completed, save the file
            onToggle.run();                      // redraw the page
        });

        Container circle = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        int sz = Display.getInstance().convertToPixels(6.5f);
        circle.setPreferredW(sz);
        circle.setPreferredH(sz);
        Style cs = circle.getAllStyles();
        cs.setBgColor(r.isCompleted() ? SAVI.DARK_PURPLE : SAVI.PALE_PURPLE);
        cs.setBgTransparency(255);
        cs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        cs.setMargin(0, 0, 0, 1.5f);
        cs.setBorder(RoundRectBorder.create().cornerRadius(3.25f));
        circle.add(BorderLayout.CENTER, checkBtn);
        circle.setLeadComponent(checkBtn);

        // Center: Title + formatted date & time
        Label titleLbl = SAVI.text(r.getTitle(), SAVI.bold(2.1f), SAVI.DARK_KNIGHT);
        Label subLbl = SAVI.text(formatDate(r.getDate()) + " • " + r.getTime(),
                SAVI.regular(1.7f), SAVI.SLATE_GRAY);
        titleLbl.getAllStyles().setMargin(0, 0, 0, 0);
        subLbl.getAllStyles().setMargin(0, 0, 0, 0);

        Container infoBox = BoxLayout.encloseY(titleLbl, subLbl);

        // Right: Status pill ("Due P..." vs "Done")
        String tagText = r.isCompleted() ? "Done" : ("Due " + Home.pesoFormat(r.getAmount()));
        Label amountTag = SAVI.text(tagText, SAVI.bold(1.7f), r.isCompleted() ? SAVI.GREEN : SAVI.AMBER);
        Style ats = amountTag.getAllStyles();
        ats.setBgColor(r.isCompleted() ? SAVI.PALE_GREEN : SAVI.YELLOW_TAG);
        ats.setBgTransparency(255);
        ats.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ats.setPadding(0.7f, 0.7f, 1.4f, 1.4f);
        ats.setBorder(RoundRectBorder.create().cornerRadius(2.5f));

        card.add(BorderLayout.WEST, circle);
        card.add(BorderLayout.CENTER, infoBox);
        card.add(BorderLayout.EAST, amountTag);
        return card;
    }

    // The "New Reminder" form. When Save is tapped it validates the input, calls
    // SAVI.reminders.addReminder(...), closes the form, and runs onAdded (= refreshUI).
    private void showAddReminderDialog(Runnable onAdded) {
        // Collect both Group and Personal savings trackers of the logged-in user
        String currentUser = activeUsername();
        ArrayList<Savings> targets = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (GroupSavings g : SAVI.groupList.getForUser(currentUser)) {
            targets.add(g);
            labels.add("[Group] " + g.getGroupName());
        }
        for (PersonalSavings p : SAVI.personalList.getForUser(currentUser)) {
            targets.add(p);
            labels.add("[Personal] " + p.getSavingsName());
        }

        if (targets.isEmpty()) {
            Dialog.show("No Savings Trackers",
                    "Create a Group Savings or Personal Savings tracker first before adding a reminder.",
                    "OK", null);
            return;
        }

        Dialog dlg = new Dialog("New Reminder", new BorderLayout());
        dlg.getContentPane().getAllStyles().setBgColor(SAVI.WHITE);
        dlg.getContentPane().getAllStyles().setBgTransparency(255);

        Container fields = new Container(BoxLayout.y());
        fields.setScrollableY(true);
        fields.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        fields.getAllStyles().setPadding(1f, 1f, 1.5f, 1.5f);

        Label rmd = SAVI.text("TITLE", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label sv = SAVI.text("SAVINGS TRACKER", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label dt = SAVI.text("DATE", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label tm = SAVI.text("TIME", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label am = SAVI.text("AMOUNT (P)", SAVI.bold(1.6f), SAVI.DEEP_SLATE);

        TextField titleField = new TextField("", "Enter reminder title");
        TextField amountField = new TextField("", "0.00");
        amountField.setConstraint(TextArea.DECIMAL);

        // Tracker selector: lists every Group and Personal savings tracker of the user
        Picker trackerPicker = new Picker();
        trackerPicker.setType(Display.PICKER_TYPE_STRINGS);
        trackerPicker.setStrings(labels.toArray(new String[0]));
        trackerPicker.setSelectedStringIndex(0);

        // Date & Time pickers defaulting to current date/time
        Picker datePicker = new Picker();
        datePicker.setType(Display.PICKER_TYPE_DATE);
        datePicker.setDate(new Date());

        Picker timePicker = new Picker();
        timePicker.setType(Display.PICKER_TYPE_TIME);

        Label[] lb = {rmd, sv, am, dt, tm};

        for (Label l : lb) {
            l.getAllStyles().setPadding(1, 1f, 2, 0);
        }

        TextField[] txt = {amountField, titleField};

        for (TextField tf : txt) {
            tf.getAllStyles().setFont(SAVI.regular(2f));
            tf.getHintLabel().getAllStyles().setFont(SAVI.italic(2f));
            tf.getHintLabel().getAllStyles().setFgColor(SAVI.SLATE_GRAY);
            tf.getHintLabel().getAllStyles().setPadding(0, 0, 2, 2);
        }

        Picker[] pk = {trackerPicker, datePicker, timePicker};

        for (Picker p : pk) {
            p.getAllStyles().setFont(SAVI.regular(2f));
            p.getAllStyles().setFgColor(SAVI.DEEP_SLATE);
            p.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
            p.getAllStyles().setMargin(0.4f, 1f, 2, 2);
        }

        fields.add(rmd).add(titleField);
        fields.add(sv).add(trackerPicker);
        fields.add(dt).add(datePicker);
        fields.add(tm).add(timePicker);
        fields.add(am).add(amountField);

        Button cancel = new Button("Cancel");
        cancel.getAllStyles().setFgColor(SAVI.DEEP_SLATE);
        cancel.getAllStyles().setFont(SAVI.regular(2.5f));
        cancel.getAllStyles().setBgTransparency(0);
        cancel.getAllStyles().setBorder(Border.createEmpty());
        cancel.getAllStyles().setPadding(3f, 1f, 2f, 1f);
        cancel.addActionListener(e -> dlg.dispose());

        Button save = new Button("Save");
        save.getAllStyles().setFgColor(SAVI.WHITE);
        save.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        save.getAllStyles().setBgTransparency(255);
        save.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        save.getAllStyles().setPadding(1f, 1f, 2f, 2f);
        save.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2.5f));
        save.addActionListener(e -> {
            String t = titleField.getText().trim();

            // pick the tracker label from the dropdown
            int idx = trackerPicker.getSelectedStringIndex();
            String g = (idx >= 0 && idx < labels.size()) ? labels.get(idx) : "General";

            // read the amount; a wrong or empty value stays 0 and is rejected below
            double amt = 0;
            try {
                amt = Double.parseDouble(amountField.getText().trim());
            } catch (Exception ignored) { }

            // VALIDATION: a title and an amount above 0 are required
            if (t.isEmpty() || amt <= 0) {
                Dialog.show("Missing Info", "Please enter a title and a valid amount.", "OK", null);
                return;
            }

            LocalDate pickedDate = toLocalDate(datePicker.getDate());
            String pickedTime = formatTime(timePicker.getTime());

            // backend: link a new node at the end of the list with the logged-in user as owner
            SAVI.reminders.addReminder(currentUser, t, g, pickedDate, pickedTime, amt);
            dlg.dispose();
            onAdded.run();   // redraw the page so the new reminder shows
        });

        Container actions = new Container(new FlowLayout(Component.RIGHT, Component.CENTER));
        actions.add(cancel).add(save);

        dlg.add(BorderLayout.CENTER, fields);
        dlg.add(BorderLayout.SOUTH, actions);
        dlg.show();
    }

    private static String activeUsername() {
        if (SAVI.user != null && SAVI.user.getCurrentName() != null
                && !SAVI.user.getCurrentName().trim().isEmpty()) {
            return SAVI.user.getCurrentName().trim();
        }
        return Profile.getInstance().getMemberName();
    }

    // the date picker gives a java.util.Date; our reminders store a LocalDate
    private LocalDate toLocalDate(Date d) {
        if (d == null) return LocalDate.now();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return LocalDate.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,    // Calendar months start at 0, LocalDate months start at 1
                cal.get(Calendar.DAY_OF_MONTH)
        );
    }

    // the time picker gives minutes from midnight (e.g. 810); this turns it into "01:30 PM"
    private String formatTime(int minutesFromMidnight) {
        if (minutesFromMidnight < 0) {
            return new SimpleDateFormat("hh:mm a").format(new Date());
        }
        int h24 = (minutesFromMidnight / 60) % 24;
        int min = minutesFromMidnight % 60;
        String ampm = h24 >= 12 ? "PM" : "AM";
        int h12 = h24 % 12;
        if (h12 == 0) h12 = 12;   // 0 o'clock and 12 o'clock are both shown as 12
        return (h12 < 10 ? "0" : "") + h12 + ":" + (min < 10 ? "0" : "") + min + " " + ampm;
    }

    // "2026-11-20" -> "20 Nov 2026" (the date is cut apart as text, then the month name is looked up)
    private String formatDate(LocalDate d) {
        if (d == null) return "";
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String s = d.toString(); // yyyy-MM-dd
        int year = Integer.parseInt(s.substring(0, 4));
        int month = Integer.parseInt(s.substring(5, 7));
        int day = Integer.parseInt(s.substring(8, 10));
        return (day < 10 ? "0" : "") + day + " " + months[month - 1] + " " + year;
    }
}