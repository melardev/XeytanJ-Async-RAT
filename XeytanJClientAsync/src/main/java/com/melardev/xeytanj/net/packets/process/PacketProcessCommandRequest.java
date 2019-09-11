package com.melardev.xeytanj.net.packets.process;

import com.melardev.xeytanj.enums.ProcessCommand;

public class PacketProcessCommandRequest extends PacketProcess {

    private Integer pid;
    private ProcessCommand processCommand;

    public PacketProcessCommandRequest(ProcessCommand processCommand, Integer pid) {
        super(processCommand);
        if (processCommand == ProcessCommand.LIST_PROCESS)
            throw new IllegalArgumentException("illegal arg, for that use ");
        this.pid = pid;
        this.processCommand = processCommand;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public ProcessCommand getProcessCommand() {
        return processCommand;
    }

    public void setProcessCommand(ProcessCommand processCommand) {
        this.processCommand = processCommand;
    }

}
