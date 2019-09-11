package com.melardev.xeytanj.net.packets.multimedia;

import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

import javax.swing.*;

public class PacketMediaResponse extends Packet {
    private ImageIcon image;

    public PacketMediaResponse(PacketType packetType, ImageIcon icon) {
        super(packetType);
        if (packetType != PacketType.DESKTOP && packetType != PacketType.CAMERA)
            throw new IllegalArgumentException("Invalid argument");
        this.image = icon;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }
}
