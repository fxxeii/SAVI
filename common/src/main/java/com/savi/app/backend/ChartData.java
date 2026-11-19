package com.savi.app.backend;

/** What the monthly chart card needs. Implemented by Summary (group) and PersonalSummary (personal). */
public interface ChartData {
    String getChartTitle();
    String getChartSubtitle();
    int getChartMode();
    void setChartMode(int mode);
    boolean hasShares();
    float[] getChartValues();
    String[] getChartLabels();
    String getChartTip();
    boolean isTargetAchieved();
    String getFooterText();
    String getEmptyMessage();
}
