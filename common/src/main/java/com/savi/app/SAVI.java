package com.savi.app;

import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.*;
import com.savi.app.frontend.*;
import com.savi.app.backend.*;
import java.util.ArrayList;
import com.savi.app.backend.User;

public class SAVI extends Lifecycle {

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

    public static User user = new User();

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

    public static User users = user;
    public static ArrayList<GroupSavings> groups = new ArrayList<>();

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
        //new HomePage().show();
        //new CalendarPage().show();
        //new SignUpPage().show();
        //new PendingPage().show();
        new LogInPage().show();
        //new MainPage().show();
        //new TransferPage().show();
        //new SummaryPage().show();
        //new TransferConfirmationPage().show(500f, "Anna", "Family", "Ben", "Friends");
        //new RecentContributions().show();
    }
}