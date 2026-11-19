package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;
import com.savi.app.backend.Home;

public class PeoplePage {

    static final int PAGE_BG = 0xF8F9FC;
    static final int LIGHT_VIOLET = 0xEFEAFE;
    static final int LIGHT_VIOLET_BORDER = 0xDDD3FB;
    static final int SOFT_GRAY = 0xF8F9FC;
    static final int LINE_GRAY = 0xE8EAF0;

    static final int BANNER_START = 0x7C3AED;
    static final int BANNER_END = 0x3B2DA6;
    static final int BANNER_SOFT = 0x8E6BE8;
    static final int BANNER_LINE = 0x9468EA;
    static final int BANNER_LABEL = 0xC9B8F5;

    static final int MISSED_BG = 0xFFF3F5;
    static final int MISSED_BORDER = 0xFBD5DC;
    static final int MISSED_TEXT = 0xC8153F;
    static final int PAID_BG = 0xEFFCF6;
    static final int PAID_BORDER = 0xBDF0D6;
    static final int PAID_TEXT = 0x0E9F6E;

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
        line.setPreferredH(Display.getInstance().convertToPixels(0.2f));
        Style s = line.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        setMargins(line, 1.5f, 1.5f, 0, 0);
        return line;
    }

    static GeneralPath roundRectPath(float x, float y, float w, float h, float r) {
        GeneralPath p = new GeneralPath();
        p.moveTo(x + r, y);
        p.lineTo(x + w - r, y);
        p.quadTo(x + w, y, x + w, y + r);
        p.lineTo(x + w, y + h - r);
        p.quadTo(x + w, y + h, x + w - r, y + h);
        p.lineTo(x + r, y + h);
        p.quadTo(x, y + h, x, y + h - r);
        p.lineTo(x, y + r);
        p.quadTo(x, y, x + r, y);
        p.closePath();
        return p;
    }

    // circle holding a single icon (white card, soft shadow)
    static Container circleIcon(char icon, float sizeMM, float iconMM, int bg, int fg, boolean shadow) {
        Label l = new Label();
        l.getAllStyles().setFgColor(fg);
        l.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(l, icon, iconMM);

        Container c = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        c.setPreferredW(Display.getInstance().convertToPixels(sizeMM));
        c.setPreferredH(Display.getInstance().convertToPixels(sizeMM));
        Style s = c.getAllStyles();
        s.setBgColor(bg);
        s.setBgTransparency(255);
        RoundRectBorder b = RoundRectBorder.create().cornerRadius(sizeMM / 2f);
        if (shadow) {
            b.shadowOpacity(40).shadowBlur(8).shadowSpread(1f).shadowY(0.3f).shadowColor(SAVI.BRIGHT_VIOLET);
        }
        s.setBorder(b);
        c.add(BorderLayout.CENTER, l);
        return c;
    }

    static Container pill(Label text, int bgColor, int borderColor) {
        Container p = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        Style s = p.getAllStyles();
        s.setBgColor(bgColor);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(2.5f).
                strokeColor(borderColor).
                strokeOpacity(255).
                stroke(0.2f, true));
        setPadding(p, 0.8f, 0.8f, 2f, 2f);
        p.add(text);
        return p;
    }

    static Container groupBanner(String name, String pool, int contributors) {
        final float radiusMM = 5f;
        Container banner = new Container(BoxLayout.y()) {
            @Override
            public void paintBackground(Graphics g) {
                super.paintBackground(g);
                if (g.isShapeClipSupported()) {
                    int r = Display.getInstance().convertToPixels(radiusMM);
                    g.setAntiAliased(true);
                    g.pushClip();
                    g.setClip(roundRectPath(getX(), getY(), getWidth(), getHeight(), r));
                    g.fillLinearGradient(BANNER_START, BANNER_END, getX(), getY(), getWidth(), getHeight(), true);
                    g.popClip();
                }
            }
        };
        cardStyles(banner, BANNER_START, radiusMM, 2.5f, 2.5f);
        setMargins(banner, 0.5f, 1, 2, 2);

        Label groupName = SAVI.text(name.toUpperCase(), SAVI.bold(3.5f), SAVI.WHITE);
        Container chevron = circleIcon(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 6f, 3f, BANNER_SOFT, SAVI.WHITE, false);

        Container titleRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        titleRow.add(BorderLayout.WEST, groupName);
        titleRow.add(BorderLayout.EAST, chevron);

        Container poolCont = new Container(BoxLayout.y());
        poolCont.add(SAVI.text("Total Pool", SAVI.regular(2f), BANNER_LABEL));
        poolCont.add(SAVI.text(pool, SAVI.bold(4f), SAVI.WHITE));

        Container memCont = new Container(BoxLayout.y());
        Label memText = SAVI.text("Members", SAVI.regular(2f), BANNER_LABEL);
        memText.getAllStyles().setAlignment(Component.RIGHT);
        Container memPill = pill(SAVI.text(contributors + " Contributors", SAVI.semiBold(2f), SAVI.WHITE),
                BANNER_SOFT, BANNER_SOFT);
        memCont.add(memText);
        memCont.add(memPill);

        Container infoRow = new Container(new BorderLayout());
        infoRow.add(BorderLayout.WEST, poolCont);
        infoRow.add(BorderLayout.EAST, memCont);

        banner.add(titleRow);
        banner.add(divider(BANNER_LINE));
        banner.add(infoRow);
        return banner;
    }

    static Container sectionHeader() {
        Container header = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(header, 1, 0.5f, 2, 2);

        Label title = SAVI.text("MEMBER CONTRIBUTIONS", SAVI.bold(2f), SAVI.SLATE_GRAY);
        Container updated = pill(SAVI.text("Updated Today", SAVI.regular(1.8f), SAVI.BRIGHT_VIOLET),
                LIGHT_VIOLET, LIGHT_VIOLET);

        header.add(BorderLayout.WEST, title);
        header.add(BorderLayout.EAST, updated);
        return header;
    }

    static Container avatar(int color) {
        Container av = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        av.setPreferredW(Display.getInstance().convertToPixels(12f));
        av.setPreferredH(Display.getInstance().convertToPixels(12f));
        Style s = av.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(3.5f).
                shadowOpacity(50).
                shadowBlur(8).
                shadowSpread(1f).
                shadowY(0.4f).
                shadowColor(color));

        Label icon = new Label();
        icon.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(icon, FontImage.MATERIAL_PERSON_OUTLINE, 5f);
        av.add(BorderLayout.CENTER, icon);
        return av;
    }

    static Container statBox(String label, String amount, int bg, int border, int labelColor, int valueColor) {
        Container box = new Container(BoxLayout.y());
        Style s = box.getAllStyles();
        s.setBgColor(bg);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(3).
                strokeColor(border).
                strokeOpacity(255).
                stroke(0.2f, true));
        setPadding(box, 1.5f, 1.5f, 2f, 2f);
        setMargins(box, 0, 0, 0.5f, 0.5f);

        box.add(SAVI.text(label, SAVI.regular(1.8f), labelColor));
        box.add(SAVI.text(amount, SAVI.bold(3f), valueColor));
        return box;
    }

    static Container memberCard(String name, String handle, double total, double missed, int avatarColor,
                                ActionListener<ActionEvent> onViewAll) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2f, 2f);
        setMargins(card, 0.5f, 1f, 2, 2);

        // header: avatar + name on the left, view all on the right
        Container nameCont = new Container(BoxLayout.y());
        nameCont.add(SAVI.text(name, SAVI.bold(3f), SAVI.DARK_KNIGHT));
        nameCont.add(SAVI.text("@" + handle, SAVI.regular(2f), SAVI.SLATE_GRAY));
        setMargins(nameCont, 0, 0, 1.5f, 0);

        Container left = new Container(new FlowLayout());
        left.add(avatar(avatarColor)).add(nameCont);

        Button viewAll = new Button("View all transactions");
        viewAll.setUIID("Label");
        viewAll.getAllStyles().setFgColor(SAVI.BRIGHT_VIOLET);
        viewAll.getAllStyles().setFont(SAVI.semiBold(1.8f));
        viewAll.setTextPosition(Component.LEFT);
        FontImage.setMaterialIcon(viewAll, FontImage.MATERIAL_ARROW_FORWARD, 2f);
        viewAll.addActionListener(onViewAll);

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.WEST, left);
        header.add(BorderLayout.EAST, BorderLayout.north(viewAll));

        // stats
        boolean hasMissed = missed > 0;
        Container totalBox = statBox("Total Contribution", Home.pesoFormat(total),
                SOFT_GRAY, SOFT_GRAY, SAVI.SLATE_GRAY, SAVI.DARK_KNIGHT);
        Container missedBox = statBox("Missed Contribution", Home.pesoFormat(missed),
                hasMissed ? MISSED_BG : PAID_BG,
                hasMissed ? MISSED_BORDER : PAID_BORDER,
                hasMissed ? MISSED_TEXT : PAID_TEXT,
                hasMissed ? MISSED_TEXT : PAID_TEXT);

        Container stats = new Container(new GridLayout(1, 2));
        stats.add(totalBox).add(missedBox);

        card.add(header);
        card.add(divider(LINE_GRAY));
        card.add(stats);
        return card;
    }

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(PAGE_BG);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        // status bar area uses the same background instead of white
        base.getTitleArea().getAllStyles().setBgColor(PAGE_BG);
        base.getTitleArea().getAllStyles().setBgTransparency(255);

        // member data (hardcoded for now)
        String[] names = {"Air", "Faye", "Sam"};
        String[] handles = {"rei", "faye", "sami"};
        double[] totals = {130, 150, 95};
        double[] missed = {35, 0, 20};
        int[] avatarColors = {0x6C4BEB, 0xA970F7, 0x5B45E6};

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);

        mainContents.add(groupBanner("December Vacation", "P12,450.00", 4));
        mainContents.add(sectionHeader());

        for (int i = 0; i < names.length; i++) {
            mainContents.add(memberCard(names[i], handles[i], totals[i], missed[i], avatarColors[i],
                    e -> {
                        // TODO: open transactions page
                    }));
        }

        base.add(BorderLayout.NORTH, SAVI.buildHeader("Group Members", () -> new HomePage().show()));
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}