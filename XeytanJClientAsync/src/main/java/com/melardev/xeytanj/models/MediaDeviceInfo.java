package com.melardev.xeytanj.models;

import java.io.Serializable;

public class MediaDeviceInfo implements Serializable {

    protected String deviceName;
    protected String deviceDescription;
    protected int refreshRate;

    public MediaDeviceInfo(String deviceName, String deviceDescription) {
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
    }

    public MediaDeviceInfo(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }
}
