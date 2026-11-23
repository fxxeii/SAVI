package com.savi.app.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// GROUP LIST (manages all GroupSavings + saves to Groups_Records.txt via Codename One Storage)
// File format (3 record types):
//   G,creator,targetAmount,groupName,expectedPerMember,createdDate,weeklyTarget,savingsName,targetDate
//   M,groupName,memberName
//   C,groupName,memberName,amount,date,note[,timestamp]
public class GroupList implements SavingsStore {

    private final ArrayList<GroupSavings> groups = new ArrayList<>();
    private final String fileRec = "Groups_Records.txt";

    // CREATE
    public String addGroup(String creator, double targetAmount, String groupName,
                           double expectedPerMember, LocalDate createdDate,
                           ArrayList<String> initialMembers) {
        return addGroup(creator, targetAmount, groupName, expectedPerMember, createdDate, initialMembers, 0);
    }

    public String addGroup(String creator, double targetAmount, String groupName,
                           double expectedPerMember, LocalDate createdDate,
                           ArrayList<String> initialMembers, double weeklyTarget) {
        return addGroup(creator, targetAmount, groupName, expectedPerMember, createdDate,
                initialMembers, weeklyTarget, groupName, "");
    }

    public String addGroup(String creator, double targetAmount, String groupName,
                           double expectedPerMember, LocalDate createdDate,
                           ArrayList<String> initialMembers, double weeklyTarget,
                           String savingsName, String targetDate) {
        if (creator == null || creator.trim().isEmpty()) {
            return "Creator name is required.";
        }
        if (groupName == null || groupName.trim().isEmpty()) {
            return "Group name is required.";
        }
        if (targetAmount <= 0) {
            return "Target amount must be greater than 0.";
        }
        if (expectedPerMember < 0) {
            return "Expected per member cannot be negative.";
        }
        if (findByName(groupName) != null) {
            return "A group with that name already exists.";
        }

        GroupSavings g = new GroupSavings(creator.trim(), targetAmount, groupName.trim(),
                expectedPerMember, createdDate, weeklyTarget, savingsName, targetDate);

        if (initialMembers != null) {
            for (String m : initialMembers) {
                g.addMember(m);
            }
        }

        groups.add(g);
        if (!saveAll()) {
            return "Could not save group to file.";
        }
        return "success";
    }

    // READ
    public List<GroupSavings> getGroups() {
        return new ArrayList<>(groups);
    }

    public List<GroupSavings> getForUser(String username) {
        ArrayList<GroupSavings> out = new ArrayList<>();
        for (GroupSavings g : groups) {
            if (g.isOwnedBy(username)) {
                out.add(g);
            }
        }
        return out;
    }

    public GroupSavings findByName(String groupName) {
        if (groupName == null) {
            return null;
        }
        String target = groupName.trim();
        for (GroupSavings g : groups) {
            if (g.getGroupName().equalsIgnoreCase(target)) {
                return g;
            }
        }
        return null;
    }

    // UPDATE
    public String addMemberToGroup(String groupName, String memberName) {
        GroupSavings g = findByName(groupName);
        if (g == null) {
            return "Group not found.";
        }
        if (memberName == null || memberName.trim().isEmpty()) {
            return "Member name is required.";
        }
        if (g.hasMember(memberName)) {
            return memberName.trim() + " is already in this group.";
        }
        g.addMember(memberName.trim());
        saveAll();
        return "success";
    }

    public String removeMemberFromGroup(String groupName, String memberName) {
        GroupSavings g = findByName(groupName);
        if (g == null) {
            return "Group not found.";
        }
        if (memberName != null && g.getCreator().equalsIgnoreCase(memberName.trim())) {
            return "Cannot remove the group creator.";
        }
        if (!g.removeMember(memberName)) {
            return "Member not found.";
        }
        saveAll();
        return "success";
    }

    /** A negative amount is a transfer out; zero is rejected. Returns true when it was saved. */
    @Override
    public boolean addContribution(String groupName, String memberName, double amount, LocalDate date, String note) {
        GroupSavings g = findByName(groupName);
        if (g == null || memberName == null || memberName.trim().isEmpty() || amount == 0) {
            return false;
        }
        try {
            if (!g.hasMember(memberName)) {
                g.addMember(memberName.trim());
            }
            g.addContribution(memberName.trim(), amount, date, note);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return saveAll();
    }

    /** Edits a group from the Profile page. dailyTarget = expected per member, totalTarget = target amount. */
    public boolean updateGroup(String groupName, String newName, String targetDate,
                               double dailyTarget, double totalTarget) {
        GroupSavings g = findByName(groupName);
        if (g == null || newName == null || newName.trim().isEmpty() || totalTarget <= 0 || dailyTarget < 0) {
            return false;
        }
        GroupSavings other = findByName(newName);
        if (other != null && other != g) {
            return false;                       // the new name belongs to another group
        }
        g.setGroupName(newName);
        g.setTargetDate(targetDate);
        g.setExpectedPerMember(dailyTarget);
        g.setTargetAmount(totalTarget);
        return saveAll();
    }

    /** A user changed their username: every group, member and contribution follows. */
    public boolean renameMember(String oldName, String newName) {
        for (GroupSavings g : groups) {
            g.renameMember(oldName, newName);
        }
        return saveAll();
    }

    @Override
    public boolean saveChanges() {
        return saveAll();
    }

    // DELETE
    public boolean deleteGroup(String groupName) {
        GroupSavings g = findByName(groupName);
        if (g == null) {
            return false;
        }
        groups.remove(g);
        saveAll();
        return true;
    }

    public void clearLocalFile() {
        RecordStorage.deleteLocal(fileRec);
        groups.clear();
    }

    // PERSISTENCE (Storage)
    private boolean saveAll() {
        StringBuilder sb = new StringBuilder();
        for (GroupSavings g : groups) {
            sb.append("G,").append(RecordStorage.encode(g.getCreator())).append(",")
                    .append(g.getTargetAmount()).append(",")
                    .append(RecordStorage.encode(g.getGroupName())).append(",")
                    .append(g.getExpectedPerMember()).append(",")
                    .append(g.getCreatedDate()).append(",")
                    .append(g.getWeeklyTarget()).append(",")
                    .append(RecordStorage.encode(g.getSavingsName())).append(",")
                    .append(RecordStorage.encode(g.getTargetDate())).append("\n");

            for (String m : g.getMembers()) {
                sb.append("M,").append(RecordStorage.encode(g.getGroupName())).append(",")
                        .append(RecordStorage.encode(m)).append("\n");
            }

            for (Contribution c : g.getContributions()) {
                sb.append("C,").append(RecordStorage.encode(g.getGroupName())).append(",")
                        .append(RecordStorage.encode(c.getMember())).append(",")
                        .append(c.getAmount()).append(",")
                        .append(c.getDate().toString()).append(",")
                        .append(RecordStorage.encode(c.getNote())).append(",")
                        .append(c.getTimestamp()).append("\n");
            }
        }
        return RecordStorage.writeLocal(fileRec, sb.toString());
    }

    public void load() {
        groups.clear();
        loadText(RecordStorage.loadOrSeed(GroupList.class, fileRec));
    }

    private void loadText(String text) {
        for (String line : RecordStorage.lines(text)) {
            parseLine(line);
        }
    }

    private void parseLine(String line) {
        ArrayList<String> parts = RecordStorage.splitLine(line);
        if (parts.isEmpty()) {
            return;
        }
        String type = parts.get(0);

        if (type.equals("G") && parts.size() >= 6) {
            try {
                String creator = RecordStorage.decode(parts.get(1));
                double target = Double.parseDouble(parts.get(2));
                String name = RecordStorage.decode(parts.get(3));
                double expected = Double.parseDouble(parts.get(4));
                LocalDate created = RecordStorage.parseDate(parts.get(5));
                double weeklyTarget = parts.size() >= 7 ? Double.parseDouble(parts.get(6)) : 0;
                String savingsName = parts.size() >= 8 ? RecordStorage.decode(parts.get(7)) : name;
                String targetDate = parts.size() >= 9 ? RecordStorage.decode(parts.get(8)) : "";
                if (findByName(name) == null) {
                    groups.add(new GroupSavings(creator, target, name, expected, created,
                            weeklyTarget, savingsName, targetDate));
                }
            } catch (Exception ignored) {
            }
        } else if (type.equals("M") && parts.size() >= 3) {
            GroupSavings g = findByName(RecordStorage.decode(parts.get(1)));
            if (g != null) {
                g.addMember(RecordStorage.decode(parts.get(2)));
            }
        } else if (type.equals("C") && parts.size() >= 6) {
            GroupSavings g = findByName(RecordStorage.decode(parts.get(1)));
            if (g != null) {
                try {
                    String member = RecordStorage.decode(parts.get(2));
                    double amount = Double.parseDouble(parts.get(3));
                    LocalDate date = RecordStorage.parseDate(parts.get(4));
                    String note = RecordStorage.decode(parts.get(5));
                    long timestamp = parts.size() >= 7 ? Long.parseLong(parts.get(6).trim()) : 0L;
                    if (!g.hasMember(member)) {
                        g.addMember(member);
                    }
                    g.addContribution(member, amount, date, note, timestamp);
                } catch (Exception ignored) {
                }
            }
        }
    }
}