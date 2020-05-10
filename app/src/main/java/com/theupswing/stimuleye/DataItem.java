package com.theupswing.stimuleye;

public class DataItem {
    private int time;
    private double diameter;
    private int flash;

    public DataItem(int time, double diameter, int flash) {
        this.time = time;
        this.diameter = diameter;
        this.flash = flash;
    }

    public String getFlash() {
        return flash==0? "Off":"On";
    }

    public double getDiameter() {
        return diameter;
    }

    public int getTime() {
        return time;
    }
}
