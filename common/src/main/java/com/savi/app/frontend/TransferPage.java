package com.savi.app.frontend;

import com.codename1.ui.*;
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

public class TransferPage {

    int darkDarkPurple = 0x30205E;
    int darkPurple = 0x7C3AED;
    int palePurple = 0xF4EFFE;
    int pastelLavander = 0xD8B4FE;
    int paleLavander = 0xE9D5FF;
    int bluishPurple = 0x4F46E5;
    int borderColor = 0xEDF0F5;
    int white = 0xFFFFFF;
    int black = 0x000000;
    int slategray = 0x64748B;
    int navy = 0x29364D;
    int gray = 0x8491A7;
    int green = 0x00A878;
    int paleGreen = 0xE6FAF2;

    void styles(Container cont, int bgColor, float padLR, float padTB, float marLR, float marT, float marB){
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
                shadowColor(slategray));
        contS.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        contS.setPadding(padTB, padTB, padLR, padLR);
        contS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        contS.setMargin(marT, marB, marLR,marLR);
    }

    private Container dropdown(String placeholder, String... options) {
        String[] items = new String[options.length + 1];
        items[0] = placeholder;
        System.arraycopy(options, 0, items, 1, options.length);

        Picker picker = new Picker();
        picker.setUIID("Label");                 // removes the default gray box behind the text
        picker.setType(Display.PICKER_TYPE_STRINGS);
        picker.setStrings(items);
        picker.setSelectedStringIndex(0);

        Style ps = picker.getAllStyles();
        ps.setBgTransparency(0);
        ps.setBorder(Border.createEmpty());
        ps.setFgColor(0x1f1f2e);
        ps.setMargin(0, 0, 0, 0);
        ps.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ps.setPadding(0, 0, 0, 0);
        ps.setFont(SAVI.semiBold(1.9f));   // or any font helper you have

        Label chevron = new Label();
        chevron.getAllStyles().setFgColor(0x4b5563);
        chevron.getAllStyles().setBgTransparency(0);
        chevron.getAllStyles().setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(chevron, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 3.5f);

        Container bar = new Container(new BorderLayout());
        bar.add(BorderLayout.CENTER, picker);
        bar.add(BorderLayout.EAST, chevron);
        bar.setLeadComponent(picker);

        Style bs = bar.getAllStyles();
        bs.setBgColor(0xffffff);
        bs.setBgTransparency(255);
        bs.setBorder(RoundRectBorder.create()
                .cornerRadius(4)                 // bigger radius = more pill-like
                .strokeColor(0xd9d4f5)           // light lavender like the design
                .strokeOpacity(255)
                .stroke(1, false));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(2, 2, 2, 2);               // top, bottom, left, right: taller than before
        bs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        bs.setMargin(0, 1.5f, 0, 0);             // space between the two bars

        return bar;
    }

    private Container transferCard(String number, String title, String subtitle,
                                   boolean active, String[] groups, String[] members) {
        // ---- card ----
        Container card = new Container(new BorderLayout());
        styles(card, white, 1f, 1f, 2f, 0, 1);
        Style cs = card.getAllStyles();
        cs.setBgColor(white);
        cs.setBgTransparency(255);
        cs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        cs.setPadding(2, 2, 2, 2);
        cs.setBorder(RoundRectBorder.create()
                .cornerRadius(4)
                .strokeColor(0xe4e0fa)
                .strokeOpacity(255)
                .stroke(1, false)
                .shadowOpacity(0));

        // ---- number badge (solid purple if active, pale lavender if not) ----
        Label num = new Label(number);
        Style ns = num.getAllStyles();
        ns.setFgColor(active ? white : darkPurple);
        ns.setBgTransparency(0);
        ns.setMargin(0, 0, 0, 0);
        ns.setPadding(0, 0, 0, 0);
        ns.setBorder(RoundBorder.create().color(active ? darkPurple : paleLavander));
        num.setAlignment(Component.CENTER);
        num.setVerticalAlignment(Component.CENTER);
        int d = Display.getInstance().convertToPixels(7);
        num.setPreferredSize(new com.codename1.ui.geom.Dimension(d, d));

        Container badgeWrap = new Container(new FlowLayout());
        badgeWrap.add(num);

        // ---- title + subtitle ----
        Label titleLbl = SAVI.text(title, SAVI.semiBold(2.2f), black);
        Label subLbl = SAVI.text(subtitle, SAVI.semiBold(1.5f), 0x6b7280);
        for (Label l : new Label[]{titleLbl, subLbl}) {
            l.getAllStyles().setMargin(0, 0, 0, 0);
            l.getAllStyles().setPadding(0, 0, 0, 0);
        }

        Container texts = new Container(BoxLayout.y());
        texts.add(titleLbl);
        texts.add(subLbl);
        texts.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        texts.getAllStyles().setMargin(0, 0, 1.5f, 0);

        Container headRow = new Container(new BorderLayout());
        headRow.add(BorderLayout.WEST, badgeWrap);
        headRow.add(BorderLayout.CENTER, texts);
        headRow.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        headRow.getAllStyles().setMargin(0, 1.5f, 0, 0);

        // ---- dropdowns ----
        Container dropdowns = new Container(BoxLayout.y());
        dropdowns.add(dropdown("Select group", groups));
        dropdowns.add(dropdown("Select member", members));

        card.add(BorderLayout.NORTH, headRow);
        card.add(BorderLayout.CENTER, dropdowns);
        return card;
    }

    private Container divider() {
        Container row = new Container(new GridLayout(1, 2));
        row.add(line());
        row.add(line());
        row.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        row.getAllStyles().setMargin(2, 2, 2, 2);
        return row;
    }

    private Container line() {
        Container l = new Container();
        Style ls = l.getAllStyles();
        ls.setBgColor(0xe5e7eb);
        ls.setBgTransparency(255);
        ls.setMargin(0, 0, 1, 1);   // the side margins create the gap between the two halves
        l.setPreferredH(Math.max(1, Display.getInstance().convertToPixels(0.2f)));
        return l;
    }

    public void show(){
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(palePurple);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        com.codename1.ui.Button back = new com.codename1.ui.Button();
        Style s = back.getAllStyles();
        s.setFgColor(black);
        s.setBgTransparency(0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1, 1, 1, 1);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 5);

        back.addActionListener(e -> {new HomePage().show();});

        // put it at the top-left
        Container top = BorderLayout.west(back);
        Style ts = top.getAllStyles();
        ts.setMarginUnit(Style.UNIT_TYPE_DIPS);   // or UNIT_TYPE_PIXELS / UNIT_TYPE_SCREEN_PERCENTAGE
        ts.setMargin(2, 1, 2, 0);                 // top, bottom, left, right (in dips)
        //base.add(BorderLayout.NORTH, top);

        Container titleContainer = new Container(new BorderLayout());
        styles(titleContainer, darkPurple, 1f, 0f, 2f, 2, 1);

        Label TransferFunds = SAVI.text("Transfer Funds", SAVI.semiBold(2.5f), white);
        titleContainer.add(BorderLayout.WEST, TransferFunds);

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.WEST, top);
        header.add(BorderLayout.CENTER, titleContainer);
        String[] groups = {"Family", "Friends", "Work"};
        String[] members = {"Anna", "Ben", "Carl"};

        Container mainContents = new Container(BoxLayout.y());
        mainContents.add(transferCard("1", "Transfer from", "Choose where the funds will come from",
                true, groups, members));
        mainContents.add(divider());
        mainContents.add(transferCard("2", "Transfer to", "Choose who will receive the funds",
                false, groups, members));

        Container amountContainer = new Container(new BorderLayout());
        amountContainer.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        amountContainer.getAllStyles().setMargin(1, 1, 2, 2);

        Label amount = SAVI.text("Amount", SAVI.semiBold(2.5f), black);
        amountContainer.add(BorderLayout.NORTH, amount);

        TextField amountField = new TextField();
        amountField.setConstraint(TextArea.DECIMAL);
        amountContainer.add(BorderLayout.SOUTH, amountField);
        mainContents.add(amountContainer);

        Button TransferSav = new Button("Transfer Savings ->");
        Style transSavS = TransferSav.getAllStyles();
        transSavS.setFgColor(white);
        transSavS.setFont(SAVI.semiBold(2f));
        transSavS.setAlignment(Component.CENTER);
        transSavS.setBgTransparency(255);
        transSavS.setBgColor(darkPurple);
        transSavS.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f));
        transSavS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        transSavS.setMargin(1, 1, 5, 5);
        mainContents.add(TransferSav);

        base.add(BorderLayout.NORTH, header);
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}
