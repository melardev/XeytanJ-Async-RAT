package com.melardev.xeytanj.services.net;

import com.melardev.xeytanj.enums.ServiceType;
import com.melardev.xeytanj.net.packets.Packet;

public interface RemoteMediaPacketHandler<T extends Packet> extends PacketHandler<T> {
    ServiceType getServiceType();

    void handlePacket(T packet);

}
