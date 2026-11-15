package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.table.TableLayout;
import com.savi.app.SAVI;

public class SummaryPage {

    static final String PESO = "P";

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

    private Container header() {
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

        Label spacer = new Label();
        spacer.getAllStyles().setMargin(0, 0, 0, 0);
        spacer.setPreferredSize(back.getPreferredSize());

        Label title = txt("Group Savings Summary", SAVI.bold(2.5f), SAVI.WHITE);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container pill = new Container(new BorderLayout());
        styles(pill, SAVI.DARK_PURPLE, 1.5f, 1.5f, 2f, 1f, 0.4f);
        pill.add(BorderLayout.WEST, back);
        pill.add(BorderLayout.CENTER, title);
        pill.add(BorderLayout.EAST, spacer);

        Container wrap = new Container(new BorderLayout());
        wrap.add(BorderLayout.CENTER, pill);
        return wrap;
    }

    private Container poolCard() {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.DARK_PURPLE, 3f, 4f, 2f, 0.5f, 0.5f);

        Label name = txt("DECEMBER VACATION", SAVI.bold(2.5f), SAVI.WHITE);
        Label edit = new Label();
        edit.getAllStyles().setFgColor(SAVI.WHITE);
        edit.getAllStyles().setBgTransparency(0);
        edit.getAllStyles().setMargin(0, 0, 1, 0);

        Container left = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        left.add(name).add(edit);

        Label chev = new Label();
        Style cs = chev.getAllStyles();
        cs.setFgColor(SAVI.DARK_PURPLE);
        cs.setBgTransparency(0);
        cs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        cs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        cs.setPadding(0.3f, 0.3f, 0.3f, 0.3f);
        cs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(chev, FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, 3f);

        Container topRow = new Container(new BorderLayout());
        topRow.add(BorderLayout.WEST, left);
        topRow.add(BorderLayout.EAST, chev);

        Container total = new Container(BoxLayout.y());
        total.add(txt("Total Pool", SAVI.regular(2f), SAVI.WHITE));
        total.add(txt(PESO + "12,450.00", SAVI.bold(2.6f), SAVI.WHITE));

        Label members = txt("Members", SAVI.regular(2f), SAVI.WHITE);
        members.getAllStyles().setAlignment(Component.RIGHT);

        Label pill = txt("4 Contributors", SAVI.semiBold(2.3f), SAVI.WHITE);

        Container right = new Container(BoxLayout.y());
        right.add(members);
        right.add(FlowLayout.encloseRight(pill));

        Container bottomRow = new Container(new BorderLayout());
        bottomRow.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        bottomRow.getAllStyles().setMargin(1.5f, 0, 0, 0);
        bottomRow.add(BorderLayout.WEST, total);
        bottomRow.add(BorderLayout.EAST, right);

        card.add(BorderLayout.NORTH, topRow);
        card.add(BorderLayout.SOUTH, bottomRow);
        return card;
    }

    private Component donut() {
        final int[] colors = {0x6D0FD6, 0xA066FF, 0x047857, 0x5EE0B0};
        final float[] shares = {40, 20, 20, 20};

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

                int start = 90;
                for (int i = 0; i < colors.length; i++) {
                    int sweep = Math.round(-shares[i] * 3.6f);
                    g.setColor(colors[i]);
                    g.fillArc(cx, cy, size, size, start, sweep);
                    start += sweep;
                }

                int t = (int) (size * 0.2f);
                g.setColor(0xFFFFFF);
                g.fillArc(cx + t, cy + t, size - 2 * t, size - 2 * t, 0, 360);

                Font f1 = SAVI.regular(1.8f);
                Font f2 = SAVI.bold(2.2f);
                Font f3 = SAVI.bold(1.8f);
                int total = f1.getHeight() + f2.getHeight() + f3.getHeight();
                int ty = cy + (size - total) / 2;

                g.setColor(0x1E293B);
                g.setFont(f1);
                g.drawString("GOAL MET", cx + (size - f1.stringWidth("GOAL MET")) / 2, ty);
                ty += f1.getHeight();
                g.setFont(f2);
                g.drawString("100%", cx + (size - f2.stringWidth("100%")) / 2, ty);
                ty += f2.getHeight();
                g.setFont(f3);
                g.drawString("On Schedule", cx + (size - f3.stringWidth("On Schedule")) / 2, ty);
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

        Container donutWrap = FlowLayout.encloseCenter(donut());
        donutWrap.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        donutWrap.getAllStyles().setMargin(2, 1, 0, 0);

        Container legend = new Container(new GridLayout(2, 2));
        legend.add(txt("Alex (You) \u2022 40%", SAVI.bold(1.7f), SAVI.DARK_BLUE));
        legend.add(txt("Faye \u2022 20%", SAVI.bold(1.7f), SAVI.DARK_BLUE));
        legend.add(txt("Sam \u2022 20%", SAVI.bold(1.7f), SAVI.DARK_BLUE));
        legend.add(txt("Air \u2022 20%", SAVI.bold(1.7f), SAVI.DARK_BLUE));
        for (int i = 0; i < legend.getComponentCount(); i++) {
            Style ls = legend.getComponentAt(i).getAllStyles();
            ls.setMarginUnit(Style.UNIT_TYPE_DIPS);
            ls.setMargin(1, 1, 2, 0);
            ls.setAlignment(Component.CENTER);
        }

        card.add(BorderLayout.NORTH, head);
        card.add(BorderLayout.CENTER, donutWrap);
        card.add(BorderLayout.SOUTH, legend);
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
        rows.add(memberRow("F", "Faye", "Paid Dec 12", PESO + "2,490", "20%", false));
        rows.add(memberRow("S", "Sam", "Paid Dec 10", PESO + "2,490", "20%", false));
        rows.add(memberRow("A", "Air", "Paid Dec 08", PESO + "2,490", "20%", false));
        rows.add(memberRow("You", "Alex (Lead)", "Auto-debited", PESO + "4,980", "40%", true));

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

    private Component lineChart(final float[] values, final String tip) {
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
                    g.setColor(0x7C3AED);
                    g.setAlpha(45);
                    g.fillShape(curve(px, py, true, bottom));
                    g.setAlpha(255);

                    // line
                    g.setColor(0x6D28D9);
                    g.drawShape(curve(px, py, false, bottom),
                            new Stroke(Display.getInstance().convertToPixels(0.6f),
                                    Stroke.CAP_ROUND, Stroke.JOIN_ROUND, 1f));
                } else {
                    g.setColor(0x6D28D9);
                    for (int i = 0; i < n - 1; i++) {
                        g.drawLine((int) px[i], (int) py[i], (int) px[i + 1], (int) py[i + 1]);
                    }
                }

                for (int i = 0; i < n - 1; i++) {
                    g.setColor(0x6D28D9);
                    g.fillArc((int) px[i] - r / 2 - 1, (int) py[i] - r / 2 - 1, r + 2, r + 2, 0, 360);
                    g.setColor(0xFFFFFF);
                    g.fillArc((int) px[i] - r / 2 + 1, (int) py[i] - r / 2 + 1, r - 2, r - 2, 0, 360);
                }
                g.setColor(0x6D28D9);
                g.fillArc((int) px[n - 1] - r / 2 - 1, (int) py[n - 1] - r / 2 - 1, r + 2, r + 2, 0, 360);

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

    private Container monthlyChartCard() {
        Container card = new Container(new BorderLayout());
        styles(card, SAVI.WHITE, 2f, 2f, 2f, 1, 1);

        Container titles = new Container(BoxLayout.y());
        titles.add(txt("MONTHLY CHART", SAVI.regular(1.9f), SAVI.BLACK));
        titles.add(txt("Growth from Jul to Dec", SAVI.regular(1.9f), SAVI.BLACK));

        Container toggles = new Container(new FlowLayout(Component.RIGHT, Component.TOP));
        toggles.add(toggle("W", false)).add(toggle("M", true)).add(toggle("Y", false));

        Container head = new Container(new BorderLayout());
        head.add(BorderLayout.WEST, titles);
        head.add(BorderLayout.EAST, toggles);

        // chart + month labels
        float[] values = {0.04f, 0.20f, 0.32f, 0.55f, 0.70f, 1.0f};
        String[] months = {"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

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
        chartBox.add(BorderLayout.CENTER, lineChart(values, PESO + "12.45k"));
        chartBox.add(BorderLayout.SOUTH, monthRow);

        Label check = new Label();
        check.getAllStyles().setFgColor(SAVI.BLACK);
        check.getAllStyles().setBgTransparency(0);
        check.getAllStyles().setMargin(0, 0, 0, 1);
        FontImage.setMaterialIcon(check, FontImage.MATERIAL_CHECK_CIRCLE, 3f);

        Container achieved = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        achieved.add(check).add(txt("Vacation Target Achieved", SAVI.bold(1.8f), SAVI.BLACK));

        com.codename1.ui.Button details = new com.codename1.ui.Button("Details");
        Style ds = details.getAllStyles();
        ds.setFgColor(SAVI.BLACK);
        ds.setBgTransparency(0);
        ds.setFont(SAVI.bold(1.8f));
        ds.setMargin(0, 0, 0, 0);
        ds.setPadding(0, 0, 0, 0);
        details.setTextPosition(Component.LEFT);
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

    private Label toggle(String letter, boolean selected) {
        Label l = txt(letter, selected ? SAVI.bold(1.9f) : SAVI.regular(1.9f), SAVI.BLACK);
        l.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        l.getAllStyles().setMargin(0, 0, 1, 1);
        return l;
    }


    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        mainContents.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        mainContents.getAllStyles().setPadding(0, 3, 0, 0);
        mainContents.add(poolCard());
        mainContents.add(poolSharesCard());
        mainContents.add(memberSummaryCard());
        mainContents.add(monthlyChartCard());

        base.add(BorderLayout.NORTH, header());
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}