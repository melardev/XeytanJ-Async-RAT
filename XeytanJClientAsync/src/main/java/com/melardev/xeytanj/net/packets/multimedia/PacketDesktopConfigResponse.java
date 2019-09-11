package com.melardev.xeytanj.net.packets.multimedia;

import com.melardev.xeytanj.models.ScreenDeviceInfo;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

import java.util.List;

public class PacketDesktopConfigResponse extends Packet {
    private List<ScreenDeviceInfo> screenDeviceInfoList;

    public PacketDesktopConfigResponse() {
        super(PacketType.DESKTOP_CONFIG);
    }

    public PacketDesktopConfigResponse(List<ScreenDeviceInfo> screenDeviceInfoList) {
        super(PacketType.DESKTOP_CONFIG);
        this.screenDeviceInfoList = screenDeviceInfoList;
    }

    public List<ScreenDeviceInfo> getScreenDeviceInfoList() {
        return screenDeviceInfoList;
    }

    public void setScreenDeviceInfoList(List<ScreenDeviceInfo> screenDeviceInfoList) {
        this.screenDeviceInfoList = screenDeviceInfoList;
    }
}
