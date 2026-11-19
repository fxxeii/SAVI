package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.codename1.ui.spinner.Picker;
import com.savi.app.SAVI;
import com.savi.app.backend.Savings;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.PersonalSavings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QuickAddMenu {

    public static void show() {
        Dialog sheet = new Dialog(new BorderLayout());

        // Custom Header
        Container header = new Container(new BorderLayout());

        header.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        header.getAllStyles().setBgTransparency(255);
        header.getAllStyles().setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(SAVI.BRIGHT_VIOLET));
        header.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        header.getAllStyles().setPadding(2f, 2f, 2.5f, 2.5f);

        Label headerTitle = new Label("What would you like to do?");
        headerTitle.getAllStyles().setFgColor(SAVI.WHITE);
        headerTitle.getAllStyles().setFont(SAVI.bold(2.5f));
        headerTitle.setAlignment(Component.CENTER);
        headerTitle.getAllStyles().setMargin(0, 0, 0, 0);
        headerTitle.getAllStyles().setPadding(0, 0, 0, 0);

        header.add(BorderLayout.CENTER, headerTitle);

        Container options = new Container(BoxLayout.y());
        options.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        options.getAllStyles().setPadding(1f, 1.5f, 1.5f, 1.5f);

        options.add(optionRow(FontImage.MATERIAL_ADD_CIRCLE, "Add a Contribution", "Record a deposit", () -> {sheet.dispose();showAddContributionDialog();}));
        options.add(optionRow(FontImage.MATERIAL_GROUP_ADD, "Create Group Savings", "Start a shared goal", () -> {sheet.dispose();showCreateGroupDialog();}));
        options.add(optionRow(FontImage.MATERIAL_SAVINGS, "Create Personal Savings", "Set up an individual savings", () -> {sheet.dispose();showCreatePersonalDialog();}));

        Button cancel = new Button("Cancel");
        cancel.getAllStyles().setFgColor(SAVI.SLATE_GRAY);
        cancel.getAllStyles().setBgTransparency(0);
        cancel.getAllStyles().setBorder(Border.createEmpty());
        cancel.getAllStyles().setFont(SAVI.semiBold(1.7f));
        cancel.addActionListener(e -> sheet.dispose());

        sheet.add(BorderLayout.NORTH, header);
        sheet.add(BorderLayout.CENTER, options);
        sheet.add(BorderLayout.SOUTH, FlowLayout.encloseCenter(cancel));
        sheet.show();
    }


    private static Container optionRow(char icon, String title, String subtitle, Runnable onClick) {

        // ICON
        Label iconLbl = new Label();
        Style is = iconLbl.getAllStyles();

        is.setFgColor(SAVI.WHITE);
        is.setBgColor(SAVI.DARK_PURPLE);
        is.setBgTransparency(255);

        is.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        is.setPadding(1.3f, 1.3f, 1.3f, 1.3f);

        is.setMarginUnit(Style.UNIT_TYPE_DIPS);
        is.setMargin(0, 0, 0, 1.5f);

        is.setBorder(RoundRectBorder.create().cornerRadius(2.5f));

        iconLbl.setMaterialIcon(icon, 3.2f);


        // TITLE
        Button titleBtn = new Button(title);
        titleBtn.setUIID("Label");

        titleBtn.setAlignment(Component.LEFT);

        titleBtn.getAllStyles().setFgColor(SAVI.DARK_KNIGHT);
        titleBtn.getAllStyles().setFont(SAVI.bold(1.8f));

        titleBtn.getAllStyles().setMargin(0, 0, 0, 0);
        titleBtn.getAllStyles().setPadding(0, 0, 0, 0);

        // SUBTITLE
        Label subLbl = SAVI.text(subtitle, SAVI.regular(1.4f), SAVI.SLATE_GRAY);

        subLbl.setAlignment(Component.LEFT);

        subLbl.getAllStyles().setMargin(0, 0, 0, 0);
        subLbl.getAllStyles().setPadding(0, 0, 0, 0);

        // TEXT CONTAINER
        Container textContainer = new Container(BoxLayout.y());

        textContainer.add(titleBtn);
        textContainer.add(subLbl);


        // OPTION ROW
        Container row = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));

        Style rs = row.getAllStyles();

        rs.setBgColor(SAVI.PALE_PURPLE);
        rs.setBgTransparency(255);

        rs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        rs.setPadding(1.4f, 1.4f, 1.5f, 1.5f);

        rs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        rs.setMargin(0.5f, 0.5f, 0, 0);

        rs.setBorder(RoundRectBorder.create().cornerRadius(3f));

        row.add(BorderLayout.WEST, iconLbl);
        row.add(BorderLayout.CENTER, textContainer);

        row.setLeadComponent(titleBtn);

        titleBtn.addActionListener(e -> onClick.run());

        return row;
    }

    // ---------------------------------------------------------------- 1. ADD CONTRIBUTION
    public static void showAddContributionDialog() {
        String currentUser = SAVI.user.getCurrentName();
        ArrayList<Savings> targets = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (GroupSavings g : SAVI.groupList.getGroups()) {
            targets.add(g);
            labels.add("[Group] " + g.getGroupName());
        }
        for (PersonalSavings p : SAVI.personalList.getForOwner(currentUser)) {
            targets.add(p);
            labels.add("[Personal] " + p.getSavingsName());
        }

        if (targets.isEmpty()) {
            Dialog.show("No Savings Goals",
                    "Create a Group Savings or Personal Savings goal first before adding a contribution.",
                    "OK", null);
            return;
        }

        Dialog dlg = new Dialog("Add Contribution", new BorderLayout());
        dlg.getContentPane().getAllStyles().setBgColor(SAVI.WHITE);
        dlg.getContentPane().getAllStyles().setBgTransparency(255);
        dlg.getAllStyles().setFont(SAVI.bold(4f));

        Container fields = new Container(BoxLayout.y());
        fields.setScrollableY(true);
        fields.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        fields.getAllStyles().setPadding(1f, 1f, 1.5f, 1.5f);

        String[] targetStrings = labels.toArray(new String[0]);
        Picker goalPicker = styledPicker();
        goalPicker.setType(Display.PICKER_TYPE_STRINGS);
        goalPicker.setStrings(targetStrings);
        goalPicker.setSelectedStringIndex(0);

        Label savings = SAVI.text("SAVINGS GOAL", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label memberTitle = SAVI.text("MEMBER", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label amount = SAVI.text("AMOUNT (P)", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label dt = SAVI.text("DATE", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label nt = SAVI.text("NOTE", SAVI.bold(1.6f), SAVI.DEEP_SLATE);

        Picker memberPicker = styledPicker();
        memberPicker.setType(Display.PICKER_TYPE_STRINGS);

        Runnable syncMembers = () -> {
            int idx = goalPicker.getSelectedStringIndex();
            if (idx < 0 || idx >= targets.size()) return;
            Savings selected = targets.get(idx);

            if (selected instanceof GroupSavings) {
                GroupSavings gs = (GroupSavings) selected;
                ArrayList<String> mems = gs.getMembers();
                if (mems.isEmpty() && currentUser != null) {
                    mems.add(currentUser);
                }
                memberPicker.setStrings(mems.toArray(new String[0]));
                int defaultIdx = 0;
                if (currentUser != null) {
                    for (int i = 0; i < mems.size(); i++) {
                        if (mems.get(i).equalsIgnoreCase(currentUser)) {
                            defaultIdx = i;
                            break;
                        }
                    }
                }
                memberPicker.setSelectedStringIndex(defaultIdx);
                memberTitle.setHidden(false);
                memberPicker.setHidden(false);
            } else {
                memberTitle.setHidden(true);
                memberPicker.setHidden(true);
            }
            fields.revalidate();
            dlg.revalidate();
        };

        syncMembers.run();
        goalPicker.addActionListener(e -> syncMembers.run());

        TextField amountField = styledField("0.00");
        amountField.setConstraint(TextArea.DECIMAL);

        Picker datePicker = styledPicker();
        datePicker.setType(Display.PICKER_TYPE_DATE);
        datePicker.setDate(new Date());

        TextField noteField = styledField("Optional note (e.g. Daily deposit)");

        Label[] lb = {savings, memberTitle, amount, dt, nt};

        for (Label l : lb) {
            l.getAllStyles().setPadding(1,1.5f,0,0);
        }

        TextField[] txt = {amountField, noteField};

        for (TextField tf : txt) {
            tf.getAllStyles().setFont(SAVI.regular(2.5f));
            tf.getHintLabel().getAllStyles().setFont(SAVI.italic(2f));
            tf.getHintLabel().getAllStyles().setFgColor(SAVI.SLATE_GRAY);
            tf.getHintLabel().getAllStyles().setPadding(0,0,2,2);
        }

        fields.add(savings).add(goalPicker);
        fields.add(memberTitle).add(memberPicker);
        fields.add(amount).add(amountField);
        fields.add(dt).add(datePicker);
        fields.add(nt).add(noteField);

        dlg.add(BorderLayout.CENTER, fields);
        dlg.add(BorderLayout.SOUTH, dialogButtons(dlg, () -> {
            double amt = parsePositiveAmount(amountField.getText());
            if (amt <= 0) {
                Dialog.show("Invalid Amount", "Please enter an amount greater than 0.", "OK", null);
                return;
            }

            int idx = goalPicker.getSelectedStringIndex();
            Savings selected = targets.get(idx);
            LocalDate date = toLocalDate(datePicker.getDate());
            String note = noteField.getText().trim();

            if (selected instanceof GroupSavings) {
                GroupSavings gs = (GroupSavings) selected;
                String member = memberPicker.getSelectedString();
                if (member == null || member.trim().isEmpty()) {
                    Dialog.show("Missing Member", "Please select a member.", "OK", null);
                    return;
                }
                SAVI.groupList.addContribution(gs.getGroupName(), member, amt, date, note);
            } else if (selected instanceof PersonalSavings) {
                PersonalSavings ps = (PersonalSavings) selected;
                SAVI.personalList.addContribution(ps.getSavingsName(), currentUser, amt, date, note);
            }

            dlg.dispose();
            new HomePage().show();
        }));
        dlg.show();
    }

    // ---------------------------------------------------------------- 2. CREATE GROUP SAVINGS
    public static void showCreateGroupDialog() {
        Dialog dlg = new Dialog("Create Group Savings", new BorderLayout());
        dlg.getContentPane().getAllStyles().setBgColor(SAVI.WHITE);
        dlg.getContentPane().getAllStyles().setBgTransparency(255);
        dlg.getAllStyles().setFont(SAVI.bold(4f));

        Container fields = new Container(BoxLayout.y());
        fields.setScrollableY(true);
        fields.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        fields.getAllStyles().setPadding(1f, 1f, 1.5f, 1.5f);

        Label gp = SAVI.text("GROUP NAME", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label sgt = SAVI.text("SAVING GOAL TITLE", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label tta = SAVI.text("TOTAL TARGET AMOUNT (P)", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label exp = SAVI.text("EXPECTED PER MEMBER (P)", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label mm = SAVI.text("OTHER MEMBERS (comma-separated)", SAVI.bold(1.6f), SAVI.DEEP_SLATE);

        TextField groupNameField = styledField("e.g. Vacation");
        TextField goalNameField = styledField("e.g. Travel & Hotel Fund");
        TextField targetField = styledField("P 0.00");
        targetField.setConstraint(TextArea.DECIMAL);

        TextField expectedField = styledField("P 0.00");
        expectedField.setConstraint(TextArea.DECIMAL);

        TextField membersField = styledField("person1, person2, person3, ...");

        Label[] lb = {gp, sgt, tta, exp, mm};

        for (Label l : lb) {
            l.getAllStyles().setPadding(1,1.5f,0,0);
        }

        TextField[] txt = {groupNameField, goalNameField, targetField, expectedField, membersField};

        for (TextField tf : txt) {
            tf.getAllStyles().setFont(SAVI.regular(2.5f));
            tf.getHintLabel().getAllStyles().setFont(SAVI.italic(2f));
            tf.getHintLabel().getAllStyles().setFgColor(SAVI.SLATE_GRAY);
            tf.getHintLabel().getAllStyles().setPadding(0,0,2,2);
        }

        fields.add(gp).add(groupNameField);
        fields.add(sgt).add(goalNameField);
        fields.add(tta).add(targetField);
        fields.add(exp).add(expectedField);
        fields.add(mm).add(membersField);

        dlg.add(BorderLayout.CENTER, fields);
        dlg.add(BorderLayout.SOUTH, dialogButtons(dlg, () -> {
            String grpName = groupNameField.getText().trim();
            String savName = goalNameField.getText().trim();
            double target = parsePositiveAmount(targetField.getText());
            double expected = parseNonNegativeAmount(expectedField.getText());

            if (grpName.isEmpty() || savName.isEmpty() || target <= 0 || expected < 0) {
                Dialog.show("Missing Info",
                        "Please fill in Group Name, Savings Title, a Target > 0, and a valid Expected amount.",
                        "OK", null);
                return;
            }

            String res = SAVI.groupList.addGroup(savName, target, grpName, expected);
            if (!"success".equals(res)) {
                Dialog.show("Error", res, "OK", null);
                return;
            }

            // Automatically include the logged-in user so the group appears on their Home & Transfer pages
            String me = SAVI.user.getCurrentName();
            if (me != null && !me.trim().isEmpty()) {
                SAVI.groupList.addMember(grpName, me.trim());
            }

            // Add comma-separated members
            String rawMembers = membersField.getText().trim();
            if (!rawMembers.isEmpty()) {
                int start = 0;
                while (start <= rawMembers.length()) {
                    int comma = rawMembers.indexOf(',', start);
                    String part = (comma == -1)
                            ? rawMembers.substring(start).trim()
                            : rawMembers.substring(start, comma).trim();
                    if (!part.isEmpty()) {
                        SAVI.groupList.addMember(grpName, part);
                    }
                    if (comma == -1) break;
                    start = comma + 1;
                }
            }

            dlg.dispose();
            new HomePage().show();
        }));
        dlg.show();
    }

    // ---------------------------------------------------------------- 3. CREATE PERSONAL SAVINGS
    public static void showCreatePersonalDialog() {
        Dialog dlg = new Dialog("Create Personal Savings", new BorderLayout());
        dlg.getContentPane().getAllStyles().setBgColor(SAVI.WHITE);
        dlg.getContentPane().getAllStyles().setBgTransparency(255);
        dlg.getAllStyles().setFont(SAVI.bold(4f));

        Container fields = new Container(BoxLayout.y());
        fields.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        fields.getAllStyles().setPadding(1f, 1f, 1.5f, 1.5f);

        Label gn = SAVI.text("GOAL NAME", SAVI.bold(1.6f), SAVI.DEEP_SLATE);
        Label tta = SAVI.text("TOTAL TARGET AMOUNT (P)", SAVI.bold(1.6f), SAVI.DEEP_SLATE);

        TextField nameField = styledField("e.g. Emergency Fund");
        TextField targetField = styledField("P 0.00");
        targetField.setConstraint(TextArea.DECIMAL);

        Label[] lb = {gn, tta};

        for (Label l : lb) {
            l.getAllStyles().setPadding(1,1.5f,0,0);
        }

        TextField[] txt = {nameField, targetField};

        for (TextField tf : txt) {
            tf.getAllStyles().setFont(SAVI.regular(2.5f));
            tf.getHintLabel().getAllStyles().setFont(SAVI.italic(2f));
            tf.getHintLabel().getAllStyles().setFgColor(SAVI.SLATE_GRAY);
            tf.getHintLabel().getAllStyles().setPadding(0,0,2,2);
        }

        fields.add(gn).add(nameField);
        fields.add(tta).add(targetField);

        dlg.add(BorderLayout.CENTER, fields);
        dlg.add(BorderLayout.SOUTH, dialogButtons(dlg, () -> {
            String goalName = nameField.getText().trim();
            double target = parsePositiveAmount(targetField.getText());
            String owner = SAVI.user.getCurrentName();

            String res = SAVI.personalList.addPersonalSavings(goalName, target, owner);
            if (!"success".equals(res)) {
                Dialog.show("Error", res, "OK", null);
                return;
            }

            dlg.dispose();
            new HomePage().show();
        }));
        dlg.show();
    }

    // ---------------------------------------------------------------- UI & PARSING HELPERS
    private static TextField styledField(String hint) {
        TextField tf = new TextField("", hint);
        tf.getAllStyles().setFont(SAVI.regular(2f));
        tf.getAllStyles().setFgColor(SAVI.DARK_KNIGHT);
        tf.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        tf.getAllStyles().setMargin(0.4f, 1f, 0, 0);
        return tf;
    }

    private static Picker styledPicker() {
        Picker p = new Picker();
        p.getAllStyles().setFont(SAVI.regular(2f));
        p.getAllStyles().setFgColor(SAVI.DARK_KNIGHT);
        p.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        p.getAllStyles().setMargin(0.4f, 1f, 0, 0);
        return p;
    }

    private static Container dialogButtons(Dialog dlg, Runnable onSave) {
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
        save.getAllStyles().setFont(SAVI.regular(2.7f));
        save.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        save.getAllStyles().setPadding(0f, 0f, 2f, 2f);
        save.getAllStyles().setBorder(RoundRectBorder.create().cornerRadius(2.5f));
        save.addActionListener(e -> onSave.run());

        Container actions = new Container(new FlowLayout(Component.RIGHT, Component.CENTER));
        actions.add(cancel).add(save);
        return actions;
    }

    private static double parsePositiveAmount(String text) {
        try {
            double v = Double.parseDouble(text.trim());
            return (v > 0 && !Double.isNaN(v) && !Double.isInfinite(v)) ? v : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private static double parseNonNegativeAmount(String text) {
        try {
            if (text == null || text.trim().isEmpty()) return 0;
            double v = Double.parseDouble(text.trim());
            return (v >= 0 && !Double.isNaN(v) && !Double.isInfinite(v)) ? v : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private static LocalDate toLocalDate(Date d) {
        if (d == null) return LocalDate.now();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return LocalDate.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
        );
    }
}