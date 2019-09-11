package com.melardev.xeytanj.services.net;


import com.melardev.xeytanj.enums.ServiceType;
import com.melardev.xeytanj.net.packets.Packet;

public interface PacketHandler<T extends Packet> {

    ServiceType getServiceType();

    void handlePacket(T packet);

}
