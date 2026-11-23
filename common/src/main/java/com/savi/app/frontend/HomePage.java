package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;
import com.savi.app.backend.GroupSavings;
import com.savi.app.backend.Home;

import java.time.LocalDate;

public class HomePage {

    // CALENDAR: the date, day letter and "is it today" now come from Home (backend)
    static Container dayCell(LocalDate d){
        boolean today = Home.isToday(d);

        int textColor = today ? SAVI.DARK_DARK_PURPLE : SAVI.WHITE;
        Label num = SAVI.text(Home.getDayNumber(d), today ? SAVI.bold(1.8f) : SAVI.regular(1.8f), textColor);
        Label dayLett = SAVI.text(Home.getDayLetter(d), today ? SAVI.bold(1.5f) : SAVI.regular(1.5f), textColor);
        num.getAllStyles().setAlignment(Component.CENTER);
        dayLett.getAllStyles().setAlignment(Component.CENTER);

        Container cell = new Container(BoxLayout.y());
        cell.add(num).add(dayLett);

        Style s = cell.getAllStyles();
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(0.3f, 0.3f, 0.3f,0.3f);
        s.setMargin(1, 1, 1, 1);
        if(today){
            s.setBgColor(SAVI.WHITE);
            s.setBgTransparency(255);
            s.setBorder(RoundRectBorder.create().cornerRadius(3f));
        }
        return cell;
    }

    static void shortcutSize(Container title){
        title.setPreferredH(Display.getInstance().convertToPixels(16f));
        title.setPreferredW(Display.getInstance().convertToPixels(16f));
    }

    static void shortcutsStyle(Container cont){
        Style s = cont.getAllStyles();
        s.setBgColor(SAVI.WHITE);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(3).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(SAVI.DARK_PURPLE));
        s.setMarginUnit(Style.UNIT_TYPE_DIPS);
        s.setMargin(0, 0, 1, 0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1, 1, 0.5f, 0.5f);
    }

    static Container makeShortcut(String title, float mm, ActionListener<ActionEvent> onClick, String imgPath) {
        Container c = new Container(new BorderLayout());
        shortcutSize(c);
        shortcutsStyle(c);

        Button b = new Button(title);
        b.setUIID("Label");
        Style bs = b.getAllStyles();
        bs.setFgColor(SAVI.DEEP_SLATE);
        bs.setFont(SAVI.semiBold(mm));
        bs.setAlignment(Component.CENTER);
        bs.setPadding(0, 0, 0, 0);
        bs.setMargin(0, 0, 0, 0);
        b.setEndsWith3Points(false);
        b.addActionListener(onClick);

        c.add(BorderLayout.SOUTH, b);
        c.add(BorderLayout.NORTH, shortcutImg(imgPath));
        c.setLeadComponent(b);
        return c;
    }

    static Label shortcutImg(String imagePath){
        Label pic = new Label();
        pic.getAllStyles().setAlignment(Component.CENTER);
        try {
            Image img = Image.createImage(imagePath);
            pic.setIcon(img.scaledWidth(Display.getInstance().convertToPixels(8f)));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return pic;
    }

    static void boxStyles(Container name, int color, float paddingTB, float paddingLR){
        Style s = name.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        s.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(SAVI.BRIGHT_VIOLET));
        s.setMarginUnit(Style.UNIT_TYPE_DIPS);
        s.setMargin(0.5f, 0.5f, 2, 2);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(paddingTB, paddingTB, paddingLR, paddingLR);
    }

    // one of the two small boxes inside an overview card: label (+ optional tag) on top, amount below
    static Container amountBox(String label, String amount, String tag,
                               int bg, int stroke, int labelColor, int amountColor, int tagBg) {
        Container top = new Container(new BorderLayout());
        top.add(BorderLayout.WEST, SAVI.text(label, SAVI.semiBold(1.4f), labelColor));
        if (!tag.isEmpty()) {
            Label chip = SAVI.text(tag, SAVI.semiBold(1.2f), labelColor);
            Style cs = chip.getAllStyles();
            cs.setBgColor(tagBg);
            cs.setBgTransparency(255);
            cs.setBorder(RoundRectBorder.create().cornerRadius(1.5f));
            cs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
            cs.setPadding(0.3f, 0.3f, 0.8f, 0.8f);
            cs.setMargin(0, 0, 0, 0);
            top.add(BorderLayout.EAST, chip);
        }

        Container box = new Container(BoxLayout.y());
        box.add(top);
        box.add(SAVI.text(amount, SAVI.bold(2.4f), amountColor));

        Style bs = box.getAllStyles();
        bs.setBgColor(bg);
        bs.setBgTransparency(255);
        bs.setBorder(RoundRectBorder.create().cornerRadius(3f)
                .strokeColor(stroke).strokeOpacity(255).stroke(1, false));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(1.2f, 1.2f, 1.2f, 1.2f);
        bs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        bs.setMargin(0, 0, 0.5f, 0.5f);
        return box;
    }

    // one Savings Overview card (same look for a group and a personal savings)
    static Container overviewCard(Home.OverviewItem item) {
        int accent = item.isPersonal() ? SAVI.GREEN : SAVI.DARK_PURPLE;

        // colored dot + name on the left, "Group" / "Personal" on the right
        Label dot = new Label();
        Style ds = dot.getAllStyles();
        ds.setBgColor(accent);
        ds.setBgTransparency(255);
        ds.setBorder(RoundRectBorder.create().cornerRadius(1.1f));
        ds.setMargin(0, 0, 0, 0);
        ds.setPadding(0, 0, 0, 0);
        int size = Display.getInstance().convertToPixels(2.2f);
        dot.setPreferredSize(new Dimension(size, size));

        Label name = SAVI.text(item.getName(), SAVI.semiBold(2.1f), SAVI.DARK_KNIGHT);
        name.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        name.getAllStyles().setMargin(0, 0, 1.2f, 0);

        Container left = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        left.add(dot).add(name);

        Container head = new Container(new BorderLayout());
        head.add(BorderLayout.WEST, left);
        head.add(BorderLayout.EAST, SAVI.text(item.getTypeLabel(), SAVI.semiBold(1.5f), accent));

        // TOTAL SAVED (always neutral) and REMAINING (amber "Due Soon", green "All Paid", or neutral)
        Container saved = amountBox("TOTAL SAVED", item.getTotalSavedText(), "",
                0xF8FAFC, 0xE2E8F0, SAVI.SLATE_GRAY, SAVI.DARK_KNIGHT, 0);

        String status = item.getStatus();
        Container remaining;
        if (status.equals("Due Soon")) {
            remaining = amountBox("REMAINING", item.getRemainingText(), status,
                    SAVI.PALE_YELLOW, SAVI.PASTEL_YELLOW, SAVI.AMBER, SAVI.AMBER, SAVI.YELLOW_TAG);
        } else if (status.equals("All Paid")) {
            remaining = amountBox("REMAINING", item.getRemainingText(), status,
                    SAVI.PALE_GREEN, 0xA7F3D0, 0x047857, SAVI.GREEN, 0xD1FAE5);
        } else {
            remaining = amountBox("REMAINING", item.getRemainingText(), "",
                    0xF8FAFC, 0xE2E8F0, SAVI.SLATE_GRAY, SAVI.DARK_KNIGHT, 0);
        }

        Container boxes = new Container(new GridLayout(1, 2));
        boxes.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        boxes.getAllStyles().setMargin(1.5f, 0, 0, 0);
        boxes.add(saved).add(remaining);

        Container card = new Container(BoxLayout.y());
        boxStyles(card, SAVI.WHITE, 2f, 2f);
        card.add(head).add(boxes);
        return card;
    }

    public void show() {
        // BACKEND: one Home object feeds the cards below (SAVI.user / SAVI.groupList.getGroups: see note)
        Home home = new Home(SAVI.user, Home.allSavings());
        GroupSavings g = home.getFeaturedGroup();                    // null if the user has no groups

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Container t = base.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        Container weekContainer = new Container(new BorderLayout());
        boxStyles(weekContainer, SAVI.DARK_PURPLE, 1.5f, 1.5f);
        weekContainer.getAllStyles().setMarginBottom(0.4f);
        weekContainer.getAllStyles().setMarginTop(1f);

        Button thisWeek = new Button("THIS WEEK");
        thisWeek.setUIID("Label");                         // removes the default button look
        thisWeek.getAllStyles().setFont(SAVI.bold(2.5f));
        thisWeek.getAllStyles().setFgColor(SAVI.WHITE);
        thisWeek.addActionListener(e -> new CalendarPage().show());
        Label icon = new Label();
        icon.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(icon, FontImage.MATERIAL_DATE_RANGE, 4f);
        Container headerLeft = new Container(new FlowLayout());
        headerLeft.add(icon).add(thisWeek);
        weekContainer.setLeadComponent(thisWeek);

        // CALENDAR: the 7 days (Sunday to Saturday) of the current week come from Home
        Container daysRow = new Container(new GridLayout(1, 7));
        for (LocalDate d : Home.getWeekDays(LocalDate.now())) {
            daysRow.add(dayCell(d));
        }
        daysRow.getAllStyles().setMarginTop(2);

        // current month, top right
        Label monthLabel = SAVI.text(Home.getMonthName(),
                SAVI.semiBold(2f), SAVI.WHITE);
        Container headerRight = new Container(new FlowLayout(Component.RIGHT, Component.CENTER));
        headerRight.add(monthLabel);

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.WEST, headerLeft);
        header.add(BorderLayout.EAST, headerRight);

        weekContainer.add(BorderLayout.NORTH, header);
        weekContainer.add(BorderLayout.CENTER, daysRow);

        Container shortcutsContainer = new Container(new GridLayout(1, 4));
        Style styleShortC = shortcutsContainer.getAllStyles();
        styleShortC.setMarginUnit(Style.UNIT_TYPE_DIPS);
        styleShortC.setMargin(0.5f, 0.5f, 2, 2);

        float shortcutTitleSize = 1.5f;

        shortcutsContainer.add(makeShortcut("Transfer", shortcutTitleSize, e -> new TransferPage().show(), "/Transfer_Icon.png"));
        shortcutsContainer.add(makeShortcut("Summary", shortcutTitleSize, e -> new SummaryPage().show(), "/Summary_Icon.png"));
        shortcutsContainer.add(makeShortcut("Pending", shortcutTitleSize, e -> new PendingPage().show(), "/Pending_Icon.png"));
        shortcutsContainer.add(makeShortcut("Reminders", shortcutTitleSize, e -> new ReminderPage().show(), "/Reminders_Icon.png"));

        Container GroupOverviewContainer = new Container(new BorderLayout());
        GroupOverviewContainer.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        GroupOverviewContainer.getAllStyles().setMargin(0.5f, 0.5f, 2, 2);
        Label groupOverviewText = SAVI.text("Savings Overview", SAVI.bold(2f), SAVI.DARK_BLUE);

        Button manage = new Button("Manage");
        manage.getAllStyles().setFgColor(SAVI.DARK_PURPLE);
        manage.getAllStyles().setFont(SAVI.semiBold(2f));
        manage.addActionListener(e->{new PeoplePage().show();});

        Container overviewTitle = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        overviewTitle.add(groupOverviewText);

        GroupOverviewContainer.add(BorderLayout.WEST, overviewTitle);
        GroupOverviewContainer.add(BorderLayout.EAST, manage);

        // SAVINGS OVERVIEW CARDS: at most 2 (groups and/or personal), newest activity first
        java.util.List<Home.OverviewItem> overviews = home.getOverviews();
        Container overviewCards = new Container(BoxLayout.y());
        for (Home.OverviewItem item : overviews) {
            overviewCards.add(overviewCard(item));
        }

        Container recentsTitle = new Container(new BorderLayout());
        recentsTitle.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        recentsTitle.getAllStyles().setMargin(0.5f, 0.5f, 2, 2);
        Label recentContText = SAVI.text("Recent Contributions", SAVI.semiBold(2f), SAVI.DARK_BLUE);

        Button seeAll = new Button("See All");
        seeAll.getAllStyles().setFgColor(SAVI.DARK_PURPLE);
        seeAll.getAllStyles().setFont(SAVI.semiBold(2f));
        seeAll.addActionListener(e-> {new RecentContributions().show();});

        recentsTitle.add(BorderLayout.WEST, recentContText);
        recentsTitle.add(BorderLayout.EAST, seeAll);

        // RECENT CONTRIBUTION CARDS: the 3 latest, built with the same card the Recent
        // Contributions page uses (RecentContributions.contributionCard)
        java.util.List<Home.RecentContribution> latest = home.getRecentContributions(3);
        LocalDate newest = latest.isEmpty() ? null : latest.get(0).getDate();
        RecentContributions cardMaker = new RecentContributions();

        Container recentCards = new Container(BoxLayout.y());
        for (Home.RecentContribution c : latest) {
            boolean isNewestDay = c.getDate().equals(newest);                 // purple badge = newest day
            String dow = c.getWeekday().substring(0, 3).toUpperCase();        // "Thursday" -> "THU"
            recentCards.add(cardMaker.contributionCard(c.getDayNumber(), dow, c.getGroupName(),
                    c.getMember(), Home.pesoShort(c.getAmount()), c.getNote(), isNewestDay));
        }

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        mainContents.add(weekContainer);
        mainContents.add(shortcutsContainer);
        mainContents.add(GroupOverviewContainer);
        if (!overviews.isEmpty()) {            // overview cards only when the user has savings
            mainContents.add(overviewCards);
        }
        mainContents.add(recentsTitle);
        if (!latest.isEmpty()) {               // cards only when there are contributions
            mainContents.add(recentCards);
        }
        base.add(BorderLayout.SOUTH, SAVI.bottomNav(SAVI.NAV_HOME));
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}