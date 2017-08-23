package com.glemontree.calendarview;

/**
 * Created by Administrator on 2017/8/22.
 */

public class CustomDate {
    private int year;
    private int month;
    private int day;
    private int week;

    public CustomDate(int year, int month, int day) {
        if (month > 12) {
            month = 1;
            year++;
        } else if (month < 1) {
            month = 12;
            year--;
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CustomDate() {
        this.year = DateUtil.getYear();
        this.month = DateUtil.getMonth();
        this.day = DateUtil.getCurrentMonthDay();
    }

    public static CustomDate modifyDayForObject(CustomDate date, int day) {
        CustomDate modifyDate = new CustomDate(date.getYear(), date.getMonth(), day);
        return modifyDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + day;
    }
}
