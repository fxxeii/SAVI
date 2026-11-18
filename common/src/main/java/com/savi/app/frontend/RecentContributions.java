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
import com.savi.app.backend.Home;

import java.time.LocalDate;

public class RecentContributions{

    // same peso handling as the other pages ("P")
    static final String PESO = "P";
    static final float NOTCH = 2.2f;
    static final float BAR_H = 14.5f;

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
        bs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(1, 1, 1, 1);
        bs.setMargin(0, 0, 2, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 4);
        back.addActionListener(e -> new HomePage().show());

        Label title = txt("Recent Contributions", SAVI.bold(3f), SAVI.WHITE);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container headerText = new Container(new BorderLayout());
        styles(headerText, SAVI.DARK_PURPLE, 1, 1, 2, 1, 1.5f);
        headerText.add(BorderLayout.CENTER, title);

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.WEST, back);
        header.add(BorderLayout.CENTER, headerText);
        return header;
    }

    // purple badge for recent contributions, pale gray badge for older ones
    private Container dateBadge(String day, String dow, boolean recent) {
        int bg = recent ? SAVI.DARK_PURPLE : 0xF1F5F9;
        int fg = recent ? SAVI.WHITE : 0x475569;

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

    private Container contributionCard(String day, String dow, String group, String member,
                                       String amount, String note, boolean recent) {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.WHITE, 2f, 1.5f, 2f, 0.5f, 1.5f);

        // title
        Label groupName = txt(group, SAVI.bold(1.9f), SAVI.BLACK);

        // "Air contributed P10" with the amount in purple
        Container who = new Container(BoxLayout.x());
        who.add(txt(member + " contributed ", SAVI.regular(1.8f), SAVI.BLACK));
        who.add(txt(amount, SAVI.regular(1.8f), SAVI.DARK_PURPLE));
        who.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        who.getAllStyles().setMargin(0.4f, 0, 0, 0);

        // note row: small bubble icon + "Note: ..."
        Label noteIcon = new Label();
        noteIcon.getAllStyles().setFgColor(SAVI.SLATE_GRAY);
        noteIcon.getAllStyles().setBgTransparency(0);
        noteIcon.getAllStyles().setMargin(0, 0, 0, 0);
        noteIcon.getAllStyles().setPadding(0, 0, 0, 0);
        FontImage.setMaterialIcon(noteIcon, FontImage.MATERIAL_COMMENT, 2f);

        Label noteTxt = txt(" Note: " + note, SAVI.regular(1.5f), SAVI.SLATE_GRAY);
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
        s.setFgColor(active ? SAVI.DARK_PURPLE : SAVI.BLACK);
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
        s.setFgColor(SAVI.WHITE);
        s.setBgTransparency(0);
        s.setBorder(RoundBorder.create().color(SAVI.DARK_PURPLE));
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1.6f, 1.6f, 1.6f, 1.6f);
        s.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(plus, FontImage.MATERIAL_ADD, 6f);
        // TODO: open the add contribution page
        return FlowLayout.encloseCenterMiddle(plus);
    }

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Container list = new Container(BoxLayout.y());
        list.setScrollableY(true);

        Home home = new Home(SAVI.user, SAVI.groups);
        java.util.List<Home.RecentContribution> items = home.getRecentContributions();
        LocalDate newest = items.isEmpty() ? null : items.get(0).getDate();

        for (Home.RecentContribution c : items) {
            boolean recent = c.getDate().equals(newest);              // purple badge = newest day
            String dow = c.getWeekday().substring(0, 3).toUpperCase(); // "Wednesday" -> "WED"
            list.add(contributionCard(c.getDayNumber(), dow, c.getGroupName(), c.getMember(),
                    Home.pesoShort(c.getAmount()), c.getNote(), recent));
        }

        base.add(BorderLayout.NORTH, header());
        base.add(BorderLayout.CENTER, list);
        base.show();
    }
}