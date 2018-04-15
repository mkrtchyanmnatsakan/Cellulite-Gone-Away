package com.wildcard.cellulite.model;

/**
 * Created by mno on 4/15/18.
 */

public class DateForStatistic {

    private String date;
    private float value;

    public DateForStatistic(String date, float value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
