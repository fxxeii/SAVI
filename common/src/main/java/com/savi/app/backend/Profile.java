package com.savi.app.backend;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Preferences;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.savi.app.SAVI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend of ProfilePage.
 *
 * Groups come from Groups_Records.csv (same file the other pages use):
 *
 *   G,<creator>,<totalTarget>,<groupName>,<dailyTarget>,<targetDate>[,more columns...]
 *   M,<groupName>,<memberName>
 *   C,<groupName>,<memberName>,<amount>,<date>,...
 *
 * A group is shown on the profile page ONLY if the logged-in member is its creator (G row)
 * or one of its members (M rows). A newly created account is in no group, so it sees none.
 * Total saved of a group = sum of its C rows. People = number of its M rows.
 */
public class Profile {

    private static final String PREF_ACTIVE_ACCOUNT = "savi_active_account_id";
    private static final String PREF_MEMBER_PREFIX = "savi_member_name_";
    private static final String ACCOUNTS_FILE = "savi_accounts_v2.dat";
    private static final String GROUPS_CSV = "Groups_Records.csv";

    // ---- CSV column positions (change here if your file differs) ----
    private static final int G_CREATOR = 1;
    private static final int G_TOTAL = 2;
    private static final int G_NAME = 3;
    private static final int G_DAILY = 4;
    private static final int G_DATE = 5;
    private static final int M_GROUP = 1;
    private static final int M_MEMBER = 2;
    private static final int C_GROUP = 1;
    private static final int C_AMOUNT = 3;

    private static Profile instance;

    private final List<UserProfile> accounts = new ArrayList<>();
    private final List<SavingsGroup> groups = new ArrayList<>();   // groups of the ACTIVE member only
    private UserProfile activeProfile;

    public interface DataChangeListener {
        void onProfileDataChanged();
    }

    private final List<DataChangeListener> listeners = new ArrayList<>();

    private Profile() {
        loadAccounts();
    }

    public static synchronized Profile getInstance() {
        if (instance == null) {
            instance = new Profile();
        }
        return instance;
    }

    public void addChangeListener(DataChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onProfileDataChanged();
        }
    }

    // ------------------------------------------------------------------ account switching

    /**
     * Call this right after login / sign-up succeeds.
     * memberName = the name this user has in Groups_Records.csv (the name used in the M / C rows).
     * A new account is created with 0 savings; it has no groups until it creates or joins one.
     */
    public UserProfile activateAccount(String id, String memberName) {
        UserProfile p = doActivate(id, memberName);
        notifyListeners();
        return p;
    }

    /**
     * Follows the real logged-in user (SAVI.user.getCurrentName()). The login name is used both as the
     * account id and as the member name in Groups_Records.csv, so Sam's groups = rows where member is "Sam".
     * Does not notify listeners, so it is safe to call right before showing the page.
     */
    public UserProfile loginAs(String loginName) {
        if (loginName == null || loginName.trim().isEmpty()) {
            return getActiveProfile();
        }
        String name = loginName.trim();
        return doActivate("acc_" + name.toLowerCase(), name);
    }

    private UserProfile doActivate(String id, String memberName) {
        String name = (memberName == null || memberName.trim().isEmpty()) ? "User" : memberName.trim();

        UserProfile found = null;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(id)) {
                found = accounts.get(i);
                break;
            }
        }
        if (found == null) {
            String handle = "@" + name.toLowerCase().replace(" ", ".");
            found = new UserProfile(id, name, name, handle, 0.0, true, false);
            accounts.add(found);
            saveAccounts();
        }
        Preferences.set(PREF_MEMBER_PREFIX + id, name);
        setActive(found);
        return found;
    }

    private void setActive(UserProfile p) {
        activeProfile = p;
        Preferences.set(PREF_ACTIVE_ACCOUNT, p.getId());
        loadGroups();
    }

    /** The name of the logged-in user as written in the CSV. */
    public String getMemberName() {
        if (activeProfile == null) {
            return "";
        }
        return Preferences.get(PREF_MEMBER_PREFIX + activeProfile.getId(), activeProfile.getUsername());
    }

    // ------------------------------------------------------------------ queries

    public UserProfile getActiveProfile() {
        if (activeProfile == null) {
            if (accounts.isEmpty()) {
                seedDefaultData();
            } else {
                setActive(accounts.get(0));
            }
        }
        return activeProfile;
    }

    public List<UserProfile> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public List<SavingsGroup> getGroups() {
        getActiveProfile();
        return new ArrayList<>(groups);
    }

    public int getGroupCount() {
        getActiveProfile();
        return groups.size();
    }

    public double getTotalSaved() {
        UserProfile current = getActiveProfile();
        String me = getMemberName();
        double personalTotal = current != null ? current.getPersonalSaved() : 0.0;

        if (SAVI.personalList != null && me != null && !me.isEmpty()) {
            List<PersonalSavings> mine = SAVI.personalList.getForOwner(me);
            if (!mine.isEmpty()) {
                personalTotal = 0.0;
                for (int i = 0; i < mine.size(); i++) {
                    personalTotal += mine.get(i).getCurrentAmount();
                }
            }
        }

        double total = personalTotal;
        for (int i = 0; i < groups.size(); i++) {
            total += groups.get(i).getCurrentSaved();
        }
        return total;
    }

    public int getTotalPeople() {
        getActiveProfile();
        int people = 0;
        for (int i = 0; i < groups.size(); i++) {
            people += groups.get(i).getMemberCount();
        }
        return people;
    }

    /** Re-reads the CSV (other pages may have changed it). Does not notify listeners. */
    public void refreshGroups() {
        getActiveProfile();
        if (SAVI.groupList != null) {
            SAVI.groupList.load();
        }
        loadGroups();
    }

    // ------------------------------------------------------------------ mutations

    public void updateProfileInfo(String greetingName, String username, String handle) {
        UserProfile current = getActiveProfile();
        if (current == null) {
            return;
        }
        if (greetingName != null && !greetingName.trim().isEmpty()) {
            current.setGreetingName(greetingName.trim());
        }
        if (username != null && !username.trim().isEmpty()) {
            current.setUsername(username.trim());
        }
        if (handle != null && !handle.trim().isEmpty()) {
            String formatted = handle.trim();
            if (!formatted.startsWith("@")) {
                formatted = "@" + formatted;
            }
            current.setHandle(formatted);
        }
        saveAccounts();
        notifyListeners();
    }

    /** Adds a G row (creator = logged-in user) and an M row for the creator. False if the name is taken. */
    public boolean addGroup(String name, String targetDate, double dailyTarget, double totalTarget) {
        getActiveProfile();
        String me = getMemberName();
        String n = clean(name);
        if (n.isEmpty() || me.isEmpty()) {
            return false;
        }
        List<String[]> rows = readCsv();
        if (findGroupRow(rows, n) >= 0) {
            return false;
        }
        String date = clean(targetDate).isEmpty() ? "TBD" : clean(targetDate);
        rows.add(new String[]{"G", clean(me), String.valueOf(totalTarget), n, String.valueOf(dailyTarget), date});
        rows.add(new String[]{"M", n, clean(me)});
        writeCsv(rows);
        if (SAVI.groupList != null) {
            SAVI.groupList.load();
        }
        loadGroups();
        notifyListeners();
        return true;
    }

    /** Edits the G row; a rename is also applied to the M and C rows. False if not found or name taken. */
    public boolean updateGroup(String groupId, String name, String targetDate, double dailyTarget, double totalTarget) {
        getActiveProfile();
        String n = clean(name);
        if (n.isEmpty()) {
            return false;
        }
        List<String[]> rows = readCsv();
        int idx = findGroupRow(rows, groupId);
        if (idx < 0) {
            return false;
        }
        if (!same(n, groupId) && findGroupRow(rows, n) >= 0) {
            return false;
        }
        String[] g = ensureLength(rows.get(idx), G_DATE + 1);   // keeps any extra columns untouched
        g[G_TOTAL] = String.valueOf(totalTarget);
        g[G_NAME] = n;
        g[G_DAILY] = String.valueOf(dailyTarget);
        g[G_DATE] = clean(targetDate).isEmpty() ? "TBD" : clean(targetDate);
        rows.set(idx, g);

        if (!n.equals(groupId)) {
            for (int i = 0; i < rows.size(); i++) {
                String[] r = rows.get(i);
                if (("M".equals(r[0]) || "C".equals(r[0])) && r.length > 1 && same(r[1], groupId)) {
                    r[1] = n;
                }
            }
        }
        writeCsv(rows);
        if (SAVI.groupList != null) {
            SAVI.groupList.load();
        }
        loadGroups();
        notifyListeners();
        return true;
    }

    /** Removes the group's G, M and C rows. */
    public void deleteGroup(String groupId) {
        getActiveProfile();
        List<String[]> rows = readCsv();
        for (int i = rows.size() - 1; i >= 0; i--) {
            String[] r = rows.get(i);
            boolean hit = false;
            if ("G".equals(r[0]) && r.length > G_NAME) {
                hit = same(r[G_NAME], groupId);
            } else if (("M".equals(r[0]) || "C".equals(r[0])) && r.length > 1) {
                hit = same(r[1], groupId);
            }
            if (hit) {
                rows.remove(i);
            }
        }
        writeCsv(rows);
        if (SAVI.groupList != null) {
            SAVI.groupList.load();
        }
        loadGroups();
        notifyListeners();
    }

    public void logout() {
        Preferences.delete(PREF_ACTIVE_ACCOUNT);
        activeProfile = null;
        groups.clear();   // never leak one account's groups into the next login
    }

    public String buildShareSummary() {
        UserProfile current = getActiveProfile();
        return current.getUsername() + " (" + current.getHandle() + ") on SAVI\n"
                + "Groups: " + getGroupCount()
                + " | Total Saved: P" + formatAmount(getTotalSaved())
                + " | People: " + getTotalPeople();
    }

    public static String formatAmount(double amount) {
        if (amount == (long) amount) {
            return formatWithCommas((long) amount);
        }
        long whole = (long) amount;
        int tenths = (int) Math.round((amount - whole) * 10);
        if (tenths == 10) {
            whole++;
            tenths = 0;
        }
        return formatWithCommas(whole) + "." + tenths;
    }

    private static String formatWithCommas(long value) {
        String raw = String.valueOf(value);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = raw.length() - 1; i >= 0; i--) {
            sb.insert(0, raw.charAt(i));
            count++;
            if (count % 3 == 0 && i > 0 && raw.charAt(i - 1) != '-') {
                sb.insert(0, ',');
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------ CSV: groups

    private boolean matchesCurrentUser(String candidate) {
        if (candidate == null || candidate.trim().isEmpty()) {
            return false;
        }
        String c = candidate.trim();
        if (same(c, getMemberName())) {
            return true;
        }
        if (activeProfile != null && (same(c, activeProfile.getUsername()) || same(c, activeProfile.getGreetingName()))) {
            return true;
        }
        if (SAVI.user != null && SAVI.user.getCurrentName() != null && same(c, SAVI.user.getCurrentName())) {
            return true;
        }
        return false;
    }

    private void loadGroups() {
        groups.clear();
        String me = getMemberName();
        String sessionName = (SAVI.user != null && SAVI.user.getCurrentName() != null)
                ? SAVI.user.getCurrentName().trim() : "";
        if (me.isEmpty() && sessionName.isEmpty()) {
            return;
        }
        List<String[]> rows = readCsv();
        for (int i = 0; i < rows.size(); i++) {
            String[] g = rows.get(i);
            if (!"G".equals(g[0]) || g.length <= G_NAME || g[G_NAME].isEmpty()) {
                continue;
            }
            String name = g[G_NAME];
            boolean mine = matchesCurrentUser(g[G_CREATOR]);
            int members = 0;
            double saved = 0;

            for (int j = 0; j < rows.size(); j++) {
                String[] r = rows.get(j);
                if ("M".equals(r[0]) && r.length > M_MEMBER && same(r[M_GROUP], name)) {
                    members++;
                    if (matchesCurrentUser(r[M_MEMBER])) {
                        mine = true;
                    }
                } else if ("C".equals(r[0]) && r.length > C_AMOUNT && same(r[C_GROUP], name)) {
                    saved += parseDouble(r[C_AMOUNT]);
                }
            }
            if (!mine && SAVI.groupList != null) {
                GroupSavings live = SAVI.groupList.findGroup(name);
                if (live != null && (live.hasMember(me) || live.hasMember(sessionName))) {
                    mine = true;
                }
            }
            if (!mine) {
                continue;   // not this user's group
            }
            String date = g.length > G_DATE && !g[G_DATE].isEmpty() ? g[G_DATE] : "TBD";
            double daily = g.length > G_DAILY ? parseDouble(g[G_DAILY]) : 0;
            double total = g.length > G_TOTAL ? parseDouble(g[G_TOTAL]) : 0;
            // the group name is the id, because it is the key the M and C rows use
            groups.add(new SavingsGroup(name, name, date, daily, total, saved, members,
                    FontImage.MATERIAL_BEACH_ACCESS));
        }
    }

    private static int findGroupRow(List<String[]> rows, String name) {
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if ("G".equals(r[0]) && r.length > G_NAME && same(r[G_NAME], name)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean same(String a, String b) {
        return a != null && b != null && !a.trim().isEmpty() && a.trim().equalsIgnoreCase(b.trim());
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /** Commas and line breaks would break the CSV, so they are removed from typed text. */
    private static String clean(String s) {
        return s == null ? "" : s.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private static String[] ensureLength(String[] row, int len) {
        if (row.length >= len) {
            return row;
        }
        String[] bigger = new String[len];
        for (int i = 0; i < len; i++) {
            bigger[i] = i < row.length ? row[i] : "";
        }
        return bigger;
    }

    // ------------------------------------------------------------------ CSV: file access
    // In the simulator the app home is C:\Users\<you>\.cn1\, which is where Groups_Records.csv lives.

    private static String csvPath() {
        return FileSystemStorage.getInstance().getAppHomePath() + GROUPS_CSV;
    }

    /** Same lookup order as PeopleBackend: the app's file storage first, then the bundled copy. */
    private static String readCsvText() {
        try {
            FileSystemStorage fs = FileSystemStorage.getInstance();
            String path = csvPath();
            if (fs.exists(path)) {
                InputStream in = fs.openInputStream(path);
                String text = Util.readToString(in, "UTF-8");
                if (text != null) {
                    return text;
                }
            }
        } catch (Exception e) {
            // fall through to the bundled file
        }
        try {
            InputStream in = Display.getInstance().getResourceAsStream(Profile.class, "/" + GROUPS_CSV);
            if (in != null) {
                String text = Util.readToString(in, "UTF-8");
                if (text != null) {
                    return text;
                }
            }
        } catch (Exception e) {
            // no file found
        }
        return "";
    }

    private static List<String[]> readCsv() {
        List<String[]> rows = new ArrayList<>();
        String text = readCsvText();
        if (text.length() > 0 && text.charAt(0) == '\uFEFF') {
            text = text.substring(1);   // byte order mark
        }
        String[] lines = Util.split(text, "\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].replace("\r", "");
            if (line.trim().isEmpty()) {
                continue;
            }
            rows.add(splitLine(line));
        }
        return rows;
    }

    private static void writeCsv(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            for (int j = 0; j < r.length; j++) {
                if (j > 0) {
                    sb.append(',');
                }
                sb.append(r[j]);
            }
            sb.append('\n');
        }
        String content = sb.toString();
        try (OutputStream os = FileSystemStorage.getInstance().openOutputStream(csvPath())) {
            os.write(content.getBytes("UTF-8"));
        } catch (IOException ignored) {
        }
        try (OutputStream os = Storage.getInstance().createOutputStream(GROUPS_CSV)) {
            os.write(content.getBytes("UTF-8"));
        } catch (IOException ignored) {
        }
    }

    /** Splits on commas and keeps empty fields (e.g. the one after "2026-11-19,"). */
    private static String[] splitLine(String line) {
        List<String> out = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ',') {
                out.add(line.substring(start, i).trim());
                start = i + 1;
            }
        }
        out.add(line.substring(start).trim());
        return out.toArray(new String[out.size()]);
    }

    // ------------------------------------------------------------------ accounts (profile info only)

    private void seedDefaultData() {
        accounts.clear();
        groups.clear();
        UserProfile primary = new UserProfile("acc_default", "User", "User2026-Trial", "@user.2026",
                0.0, true, true);
        accounts.add(primary);
        saveAccounts();
        setActive(primary);   // "User2026-Trial" is in no CSV group, so it sees none
    }

    private void loadAccounts() {
        Storage storage = Storage.getInstance();
        if (!storage.exists(ACCOUNTS_FILE)) {
            seedDefaultData();
            return;
        }
        accounts.clear();
        try (DataInputStream in = new DataInputStream(storage.createInputStream(ACCOUNTS_FILE))) {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                String id = in.readUTF();
                String greetingName = in.readUTF();
                String username = in.readUTF();
                String handle = in.readUTF();
                double personalSaved = in.readDouble();
                boolean notif = in.readBoolean();
                boolean lock = in.readBoolean();
                accounts.add(new UserProfile(id, greetingName, username, handle, personalSaved, notif, lock));
            }
        } catch (IOException e) {
            accounts.clear();
        }
        if (accounts.isEmpty()) {
            seedDefaultData();
            return;
        }
        String savedActiveId = Preferences.get(PREF_ACTIVE_ACCOUNT, "");
        UserProfile toActivate = accounts.get(0);
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(savedActiveId)) {
                toActivate = accounts.get(i);
                break;
            }
        }
        setActive(toActivate);
    }

    private void saveAccounts() {
        try (DataOutputStream out = new DataOutputStream(Storage.getInstance().createOutputStream(ACCOUNTS_FILE))) {
            out.writeInt(accounts.size());
            for (int i = 0; i < accounts.size(); i++) {
                UserProfile p = accounts.get(i);
                out.writeUTF(p.getId());
                out.writeUTF(p.getGreetingName());
                out.writeUTF(p.getUsername());
                out.writeUTF(p.getHandle());
                out.writeDouble(p.getPersonalSaved());
                out.writeBoolean(p.isNotificationsEnabled());
                out.writeBoolean(p.isAppLockEnabled());
            }
        } catch (IOException ignored) {
        }
    }
}