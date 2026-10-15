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
        cont.setMargin(0, 5f, 0, 0);
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
        form.getAllStyles().setPadding(20, 15, 10, 10);

        TextField username = new TextField("","Enter username");
        TextField email = new TextField("", "Enter email");
        TextField pass = new TextField("", "Enter password");
        TextField cpass = new TextField("", "Confirm password");

        //input
        username.getAllStyles().setFont(SAVI.regular(3f));
        email.getAllStyles().setFont(SAVI.regular(3f));
        pass.getAllStyles().setFont(SAVI.regular(3f));
        cpass.getAllStyles().setFont(SAVI.regular(3f));

        username.getAllStyles().setFgColor(black);
        email.getAllStyles().setFgColor(black);
        pass.getAllStyles().setFgColor(black);
        cpass.getAllStyles().setFgColor(black);

        //hint
        username.getHintLabel().getAllStyles().setFont(SAVI.regular(2.5f));
        email.getHintLabel().getAllStyles().setFont(SAVI.regular(2.5f));
        pass.getHintLabel().getAllStyles().setFont(SAVI.regular(2.5f));
        cpass.getHintLabel().getAllStyles().setFont(SAVI.regular(2.5f));

        username.getHintLabel().getAllStyles().setFgColor(gray);
        email.getHintLabel().getAllStyles().setFgColor(gray);
        pass.getHintLabel().getAllStyles().setFgColor(gray);
        cpass.getHintLabel().getAllStyles().setFgColor(gray);

        TextField[] fields = {username, email, pass, cpass};

        for (TextField field : fields) {
            field.getAllStyles().setPadding(2, 1, 3, 4);
            field.getAllStyles().setMargin(2, 2, 9, 9);
            field.setSingleLineTextArea(true);
            //field.setRows(1);
            //field.getAllStyles().setPaddingUnit(Style.UNIT_TYPE_DIPS);
            //field.getAllStyles().setPadding(Component.TOP, 0.5f);
            //field.getAllStyles().setPadding(Component.BOTTOM, 0.5f);
            Style normal = field.getUnselectedStyle();
            Style focused = field.getSelectedStyle();

            normal.setBgColor(white);
            normal.setBgTransparency(255);
            normal.setBorder(
                    RoundRectBorder.create()
                            .cornerRadius(4)
                            .shadowOpacity(50)
                            .shadowBlur(10)
                            .shadowSpread(1f)
                            .shadowX(0.5f)
                            .shadowY(0f)
                            .shadowColor(darkDarkPurple)
            );

            focused.setBgColor(white);
            focused.setBgTransparency(255);
            focused.setBorder(
                    RoundRectBorder.create()
                            .cornerRadius(4)
                            .shadowOpacity(50)
                            .shadowBlur(10)
                            .shadowSpread(1f)
                            .shadowX(0.5f)
                            .shadowY(0f)
                            .shadowColor(darkDarkPurple)
            );

        }

        pass.setConstraint(TextField.PASSWORD);
        cpass.setConstraint(TextField.PASSWORD);

        form.add(username).add(email).add(pass).add(cpass);

        Button signupbtn = new Button("Sign Up");
        signupbtn.getAllStyles().setMarginTop(10);

        form.add(signupbtn);

        signUpForm.add(BorderLayout.CENTER, form).add(BorderLayout.NORTH, header);
        signUpForm.show();

    }
}
