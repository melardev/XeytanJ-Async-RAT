package com.melardev.xeytanj;

import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.net.packets.Packet;

public interface IApplication {
    void onFileSystemSessionClosed();

    void onSessionClosed(SessionType sessionType);

    void sendPacket(Packet packet);
}
