package com.savi.app.backend;

import com.codename1.io.Preferences;
import com.savi.app.SAVI;

import java.util.ArrayList;
import java.util.List;

/**
 * Profile service. It stores profile presentation preferences, but it does NOT
 * own a second copy of group or savings records. GroupList and PersonalList
 * remain the single sources of truth for those records.
 */
public class Profile {
    private static final String PREF_ACTIVE_ACCOUNT = "savi_active_account_id";
    private static final String PREF_MEMBER_PREFIX = "savi_member_name_";
    private static final String PREF_GREETING_PREFIX = "savi_greeting_";
    private static final String PREF_HANDLE_PREFIX = "savi_handle_";

    private static Profile instance;
    private final List<UserProfile> accounts = new ArrayList<>();
    private UserProfile activeProfile;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public interface DataChangeListener {
        void onProfileDataChanged();
    }

    private Profile() {
        loadAccounts();
    }

    public static synchronized Profile getInstance() {
        if (instance == null) instance = new Profile();
        return instance;
    }

    public void addChangeListener(DataChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) listeners.add(listener);
    }

    public void removeChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (DataChangeListener listener : new ArrayList<>(listeners)) {
            listener.onProfileDataChanged();
        }
    }

    public UserProfile activateAccount(String id, String memberName) {
        UserProfile profile = doActivate(id, memberName);
        notifyListeners();
        return profile;
    }

    public UserProfile loginAs(String loginName) {
        if (loginName == null || loginName.trim().isEmpty()) return getActiveProfile();
        String name = loginName.trim();
        return doActivate("acc_" + name.toLowerCase(), name);
    }

    private UserProfile doActivate(String id, String memberName) {
        String name = memberName == null || memberName.trim().isEmpty() ? "User" : memberName.trim();
        UserProfile found = null;
        for (UserProfile account : accounts) {
            if (account.getId().equals(id) || account.getUsername().equalsIgnoreCase(name)) {
                found = account;
                break;
            }
        }
        if (found == null) {
            String greeting = Preferences.get(PREF_GREETING_PREFIX + id, name);
            String handle = Preferences.get(PREF_HANDLE_PREFIX + id,
                    "@" + name.toLowerCase().replace(" ", "."));
            found = new UserProfile(id, greeting, name, handle, 0.0, true, false);
            accounts.add(found);
        }
        found.setUsername(name);
        Preferences.set(PREF_MEMBER_PREFIX + found.getId(), name);
        setActive(found);
        return found;
    }

    private void setActive(UserProfile profile) {
        activeProfile = profile;
        Preferences.set(PREF_ACTIVE_ACCOUNT, profile.getId());
    }

    public String getMemberName() {
        if (activeProfile == null) return "";
        return Preferences.get(PREF_MEMBER_PREFIX + activeProfile.getId(), activeProfile.getUsername());
    }

    public UserProfile getActiveProfile() {
        if (activeProfile == null) {
            loadAccounts();
            if (activeProfile == null) seedDefaultAccount();
        }
        return activeProfile;
    }

    public List<UserProfile> getAccounts() {
        return new ArrayList<>(accounts);
    }

    /** Groups created by OR joined by the currently logged-in user. */
    public List<GroupSavings> getGroups() {
        getActiveProfile();
        String member = getMemberName();
        if (SAVI.user != null && SAVI.user.getCurrentName() != null) {
            member = SAVI.user.getCurrentName();
        }
        if (SAVI.groupList == null) return new ArrayList<>();
        return SAVI.groupList.getForUser(member);
    }

    public int getGroupCount() {
        return getGroups().size();
    }

    public double getTotalSaved() {
        getActiveProfile();
        String member = getMemberName();
        if (SAVI.user != null && SAVI.user.getCurrentName() != null) member = SAVI.user.getCurrentName();
        double total = 0;
        if (SAVI.personalList != null) {
            for (PersonalSavings savings : SAVI.personalList.getForUser(member)) {
                total += savings.getCurrentAmount();
            }
        }
        for (GroupSavings group : getGroups()) total += group.getCurrentAmount();
        return total;
    }

    public int getTotalPeople() {
        int total = 0;
        for (GroupSavings group : getGroups()) total += group.getMembers().size();
        return total;
    }

    public void refreshGroups() {
        if (SAVI.groupList != null) SAVI.groupList.load();
        if (SAVI.personalList != null) SAVI.personalList.load();
        notifyListeners();
    }

    /** Returns "success" or the message to show (e.g. the new username is already taken). */
    public String updateProfileInfo(String greetingName, String username, String handle) {
        UserProfile current = getActiveProfile();
        if (current == null) return "No active account.";

        String oldUsername = current.getUsername();
        if (greetingName != null && !greetingName.trim().isEmpty()) {
            current.setGreetingName(greetingName.trim());
            Preferences.set(PREF_GREETING_PREFIX + current.getId(), current.getGreetingName());
        }
        if (username != null && !username.trim().isEmpty()) {
            String newUsername = username.trim();
            if (!newUsername.equalsIgnoreCase(oldUsername)) {
                // User owns the account list, so it checks the name is valid and not taken
                String result = SAVI.user.rename(oldUsername, newUsername);
                if (!result.equals("success")) {
                    return result;
                }
                updateUsernameInGroupMemberships(oldUsername, newUsername);
                current.setUsername(newUsername);
                Preferences.set(PREF_MEMBER_PREFIX + current.getId(), newUsername);
                current.setId("acc_" + newUsername.toLowerCase());
            }
        }
        if (handle != null && !handle.trim().isEmpty()) {
            String formatted = handle.trim();
            if (!formatted.startsWith("@")) formatted = "@" + formatted;
            current.setHandle(formatted);
            Preferences.set(PREF_HANDLE_PREFIX + current.getId(), formatted);
        }
        Preferences.set(PREF_ACTIVE_ACCOUNT, current.getId());
        notifyListeners();
        return "success";
    }

    private void updateUsernameInGroupMemberships(String oldName, String newName) {
        if (SAVI.groupList == null) return;
        // Each list renames its own records, so Profile never edits a data file directly.
        SAVI.groupList.renameMember(oldName, newName);
        if (SAVI.personalList != null) SAVI.personalList.renameOwner(oldName, newName);
    }

    public boolean updateGroup(String groupId, String name, String targetDate,
                               double dailyTarget, double totalTarget) {
        if (SAVI.groupList == null) return false;
        boolean updated = SAVI.groupList.updateGroup(groupId, name, targetDate, dailyTarget, totalTarget);
        if (updated) notifyListeners();
        return updated;
    }

    public void deleteGroup(String groupId) {
        if (SAVI.groupList != null && SAVI.groupList.deleteGroup(groupId)) notifyListeners();
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

    /** Amount without the peso sign ("1,250" / "1,250.50"): same formatter as the rest of the app. */
    public static String formatAmount(double amount) {
        return Home.pesoShort(amount).replace("P", "");
    }

    private void seedDefaultAccount() {
        accounts.clear();
        UserProfile profile = new UserProfile("acc_default", "User", "User", "@user", 0.0, true, true);
        accounts.add(profile);
        setActive(profile);
    }

    private void loadAccounts() {
        accounts.clear();
        if (SAVI.user != null) {
            if (SAVI.user.getUsernames().isEmpty()) SAVI.user.load();
            for (String username : SAVI.user.getUsernames()) {
                if (username == null || username.trim().isEmpty()) continue;
                String name = username.trim();
                String id = "acc_" + name.toLowerCase();
                String greeting = Preferences.get(PREF_GREETING_PREFIX + id, name);
                String handle = Preferences.get(PREF_HANDLE_PREFIX + id,
                        "@" + name.toLowerCase().replace(" ", "."));
                accounts.add(new UserProfile(id, greeting, name, handle, 0.0, true, false));
            }
        }
        if (accounts.isEmpty()) {
            seedDefaultAccount();
            return;
        }
        String session = SAVI.user == null ? "" : SAVI.user.getCurrentName();
        String savedId = Preferences.get(PREF_ACTIVE_ACCOUNT, "");
        UserProfile chosen = accounts.get(0);
        for (UserProfile account : accounts) {
            if (session != null && !session.isEmpty() && account.getUsername().equalsIgnoreCase(session)) {
                chosen = account;
                break;
            }
            if (account.getId().equals(savedId)) chosen = account;
        }
        setActive(chosen);
    }

}
