package com.melardev.xeytanj.net.packets.process;

import com.melardev.xeytanj.enums.ProcessCommand;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

public abstract class PacketProcess extends Packet {

    ProcessCommand processCommand;

    PacketProcess(ProcessCommand processCommand) {
        super(PacketType.PROCESS);
        this.processCommand = processCommand;
    }

    ProcessCommand getProcessCommand() {
        return processCommand;
    }

}
