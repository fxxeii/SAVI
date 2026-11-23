package com.savi.app.frontend;

import com.codename1.ui.layouts.*;
import com.codename1.ui.*;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;
import com.codename1.components.SpanLabel;
import com.savi.app.SAVI;
import com.savi.app.backend.OTPManager;

public class ForgotPassPage {

    private String verifiedEmail = null;

    private void showCustomOtpDialog(String email, String accountName, TextField emailf, Button verifyBtn) {
        Dialog otpDialog = new Dialog();
        otpDialog.setLayout(new BorderLayout());
        otpDialog.setDisposeWhenPointerOutOfBounds(false);

        Style dlgStyle = otpDialog.getDialogStyle();
        dlgStyle.setBgColor(SAVI.WHITE);
        dlgStyle.setBgTransparency(255);
        dlgStyle.setBorder(RoundRectBorder.create()
                .cornerRadius(4f)
                .shadowOpacity(35)
                .shadowBlur(3)
                .shadowColor(SAVI.DARK_PURPLE)
        );
        dlgStyle.setPadding(0, 15, 0, 0);

        Container headerBox = new Container(BoxLayout.y());
        Style hdrStyle = headerBox.getAllStyles();
        hdrStyle.setBgTransparency(255);
        hdrStyle.setBgColor(SAVI.DARK_PURPLE);
        hdrStyle.setPadding(25, 10, 5, 5);
        hdrStyle.setBorder(RoundRectBorder.create()
                .cornerRadius(3f)
                .shadowOpacity(35)
                .shadowBlur(3)
                .shadowColor(SAVI.DARK_PURPLE)
        );

        Label titleLbl = SAVI.text("Verify Account", SAVI.bold(2.8f), SAVI.WHITE);
        titleLbl.setAlignment(Component.CENTER);
        headerBox.add(titleLbl);

        Container bodyBox = new Container(BoxLayout.y());
        bodyBox.getAllStyles().setPadding(15, 5, 20, 20);

        // Display the account's username found for this email
        Label accountLbl = SAVI.text("Account: " + accountName, SAVI.bold(2.3f), SAVI.DARK_PURPLE);
        accountLbl.setAlignment(Component.CENTER);
        accountLbl.getAllStyles().setMargin(0, 6, 0, 0);

        SpanLabel sentMsg = new SpanLabel(
                "A verification code was sent to " + email + ", please check your email."
        );
        sentMsg.getTextAllStyles().setFont(SAVI.regular(2f));
        sentMsg.getTextAllStyles().setFgColor(SAVI.DEEP_SLATE);
        sentMsg.getTextAllStyles().setAlignment(Component.CENTER);
        sentMsg.getAllStyles().setMargin(0, 10, 0, 0);

        Label promptLbl = SAVI.text("Input verification code:", SAVI.bold(2.1f), SAVI.DARK_PURPLE);
        promptLbl.getAllStyles().setMargin(5, 4, 2, 0);

        TextField otpField = new TextField("", "Enter 6-digit code", 6, TextField.NUMERIC);
        otpField.getAllStyles().setFont(SAVI.bold(2.5f));
        otpField.getAllStyles().setFgColor(SAVI.BLACK);
        otpField.getAllStyles().setPadding(1, 1, 2, 2);
        otpField.getHintLabel().getAllStyles().setPadding(1, 1, 2, 2);
        otpField.getHintLabel().getAllStyles().setFont(SAVI.italic(1.9f));
        otpField.getHintLabel().getAllStyles().setFgColor(SAVI.GRANITE_GRAY);
        otpField.setSingleLineTextArea(true);

        Style[] tfStyles = {otpField.getUnselectedStyle(), otpField.getSelectedStyle(), otpField.getPressedStyle()};
        for (Style s : tfStyles) {
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
            s.setBorder(RoundRectBorder.create().cornerRadius(1f));
        }

        bodyBox.add(accountLbl).add(sentMsg).add(promptLbl).add(otpField);

        Button confirmBtn = new Button("Verify Code");
        confirmBtn.getAllStyles().setFont(SAVI.bold(2.2f));
        confirmBtn.getAllStyles().setPadding(1, 1, 1, 1);
        confirmBtn.getAllStyles().setMargin(2, 2, 3, 3);
        confirmBtn.getAllStyles().setBorder(Border.createRoundBorder(80, SAVI.BORDER_COLOR));

        Style[] cStyles = {confirmBtn.getUnselectedStyle(), confirmBtn.getSelectedStyle(), confirmBtn.getPressedStyle()};
        for (Style s : cStyles) {
            s.setBgTransparency(255);
            s.setBgColor(SAVI.DARK_PURPLE);
            s.setFgColor(SAVI.WHITE);
        }

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getAllStyles().setFont(SAVI.regular(2f));
        cancelBtn.getAllStyles().setPadding(1, 1, 1, 1);
        cancelBtn.getAllStyles().setMargin(2, 2, 3, 3);
        cancelBtn.getAllStyles().setBorder(Border.createRoundBorder(80, SAVI.BORDER_COLOR));

        Style[] xStyles = {cancelBtn.getUnselectedStyle(), cancelBtn.getSelectedStyle(), cancelBtn.getPressedStyle()};
        for (Style s : xStyles) {
            s.setBgTransparency(255);
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setFgColor(SAVI.DEEP_SLATE);
        }

        confirmBtn.addActionListener(ev -> {
            String enteredCode = otpField.getText().trim();
            if (OTPManager.verifyOtp(enteredCode)) {
                verifiedEmail = email;
                verifyBtn.setText("Verified");
                verifyBtn.getParent().revalidate();
                otpDialog.dispose();
                Dialog.show("Success", "Email verified for " + accountName + "! You can now save your new password.", "OK", null);
            } else {
                Dialog.show("Invalid Code", "The code you entered is incorrect or expired.", "OK", null);
            }
        });

        cancelBtn.addActionListener(ev -> otpDialog.dispose());

        Container buttonRow = new Container(new GridLayout(1, 2));
        buttonRow.getAllStyles().setPadding(5, 5, 15, 15);
        buttonRow.add(cancelBtn).add(confirmBtn);

        otpDialog.add(BorderLayout.NORTH, headerBox);
        otpDialog.add(BorderLayout.CENTER, bodyBox);
        otpDialog.add(BorderLayout.SOUTH, buttonRow);

        int width = Display.getInstance().getDisplayWidth();
        int height = Display.getInstance().getDisplayHeight();
        int sideMargin = (int) (width * 0.08f);
        int topBottomMargin = (int) (height * 0.28f);

        otpDialog.show(topBottomMargin, topBottomMargin, sideMargin, sideMargin);
    }

    public void show(){
        Form ForgotpForm = new Form(new BorderLayout());
        ForgotpForm.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        ForgotpForm.getAllStyles().setBgTransparency(255);

        Container t = ForgotpForm.getTitleArea();
        t.getAllStyles().setBgColor(SAVI.PALE_PURPLE);
        t.getAllStyles().setBgTransparency(255);
        for (int i = 0; i < t.getComponentCount(); i++) {
            Style s = t.getComponentAt(i).getAllStyles();
            s.setBgColor(SAVI.PALE_PURPLE);
            s.setBgTransparency(255);
        }

        Container backHeader = new Container(new BorderLayout());
        Button back = new Button();
        Style bs = back.getAllStyles();
        bs.setFgColor(SAVI.BLACK);
        bs.setBgTransparency(0);
        bs.setBorder(RoundBorder.create().color(SAVI.WHITE));
        bs.setPaddingUnit(Style.UNIT_TYPE_DIPS);
        bs.setPadding(0.6f, 0.6f, 0.6f, 0.6f);
        bs.setMarginUnit(Style.UNIT_TYPE_DIPS);
        bs.setMargin(1f, 0, 2f, 0);
        FontImage.setMaterialIcon(back, FontImage.MATERIAL_CHEVRON_LEFT, 4f);
        back.addActionListener(e -> new LogInPage().show());
        backHeader.add(BorderLayout.WEST, back);

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

        Label fp = SAVI.text("Forgot Password", SAVI.bold(4f), SAVI.WHITE);
        Label sub1 = SAVI.text("Enter a new password below to change", SAVI.regular(2.3f), SAVI.WHITE);
        Label sub2 = SAVI.text("your password", SAVI.regular(2.3f), SAVI.WHITE);

        fp.getAllStyles().setAlignment(Component.CENTER);
        sub1.getAllStyles().setPadding(0,0,0,0);
        sub1.getAllStyles().setAlignment(Component.CENTER);
        sub2.getAllStyles().setPadding(0,0,0,0);
        sub2.getAllStyles().setAlignment(Component.CENTER);

        Container form = new Container(BoxLayout.y());

        Label eml = SAVI.text("Email", SAVI.regular(2.3f), SAVI.WHITE);
        Label ps = SAVI.text("New Password", SAVI.regular(2.3f), SAVI.WHITE);
        Label cps = SAVI.text("Confirm New Password", SAVI.regular(2.3f), SAVI.WHITE);

        TextField emailf = new TextField("", "example@mail.com");
        TextField psf = new TextField("", "e.g. 012345savi");
        TextField cpsf = new TextField("", "e.g. 012345savi");

        Label[] labels = {eml, ps, cps};

        for (Label lbl : labels) {
            lbl.getAllStyles().setMargin(0, 0, 0, 0);
            lbl.getAllStyles().setPadding(3, 0, 5, 0);
        }

        TextField[] fields = {emailf, psf, cpsf};

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

        emailf.getAllStyles().setPadding(2, 1.5f, 3, 4);
        emailf.getAllStyles().setMargin(1, 1, 5, 1.5f);
        psf.getAllStyles().setPadding(2, 1.5f, 3, 4);
        psf.getAllStyles().setMargin(1, 1, 5, 1.5f);
        cpsf.getAllStyles().setPadding(2, 1.5f, 3, 4);
        cpsf.getAllStyles().setMargin(1, 1, 5, 1.5f);

        psf.setConstraint(TextField.PASSWORD);
        cpsf.setConstraint(TextField.PASSWORD);

        Button verify = new Button("Verify");
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

        emailf.addDataChangedListener((type, index) -> {
            String currentText = emailf.getText().trim();
            if (verifiedEmail != null && !verifiedEmail.equals(currentText)) {
                verifiedEmail = null;
                verify.setText("Verify");
                verify.getParent().revalidate();
            }
        });

        verify.addActionListener(e -> {
            String email = emailf.getText().trim();
            if (email.isEmpty()) {
                Dialog.show("Missing Email", "Please enter your email address first.", "OK", null);
                return;
            }
            if (!SAVI.user.validEmail(email)) {
                Dialog.show("Invalid Email", "Please enter a valid email address.", "OK", null);
                return;
            }

            if (!SAVI.user.emailExists(email)) {
                Dialog.show("Account Not Found", "No account is registered with this email address.", "OK", null);
                return;
            }
            String accountName = SAVI.user.getUsernameByEmail(email);
            verify.setEnabled(false);
            verify.setText("Sending...");
            OTPManager.sendOtp(
                    email,
                    () -> Display.getInstance().callSerially(() -> {
                        verify.setEnabled(true);
                        verify.setText("Verify");
                        showCustomOtpDialog(email, accountName, emailf, verify);
                    }),
                    errorMsg -> Display.getInstance().callSerially(() -> {
                        verify.setEnabled(true);
                        verify.setText("Verify");
                        Dialog.show("Failed to Send Code", errorMsg, "OK", null);
                    })
            );
        });

        Button[] allRightButtons = {verify, showPass, showCpass};

        for (Button btn : allRightButtons) {
            Style[] styles = {
                    btn.getUnselectedStyle(),
                    btn.getSelectedStyle(),
                    btn.getPressedStyle(),
                    btn.getDisabledStyle()
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

        Container emlContainer = new Container(new BorderLayout());
        emlContainer.add(BorderLayout.NORTH, eml);
        emlContainer.add(BorderLayout.CENTER, emailf);
        emlContainer.add(BorderLayout.EAST, verify);

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

        form.add(emlContainer).add(pasContainer).add(cpassContainer);
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
            String email = emailf.getText().trim();

            if (verifiedEmail == null || !verifiedEmail.equals(email)) {
                Dialog.show("Email Not Verified", "Please verify your email address first before changing your password.", "OK", null);
                return;
            }

            String result = SAVI.user.resetPassByEmail(
                    email,
                    psf.getText(),
                    cpsf.getText()
            );

            if (result.equals("success")) {
                Dialog.show("Success", "Password changed successfully!", "OK", null);
                new LogInPage().show();
            } else {
                Dialog.show("Error", result, "OK", null);
            }
        });

        Container top = new Container(BoxLayout.y());
        top.add(backHeader).add(box);

        ForgotpForm.add(BorderLayout.NORTH, top).add(BorderLayout.SOUTH, savebtn);
        ForgotpForm.show();
    }
}