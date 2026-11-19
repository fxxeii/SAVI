package com.savi.app;

import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.savi.app.frontend.*;
import com.savi.app.backend.*;
import com.savi.app.backend.User;

public class SAVI extends Lifecycle {

    public static User user = new User();
    public static ReminderList reminders = new ReminderList();
    public static GroupList groupList = new GroupList();
    public static PersonalList personalList = new PersonalList();

    public static final int DARK_DARK_PURPLE = 0x30205E;
    public static final int DARK_PURPLE = 0x7C3AED;
    public static final int PALE_PURPLE = 0xF4EFFE;
    public static final int NAVY = 0x29364D;
    public static final int BLUE_GRAY = 0x8491A7;
    public static final int GREEN = 0x00A878;
    public static final int PALE_GREEN = 0xE6FAF2;
    public static final int BORDER_COLOR = 0xEDF0F5;
    public static final int WHITE = 0xFFFFFF;
    public static final int DEEP_SLATE = 0x334155;
    public static final int SLATE_GRAY = 0x64748B;
    public static final int DARK_KNIGHT = 0x0F172A;
    public static final int DARK_BLUE = 0x1E293B;
    public static final int BRIGHT_VIOLET = 0x6D28D9;
    public static final int POWDER_WHITE = 0xF1F5F9;
    public static final int BLACK = 0x000000;
    public static final int GRANITE_GRAY = 0x616161;
    public static final int PASTEL_LAVENDER = 0xD8B4FE;
    public static final int BLUISH_PURPLE = 0x4F46E5;
    public static final int PASTEL_YELLOW = 0xF5E2A8;
    public static final int PALE_YELLOW = 0xFFFCF2;
    public static final int YELLOW_TAG = 0xFEF3C7;
    public static final int AMBER = 0xB45309;
    public static final int RED = 0xE11D48;
    public static final int PALE_RED = 0xFFF1F2;
    public static final int PINKISH = 0xC084FC;

    public static Font regularFont;
    public static Font boldFont;
    public static Font semiBoldFont;
    public static Font italicFont;

    public static final int NAV_HOME = 0;
    public static final int NAV_CALENDAR = 1;
    public static final int NAV_PEOPLE = 2;
    public static final int NAV_USER = 3;

    private static final float NAV_ADD_BTN = 12f;  // width of addButton.png in mm
    private static final float NAV_NOTCH = 2f;   // space above the bar where the + button sticks out
    private static final float NAV_BAR_H = 14.5f;  // height of the bar itself
    private static final float NAV_SHADOW_MM = 1.5f; // how far the shadow reaches above the bar
    private static final int NAV_SHADOW_ALPHA = 90;  // shadow strength at the bar: 0 (none) to 255

    public static Font regular(float mm){
        if(regularFont==null){
            regularFont = Font.createTrueTypeFont("GoogleSans-Regular", "GoogleSans-Regular.ttf");
        }
        return regularFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_PLAIN);
    }

    public static Font bold(float mm){
        if(boldFont==null){
            boldFont = Font.createTrueTypeFont("GoogleSans-Bold", "GoogleSans-Bold.ttf");
        }
        return boldFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_BOLD);
    }

    public static Font semiBold(float mm){
        if(semiBoldFont==null){
            semiBoldFont = Font.createTrueTypeFont("Google Sans SemiBold", "GoogleSans-SemiBold.ttf");
        }
        return semiBoldFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_BOLD);
    }

    public static Font italic(float mm){
        if(italicFont==null){
            italicFont = Font.createTrueTypeFont("GoogleSans-italic", "GoogleSans-italic.ttf");
        }
        return italicFont.derive(Display.getInstance().convertToPixels(mm), Font.STYLE_BOLD);
    }

    public static Label text(String str, Font font, int color) {
        Label lbl = new Label(str);
        lbl.getAllStyles().setFont(font);
        lbl.getAllStyles().setFgColor(color);
        return lbl;
    }

    /** Bottom bar with the + button; "active" is the tab drawn in purple (NAV_HOME, ...). */
    public static Container bottomNav(int active) {
        return bottomNav(active, QuickAddMenu::show);
    }

    /** Same, and onAdd runs when the + button is tapped (null = does nothing yet). */
    public static Container bottomNav(int active, Runnable onAdd) {
        // the nav paints its own background: the bar and its thin top line
        Container nav = new Container(new BorderLayout()) {
            @Override
            public void paintBackground(Graphics g) {
                int x = getX(), y = getY(), w = getWidth(), h = getHeight();
                int barTop = y + Display.getInstance().convertToPixels(NAV_NOTCH);

                g.setAntiAliased(true);

                // soft shadow above the bar: darkest right at the bar, fading out going up
                int shadowH = Display.getInstance().convertToPixels(NAV_SHADOW_MM);
                g.setColor(0x64748B);
                for (int i = 0; i < shadowH; i++) {
                    g.setAlpha(NAV_SHADOW_ALPHA - (NAV_SHADOW_ALPHA * i / shadowH));
                    g.drawLine(x, barTop - 1 - i, x + w, barTop - 1 - i);
                }

                // the bar and its thin top line
                g.setAlpha(255);
                g.setColor(0xF8FAFC);
                g.fillRect(x, barTop, w, h - (barTop - y));
                g.setColor(0xE2E8F0);
                g.drawLine(x, barTop, x + w, barTop);
            }
        };
        nav.setPreferredH(Display.getInstance().convertToPixels(NAV_NOTCH + NAV_BAR_H));

        Container items = new Container(new GridLayout(1, 5));
        items.add(navItem(FontImage.MATERIAL_HOME, "Home", active == NAV_HOME,
                () -> new HomePage().show()));
        items.add(navItem(FontImage.MATERIAL_CALENDAR_TODAY, "Calendar", active == NAV_CALENDAR,
                () -> new CalendarPage().show()));
        items.add(addButton(onAdd));
        items.add(navItem(FontImage.MATERIAL_PEOPLE_OUTLINE, "People", active == NAV_PEOPLE,
                () -> new PeoplePage().show()));
        items.add(navItem(FontImage.MATERIAL_PERSON_OUTLINE, "User", active == NAV_USER,
                () -> new ProfilePage().show()));

        nav.add(BorderLayout.CENTER, items);
        return nav;
    }

    private static Container navItem(char icon, String label, boolean active, Runnable action) {
        Button b = new Button(label);
        b.setUIID("Label");
        Style s = b.getAllStyles();
        s.setFgColor(active ? DARK_PURPLE : BLACK);
        s.setFont(bold(1.2f));
        s.setAlignment(Component.CENTER);
        s.setBgTransparency(0);
        s.setMargin(0, 0, 0, 0);
        s.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        s.setPadding(0.5f, 0.5f, 0, 0);
        FontImage.setMaterialIcon(b, icon, 5f);   // the icon takes the fg color set above
        b.setTextPosition(Component.BOTTOM);
        if (!active && action != null) {          // tapping the tab you are already on does nothing
            b.addActionListener(e -> action.run());
        }

        Container cell = BorderLayout.center(b);
        // push the item down by the notch height so it is centered in the bar, not in the notch
        cell.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        cell.getAllStyles().setMargin(NAV_NOTCH, 0, 0, 0);
        return cell;
    }

    // the purple + button: resources/addButton.png, sticking out above the bar
    private static Container addButton(Runnable onAdd) {
        Button plus = new Button();
        plus.setUIID("Label");
        try {
            Image img = Image.createImage("/addButton.png");
            plus.setIcon(img.scaledWidth(Display.getInstance().convertToPixels(NAV_ADD_BTN)));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            plus.getAllStyles().setFgColor(DARK_PURPLE);
            FontImage.setMaterialIcon(plus, FontImage.MATERIAL_ADD, 6f);   // fallback if the png is missing
        }
        Style s = plus.getAllStyles();
        s.setBgTransparency(0);
        s.setMargin(0, 0, 0, 0);
        s.setPadding(0, 0, 0, 0);
        if (onAdd != null) {
            plus.addActionListener(e -> onAdd.run());
        }
        return FlowLayout.encloseCenter(plus);   // centered across, at the top of the nav
    }

    public static Container buildHeader(String titleText, Runnable onBack) {
        Button back = new Button();
        Style bs = back.getAllStyles();
        bs.setFgColor(SAVI.DARK_PURPLE);
        bs.setBgTransparency(0);
        bs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(0.6f, 0.6f, 0.6f, 0.6f);
        bs.setMargin(0, 0, 0, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 3.5f);
        back.addActionListener(e -> onBack.run());

        Label spacer = new Label();
        spacer.getAllStyles().setMargin(0, 0, 0, 0);
        spacer.setPreferredSize(back.getPreferredSize());

        Label title = SAVI.text(titleText, SAVI.bold(2.5f), SAVI.WHITE);
        title.getAllStyles().setMargin(0, 0, 0, 0);
        title.getAllStyles().setPadding(0, 0, 0, 0);
        title.getAllStyles().setAlignment(Component.CENTER);

        Container pill = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
        Style ps = pill.getAllStyles();
        ps.setBgColor(SAVI.DARK_PURPLE);
        ps.setBgTransparency(255);
        ps.setBorder(RoundRectBorder.create().cornerRadius(4f).shadowOpacity(50).shadowBlur(10).shadowSpread(1f).shadowX(0.5f).shadowY(0.3f).shadowColor(SAVI.SLATE_GRAY));
        ps.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        ps.setPadding(1.5f, 1.5f, 1.5f, 1.5f);
        ps.setMarginUnit(Style.UNIT_TYPE_DIPS);
        ps.setMargin(1f, 0.4f, 2f, 2f);

        pill.add(BorderLayout.WEST, back);
        pill.add(BorderLayout.CENTER, title);
        pill.add(BorderLayout.EAST, spacer);

        Container wrap = new Container(new BorderLayout());
        wrap.add(BorderLayout.CENTER, pill);
        return wrap;
    }

    @Override
    public void runApp() {
        user.load();
        reminders.load();
        groupList.load();
        personalList.load();

        //new MainPage().show();
        new ProfilePage().show();
    }
}