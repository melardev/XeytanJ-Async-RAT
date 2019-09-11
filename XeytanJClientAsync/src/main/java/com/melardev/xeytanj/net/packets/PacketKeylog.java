package com.melardev.xeytanj.net.packets;

public class PacketKeylog extends Packet {

    public String logs;

    public PacketKeylog() {
        super(PacketType.KEYLOGGER);
    }

    public PacketKeylog(String logs) {
        super(PacketType.KEYLOGGER);
        this.logs = logs;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }
}
