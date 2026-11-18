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
import com.savi.app.backend.Transfer;

public class TransferPage {

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
                shadowColor(SAVI.SLATE_GRAY));
        contS.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        contS.setPadding(padTB, padTB, padLR, padLR);
        contS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        contS.setMargin(marT, marB, marLR,marLR);
    }

    // the Picker is created by the caller, so show() can read what was chosen and refill it
    private Container dropdown(Picker picker, String placeholder, String... options) {
        String[] items = new String[options.length + 1];
        items[0] = placeholder;
        System.arraycopy(options, 0, items, 1, options.length);

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
                .stroke(2f, false));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(0, 0, 2.5f, 2.5f);         // top, bottom, left, right: smaller height
        bs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        bs.setMargin(1, 1, 2.5f, 2.5f);         // top, bottom, left, right: gap between bars, and
        // a little room so the border is not cut at the edges

        return bar;
    }

    private Container transferCard(String number, String title, String subtitle,
                                   boolean active, Picker groupPicker, String[] groups,
                                   Picker memberPicker, String[] members) {
        // ---- card ----
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.WHITE, 1f, 1f, 2f, 0, 1);
        Style cs = card.getAllStyles();
        cs.setBgColor(SAVI.WHITE);
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
        ns.setFgColor(active ? SAVI.WHITE : SAVI.DARK_PURPLE);
        ns.setBgTransparency(0);
        ns.setMargin(0, 0, 0, 0);
        ns.setPadding(0, 0, 0, 0);
        ns.setBorder(RoundBorder.create().color(active ? SAVI.DARK_PURPLE : SAVI.PASTEL_LAVENDER));
        num.setAlignment(Component.CENTER);
        num.setVerticalAlignment(Component.CENTER);
        int d = Display.getInstance().convertToPixels(7);
        num.setPreferredSize(new com.codename1.ui.geom.Dimension(d, d));

        Container badgeWrap = new Container(new FlowLayout());
        badgeWrap.add(num);

        // ---- title + subtitle ----
        Label titleLbl = SAVI.text(title, SAVI.semiBold(2.2f), SAVI.BLACK);
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
        dropdowns.add(dropdown(groupPicker, "Select group", groups));
        dropdowns.add(dropdown(memberPicker, "Select member", members));

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

    // BACKEND helpers: the backend gives java.util.List, a Picker needs a String[]
    private String[] toArray(java.util.List<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    // refills a dropdown (placeholder first) and goes back to the placeholder
    private void setOptions(Picker picker, String placeholder, java.util.List<String> options) {
        String[] items = new String[options.size() + 1];
        items[0] = placeholder;
        for (int i = 0; i < options.size(); i++) {
            items[i + 1] = options.get(i);
        }
        picker.setStrings(items);
        picker.setSelectedStringIndex(0);
    }

    public void show(){
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        // BACKEND: the groups come from the logged-in user's groups, members from the chosen group
        Transfer transfer = new Transfer(SAVI.user, SAVI.groups);
        String[] groups = toArray(transfer.getGroupNames());
        String[] noMembers = new String[0];     // filled in once a group is chosen

        Picker fromGroup = new Picker();
        Picker fromMember = new Picker();
        Picker toGroup = new Picker();
        Picker toMember = new Picker();

        fromGroup.addActionListener(e -> {
            transfer.setFromGroup(fromGroup.getSelectedString());   // also clears the member
            setOptions(fromMember, "Select member", transfer.getMemberNames(transfer.getFromGroup()));
        });
        fromMember.addActionListener(e -> transfer.setFromMember(fromMember.getSelectedString()));

        toGroup.addActionListener(e -> {
            transfer.setToGroup(toGroup.getSelectedString());
            setOptions(toMember, "Select member", transfer.getMemberNames(transfer.getToGroup()));
        });
        toMember.addActionListener(e -> transfer.setToMember(toMember.getSelectedString()));

        Container mainContents = new Container(BoxLayout.y());
        mainContents.add(transferCard("1", "Transfer from", "Choose where the funds will come from",
                true, fromGroup, groups, fromMember, noMembers));
        mainContents.add(divider());
        mainContents.add(transferCard("2", "Transfer to", "Choose who will receive the funds",
                false, toGroup, groups, toMember, noMembers));

        Container amountContainer = new Container(new BorderLayout());
        amountContainer.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        amountContainer.getAllStyles().setMargin(1, 1, 2, 2);

        Label amount = SAVI.text("Amount", SAVI.semiBold(2.5f), SAVI.BLACK);
        amountContainer.add(BorderLayout.NORTH, amount);

        TextField amountField = new TextField();
        amountField.setConstraint(TextArea.DECIMAL);
        amountContainer.add(BorderLayout.SOUTH, amountField);
        mainContents.add(amountContainer);

        Button TransferSav = new Button("Transfer Savings ->");
        Style transSavS = TransferSav.getAllStyles();
        transSavS.setFgColor(SAVI.WHITE);
        transSavS.setFont(SAVI.semiBold(2f));
        transSavS.setAlignment(Component.CENTER);
        transSavS.setBgTransparency(255);
        transSavS.setBgColor(SAVI.DARK_PURPLE);
        transSavS.setBorder(RoundRectBorder.create().
                cornerRadius(4).
                shadowOpacity(50).
                shadowBlur(10).
                shadowSpread(1f).
                shadowX(0.5f).
                shadowY(0.3f));
        transSavS.setMarginUnit(Style.UNIT_TYPE_DIPS);
        transSavS.setMargin(1, 1, 5, 5);

        TransferSav.addActionListener(e -> {
            transfer.setAmount(amountField.getText());
            String result = transfer.confirm();          // checks everything, then moves the money
            if (!result.equals(Transfer.SUCCESS)) {
                Dialog.show("Transfer failed", result, "OK", null);
                return;
            }
            new TransferConfirmationPage().show((float) transfer.getAmount(),
                    transfer.getFromMember(), transfer.getFromGroup(),
                    transfer.getToMember(), transfer.getToGroup());
        });
        mainContents.add(TransferSav);

        base.add(BorderLayout.NORTH, SAVI.buildHeader("Transfer Funds", () -> new HomePage().show()));
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}