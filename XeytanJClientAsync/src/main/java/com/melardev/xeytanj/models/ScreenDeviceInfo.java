package com.melardev.xeytanj.models;

public class ScreenDeviceInfo extends MediaDeviceInfo {

    private double x;
    private double y;
    private double width;
    private double height;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    public ScreenDeviceInfo(String deviceName, String deviceDescription) {
        super(deviceName, deviceDescription);
    }

    public ScreenDeviceInfo(String deviceName) {
        super(deviceName);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }


    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }


    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }


    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

}
