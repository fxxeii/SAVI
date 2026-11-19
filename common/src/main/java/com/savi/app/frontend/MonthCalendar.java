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

// MONTH CALENDAR - our own calendar component (replaces the default Codename One Calendar
// so it matches the look of the Home page).
// INHERITANCE: it "extends Container", so it can be added to a page like any other component.
// It shows one month as a 6 x 7 grid of day buttons, the arrows change the month,
// and tapping a day selects it and tells the page (through the listener).
public class MonthCalendar extends Container {

    private static final int palePurple = 0xF4EFFE; // days outside the shown month

    private Calendar shown = Calendar.getInstance(); // month being displayed
    private Date selected = new Date();              // selected day
    private ActionListener<ActionEvent> listener;    // the page's code that runs when a day is tapped

    private SimpleDateFormat keyFormat = new SimpleDateFormat("yyyyMMdd");   // used to compare two days
    private SimpleDateFormat dayFormat = new SimpleDateFormat("d");          // "5"
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy"); // "November 2026"

    public MonthCalendar() {
        super(BoxLayout.y());
        shown.set(Calendar.DAY_OF_MONTH, 1);
        HomePage.boxStyles(this, SAVI.DARK_DARK_PURPLE, 1.5f, 1.5f); // same card style as Home
        build();
    }

    // same method names as the old Calendar, so CalendarPage barely changes
    // ENCAPSULATION: the page reads the selected day through this getter only
    public Date getDate() {
        return selected;
    }

    public void addDayActionListener(ActionListener<ActionEvent> l) {
        listener = l;
    }

    // Draws the whole calendar. It is called again every time the month or the selected day changes.
    private void build() {
        removeAll();

        // HEADER: < Month Year >
        Button prev = arrowButton(FontImage.MATERIAL_CHEVRON_LEFT);
        Button next = arrowButton(FontImage.MATERIAL_CHEVRON_RIGHT);
        prev.addActionListener(e -> { shown.add(Calendar.MONTH, -1); build(); });   // go back one month
        next.addActionListener(e -> { shown.add(Calendar.MONTH, 1); build(); });    // go forward one month

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
        // c starts at the first cell of the grid and moves forward one day at a time
        Calendar c = Calendar.getInstance();
        c.setTime(shown.getTime());
        c.set(Calendar.DAY_OF_MONTH, 1);
        int shownMonth = c.get(Calendar.MONTH);
        c.add(Calendar.DAY_OF_MONTH, -(c.get(Calendar.DAY_OF_WEEK) - 1));   // go back to the Sunday

        Container grid = new Container(new GridLayout(6, 7));
        for (int i = 0; i < 42; i++) {   // 6 rows x 7 days = 42 cells
            boolean inMonth = c.get(Calendar.MONTH) == shownMonth;   // false for the faded days of other months
            grid.add(dayButton(c.getTime(), inMonth));
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        add(grid);

        revalidate();
    }

    // One day cell. The selected day gets a white rounded background, the others are transparent.
    private Button dayButton(Date d, boolean inMonth) {
        boolean isSelected = keyFormat.format(d).equals(keyFormat.format(selected));   // same day?

        Button b = new Button(dayFormat.format(d));
        b.setUIID("Label");

        int cellSize = Display.getInstance().convertToPixels(6.2f);
        b.setPreferredW(cellSize);
        b.setPreferredH(cellSize);

        Style s = b.getAllStyles();
        s.setAlignment(Component.CENTER);
        s.setFont(isSelected ? SAVI.bold(1.8f) : SAVI.regular(1.8f));
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(0.6f, 0.6f, 0.4f, 0.4f);
        s.setMarginUnit(Style.UNIT_TYPE_DIPS);
        s.setMargin(0.3f, 0.3f, 0.3f, 0.3f);

        // Apply RoundRectBorder to ALL cells so selected & unselected cells have identical height
        s.setBorder(RoundRectBorder.create().cornerRadius(2.5f));

        if (isSelected) {
            s.setFgColor(SAVI.DARK_DARK_PURPLE);
            s.setBgColor(SAVI.WHITE);
            s.setBgTransparency(255);
        } else {
            s.setFgColor(inMonth ? SAVI.WHITE : palePurple);
            s.setBgTransparency(0);
        }

        // When a day is tapped: remember it, jump to its month (for faded days),
        // redraw the calendar, then tell the page so it can update its cards.
        b.addActionListener(e -> {
            selected = d;
            shown.setTime(d);
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