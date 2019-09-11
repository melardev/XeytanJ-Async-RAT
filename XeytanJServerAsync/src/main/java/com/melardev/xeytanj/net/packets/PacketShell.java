package com.melardev.xeytanj.net.packets;

public class PacketShell extends Packet {

    public enum PacketShellInstruction {
        START, EXEC, STOP, OUT
    }

    private String result;
    private PacketShellInstruction packetShellInstruction;


    public PacketShell(PacketShellInstruction packetShellInstruction) {
        super(PacketType.SHELL);
        this.packetShellInstruction = packetShellInstruction;
    }

    public PacketShell(PacketShellInstruction packetShellInstruction, String result) {
        super(PacketType.SHELL);
        this.packetShellInstruction = packetShellInstruction;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public PacketShellInstruction getPacketShellInstruction() {
        return packetShellInstruction;
    }

    public void setPacketShellInstruction(PacketShellInstruction packetShellInstruction) {
        this.packetShellInstruction = packetShellInstruction;
    }
}
