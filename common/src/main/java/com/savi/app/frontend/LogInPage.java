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


    int darkDarkPurple = 0x30205E;
    int darkPurple = 0x7C3AED;
    int palePurple = 0xF4EFFE;
    int pastelLavander = 0xD8B4FE;
    int paleLavander = 0xE9D5FF;
    int bluishPurple = 0x4F46E5;
    int borderColor = 0xEDF0F5;
    int white = 0xFFFFFF;
    int black = 0x000000;
    int slategray = 0x64748B;
    int navy = 0x29364D;
    int gray = 0x8491A7;
    int green = 0x00A878;
    int paleGreen = 0xE6FAF2;


    public void show(){

        Form LogInForm = new Form(new BorderLayout());
        LogInForm.getAllStyles().setBgColor(palePurple);
        LogInForm.getAllStyles().setBgTransparency(255);

        Container header = new Container(BoxLayout.yCenter());
        header.getAllStyles().setBgColor(darkPurple);
        header.getAllStyles().setBgTransparency(255);
        header.getAllStyles().setPadding(225, 20, 0, 0);

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
        Label welcome = SAVI.text("Welcome Back!", SAVI.bold(5f), white);
        Label sub = SAVI.text("Continue your savings with Savi!", SAVI.regular(2.5f), white);

        logo.setAlignment(Component.CENTER);
        Container logoc = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        logoc.add(logo);

        welcome.setAlignment(Component.CENTER);
        sub.setAlignment(Component.CENTER);

        header.add(logoc).add(welcome).add(sub);

        Container form = new Container(BoxLayout.y());
        //form.getAllStyles().setBgColor(darkPurple);
        form.getAllStyles().setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        form.getAllStyles().setBackgroundGradientStartColor(darkPurple);
        form.getAllStyles().setBackgroundGradientEndColor(0x5C21B7);
        form.getAllStyles().setBgTransparency(255);
        form.getAllStyles().setPadding(20, 0, 10, 10);

        TextField usernamef = new TextField("","Enter username");
        TextField passf = new TextField("", "Enter password");

        Button showPass = new Button("Show");

        Container passContainer = new Container(new BorderLayout());
        passContainer.add(BorderLayout.CENTER, passf);
        passContainer.add(BorderLayout.EAST, showPass);

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
                s.setFgColor(white);
                s.setFont(SAVI.regular(2f));
                s.setPadding(0, 0, 0, 3);
                s.setMargin(0, 0,3 , 0);
            }
        }

        TextField[] fields = {usernamef, passf};

        for (TextField field : fields) {
            field.getAllStyles().setFont(SAVI.regular(3f));
            field.getAllStyles().setFgColor(black);
            field.getHintLabel().getAllStyles().setFont(SAVI.regular(2.5f));
            field.getHintLabel().getAllStyles().setFgColor(gray);

            field.getAllStyles().setPadding(2, 1, 3, 4);
            field.getAllStyles().setMargin(2, 2, 9, 9);
            field.setSingleLineTextArea(true);

            Style normal = field.getUnselectedStyle();
            Style focused = field.getSelectedStyle();
            Style pressed = field.getPressedStyle();

            normal.setBgColor(white);
            normal.setBgTransparency(255);
            normal.setBgImage(null);
            normal.setBorder(Border.createEmpty());
            normal.setBorder(RoundRectBorder.create()
                    .cornerRadius(4f)
                    .shadowOpacity(50)
                    .shadowBlur(1)
                    .shadowSpread(0.3f)
                    .shadowX(0.5f)
                    .shadowY(2f)
                    .shadowColor(darkDarkPurple)
            );

            focused.setBgColor(white);
            focused.setBgTransparency(255);
            focused.setBgImage(null);
            focused.setBorder(Border.createEmpty());
            focused.setBorder(RoundRectBorder.create()
                    .cornerRadius(4f)
                    .shadowOpacity(50)
                    .shadowBlur(1)
                    .shadowSpread(0.3f)
                    .shadowX(0.5f)
                    .shadowY(2f)
                    .shadowColor(darkDarkPurple)
            );

            pressed.setBgColor(white);
            pressed.setBgTransparency(255);
            pressed.setBgImage(null);
            pressed.setBorder(Border.createEmpty());
            pressed.setBorder(RoundRectBorder.create()
                    .cornerRadius(4f)
                    .shadowOpacity(50)
                    .shadowBlur(1)
                    .shadowSpread(0.3f)
                    .shadowX(0.5f)
                    .shadowY(2f)
                    .shadowColor(darkDarkPurple)
            );
        }

        passf.setConstraint(TextField.PASSWORD);

        form.add(usernamef);
        form.add(passContainer);

        Button loginbtn = new Button("Log In");

        Button[] sbtn = {loginbtn};

        for (Button btn : sbtn) {
            btn.getAllStyles().setMargin(8,0,10,10);
            btn.getAllStyles().setPadding(2f, 2f, 0f, 0f);
            btn.getAllStyles().setFont(SAVI.bold(3f));

            Style normal = loginbtn.getUnselectedStyle();
            Style hover = loginbtn.getSelectedStyle();
            Style pressed = loginbtn.getPressedStyle();

            normal.setBgImage(null);
            normal.setBorder(Border.createEmpty());
            normal.setBgTransparency(255);
            normal.setBgColor(white);
            normal.setFgColor(darkPurple);

            hover.setBgImage(null);
            hover.setBorder(Border.createEmpty());
            hover.setFgColor(white);
            hover.setBgTransparency(255);
            hover.setBgColor(darkPurple);

            pressed.setBgImage(null);
            pressed.setBorder(Border.createEmpty());
            pressed.setBgTransparency(255);
            pressed.setFgColor(white);
            pressed.setBgColor(darkPurple);
        }

        loginbtn.getAllStyles().setBorder(Border.createRoundBorder(140, borderColor));
        loginbtn.addActionListener(evt ->{
            String name = usernamef.getText().trim();
            String pass = passf.getText().trim();

            if (name.isEmpty() || pass.isEmpty()) {
                Dialog.show("Error", "Please fill in all fields.",
                        "OK", null);
            }
            else {
                boolean success = SAVI.users.login(name, pass);

                if (success) {
                    Dialog.show("Success", "Account created!",
                            "OK", null);

                    new HomePage().show();
                } else {
                    Dialog.show("Error", "Account does not exists.",
                            "OK", null);
                }
            }
        });
        form.add(loginbtn);

        Container footer = new Container(BoxLayout.y());
        footer.getAllStyles().setBgColor(palePurple);
        footer.getAllStyles().setBgTransparency(255);
        footer.getAllStyles().setMargin(150,0,0,0);
        footer.getAllStyles().setPadding(40, 20, 0, 0);

        Label last = SAVI.text("No account yet?", SAVI.regular(2.3f), slategray);
        last.getAllStyles().setMargin(10,0,9,9);
        last.setAlignment(Component.CENTER);
        footer.add(last);

        Button signupbtn = new Button("Sign Up");

        Container signup = new Container(new BorderLayout());
        signup.add(BorderLayout.SOUTH, signupbtn);
        //login.add(BorderLayout.EAST, loginbtn);

        Style btn = signupbtn.getAllStyles();
        btn.setBgTransparency(0);
        btn.setBgImage(null);
        btn.setBorder(Border.createEmpty());
        btn.setFgColor(darkPurple);
        btn.setFont(SAVI.bold(2.3f));
        btn.setPadding(0, 0, 0, 0);
        btn.setMargin(0, 4,0 , 0);

        footer.add(signup);
        signupbtn.addActionListener(e -> { SignUpPage signin = new SignUpPage(); signin.show();});

        LogInForm.add(BorderLayout.CENTER, form).add(BorderLayout.NORTH, header).add(BorderLayout.SOUTH, footer);
        LogInForm.show();
    }
}
