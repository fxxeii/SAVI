package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Graphics;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.PersonalSavings;
import com.savi.app.backend.Profile;
import com.savi.app.backend.UserProfile;

import java.util.List;

public class ProfilePage implements Profile.DataChangeListener {

    private final Profile backend = Profile.getInstance();
    private Container mainContents;

    private static final float GREETING_SIZE = 4.2f;   // size of "Hello, Saver!" AND the username

    static int mm(float v) {
        return Display.getInstance().convertToPixels(v);
    }

    static void setMargins(Component c, float t, float b, float l, float r) {
        Style s = c.getAllStyles();
        s.setMarginUnit(Style.UNIT_TYPE_DIPS);
        s.setMargin(t, b, l, r);
    }

    static void setPadding(Component c, float t, float b, float l, float r) {
        Style s = c.getAllStyles();
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(t, b, l, r);
    }

    static void paintStatusBar(Form f, int color) {
        Container t = f.getTitleArea();
        t.getAllStyles().setBgColor(color);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(color);
            s.setBgTransparency(255);
        }
    }

    static void cardStyles(Container cont, int color, float radius, float paddingTB, float paddingLR) {
        Style s = cont.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(radius).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(SAVI.BRIGHT_VIOLET));
        setMargins(cont, 0.5f, 0.5f, 2, 2);
        setPadding(cont, paddingTB, paddingTB, paddingLR, paddingLR);
    }

    static Container divider(int color) {
        Container line = new Container();
        line.setPreferredH(Math.max(1, mm(0.2f)));
        Style s = line.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        setMargins(line, 1.5f, 1.5f, 0, 0);
        return line;
    }

    static class Rounded extends Container {
        private final int fill, fillAlpha;
        private final float radiusMM;
        private int strokeColor, strokeAlpha, strokePx;

        Rounded(Layout layout, int fill, int fillAlpha, float radiusMM) {
            super(layout);
            this.fill = fill;
            this.fillAlpha = fillAlpha;
            this.radiusMM = radiusMM;
            getAllStyles().setBgTransparency(0);
        }

        Rounded stroke(int color, int alpha, int px) {
            strokeColor = color;
            strokeAlpha = alpha;
            strokePx = px;
            return this;
        }

        Rounded fixed(float sizeMM) {
            setPreferredW(mm(sizeMM));
            setPreferredH(mm(sizeMM));
            return this;
        }

        @Override
        public void paintBackground(Graphics g) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            int arc = radiusMM <= 0 ? Math.min(w, h) : Math.min(Math.min(w, h), mm(radiusMM * 2));
            g.setAntiAliased(true);
            if (fillAlpha > 0) {
                g.setAlpha(fillAlpha);
                g.setColor(fill);
                g.fillRoundRect(x, y, w, h, arc, arc);
            }
            if (strokePx > 0 && strokeAlpha > 0) {
                g.setAlpha(strokeAlpha);
                g.setColor(strokeColor);
                for (int i = 0; i < strokePx; i++) {
                    g.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, arc, arc);
                }
            }
            g.setAlpha(255);
        }
    }

    static Container pill(Label text, int bgColor) {
        setPadding(text, 0, 0, 0, 0);
        setMargins(text, 0, 0, 0, 0);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), bgColor, 255, 0);
        setPadding(p, 0.5f, 0.5f, 1.6f, 1.6f);
        p.add(text);
        return p;
    }

    static Container squareIcon(char icon, int bg, int fg) {
        Label l = new Label();
        l.getAllStyles().setFgColor(fg);
        FontImage.setMaterialIcon(l, icon, 2.6f);

        Rounded c = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), bg, 255, 1.6f).fixed(5.4f);
        c.add(BorderLayout.CENTER, l);
        return c;
    }

    static Image gradientImage(int w, int h, int c1, int c2, int radiusPx) {
        Image img = Image.createImage(w, h, 0);
        Graphics g = img.getGraphics();
        g.fillLinearGradient(c1, c2, 0, 0, w, h, false);

        Image mask = Image.createImage(w, h, 0xff000000);
        Graphics mg = mask.getGraphics();
        mg.setAntiAliased(true);
        mg.setColor(0xffffff);
        mg.fillRoundRect(0, 0, w, h, radiusPx * 2, radiusPx * 2);
        mg.fillRect(0, 0, w, h / 2);
        return img.applyMask(mask.createMask());
    }

    static class GradientHeader extends Container {
        private final float radiusMM;
        private Image cache;

        GradientHeader(Layout layout, float radiusMM) {
            super(layout);
            this.radiusMM = radiusMM;
            getAllStyles().setBgTransparency(0);
        }

        @Override
        public void paintBackground(Graphics g) {
            int w = getWidth(), h = getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            if (cache == null || cache.getWidth() != w || cache.getHeight() != h) {
                cache = gradientImage(w, h, SAVI.DARK_PURPLE, SAVI.BRIGHT_VIOLET, mm(radiusMM));
            }
            g.drawImage(cache, getX(), getY());
        }
    }

    Container greetingBanner(UserProfile profile) {
        Rounded badge = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), SAVI.WHITE, 35, 3.5f)
                .stroke(SAVI.WHITE, 90, 1);
        setPadding(badge, 1.6f, 1.6f, 3.5f, 3.5f);
        setMargins(badge, 0.5f, 0.5f, 0, 0);

        Button greetingBtn = new Button("Hello, " + profile.getUsername() + "!");
        greetingBtn.setUIID("Label");
        greetingBtn.getAllStyles().setFont(SAVI.bold(GREETING_SIZE));
        greetingBtn.getAllStyles().setFgColor(SAVI.WHITE);
        setPadding(greetingBtn, 0, 0, 0, 0);
        setMargins(greetingBtn, 0, 0, 0, 0);
        greetingBtn.addActionListener(e -> showEditProfileDialog());

        Label editIcon = new Label();
        editIcon.getAllStyles().setFgColor(SAVI.WHITE);
        setPadding(editIcon, 0, 0.4f, 0.8f, 0);
        setMargins(editIcon, 0, 0, 0, 0);
        FontImage.setMaterialIcon(editIcon, FontImage.MATERIAL_EDIT, GREETING_SIZE * 0.65f);

        Container row = BoxLayout.encloseX(
                BoxLayout.encloseYCenter(greetingBtn),
                BoxLayout.encloseYCenter(editIcon)
        );

        badge.add(row);
        badge.setLeadComponent(greetingBtn);

        return FlowLayout.encloseCenter(badge);
    }

    Container profileHeader(float bottomPaddingMM, UserProfile profile) {
        Container header = new GradientHeader(BoxLayout.y(), 6f);
        setPadding(header, 1f, bottomPaddingMM, 2f, 2f);
        setMargins(header, 0, 1f, 0, 0);

        Label title = SAVI.text("SAVER PROFILE", SAVI.bold(2.3f), SAVI.WHITE);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container topRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        topRow.add(BorderLayout.CENTER, title);
        setPadding(topRow, 1.2f, 1.2f, 0, 0);
        setMargins(topRow, 0, 1.8f, 0, 0);

        header.add(topRow);
        header.add(greetingBanner(profile));
        return header;
    }

    Container topSection(UserProfile profile) {
        return profileHeader(3.2f, profile);
    }

    static Container sectionHeader(String title, Component right) {
        Container row = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(row, 1.5f, 0.5f, 2, 2);
        row.add(BorderLayout.WEST, SAVI.text(title, SAVI.bold(2.3f), SAVI.NAVY));
        if (right != null) {
            row.add(BorderLayout.EAST, right);
        }
        return row;
    }

    static Container editPill(ActionListener<ActionEvent> onClick) {
        Button b = new Button("Edit");
        b.setUIID("Label");
        b.getAllStyles().setFgColor(SAVI.SLATE_GRAY);
        b.getAllStyles().setFont(SAVI.regular(1.8f));
        setPadding(b, 0, 0, 0, 0);
        setMargins(b, 0, 0, 0, 0);
        FontImage.setMaterialIcon(b, FontImage.MATERIAL_EDIT, 1.9f);
        b.addActionListener(onClick);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), SAVI.POWDER_WHITE, 255, 0);
        setPadding(p, 0.6f, 0.6f, 1.8f, 1.8f);
        p.add(b);
        p.setLeadComponent(b);
        return FlowLayout.encloseRightMiddle(p);
    }

    static Container targetBox(String label, String amount) {
        Rounded box = new Rounded(BoxLayout.y(), SAVI.PALE_PURPLE, 255, 2.5f).stroke(SAVI.PASTEL_LAVENDER, 90, 1);
        setPadding(box, 1.1f, 1.1f, 1.8f, 1.8f);
        setMargins(box, 0, 0, 0.5f, 0.5f);

        box.add(SAVI.text(label, SAVI.bold(1.7f), SAVI.BRIGHT_VIOLET));
        box.add(SAVI.text(amount, SAVI.bold(2.1f), SAVI.DARK_KNIGHT));
        return box;
    }

    static Container savingsCard(char icon, String title, double dailyTarget, double totalTarget,
                                 ActionListener<ActionEvent> onEdit) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2f, 2.2f);
        setMargins(card, 0.5f, 1f, 2, 2);

        Container nameCont = new Container(BoxLayout.y());
        nameCont.add(SAVI.text(title, SAVI.bold(2.1f), SAVI.DARK_KNIGHT));
        setMargins(nameCont, 0, 0, 1.5f, 1f);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, squareIcon(icon, SAVI.PALE_PURPLE, SAVI.BRIGHT_VIOLET));
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(nameCont));

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.CENTER, left);
        header.add(BorderLayout.EAST, editPill(onEdit));

        Container targets = new Container(new GridLayout(1, 2));
        targets.add(targetBox("DAILY TARGET", "P " + Profile.formatAmount(dailyTarget)));
        targets.add(targetBox("TOTAL TARGET", "P " + Profile.formatAmount(totalTarget)));

        card.add(header);
        card.add(divider(SAVI.BORDER_COLOR));
        card.add(targets);
        return card;
    }

    static Container groupCard(GroupSavings group, ActionListener<ActionEvent> onEdit) {
        return savingsCard(
                FontImage.MATERIAL_BEACH_ACCESS,
                group.getGroupName(),
                group.getExpectedPerMember(),
                group.getTargetAmount(),
                onEdit
        );
    }

    static Container personalCard(PersonalSavings personal, ActionListener<ActionEvent> onEdit) {
        return savingsCard(
                FontImage.MATERIAL_SAVINGS,
                personal.getSavingsName(),
                personal.getDailyTarget(),
                personal.getTargetAmount(),
                onEdit
        );
    }

    static Container emptySavingsCard(String emptyText) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2.5f, 2.5f);
        setMargins(card, 0.5f, 1f, 2, 2);

        Label msg = SAVI.text(emptyText, SAVI.regular(2.0f), SAVI.SLATE_GRAY);
        msg.getAllStyles().setAlignment(Component.CENTER);
        card.add(msg);
        return card;
    }

    static Container settingRow(char icon, String title, String badgeText, int iconColor, int titleColor,
                                ActionListener<ActionEvent> onClick) {
        Label text = SAVI.text(title, SAVI.regular(2.2f), titleColor);
        setMargins(text, 0, 0, 1.8f, 1f);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, squareIcon(icon, SAVI.POWDER_WHITE, iconColor));
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(text));

        Button chevron = new Button();
        chevron.setUIID("Label");
        chevron.getAllStyles().setFgColor(SAVI.BLUE_GRAY);
        FontImage.setMaterialIcon(chevron, FontImage.MATERIAL_CHEVRON_RIGHT, 3f);
        chevron.addActionListener(onClick);

        Container right = new Container(new FlowLayout(Component.RIGHT, Component.CENTER));
        if (badgeText != null) {
            right.add(pill(SAVI.text(badgeText, SAVI.semiBold(1.6f), SAVI.BRIGHT_VIOLET), SAVI.PALE_PURPLE));
        }
        right.add(chevron);

        Container row = new Container(new BorderLayout());
        setPadding(row, 2f, 2f, 2.2f, 1.5f);
        row.add(BorderLayout.CENTER, left);
        row.add(BorderLayout.EAST, right);
        row.setLeadComponent(chevron);
        return row;
    }

    Container settingsCard() {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 0f, 0f);
        setMargins(card, 0.5f, 1.5f, 2, 2);

        card.add(settingRow(FontImage.MATERIAL_EXIT_TO_APP, "Logout", null, 0xD32F2F, 0xD32F2F, e -> {
            boolean confirmed = Dialog.show("Logout", "Are you sure you want to log out?", "Logout", "Cancel");
            if (!confirmed) {
                return;
            }
            backend.removeChangeListener(this);
            backend.logout();
            SAVI.user.logout();
            new MainPage().show();
        }));

        return card;
    }

    // --- Interactive Dialogs Connected to Backend ---

    private void showEditProfileDialog() {
        UserProfile profile = backend.getActiveProfile();
        Dialog dlg = new Dialog("Edit Username", new BorderLayout());
        Container body = new Container(BoxLayout.y());
        setPadding(body, 1.5f, 1.5f, 2f, 2f);

        TextField usernameField = new TextField(profile.getUsername(), "Username", 20, TextField.ANY);
        body.add(SAVI.text("USERNAME", SAVI.bold(1.6f), SAVI.SLATE_GRAY));
        body.add(usernameField);

        Container actions = new Container(new GridLayout(1, 2));
        setPadding(actions, 0.5f, 1f, 0.8f, 0.8f);

        Button cancel = new Button("Cancel");
        cancel.getAllStyles().setFont(SAVI.semiBold(1.6f));
        setPadding(cancel, 1f, 1f, 0.3f, 0.3f);
        setMargins(cancel, 0.3f, 0.3f, 0.3f, 0.3f);
        cancel.addActionListener(e -> dlg.dispose());

        Button save = new Button("Save");
        save.getAllStyles().setFont(SAVI.semiBold(1.6f));
        setPadding(save, 1f, 1f, 0.3f, 0.3f);
        setMargins(save, 0.3f, 0.3f, 0.3f, 0.3f);
        save.addActionListener(e -> {
            String result = backend.updateProfileInfo(null, usernameField.getText(), null);
            if (!result.equals("success")) {
                Dialog.show("Could Not Save", result, "OK", null);
                return;
            }
            dlg.dispose();
        });

        actions.add(cancel).add(save);

        dlg.add(BorderLayout.CENTER, body);
        dlg.add(BorderLayout.SOUTH, actions);
        dlg.show();
    }

    /**
     * Edit or delete an existing group in Groups_Records.txt.
     */
    private void showEditGroupDialog(final GroupSavings existing) {
        if (existing == null) {
            return;
        }
        Dialog dlg = new Dialog("Edit Group", new BorderLayout());
        Container body = new Container(BoxLayout.y());
        body.setScrollableY(true);
        setPadding(body, 1.5f, 1.5f, 2f, 2f);

        TextField nameField = new TextField(existing.getGroupName(), "Group Name", 24, TextField.ANY);
        TextField dailyField = new TextField(String.valueOf(existing.getExpectedPerMember()), "Daily Target (P)", 12, TextField.DECIMAL);
        TextField totalField = new TextField(String.valueOf(existing.getTargetAmount()), "Total Target (P)", 12, TextField.DECIMAL);

        body.add(SAVI.text("GROUP NAME", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(nameField);
        body.add(SAVI.text("DAILY TARGET (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(dailyField);
        body.add(SAVI.text("TOTAL TARGET (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(totalField);

        Container actions = new Container(new GridLayout(1, 3));
        setPadding(actions, 0.5f, 1f, 0.8f, 0.8f);

        Button cancel = new Button("Cancel");
        cancel.getAllStyles().setFont(SAVI.semiBold(1.6f));
        setPadding(cancel, 1f, 1f, 0.3f, 0.3f);
        setMargins(cancel, 0.3f, 0.3f, 0.3f, 0.3f);
        cancel.addActionListener(e -> dlg.dispose());
        actions.add(cancel);

        Button delete = new Button("Delete");
        delete.getAllStyles().setFont(SAVI.semiBold(1.6f));
        delete.getAllStyles().setFgColor(0xD32F2F);
        setPadding(delete, 1f, 1f, 0.3f, 0.3f);
        setMargins(delete, 0.3f, 0.3f, 0.3f, 0.3f);
        delete.addActionListener(e -> {
            boolean sure = Dialog.show("Delete Group",
                    "Delete \"" + existing.getGroupName() + "\" and all its members and contributions?",
                    "Delete", "Cancel");
            if (!sure) {
                return;
            }
            backend.deleteGroup(existing.getGroupName());
            dlg.dispose();
        });
        actions.add(delete);

        Button save = new Button("Save");
        save.getAllStyles().setFont(SAVI.semiBold(1.6f));
        setPadding(save, 1f, 1f, 0.3f, 0.3f);
        setMargins(save, 0.3f, 0.3f, 0.3f, 0.3f);
        save.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                Dialog.show("Group Name", "Please enter a group name.", "OK", null);
                return;
            }
            double daily = parseDoubleSafe(dailyField.getText(), 50.0);
            double total = parseDoubleSafe(totalField.getText(), 500.0);

            boolean ok = backend.updateGroup(existing.getGroupName(), name, daily, total);
            if (!ok) {
                Dialog.show("Could Not Save", "Please check the target amounts and make sure the group name is unique.", "OK", null);
                return;
            }
            dlg.dispose();
        });
        actions.add(save);

        dlg.add(BorderLayout.CENTER, body);
        dlg.add(BorderLayout.SOUTH, actions);
        dlg.show();
    }

    /**
     * Edit or delete an existing personal savings goal in Personal_Records.txt.
     */
    private void showEditPersonalDialog(final PersonalSavings existing) {
        if (existing == null) {
            return;
        }
        Dialog dlg = new Dialog("Edit Personal Savings", new BorderLayout());
        Container body = new Container(BoxLayout.y());
        body.setScrollableY(true);
        setPadding(body, 1.5f, 1.5f, 2f, 2f);

        TextField nameField = new TextField(existing.getSavingsName(), "Savings Name", 24, TextField.ANY);
        TextField dailyField = new TextField(String.valueOf(existing.getDailyTarget()), "Daily Target (P)", 12, TextField.DECIMAL);
        TextField totalField = new TextField(String.valueOf(existing.getTargetAmount()), "Total Target (P)", 12, TextField.DECIMAL);

        body.add(SAVI.text("SAVINGS NAME", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(nameField);
        body.add(SAVI.text("DAILY TARGET (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(dailyField);
        body.add(SAVI.text("TOTAL TARGET (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(totalField);

        Container actions = new Container(new GridLayout(1, 3));
        setPadding(actions, 0.5f, 1f, 0.8f, 0.8f);

        Button cancel = new Button("Cancel");
        cancel.getAllStyles().setFont(SAVI.regular(1.8f));
        setPadding(cancel, 1f, 1f, 0, 0);
        setMargins(cancel, 0.3f, 0.3f, 0.3f, 0.3f);
        cancel.addActionListener(e -> dlg.dispose());
        actions.add(cancel);

        Button delete = new Button("Delete");
        delete.getAllStyles().setFont(SAVI.regular(1.8f));
        delete.getAllStyles().setFgColor(0xD32F2F);
        setPadding(delete, 1f, 1f, 0, 0);
        setMargins(delete, 0.3f, 0.3f, 0.3f, 0.3f);
        delete.addActionListener(e -> {
            boolean sure = Dialog.show("Delete Personal Savings",
                    "Delete \"" + existing.getSavingsName() + "\" and all its contributions?",
                    "Delete", "Cancel");
            if (!sure) {
                return;
            }
            backend.deletePersonalSavings(existing.getSavingsName());
            dlg.dispose();
        });
        actions.add(delete);

        Button save = new Button("Save");
        save.getAllStyles().setFont(SAVI.regular(1.8f));
        setPadding(save, 1f, 1f, 0, 0);
        setMargins(save, 0.3f, 0.3f, 0.3f, 0.3f);
        save.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                Dialog.show("Savings Name", "Please enter a savings name.", "OK", null);
                return;
            }
            double daily = parseDoubleSafe(dailyField.getText(), 0.0);
            double total = parseDoubleSafe(totalField.getText(), 500.0);

            boolean ok = backend.updatePersonalSavings(existing.getSavingsName(), name, daily, total);
            if (!ok) {
                Dialog.show("Could Not Save", "Please check the target amounts and make sure the savings name is unique.", "OK", null);
                return;
            }
            dlg.dispose();
        });
        actions.add(save);

        dlg.add(BorderLayout.CENTER, body);
        dlg.add(BorderLayout.SOUTH, actions);
        dlg.show();
    }

    private static double parseDoubleSafe(String val, double fallback) {
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    // --- Reactive UI Rendering ---

    private void renderSections() {
        if (mainContents == null) {
            return;
        }
        mainContents.removeAll();

        UserProfile profile = backend.getActiveProfile();
        List<GroupSavings> groups = backend.getGroups();
        List<PersonalSavings> personals = backend.getPersonalSavings();

        mainContents.add(topSection(profile));
        mainContents.add(sectionHeader("GROUP SAVINGS SETTINGS", null));

        if (groups.isEmpty()) {
            mainContents.add(emptySavingsCard("No savings groups yet."));
        } else {
            for (int i = 0; i < groups.size(); i++) {
                final GroupSavings group = groups.get(i);
                mainContents.add(groupCard(group, e -> showEditGroupDialog(group)));
            }
        }

        mainContents.add(sectionHeader("PERSONAL SAVINGS SETTINGS", null));

        if (personals.isEmpty()) {
            mainContents.add(emptySavingsCard("No personal savings yet."));
        } else {
            for (int i = 0; i < personals.size(); i++) {
                final PersonalSavings personal = personals.get(i);
                mainContents.add(personalCard(personal, e -> showEditPersonalDialog(personal)));
            }
        }

        mainContents.add(sectionHeader("GENERAL SETTINGS", null));
        mainContents.add(settingsCard());

        mainContents.revalidate();
    }

    @Override
    public void onProfileDataChanged() {
        renderSections();
    }

    public void show() {
        backend.loginAs(SAVI.user.getCurrentName());
        backend.refreshGroups();
        backend.addChangeListener(this);

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        paintStatusBar(base, SAVI.DARK_PURPLE);

        mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        renderSections();

        base.add(BorderLayout.CENTER, mainContents);
        base.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_PROFILE));
        base.show();
    }
}