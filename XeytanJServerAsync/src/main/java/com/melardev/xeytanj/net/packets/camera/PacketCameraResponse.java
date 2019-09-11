package com.melardev.xeytanj.net.packets.camera;

import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.net.packets.PacketType;

import javax.swing.*;

public class PacketCameraResponse extends PacketCamera {
    private ImageIcon image;

    public PacketCameraResponse(ImageIcon icon) {
        super(PacketType.CAMERA);
        this.image = icon;
    }

    public PacketCameraResponse(MediaInstruction content) {
        super(PacketType.CAMERA);
        this.packetContent = content;
    }

    public void setIcon(ImageIcon icon) {
        image = icon;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public MediaInstruction getContent() {
        return packetContent;
    }

    public void setContent(MediaInstruction content) {
        this.packetContent = content;
    }
}
