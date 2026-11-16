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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    static Container dayCell(Date d, boolean today){
        SimpleDateFormat dateFormat = new SimpleDateFormat("d");
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        String day = days[c.get(Calendar.DAY_OF_WEEK)-1];

        int textColor = today ? SAVI.DARK_DARK_PURPLE : SAVI.WHITE;
        Label num = SAVI.text(dateFormat.format(d), today ? SAVI.bold(1.8f) : SAVI.regular(1.8f), textColor);
        Label dayLett = SAVI.text(day, today ? SAVI.bold(1.5f) : SAVI.regular(1.5f), textColor);
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
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Container weekContainer = new Container(new BorderLayout());
        boxStyles(weekContainer, SAVI.DARK_PURPLE, 1.5f, 1.5f);
        weekContainer.getAllStyles().setMarginBottom(0.4f);
        weekContainer.getAllStyles().setMarginTop(1f);

        Label thisWeek = SAVI.text("THIS WEEK", SAVI.bold(2.5f), SAVI.WHITE);
        Label icon = new Label();
        icon.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(icon, FontImage.MATERIAL_DATE_RANGE, 4f);
        Container headerLeft = new Container(new FlowLayout());
        headerLeft.add(icon).add(thisWeek);

        Calendar cal = Calendar.getInstance();
        Date today = new Date();
        cal.setTime(today);
        long noon = today.getTime();
        long weekStart = noon - (cal.get(Calendar.DAY_OF_WEEK) - 1) * 86400000L;
        Date start = new Date(weekStart);
        Date end = new Date(weekStart + 6 * 86400000L);

        Container daysRow = new Container(new GridLayout(1, 7));
        SimpleDateFormat key = new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < 7; i++) {
            Date d = new Date(weekStart + i * 86400000L);
            boolean isToday = key.format(d).equals(key.format(today));
            daysRow.add(dayCell(d, isToday));
        }
        daysRow.getAllStyles().setMarginTop(2);

        // current month, top right
        Label monthLabel = SAVI.text(new SimpleDateFormat("MMMM").format(today).toUpperCase(),
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
        double saved = 4256.50, target = 5000;
        float pct = (float) (saved / target);

        Label amount = SAVI.text("P4,256.50", SAVI.bold(4), SAVI.DARK_KNIGHT);

        Container targetPercent = new Container(new BorderLayout());
        Label weekTarget = SAVI.text("Weekly Target: P5,000", SAVI.regular(2f), SAVI.SLATE_GRAY);
        Label percentage = SAVI.text((int) (pct * 100) + "%", SAVI.bold(2f), SAVI.BRIGHT_VIOLET);
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

        Label groupName = SAVI.text("December Vacation", SAVI.semiBold(2f), SAVI.DARK_BLUE);

        Container totalSaved = new Container(BoxLayout.y());
        Label totalSavedText = SAVI.text("TOTAL SAVED", SAVI.semiBold(1.5f), SAVI.DARK_BLUE);
        Label groupSaveAmount = SAVI.text("P12,000", SAVI.bold(3f), SAVI.DARK_BLUE);
        totalSaved.add(totalSavedText);
        totalSaved.add(groupSaveAmount);

        Container remainingBal = new Container(BoxLayout.y());
        Label remainingBalText = SAVI.text("REMAINING BALANCE", SAVI.semiBold(1.5f), SAVI.DARK_BLUE);
        Label remainingBalAmount = SAVI.text("P350", SAVI.bold(3f), SAVI.DARK_BLUE);
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

        //put in a method
        Container recentInfoCont = new Container(new BorderLayout());
        boxStyles(recentInfoCont, SAVI.WHITE,1.5f, 1.5f);

        Container recentDate = new Container(new BorderLayout());
        recentDate.setPreferredW(Display.getInstance().convertToPixels(15f));
        boxStyles(recentDate, SAVI.DARK_PURPLE,1.5f, 0.6f);

        Label dateNum = SAVI.text("02", SAVI.semiBold(3f), SAVI.WHITE);
        Label dateDay = SAVI.text("Wednesday", SAVI.semiBold(1.5f), SAVI.WHITE);
        recentDate.add(BorderLayout.NORTH, dateNum);
        recentDate.add(BorderLayout.SOUTH, dateDay);
        recentInfoCont.add(BorderLayout.WEST, recentDate);

        Container groupAndMemInfo = new Container(BoxLayout.y());
        Label recentTitle = SAVI.text("December Vacation", SAVI.semiBold(2.2f), SAVI.DARK_BLUE);
        Label recentContributor = SAVI.text("Someone contributed P24", SAVI.regular(2f), SAVI.DARK_BLUE);
        Label contributionNote = SAVI.text("Note: ", SAVI.regular(2f), SAVI.DARK_BLUE);
        groupAndMemInfo.add(recentTitle);
        groupAndMemInfo.add(recentContributor);
        groupAndMemInfo.add(contributionNote);
        recentInfoCont.add(BorderLayout.CENTER, groupAndMemInfo);

        Container mainContents = new Container(BoxLayout.y());
        mainContents.add(weekContainer);
        mainContents.add(weekTotalContainer);
        mainContents.add(shortcutsContainer);
        mainContents.add(GroupOverviewContainer);
        mainContents.add(singleOverview);
        mainContents.add(recentsTitle);
        mainContents.add(recentInfoCont);
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}