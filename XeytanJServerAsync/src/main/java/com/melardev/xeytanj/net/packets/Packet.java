package com.melardev.xeytanj.net.packets;

import java.io.Serializable;

public class Packet implements Serializable {

    private PacketType type;

    public Packet(PacketType _type) {
        type = _type;
    }

    public PacketType getType() {
        return type;
    }

    public void setType(PacketType type) {
        this.type = type;
    }

}
