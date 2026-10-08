package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;


public class HomePage {
    static void shortcutSize(Container title){
        title.setPreferredH(Display.getInstance().convertToPixels(16f));
        title.setPreferredW(Display.getInstance().convertToPixels(16f));
    }

    static void shortcutsStyle(Container cont){
        Style s = cont.getAllStyles();
        s.setBgColor(0xFFFFFF);
        s.setBgTransparency(255);
        //RoundRectBorder.create().cornerRadius(4).shadowOpacity(50).shadowBlur(10).shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(0x6d28d9)
        s.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(0x6d28d9).
                strokeColor(0xF1F5F9).
                strokeOpacity(255).
                stroke(0.3f, true));
        s.setMarginUnit(Style.UNIT_TYPE_DIPS);
        s.setMargin(0, 0, 1, 0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1, 1, 0.8f, 0.5f);
    }

    static Container makeShortcut(String title, float mm, ActionListener<ActionEvent> onClick) {
        Container c = new Container(new BorderLayout());
        shortcutSize(c);
        shortcutsStyle(c);

        Button b = new Button(title);
        b.setUIID("Label");
        b.getAllStyles().setFgColor(0x334155);
        b.getAllStyles().setFont(SAVI.semiBold(mm));
        b.addActionListener(onClick);

        c.add(BorderLayout.SOUTH, b);
        c.setLeadComponent(b);
        return c;
    }

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(0xF4EFFE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Container weekContainer = new Container(new BorderLayout());
        weekContainer.setPreferredH(Display.getInstance().convertToPixels(20f));
        Style sWC = weekContainer.getAllStyles();
        sWC.setBgColor(0x7C3AED);
        sWC.setBgTransparency(255);
        sWC.setBorder(RoundRectBorder.create().cornerRadius(4).shadowOpacity(50).shadowBlur(10).shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(0x6d28d9));
        sWC.setMarginUnit(Style.UNIT_TYPE_DIPS);
        sWC.setMargin(2, 0.5f, 2, 2); //top, bottom, left, right
        sWC.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        sWC.setPadding(2, 2, 3, 3);

        Label thisWeek = SAVI.text("THIS WEEK", SAVI.bold(2.5f), 0xFFFFFF);
        weekContainer.add(BorderLayout.NORTH, thisWeek);

        Container weekTotalContainer = new Container(new BorderLayout());
        weekTotalContainer.setPreferredH(Display.getInstance().convertToPixels(25f));
        Style sWT =  weekTotalContainer.getAllStyles();
        sWT.setBgColor(0xFFFFFF);
        sWT.setBgTransparency(255);
        sWT.setBorder(RoundRectBorder.create().
                cornerRadius(5).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f).
                shadowColor(0x6d28d9).
                strokeColor(0xF1F5F9).
                strokeOpacity(255).
                stroke(0.3f, true));
        sWT.setMarginUnit(Style.UNIT_TYPE_DIPS);
        sWT.setMargin(0.5f, 0.5f, 2, 2);
        sWT.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        sWT.setPadding(2, 2, 3, 3);

        Label amountSaved = SAVI.text("AMOUNT SAVED", SAVI.bold(2.5f), 0x64748B);
        Label amount = SAVI.text("P4, 256.50", SAVI.bold(4), 0x0f172A);

        Container targetPercent = new Container(new BorderLayout());
        Label weekTarget = SAVI.text("Weekly Target: P5,000", SAVI.regular(2f), 0x64748B);
        Label percentage = SAVI.text("85%", SAVI.bold(2f), 0x6D28D9);
        targetPercent.add(BorderLayout.WEST, weekTarget);
        targetPercent.add(BorderLayout.EAST, percentage);

        Container weekTotalContent = new Container(BoxLayout.y());
        weekTotalContent.add(amountSaved);
        weekTotalContent.add(amount);
        weekTotalContent.add(targetPercent);
        weekTotalContainer.add(BorderLayout.NORTH, weekTotalContent);

        Container shortcutsContainer = new Container(new GridLayout(1, 4));
        Style styleShortC = shortcutsContainer.getAllStyles();
        styleShortC.setMarginUnit(Style.UNIT_TYPE_DIPS);
        styleShortC.setMargin(0.5f, 0.5f, 2, 2);

        float shortcutTitleSize = 1.5f;

        shortcutsContainer.add(makeShortcut("Transfer", shortcutTitleSize, e -> Dialog.show("Pending", "Coming soon", "OK", null)));
        shortcutsContainer.add(makeShortcut("Summary", shortcutTitleSize, e -> Dialog.show("Pending", "Coming soon", "OK", null)));
        shortcutsContainer.add(makeShortcut("Pending", shortcutTitleSize, e -> Dialog.show("Pending", "Coming soon", "OK", null)));
        shortcutsContainer.add(makeShortcut("Reminders", shortcutTitleSize, e -> Dialog.show("Pending", "Coming soon", "OK", null)));

        Container GroupOverviewContainer = new Container(new BorderLayout());
        GroupOverviewContainer.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        GroupOverviewContainer.getAllStyles().setMargin(0.5f, 0.5f, 2, 2);
        Label groupOverviewText = SAVI.text("Group Overview", SAVI.bold(2f), 0x1E293B);

        Button manage = new Button("Manage");
        manage.getAllStyles().setFgColor(0x7C3AED);
        manage.getAllStyles().setFont(SAVI.semiBold(2f));

        GroupOverviewContainer.add(BorderLayout.WEST, groupOverviewText);
        GroupOverviewContainer.add(BorderLayout.EAST, manage);

        Container singleOverview = new Container(new BorderLayout());
        Label groupName = SAVI.text("December Vacation", SAVI.semiBold(2f), 0x1E293B);

        Container totalSaved = new Container(BoxLayout.y());
        Label totalSavedText = SAVI.text("TOTAL SAVED", SAVI.semiBold(2f), 0x1E293B);
        Label groupSaveAmount = SAVI.text("P12,000", SAVI.bold(3f), 0x1E293B);
        totalSaved.add(totalSavedText);
        totalSaved.add(groupSaveAmount);

        Container remainingBal = new Container(BoxLayout.y());
        Label remainingBalText = SAVI.text("REMAINING BALANCE", SAVI.semiBold(2f), 0x1E293B);
        Label remainingBalAmount = SAVI.text("P350", SAVI.bold(3f), 0x1E293B);
        remainingBal.add(remainingBalText);
        remainingBal.add(remainingBalAmount);

        singleOverview.add(BorderLayout.NORTH, groupName);
        singleOverview.add(BorderLayout.WEST, totalSaved);
        singleOverview.add(BorderLayout.EAST, remainingBal);

        Container mainContents = new Container(BoxLayout.y());
        mainContents.add(weekContainer);
        mainContents.add(weekTotalContainer);
        mainContents.add(shortcutsContainer);
        mainContents.add(GroupOverviewContainer);
        mainContents.add(singleOverview);
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}
