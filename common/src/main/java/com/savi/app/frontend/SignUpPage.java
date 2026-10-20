package com.savi.app.frontend;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.io.Log;
import com.codename1.ui.plaf.*;
import com.codename1.ui.plaf.RoundRectBorder;
import com.savi.app.SAVI;

import java.io.IOException;


public class SignUpPage {

    int darkDarkPurple = 0x30205E;
    int darkPurple = 0x7C3AED;
    int palePurple = 0xF4EFFE;
    int pastelLavander = 0xD8B4FE;
    int paleLavander = 0xE9D5FF;
    int pinkish = 0xC084FC;
    int bluishPurple = 0x4F46E5;
    int borderColor = 0xEDF0F5;
    int white = 0xFFFFFF;
    int black = 0x000000;
    int slategray = 0x64748B;
    int navy = 0x29364D;
    int gray = 0x8491A7;
    int green = 0x00A878;
    int paleGreen = 0xE6FAF2;

    public void show() {

        Form signUpForm = new Form(new BorderLayout());
        signUpForm.getAllStyles().setBgColor(palePurple);
        signUpForm.getAllStyles().setBgTransparency(255);

        Container header = new Container(BoxLayout.yCenter());
        header.getAllStyles().setBgColor(darkPurple);
        header.getAllStyles().setBgTransparency(255);
        header.getAllStyles().setPadding(20, 50, 0, 0);
        Style cont = header.getAllStyles();
        /*cont.setBorder(RoundRectBorder.create()
                .cornerRadius(5)
                .shadowOpacity(50)
                .shadowBlur(10)
                .shadowSpread(1f)
                .shadowX(1f)
                .shadowY(1f)
                .shadowColor(darkDarkPurple));*/
        cont.setMarginUnit(Style.UNIT_TYPE_DIPS);
        cont.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
        cont.setBackgroundGradientStartColor(pinkish);
        cont.setBackgroundGradientEndColor(darkPurple);

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
        Label welcome = SAVI.text("Welcome to Savi!", SAVI.bold(4f), white);
        Label sub = SAVI.text("Start tracking your savings with us today.", SAVI.regular(2.5f), white);

        logo.setAlignment(Component.CENTER);
        Container logoc = new Container(new FlowLayout(Component.CENTER, Component.CENTER));
        logoc.add(logo);

        welcome.setAlignment(Component.CENTER);
        sub.setAlignment(Component.CENTER);

        header.add(logoc).add(welcome).add(sub);

        Container form = new Container(BoxLayout.y());
        form.getAllStyles().setBgColor(palePurple);
        form.getAllStyles().setBgTransparency(255);
        form.getAllStyles().setPadding(70, 5, 10, 10);

        TextField usernamef = new TextField("","Enter username");
        TextField emailf = new TextField("", "Enter email");
        TextField passf = new TextField("", "Enter password");
        TextField cpassf = new TextField("", "Confirm password");

        Button showPass = new Button("Show");
        Button showCpass = new Button("Show");

        Container passContainer = new Container(new BorderLayout());
        passContainer.add(BorderLayout.CENTER, passf);
        passContainer.add(BorderLayout.EAST, showPass);

        Container cpassContainer = new Container(new BorderLayout());
        cpassContainer.add(BorderLayout.CENTER, cpassf);
        cpassContainer.add(BorderLayout.EAST, showCpass);

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
                s.setFgColor(darkPurple);
                s.setFont(SAVI.regular(2f));
                s.setPadding(0, 0, 0, 3);
                s.setMargin(0, 0,3 , 0);
            }
        }

        TextField[] fields = {usernamef, emailf, passf, cpassf};

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
        cpassf.setConstraint(TextField.PASSWORD);

        form.add(usernamef).add(emailf);
        form.add(passContainer).add(cpassContainer);

        Button signupbtn = new Button("Sign Up");

        Button[] sbtn = {signupbtn};

        for (Button btn : sbtn) {
            btn.getAllStyles().setMargin(8,0,3,3);
            btn.getAllStyles().setPadding(2f, 2f, 0f, 0f);
            btn.getAllStyles().setFont(SAVI.bold(3f));

            Style normal = signupbtn.getUnselectedStyle();
            Style hover = signupbtn.getSelectedStyle();
            Style pressed = signupbtn.getPressedStyle();

            normal.setBgImage(null);
            normal.setBorder(Border.createEmpty());
            normal.setBgTransparency(255);
            normal.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
            normal.setBackgroundGradientStartColor(darkPurple);
            normal.setBackgroundGradientEndColor(bluishPurple);
            normal.setFgColor(white);

            hover.setBgImage(null);
            hover.setBorder(Border.createEmpty());
            hover.setFgColor(white);
            hover.setBgTransparency(255);
            hover.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_HORIZONTAL);
            hover.setBackgroundGradientStartColor(bluishPurple);
            hover.setBackgroundGradientEndColor(darkPurple);

            pressed.setBgImage(null);
            pressed.setBorder(Border.createEmpty());
            pressed.setBgTransparency(255);
            pressed.setFgColor(white);
            pressed.setBgColor(darkPurple);
        }

        signupbtn.getAllStyles().setBorder(Border.createRoundBorder(140, borderColor));
        signupbtn.addActionListener(evt ->{
            String name = usernamef.getText().trim();
            String email = emailf.getText().trim();
            String pass = passf.getText().trim();
            String cpass = cpassf.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Dialog.show("Error", "Please fill in all fields.",
                        "OK", null);
            }
            else if(!pass.equals(cpass)){
                Dialog.show("Error", "Password does not match.", "Ok", null);
            }
            else {
                boolean success = SAVI.users.signup(name, email, pass);

                if (success) {
                    Dialog.show("Success", "Account created!",
                            "OK", null);

                    new HomePage().show();
                } else {
                    Dialog.show("Error", "Username already exists.",
                            "OK", null);
                }
            }
        });
        form.add(signupbtn);

        Container footer = new Container(BoxLayout.y());
        footer.getAllStyles().setBgColor(palePurple);
        footer.getAllStyles().setBgTransparency(255);
        footer.getAllStyles().setPadding(50, 20, 0, 0);
        Label last = SAVI.text("Already have an account?", SAVI.regular(2.3f), slategray);

        last.getAllStyles().setMargin(2,0,9,9);
        last.setAlignment(Component.CENTER);
        footer.add(last);

        Button loginbtn = new Button("Log In");

        Container login = new Container(new BorderLayout());
        login.add(BorderLayout.SOUTH, loginbtn);
        //login.add(BorderLayout.EAST, loginbtn);

        Style btn = loginbtn.getAllStyles();
        btn.setBgTransparency(0);
        btn.setBgImage(null);
        btn.setBorder(Border.createEmpty());
        btn.setFgColor(darkPurple);
        btn.setFont(SAVI.bold(2.3f));
        btn.setPadding(0, 0, 0, 0);
        btn.setMargin(0, 4,0 , 0);

        footer.add(login);
        loginbtn.addActionListener(e -> { LogInPage loginp = new LogInPage(); loginp.show();});

        signUpForm.add(BorderLayout.CENTER, form).add(BorderLayout.NORTH, header).add(BorderLayout.SOUTH, footer);
        signUpForm.show();

    }
}
