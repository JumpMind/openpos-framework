package org.jumpmind.pos.core.ui.data;

public class TimeUnitLabels {

    public TimeUnitLabels(String hourLabel, String hourLabelPlural, String minuteLabel, String minuteLabelPlural, String secondLabel, String secondLabelPlural, String nowLabel) {
        this.hourLabel = hourLabel;
        this.hourLabelPlural = hourLabelPlural;
        this.minuteLabel = minuteLabel;
        this.minuteLabelPlural = minuteLabelPlural;
        this.secondLabel = secondLabel;
        this.secondLabelPlural = secondLabelPlural;
        this.nowLabel = nowLabel;
    }

    private String hourLabel;
    private String hourLabelPlural;
    private String minuteLabel;
    private String minuteLabelPlural;
    private String secondLabel;
    private String secondLabelPlural;
    private String nowLabel;

    public String getHourLabel() {
        return hourLabel;
    }

    public void setHourLabel(String hourLabel) {
        this.hourLabel = hourLabel;
    }

    public String getHourLabelPlural() {
        return hourLabelPlural;
    }

    public void setHourLabelPlural(String hourLabelPlural) {
        this.hourLabelPlural = hourLabelPlural;
    }

    public String getMinuteLabel() {
        return minuteLabel;
    }

    public void setMinuteLabel(String minuteLabel) {
        this.minuteLabel = minuteLabel;
    }

    public String getMinuteLabelPlural() {
        return minuteLabelPlural;
    }

    public void setMinuteLabelPlural(String minuteLabelPlural) {
        this.minuteLabelPlural = minuteLabelPlural;
    }

    public String getSecondLabel() {
        return secondLabel;
    }

    public void setSecondLabel(String secondLabel) {
        this.secondLabel = secondLabel;
    }

    public String getSecondLabelPlural() {
        return secondLabelPlural;
    }

    public void setSecondLabelPlural(String secondLabelPlural) {
        this.secondLabelPlural = secondLabelPlural;
    }

    public String getNowLabel() {
        return nowLabel;
    }

    public void setNowLabel(String nowLabel) {
        this.nowLabel = nowLabel;
    }
}
