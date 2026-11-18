package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;


public class ViewAllTransacPage {

    static final int PAGE_BG = 0xF7F5FF;
    static final int HEADER_START = 0x7A3BEA;
    static final int HEADER_END = 0x5D30D2;
    static final int HEADER_LINE = 0x9068EE;
    static final int HEADER_LABEL = 0xD7CAFA;
    static final int LIGHT_VIOLET = 0xEFEAFE;
    static final int VIOLET_STROKE = 0xDDD3FB;
    static final int LINE_GRAY = 0xE8EAF0;
    static final int SEARCH_STROKE = 0xECE8F8;
    static final int MUTED = 0x8B93A7;
    static final int NAVY = 0x111827;

    static final int RED = 0xE5365B;
    static final int RED_SOFT = 0xFDE4E9;
    static final int ROW_MISSED_BG = 0xFFF5F6;
    static final int ROW_MISSED_STROKE = 0xFBE1E6;
    static final int GREEN = 0x059669;
    static final int GREEN_SOFT = 0xE3F8EE;

    // ---------- sample data ----------

    static class Txn {
        final String title, date, month, type; // type: DEPOSIT, MISSED, ADJUSTMENT
        final double amount;
        final boolean settled;

        Txn(String title, String date, String month, String type, double amount, boolean settled) {
            this.title = title;
            this.date = date;
            this.month = month;
            this.type = type;
            this.amount = amount;
            this.settled = settled;
        }
    }

    static final Txn[] DATA = {
            new Txn("Missed Contribution", "Dec 12, 2026", "DECEMBER 2026", "MISSED", 35, false),
            new Txn("Daily Contribution", "Dec 18, 2026", "DECEMBER 2026", "DEPOSIT", 50, true),
            new Txn("Kickoff Group Deposit", "Nov 28, 2026", "NOVEMBER 2026", "DEPOSIT", 40, true),
            new Txn("Food Contribution", "Nov 15, 2026", "NOVEMBER 2026", "DEPOSIT", 50, true)
    };

    static final String[] CHIP_NAMES = {"All", "Deposits", "Missed", "Adjustments"};
    static final String[] CHIP_TYPES = {null, "DEPOSIT", "MISSED", "ADJUSTMENT"};

    // screen state
    private Container listContainer;
    private Rounded[] chips = new Rounded[4];
    private Button[] chipButtons = new Button[4];
    private int selectedChip = 0;
    private String query = "";

    // ---------- general helpers ----------

    static int mm(float v) {
        return Display.getInstance().convertToPixels(v);
    }

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

    // colors the status bar / title area so it doesn't show up as a white strip
    static void paintStatusBar(Form f, int color) {
        Container t = f.getTitleArea();
        t.getAllStyles().setBgColor(color);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(color);
            s.setBgTransparency(255);
        }
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
                shadowColor(SAVI.BRIGHT_VIOLET));
        setMargins(cont, 0.5f, 0.5f, 2, 2);
        setPadding(cont, paddingTB, paddingTB, paddingLR, paddingLR);
    }

    static Container divider(int color, float marginTB, float marginLR) {
        Container line = new Container();
        line.setPreferredH(mm(0.2f));
        Style s = line.getAllStyles();
        s.setBgColor(color);
        s.setBgTransparency(255);
        setMargins(line, marginTB, marginTB, marginLR, marginLR);
        return line;
    }

    static Label text(String t, Font f, int color, int align) {
        Label l = SAVI.text(t, f, color);
        l.getAllStyles().setAlignment(align);
        return l;
    }

    // ---------- shapes drawn with fillRoundRect (true circles / pills) ----------

    static class Rounded extends Container {
        private int fill, fillAlpha;
        private final float radiusMM; // 0 = fully round (circle or pill)
        private int strokeColor, strokeAlpha, strokePx;

        Rounded(Layout layout, int fill, int fillAlpha, float radiusMM) {
            super(layout);
            this.fill = fill;
            this.fillAlpha = fillAlpha;
            this.radiusMM = radiusMM;
            getAllStyles().setBgTransparency(0);
        }

        Rounded stroke(int color, int alpha, int px) {
            strokeColor = color;
            strokeAlpha = alpha;
            strokePx = px;
            return this;
        }

        Rounded fixed(float sizeMM) {
            setPreferredW(mm(sizeMM));
            setPreferredH(mm(sizeMM));
            return this;
        }

        void colors(int fill, int strokeColor) {
            this.fill = fill;
            this.strokeColor = strokeColor;
            repaint();
        }

        @Override
        public void paintBackground(Graphics g) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            int arc = radiusMM <= 0 ? Math.min(w, h) : Math.min(Math.min(w, h), mm(radiusMM * 2));
            g.setAntiAliased(true);
            if (fillAlpha > 0) {
                g.setAlpha(fillAlpha);
                g.setColor(fill);
                g.fillRoundRect(x, y, w, h, arc, arc);
            }
            if (strokePx > 0 && strokeAlpha > 0) {
                g.setAlpha(strokeAlpha);
                g.setColor(strokeColor);
                for (int i = 0; i < strokePx; i++) {
                    g.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, arc, arc);
                }
            }
            g.setAlpha(255);
        }
    }

    static Container circleButton(char icon, float sizeMM, float iconMM, int bg, int fg,
                                  ActionListener<ActionEvent> onClick) {
        Button b = new Button();
        b.setUIID("Label");
        b.getAllStyles().setFgColor(fg);
        b.getAllStyles().setAlignment(Component.CENTER);
        FontImage.setMaterialIcon(b, icon, iconMM);
        b.addActionListener(onClick);

        Rounded c = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), bg, 255, 0)
                .stroke(SEARCH_STROKE, 255, 1).fixed(sizeMM);
        c.add(BorderLayout.CENTER, b);
        c.setLeadComponent(b);
        return c;
    }

    static Container squareIcon(char icon, float sizeMM, int bg, int fg) {
        Label l = new Label();
        l.getAllStyles().setFgColor(fg);
        FontImage.setMaterialIcon(l, icon, sizeMM * 0.5f);

        Rounded c = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), bg, 255, 2f).fixed(sizeMM);
        c.add(BorderLayout.CENTER, l);
        return c;
    }

    // pill with a label inside
    static Rounded pill(Label label, int fill, int fillAlpha, int strokeColor, int strokeAlpha) {
        setPadding(label, 0, 0, 0, 0);
        setMargins(label, 0, 0, 0, 0);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), fill, fillAlpha, 0)
                .stroke(strokeColor, strokeAlpha, 1);
        setPadding(p, 0.5f, 0.5f, 1.6f, 1.6f);
        p.add(label);
        return p;
    }

    // pill that is a button
    static Rounded pillButton(String text, Font f, int fg, int fill, int strokeColor, float padLR,
                              ActionListener<ActionEvent> onClick) {
        Button b = new Button(text);
        b.setUIID("Label");
        b.getAllStyles().setFgColor(fg);
        b.getAllStyles().setFont(f);
        setPadding(b, 0, 0, 0, 0);
        setMargins(b, 0, 0, 0, 0);
        b.addActionListener(onClick);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), fill, 255, 0)
                .stroke(strokeColor, 255, 1);
        setPadding(p, 0.8f, 0.8f, padLR, padLR);
        p.add(b);
        p.setLeadComponent(b);
        return p;
    }

    // ---------- gradient card background (masked image, no clipping) ----------

    static Image gradientImage(int w, int h, int c1, int c2, int radiusPx) {
        Image img = Image.createImage(w, h, 0);
        Graphics g = img.getGraphics();
        g.fillLinearGradient(c1, c2, 0, 0, w, h, false);

        Image mask = Image.createImage(w, h, 0xff000000);
        Graphics mg = mask.getGraphics();
        mg.setAntiAliased(true);
        mg.setColor(0xffffff);
        mg.fillRoundRect(0, 0, w, h, radiusPx * 2, radiusPx * 2);
        return img.applyMask(mask.createMask());
    }

    static class GradientCard extends Container {
        private final float radiusMM;
        private Image cache;

        GradientCard(Layout layout, float radiusMM) {
            super(layout);
            this.radiusMM = radiusMM;
        }

        @Override
        public void paintBackground(Graphics g) {
            super.paintBackground(g); // shadow
            int w = getWidth(), h = getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            if (cache == null || cache.getWidth() != w || cache.getHeight() != h) {
                cache = gradientImage(w, h, HEADER_START, HEADER_END, mm(radiusMM));
            }
            g.drawImage(cache, getX(), getY());
        }
    }

    // progress bar with a translucent track (same idea as HomePage.progressBar)
    static Component progressBar(float progress, int trackColor, int trackAlpha, int fillColor, float heightMM) {
        final float p = Math.max(0f, Math.min(1f, progress));

        return new Component() {
            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(mm(30f), mm(heightMM));
            }

            @Override
            public void paint(Graphics g) {
                int x = getX(), y = getY(), w = getWidth(), h = getHeight();
                g.setAntiAliased(true);

                // track
                g.setAlpha(trackAlpha);
                g.setColor(trackColor);
                g.fillRoundRect(x, y, w, h, h, h);

                // fill
                g.setAlpha(255);
                int fw = (int) (w * p);
                if (fw > 0) {
                    g.setColor(fillColor);
                    g.fillRoundRect(x, y, Math.max(fw, h), h, h, h);
                }
            }
        };
    }

    // ---------- top bar ----------

    static Container topBar() {
        Container bar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(bar, 1, 1, 2, 2);

        Container back = circleButton(FontImage.MATERIAL_ARROW_BACK_IOS, 6.5f, 2.6f, SAVI.WHITE, SAVI.DEEP_SLATE,
                e -> new PeoplePage().show());
        Container menu = circleButton(FontImage.MATERIAL_MORE_VERT, 6.5f, 2.6f, SAVI.WHITE, SAVI.DEEP_SLATE,
                e -> {
                });

        Label title = SAVI.text("TRANSACTIONS FOR:", SAVI.bold(1.8f), SAVI.BRIGHT_VIOLET);
        Rounded titlePill = pill(title, LIGHT_VIOLET, 255, VIOLET_STROKE, 255);
        setPadding(titlePill, 0.9f, 0.9f, 2.2f, 2.2f);

        bar.add(BorderLayout.WEST, back);
        bar.add(BorderLayout.CENTER, titlePill);
        bar.add(BorderLayout.EAST, menu);
        return bar;
    }

    // ---------- member summary card ----------

    static Container statBox(String label, String amount) {
        Rounded box = new Rounded(BoxLayout.y(), SAVI.WHITE, 38, 3f).stroke(SAVI.WHITE, 70, 1);
        setPadding(box, 1.8f, 1.8f, 2.2f, 2.2f);
        setMargins(box, 0, 0, 0.5f, 0.5f);

        box.add(SAVI.text(label, SAVI.regular(1.7f), HEADER_LABEL));
        box.add(SAVI.text(amount, SAVI.bold(3f), SAVI.WHITE));
        return box;
    }

    static Container memberCard(String name, String role, String handle, String group, boolean active,
                                double totalPaid, double missed, double goal) {
        Container card = new GradientCard(BoxLayout.y(), 4.5f);
        cardStyles(card, HEADER_START, 4.5f, 2.5f, 2.5f);
        setMargins(card, 0.5f, 1f, 2, 2);

        // avatar
        Label person = new Label();
        person.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(person, FontImage.MATERIAL_PERSON_OUTLINE, 4f);
        Rounded avatar = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), SAVI.WHITE, 45, 2.4f)
                .stroke(SAVI.WHITE, 70, 1).fixed(7.8f);
        avatar.add(BorderLayout.CENTER, person);

        // name + role pill, then @handle . group
        Label nameLabel = SAVI.text(name, SAVI.bold(3.2f), SAVI.WHITE);
        Rounded rolePill = pill(SAVI.text(role, SAVI.regular(1.6f), SAVI.WHITE), SAVI.WHITE, 45, SAVI.WHITE, 0);
        setMargins(rolePill, 0, 0, 1f, 0);
        Container nameRow = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        nameRow.add(nameLabel).add(rolePill);

        Container textCol = new Container(BoxLayout.y());
        textCol.add(nameRow);
        textCol.add(SAVI.text(handle + " \u2022 " + group, SAVI.regular(1.7f), HEADER_LABEL));
        setMargins(textCol, 0, 0, 1.8f, 0);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, avatar);
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(textCol));

        // status pill
        Rounded dot = new Rounded(new BorderLayout(), SAVI.WHITE, 255, 0).fixed(1.1f);
        setMargins(dot, 0, 0, 0, 0.8f);
        Container dotWrap = BoxLayout.encloseYCenter(dot);
        Label statusText = SAVI.text(active ? "Active" : "Inactive", SAVI.bold(1.6f), SAVI.WHITE);
        setPadding(statusText, 0, 0, 0, 0);
        Rounded status = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), SAVI.WHITE, 45, 0)
                .stroke(SAVI.WHITE, 70, 1);
        setPadding(status, 0.7f, 0.7f, 1.6f, 1.6f);
        status.add(dotWrap).add(statusText);

        Container topRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        topRow.add(BorderLayout.WEST, left);
        topRow.add(BorderLayout.EAST, FlowLayout.encloseRightMiddle(status));

        // stat boxes
        Container stats = new Container(new GridLayout(1, 2));
        stats.add(statBox("Total Paid", "P" + String.format("%,.2f", totalPaid)));
        stats.add(statBox("Missed / Unsettled", "P" + String.format("%,.2f", missed)));

        // progress to goal
        float pct = (float) (totalPaid / goal);
        Container progressRow = new Container(new BorderLayout());
        progressRow.add(BorderLayout.WEST, SAVI.text("Progress to Goal (P" + String.format("%,.2f", goal) + ")",
                SAVI.regular(1.7f), SAVI.WHITE));
        progressRow.add(BorderLayout.EAST, SAVI.text((int) (pct * 100) + "%", SAVI.bold(1.7f), SAVI.WHITE));
        setMargins(progressRow, 2f, 0.8f, 0, 0);

        Component bar = progressBar(pct, SAVI.WHITE, 60, SAVI.WHITE, 1f);

        card.add(topRow);
        card.add(divider(HEADER_LINE, 2f, 0));
        card.add(stats);
        card.add(progressRow);
        card.add(bar);
        return card;
    }

    // ---------- search + filter chips ----------

    Container searchBar() {
        Label searchIcon = new Label();
        searchIcon.getAllStyles().setFgColor(MUTED);
        FontImage.setMaterialIcon(searchIcon, FontImage.MATERIAL_SEARCH, 3.2f);

        Label filterIcon = new Label();
        filterIcon.getAllStyles().setFgColor(MUTED);
        FontImage.setMaterialIcon(filterIcon, FontImage.MATERIAL_FILTER_LIST, 3.2f);

        TextField search = new TextField();
        // own placeholder label (the built in hint ignores the page's font), shown under the field
        final Label hint = SAVI.text("Search date...", SAVI.regular(1.9f), 0xA3A9BA);
        setMargins(hint, 0, 0, 1f, 0);
        Style ts = search.getAllStyles();
        ts.setBorder(Border.createEmpty());
        ts.setBgTransparency(0);
        ts.setFgColor(SAVI.DARK_KNIGHT);
        ts.setFont(SAVI.regular(1.9f));
        setPadding(search, 0, 0, 1f, 0);
        search.addDataChangedListener((type, index) -> {
            query = search.getText().trim().toLowerCase();
            hint.setVisible(search.getText().length() == 0);
            buildList();
        });

        Container field = new Container(new LayeredLayout());
        field.add(BoxLayout.encloseYCenter(hint));
        field.add(search);

        Rounded bar = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), SAVI.WHITE, 255, 0)
                .stroke(SEARCH_STROKE, 255, 1);
        setPadding(bar, 0.8f, 0.8f, 2.2f, 2.2f);
        setMargins(bar, 1f, 0.8f, 2, 2);
        bar.add(BorderLayout.WEST, searchIcon);
        bar.add(BorderLayout.CENTER, field);
        bar.add(BorderLayout.EAST, filterIcon);
        return bar;
    }

    static int countFor(String type) {
        int n = 0;
        for (Txn t : DATA) {
            if (type == null || t.type.equals(type)) {
                n++;
            }
        }
        return n;
    }

    void styleChip(int i) {
        boolean selected = i == selectedChip;
        chips[i].colors(selected ? NAVY : SAVI.WHITE, selected ? NAVY : LINE_GRAY);
        chipButtons[i].getAllStyles().setFgColor(selected ? SAVI.WHITE : SAVI.DARK_KNIGHT);
        chipButtons[i].getAllStyles().setFont(selected ? SAVI.bold(1.8f) : SAVI.semiBold(1.8f));
        chipButtons[i].repaint();
    }

    Container chipsRow() {
        Container row = new Container(BoxLayout.x());
        row.setScrollableX(true);
        setMargins(row, 0.5f, 1f, 2, 0);

        for (int i = 0; i < CHIP_NAMES.length; i++) {
            final int index = i;
            Button b = new Button(CHIP_NAMES[i] + " (" + countFor(CHIP_TYPES[i]) + ")");
            b.setUIID("Label");
            setPadding(b, 0, 0, 0, 0);
            setMargins(b, 0, 0, 0, 0);
            b.addActionListener(e -> {
                int old = selectedChip;
                selectedChip = index;
                styleChip(old);
                styleChip(index);
                buildList();
            });

            Rounded chip = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), SAVI.WHITE, 255, 0)
                    .stroke(LINE_GRAY, 255, 1);
            setPadding(chip, 1f, 1f, 2.4f, 2.4f);
            setMargins(chip, 0, 0, 0, 1.2f);
            chip.add(b);
            chip.setLeadComponent(b);

            chips[i] = chip;
            chipButtons[i] = b;
            row.add(chip);
        }
        for (int i = 0; i < chips.length; i++) {
            styleChip(i);
        }
        return row;
    }

    static Container txRow(Txn t) {
        boolean missed = t.type.equals("MISSED");
        boolean deposit = t.type.equals("DEPOSIT");

        int accent = missed ? RED : GREEN;
        int soft = missed ? RED_SOFT : GREEN_SOFT;
        char icon = missed ? FontImage.MATERIAL_WARNING : FontImage.MATERIAL_ARROW_DOWNWARD;
        Container iconBox = squareIcon(icon, 6.5f, soft, accent);

        Container info = new Container(BoxLayout.y());
        info.add(SAVI.text(t.title, SAVI.bold(2.1f), SAVI.DARK_KNIGHT));
        info.add(SAVI.text(t.date, SAVI.regular(1.7f), MUTED));
        if (missed && !t.settled) {
            Rounded overdue = pill(SAVI.text("Overdue", SAVI.bold(1.5f), RED), RED_SOFT, 255, RED_SOFT, 0);
            Rounded settle = pillButton("Settle Now \u2192", SAVI.semiBold(1.5f), SAVI.BRIGHT_VIOLET,
                    SAVI.WHITE, VIOLET_STROKE, 1.8f, e -> {
                        // TODO: open the settle / pay page
                    });
            setMargins(settle, 0, 0, 0.8f, 0);
            Container tags = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
            tags.add(overdue).add(settle);
            setMargins(tags, 0.4f, 0, 0, 0);
            info.add(tags);
        }
        setMargins(info, 0, 0, 1.8f, 0);

        // right column: amount + status
        String sign = deposit ? "+" : "";
        Container amountCol = new Container(BoxLayout.y());
        amountCol.add(text(sign + "P" + String.format("%,.2f", t.amount), SAVI.bold(2.3f), accent, Component.RIGHT));
        Label status = text(t.settled ? "Settled" : "Unpaid", missed ? SAVI.bold(1.5f) : SAVI.regular(1.5f),
                missed ? RED : MUTED, Component.RIGHT);
        amountCol.add(status);

        Container row;
        if (missed) {
            Rounded r = new Rounded(new BorderLayout(), ROW_MISSED_BG, 255, 3.5f).stroke(ROW_MISSED_STROKE, 255, 1);
            setMargins(r, 0.6f, 0.6f, 0.6f, 0.6f);
            setPadding(r, 1.8f, 1.8f, 1.8f, 1.8f);
            row = r;
            row.add(BorderLayout.WEST, BorderLayout.north(iconBox));
            row.add(BorderLayout.CENTER, info);
            row.add(BorderLayout.EAST, BorderLayout.north(amountCol));
        } else {
            row = new Container(new BorderLayout());
            setPadding(row, 1.8f, 1.8f, 2.4f, 2.4f);
            row.add(BorderLayout.WEST, BoxLayout.encloseYCenter(iconBox));
            row.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(info));
            row.add(BorderLayout.EAST, BoxLayout.encloseYCenter(amountCol));
        }
        return row;
    }

    // month title + "N Entries"
    static Container monthHeader(String month, int entries, boolean highlight) {
        Container row = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(row, 1.5f, 0.5f, 2.4f, 2);
        row.add(BorderLayout.WEST, SAVI.text(month, SAVI.bold(1.7f), MUTED));

        String count = entries + (entries == 1 ? " Entry" : " Entries");
        if (highlight) {
            Rounded pill = pill(SAVI.text(count, SAVI.bold(1.6f), SAVI.BRIGHT_VIOLET), LIGHT_VIOLET, 255,
                    VIOLET_STROKE, 255);
            row.add(BorderLayout.EAST, FlowLayout.encloseRightMiddle(pill));
        } else {
            row.add(BorderLayout.EAST, SAVI.text(count, SAVI.regular(1.6f), MUTED));
        }
        return row;
    }

    boolean matches(Txn t) {
        String type = CHIP_TYPES[selectedChip];
        if (type != null && !t.type.equals(type)) {
            return false;
        }
        if (query.length() == 0) {
            return true;
        }
        return t.date.toLowerCase().contains(query) || t.month.toLowerCase().contains(query)
                || t.title.toLowerCase().contains(query);
    }

    // rebuilds the month sections from DATA using the current chip + search text
    void buildList() {
        listContainer.removeAll();

        String currentMonth = null;
        Container card = null;
        int sections = 0;
        for (int i = 0; i < DATA.length; i++) {
            Txn t = DATA[i];
            if (!matches(t)) {
                continue;
            }
            if (!t.month.equals(currentMonth)) {
                currentMonth = t.month;
                int entries = 0;
                for (Txn x : DATA) {
                    if (x.month.equals(currentMonth) && matches(x)) {
                        entries++;
                    }
                }
                listContainer.add(monthHeader(currentMonth, entries, sections == 0));
                card = new Container(BoxLayout.y());
                cardStyles(card, SAVI.WHITE, 5f, 0.8f, 0f);
                setMargins(card, 0.5f, 1f, 2, 2);
                listContainer.add(card);
                sections++;
            } else {
                card.add(divider(LINE_GRAY, 0, 2.4f));
            }
            card.add(txRow(t));
        }

        if (sections == 0) {
            Label empty = text("No transactions found", SAVI.regular(2f), MUTED, Component.CENTER);
            setMargins(empty, 4, 0, 0, 0);
            listContainer.add(empty);
        }
        listContainer.revalidate();
    }

    // ---------- page ----------

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(PAGE_BG);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        paintStatusBar(base, PAGE_BG);

        listContainer = new Container(BoxLayout.y());

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);

        mainContents.add(memberCard("Air", "Member", "@rei", "December Vacation", true, 130, 35, 200));
        mainContents.add(searchBar());
        mainContents.add(chipsRow());
        mainContents.add(listContainer);
        buildList();

        base.add(BorderLayout.NORTH, topBar());
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}