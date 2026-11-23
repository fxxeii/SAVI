package com.savi.app.backend;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.ui.Display;
import com.codename1.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class OTPManager {

    private static String BREVO_API_KEY;
    private static String SENDER_EMAIL;
    private static final String SENDER_NAME = "SAVI";

    private static String currentOtp;
    private static long otpExpiryTime;
    private static final long EXPIRY_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    // Load credentials safely when sendOtp is called
    private static void loadConfig() {
        if (BREVO_API_KEY != null && SENDER_EMAIL != null) {
            return;
        }
        try (InputStream is = OTPManager.class.getResourceAsStream("/config.properties")) {
            if (is != null) {
                String content = Util.readToString(is, "UTF-8");
                List<String> lines = StringUtil.tokenize(content, "\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("brevo.api.key=")) {
                        BREVO_API_KEY = line.substring("brevo.api.key=".length()).trim();
                    } else if (line.startsWith("brevo.sender.email=")) {
                        SENDER_EMAIL = line.substring("brevo.sender.email=".length()).trim();
                    }
                }
            } else {
                System.err.println("ERROR: /config.properties was not found in classpath!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Generate and Send OTP
    public static void sendOtp(String recipientEmail, Runnable onSuccess, OtpErrorCallback onError) {
        loadConfig();

        if (BREVO_API_KEY == null || BREVO_API_KEY.isEmpty() || SENDER_EMAIL == null || SENDER_EMAIL.isEmpty()) {
            if (onError != null) {
                onError.onError("Missing config.properties file or API key inside common/src/main/resources/.");
            }
            return;
        }

        // Generate a random 6-digit number
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        currentOtp = String.valueOf(code);
        otpExpiryTime = System.currentTimeMillis() + EXPIRY_DURATION_MS;

        ConnectionRequest req = new ConnectionRequest() {
            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                String message = "SAVI APP VERIFICATION\n"
                        + "==============================\n\n"
                        + "Hi there,\n\n"
                        + "Your One-Time Password (OTP) is:\n\n"
                        + "        [ " + currentOtp + " ]\n\n"
                        + "This code will expire in 5 minutes.\n"
                        + "If you did not request this code, please ignore this email.\n\n"
                        + "==============================\n"
                        + "Thank you,\n"
                        + "Savi App Team";

                String escapedMessage = message.replace("\n", "\\n");

                String json = "{"
                        + "\"sender\":{\"name\":\"" + SENDER_NAME + "\",\"email\":\"" + SENDER_EMAIL + "\"},"
                        + "\"to\":[{\"email\":\"" + recipientEmail.trim() + "\"}],"
                        + "\"subject\":\"Your Savi Verification Code: " + currentOtp + "\","
                        + "\"textContent\":\"" + escapedMessage + "\""
                        + "}";
                os.write(json.getBytes("UTF-8"));
            }

            @Override
            protected void handleException(Exception err) {
                err.printStackTrace();
                if (onError != null) {
                    onError.onError("Network error: " + err.toString());
                }
            }

            @Override
            protected void postResponse() {
                if (getResponseCode() == 201 || getResponseCode() == 200) {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    String serverError = "HTTP " + getResponseCode();
                    if (getResponseData() != null) {
                        try {
                            serverError += ": " + new String(getResponseData(), "UTF-8");
                        } catch (Exception e) {
                            serverError += ": " + new String(getResponseData());
                        }
                    }
                    if (onError != null) {
                        onError.onError(serverError);
                    }
                }
            }
        };

        req.setUrl("https://api.brevo.com/v3/smtp/email");
        req.setPost(true);
        req.setContentType("application/json");
        req.setReadResponseForErrors(true);
        req.setFailSilently(true);
        req.addRequestHeader("accept", "application/json");
        req.addRequestHeader("api-key", BREVO_API_KEY);

        NetworkManager.getInstance().addToQueue(req);
    }

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
