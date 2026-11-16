package com.savi.app.backend;

import com.codename1.ui.List;

import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.Date;

public class Home {
    private User currentUser;
    private double weeklySaved;
    private double weeklyTarget;
    private double weeklyProgress;
    private double saved;
    private  double target;


    public void getCurrentUser(User u){
        currentUser = u;
    }

    //this week and amount saved
    public double getWeeklySaved(){
        return weeklySaved;
    }

    public double getWeeklyTarget(){
        return weeklyTarget;
    }

    public double getWeeklyProgress(){
        return weeklyProgress;
    }

    //Group Overview Card
    public GroupSavings getFeaturedGroup(User u){
        return;
    }

    public String getName(){
        return "";
    }

    public double getTotalSaved(){
        return 0.0;
    }

    public double getRemainningBalance(){
        return 0.0;
    }

    public List<GroupSavings> getGroups(User u){

    }

    //Recent Contributions
    public List<Contribution> getRecentContributions(User u, int limit){

    }

    public Date getDate(){

    }

    public String getContributorName(){

    }

    public double getAmount(){
        return 0.0;
    }

    public String getNote(){
        return "";
    }

    public String getGroupName(){
        return "";
    }

    //Helper
    public  String pesoFormat(double amount){
        String formatted = String.format("%.2f", amount);
        return "P" + formatted;
    }
}
