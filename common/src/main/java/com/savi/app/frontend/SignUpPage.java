package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.io.Log;
import com.codename1.ui.plaf.*;
import com.codename1.ui.plaf.RoundRectBorder;
import com.savi.app.SAVI;

import java.io.IOException;


public class SignUpPage {

    public void show() {

        Form signUpForm = new Form(new BorderLayout());
        signUpForm.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        signUpForm.getAllStyles().setBgTransparency(255);

        Container header = new Container(BoxLayout.yCenter());

        Style cont = header.getAllStyles();

        cont.setBgColor(SAVI.DARK_PURPLE);
        cont.setBgTransparency(255);
        cont.setPadding(20, 50, 0, 0);
        cont.setMarginUnit(Style.UNIT_TYPE_DIPS);
        cont.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
        cont.setBackgroundGradientStartColor(SAVI.PINKISH);
        cont.setBackgroundGradientEndColor(SAVI.DARK_PURPLE);

        Image img = null;
        try {
            Image logoimg = Image.createImage("/Logo_whitebg.png");
            int deviceWidth = Display.getInstance().getDisplayWidth();
            int targetWidth = (int)(deviceWidth * 0.29f);
            img = logoimg.scaledWidth(targetWidth);
        } catch (IOException err) {
            Log.e(err);
        }

        Label logo = new Label(img);
        Label welcome = SAVI.text("Welcome to Savi!", SAVI.bold(4f), SAVI.WHITE);
        Label sub = SAVI.text("Start tracking your savings with us today.", SAVI.regular(2.5f), SAVI.WHITE);

        logo.setAlignment(Component.CENTER);
        Container logoc = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        logoc.add(logo);

        welcome.setAlignment(Component.CENTER);
        sub.setAlignment(Component.CENTER);

        header.add(logoc).add(welcome).add(sub);

        Container form = new Container(BoxLayout.y());
        form.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        form.getAllStyles().setBgTransparency(255);
        form.getAllStyles().setPadding(70, 5, 10, 10);

        Label usn = SAVI.text("Username", SAVI.regular(2f), SAVI.DEEP_SLATE);
        Label eml = SAVI.text("Email", SAVI.regular(2f), SAVI.DEEP_SLATE);
        Label ps = SAVI.text("Password", SAVI.regular(2f), SAVI.DEEP_SLATE);
        Label cps = SAVI.text("Confirm Password", SAVI.regular(2f), SAVI.DEEP_SLATE);

        TextField usernamef = new TextField("","username");
        TextField emailf = new TextField("", "example@mail.com");
        TextField passf = new TextField("", "password");
        TextField cpassf = new TextField("", "confirm password");

        Label[] labels = {usn, eml, ps, cps};

        for (Label lbl : labels) {
            lbl.getAllStyles().setMargin(0, 0, 0, 0);
            lbl.getAllStyles().setPadding(0, 0, 9, 0);
        }

        TextField[] fields = {usernamef, emailf, passf, cpassf};

        for (TextField tf : fields) {
            tf.getAllStyles().setFont(SAVI.regular(2.5f));
            tf.getAllStyles().setFgColor(SAVI.BLACK);
            tf.getAllStyles().setPadding(2, 1.5f, 3, 4);
            tf.getAllStyles().setMargin(1, 2, 9, 9);

            tf.getHintLabel().getAllStyles().setFont(SAVI.italic(1.9f));
            tf.getHintLabel().getAllStyles().setFgColor(SAVI.GRANITE_GRAY);
            tf.getHintLabel().getAllStyles().setPadding(2, 1.5f, 3, 0);

            tf.setSingleLineTextArea(true);
        }

        for (TextField tf : fields) {
            Style[] styles = {
                    tf.getUnselectedStyle(),
                    tf.getSelectedStyle(),
                    tf.getPressedStyle()
            };

            for (Style s : styles) {
                s.setBgColor(SAVI.WHITE);
                s.setBgTransparency(255);
                s.setBgImage(null);
                s.setBorder(Border.createEmpty());
                s.setBorder(RoundRectBorder.create()
                        .cornerRadius(3f)
                        .shadowOpacity(25)
                        .shadowBlur(1)
                        .shadowSpread(0.3f)
                        .shadowX(0.5f)
                        .shadowY(1.5f)
                        .shadowColor(SAVI.GRANITE_GRAY)
                );
            }
        }

        passf.getAllStyles().setPadding(2, 1.5f, 3, 3);
        passf.getAllStyles().setMargin(1, 2, 9, 1);
        cpassf.getAllStyles().setPadding(2, 1.5f, 3, 3);
        cpassf.getAllStyles().setMargin(1, 2, 9, 1);

        passf.setConstraint(TextField.PASSWORD);
        cpassf.setConstraint(TextField.PASSWORD);

        Button showPass = new Button("Show");
        Button showCpass = new Button("Show");

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

        showCpass.addActionListener(e -> {
            if (cpassf.getConstraint() == TextField.PASSWORD) {
                cpassf.setConstraint(TextField.ANY);
                showCpass.setText("Hide");
            } else {
                cpassf.setConstraint(TextField.PASSWORD);
                showCpass.setText("Show");
            }
            cpassf.repaint();
            cpassf.getParent().revalidate();
        });

        Button[] showButtons = {showPass, showCpass};

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
                s.setFgColor(SAVI.DARK_PURPLE);
                s.setFont(SAVI.regular(2f));
                s.setPadding(0, 0, 0, 3);
                s.setMargin(0, 0,0, 0);
            }
        }

        Container usnContainer = new Container(new BorderLayout());
        usnContainer.add(BorderLayout.NORTH, usn);
        usnContainer.add(BorderLayout.CENTER, usernamef);

        Container emlContainer = new Container(new BorderLayout());
        emlContainer.add(BorderLayout.NORTH, eml);
        emlContainer.add(BorderLayout.CENTER, emailf);

        Container passContainer = new Container(new BorderLayout());
        passContainer.add(BorderLayout.NORTH, ps);
        passContainer.add(BorderLayout.CENTER, passf);
        passContainer.add(BorderLayout.EAST, showPass);

        Container cpassContainer = new Container(new BorderLayout());
        cpassContainer.add(BorderLayout.NORTH, cps);
        cpassContainer.add(BorderLayout.CENTER, cpassf);
        cpassContainer.add(BorderLayout.EAST, showCpass);

        form.add(usnContainer).add(emlContainer);
        form.add(passContainer).add(cpassContainer);

        Button signupbtn = new Button("Sign Up");

        Button[] sbtn = {signupbtn};

        for (Button btn : sbtn) {
            btn.getAllStyles().setMargin(8,0,5,5);
            btn.getAllStyles().setPadding(2f, 2f, 0f, 0f);
            btn.getAllStyles().setFont(SAVI.bold(2.5f));

            Style n = signupbtn.getUnselectedStyle();
            Style h = signupbtn.getSelectedStyle();
            Style p = signupbtn.getPressedStyle();

            n.setBgImage(null);
            n.setBorder(Border.createEmpty());
            n.setBgTransparency(255);
            n.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
            n.setBackgroundGradientStartColor(SAVI.DARK_PURPLE);
            n.setBackgroundGradientEndColor(SAVI.BLUISH_PURPLE);
            n.setFgColor(SAVI.WHITE);

            h.setBgImage(null);
            h.setBorder(Border.createEmpty());
            h.setFgColor(SAVI.WHITE);
            h.setBgTransparency(255);
            h.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
            h.setBackgroundGradientStartColor(SAVI.BLUISH_PURPLE);
            h.setBackgroundGradientEndColor(SAVI.DARK_PURPLE);

            p.setBgImage(null);
            p.setBorder(Border.createEmpty());
            p.setBgTransparency(255);
            p.setFgColor(SAVI.WHITE);
            p.setBgColor(SAVI.DARK_PURPLE);
        }

        signupbtn.getAllStyles().setBorder(Border.createRoundBorder(140, SAVI.BORDER_COLOR));
        signupbtn.addActionListener(evt ->{
            String name = usernamef.getText().trim();
            String email = emailf.getText().trim();
            String pass = passf.getText().trim();
            String cpass = cpassf.getText().trim();

            String result = SAVI.users.signup(name, email, pass, cpass);

            if (result.equals("success")) {
                Dialog.show("Success", "Account created!", "OK", null);
                new LogInPage().show();
            } else {
                Dialog.show("Error", result, "OK", null);
            }
        });
        form.add(signupbtn);

        Container footer = new Container(BoxLayout.y());
        footer.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        footer.getAllStyles().setBgTransparency(255);
        footer.getAllStyles().setMargin(50,0,0,0);
        footer.getAllStyles().setPadding(40, 20, 10, 10);

        Label last = SAVI.text("Already have an account? ", SAVI.regular(2.3f), SAVI.SLATE_GRAY);
        last.getAllStyles().setPadding(6,0,0,0);
        last.getAllStyles().setMargin(0,4,0,0);

        Button loginbtn = new Button("Log In");

        Style btn = loginbtn.getAllStyles();
        btn.setBgTransparency(0);
        btn.setBgImage(null);
        btn.setBorder(Border.createEmpty());
        btn.setFgColor(SAVI.DARK_PURPLE);
        btn.setFont(SAVI.bold(2.3f));
        btn.setPadding(6, 0, 0, 0);
        btn.setMargin(0, 4,0 , 0);

        loginbtn.addActionListener(e -> { LogInPage loginp = new LogInPage(); loginp.show();});

        Container f1 = new Container(new FlowLayout(Component.CENTER, Component.BOTTOM));
        f1.add(last);
        f1.add(loginbtn);

        footer.add(f1);

        signUpForm.add(BorderLayout.CENTER, form).add(BorderLayout.NORTH, header).add(BorderLayout.SOUTH, footer);
        signUpForm.show();

    }
}
