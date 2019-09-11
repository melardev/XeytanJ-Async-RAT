package com.melardev.xeytanj.net.services;

import com.melardev.xeytanj.net.packets.Packet;

public interface NetClientService {
    void interact();

    void sendPacket(Packet packet);

    void closeConnection();
}
