package com.savi.app.backend;

import com.codename1.ui.FontImage;

public class SavingsGroup {
    private String id;
    private String name;
    private String targetDate;
    private double dailyTarget;
    private double totalTarget;
    private double currentSaved;
    private int memberCount;
    private char icon;

    public SavingsGroup(String id, String name, String targetDate, double dailyTarget,
                        double totalTarget, double currentSaved, int memberCount, char icon) {
        this.id = id;
        this.name = name;
        this.targetDate = targetDate;
        this.dailyTarget = dailyTarget;
        this.totalTarget = totalTarget;
        this.currentSaved = currentSaved;
        this.memberCount = memberCount;
        this.icon = icon == 0 ? FontImage.MATERIAL_BEACH_ACCESS : icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public double getDailyTarget() {
        return dailyTarget;
    }

    public void setDailyTarget(double dailyTarget) {
        this.dailyTarget = dailyTarget;
    }

    public double getTotalTarget() {
        return totalTarget;
    }

    public void setTotalTarget(double totalTarget) {
        this.totalTarget = totalTarget;
    }

    public double getCurrentSaved() {
        return currentSaved;
    }

    public void setCurrentSaved(double currentSaved) {
        this.currentSaved = currentSaved;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public char getIcon() {
        return icon;
    }

    public void setIcon(char icon) {
        this.icon = icon;
    }
}