package com.melardev.xeytanj.net.packets.multimedia;

import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.models.MediaConfigState;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

public class PacketCameraRequest extends Packet {

    private MediaInstruction mediaInstruction;
    private MediaConfigState mediaConfigState;
    private boolean recordAudio;

    public PacketCameraRequest(MediaInstruction mediaInstruction) {
        super(PacketType.CAMERA);
        this.mediaInstruction = mediaInstruction;
    }

    public PacketCameraRequest(PacketType packetType) {
        super(packetType);
        if (packetType != PacketType.CAMERA && packetType != PacketType.CAMERA_CONFIG)
            throw new IllegalArgumentException("Invalid args");
    }

    public MediaConfigState getMediaConfigState() {
        return mediaConfigState;
    }

    public void setMediaConfigState(MediaConfigState mediaConfigState) {
        this.mediaConfigState = mediaConfigState;
    }

    public boolean isRecordAudio() {
        return recordAudio;
    }

    public void setRecordAudio(boolean recordAudio) {
        this.recordAudio = recordAudio;
    }

    public MediaInstruction getMediaInstruction() {
        return mediaInstruction;
    }

    public void setMediaInstruction(MediaInstruction mediaInstruction) {
        this.mediaInstruction = mediaInstruction;
    }
}
