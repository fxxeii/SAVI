package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
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
                shadowColor(SAVI.SLATE_GRAY));
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

    // Small round white-bordered chevron matching SummaryPage & PendingPage
    private static Label chevron(int iconColor) {
        Label chev = new Label();
        Style cs = chev.getAllStyles();
        cs.setFgColor(iconColor);
        cs.setBgTransparency(0);
        cs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        cs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        cs.setPadding(0.3f, 0.3f, 0.3f, 0.3f);
        cs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(chev, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 3f);
        return chev;
    }

    // TOP DROPDOWN PILL: switches between Personal Savings and Group Savings (matches SummaryPage & PendingPage)
    static Container peopleHeader(boolean personalMode) {
        final String[] modes = {"Personal Savings", "Group Savings"};
        String current = personalMode ? modes[0] : modes[1];

        Button back = new Button();
        Style bs = back.getAllStyles();
        bs.setFgColor(SAVI.DARK_PURPLE);
        bs.setBgTransparency(0);
        bs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(0.6f, 0.6f, 0.6f, 0.6f);
        bs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 3.5f);
        back.addActionListener(e -> new HomePage().show());

        final Picker modePicker = new Picker();
        modePicker.setUIID("Label");
        modePicker.setType(Display.PICKER_TYPE_STRINGS);
        modePicker.setStrings(modes);
        modePicker.setSelectedString(current);
        Style st = modePicker.getAllStyles();
        st.setFont(SAVI.bold(2.2f));
        st.setFgColor(SAVI.WHITE);
        st.setBgTransparency(0);
        st.setBorder(Border.createEmpty());
        st.setMargin(0, 0, 0, 0);
        st.setPadding(0, 0, 0, 0);
        st.setAlignment(Component.CENTER);

        modePicker.addActionListener(e -> {
            String chosen = modePicker.getSelectedString();
            if (chosen != null && !chosen.equalsIgnoreCase(current)) {
                PeopleBackend.setPersonalMode(chosen.equals(modes[0]));
                new PeoplePage().show();
            }
        });

        Container titleArea = new Container(new BorderLayout());
        titleArea.add(BorderLayout.CENTER, modePicker);
        titleArea.add(BorderLayout.EAST, chevron(SAVI.DARK_PURPLE));
        titleArea.setLeadComponent(modePicker);

        Container pill = new Container(new BorderLayout());
        Style ps = pill.getAllStyles();
        ps.setBgColor(SAVI.DARK_PURPLE);
        ps.setBgTransparency(255);
        ps.setBorder(RoundRectBorder.create().cornerRadius(4f).shadowOpacity(50).shadowBlur(10)
                .shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(SAVI.SLATE_GRAY));
        ps.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ps.setPadding(1.5f, 1.5f, 1.5f, 1.5f);
        ps.setMarginUnit(Style.UNIT_TYPE_DIPS);
        ps.setMargin(1f, 0.4f, 2f, 2f);
        pill.add(BorderLayout.WEST, back);
        pill.add(BorderLayout.CENTER, titleArea);

        Container wrap = new Container(new BorderLayout());
        wrap.add(BorderLayout.CENTER, pill);
        return wrap;
    }

    // SECOND PURPLE CARD: matches SummaryPage & PendingPage (tracker Picker + Total Pool + Contributors/Target)
    static Container trackerBanner(String name, String pool, int contributors, double targetAmount, boolean personalMode) {
        Container banner = new Container(new BorderLayout());
        cardStyles(banner, SAVI.DARK_PURPLE, 4f, 4f, 3f);
        setMargins(banner, 0.5f, 0.5f, 2f, 2f);

        String[] rawTrackers = PeopleBackend.getTrackerNames();
        final String[] options;
        if (rawTrackers == null || rawTrackers.length == 0) {
            options = new String[]{name.toUpperCase()};
        } else {
            options = new String[rawTrackers.length];
            for (int i = 0; i < rawTrackers.length; i++) {
                options[i] = rawTrackers[i].toUpperCase();
            }
        }

        final Picker trackerPicker = new Picker();
        trackerPicker.setUIID("Label");
        trackerPicker.setType(Display.PICKER_TYPE_STRINGS);
        trackerPicker.setStrings(options);
        trackerPicker.setSelectedString(name.toUpperCase());
        Style pst = trackerPicker.getAllStyles();
        pst.setFont(SAVI.bold(2.5f));
        pst.setFgColor(SAVI.WHITE);
        pst.setBgTransparency(0);
        pst.setBorder(Border.createEmpty());
        pst.setMargin(0, 0, 0, 0);
        pst.setPadding(0, 0, 0, 0);

        trackerPicker.addActionListener(e -> {
            String picked = trackerPicker.getSelectedString();
            if (picked != null && !picked.trim().isEmpty() && !picked.equalsIgnoreCase(name)) {
                if (personalMode) {
                    PeopleBackend.setSelectedPersonal(picked.trim());
                } else {
                    PeopleBackend.setSelectedGroup(picked.trim());
                }
                new PeoplePage().show();
            }
        });

        Container topRow = new Container(new BorderLayout());
        topRow.add(BorderLayout.WEST, trackerPicker);
        topRow.add(BorderLayout.EAST, chevron(SAVI.DARK_PURPLE));
        topRow.setLeadComponent(trackerPicker);

        Container poolCont = new Container(BoxLayout.y());
        Label poolLbl = SAVI.text("Total Pool", SAVI.regular(2f), SAVI.WHITE);
        Label poolVal = SAVI.text(pool, SAVI.bold(2.6f), SAVI.WHITE);
        poolLbl.getAllStyles().setMargin(0, 0, 0, 0);
        poolVal.getAllStyles().setMargin(0, 0, 0, 0);
        poolCont.add(poolLbl);
        poolCont.add(poolVal);

        Container rightCont = new Container(BoxLayout.y());
        if (personalMode) {
            Label targetLbl = SAVI.text("Target Amount", SAVI.regular(2f), SAVI.WHITE);
            targetLbl.getAllStyles().setAlignment(Component.RIGHT);
            targetLbl.getAllStyles().setMargin(0, 0, 0, 0);
            Label targetVal = SAVI.text(Home.pesoFormat(targetAmount), SAVI.bold(2.6f), SAVI.WHITE);
            targetVal.getAllStyles().setMargin(0, 0, 0, 0);
            rightCont.add(targetLbl);
            rightCont.add(FlowLayout.encloseRight(targetVal));
        } else {
            Label memText = SAVI.text("Members", SAVI.regular(2f), SAVI.WHITE);
            memText.getAllStyles().setAlignment(Component.RIGHT);
            memText.getAllStyles().setMargin(0, 0, 0, 0);
            Label countLbl = SAVI.text(contributors + (contributors == 1 ? " Contributor" : " Contributors"),
                    SAVI.semiBold(2.3f), SAVI.WHITE);
            countLbl.getAllStyles().setMargin(0, 0, 0, 0);
            rightCont.add(memText);
            rightCont.add(FlowLayout.encloseRight(countLbl));
        }

        Container bottomRow = new Container(new BorderLayout());
        setMargins(bottomRow, 1.5f, 0, 0, 0);
        bottomRow.add(BorderLayout.WEST, poolCont);
        bottomRow.add(BorderLayout.EAST, rightCont);

        banner.add(BorderLayout.NORTH, topRow);
        banner.add(BorderLayout.SOUTH, bottomRow);
        return banner;
    }

    static Container sectionHeader(boolean showUpdated, boolean personalMode) {
        Container header = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(header, 1, 0.5f, 2, 2);

        Label title = SAVI.text(personalMode ? "PERSONAL CONTRIBUTIONS" : "MEMBER CONTRIBUTIONS",
                SAVI.bold(2f), SAVI.SLATE_GRAY);
        header.add(BorderLayout.WEST, title);

        if (showUpdated) {
            Container updated = pill(SAVI.text("Updated Today", SAVI.regular(1.8f), SAVI.BRIGHT_VIOLET),
                    SAVI.PALE_PURPLE, SAVI.PALE_PURPLE);
            header.add(BorderLayout.EAST, updated);
        }
        return header;
    }

    static Container emptyState(boolean personalMode) {
        Container card = new Container(BoxLayout.y());
        cardStyles(card, SAVI.WHITE, 4, 3.5f, 2.5f);
        setMargins(card, 1f, 1f, 2, 2);

        Container icon = circleIcon(personalMode ? FontImage.MATERIAL_PERSON : FontImage.MATERIAL_GROUP,
                16f, 8f, SAVI.PALE_PURPLE, SAVI.BRIGHT_VIOLET, false);

        Label title = SAVI.text(
                personalMode ? "No personal savings yet" : "No group contributions yet",
                SAVI.bold(2.6f), SAVI.DARK_KNIGHT);
        title.getAllStyles().setAlignment(Component.CENTER);
        setMargins(title, 2f, 0.5f, 0, 0);

        Label line1 = SAVI.text("Tap the + button below to add", SAVI.regular(2f), SAVI.SLATE_GRAY);
        line1.getAllStyles().setAlignment(Component.CENTER);
        Label line2 = SAVI.text(
                personalMode ? "a personal deposit. Your progress" : "a contribution. Your members will",
                SAVI.regular(2f), SAVI.SLATE_GRAY);
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
        boolean personalMode = PeopleBackend.isPersonalMode();

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        base.getTitleArea().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getTitleArea().getAllStyles().setBgTransparency(255);

        Container t = base.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        PeopleBackend.Member[] members = PeopleBackend.getMembers();
        if (members == null) {
            members = new PeopleBackend.Member[0];
        }
        boolean hasMembers = members.length > 0;

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        setPadding(mainContents, 0, 3f, 0, 0);

        mainContents.add(trackerBanner(
                PeopleBackend.getGroupName(),
                Home.pesoFormat(PeopleBackend.getGroupPool()),
                members.length,
                PeopleBackend.getTargetAmount(),
                personalMode
        ));
        mainContents.add(sectionHeader(hasMembers, personalMode));

        if (!hasMembers) {
            mainContents.add(emptyState(personalMode));
        } else {
            for (int i = 0; i < members.length; i++) {
                final PeopleBackend.Member member = members[i];
                mainContents.add(memberCard(member, e -> new ViewAllTransacPage(member).show()));
            }
        }

        base.add(BorderLayout.NORTH, peopleHeader(personalMode));
        base.add(BorderLayout.CENTER, mainContents);

        base.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_PEOPLE));
        base.show();
    }
}