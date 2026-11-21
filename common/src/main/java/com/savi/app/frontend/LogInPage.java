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

        Container t = LogInForm.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.DARK_PURPLE);
            s.setBgTransparency(255);
        }

        Container header = new Container(BoxLayout.yCenter());
        header.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        header.getAllStyles().setBgTransparency(255);
        header.getAllStyles().setPadding(225, 60, 0, 0);

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

        Container txt = new Container(BoxLayout.y());

        TextField usernamef = new TextField("","Username");
        TextField passf = new TextField("", "Password");

        TextField[] fields = {usernamef, passf};

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

        passf.setConstraint(TextField.PASSWORD);

        Button showPass = new Button("Show");

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

        Button forgotp = new Button("Forgot Password");

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

        forgotp.addActionListener(e -> {ForgotPassPage fppage = new ForgotPassPage(); fppage.show();});

        Container usnContainer = new Container(new BorderLayout());
        usnContainer.add(BorderLayout.CENTER, usernamef);

        Container passContainer = new Container(new BorderLayout());
        passContainer.add(BorderLayout.CENTER, passf);
        passContainer.add(BorderLayout.EAST, showPass);
        passContainer.add(BorderLayout.SOUTH, forgotp);

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
            h.setBgColor(SAVI.WHITE);
            n.setFgColor(SAVI.DARK_PURPLE);

            h.setBgImage(null);
            h.setBorder(Border.createEmpty());
            h.setFgColor(SAVI.WHITE);
            h.setBgTransparency(255);
            h.setBgColor(SAVI.DARK_PURPLE);

            p.setBgImage(null);
            p.setBorder(Border.createEmpty());
            p.setBgTransparency(255);
            p.setFgColor(SAVI.WHITE);
            p.setBgColor(SAVI.DARK_DARK_PURPLE);
        }

        loginbtn.getAllStyles().setBorder(Border.createRoundBorder(140, SAVI.BORDER_COLOR));
        loginbtn.addActionListener(evt ->{
            String name = usernamef.getText().trim();
            String pass = passf.getText().trim();

            String result = SAVI.user.login(name, pass);

            if (result.equals("success")) {
                Dialog.show("Success", "Login successful!", "OK", null);
                new HomePage().show();
            } else {
                Dialog.show("Error", result, "OK", null);
            }
        });

        txt.add(usnContainer).add(passContainer).add(loginbtn);
        form.add(txt);

        Container footer = new Container(BoxLayout.y());
        footer.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        footer.getAllStyles().setBgTransparency(255);
        footer.getAllStyles().setMargin(150,0,0,0);
        footer.getAllStyles().setPadding(40, 20, 0, 0);

        Label last = SAVI.text("No account yet? ", SAVI.regular(2.3f), SAVI.SLATE_GRAY);
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
