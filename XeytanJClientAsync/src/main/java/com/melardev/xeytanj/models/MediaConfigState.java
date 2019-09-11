package com.melardev.xeytanj.models;

import java.io.Serializable;

public class MediaConfigState implements Serializable {
    private String[] selectedDeviceNames;
    private int delay;
    private int interval;
    private int scaleX;
    private int scaleY;

    public MediaConfigState(String[] selectedDeviceNames, int interval) {
        this(selectedDeviceNames, interval, 1, 1);
    }


    public MediaConfigState(String[] selectedDeviceNames, int interval, int scaleFactorX, int scaleFactorY) {
        this.interval = interval;
        this.selectedDeviceNames = selectedDeviceNames;
        scaleX = scaleFactorX;
        scaleY = scaleFactorY;
    }

    public MediaConfigState(String[] deviceNames) {
        this.selectedDeviceNames = deviceNames;
    }

    public String[] getSelectedDeviceNames() {
        return selectedDeviceNames;
    }

    public void setSelectedDeviceNames(String[] selectedDeviceNames) {
        this.selectedDeviceNames = selectedDeviceNames;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getScaleX() {
        return scaleX;
    }

    public void setScaleX(int scaleX) {
        this.scaleX = scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public void setScaleY(int scaleY) {
        this.scaleY = scaleY;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
