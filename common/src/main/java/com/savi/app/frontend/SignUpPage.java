package com.savi.app.frontend;

import com.codename1.ui.*; // for form, button, dialog
import com.codename1.ui.layouts.*; //boxlayout, border layout, gridlayout
import com.codename1.ui.plaf.Border;


public class SignUpPage {
    public void show() {

        int darkDarkPurple = 0x30205E;
        int darkPurple = 0x7C3AED;
        int palePurple = 0xF4EFFE;
        int navy = 0x29364D;
        int gray = 0x8491A7;
        int green = 0x00A878;
        int paleGreen = 0xE6FAF2;
        int borderColor = 0xEDF0F5;
        int white = 0xFFFFFF;

        Form signUpForm = new Form(new BorderLayout());
        signUpForm.getAllStyles().setBgColor(palePurple);
        signUpForm.getAllStyles().setBgTransparency(255);

        Container header = new Container(BoxLayout.yCenter());
        header.getAllStyles().setBgColor(darkPurple);
        header.getAllStyles().setBgTransparency(255);
        header.getAllStyles().setPadding(150, 150, 30, 30);

        Label logo = new Label("LOGO");
        Label welcome = new Label("Welcome to Savi!");
        Label sub = new Label("Start tracking your savings with us today.");

        logo.setAlignment(Component.CENTER);
        logo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));

        welcome.setAlignment(Component.CENTER);
        welcome.getAllStyles().setFgColor(white);
        welcome.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));

        sub.setAlignment(Component.CENTER);
        sub.getAllStyles().setFgColor(white);
        sub.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));

        header.add(logo);
        header.add(welcome);
        header.add(sub);

        Container form = new Container(BoxLayout.yCenter());
        form.getAllStyles().setBgColor(darkPurple);
        form.getAllStyles().setBgTransparency(255);
        form.getAllStyles().setPadding(90, 15, 30, 30);

        TextField username = new TextField("","Username");
        TextField email = new TextField("", "Email");
        TextField password = new TextField("", "Password");
        TextField cpassword = new TextField("", "Confirm Password");

        username.getAllStyles().setPadding(2, 1, 3, 1);
        email.getAllStyles().setPadding(2, 1, 3, 1);
        password.getAllStyles().setPadding(2, 1, 3, 1);
        cpassword.getAllStyles().setPadding(2, 1, 3, 1);

        username.getAllStyles().setMarginBottom(5);
        email.getAllStyles().setMarginBottom(5);
        password.getAllStyles().setMarginBottom(5);
        cpassword.getAllStyles().setMarginBottom(5);

        username.getAllStyles().setBorder(Border.createRoundBorder(125, borderColor));
        email.getAllStyles().setBorder(Border.createRoundBorder(125, borderColor));
        password.getAllStyles().setBorder(Border.createRoundBorder(125, borderColor));
        cpassword.getAllStyles().setBorder(Border.createRoundBorder(125, borderColor));

        password.setConstraint(TextField.PASSWORD);
        cpassword.setConstraint(TextField.PASSWORD);

        Container inputs = new Container(BoxLayout.y());
        inputs.getAllStyles().setBgColor(darkPurple);
        inputs.getAllStyles().setBgTransparency(255);
        inputs.add(username);
        inputs.add(email);
        inputs.add(password);
        inputs.add(cpassword);

        Button signupbtn = new Button("Sign Up");
        signupbtn.getAllStyles().setMarginTop(10);

        form.add(inputs);
        form.add(signupbtn);

        //signUpForm.add(BorderLayout.NORTH, header);
        //signUpForm.add(BorderLayout.CENTER, form);
        Container purpleSection = new Container(BoxLayout.y());
        purpleSection.getAllStyles().setBgColor(darkPurple);
        purpleSection.getAllStyles().setBgTransparency(255);

        purpleSection.add(header);
        purpleSection.add(form);

        signUpForm.add(BorderLayout.NORTH, purpleSection);
        signUpForm.show();


    }
}
