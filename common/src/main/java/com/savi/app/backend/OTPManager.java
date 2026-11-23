package com.savi.app.backend;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
//EMAIL OTP


public class OTPManager {

    private static final String BREVO_API_KEY = "BREVO_API_KEY";
    private static final String SENDER_EMAIL = "hello.saviph@gmail.com";
    private static final String SENDER_NAME = "SAVI";

    private static String currentOtp;
    private static long otpExpiryTime;
    private static final long EXPIRY_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    // 1. Generate and Send OTP
    public static void sendOtp(String recipientEmail, Runnable onSuccess, OtpErrorCallback onError) {
        // Generate a random 6-digit number
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        currentOtp = String.valueOf(code);
        otpExpiryTime = System.currentTimeMillis() + EXPIRY_DURATION_MS;

        // Inside OTPManager.java -> sendOtp(...)
        ConnectionRequest req = new ConnectionRequest() {
            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                String message = "SAVI APP VERIFICATION\n"
                        + "============================================================\n\n"
                        + "Hi there,\n\n"
                        + "Your One-Time Password (OTP) is:\n\n"
                        + "        [ " + currentOtp + " ]\n\n"
                        + "This code will expire in 5 minutes.\n"
                        + "If you did not request this code, please ignore this email.\n\n"
                        + "============================================================\n"
                        + "Thank you,\n"
                        + "Savi Team";

                String escapedMessage = message
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\r", "")
                        .replace("\n", "\\n");

                String json = "{"
                        + "\"sender\":{\"name\":\"" + SENDER_NAME + "\",\"email\":\"" + SENDER_EMAIL + "\"},"
                        + "\"to\":[{\"email\":\"" + recipientEmail.trim() + "\"}],"
                        + "\"subject\":\"Your Savi Verification Code\","
                        + "\"textContent\":\"" + escapedMessage + "\""
                        + "}";
                os.write(json.getBytes("UTF-8"));
            }

            @Override
            protected void postResponse() {
                if (getResponseCode() == 201 || getResponseCode() == 200) {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    String serverError = "No details provided";
                    if (getResponseData() != null) {
                        try {
                            serverError = new String(getResponseData(), "UTF-8");
                        } catch (Exception e) {
                            serverError = new String(getResponseData());
                        }
                    }
                    if (onError != null) {
                        onError.onError("Error (" + getResponseCode() + "): " + serverError);
                    }
                }
            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                // Handled in postResponse()
            }

            @Override
            protected void handleException(Exception err) {
                // Catches offline / DNS / SSL errors so the button doesn't stay disabled forever
                if (onError != null) {
                    onError.onError("Network error: " + err.getMessage());
                }
            }
        };

        req.setUrl("https://api.brevo.com/v3/smtp/email");
        req.setPost(true);
        req.setContentType("application/json");
        req.setReadResponseForErrors(true);
        req.addRequestHeader("accept", "application/json");
        req.addRequestHeader("api-key", BREVO_API_KEY);

        NetworkManager.getInstance().addToQueue(req);
    }

    ;


    // 2. Verify the User's Input
    public static boolean verifyOtp(String userInput) {
        if (currentOtp == null || userInput == null) {
            return false;
        }
        if (System.currentTimeMillis() > otpExpiryTime) {
            currentOtp = null; // Expired
            return false;
        }
        boolean isValid = currentOtp.equals(userInput.trim());
        if (isValid) {
            currentOtp = null; // Clear after successful use
        }
        return isValid;
    }

    public interface OtpErrorCallback {
        void onError(String errorMessage);
    }
}
