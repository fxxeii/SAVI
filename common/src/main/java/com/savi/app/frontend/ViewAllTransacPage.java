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
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;
import com.savi.app.backend.Home;
import com.savi.app.backend.PeopleBackend;

public class ViewAllTransacPage {

    // only three filter chips: All, Deposits, Missed
    static final String[] CHIP_NAMES = {"All", "Deposits", "Missed"};
    static final String[] CHIP_TYPES = {null, "DEPOSIT", "MISSED"};

    // screen state
    private Container listContainer;
    private Chip[] chips = new Chip[3];
    private int selectedChip = 0;
    private String query = "";

    // data comes from the backend (PeopleBackend), for the member picked on the People page
    private final PeopleBackend.Member member;
    private final PeopleBackend.Txn[] txns;

    public ViewAllTransacPage(PeopleBackend.Member member) {
        this.member = member;
        this.txns = PeopleBackend.getTransactions(member);
    }

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
                .stroke(SAVI.BORDER_COLOR, 255, 1).fixed(sizeMM);
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

    // flat pill with a label inside (no stroke)
    static Rounded pill(Label label, int fill, int fillAlpha) {
        setPadding(label, 0, 0, 0, 0);
        setMargins(label, 0, 0, 0, 0);

        Rounded p = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), fill, fillAlpha, 0);
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

    // progress bar with a translucent track
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

                g.setAlpha(trackAlpha);
                g.setColor(trackColor);
                g.fillRoundRect(x, y, w, h, h, h);

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

    // back button + title only (the 3-dot menu button was removed)
    static Container topBar() {
        Container bar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        setMargins(bar, 1, 1, 2, 2);

        Container back = circleButton(FontImage.MATERIAL_ARROW_BACK_IOS, 6.5f, 2.6f, SAVI.WHITE, SAVI.DEEP_SLATE,
                e -> new PeoplePage().show());

        Label title = SAVI.text("TRANSACTIONS FOR:", SAVI.bold(1.8f), SAVI.BRIGHT_VIOLET);
        Rounded titlePill = pill(title, SAVI.PALE_PURPLE, 255);
        setPadding(titlePill, 0.9f, 0.9f, 2.2f, 2.2f);

        bar.add(BorderLayout.WEST, back);
        bar.add(BorderLayout.CENTER, titlePill);
        return bar;
    }

    // ---------- member summary card ----------

    static Container statBox(String label, String amount) {
        Rounded box = new Rounded(BoxLayout.y(), SAVI.WHITE, 30, 3f);
        setPadding(box, 1.8f, 1.8f, 2.2f, 2.2f);
        setMargins(box, 0, 0, 0.5f, 0.5f);

        box.add(SAVI.text(label, SAVI.regular(1.7f), SAVI.PASTEL_LAVENDER));
        box.add(SAVI.text(amount, SAVI.bold(3f), SAVI.WHITE));
        return box;
    }

    static Container memberCard(String name, String role, String handle, String group,
                                double totalPaid, double missed, double goal) {
        Rounded card = new Rounded(BoxLayout.y(), SAVI.BRIGHT_VIOLET, 255, 4.5f);
        setPadding(card, 2.5f, 2.5f, 2.5f, 2.5f);
        setMargins(card, 0.5f, 1f, 2, 2);

        // avatar
        Label person = new Label();
        person.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(person, FontImage.MATERIAL_PERSON_OUTLINE, 4f);
        Rounded avatar = new Rounded(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER), SAVI.WHITE, 30, 2.4f)
                .fixed(7.8f);
        avatar.add(BorderLayout.CENTER, person);

        // name + role pill, then @handle . group
        Label nameLabel = SAVI.text(name, SAVI.bold(3.2f), SAVI.WHITE);
        Rounded rolePill = pill(SAVI.text(role, SAVI.regular(1.6f), SAVI.WHITE), SAVI.WHITE, 30);
        setMargins(rolePill, 0, 0, 1f, 0);
        Container nameRow = new Container(new FlowLayout(Component.LEFT, Component.CENTER));
        nameRow.add(nameLabel).add(rolePill);

        Container textCol = new Container(BoxLayout.y());
        textCol.add(nameRow);
        textCol.add(SAVI.text(handle + " \u2022 " + group, SAVI.regular(1.7f), SAVI.PASTEL_LAVENDER));
        setMargins(textCol, 0, 0, 1.8f, 0);

        Container left = new Container(new BorderLayout());
        left.add(BorderLayout.WEST, avatar);
        left.add(BorderLayout.CENTER, BoxLayout.encloseYCenter(textCol));

        // status pill
        Rounded dot = new Rounded(new BorderLayout(), SAVI.WHITE, 255, 0).fixed(1.1f);
        setMargins(dot, 0, 0, 0, 0.8f);
        Container dotWrap = BoxLayout.encloseYCenter(dot);
        Rounded status = new Rounded(new FlowLayout(Component.CENTER, Component.CENTER), SAVI.WHITE, 30, 0);
        setPadding(status, 0.7f, 0.7f, 1.6f, 1.6f);

        Container topRow = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        topRow.add(BorderLayout.WEST, left);
        topRow.add(BorderLayout.EAST, FlowLayout.encloseRightMiddle(status));

        // stat boxes
        Container stats = new Container(new GridLayout(1, 2));
        stats.add(statBox("Total Paid", Home.pesoFormat(totalPaid)));
        stats.add(statBox("Missed / Unsettled", Home.pesoFormat(missed)));

        // progress to goal
        float pct = (float) (totalPaid / goal);
        Container progressRow = new Container(new BorderLayout());
        progressRow.add(BorderLayout.WEST, SAVI.text("Progress to Goal (" + Home.pesoFormat(goal) + ")",
                SAVI.regular(1.7f), SAVI.WHITE));
        progressRow.add(BorderLayout.EAST, SAVI.text((int) (pct * 100) + "%", SAVI.bold(1.7f), SAVI.WHITE));
        setMargins(progressRow, 2f, 0.8f, 0, 0);

        Component bar = progressBar(pct, SAVI.WHITE, 60, SAVI.WHITE, 1f);

        card.add(topRow);
        card.add(divider(SAVI.DARK_PURPLE, 2f, 0));
        card.add(stats);
        card.add(progressRow);
        card.add(bar);
        return card;
    }

    // ---------- search + filter chips ----------

    Container searchBar() {
        Label searchIcon = new Label();
        searchIcon.getAllStyles().setFgColor(SAVI.BLUE_GRAY);
        FontImage.setMaterialIcon(searchIcon, FontImage.MATERIAL_SEARCH, 3.2f);

        Label filterIcon = new Label();
        filterIcon.getAllStyles().setFgColor(SAVI.BLUE_GRAY);
        FontImage.setMaterialIcon(filterIcon, FontImage.MATERIAL_FILTER_LIST, 3.2f);

        TextField search = new TextField();
        // own placeholder label (the built in hint ignores the page's font)
        final Label hint = SAVI.text("Search date...", SAVI.regular(1.9f), SAVI.BLUE_GRAY);
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
                .stroke(SAVI.BORDER_COLOR, 255, 1);
        setPadding(bar, 0.8f, 0.8f, 2.2f, 2.2f);
        setMargins(bar, 1f, 0.8f, 2, 2);
        bar.add(BorderLayout.WEST, searchIcon);
        bar.add(BorderLayout.CENTER, field);
        bar.add(BorderLayout.EAST, filterIcon);
        return bar;
    }

    int countFor(String type) {
        int n = 0;
        for (PeopleBackend.Txn t : txns) {
            if (type == null || t.type.equals(type)) {
                n++;
            }
        }
        return n;
    }

    // chip drawn completely by hand, so no theme state (pressed / selected / focus) can add a box behind the text
    static class Chip extends Button {
        private boolean selected;

        Chip(String text) {
            super(text);
            setUIID("Label");
            setMargins(this, 0, 0, 0, 0.8f);
        }

        // stops the row from auto-scrolling when a chip is tapped (that was moving "All")
        @Override
        public void scrollRectToVisible(int x, int y, int w, int h, Component coordinateSpace) {
        }

        @Override
        public void scrollRectToVisible(com.codename1.ui.geom.Rectangle rect, Component coordinateSpace) {
        }

        void setSelectedChip(boolean s) {
            selected = s;
            repaint();
        }

        @Override
        protected Dimension calcPreferredSize() {
            Font f = SAVI.semiBold(1.7f);
            return new Dimension(f.stringWidth(getText()) + mm(3.2f), f.getHeight() + mm(2f));
        }

        @Override
        public void paint(Graphics g) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            g.setAntiAliased(true);

            g.setAlpha(255);
            g.setColor(selected ? SAVI.BRIGHT_VIOLET : SAVI.WHITE);
            g.fillRoundRect(x, y, w, h, h, h);

            if (!selected) {
                g.setColor(SAVI.BORDER_COLOR);
                g.drawRoundRect(x, y, w - 1, h - 1, h, h);
            }

            Font f = SAVI.semiBold(1.7f);
            g.setFont(f);
            g.setColor(selected ? SAVI.WHITE : SAVI.DARK_KNIGHT);
            g.drawString(getText(), x + (w - f.stringWidth(getText())) / 2, y + (h - f.getHeight()) / 2);
        }
    }

    Container chipsRow() {
        Container row = new Container(BoxLayout.x());
        row.setScrollableX(true);
        row.setScrollVisible(false); // hides the black scroll line
        setMargins(row, 0.5f, 1f, 2, 2);

        for (int i = 0; i < CHIP_NAMES.length; i++) {
            final int index = i;
            Chip chip = new Chip(CHIP_NAMES[i] + " (" + countFor(CHIP_TYPES[i]) + ")");
            chip.addActionListener(e -> {
                chips[selectedChip].setSelectedChip(false);
                selectedChip = index;
                chips[index].setSelectedChip(true);
                buildList();
            });
            chips[i] = chip;
            row.add(chip);
        }
        chips[selectedChip].setSelectedChip(true);
        return row;
    }

    static Container txRow(PeopleBackend.Txn t) {
        boolean missed = t.type.equals("MISSED");
        boolean deposit = t.type.equals("DEPOSIT");

        int accent = missed ? SAVI.RED : SAVI.GREEN;
        int soft = missed ? SAVI.PALE_RED : SAVI.PALE_GREEN;
        char icon = missed ? FontImage.MATERIAL_WARNING : FontImage.MATERIAL_ARROW_DOWNWARD;
        Container iconBox = squareIcon(icon, 6.5f, soft, accent);

        Container info = new Container(BoxLayout.y());
        info.add(SAVI.text(t.title, SAVI.bold(2.1f), SAVI.DARK_KNIGHT));
        info.add(SAVI.text(t.date, SAVI.regular(1.7f), SAVI.BLUE_GRAY));
        if (missed && !t.settled) {
            Rounded overdue = pill(SAVI.text("Overdue", SAVI.bold(1.5f), SAVI.RED), SAVI.WHITE, 255);
            Rounded settle = pillButton("Settle Now \u2192", SAVI.semiBold(1.5f), SAVI.BRIGHT_VIOLET,
                    SAVI.WHITE, SAVI.BORDER_COLOR, 1.8f, e -> {
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
        amountCol.add(text(sign + Home.pesoFormat(t.amount), SAVI.bold(2.3f), accent, Component.RIGHT));
        Label status = text(t.settled ? "Settled" : "Unpaid", missed ? SAVI.bold(1.5f) : SAVI.regular(1.5f),
                missed ? SAVI.RED : SAVI.BLUE_GRAY, Component.RIGHT);
        amountCol.add(status);

        Container row;
        if (missed) {
            Rounded r = new Rounded(new BorderLayout(), SAVI.PALE_RED, 255, 3.5f);
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
        row.add(BorderLayout.WEST, SAVI.text(month, SAVI.bold(1.7f), SAVI.BLUE_GRAY));

        String count = entries + (entries == 1 ? " Entry" : " Entries");
        if (highlight) {
            Rounded pill = pill(SAVI.text(count, SAVI.bold(1.6f), SAVI.BRIGHT_VIOLET), SAVI.PALE_PURPLE, 255);
            row.add(BorderLayout.EAST, FlowLayout.encloseRightMiddle(pill));
        } else {
            row.add(BorderLayout.EAST, SAVI.text(count, SAVI.regular(1.6f), SAVI.BLUE_GRAY));
        }
        return row;
    }

    boolean matches(PeopleBackend.Txn t) {
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
        for (int i = 0; i < txns.length; i++) {
            PeopleBackend.Txn t = txns[i];
            if (!matches(t)) {
                continue;
            }
            if (!t.month.equals(currentMonth)) {
                currentMonth = t.month;
                int entries = 0;
                for (PeopleBackend.Txn x : txns) {
                    if (x.month.equals(currentMonth) && matches(x)) {
                        entries++;
                    }
                }
                listContainer.add(monthHeader(currentMonth, entries, sections == 0));
                card = new Rounded(BoxLayout.y(), SAVI.WHITE, 255, 5f).stroke(SAVI.BORDER_COLOR, 255, 1);
                setPadding(card, 0.8f, 0.8f, 0, 0);
                setMargins(card, 0.5f, 1f, 2, 2);
                listContainer.add(card);
                sections++;
            } else {
                card.add(divider(SAVI.BORDER_COLOR, 0, 2.4f));
            }
            card.add(txRow(t));
        }

        if (sections == 0) {
            Label empty = text("No transactions found", SAVI.regular(2f), SAVI.BLUE_GRAY, Component.CENTER);
            setMargins(empty, 4, 0, 0, 0);
            listContainer.add(empty);
        }
        listContainer.revalidate();
    }

    // ---------- page ----------

    public void show() {
        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);
        paintStatusBar(base, SAVI.PALE_PURPLE);

        listContainer = new Container(BoxLayout.y());

        Container mainContents = new Container(BoxLayout.y());
        mainContents.setScrollableY(true);
        mainContents.setScrollVisible(false); // hides the dark bar on the right edge

        mainContents.add(memberCard(member.name, member.role, "@" + member.handle, member.group,
                member.totalPaid, member.missed, member.goal));
        mainContents.add(searchBar());
        mainContents.add(chipsRow());
        mainContents.add(listContainer);
        buildList();

        base.add(BorderLayout.NORTH, topBar());
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}