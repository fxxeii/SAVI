package com.savi.app.frontend;

import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.*;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.savi.app.SAVI;

public class ForgotPassPage {

    public void show(){
        Form ForgotpForm = new Form(new BorderLayout());
        ForgotpForm.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        ForgotpForm.getAllStyles().setBgTransparency(255);

        Container box = new Container(BoxLayout.y());
        box.getAllStyles().setBgColor(SAVI.DARK_PURPLE);
        box.getAllStyles().setBgTransparency(255);
        box.getAllStyles().setPadding(50, 50, 0, 0);
        box.getAllStyles().setMargin(50,0,100,100);
        box.getAllStyles().setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(25)
                .shadowBlur(1)
                .shadowSpread(0.3f)
                .shadowX(0.5f)
                .shadowY(1.5f)
                .shadowColor(SAVI.GRANITE_GRAY)
        );

        ForgotpForm.getToolbar().addMaterialCommandToLeftBar(
                "", FontImage.MATERIAL_CHEVRON_LEFT,e -> {
                    LogInPage lipage = new LogInPage(); lipage.show();
                }
        );

        Label fp = SAVI.text("Forgot Password", SAVI.bold(4f), SAVI.WHITE);
        Label sub1 = SAVI.text("Enter a new password below to change", SAVI.regular(2.3f), SAVI.WHITE);
        Label sub2 = SAVI.text("your password", SAVI.regular(2.3f), SAVI.WHITE);

        fp.getAllStyles().setAlignment(Component.CENTER);
        sub1.getAllStyles().setPadding(0,0,0,0);
        sub1.getAllStyles().setAlignment(Component.CENTER);
        sub2.getAllStyles().setPadding(0,0,0,0);
        sub2.getAllStyles().setAlignment(Component.CENTER);

        Container form = new Container(BoxLayout.y());

        Label usn = SAVI.text("Username", SAVI.regular(2.3f), SAVI.WHITE);
        Label ps = SAVI.text("New Password", SAVI.regular(2.3f), SAVI.WHITE);
        Label cps = SAVI.text("Confirm New Password", SAVI.regular(2.3f), SAVI.WHITE);

        TextField usernamef = new TextField("", "e.g. username");
        TextField psf = new TextField("", "e.g. 012345savi");
        TextField cpsf = new TextField("", "e.g. 012345savi");

        Label[] labels = {usn, ps, cps};

        for (Label lbl : labels) {
            lbl.getAllStyles().setMargin(0, 0, 0, 0);
            lbl.getAllStyles().setPadding(3, 0, 5, 0);
        }

        TextField[] fields = {usernamef, psf, cpsf};

        for (TextField tf : fields) {
            tf.getAllStyles().setFont(SAVI.regular(2.5f));
            tf.getAllStyles().setFgColor(SAVI.BLACK);
            tf.getAllStyles().setPadding(2, 1.5f, 3, 4);
            tf.getAllStyles().setMargin(1, 1, 5, 9);

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
                        .shadowBlur(3)
                        .shadowSpread(0.3f)
                        .shadowX(0.5f)
                        .shadowY(1.5f)
                        .shadowColor(SAVI.GRANITE_GRAY)
                );
            }
        }

        psf.getAllStyles().setPadding(2, 1.5f, 3, 4);
        psf.getAllStyles().setMargin(1, 1, 5, 1.5f);
        cpsf.getAllStyles().setPadding(2, 1.5f, 3, 4);
        cpsf.getAllStyles().setMargin(1, 1, 5, 1.5f);

        psf.setConstraint(TextField.PASSWORD);
        cpsf.setConstraint(TextField.PASSWORD);

        Button showPass = new Button("Show");
        Button showCpass = new Button("Show");

        TextField[] passFields = {psf, cpsf};
        Button[] showButtons = {showPass, showCpass};

        for (int i = 0; i < passFields.length; i++) {
            TextField tf = passFields[i];
            Button btn = showButtons[i];

            btn.addActionListener(e -> {
                if (tf.getConstraint() == TextField.PASSWORD) {
                    tf.setConstraint(TextField.ANY);
                    btn.setText("Hide");
                } else {
                    tf.setConstraint(TextField.PASSWORD);
                    btn.setText("Show");
                }
                tf.repaint();
                tf.getParent().revalidate();
            });
        }

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
                s.setMargin(0, 0, 0, 0);
            }
        }

        Container usnContainer = new Container(new BorderLayout());
        usnContainer.add(BorderLayout.NORTH, usn);
        usnContainer.add(BorderLayout.CENTER, usernamef);

        Container pasContainer = new Container(new BorderLayout());
        pasContainer.add(BorderLayout.NORTH, ps);
        pasContainer.add(BorderLayout.CENTER, psf);
        pasContainer.add(BorderLayout.EAST, showPass);

        Container cpassContainer = new Container(new BorderLayout());
        cpassContainer.add(BorderLayout.NORTH, cps);
        cpassContainer.add(BorderLayout.CENTER, cpsf);
        cpassContainer.add(BorderLayout.EAST, showCpass);

        Label warning = SAVI.text("Your password must contain: ", SAVI.bold(2f), SAVI.WHITE);
        Label sub3 = SAVI.text("  * At least 5 characters in length ", SAVI.regular(2f), SAVI.WHITE);
        warning.getAllStyles().setPadding(3, 0, 5, 0);
        sub3.getAllStyles().setPadding(0.5f, 6, 5, 0);

        form.add(usnContainer).add(pasContainer).add(cpassContainer);
        form.add(warning).add(sub3);

        box.add(fp).add(sub1).add(sub2);
        box.add(form);

        Button savebtn = new Button("Save Password");

        Button[] sbtn = {savebtn};

        for (Button btn : sbtn) {
            btn.getAllStyles().setMargin(0,26,9,9);
            btn.getAllStyles().setPadding(2f, 2f, 0f, 0f);
            btn.getAllStyles().setFont(SAVI.bold(2.5f));

            Style n = savebtn.getUnselectedStyle();
            Style h = savebtn.getSelectedStyle();
            Style p = savebtn.getPressedStyle();

            n.setBgImage(null);
            n.setBorder(Border.createEmpty());
            n.setBgTransparency(255);
            n.setBgColor(SAVI.DARK_PURPLE);
            n.setFgColor(SAVI.WHITE);

            h.setBgImage(null);
            h.setBorder(Border.createEmpty());
            h.setFgColor(SAVI.DARK_PURPLE);
            h.setBgTransparency(255);
            h.setBgColor(SAVI.WHITE);

            p.setBgImage(null);
            p.setBorder(Border.createEmpty());
            p.setBgTransparency(255);
            p.setFgColor(SAVI.WHITE);
            p.setBgColor(SAVI.DARK_PURPLE);
        }
        savebtn.getAllStyles().setBorder(Border.createRoundBorder(140, SAVI.BORDER_COLOR));

        savebtn.addActionListener(e -> {
            String result = SAVI.user.resetPass(
                    usernamef.getText(),
                    psf.getText(),
                    cpsf.getText()
            );

            if (result.equals("success")) {
                Dialog.show("Success", "Password changed successfully!", "OK", null);

                LogInPage lipage = new LogInPage();
                lipage.show();
            } else {
                Dialog.show("Error", result, "OK", null);
            }
        });

        ForgotpForm.add(BorderLayout.NORTH, box).add(BorderLayout.SOUTH, savebtn);
        ForgotpForm.show();
    }
}
