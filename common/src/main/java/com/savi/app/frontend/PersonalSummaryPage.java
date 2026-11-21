package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;
import com.savi.app.backend.PersonalSummary;

/**
 * Personal Savings GroupSummary: the purple card (name picker, Total Pool, Target Amount)
 * and the monthly chart. It shares its header, card and chart with SummaryPage.
 */
public class PersonalSummaryPage {

    // BACKEND: every number and text on this page comes from here
    private PersonalSummary personal;

    // opens on the first personal saving
    public void show() {
        show(null);
    }

    // opens on the personal saving with this name (used by the picker)
    public void show(String name) {
        // only the personal savings of the logged-in user
        personal = new PersonalSummary(SAVI.personalList.getForOwner(SAVI.user.getCurrentName()));
        if (name != null) {
            personal.select(name);
        }

        final SummaryPage ui = new SummaryPage();   // reused: header, purple card, chart card

        Form base = new Form("", new BorderLayout());
        base.getContentPane().getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        base.getContentPane().getAllStyles().setBgTransparency(255);

        Container t = base.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        // right side of the purple card: "Target Amount" + the amount
        Label targetLabel = ui.txt("Target Amount", SAVI.regular(2f), SAVI.WHITE);
        targetLabel.getAllStyles().setAlignment(Component.RIGHT);
        Container right = new Container(BoxLayout.y());
        right.add(targetLabel);
        right.add(FlowLayout.encloseRight(ui.txt(personal.getTargetText(), SAVI.bold(2.6f), SAVI.WHITE)));

        String title = personal.getTitle();
        Container card = ui.poolCardFor(title, ui.pickerItems(personal.getNames(), title),
                chosen -> new PersonalSummaryPage().show(chosen),    // reload on the chosen saving
                personal.getTotalPoolText(), right);

        // the chart card sits in a holder so W / M / Y can swap it without rebuilding the page
        final Container chartHolder = new Container(new BorderLayout());
        final Runnable[] refreshChart = new Runnable[1];
        refreshChart[0] = () -> {
            chartHolder.removeAll();
            chartHolder.add(BorderLayout.CENTER, ui.monthlyChartCard(personal, refreshChart[0]));
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
        mainContents.add(card);
        mainContents.add(chartHolder);

        base.add(BorderLayout.NORTH, ui.summaryHeader(true));
        base.add(BorderLayout.CENTER, mainContents);
        base.show();
    }
}