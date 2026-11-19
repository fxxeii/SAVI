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
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;
import com.savi.app.backend.Profile;
import com.savi.app.backend.SavingsGroup;
import com.savi.app.backend.UserProfile;

import java.util.List;

public class ProfilePage implements Profile.DataChangeListener {

    private final Profile backend = Profile.getInstance();
    private Container mainContents;

    static int mm(float v) {
        return Display.getInstance().convertToPixels(v);
    }

    static float pxToMM(int px) {
        int oneMM = mm(1f);
        return oneMM > 0 ? px / (float) oneMM : px / 6f;
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

    static Container interactivePill(String labelText, int bgColor, int fgColor, ActionListener<ActionEvent> onClick) {
        Button btn = new Button(labelText);
        btn.setUIID("Label");
        btn.getAllStyles().setFgColor(fgColor);
        btn.getAllStyles().setFont(SAVI.semiBold(1.8f));
        setPadding(btn, 0, 0, 0, 0);
        setMargins(btn, 0, 0, 0, 0);
        btn.addActionListener(onClick);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), bgColor, 255, 0);
        setPadding(p, 0.6f, 0.6f, 1.8f, 1.8f);
        p.add(btn);
        p.setLeadComponent(btn);
        return FlowLayout.encloseRightMiddle(p);
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

    static Container greetingBanner(UserProfile profile) {
        Rounded badge = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), SAVI.WHITE, 35, 3f)
                .stroke(SAVI.WHITE, 90, 1);
        setPadding(badge, 1.2f, 1.2f, 3.2f, 3.2f);
        setMargins(badge, 0.5f, 0.5f, 0, 0);

        Label helloLabel = SAVI.text("Hello, " + profile.getGreetingName() + "!", SAVI.bold(3.2f), SAVI.WHITE);
        helloLabel.getAllStyles().setAlignment(Component.CENTER);
        badge.add(helloLabel);

        return FlowLayout.encloseCenter(badge);
    }

    Container profileHeader(float extraBottomMM, UserProfile profile) {
        Container header = new GradientHeader(BoxLayout.y(), 6f);
        setPadding(header, 1f, extraBottomMM, 2f, 2f);

        Label title = SAVI.text("PROFILE", SAVI.bold(2.3f), SAVI.WHITE);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container topRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        topRow.add(BorderLayout.CENTER, title);
        setPadding(topRow, 1.2f, 1.2f, 0, 0);
        setMargins(topRow, 0, 2f, 0, 0);

        Button nameBtn = new Button(profile.getUsername());
        nameBtn.setUIID("Label");
        nameBtn.getAllStyles().setFont(SAVI.bold(2.6f));
        nameBtn.getAllStyles().setFgColor(SAVI.WHITE);
        nameBtn.setTextPosition(Component.LEFT);
        nameBtn.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(nameBtn, FontImage.MATERIAL_EDIT, 2.1f);
        nameBtn.addActionListener(e -> showEditProfileDialog());
        setMargins(nameBtn, 1.2f, 0, 0, 0);

        Label handle = SAVI.text(profile.getHandle(), SAVI.regular(2.0f), SAVI.PASTEL_LAVENDER);
        handle.getAllStyles().setAlignment(Component.CENTER);

        header.add(topRow);
        header.add(greetingBanner(profile));
        header.add(FlowLayout.encloseCenter(nameBtn));
        header.add(handle);
        return header;
    }

    static Container statCell(String value, String caption, int valueColor, boolean divided) {
        Container cell = new Container(BoxLayout.y());
        Label v = SAVI.text(value, SAVI.bold(2.5f), valueColor);
        Label c = SAVI.text(caption, SAVI.regular(1.7f), SAVI.SLATE_GRAY);
        v.getAllStyles().setAlignment(Component.CENTER);
        c.getAllStyles().setAlignment(Component.CENTER);
        cell.add(v).add(c);
        setPadding(cell, 1.2f, 1.2f, 0.4f, 0.4f);
        if (divided) {
            Border line = Border.createLineBorder(1, SAVI.BORDER_COLOR);
            cell.getAllStyles().setBorder(Border.createCompoundBorder(null, null, line, line));
        }
        return cell;
    }

    static Container statsCard(int groups, double saved, int people) {
        Container card = new Container(new GridLayout(1, 3));
        cardStyles(card, SAVI.WHITE, 4, 1.5f, 1.2f);
        setMargins(card, 0, 1, 2, 2);

        card.add(statCell(String.valueOf(groups), "GROUPS", SAVI.DARK_KNIGHT, false));
        card.add(statCell("P" + Profile.formatAmount(saved), "TOTAL SAVED", SAVI.BRIGHT_VIOLET, true));
        card.add(statCell(String.valueOf(people), "PEOPLE", SAVI.DARK_KNIGHT, false));
        return card;
    }

    Container topSection(UserProfile profile) {
        final Container stats = statsCard(
                backend.getGroupCount(),
                backend.getTotalSaved(),
                backend.getTotalPeople()
        );
        int measuredStatsH = stats.getOuterPreferredH();
        if (measuredStatsH <= 0) {
            measuredStatsH = mm(12f);
        }
        final int statsH = measuredStatsH;
        final float gapMM = 3.2f;

        final Container header = profileHeader(gapMM + pxToMM(statsH) / 2f, profile);

        Container top = new Container(new LayeredLayout()) {
            @Override
            protected Dimension calcPreferredSize() {
                Dimension d = super.calcPreferredSize();
                int headerH = header.getPreferredH();
                return new Dimension(d.getWidth(), headerH + (statsH / 2));
            }
        };
        top.add(BorderLayout.north(header));
        top.add(BorderLayout.south(stats));
        return top;
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

    static Container groupCard(SavingsGroup group, ActionListener<ActionEvent> onEdit) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2f, 2.2f);
        setMargins(card, 0.5f, 1f, 2, 2);

        Container nameCont = new Container(BoxLayout.y());
        nameCont.add(SAVI.text(group.getName(), SAVI.bold(2.1f), SAVI.DARK_KNIGHT));
        nameCont.add(SAVI.text("Target: " + group.getTargetDate(), SAVI.regular(1.75f), SAVI.SLATE_GRAY));
        setMargins(nameCont, 0, 0, 1.5f, 1f);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, squareIcon(group.getIcon(), SAVI.PALE_PURPLE, SAVI.BRIGHT_VIOLET));
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(nameCont));

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.CENTER, left);
        header.add(BorderLayout.EAST, editPill(onEdit));

        Container targets = new Container(new GridLayout(1, 2));
        targets.add(targetBox("DAILY TARGET", "P " + Profile.formatAmount(group.getDailyTarget())));
        targets.add(targetBox("TOTAL TARGET", "P " + Profile.formatAmount(group.getTotalTarget())));

        card.add(header);
        card.add(divider(SAVI.BORDER_COLOR));
        card.add(targets);
        return card;
    }

    static Container emptyGroupCard(ActionListener<ActionEvent> onAdd) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2.5f, 2.5f);
        setMargins(card, 0.5f, 1f, 2, 2);

        Label msg = SAVI.text("No savings groups yet.", SAVI.regular(2.0f), SAVI.SLATE_GRAY);
        msg.getAllStyles().setAlignment(Component.CENTER);
        card.add(msg);
        card.add(FlowLayout.encloseCenter(
                interactivePill("+ Create First Group", SAVI.PALE_PURPLE, SAVI.BRIGHT_VIOLET, onAdd)
        ));
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
            backend.removeChangeListener(this);
            backend.logout();
            new MainPage().show();
        }));

        return card;
    }

    // --- Interactive Dialogs Connected to Backend ---

    private void showEditProfileDialog() {
        UserProfile profile = backend.getActiveProfile();
        Dialog dlg = new Dialog("Edit Profile", new BorderLayout());
        Container body = new Container(BoxLayout.y());
        setPadding(body, 1.5f, 1.5f, 2f, 2f);

        TextField greetingField = new TextField(profile.getGreetingName(), "Greeting Name (e.g. User)", 20, TextField.ANY);
        TextField usernameField = new TextField(profile.getUsername(), "Display Username", 20, TextField.ANY);
        TextField handleField = new TextField(profile.getHandle(), "@handle", 20, TextField.ANY);

        body.add(SAVI.text("GREETING NAME (SHOWN IN HEADER)", SAVI.bold(1.6f), SAVI.SLATE_GRAY));
        body.add(greetingField);
        body.add(SAVI.text("USERNAME", SAVI.bold(1.6f), SAVI.SLATE_GRAY));
        body.add(usernameField);
        body.add(SAVI.text("HANDLE", SAVI.bold(1.6f), SAVI.SLATE_GRAY));
        body.add(handleField);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(e -> dlg.dispose());

        Button save = new Button("Save");
        save.addActionListener(e -> {
            backend.updateProfileInfo(greetingField.getText(), usernameField.getText(), handleField.getText());
            dlg.dispose();
        });

        Container actions = new Container(new GridLayout(1, 2));
        actions.add(cancel).add(save);

        dlg.add(BorderLayout.CENTER, body);
        dlg.add(BorderLayout.SOUTH, actions);
        dlg.show();
    }

    private void showGroupFormDialog(final SavingsGroup existing) {
        boolean isEdit = existing != null;
        Dialog dlg = new Dialog(isEdit ? "Edit Group" : "Add Savings Group", new BorderLayout());
        Container body = new Container(BoxLayout.y());
        body.setScrollableY(true);
        setPadding(body, 1.5f, 1.5f, 2f, 2f);

        TextField nameField = new TextField(isEdit ? existing.getName() : "", "Group Name", 24, TextField.ANY);
        TextField dateField = new TextField(isEdit ? existing.getTargetDate() : "Dec 10, 2026", "Target Date", 20, TextField.ANY);
        TextField dailyField = new TextField(isEdit ? String.valueOf(existing.getDailyTarget()) : "50", "Daily Target (P)", 12, TextField.DECIMAL);
        TextField totalField = new TextField(isEdit ? String.valueOf(existing.getTotalTarget()) : "500", "Total Target (P)", 12, TextField.DECIMAL);
        TextField savedField = new TextField(isEdit ? String.valueOf(existing.getCurrentSaved()) : "0", "Current Saved (P)", 12, TextField.DECIMAL);
        TextField membersField = new TextField(isEdit ? String.valueOf(existing.getMemberCount()) : "1", "People Count", 6, TextField.NUMERIC);

        body.add(SAVI.text("GROUP NAME", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(nameField);
        body.add(SAVI.text("TARGET DATE", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(dateField);
        body.add(SAVI.text("DAILY TARGET (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(dailyField);
        body.add(SAVI.text("TOTAL TARGET (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(totalField);
        body.add(SAVI.text("AMOUNT SAVED SO FAR (P)", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(savedField);
        body.add(SAVI.text("NUMBER OF PEOPLE", SAVI.bold(1.6f), SAVI.SLATE_GRAY)).add(membersField);

        Container actions = new Container(new GridLayout(1, isEdit ? 3 : 2));

        Button cancel = new Button("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        actions.add(cancel);

        if (isEdit) {
            Button delete = new Button("Delete");
            delete.getAllStyles().setFgColor(0xD32F2F);
            delete.addActionListener(e -> {
                backend.deleteGroup(existing.getId());
                dlg.dispose();
            });
            actions.add(delete);
        }

        Button save = new Button("Save");
        save.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                return;
            }
            String date = dateField.getText().trim().isEmpty() ? "TBD" : dateField.getText().trim();
            double daily = parseDoubleSafe(dailyField.getText(), 50.0);
            double total = parseDoubleSafe(totalField.getText(), 500.0);
            double saved = parseDoubleSafe(savedField.getText(), 0.0);
            int members = Math.max(1, parseIntSafe(membersField.getText(), 1));

            if (isEdit) {
                backend.updateGroup(existing.getId(), name, date, daily, total, saved, members);
            } else {
                backend.addGroup(name, date, daily, total, saved, members, FontImage.MATERIAL_BEACH_ACCESS);
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

    private static int parseIntSafe(String val, int fallback) {
        try {
            return Integer.parseInt(val.trim());
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
        List<SavingsGroup> groups = backend.getGroups();

        mainContents.add(topSection(profile));

        Container addGroup = interactivePill("+ Add Group", SAVI.PALE_PURPLE, SAVI.BRIGHT_VIOLET,
                e -> showGroupFormDialog(null));
        mainContents.add(sectionHeader("GROUP SETTINGS", addGroup));

        if (groups.isEmpty()) {
            mainContents.add(emptyGroupCard(e -> showGroupFormDialog(null)));
        } else {
            for (int i = 0; i < groups.size(); i++) {
                final SavingsGroup group = groups.get(i);
                mainContents.add(groupCard(group, e -> showGroupFormDialog(group)));
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
        backend.addChangeListener(this);

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        paintStatusBar(base, SAVI.DARK_PURPLE);

        mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        renderSections();

        base.add(BorderLayout.CENTER, mainContents);
        base.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_USER));
        base.show();
    }
}