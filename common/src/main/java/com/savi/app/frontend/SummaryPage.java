package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.geom.Dimension;
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
import com.codename1.ui.table.TableLayout;
import com.savi.app.SAVI;
import com.codename1.util.SuccessCallback;
import com.savi.app.backend.ChartData;
import com.savi.app.backend.Summary;

public class SummaryPage {

    static final String PESO = "P";

    // slice colors of the donut (used again if a group has more than 4 members)
    private static final int[] SHARE_COLORS = {0x6D0FD6, 0xA066FF, 0x047857, 0x5EE0B0};

    // BACKEND: every number and text on this page comes from here
    private Summary summary;

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

    Label txt(String s, Font f, int color) {
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

    private TableLayout.Constraint cell(TableLayout tl, int pct) {
        TableLayout.Constraint c = tl.createConstraint();
        c.setWidthPercentage(pct);
        return c;
    }

    private Container tableRow(Component c1, Component c2, Component c3) {
        TableLayout tl = new TableLayout(1, 3);
        tl.setGrowHorizontally(true);
        Container row = new Container(tl);
        row.add(cell(tl, 52), c1);
        row.add(cell(tl, 28), c2);
        row.add(cell(tl, 20), c3);
        return row;
    }

    // splits a text into lines that fit maxW pixels (used for the "No shares" message)
    private String[] wrap(String text, Font f, int maxW) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<String>();
        String line = "";
        int start = 0;
        while (start <= text.length()) {
            int sp = text.indexOf(' ', start);
            if (sp == -1) {
                sp = text.length();
            }
            String word = text.substring(start, sp);
            start = sp + 1;

            String test = line.isEmpty() ? word : line + " " + word;
            if (f.stringWidth(test) > maxW && !line.isEmpty()) {
                lines.add(line);
                line = word;
            } else {
                line = test;
            }
        }
        if (!line.isEmpty()) {
            lines.add(line);
        }
        String[] out = new String[lines.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = lines.get(i);
        }
        return out;
    }

    // the small round arrow used by both dropdowns
    Label chevron(int iconColor) {
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

    // a borderless white-text Picker (same wheel as the Transfer page);
    // onPick runs only when the chosen item is different from `current`
    Picker titlePicker(String[] items, final String current, Font font,
                       final SuccessCallback<String> onPick) {
        final Picker p = new Picker();
        p.setUIID("Label");
        p.setType(Display.PICKER_TYPE_STRINGS);
        p.setStrings(items);
        p.setSelectedString(current);
        Style st = p.getAllStyles();
        st.setFont(font);
        st.setFgColor(SAVI.WHITE);
        st.setBgTransparency(0);
        st.setBorder(Border.createEmpty());
        st.setMargin(0, 0, 0, 0);
        st.setPadding(0, 0, 0, 0);
        p.addActionListener(e -> {
            String chosen = p.getSelectedString();
            if (chosen != null && !chosen.equalsIgnoreCase(current)) {
                onPick.onSucess(chosen);
            }
        });
        return p;
    }

    // picker items: every name in capitals (or just the title when the list is empty)
    String[] pickerItems(java.util.List<String> names, String title) {
        if (names.isEmpty()) {
            return new String[]{title};
        }
        String[] items = new String[names.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = names.get(i).toUpperCase();
        }
        return items;
    }

    // TOP DROPDOWN: switches between the Personal and the Group savings summary
    Container summaryHeader(boolean personalMode) {
        final String[] modes = {"Personal Savings Summary", "Group Savings Summary"};
        String current = personalMode ? modes[0] : modes[1];

        com.codename1.ui.Button back = new com.codename1.ui.Button();
        Style bs = back.getAllStyles();
        bs.setFgColor(SAVI.DARK_PURPLE);
        bs.setBgTransparency(0);
        bs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(0.6f, 0.6f, 0.6f, 0.6f);
        bs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 3.5f);
        back.addActionListener(e -> new HomePage().show());

        Picker modePicker = titlePicker(modes, current, SAVI.bold(2.2f), chosen -> {
            if (chosen.equals(modes[0])) {
                new PersonalSummaryPage().show();
            } else {
                new SummaryPage().show();
            }
        });
        modePicker.getAllStyles().setAlignment(Component.CENTER);

        // tapping the title or the arrow opens the picker
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

    // SECOND DROPDOWN: the purple card shared by the group and personal pages.
    // Name picker + arrow on top, "Total Pool" on the left, `right` on the right
    // (the Members pill for a group, the Target Amount for a personal saving).
    Container poolCardFor(String title, String[] items, SuccessCallback<String> onPick,
                          String totalText, Component right) {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.DARK_PURPLE, 3f, 4f, 2f, 0.5f, 0.5f);

        Picker namePicker = titlePicker(items, title, SAVI.bold(2.5f), onPick);

        Container topRow = new Container(new BorderLayout());
        topRow.add(BorderLayout.WEST, namePicker);
        topRow.add(BorderLayout.EAST, chevron(SAVI.DARK_PURPLE));
        topRow.setLeadComponent(namePicker);

        Container total = new Container(BoxLayout.y());
        total.add(txt("Total Pool", SAVI.regular(2f), SAVI.WHITE));
        total.add(txt(totalText, SAVI.bold(2.6f), SAVI.WHITE));

        Container bottomRow = new Container(new BorderLayout());
        bottomRow.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        bottomRow.getAllStyles().setMargin(1.5f, 0, 0, 0);
        bottomRow.add(BorderLayout.WEST, total);
        bottomRow.add(BorderLayout.EAST, right);

        card.add(BorderLayout.NORTH, topRow);
        card.add(BorderLayout.SOUTH, bottomRow);
        return card;
    }

    private Container poolCard() {
        Label members = txt("Members", SAVI.regular(2f), SAVI.WHITE);
        members.getAllStyles().setAlignment(Component.RIGHT);
        Label pill = txt(summary.getContributorsText(), SAVI.semiBold(2.3f), SAVI.WHITE);

        Container right = new Container(BoxLayout.y());
        right.add(members);
        right.add(FlowLayout.encloseRight(pill));

        String title = summary.getGroupTitle();
        return poolCardFor(title, pickerItems(summary.getGroupNames(), title),
                chosen -> new SummaryPage().show(chosen),      // reload on the chosen group
                summary.getTotalPoolText(), right);
    }

    // empty = the group has no shares yet: gray ring + the "No shares" message in the middle
    private Component donut(final float[] shares, final String goalLabel, final int goalPercent,
                            final String status, final boolean empty) {
        return new Component() {
            @Override
            protected Dimension calcPreferredSize() {
                int s = Display.getInstance().convertToPixels(45f);
                return new Dimension(s, s);
            }

            @Override
            public void paint(Graphics g) {
                int x = getX(), y = getY(), w = getWidth(), h = getHeight();
                int size = Math.min(w, h);
                int cx = x + (w - size) / 2;
                int cy = y + (h - size) / 2;

                g.setAntiAliased(true);
                g.setAlpha(255);

                if (empty) {
                    g.setColor(SAVI.SLATE_GRAY);
                    g.fillArc(cx, cy, size, size, 0, 360);
                } else {
                    int start = 90;
                    for (int i = 0; i < shares.length; i++) {
                        int sweep = Math.round(-shares[i] * 3.6f);
                        g.setColor(SHARE_COLORS[i % SHARE_COLORS.length]);
                        g.fillArc(cx, cy, size, size, start, sweep);
                        start += sweep;
                    }
                }

                int t = (int) (size * 0.2f);
                g.setColor(0xFFFFFF);
                g.fillArc(cx + t, cy + t, size - 2 * t, size - 2 * t, 0, 360);

                if (empty) {
                    Font mf = SAVI.semiBold(1.5f);
                    String[] lines = SummaryPage.this.wrap(Summary.NO_SHARES_MESSAGE, mf, (int) ((size - 2 * t) * 0.85f));
                    int lh = mf.getHeight();
                    int ty = cy + (size - lines.length * lh) / 2;
                    g.setColor(SAVI.SLATE_GRAY);
                    g.setFont(mf);
                    for (int i = 0; i < lines.length; i++) {
                        g.drawString(lines[i], cx + (size - mf.stringWidth(lines[i])) / 2, ty + i * lh);
                    }
                    return;
                }

                String percent = goalPercent + "%";
                Font f1 = SAVI.regular(1.8f);
                Font f2 = SAVI.bold(2.2f);
                Font f3 = SAVI.bold(1.8f);
                int total = f1.getHeight() + f2.getHeight() + f3.getHeight();
                int ty = cy + (size - total) / 2;

                g.setColor(0x1E293B);
                g.setFont(f1);
                g.drawString(goalLabel, cx + (size - f1.stringWidth(goalLabel)) / 2, ty);
                ty += f1.getHeight();
                g.setFont(f2);
                g.drawString(percent, cx + (size - f2.stringWidth(percent)) / 2, ty);
                ty += f2.getHeight();
                g.setFont(f3);
                g.drawString(status, cx + (size - f3.stringWidth(status)) / 2, ty);
            }
        };
    }

    private Container poolSharesCard() {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.WHITE, 2f, 2f, 2f, 1, 1);

        Container titles = new Container(BoxLayout.y());
        titles.add(txt("Pool Shares", SAVI.regular(1.9f), SAVI.BLACK));
        titles.add(txt("Breakdown by individual member", SAVI.regular(1.9f), SAVI.BLACK));

        Label icon = new Label();
        icon.getAllStyles().setFgColor(SAVI.BLACK);
        icon.getAllStyles().setBgTransparency(0);
        FontImage.setMaterialIcon(icon, FontImage.MATERIAL_PIE_CHART, 3f);

        Container head = new Container(new BorderLayout());
        head.add(BorderLayout.WEST, titles);
        head.add(BorderLayout.EAST, icon);

        boolean hasShares = summary.hasShares();
        Container donutWrap = FlowLayout.encloseCenter(donut(summary.getShareValues(),
                summary.getGoalLabel(), summary.getGoalPercent(), summary.getStatusText(), !hasShares));
        donutWrap.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        donutWrap.getAllStyles().setMargin(2, 1, 0, 0);

        card.add(BorderLayout.NORTH, head);
        card.add(BorderLayout.CENTER, donutWrap);

        // the legend is only shown when there are shares to list
        if (hasShares) {
            java.util.List<Summary.MemberShare> members = summary.getMembers();
            Container legend = new Container(new GridLayout((members.size() + 1) / 2, 2));
            for (Summary.MemberShare m : members) {
                legend.add(txt(m.getLegendText(), SAVI.bold(1.7f), SAVI.DARK_BLUE));
            }
            for (int i = 0; i < legend.getComponentCount(); i++) {
                Style ls = legend.getComponentAt(i).getAllStyles();
                ls.setMarginUnit(Style.UNIT_TYPE_DIPS);
                ls.setMargin(1, 1, 2, 0);
                ls.setAlignment(Component.CENTER);
            }
            card.add(BorderLayout.SOUTH, legend);
        }
        return card;
    }

    private Container memberCell(String initial, String name, String sub, boolean bold) {
        Label ini = txt(initial, bold ? SAVI.bold(1.9f) : SAVI.regular(1.9f), SAVI.BLACK);
        ini.setVerticalAlignment(Component.CENTER);
        ini.setPreferredW(Display.getInstance().convertToPixels(8f));
        ini.getAllStyles().setAlignment(Component.LEFT);

        Container texts = new Container(BoxLayout.y());
        texts.add(txt(name, bold ? SAVI.bold(1.9f) : SAVI.regular(1.9f), SAVI.BLACK));
        texts.add(txt(sub, SAVI.regular(1.9f), SAVI.BLACK));

        Container c = new Container(new BorderLayout());
        c.add(BorderLayout.WEST, ini);
        c.add(BorderLayout.CENTER, texts);
        return c;
    }

    private Container memberRow(String initial, String name, String sub,
                                String amount, String share, boolean bold) {
        Font f = bold ? SAVI.bold(1.9f) : SAVI.regular(1.9f);
        Container row = tableRow(
                memberCell(initial, name, sub, bold),
                vcenter(txt(amount, f, SAVI.BLACK)),
                vcenter(txt(share, f, SAVI.BLACK)));
        row.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        row.getAllStyles().setMargin(1.5f, 1.5f, 0, 0);
        return row;
    }

    private Container memberSummaryCard() {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.WHITE, 2f, 2f, 2f, 1, 1);

        Container title = new Container(new BorderLayout());
        title.add(BorderLayout.WEST, txt("Member Summary", SAVI.regular(1.9f), SAVI.BLACK));
        title.add(BorderLayout.EAST, txt("Active Cycle", SAVI.bold(1.9f), SAVI.BLACK));

        Container tableHead = tableRow(
                txt("MEMBERS", SAVI.regular(1.9f), SAVI.BLACK),
                txt("AMOUNT", SAVI.regular(1.9f), SAVI.BLACK),
                txt("SHARE", SAVI.regular(1.9f), SAVI.BLACK));
        tableHead.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        tableHead.getAllStyles().setMargin(2.5f, 1, 0, 0);

        Container rows = new Container(BoxLayout.y());
        rows.add(tableHead);

        // one row per member (biggest share first); the logged-in user is shown in bold as "You"
        java.util.List<Summary.MemberShare> members = summary.getMembers();
        for (Summary.MemberShare m : members) {
            rows.add(memberRow(m.isYou() ? "You" : m.getInitial(), m.getDisplayName(), m.getPaidText(),
                    m.getAmountText(), m.getPercentText(), m.isYou()));
        }
        if (members.isEmpty()) {
            Label none = txt("No members yet.", SAVI.regular(1.9f), SAVI.SLATE_GRAY);
            none.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
            none.getAllStyles().setMargin(1.5f, 1.5f, 0, 0);
            rows.add(none);
        }

        card.add(BorderLayout.NORTH, title);
        card.add(BorderLayout.CENTER, rows);
        return card;
    }

    private GeneralPath curve(float[] px, float[] py, boolean area, float bottom) {
        GeneralPath p = new GeneralPath();
        int n = px.length;
        p.moveTo(px[0], py[0]);
        for (int i = 0; i < n - 1; i++) {
            int i0 = Math.max(i - 1, 0);
            int i3 = Math.min(i + 2, n - 1);
            float c1x = px[i] + (px[i + 1] - px[i0]) / 6f;
            float c1y = py[i] + (py[i + 1] - py[i0]) / 6f;
            float c2x = px[i + 1] - (px[i3] - px[i]) / 6f;
            float c2y = py[i + 1] - (py[i3] - py[i]) / 6f;
            p.curveTo(c1x, c1y, c2x, c2y, px[i + 1], py[i + 1]);
        }
        if (area) {
            p.lineTo(px[n - 1], bottom);
            p.lineTo(px[0], bottom);
            p.closePath();
        }
        return p;
    }

    // empty = no shares yet: everything gray, no bubble, and the "No shares" message in the middle
    private Component lineChart(final float[] values, final String tip, final boolean empty,
                                final String emptyMsg) {
        final int lineColor = empty ? SAVI.SLATE_GRAY : 0x6D28D9;
        final int fillColor = empty ? SAVI.SLATE_GRAY : 0x7C3AED;

        return new Component() {
            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(
                        Display.getInstance().convertToPixels(30f),
                        Display.getInstance().convertToPixels(32f));
            }

            @Override
            public void paint(Graphics g) {
                int x = getX(), y = getY(), w = getWidth(), h = getHeight();
                int n = values.length;
                int r = Display.getInstance().convertToPixels(0.9f);
                float top = y + h * 0.30f;
                float bottom = y + h - r;

                g.setAntiAliased(true);
                g.setAlpha(255);

                // light grid lines
                g.setColor(0xE5E7EB);
                for (int i = 0; i < 3; i++) {
                    int gy = (int) (top + (bottom - top) * i / 2f);
                    g.drawLine(x, gy, x + w, gy);
                }

                float[] px = new float[n];
                float[] py = new float[n];
                for (int i = 0; i < n; i++) {
                    px[i] = x + (i + 0.5f) * w / n;
                    py[i] = bottom - values[i] * (bottom - top);
                }

                if (g.isShapeSupported()) {
                    // soft fill under the line
                    g.setColor(fillColor);
                    g.setAlpha(45);
                    g.fillShape(curve(px, py, true, bottom));
                    g.setAlpha(255);

                    // line
                    g.setColor(lineColor);
                    g.drawShape(curve(px, py, false, bottom),
                            new Stroke(Display.getInstance().convertToPixels(0.6f),
                                    Stroke.CAP_ROUND, Stroke.JOIN_ROUND, 1f));
                } else {
                    g.setColor(lineColor);
                    for (int i = 0; i < n - 1; i++) {
                        g.drawLine((int) px[i], (int) py[i], (int) px[i + 1], (int) py[i + 1]);
                    }
                }

                for (int i = 0; i < n - 1; i++) {
                    g.setColor(lineColor);
                    g.fillArc((int) px[i] - r / 2 - 1, (int) py[i] - r / 2 - 1, r + 2, r + 2, 0, 360);
                    g.setColor(0xFFFFFF);
                    g.fillArc((int) px[i] - r / 2 + 1, (int) py[i] - r / 2 + 1, r - 2, r - 2, 0, 360);
                }
                g.setColor(lineColor);
                g.fillArc((int) px[n - 1] - r / 2 - 1, (int) py[n - 1] - r / 2 - 1, r + 2, r + 2, 0, 360);

                if (empty) {
                    Font mf = SAVI.semiBold(1.6f);
                    String[] lines = SummaryPage.this.wrap(emptyMsg, mf, (int) (w * 0.8f));
                    int lh = mf.getHeight();
                    int ty = (int) ((top + bottom) / 2f) - (lines.length * lh) / 2;
                    g.setColor(SAVI.SLATE_GRAY);
                    g.setFont(mf);
                    for (int i = 0; i < lines.length; i++) {
                        g.drawString(lines[i], x + (w - mf.stringWidth(lines[i])) / 2, ty + i * lh);
                    }
                    return;
                }

                Font f = SAVI.semiBold(1.5f);
                int padX = Display.getInstance().convertToPixels(1.5f);
                int padY = Display.getInstance().convertToPixels(0.8f);
                int bw = f.stringWidth(tip) + 2 * padX;
                int bh = f.getHeight() + 2 * padY;
                int bx = Math.max(x, (int) px[n - 1] - bw + r);
                int by = Math.max(y, (int) py[n - 1] - bh - r * 2);
                g.setColor(0xFFFFFF);
                g.fillRoundRect(bx, by, bw, bh, bh / 2, bh / 2);
                g.setColor(0xE9D5FF);
                g.drawRoundRect(bx, by, bw, bh, bh / 2, bh / 2);
                g.setColor(0x1E293B);
                g.setFont(f);
                g.drawString(tip, bx + padX, by + padY);
            }
        };
    }

    // onModeChange runs after W / M / Y is tapped, so show() can redraw this card
    Container monthlyChartCard(final ChartData summary, final Runnable onModeChange) {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.WHITE, 2f, 2f, 2f, 1, 1);

        Container titles = new Container(BoxLayout.y());
        titles.add(txt(summary.getChartTitle(), SAVI.regular(1.9f), SAVI.BLACK));
        titles.add(txt(summary.getChartSubtitle(), SAVI.regular(1.9f), SAVI.BLACK));

        int mode = summary.getChartMode();
        Container toggles = new Container(new FlowLayout(Component.RIGHT, Component.TOP));
        toggles.add(toggle("W", mode == Summary.WEEK, () -> {
            summary.setChartMode(Summary.WEEK);
            onModeChange.run();
        }));
        toggles.add(toggle("M", mode == Summary.MONTH, () -> {
            summary.setChartMode(Summary.MONTH);
            onModeChange.run();
        }));
        toggles.add(toggle("Y", mode == Summary.YEAR, () -> {
            summary.setChartMode(Summary.YEAR);
            onModeChange.run();
        }));

        Container head = new Container(new BorderLayout());
        head.add(BorderLayout.WEST, titles);
        head.add(BorderLayout.EAST, toggles);

        // chart + month labels
        boolean hasShares = summary.hasShares();
        float[] values = summary.getChartValues();
        String[] months = summary.getChartLabels();

        Container monthRow = new Container(new GridLayout(1, months.length));
        for (int i = 0; i < months.length; i++) {
            boolean last = i == months.length - 1;
            Label m = txt(months[i], last ? SAVI.bold(1.8f) : SAVI.regular(1.8f), SAVI.BLACK);
            m.getAllStyles().setAlignment(Component.CENTER);
            monthRow.add(m);
        }
        monthRow.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        monthRow.getAllStyles().setMargin(1, 1.5f, 0, 0);

        Container chartBox = new Container(new BorderLayout());
        chartBox.add(BorderLayout.CENTER, lineChart(values, summary.getChartTip(), !hasShares, summary.getEmptyMessage()));
        chartBox.add(BorderLayout.SOUTH, monthRow);

        Container achieved = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        if (summary.isTargetAchieved()) {
            Label check = new Label();
            check.getAllStyles().setFgColor(SAVI.BLACK);
            check.getAllStyles().setBgTransparency(0);
            check.getAllStyles().setMargin(0, 0, 0, 1);
            FontImage.setMaterialIcon(check, FontImage.MATERIAL_CHECK_CIRCLE, 3f);
            achieved.add(check);
        }
        achieved.add(txt(summary.getFooterText(), SAVI.bold(1.8f), SAVI.BLACK));

        com.codename1.ui.Button details = new com.codename1.ui.Button("Details");
        Style ds = details.getAllStyles();
        ds.setFgColor(SAVI.BLACK);
        ds.setBgTransparency(0);
        ds.setFont(SAVI.bold(1.8f));
        ds.setMargin(0, 0, 0, 0);
        ds.setPadding(0, 0, 0, 0);
        details.setTextPosition(Component.LEFT);
        details.addActionListener(e-> {new PeoplePage().show();});
        FontImage.setMaterialIcon(details, FontImage.MATERIAL_CHEVRON_RIGHT, 3f);

        Container footer = new Container(new BorderLayout());
        footer.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        footer.getAllStyles().setMargin(1, 0, 0, 0);
        footer.add(BorderLayout.WEST, achieved);
        footer.add(BorderLayout.EAST, details);

        card.add(BorderLayout.NORTH, head);
        card.add(BorderLayout.CENTER, chartBox);
        card.add(BorderLayout.SOUTH, footer);
        return card;
    }

    // W / M / Y: a tappable letter, bold when it is the selected one
    private com.codename1.ui.Button toggle(String letter, boolean selected, final Runnable onClick) {
        com.codename1.ui.Button b = new com.codename1.ui.Button(letter);
        b.setUIID("Label");
        Style st = b.getAllStyles();
        st.setFont(selected ? SAVI.bold(1.9f) : SAVI.regular(1.9f));
        st.setFgColor(SAVI.BLACK);
        st.setBgTransparency(0);
        st.setPadding(0, 0, 0, 0);
        st.setMarginUnit(Style.UNIT_TYPE_DIPS);
        st.setMargin(0, 0, 1, 1);
        b.addActionListener(e -> onClick.run());
        return b;
    }

    // opens on the group with the newest contribution
    public void show() {
        show(null);
    }

    // opens on the group with this name (used by the group popup)
    public void show(String groupName) {
        summary = new Summary(SAVI.user, SAVI.groupList.getGroups());
        if (groupName != null) {
            summary.selectGroup(groupName);
        }

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        // the chart card sits in a holder so W / M / Y can swap it without rebuilding the page
        final Container chartHolder = new Container(new BorderLayout());
        final Runnable[] refreshChart = new Runnable[1];
        refreshChart[0] = () -> {
            chartHolder.removeAll();
            chartHolder.add(BorderLayout.CENTER, monthlyChartCard(summary, refreshChart[0]));
            Form f = chartHolder.getComponentForm();
            if (f != null) {
                f.revalidate();
            }
        };
        refreshChart[0].run();

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        mainContents.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        mainContents.getAllStyles().setPadding(0, 3, 0, 0);
        mainContents.add(poolCard());
        mainContents.add(poolSharesCard());
        mainContents.add(memberSummaryCard());
        mainContents.add(chartHolder);

        base.add(BorderLayout.NORTH, summaryHeader(false));
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}