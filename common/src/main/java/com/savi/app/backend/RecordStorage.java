package com.savi.app.backend;

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

final class RecordStorage {
    private RecordStorage() {
    }

    static String loadOrSeed(Class<?> resourceOwner, String fileRec) {
        if (Storage.getInstance().exists(fileRec)) {
            return readLocal(fileRec);
        }
        String seeded = readResource(resourceOwner, fileRec);
        if (!seeded.isEmpty()) {
            writeLocal(fileRec, seeded);
        }
        return seeded;
    }

    static String readLocal(String fileRec) {
        try {
            if (!Storage.getInstance().exists(fileRec)) {
                return "";
            }
            try (InputStream in = Storage.getInstance().createInputStream(fileRec)) {
                String text = Util.readToString(in, "UTF-8");
                return text == null ? "" : text;
            }
        } catch (Exception ex) {
            Log.e(ex);
            return "";
        }
    }

    static String readResource(Class<?> resourceOwner, String fileRec) {
        try {
            InputStream resource = Display.getInstance().getResourceAsStream(resourceOwner, "/" + fileRec);
            if (resource == null) {
                return "";
            }
            try (InputStream in = resource) {
                String text = Util.readToString(in, "UTF-8");
                return text == null ? "" : text;
            }
        } catch (Exception ex) {
            Log.e(ex);
            return "";
        }
    }

    static boolean writeLocal(String fileRec, String text) {
        try (OutputStream out = Storage.getInstance().createOutputStream(fileRec)) {
            out.write((text == null ? "" : text).getBytes("UTF-8"));
            return true;
        } catch (Exception ex) {
            Log.e(ex);
            return false;
        }
    }

    static void deleteLocal(String fileRec) {
        if (Storage.getInstance().exists(fileRec)) {
            Storage.getInstance().deleteStorageFile(fileRec);
        }
    }

    /** Splits a whole file into its non-empty, trimmed lines. */
    static ArrayList<String> lines(String text) {
        ArrayList<String> out = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return out;
        }
        int lineStart = 0;
        for (int i = 0; i <= text.length(); i++) {
            if (i == text.length() || text.charAt(i) == '\n') {
                String line = text.substring(lineStart, i).trim();
                lineStart = i + 1;
                if (!line.isEmpty()) {
                    out.add(line);
                }
            }
        }
        return out;
    }

    /** "2026-11-18" -> LocalDate (today if the text is damaged). One copy for every list class. */
    static LocalDate parseDate(String s) {
        if (s == null || s.length() < 10) {
            return LocalDate.now();
        }
        try {
            int y = Integer.parseInt(s.substring(0, 4));
            int m = Integer.parseInt(s.substring(5, 7));
            int d = Integer.parseInt(s.substring(8, 10));
            return LocalDate.of(y, m, d);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    static ArrayList<String> splitLine(String line) {
        ArrayList<String> parts = new ArrayList<>();
        int start = 0;
        while (true) {
            int comma = line.indexOf(',', start);
            if (comma == -1) {
                parts.add(line.substring(start));
                break;
            }
            parts.add(line.substring(start, comma));
            start = comma + 1;
        }
        return parts;
    }

    static String encode(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '%') {
                out.append("%25");
            } else if (c == ',') {
                out.append("%2C");
            } else if (c == '\n') {
                out.append("%0A");
            } else if (c == '\r') {
                out.append("%0D");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    static String decode(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '%' && i + 3 <= s.length()) {
                String code = s.substring(i, i + 3);
                if (code.equals("%25")) {
                    out.append('%');
                    i += 3;
                    continue;
                }
                if (code.equals("%2C")) {
                    out.append(',');
                    i += 3;
                    continue;
                }
                if (code.equals("%0A")) {
                    out.append('\n');
                    i += 3;
                    continue;
                }
                if (code.equals("%0D")) {
                    out.append('\r');
                    i += 3;
                    continue;
                }
            }
            out.append(c);
            i++;
        }
        return out.toString();
    }
}