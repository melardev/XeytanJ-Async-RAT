package com.melardev.xeytanj.remote;

import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.Packet;

public interface PacketHandler<T extends Packet> {
    void handlePacket(T packet);

    void setLogger(ILogger logger);
}
