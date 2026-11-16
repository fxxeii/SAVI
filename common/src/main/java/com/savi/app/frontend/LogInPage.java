package com.savi.app.frontend;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;

import java.io.IOException;

public class LogInPage {

    public void show(){

        Form LogInForm = new Form(new BorderLayout());
        LogInForm.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        LogInForm.getAllStyles().setBgTransparency(255);

        Container header = new Container(BoxLayout.yCenter());
        header.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        header.getAllStyles().setBgTransparency(255);
        header.getAllStyles().setPadding(225, 50, 0, 0);

        Image img = null;
        try {
            Image logoimg = Image.createImage("/Logo_whitebg.png");
            int deviceWidth = Display.getInstance().getDisplayWidth();
            int targetWidth = (int)(deviceWidth * 0.28f);
            img = logoimg.scaledWidth(targetWidth);
        } catch (IOException err) {
            Log.e(err);
        }

        Label logo = new Label(img);
        Label welcome = SAVI.text("Welcome Back!", SAVI.bold(5f), SAVI.WHITE);
        Label sub1 = SAVI.text("Continue your savings with ", SAVI.regular(2.3f), SAVI.WHITE);
        Label sub2 = SAVI.text("Savi", SAVI.bold(2.3f), SAVI.WHITE);

        welcome.getAllStyles().setPadding(0,1f,0,0);
        welcome.setAlignment(Component.CENTER);
        sub1.getAllStyles().setPadding(0,0,0,0);
        sub2.getAllStyles().setPadding(0,0,0,0);

        Container logoc = new Container(new FlowLayout(Component.CENTER, Component.TOP));
        logoc.add(logo);

        Container subs = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        subs.add(sub1);
        subs.add(sub2);

        header.add(logoc).add(welcome).add(subs);

        Container form = new Container(BoxLayout.y());
        form.getAllStyles().setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        form.getAllStyles().setBackgroundGradientStartColor(SAVI.DARK_PURPLE);
        form.getAllStyles().setBackgroundGradientEndColor(0x5C21B7);
        form.getAllStyles().setBgTransparency(255);
        form.getAllStyles().setPadding(0, 0, 10, 10);

        TextField usernamef = new TextField("","Enter username");
        TextField passf = new TextField("", "Enter password");

        usernamef.getAllStyles().setFont(SAVI.regular(2.5f));
        usernamef.getAllStyles().setFgColor(SAVI.BLACK);
        usernamef.getAllStyles().setPadding(2, 1.5f, 3, 4);
        usernamef.getAllStyles().setMargin(2, 2, 9, 9);

        usernamef.getHintLabel().getAllStyles().setFont(SAVI.regular(1.9f));
        usernamef.getHintLabel().getAllStyles().setFgColor(SAVI.GRANITE_GRAY);
        usernamef.getHintLabel().getAllStyles().setPadding(2,1.5f,3,0);

        usernamef.setSingleLineTextArea(true);

        Style normal = usernamef.getUnselectedStyle();
        Style focused = usernamef.getSelectedStyle();
        Style pressed = usernamef.getPressedStyle();

        normal.setBgColor(SAVI.WHITE);
        normal.setBgTransparency(255);
        normal.setBgImage(null);
        normal.setBorder(Border.createEmpty());
        normal.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );

        focused.setBgColor(SAVI.WHITE);
        focused.setBgTransparency(255);
        focused.setBgImage(null);
        focused.setBorder(Border.createEmpty());
        focused.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );

        pressed.setBgColor(SAVI.WHITE);
        pressed.setBgTransparency(255);
        pressed.setBgImage(null);
        pressed.setBorder(Border.createEmpty());
        pressed.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );

        passf.getAllStyles().setFont(SAVI.regular(2.5f));
        passf.getAllStyles().setFgColor(SAVI.BLACK);
        passf.getAllStyles().setPadding(2, 1.5f, 3, 3);
        passf.getAllStyles().setMargin(2, 2, 9, 1);

        passf.getHintLabel().getAllStyles().setFont(SAVI.regular(1.9f));
        passf.getHintLabel().getAllStyles().setFgColor(SAVI.GRANITE_GRAY);
        passf.getHintLabel().getAllStyles().setPadding(2,1.5f,3,0);

        passf.setSingleLineTextArea(true);

        Style norm = passf.getUnselectedStyle();
        Style foc = passf.getSelectedStyle();
        Style pre = passf.getPressedStyle();

        norm.setBgColor(SAVI.WHITE);
        norm.setBgTransparency(255);
        norm.setBgImage(null);
        norm.setBorder(Border.createEmpty());
        norm.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );

        foc.setBgColor(SAVI.WHITE);
        foc.setBgTransparency(255);
        foc.setBgImage(null);
        foc.setBorder(Border.createEmpty());
        foc.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );

        pre.setBgColor(SAVI.WHITE);
        pre.setBgTransparency(255);
        pre.setBgImage(null);
        pre.setBorder(Border.createEmpty());
        pre.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );
        passf.setConstraint(TextField.PASSWORD);

        Button showPass = new Button("Show");
        Button forgotp = new Button("Forgot Password");

        Container passContainer = new Container(new BorderLayout());
        passContainer.add(BorderLayout.CENTER, passf);
        passContainer.add(BorderLayout.EAST, showPass);
        passContainer.add(BorderLayout.SOUTH, forgotp);

        showPass.addActionListener(e -> {
            if (passf.getConstraint() == TextField.PASSWORD) {
                passf.setConstraint(TextField.ANY);
                showPass.setText("Hide");
            } else {
                passf.setConstraint(TextField.PASSWORD);
                showPass.setText("Show");
            }
            passf.repaint();
            passf.getParent().revalidate();
        });

        Button[] showButtons = {showPass};

        for (Button btn : showButtons) {
            Style[] styles = {
                    btn.getUnselectedStyle(),
                    btn.getSelectedStyle(),
                    btn.getPressedStyle()
            };

            for (Style s : styles) {
                s.setBgTransparency(0);
                s.setBgImage(null);
                s.setBorder(Border.createEmpty());
                s.setFgColor(SAVI.WHITE);
                s.setFont(SAVI.bold(1.9f));
                s.setPadding(0, 0, 0, 3);
                s.setMargin(0, 0,0, 0);
            }
        }

        forgotp.addActionListener(e -> {ForgotPassPage fppage = new ForgotPassPage(); fppage.show();});

        Button[] forgotButton = {forgotp};

        for (Button btn : forgotButton) {
            Style[] styles = {
                    btn.getUnselectedStyle(),
                    btn.getSelectedStyle(),
                    btn.getPressedStyle()
            };

            for (Style s : styles) {
                s.setBgTransparency(0);
                s.setBgImage(null);
                s.setBorder(Border.createEmpty());
                s.setFgColor(SAVI.WHITE);
                s.setFont(SAVI.bold(1.7f));
                s.setPadding(0, 0, 0, 25);
                s.setMargin(0, 0,0, 0);
            }
        }

        form.add(usernamef);
        form.add(passContainer);

        Button loginbtn = new Button("Log In");

        Button[] sbtn = {loginbtn};

        for (Button btn : sbtn) {
            btn.getAllStyles().setMargin(8,0,7,7);
            btn.getAllStyles().setPadding(2f, 2f, 0f, 0f);
            btn.getAllStyles().setFont(SAVI.bold(2.5f));

            Style n = loginbtn.getUnselectedStyle();
            Style h = loginbtn.getSelectedStyle();
            Style p = loginbtn.getPressedStyle();

            n.setBgImage(null);
            n.setBorder(Border.createEmpty());
            n.setBgTransparency(255);
            n.setBgColor(SAVI.WHITE);
            n.setFgColor(SAVI.DARK_DARK_PURPLE);

            h.setBgImage(null);
            h.setBorder(Border.createEmpty());
            h.setFgColor(SAVI.WHITE);
            h.setBgTransparency(255);
            h.setBgColor(SAVI.DARK_PURPLE);

            p.setBgImage(null);
            p.setBorder(Border.createEmpty());
            p.setBgTransparency(255);
            p.setFgColor(SAVI.WHITE);
            p.setBgColor(SAVI.DARK_PURPLE);
        }

        loginbtn.getAllStyles().setBorder(Border.createRoundBorder(140, SAVI.BORDER_COLOR));
        loginbtn.addActionListener(evt ->{
            String name = usernamef.getText().trim();
            String pass = passf.getText().trim();

            String result = SAVI.users.login(name, pass);

            if (result.equals("success")) {
                Dialog.show("Success", "Login successful!", "OK", null);
                new HomePage().show();
            } else {
                Dialog.show("Error", result, "OK", null);
            }
        });
        form.add(loginbtn);

        Container footer = new Container(BoxLayout.y());
        footer.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        footer.getAllStyles().setBgTransparency(255);
        footer.getAllStyles().setMargin(150,0,0,0);
        footer.getAllStyles().setPadding(40, 20, 0, 0);

        Label last = SAVI.text("No account yet?", SAVI.regular(2.3f), SAVI.SLATE_GRAY);
        last.getAllStyles().setPadding(6,0,0,0);
        last.getAllStyles().setMargin(0,4,0,0);

        Button signupbtn = new Button("Sign Up");

        Style btn = signupbtn.getAllStyles();
        btn.setBgTransparency(0);
        btn.setBgImage(null);
        btn.setBorder(Border.createEmpty());
        btn.setFgColor(SAVI.DARK_PURPLE);
        btn.setFont(SAVI.bold(2.3f));
        btn.setPadding(6, 0, 0, 0);
        btn.setMargin(0, 4,0 , 0);

        Container f1 = new Container(new FlowLayout(Component.CENTER, Component.BOTTOM));
        f1.add(last);
        f1.add(signupbtn);

        footer.add(f1);

        signupbtn.addActionListener(e -> { SignUpPage signin = new SignUpPage(); signin.show();});

        LogInForm.add(BorderLayout.CENTER, form).add(BorderLayout.NORTH, header).add(BorderLayout.SOUTH, footer);
        LogInForm.show();
    }
}
