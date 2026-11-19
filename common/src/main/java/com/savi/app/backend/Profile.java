package com.savi.app.backend;

import com.codename1.io.Preferences;
import com.codename1.io.Storage;
import com.codename1.ui.FontImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile {

    private static final String PREF_ACTIVE_ACCOUNT = "savi_active_account_id";
    private static final String STORAGE_FILE = "savi_profile_data_v1.dat";

    private static Profile instance;

    private final List<UserProfile> accounts = new ArrayList<>();
    private final List<SavingsGroup> groups = new ArrayList<>();
    private UserProfile activeProfile;

    public interface DataChangeListener {
        void onProfileDataChanged();
    }

    private final List<DataChangeListener> listeners = new ArrayList<>();

    private Profile() {
        loadFromStorage();
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

    // --- Queries & Computed Stats ---

    public UserProfile getActiveProfile() {
        if (activeProfile == null) {
            if (accounts.isEmpty()) {
                seedDefaultData();
            } else {
                activeProfile = accounts.get(0);
            }
        }
        return activeProfile;
    }

    public List<UserProfile> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public List<SavingsGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public double getTotalSaved() {
        UserProfile current = getActiveProfile();
        double total = current != null ? current.getPersonalSaved() : 0.0;
        for (int i = 0; i < groups.size(); i++) {
            total += groups.get(i).getCurrentSaved();
        }
        return total;
    }

    public int getTotalPeople() {
        int people = 0;
        for (int i = 0; i < groups.size(); i++) {
            people += groups.get(i).getMemberCount();
        }
        return people;
    }

    // --- Mutations ---

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
        saveToStorage();
        notifyListeners();
    }

    public void addGroup(String name, String targetDate, double dailyTarget, double totalTarget,
                         double initialSaved, int members, char icon) {
        String id = "grp_" + System.currentTimeMillis();
        groups.add(new SavingsGroup(id, name, targetDate, dailyTarget, totalTarget, initialSaved, members, icon));
        saveToStorage();
        notifyListeners();
    }

    public void updateGroup(String groupId, String name, String targetDate, double dailyTarget,
                            double totalTarget, double currentSaved, int members) {
        for (int i = 0; i < groups.size(); i++) {
            SavingsGroup g = groups.get(i);
            if (g.getId().equals(groupId)) {
                g.setName(name);
                g.setTargetDate(targetDate);
                g.setDailyTarget(dailyTarget);
                g.setTotalTarget(totalTarget);
                g.setCurrentSaved(currentSaved);
                g.setMemberCount(members);
                break;
            }
        }
        saveToStorage();
        notifyListeners();
    }

    public void deleteGroup(String groupId) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getId().equals(groupId)) {
                groups.remove(i);
                break;
            }
        }
        saveToStorage();
        notifyListeners();
    }

    public void logout() {
        Preferences.delete(PREF_ACTIVE_ACCOUNT);
        activeProfile = null;
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

    // --- Persistence ---

    private void seedDefaultData() {
        accounts.clear();
        groups.clear();

        UserProfile primary = new UserProfile(
                "acc_default",
                "User",
                "User2026-Trial",
                "@user.2026",
                3500.0,
                true,
                true
        );
        accounts.add(primary);
        activeProfile = primary;

        groups.add(new SavingsGroup(
                "grp_1",
                "December Vacation",
                "Dec 10, 2026",
                50.0,
                500.0,
                456.5,
                8,
                FontImage.MATERIAL_BEACH_ACCESS
        ));
        groups.add(new SavingsGroup(
                "grp_2",
                "Emergency Fund",
                "Mar 30, 2027",
                100.0,
                10000.0,
                300.0,
                7,
                FontImage.MATERIAL_BEACH_ACCESS
        ));

        Preferences.set(PREF_ACTIVE_ACCOUNT, activeProfile.getId());
        saveToStorage();
    }

    private void loadFromStorage() {
        Storage storage = Storage.getInstance();
        if (!storage.exists(STORAGE_FILE)) {
            seedDefaultData();
            return;
        }

        try (DataInputStream in = new DataInputStream(storage.createInputStream(STORAGE_FILE))) {
            accounts.clear();
            groups.clear();

            int accCount = in.readInt();
            for (int i = 0; i < accCount; i++) {
                String id = in.readUTF();
                String greetingName = in.readUTF();
                String username = in.readUTF();
                String handle = in.readUTF();
                double personalSaved = in.readDouble();
                boolean notif = in.readBoolean();
                boolean lock = in.readBoolean();
                accounts.add(new UserProfile(id, greetingName, username, handle, personalSaved, notif, lock));
            }

            int grpCount = in.readInt();
            for (int i = 0; i < grpCount; i++) {
                String id = in.readUTF();
                String name = in.readUTF();
                String targetDate = in.readUTF();
                double daily = in.readDouble();
                double total = in.readDouble();
                double saved = in.readDouble();
                int members = in.readInt();
                char icon = in.readChar();
                groups.add(new SavingsGroup(id, name, targetDate, daily, total, saved, members, icon));
            }

            String savedActiveId = Preferences.get(PREF_ACTIVE_ACCOUNT, "");
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getId().equals(savedActiveId)) {
                    activeProfile = accounts.get(i);
                    break;
                }
            }
            if (activeProfile == null && !accounts.isEmpty()) {
                activeProfile = accounts.get(0);
            }
            if (activeProfile == null) {
                seedDefaultData();
            }
        } catch (IOException e) {
            seedDefaultData();
        }
    }

    private void saveToStorage() {
        try (DataOutputStream out = new DataOutputStream(Storage.getInstance().createOutputStream(STORAGE_FILE))) {
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

            out.writeInt(groups.size());
            for (int i = 0; i < groups.size(); i++) {
                SavingsGroup g = groups.get(i);
                out.writeUTF(g.getId());
                out.writeUTF(g.getName());
                out.writeUTF(g.getTargetDate());
                out.writeDouble(g.getDailyTarget());
                out.writeDouble(g.getTotalTarget());
                out.writeDouble(g.getCurrentSaved());
                out.writeInt(g.getMemberCount());
                out.writeChar(g.getIcon());
            }
        } catch (IOException ignored) {
        }
    }
}