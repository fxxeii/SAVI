package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Label;
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
import com.savi.app.backend.Home;

public class ProfilePage {

    // Colors specific to this page (move to SAVI if you want to reuse them)
    static final int PAGE_BG = 0xF8F9FC;
    static final int HEADER_START = 0x7C3AED;
    static final int HEADER_END = 0x6A27D4;
    static final int HEADER_SOFT = 0x8E6BE8;
    static final int HEADER_LABEL = 0xC9B8F5;
    static final int AVATAR_FILL = 0x9B6BF2;
    static final int LIGHT_VIOLET = 0xEFEAFE;
    static final int ICON_BOX = 0xF1F2F6;
    static final int LINE_GRAY = 0xE8EAF0;
    static final int TARGET_BG = 0xF7F4FF;
    static final int TARGET_BORDER = 0xE9E1FC;
    static final int TITLE_DARK = 0x2D3748;

    // ---------- general helpers ----------

    static int mm(float v) {
        return Display.getInstance().convertToPixels(v);
    }

    static float pxToMM(int px) {
        return px / (float) mm(1f);
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

    // colors the status bar / title area so it doesn't show up as a white strip
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
        line.setPreferredH(mm(0.2f));
        Style s = line.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        setMargins(line, 1.5f, 1.5f, 0, 0);
        return line;
    }

    // ---------- shapes drawn with fillRoundRect (true circles / pills, no border min size) ----------

    static class Rounded extends Container {
        private final int fill, fillAlpha;
        private final float radiusMM; // 0 = fully round (circle or pill)
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

    // non clickable circle holding one icon
    static Container circleIcon(char icon, float sizeMM, float iconMM, int bg, int fg) {
        Label l = new Label();
        l.getAllStyles().setFgColor(fg);
        l.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(l, icon, iconMM);

        Rounded c = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), bg, 255, 0).fixed(sizeMM);
        c.add(BorderLayout.CENTER, l);
        return c;
    }

    // clickable circle: an icon Button inside the circle leads it
    static Container circleButton(char icon, float sizeMM, float iconMM, int bg, int fg,
                                  ActionListener<ActionEvent> onClick) {
        Button b = new Button();
        b.setUIID("Label");
        b.getAllStyles().setFgColor(fg);
        b.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(b, icon, iconMM);
        b.addActionListener(onClick);

        Rounded c = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), bg, 255, 0).fixed(sizeMM);
        c.add(BorderLayout.CENTER, b);
        c.setLeadComponent(b);
        return c;
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

    // ---------- gradient header background (masked image, no clipping) ----------

    static Image gradientImage(int w, int h, int c1, int c2, int radiusPx) {
        Image img = Image.createImage(w, h, 0);
        Graphics g = img.getGraphics();
        g.fillLinearGradient(c1, c2, 0, 0, w, h, false);

        Image mask = Image.createImage(w, h, 0xff000000);
        Graphics mg = mask.getGraphics();
        mg.setAntiAliased(true);
        mg.setColor(0xffffff);
        mg.fillRoundRect(0, 0, w, h, radiusPx * 2, radiusPx * 2);
        mg.fillRect(0, 0, w, h / 2); // keep the top edge square, only the bottom is rounded
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
                cache = gradientImage(w, h, HEADER_START, HEADER_END, mm(radiusMM));
            }
            g.drawImage(cache, getX(), getY());
        }
    }

    // ---------- header ----------

    static Container avatar() {
        Rounded innerC = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), AVATAR_FILL, 255, 0)
                .stroke(SAVI.WHITE, 255, 2).fixed(13.5f);
        Label person = new Label();
        person.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(person, FontImage.MATERIAL_PERSON, 6.5f);
        innerC.add(BorderLayout.CENTER, person);

        Rounded ringC = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), 0, 0, 0)
                .stroke(SAVI.WHITE, 110, 2).fixed(16f);
        ringC.add(BorderLayout.CENTER, innerC);

        // camera badge, bottom right
        Container badge = circleIcon(FontImage.MATERIAL_PHOTO_CAMERA, 4.6f, 2.4f, SAVI.WHITE, SAVI.BRIGHT_VIOLET);

        Container av = new Container(new LayeredLayout());
        av.add(ringC);
        av.add(BorderLayout.south(FlowLayout.encloseRight(badge)));
        return FlowLayout.encloseCenter(av);
    }

    static Container profileHeader(float extraBottomMM) {
        Container header = new GradientHeader(BoxLayout.y(), 6f);
        setPadding(header, 1f, extraBottomMM, 2f, 2f);

        // top row: back / title / share
        Container back = circleButton(FontImage.MATERIAL_ARROW_BACK_IOS, 5.3f, 2.2f,
                HEADER_SOFT, SAVI.WHITE, e -> new HomePage().show());
        Container share = circleButton(FontImage.MATERIAL_FILE_UPLOAD, 5.3f, 2.2f,
                HEADER_SOFT, SAVI.WHITE, e -> {
                });
        Label title = SAVI.text("PROFILE", SAVI.bold(2.3f), SAVI.WHITE);

        Container topRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        topRow.add(BorderLayout.WEST, back);
        topRow.add(BorderLayout.CENTER, title);
        topRow.add(BorderLayout.EAST, share);
        setMargins(topRow, 0, 2.5f, 0, 0);

        // name with the pencil icon right next to it
        Label name = SAVI.text("User2026-Trial", SAVI.bold(3f), SAVI.WHITE);
        name.setTextPosition(Component.LEFT);
        name.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(name, FontImage.MATERIAL_EDIT, 2.2f);
        setMargins(name, 1.5f, 0, 0, 0);

        Label handle = SAVI.text("@user.2026", SAVI.regular(2.1f), HEADER_LABEL);
        handle.getAllStyles().setAlignment(Component.CENTER);

        header.add(topRow);
        header.add(avatar());
        header.add(name);
        header.add(handle);
        return header;
    }

    // ---------- stats card ----------

    static Container statCell(String value, String caption, int valueColor, boolean divided) {
        Container cell = new Container(BoxLayout.y());
        Label v = SAVI.text(value, SAVI.bold(2.6f), valueColor);
        Label c = SAVI.text(caption, SAVI.regular(1.75f), SAVI.SLATE_GRAY);
        v.getAllStyles().setAlignment(Component.CENTER);
        c.getAllStyles().setAlignment(Component.CENTER);
        cell.add(v).add(c);
        setPadding(cell, 1.2f, 1.2f, 0.5f, 0.5f);
        if (divided) { // line on both sides of the middle cell
            Border line = Border.createLineBorder(1, LINE_GRAY);
            cell.getAllStyles().setBorder(Border.createCompoundBorder(null, null, line, line));
        }
        return cell;
    }

    static Container statsCard(int groups, double saved, int people) {
        Container card = new Container(new GridLayout(1, 3));
        cardStyles(card, SAVI.WHITE, 4, 1.5f, 1.5f);
        setMargins(card, 0, 1, 2, 2);

        card.add(statCell(String.valueOf(groups), "GROUPS", SAVI.DARK_KNIGHT, false));
        card.add(statCell(Home.pesoShort(saved), "TOTAL SAVED", SAVI.BRIGHT_VIOLET, true));
        card.add(statCell(String.valueOf(people), "PEOPLE", SAVI.DARK_KNIGHT, false));
        return card;
    }

    // header with the stats card hanging over its bottom edge
    static Container topSection() {
        final Container stats = statsCard(2, 4256.5, 15);
        final int statsH = stats.getOuterPreferredH();
        final float gapMM = 3.5f;

        final Container header = profileHeader(gapMM + pxToMM(statsH) / 2f);

        Container top = new Container(new LayeredLayout()) {
            @Override
            protected Dimension calcPreferredSize() {
                Dimension d = super.calcPreferredSize();
                // header already includes half of the card, add the other half
                return new Dimension(d.getWidth(), header.getPreferredH() + statsH / 2);
            }
        };
        top.add(BorderLayout.north(header));
        top.add(BorderLayout.south(stats));
        return top;
    }

    // ---------- section header ----------

    static Container sectionHeader(String title, Component right) {
        Container row = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(row, 1.5f, 0.5f, 2, 2);
        row.add(BorderLayout.WEST, SAVI.text(title, SAVI.bold(2.4f), TITLE_DARK));
        if (right != null) {
            row.add(BorderLayout.EAST, right);
        }
        return row;
    }

    // ---------- group settings ----------

    static Container editPill(ActionListener<ActionEvent> onClick) {
        Button b = new Button("Edit");
        b.setUIID("Label");
        b.getAllStyles().setFgColor(SAVI.SLATE_GRAY);
        b.getAllStyles().setFont(SAVI.regular(1.8f));
        setPadding(b, 0, 0, 0, 0);
        setMargins(b, 0, 0, 0, 0);
        FontImage.setMaterialIcon(b, FontImage.MATERIAL_EDIT, 1.9f);
        b.addActionListener(onClick);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), ICON_BOX, 255, 0);
        setPadding(p, 0.6f, 0.6f, 1.8f, 1.8f);
        p.add(b);
        p.setLeadComponent(b);
        return FlowLayout.encloseRightMiddle(p);
    }

    static Container targetBox(String label, String amount) {
        Rounded box = new Rounded(BoxLayout.y(), TARGET_BG, 255, 2.5f).stroke(TARGET_BORDER, 255, 1);
        setPadding(box, 1.1f, 1.1f, 2f, 2f);
        setMargins(box, 0, 0, 0.5f, 0.5f);

        box.add(SAVI.text(label, SAVI.bold(1.75f), SAVI.BRIGHT_VIOLET));
        box.add(SAVI.text(amount, SAVI.bold(2.2f), SAVI.DARK_KNIGHT));
        return box;
    }

    static Container groupCard(String name, String target, String daily, String total,
                               ActionListener<ActionEvent> onEdit) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2f, 2.5f);
        setMargins(card, 0.5f, 1f, 2, 2);

        Container nameCont = new Container(BoxLayout.y());
        nameCont.add(SAVI.text(name, SAVI.bold(2.1f), SAVI.DARK_KNIGHT));
        nameCont.add(SAVI.text(target, SAVI.regular(1.8f), SAVI.SLATE_GRAY));
        setMargins(nameCont, 0, 0, 1.5f, 0);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, squareIcon(FontImage.MATERIAL_BEACH_ACCESS, LIGHT_VIOLET, SAVI.BRIGHT_VIOLET));
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(nameCont));

        Container header = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        header.add(BorderLayout.WEST, left);
        header.add(BorderLayout.EAST, editPill(onEdit));

        Container targets = new Container(new GridLayout(1, 2));
        targets.add(targetBox("DAILY TARGET", "P " + daily));
        targets.add(targetBox("TOTAL TARGET", "P " + total));

        card.add(header);
        card.add(divider(LINE_GRAY));
        card.add(targets);
        return card;
    }

    // ---------- general settings ----------

    static Container settingRow(char icon, String title, String badgeText, ActionListener<ActionEvent> onClick) {
        Label text = SAVI.text(title, SAVI.regular(2.3f), SAVI.DARK_KNIGHT);
        setMargins(text, 0, 0, 1.8f, 0);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, squareIcon(icon, ICON_BOX, SAVI.DEEP_SLATE));
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(text));

        // the chevron is a real child Button, so it can lead the whole row
        Button chevron = new Button();
        chevron.setUIID("Label");
        chevron.getAllStyles().setFgColor(0xB4B9C5);
        FontImage.setMaterialIcon(chevron, FontImage.MATERIAL_CHEVRON_RIGHT, 3f);
        chevron.addActionListener(onClick);

        Container right = new Container(new FlowLayout(Component.RIGHT, Component.CENTER));
        if (badgeText != null) {
            right.add(pill(SAVI.text(badgeText, SAVI.semiBold(1.6f), SAVI.BRIGHT_VIOLET), LIGHT_VIOLET));
        }
        right.add(chevron);

        Container row = new Container(new BorderLayout());
        setPadding(row, 2f, 2f, 2.5f, 1.5f);
        row.add(BorderLayout.WEST, left);
        row.add(BorderLayout.EAST, right);
        row.setLeadComponent(chevron);
        return row;
    }

    static Container settingsCard(char[] icons, String[] titles, String[] badges) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 0f, 0f);
        setMargins(card, 0.5f, 1f, 2, 2);

        for (int i = 0; i < titles.length; i++) {
            if (i > 0) {
                Container line = divider(LINE_GRAY);
                setMargins(line, 0, 0, 0, 0);
                card.add(line);
            }
            card.add(settingRow(icons[i], titles[i], badges[i], e -> {
                // TODO: open setting
            }));
        }
        return card;
    }

    // ---------- page ----------

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(PAGE_BG);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        paintStatusBar(base, HEADER_START);

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);

        mainContents.add(topSection());

        // group settings
        Container addGroup = FlowLayout.encloseRightMiddle(
                pill(SAVI.text("+ Add Group", SAVI.semiBold(1.8f), SAVI.BRIGHT_VIOLET), LIGHT_VIOLET));
        mainContents.add(sectionHeader("GROUP SETTINGS", addGroup));
        mainContents.add(groupCard("December Vacation", "Target: Dec 10, 2026", "50", "500", e -> {
            // TODO: open edit group page
        }));

        // general settings
        mainContents.add(sectionHeader("GENERAL SETTINGS", null));
        char[] icons = {FontImage.MATERIAL_SECURITY, FontImage.MATERIAL_NOTIFICATIONS};
        String[] titles = {"Security & Privacy", "Notifications"};
        String[] badges = {null, "Active"};
        mainContents.add(settingsCard(icons, titles, badges));

        base.add(BorderLayout.CENTER, mainContents);

        base.add(BorderLayout.SOUTH,SAVI.bottomNav(SAVI.NAV_USER));
        base.show();


    }
}