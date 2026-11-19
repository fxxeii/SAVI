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

    static Component progressBar(float progress, int trackColor, int fillColor, float heightMM) {
        final float p = Math.max(0f, Math.min(1f, progress));

        return new Component() {
            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(
                        Display.getInstance().convertToPixels(30f),
                        Display.getInstance().convertToPixels(heightMM));
            }

            @Override
            public void paint(Graphics g) {
                int x = getX(), y = getY(), w = getWidth(), h = getHeight();

                g.setAntiAliased(true);
                g.setAlpha(255);

                // track
                g.setColor(trackColor);
                g.fillRoundRect(x, y, w, h, h, h);

                // fill
                int fw = (int) (w * p);
                if (fw > 0) {
                    g.setColor(fillColor);
                    g.fillRoundRect(x, y, Math.max(fw, h), h, h, h);
                }
            }
        };
    }

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
        s.setPadding(1, 1, 0.8f, 0.5f);
    }

    static Container makeShortcut(String title, float mm, ActionListener<ActionEvent> onClick, String imgPath) {
        Container c = new Container(new BorderLayout());
        shortcutSize(c);
        shortcutsStyle(c);

        Button b = new Button(title);
        b.setUIID("Label");
        b.getAllStyles().setFgColor(SAVI.DEEP_SLATE);
        b.getAllStyles().setFont(SAVI.semiBold(mm));
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

    public void show() {
        // BACKEND: one Home object feeds the cards below (SAVI.user / SAVI.groupList.getGroups: see note)
        Home home = new Home(SAVI.user, Home.allSavings());
        GroupSavings g = home.getFeaturedGroup();                    // null if the user has no groups

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

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

        Container weekTotalContainer = new Container(new BorderLayout());
        boxStyles(weekTotalContainer, SAVI.WHITE, 2, 3);

        Label amountSaved = SAVI.text("AMOUNT SAVED", SAVI.bold(2.5f), SAVI.SLATE_GRAY);
        float pct = (float) home.getWeeklyProgress();

        Label amount = SAVI.text(Home.pesoFormat(home.getWeeklySaved()), SAVI.bold(4), SAVI.DARK_KNIGHT);

        Container targetPercent = new Container(new BorderLayout());
        Label weekTarget = SAVI.text("Weekly Target: " + Home.pesoShort(home.getWeeklyTarget()), SAVI.regular(2f), SAVI.SLATE_GRAY);
        Label percentage = SAVI.text(home.getWeeklyPercent() + "%", SAVI.bold(2f), SAVI.BRIGHT_VIOLET);
        targetPercent.add(BorderLayout.WEST, weekTarget);
        targetPercent.add(BorderLayout.EAST, percentage);

        Container weekTotalContent = new Container(BoxLayout.y());
        weekTotalContent.add(amountSaved);
        weekTotalContent.add(amount);
        weekTotalContent.add(targetPercent);

        Component bar = progressBar(pct, SAVI.POWDER_WHITE, SAVI.DARK_PURPLE, 1.5f);
        bar.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        bar.getAllStyles().setMargin(1f, 0f, 0f, 0f);
        weekTotalContent.add(bar);
        weekTotalContainer.add(BorderLayout.NORTH, weekTotalContent);

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
        Label groupOverviewText = SAVI.text("Group Overview", SAVI.bold(2f), SAVI.DARK_BLUE);

        Button manage = new Button("Manage");
        manage.getAllStyles().setFgColor(SAVI.DARK_PURPLE);
        manage.getAllStyles().setFont(SAVI.semiBold(2f));
        manage.addActionListener(e->{new PeoplePage().show();});

        GroupOverviewContainer.add(BorderLayout.WEST, groupOverviewText);
        GroupOverviewContainer.add(BorderLayout.EAST, manage);

        Container singleOverview = new Container(new BorderLayout());
        boxStyles(singleOverview, SAVI.WHITE,2f, 2f);

        Label groupName = SAVI.text(g == null ? "No groups yet" : g.getGroupName(), SAVI.semiBold(2f), SAVI.DARK_BLUE);

        Container totalSaved = new Container(BoxLayout.y());
        Label totalSavedText = SAVI.text("TOTAL SAVED", SAVI.semiBold(1.5f), SAVI.DARK_BLUE);
        Label groupSaveAmount = SAVI.text(g == null ? "P0" : Home.pesoShort(home.getTotalSaved(g)), SAVI.bold(3f), SAVI.DARK_BLUE);
        totalSaved.add(totalSavedText);
        totalSaved.add(groupSaveAmount);

        Container remainingBal = new Container(BoxLayout.y());
        Label remainingBalText = SAVI.text("REMAINING BALANCE", SAVI.semiBold(1.5f), SAVI.DARK_BLUE);
        Label remainingBalAmount = SAVI.text(g == null ? "P0" : Home.pesoShort(home.getRemainingBalance(g)), SAVI.bold(3f), SAVI.DARK_BLUE);
        remainingBal.add(remainingBalText);
        remainingBal.add(remainingBalAmount);

        singleOverview.add(BorderLayout.NORTH, groupName);
        singleOverview.add(BorderLayout.WEST, totalSaved);
        singleOverview.add(BorderLayout.EAST, remainingBal);

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
        mainContents.add(weekTotalContainer);
        mainContents.add(shortcutsContainer);
        mainContents.add(GroupOverviewContainer);
        if (g != null) {                       // group card only when the user has a group
            mainContents.add(singleOverview);
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