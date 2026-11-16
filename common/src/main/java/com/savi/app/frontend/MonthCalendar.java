package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MonthCalendar extends Container {

    private static final int palePurple = 0xF4EFFE; // days outside the shown month

    private Calendar shown = Calendar.getInstance(); // month being displayed
    private Date selected = new Date();              // selected day
    private ActionListener<ActionEvent> listener;

    private SimpleDateFormat keyFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("d");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");

    public MonthCalendar() {
        super(BoxLayout.y());
        shown.set(Calendar.DAY_OF_MONTH, 1);
        HomePage.boxStyles(this, SAVI.DARK_DARK_PURPLE, 1.5f, 1.5f); // same card style as Home
        build();
    }

    // same method names as the old Calendar, so CalendarPage barely changes
    public Date getDate() {
        return selected;
    }

    public void addDayActionListener(ActionListener<ActionEvent> l) {
        listener = l;
    }

    private void build() {
        removeAll();

        // HEADER: < Month Year >
        Button prev = arrowButton(FontImage.MATERIAL_CHEVRON_LEFT);
        Button next = arrowButton(FontImage.MATERIAL_CHEVRON_RIGHT);
        prev.addActionListener(e -> { shown.add(Calendar.MONTH, -1); build(); });
        next.addActionListener(e -> { shown.add(Calendar.MONTH, 1); build(); });

        Label monthLabel = SAVI.text(monthFormat.format(shown.getTime()), SAVI.bold(2.5f), SAVI.WHITE);
        monthLabel.getAllStyles().setAlignment(Component.CENTER);

        Container header = new Container(new BorderLayout());
        header.add(BorderLayout.WEST, prev);
        header.add(BorderLayout.CENTER, monthLabel);
        header.add(BorderLayout.EAST, next);
        add(header);

        // DAY LETTERS
        String[] letters = {"S", "M", "T", "W", "T", "F", "S"};
        Container lettersRow = new Container(new GridLayout(1, 7));
        for (String l : letters) {
            Label lbl = SAVI.text(l, SAVI.semiBold(1.5f), SAVI.WHITE);
            lbl.getAllStyles().setAlignment(Component.CENTER);
            lettersRow.add(lbl);
        }
        lettersRow.getAllStyles().setMarginTop(2);
        add(lettersRow);

        // DAY GRID (6 rows x 7), starting from the Sunday before the 1st
        Calendar c = Calendar.getInstance();
        c.setTime(shown.getTime());
        c.set(Calendar.DAY_OF_MONTH, 1);
        int shownMonth = c.get(Calendar.MONTH);
        c.add(Calendar.DAY_OF_MONTH, -(c.get(Calendar.DAY_OF_WEEK) - 1));

        Container grid = new Container(new GridLayout(6, 7));
        for (int i = 0; i < 42; i++) {
            boolean inMonth = c.get(Calendar.MONTH) == shownMonth;
            grid.add(dayButton(c.getTime(), inMonth));
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        add(grid);

        revalidate();
    }

    private Button dayButton(Date d, boolean inMonth) {
        boolean isSelected = keyFormat.format(d).equals(keyFormat.format(selected));

        Button b = new Button(dayFormat.format(d));
        b.setUIID("Label"); // removes the default button look

        Style s = b.getAllStyles();
        s.setAlignment(Component.CENTER);
        s.setFont(isSelected ? SAVI.bold(1.8f) : SAVI.regular(1.8f));
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(1, 1, 0.5f, 0.5f);
        s.setMarginUnit(Style.UNIT_TYPE_DIPS);
        s.setMargin(0.3f, 0.3f, 0.3f, 0.3f);

        if (isSelected) {
            s.setFgColor(SAVI.DARK_DARK_PURPLE);
            s.setBgColor(SAVI.WHITE);
            s.setBgTransparency(255);
            s.setBorder(RoundRectBorder.create().cornerRadius(3f));
        } else {
            s.setFgColor(inMonth ? SAVI.WHITE : palePurple);
        }

        b.addActionListener(e -> {
            selected = d;
            shown.setTime(d);   // tapping a palePurple day jumps to its month
            build();
            if (listener != null) {
                listener.actionPerformed(e);
            }
        });
        return b;
    }

    private Button arrowButton(char icon) {
        Button b = new Button();
        b.setUIID("Label");
        b.getAllStyles().setFgColor(SAVI.WHITE);
        FontImage.setMaterialIcon(b, icon, 4f);
        return b;
    }
}