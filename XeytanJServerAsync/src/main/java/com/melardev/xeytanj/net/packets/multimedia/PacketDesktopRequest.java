package com.melardev.xeytanj.net.packets.multimedia;

import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.models.MediaConfigState;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

public class PacketDesktopRequest extends Packet {

    private MediaConfigState mediaConfigState;
    private MediaInstruction mediaInstruction;

    public PacketDesktopRequest(PacketType packetType) {
        super(packetType);
    }

    public PacketDesktopRequest(PacketType packetType, MediaInstruction mediaInstruction, MediaConfigState configState) {
        super(packetType);
        this.mediaConfigState = configState;
        this.mediaInstruction = mediaInstruction;
    }

    public PacketDesktopRequest(PacketType packetType, MediaInstruction mediaInstruction, String[] displays, int delay) {
        this(packetType, mediaInstruction, displays, delay, 1);
    }

    public PacketDesktopRequest(PacketType packetType, MediaInstruction mediaInstruction, String[] displays, int delay, int scaleFactor) {
        this(packetType, mediaInstruction, displays, delay, scaleFactor, scaleFactor);
    }

    public PacketDesktopRequest(MediaConfigState mediaConfigState, MediaInstruction mediaInstruction) {
        super(PacketType.DESKTOP);
        this.mediaInstruction = mediaInstruction;
        this.mediaConfigState = mediaConfigState;
    }

    public PacketDesktopRequest(PacketType packetType, MediaInstruction mediaInstruction) {
        super(packetType);
        if (packetType != PacketType.DESKTOP && packetType != PacketType.DESKTOP_CONFIG)
            throw new IllegalArgumentException("");

        this.mediaInstruction = mediaInstruction;
    }

    public PacketDesktopRequest(PacketType packetType, MediaInstruction mediaInstruction, String[] displays, int delay, int scaleX, int scaleY) {
        super(packetType);
        if (packetType != PacketType.DESKTOP && packetType != PacketType.DESKTOP_CONFIG)
            throw new IllegalArgumentException("");
        this.mediaInstruction = mediaInstruction;
        mediaConfigState = new MediaConfigState(displays, delay, scaleX, scaleY);
    }

    public PacketDesktopRequest(MediaInstruction mediaInstruction) {
        super(PacketType.DESKTOP);
        this.mediaInstruction = mediaInstruction;
    }


    public MediaInstruction getMediaInstruction() {
        return mediaInstruction;
    }

    public void setMediaInstruction(MediaInstruction mediaInstruction) {
        this.mediaInstruction = mediaInstruction;
    }

    public MediaConfigState getMediaConfigState() {
        return mediaConfigState;
    }

    public void setMediaConfigState(MediaConfigState mediaConfigState) {
        this.mediaConfigState = mediaConfigState;
    }
}
