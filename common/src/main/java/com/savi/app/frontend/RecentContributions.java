package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;

public class RecentContributions{

    // same peso handling as the other pages ("P")
    static final String PESO = "P";
    static final float NOTCH = 2.2f;
    static final float BAR_H = 14.5f;

    int darkPurple = 0x7C3AED;
    int palePurple = 0xF4EFFE;
    int white = 0xFFFFFF;
    int slategray = 0x64748B;

    // ------------------------------------------------------------------ helpers

    void styles(Container cont, int bgColor, float padLR, float padTB, float marLR, float marT, float marB) {
        Style contS = cont.getAllStyles();
        contS.setBgTransparency(255);
        contS.setBgColor(bgColor);
        contS.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(SAVI.SLATE_GRAY));
        contS.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        contS.setPadding(padTB, padTB, padLR, padLR);
        contS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        contS.setMargin(marT, marB, marLR, marLR);
    }

    // label with no default padding/margin so spacing is fully controlled by us
    private Label txt(String s, Font f, int color) {
        Label l = SAVI.text(s, f, color);
        Style st = l.getAllStyles();
        st.setMargin(0, 0, 0, 0);
        st.setPadding(0, 0, 0, 0);
        return l;
    }

    private Container vcenter(Component c) {
        Container w = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        w.add(c);
        return w;
    }

    private Container header() {
        com.codename1.ui.Button back = new com.codename1.ui.Button();
        Style bs = back.getAllStyles();
        bs.setFgColor(SAVI.BLACK);
        bs.setBgTransparency(0);
        bs.setBorder(RoundBorder.create().color(white));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(1, 1, 1, 1);
        bs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 4);
        back.addActionListener(e -> new HomePage().show());

        // invisible spacer, same size as the back button, so the title is truly centered
        Label spacer = new Label();
        spacer.getAllStyles().setMargin(0, 0, 0, 0);
        spacer.setPreferredSize(back.getPreferredSize());

        Label title = txt("Recent Contributions", SAVI.bold(3f), darkPurple);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container header = new Container(new BorderLayout());
        header.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        header.getAllStyles().setMargin(1.5f, 1f, 2, 2);
        header.add(BorderLayout.WEST, back);
        header.add(BorderLayout.CENTER, title);
        header.add(BorderLayout.EAST, spacer);
        return header;
    }

    // purple badge for recent contributions, pale gray badge for older ones
    private Container dateBadge(String day, String dow, boolean recent) {
        int bg = recent ? darkPurple : 0xF1F5F9;
        int fg = recent ? white : 0x475569;

        Label d = txt(day, SAVI.bold(2f), fg);
        Label w = txt(dow, SAVI.bold(1.2f), fg);
        d.getAllStyles().setAlignment(Component.CENTER);
        w.getAllStyles().setAlignment(Component.CENTER);

        Container inner = new Container(BoxLayout.y());
        inner.add(d).add(w);

        Container badge = new Container(new BorderLayout());
        Style s = badge.getAllStyles();
        s.setBgColor(bg);
        s.setBgTransparency(255);
        if (recent) {
            s.setBorder(RoundRectBorder.create().cornerRadius(3));
        } else {
            s.setBorder(RoundRectBorder.create()
                    .cornerRadius(3)
                    .strokeColor(0xE2E8F0)
                    .strokeOpacity(255)
                    .stroke(1, false));
        }
        s.setMargin(0, 0, 0, 0);
        int size = Display.getInstance().convertToPixels(11f);
        badge.setPreferredSize(new com.codename1.ui.geom.Dimension(size, size));
        badge.add(BorderLayout.CENTER, BorderLayout.centerAbsolute(inner));
        return badge;
    }

    private Container contributionCard(String day, String dow, String member,
                                       String amount, String note, boolean recent) {
        Container card = new Container(new BorderLayout());
        styles(card, white, 2f, 1.5f, 2f, 0.5f, 1.5f);

        // title
        Label groupName = txt("December Vacation", SAVI.bold(1.9f), SAVI.BLACK);

        // "Air contributed P10" with the amount in purple
        Container who = new Container(BoxLayout.x());
        who.add(txt(member + " contributed ", SAVI.regular(1.8f), SAVI.BLACK));
        who.add(txt(amount, SAVI.regular(1.8f), darkPurple));
        who.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        who.getAllStyles().setMargin(0.4f, 0, 0, 0);

        // note row: small bubble icon + "Note: ..."
        Label noteIcon = new Label();
        noteIcon.getAllStyles().setFgColor(slategray);
        noteIcon.getAllStyles().setBgTransparency(0);
        noteIcon.getAllStyles().setMargin(0, 0, 0, 0);
        noteIcon.getAllStyles().setPadding(0, 0, 0, 0);
        FontImage.setMaterialIcon(noteIcon, FontImage.MATERIAL_COMMENT, 2f);

        Label noteTxt = txt(" Note: " + note, SAVI.regular(1.5f), slategray);
        Container noteRow = new Container(BoxLayout.x());
        noteRow.add(noteIcon).add(noteTxt);
        noteRow.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        noteRow.getAllStyles().setMargin(0.4f, 0, 0, 0);

        Container info = new Container(BoxLayout.y());
        info.add(groupName).add(who).add(noteRow);
        info.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        info.getAllStyles().setMargin(0, 0, 2, 0);

        card.add(BorderLayout.WEST, vcenter(dateBadge(day, dow, recent)));
        card.add(BorderLayout.CENTER, info);
        return card;
    }

    // ------------------------------------------------------------------ bottom navigation

    private Container navItem(char icon, String label, boolean active, ActionListener<ActionEvent> onClick) {
        com.codename1.ui.Button b = new com.codename1.ui.Button(label);
        b.setUIID("Label");
        Style s = b.getAllStyles();
        s.setFgColor(active ? darkPurple : SAVI.BLACK);
        s.setFont(SAVI.bold(1.2f));
        s.setAlignment(Component.CENTER);
        s.setBgTransparency(0);
        s.setMargin(0, 0, 0, 0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(0.5f, 0.5f, 0, 0);
        FontImage.setMaterialIcon(b, icon, 4f);   // icon takes the fg color set above
        b.setTextPosition(Component.BOTTOM);
        if (onClick != null) {
            b.addActionListener(onClick);
        }
        return BorderLayout.center(b);
    }

    private Container fab() {
        com.codename1.ui.Button plus = new com.codename1.ui.Button();
        Style s = plus.getAllStyles();
        s.setFgColor(white);
        s.setBgTransparency(0);
        s.setBorder(RoundBorder.create().color(darkPurple));
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1.6f, 1.6f, 1.6f, 1.6f);
        s.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(plus, FontImage.MATERIAL_ADD, 6f);
        // TODO: open the add contribution page
        return FlowLayout.encloseCenterMiddle(plus);
    }

    private Container bottomNav() {
        // the nav paints its own background: the bar + the lavender cup behind the + button
        Container nav = new Container(new BorderLayout()) {
            @Override
            public void paintBackground(Graphics g) {
                Display d = Display.getInstance();
                int x = getX(), y = getY(), w = getWidth(), h = getHeight();
                int notch = d.convertToPixels(NOTCH);
                int barTop = y + notch;

                g.setAntiAliased(true);
                g.setAlpha(255);

                // bar + thin top line
                g.setColor(0xF8FAFC);
                g.fillRect(x, barTop, w, h - notch);
                g.setColor(0xE2E8F0);
                g.drawLine(x, barTop, x + w, barTop);

                // lavender cup (a soft shadow first, then the cup)
                int r = d.convertToPixels(6.7f);
                int cx = x + w / 2;
                int cy = barTop + d.convertToPixels(4.6f);
                g.setColor(0x64748B);
                g.setAlpha(70);
                g.fillArc(cx - r, cy - r + d.convertToPixels(0.5f), 2 * r, 2 * r, 0, 360);
                g.setAlpha(255);
                g.setColor(0xE9E0FB);
                g.fillArc(cx - r, cy - r, 2 * r, 2 * r, 0, 360);
            }
        };
        nav.setPreferredH(Display.getInstance().convertToPixels(NOTCH + BAR_H));

        Container items = new Container(new GridLayout(1, 5));
        items.add(navItem(FontImage.MATERIAL_HOME, "Home", true, e -> new HomePage().show()));
        items.add(navItem(FontImage.MATERIAL_CALENDAR_TODAY, "Calendar", false, null));   // TODO
        items.add(fab());
        items.add(navItem(FontImage.MATERIAL_PEOPLE_OUTLINE, "People", false, null));     // TODO
        items.add(navItem(FontImage.MATERIAL_PERSON_OUTLINE, "User", false, null));       // TODO

        nav.add(BorderLayout.CENTER, items);
        return nav;
    }

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(palePurple);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        // sample data: {day, weekday, member, amount, note, recent?}
        // TODO: replace with the contributions from the backend
        String[][] data = {
                {"16", "WED", "Air", PESO + "10", "", "true"},
                {"16", "WED", "Sam", PESO + "10", "", "true"},
                {"16", "WED", "Faye", PESO + "10", "", "true"},
                {"16", "WED", "Shy", PESO + "10", "", "true"},
                {"16", "WED", "Aron", PESO + "10", "", "true"},
                {"13", "SUN", "Aron", PESO + "10", "", "false"},
                {"13", "SUN", "Faye", PESO + "10", "", "false"},
        };

        Container list = new Container(BoxLayout.y());
        list.setScrollableY(true);
        for (String[] row : data) {
            list.add(contributionCard(row[0], row[1], row[2], row[3], row[4], row[5].equals("true")));
        }

        base.add(BorderLayout.NORTH, header());
        base.add(BorderLayout.CENTER, list);
        base.add(BorderLayout.SOUTH, bottomNav());
        base.show();
    }
}