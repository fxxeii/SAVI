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
import com.codename1.ui.spinner.Picker;
import com.savi.app.SAVI;
import com.savi.app.backend.Home;
import com.savi.app.backend.PeopleBackend;

public class PeoplePage {

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

    static Container header(String title) {
        Label l = SAVI.text(title, SAVI.bold(2.6f), SAVI.DARK_KNIGHT);
        l.getAllStyles().setAlignment(Component.CENTER);

        Container header = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(header, 1.5f, 1f, 2, 2);
        header.add(BorderLayout.CENTER, l);
        return header;
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
                    g.fillLinearGradient(SAVI.DARK_PURPLE, SAVI.DARK_DARK_PURPLE, getX(), getY(), getWidth(), getHeight(), true);
                    g.popClip();
                }
            }
        };
        cardStyles(banner, SAVI.DARK_PURPLE, radiusMM, 2.5f, 2.5f);
        setMargins(banner, 0.5f, 1, 2, 2);

        Label groupName = SAVI.text(name.toUpperCase(), SAVI.bold(3.5f), SAVI.WHITE);

        String[] rawGroups = PeopleBackend.getUserGroupNames();
        final String[] options;
        if (rawGroups == null || rawGroups.length == 0) {
            options = new String[]{name.toUpperCase()};
        } else {
            options = new String[rawGroups.length];
            for (int i = 0; i < rawGroups.length; i++) {
                options[i] = rawGroups[i].toUpperCase();
            }
        }

        final Picker groupPicker = new Picker();
        groupPicker.setType(Display.PICKER_TYPE_STRINGS);
        groupPicker.setStrings(options);
        groupPicker.setSelectedString(name.toUpperCase());
        groupPicker.setUIID("Label");
        groupPicker.setText("");
        groupPicker.getAllStyles().setFgColor(SAVI.WHITE);
        groupPicker.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(groupPicker, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 3f);

        groupPicker.addActionListener(e -> {
            String picked = groupPicker.getSelectedString();
            groupPicker.setText("");
            if (picked != null && !picked.trim().isEmpty() && !picked.equalsIgnoreCase(name)) {
                PeopleBackend.setSelectedGroup(picked.trim());
                new PeoplePage().show();
            }
        });

        Container chevron = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        chevron.setPreferredW(Display.getInstance().convertToPixels(6f));
        chevron.setPreferredH(Display.getInstance().convertToPixels(6f));
        Style cs = chevron.getAllStyles();
        cs.setBgColor(SAVI.BRIGHT_VIOLET);
        cs.setBgTransparency(255);
        cs.setBorder(RoundRectBorder.create().cornerRadius(3f));
        chevron.add(BorderLayout.CENTER, groupPicker);
        chevron.setLeadComponent(groupPicker);

        Container titleRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        titleRow.add(BorderLayout.WEST, groupName);
        titleRow.add(BorderLayout.EAST, chevron);

        Container poolCont = new Container(BoxLayout.y());
        poolCont.add(SAVI.text("Total Pool", SAVI.regular(2f), SAVI.PASTEL_LAVENDER));
        poolCont.add(SAVI.text(pool, SAVI.bold(4f), SAVI.WHITE));

        Container memCont = new Container(BoxLayout.y());
        Label memText = SAVI.text("Members", SAVI.regular(2f), SAVI.PASTEL_LAVENDER);
        memText.getAllStyles().setAlignment(Component.RIGHT);
        Container memPill = pill(SAVI.text(contributors + " Contributors", SAVI.semiBold(2f), SAVI.WHITE),
                SAVI.BRIGHT_VIOLET, SAVI.BRIGHT_VIOLET);
        memCont.add(memText);
        memCont.add(memPill);

        Container infoRow = new Container(new BorderLayout());
        infoRow.add(BorderLayout.WEST, poolCont);
        infoRow.add(BorderLayout.EAST, memCont);

        banner.add(titleRow);
        banner.add(divider(SAVI.BRIGHT_VIOLET));
        banner.add(infoRow);
        return banner;
    }

    static Container sectionHeader(boolean showUpdated) {
        Container header = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(header, 1, 0.5f, 2, 2);

        Label title = SAVI.text("MEMBER CONTRIBUTIONS", SAVI.bold(2f), SAVI.SLATE_GRAY);
        header.add(BorderLayout.WEST, title);

        if (showUpdated) {
            Container updated = pill(SAVI.text("Updated Today", SAVI.regular(1.8f), SAVI.BRIGHT_VIOLET),
                    SAVI.PALE_PURPLE, SAVI.PALE_PURPLE);
            header.add(BorderLayout.EAST, updated);
        }
        return header;
    }

    static Container emptyState() {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 3.5f, 2.5f);
        setMargins(card, 1f, 1f, 2, 2);

        Container icon = circleIcon(FontImage.MATERIAL_GROUP, 16f, 8f, SAVI.PALE_PURPLE, SAVI.BRIGHT_VIOLET, false);

        Label title = SAVI.text("No group contributions yet", SAVI.bold(2.6f), SAVI.DARK_KNIGHT);
        title.getAllStyles().setAlignment(Component.CENTER);
        setMargins(title, 2f, 0.5f, 0, 0);

        Label line1 = SAVI.text("Tap the + button below to add", SAVI.regular(2f), SAVI.SLATE_GRAY);
        line1.getAllStyles().setAlignment(Component.CENTER);
        Label line2 = SAVI.text("a contribution. Your members will", SAVI.regular(2f), SAVI.SLATE_GRAY);
        line2.getAllStyles().setAlignment(Component.CENTER);
        Label line3 = SAVI.text("show up right here.", SAVI.regular(2f), SAVI.SLATE_GRAY);
        line3.getAllStyles().setAlignment(Component.CENTER);

        card.add(FlowLayout.encloseCenter(icon));
        card.add(title);
        card.add(line1);
        card.add(line2);
        card.add(line3);
        return card;
    }

    static Container avatar() {
        Container av = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        av.setPreferredW(Display.getInstance().convertToPixels(12f));
        av.setPreferredH(Display.getInstance().convertToPixels(12f));
        Style s = av.getAllStyles();
        s.setBgColor(SAVI.BRIGHT_VIOLET);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(3.5f).
                shadowOpacity(50).
                shadowBlur(8).
                shadowSpread(1f).
                shadowY(0.4f).
                shadowColor(SAVI.BRIGHT_VIOLET));

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

    static Container memberCard(PeopleBackend.Member m, ActionListener<ActionEvent> onViewAll) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 2f, 2f);
        setMargins(card, 0.5f, 1f, 2, 2);

        Container nameCont = new Container(BoxLayout.y());
        nameCont.add(SAVI.text(m.name, SAVI.bold(3f), SAVI.DARK_KNIGHT));
        nameCont.add(SAVI.text("@" + m.handle, SAVI.regular(2f), SAVI.SLATE_GRAY));
        setMargins(nameCont, 0, 0, 1.5f, 0);

        Container left = new Container(new FlowLayout());
        left.add(avatar()).add(nameCont);

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

        boolean hasMissed = m.missed > 0;
        Container totalBox = statBox("Total Contribution", Home.pesoFormat(m.totalPaid),
                SAVI.POWDER_WHITE, SAVI.BORDER_COLOR, SAVI.SLATE_GRAY, SAVI.DARK_KNIGHT);
        Container missedBox = statBox("Missed Contribution", Home.pesoFormat(m.missed),
                hasMissed ? SAVI.PALE_RED : SAVI.PALE_GREEN,
                hasMissed ? SAVI.RED : SAVI.GREEN,
                hasMissed ? SAVI.RED : SAVI.GREEN,
                hasMissed ? SAVI.RED : SAVI.GREEN);

        Container stats = new Container(new GridLayout(1, 2));
        stats.add(totalBox).add(missedBox);

        card.add(header);
        card.add(divider(SAVI.BORDER_COLOR));
        card.add(stats);
        return card;
    }

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.POWDER_WHITE);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        base.getTitleArea().getAllStyles().setBgColor(SAVI.POWDER_WHITE);
        base.getTitleArea().getAllStyles().setBgTransparency(255);

        PeopleBackend.Member[] members = PeopleBackend.getMembers();
        if (members == null) {
            members = new PeopleBackend.Member[0];
        }
        boolean hasMembers = members.length > 0;

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);

        mainContents.add(groupBanner(PeopleBackend.getGroupName(), Home.pesoFormat(PeopleBackend.getGroupPool()), members.length));
        mainContents.add(sectionHeader(hasMembers));

        if (!hasMembers) {
            mainContents.add(emptyState());
        } else {
            for (int i = 0; i < members.length; i++) {
                final PeopleBackend.Member member = members[i];
                mainContents.add(memberCard(member, e -> new ViewAllTransacPage(member).show()));
            }
        }

        base.add(BorderLayout.NORTH, header("Group Members"));
        base.add(BorderLayout.CENTER, mainContents);

        base.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_PEOPLE));
        base.show();
    }
}