package com.melardev.xeytanj.net.packets.multimedia;

import com.melardev.xeytanj.models.CameraDeviceInfo;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

import java.util.List;

public class PacketCameraConfigResponse extends Packet {

    private List<CameraDeviceInfo> screenDeviceInfoList;

    public PacketCameraConfigResponse() {
        super(PacketType.CAMERA_CONFIG);
    }

    public List<CameraDeviceInfo> getScreenDeviceInfoList() {
        return screenDeviceInfoList;
    }

    public void setScreenDeviceInfoList(List<CameraDeviceInfo> screenDeviceInfoList) {
        this.screenDeviceInfoList = screenDeviceInfoList;
    }
}
